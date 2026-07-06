package world.bentobox.caveblock.listeners;

import java.util.Locale;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.AsyncStructureSpawnEvent;

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
 * generation, so this handler only reads config and inspects the event — it
 * performs no Bukkit world access.</p>
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
     * @param structureKey the vanilla structure key path, e.g. {@code ancient_city}
     * @return {@code true} if the config explicitly disables this structure
     */
    private boolean isDisabled(String structureKey) {
        Map<String, Boolean> structures = addon.getSettings().getGenerateStructures();
        if (structures == null || structures.isEmpty()) {
            return false;
        }
        for (Map.Entry<String, Boolean> entry : structures.entrySet()) {
            // Accept hyphen or underscore separators and any casing.
            if (normalize(entry.getKey()).equals(structureKey) && Boolean.FALSE.equals(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String key) {
        return key.toLowerCase(Locale.ROOT).replace('-', '_');
    }
}
