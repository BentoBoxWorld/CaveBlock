package world.bentobox.caveblock.listeners;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.AsyncStructureSpawnEvent;
import org.bukkit.generator.structure.Structure;

import io.papermc.paper.event.world.StructuresLocateEvent;
import world.bentobox.caveblock.CaveBlock;

/**
 * Prevents configured vanilla structures from generating in the CaveBlock
 * overworld.
 *
 * <p>The overworld delegates to vanilla generation, which includes structures
 * such as Ancient Cities, Trial Chambers and Strongholds. Some of these fill or
 * unbalance a cave world (see issue #112). The {@link org.bukkit.generator.ChunkGenerator}
 * flag can only turn all structures on or off, so this listener provides
 * per-structure control by cancelling the spawn of any structure the admin has
 * disabled in {@code world.structures}.</p>
 *
 * <p>{@link AsyncStructureSpawnEvent} fires off the main thread during chunk
 * generation, so that handler only reads config and inspects the event. It
 * queries the event's world for environment and ownership checks but performs
 * no world or block mutation, which is what makes it safe to run async.</p>
 *
 * <p>Cancelling the spawn stops a structure being <em>placed</em> but leaves its
 * placement rules intact, so structure <em>searches</em> ({@code /locate}, Eyes of
 * Ender, treasure/explorer maps, villager map trades) keep proposing candidate
 * positions that are then all cancelled, scanning to the radius cap and freezing
 * the server (issue #116). {@link #onStructuresLocate} closes that hole by removing
 * disabled structures from the search before it runs.</p>
 *
 * @author tastybento
 */
public class StructureGenerationListener implements Listener {

    private final CaveBlock addon;

    /**
     * @param addon CaveBlock addon
     */
    public StructureGenerationListener(CaveBlock addon) {
        this.addon = addon;
    }

    /**
     * Cancels the spawn of a disabled structure in the CaveBlock overworld.
     *
     * @param event the structure spawn event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onStructureSpawn(AsyncStructureSpawnEvent event) {
        World world = event.getWorld();
        // Only the CaveBlock overworld uses vanilla structures; nether/end do not.
        if (world.getEnvironment() != World.Environment.NORMAL || !addon.inWorld(world)) {
            return;
        }
        String structureKey = event.getStructure().getKey().getKey();
        if (isDisabled(structureKey)) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents disabled structures from being <em>located</em>. {@link StructuresLocateEvent}
     * fires before any structure search — the {@code /locate} command, Eyes of Ender,
     * explorer/treasure maps, dolphins and villager map trades. On a cave world where the
     * target structure is suppressed, that search never succeeds and scans out to the radius
     * cap, freezing the main thread (issue #116). Removing the disabled structures from the
     * search list — and cancelling outright when nothing remains — skips the scan entirely so
     * the search returns "not found" instantly.
     *
     * @param event the structure locate event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onStructuresLocate(StructuresLocateEvent event) {
        if (!addon.inWorld(event.getWorld())) {
            return;
        }
        List<Structure> targets = event.getStructures();
        List<Structure> allowed = targets.stream()
                .filter(structure -> !isDisabled(structure.getKey().getKey()))
                .toList();
        if (allowed.size() == targets.size()) {
            // Nothing disabled in this search — let it run normally.
            return;
        }
        if (allowed.isEmpty()) {
            // Every requested structure is disabled here: skip the expensive scan entirely.
            event.setCancelled(true);
        } else {
            event.setStructures(allowed);
        }
    }

    /**
     * @param structureKey the vanilla structure key path, e.g. {@code ancient_city}
     * @return {@code true} if the config explicitly disables this structure
     */
    private boolean isDisabled(String structureKey) {
        Map<String, Boolean> structures = addon.getSettings().getGenerateStructures();
        if (structures == null || structures.isEmpty()) {
            return false;
        }
        // Accept hyphen or underscore separators and any casing on both sides.
        String normalizedKey = normalize(structureKey);
        for (Map.Entry<String, Boolean> entry : structures.entrySet()) {
            if (normalize(entry.getKey()).equals(normalizedKey) && Boolean.FALSE.equals(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String key) {
        return key.toLowerCase(Locale.ROOT).replace('-', '_');
    }
}
