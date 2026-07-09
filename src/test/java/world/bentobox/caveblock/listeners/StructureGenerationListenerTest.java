package world.bentobox.caveblock.listeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.world.AsyncStructureSpawnEvent;
import org.bukkit.generator.structure.Structure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.papermc.paper.event.world.StructuresLocateEvent;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;

/**
 * Tests for {@link StructureGenerationListener}.
 */
@ExtendWith(MockitoExtension.class)
class StructureGenerationListenerTest {

    @Mock
    private CaveBlock addon;
    @Mock
    private Settings settings;
    @Mock
    private World world;

    private StructureGenerationListener listener;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        lenient().when(addon.getSettings()).thenReturn(settings);
        lenient().when(settings.getGenerateStructures())
                .thenReturn(Map.of("ancient_city", false, "trial_chambers", false, "mineshaft", true));
        lenient().when(settings.getWorldName()).thenReturn("caveblock_world");
        lenient().when(world.getName()).thenReturn("caveblock_world");
        lenient().when(world.getEnvironment()).thenReturn(World.Environment.NORMAL);
        listener = new StructureGenerationListener(addon);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    private AsyncStructureSpawnEvent event(String structureKey) {
        Structure structure = mock(Structure.class);
        lenient().when(structure.getKey()).thenReturn(NamespacedKey.minecraft(structureKey));
        AsyncStructureSpawnEvent e = mock(AsyncStructureSpawnEvent.class);
        lenient().when(e.getWorld()).thenReturn(world);
        lenient().when(e.getStructure()).thenReturn(structure);
        return e;
    }

    @Test
    void testDisabledStructureIsCancelled() {
        AsyncStructureSpawnEvent e = event("ancient_city");
        listener.onStructureSpawn(e);
        verify(e).setCancelled(true);
    }

    @Test
    void testEnabledStructureIsNotCancelled() {
        AsyncStructureSpawnEvent e = event("mineshaft");
        listener.onStructureSpawn(e);
        verify(e, never()).setCancelled(true);
    }

    @Test
    void testUnlistedStructureGeneratesNormally() {
        AsyncStructureSpawnEvent e = event("village_plains");
        listener.onStructureSpawn(e);
        verify(e, never()).setCancelled(true);
    }

    @Test
    void testStructureOutsideCaveBlockWorldIsIgnored() {
        when(world.getName()).thenReturn("some_other_world");
        AsyncStructureSpawnEvent e = event("ancient_city");
        listener.onStructureSpawn(e);
        verify(e, never()).setCancelled(true);
    }

    @Test
    void testNetherEnvironmentIsIgnored() {
        when(world.getEnvironment()).thenReturn(World.Environment.NETHER);
        AsyncStructureSpawnEvent e = event("ancient_city");
        listener.onStructureSpawn(e);
        verify(e, never()).setCancelled(true);
    }

    @Test
    void testConfigKeyWithHyphensAndCaseMatches() {
        when(settings.getGenerateStructures()).thenReturn(Map.of("Ancient-City", false));
        AsyncStructureSpawnEvent e = event("ancient_city");
        listener.onStructureSpawn(e);
        verify(e).setCancelled(true);
    }

    private StructuresLocateEvent locateEvent(String... structureKeys) {
        List<Structure> structures = Arrays.stream(structureKeys).map(key -> {
            Structure structure = mock(Structure.class);
            lenient().when(structure.getKey()).thenReturn(NamespacedKey.minecraft(key));
            return structure;
        }).map(Structure.class::cast).toList();
        StructuresLocateEvent e = mock(StructuresLocateEvent.class);
        lenient().when(e.getWorld()).thenReturn(world);
        lenient().when(e.getStructures()).thenReturn(structures);
        return e;
    }

    @Test
    void testLocateAllDisabledIsCancelled() {
        StructuresLocateEvent e = locateEvent("trial_chambers");
        listener.onStructuresLocate(e);
        verify(e).setCancelled(true);
        verify(e, never()).setStructures(anyList());
    }

    @Test
    void testLocateMixedNarrowsToEnabledOnly() {
        StructuresLocateEvent e = locateEvent("trial_chambers", "mineshaft");
        listener.onStructuresLocate(e);
        verify(e, never()).setCancelled(true);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Structure>> captor = ArgumentCaptor.forClass(List.class);
        verify(e).setStructures(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals("mineshaft", captor.getValue().get(0).getKey().getKey());
    }

    @Test
    void testLocateAllEnabledIsUntouched() {
        StructuresLocateEvent e = locateEvent("mineshaft", "village_plains");
        listener.onStructuresLocate(e);
        verify(e, never()).setCancelled(true);
        verify(e, never()).setStructures(anyList());
    }

    @Test
    void testLocateOutsideCaveBlockWorldIsIgnored() {
        when(world.getName()).thenReturn("some_other_world");
        StructuresLocateEvent e = locateEvent("trial_chambers");
        listener.onStructuresLocate(e);
        verify(e, never()).setCancelled(true);
        verify(e, never()).setStructures(anyList());
    }
}
