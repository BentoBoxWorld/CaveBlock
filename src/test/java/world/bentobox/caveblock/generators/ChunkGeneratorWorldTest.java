package world.bentobox.caveblock.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.generator.WorldInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;
import world.bentobox.caveblock.generators.populators.FlatBiomeProvider;
import world.bentobox.caveblock.generators.populators.NetherBiomeProvider;

/**
 * Tests for {@link ChunkGeneratorWorld}.
 */
@ExtendWith(MockitoExtension.class)
class ChunkGeneratorWorldTest {

    @Mock
    private CaveBlock addon;
    @Mock
    private World world;
    @Mock
    private WorldInfo worldInfo;

    private Settings settings;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        settings = new Settings();
        when(addon.getSettings()).thenReturn(settings);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    // -------------------------------------------------------------------------
    // shouldGenerate* delegation flags — NORMAL environment
    // -------------------------------------------------------------------------

    @Test
    void testShouldGenerateNoiseNormal() {
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.NORMAL);
        assertTrue(cg.shouldGenerateNoise());
    }

    @Test
    void testShouldGenerateNoiseNether() {
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.NETHER);
        assertFalse(cg.shouldGenerateNoise());
    }

    @Test
    void testShouldGenerateNoiseEnd() {
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.THE_END);
        assertFalse(cg.shouldGenerateNoise());
    }

    @Test
    void testShouldGenerateSurfaceAlwaysFalse() {
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.NORMAL).shouldGenerateSurface());
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.NETHER).shouldGenerateSurface());
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.THE_END).shouldGenerateSurface());
    }

    @Test
    void testShouldGenerateBedrockAlwaysFalse() {
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.NORMAL).shouldGenerateBedrock());
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.NETHER).shouldGenerateBedrock());
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.THE_END).shouldGenerateBedrock());
    }

    @Test
    void testShouldGenerateCavesNormal() {
        assertTrue(new ChunkGeneratorWorld(addon, World.Environment.NORMAL).shouldGenerateCaves());
    }

    @Test
    void testShouldGenerateCavesNormalCarversDisabled() {
        settings.setOverworldCarvers(false);
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.NORMAL).shouldGenerateCaves(),
                "Disabling overworld carvers must stop vanilla carver caves");
    }

    @Test
    void testShouldGenerateCavesNether() {
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.NETHER).shouldGenerateCaves());
    }

    @Test
    void testShouldGenerateCavesEnd() {
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.THE_END).shouldGenerateCaves());
    }

    @Test
    void testShouldGenerateDecorationsNormal() {
        assertTrue(new ChunkGeneratorWorld(addon, World.Environment.NORMAL).shouldGenerateDecorations());
    }

    @Test
    void testShouldGenerateDecorationsNether() {
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.NETHER).shouldGenerateDecorations());
    }

    @Test
    void testShouldGenerateStructuresNormal() {
        assertTrue(new ChunkGeneratorWorld(addon, World.Environment.NORMAL).shouldGenerateStructures());
    }

    @Test
    void testShouldGenerateStructuresNether() {
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.NETHER).shouldGenerateStructures());
    }

    @Test
    void testShouldGenerateMobsAlwaysFalse() {
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.NORMAL).shouldGenerateMobs());
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.NETHER).shouldGenerateMobs());
        assertFalse(new ChunkGeneratorWorld(addon, World.Environment.THE_END).shouldGenerateMobs());
    }

    // -------------------------------------------------------------------------
    // Block populators
    // -------------------------------------------------------------------------

    @Test
    void testGetDefaultPopulatorsNormalIsEmpty() {
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.NORMAL);
        assertTrue(cg.getDefaultPopulators(world).isEmpty(),
                "Overworld should have no custom block populators");
    }

    @Test
    void testGetDefaultPopulatorsNetherHasMaterialAndDecoration() {
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.NETHER);
        assertFalse(cg.getDefaultPopulators(world).isEmpty(),
                "Nether should have material and decoration populators");
        assertEquals(2, cg.getDefaultPopulators(world).size());
    }

    @Test
    void testGetDefaultPopulatorsEndHasMaterialAndDecoration() {
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.THE_END);
        assertFalse(cg.getDefaultPopulators(world).isEmpty(),
                "End should have material and decoration populators");
        assertEquals(2, cg.getDefaultPopulators(world).size());
    }

    // -------------------------------------------------------------------------
    // Biome provider
    // -------------------------------------------------------------------------

    @Test
    void testGetDefaultBiomeProviderNormalReturnsNull() {
        when(worldInfo.getEnvironment()).thenReturn(World.Environment.NORMAL);
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.NORMAL);
        assertNull(cg.getDefaultBiomeProvider(worldInfo),
                "Overworld should return null so vanilla biomes are used");
    }

    @Test
    void testGetDefaultBiomeProviderNetherReturnsNetherBiomeProvider() {
        when(worldInfo.getEnvironment()).thenReturn(World.Environment.NETHER);
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.NETHER);
        assertNotNull(cg.getDefaultBiomeProvider(worldInfo));
        assertTrue(cg.getDefaultBiomeProvider(worldInfo) instanceof NetherBiomeProvider);
    }

    @Test
    void testGetDefaultBiomeProviderEndReturnsFlatBiomeProvider() {
        when(worldInfo.getEnvironment()).thenReturn(World.Environment.THE_END);
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.THE_END);
        assertNotNull(cg.getDefaultBiomeProvider(worldInfo));
        assertTrue(cg.getDefaultBiomeProvider(worldInfo) instanceof FlatBiomeProvider);
    }

    // -------------------------------------------------------------------------
    // reload()
    // -------------------------------------------------------------------------

    @Test
    void testReloadClearsAndRebuildsPopulatorsNormal() {
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.NORMAL);
        cg.reload();
        assertTrue(cg.getDefaultPopulators(world).isEmpty());
    }

    @Test
    void testReloadClearsAndRebuildsPopulatorsNether() {
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.NETHER);
        cg.reload();
        assertEquals(2, cg.getDefaultPopulators(world).size());
    }

    // -------------------------------------------------------------------------
    // Overworld cave fill (generateSurface)
    // -------------------------------------------------------------------------

    private static final int MIN_HEIGHT = 0;
    private static final int MAX_HEIGHT = 64;
    private static final int CAVE_LOW = 20;
    private static final int CAVE_HIGH = 40;
    private static final int CAVE_COUNT = 16 * 16 * (CAVE_HIGH - CAVE_LOW);

    /**
     * With the default fill of 0.0 no cave air is solidified — the overworld keeps
     * every vanilla cave (the pre-existing behaviour) — but sky air is still sealed.
     */
    @Test
    void testGenerateSurfaceNormalNoFillKeepsCaves() {
        Map<String, Material> store = new HashMap<>();
        ChunkData data = buildChunkData(store);
        // fill defaults to 0.0
        runNormalSurface(data);

        assertEquals(CAVE_COUNT, countCaveAir(store), "Fill 0.0 must leave every cave untouched");
        assertEquals(settings.getNormalMainBlock(), store.get("0,60,0"), "Sky air must still be sealed");
    }

    /**
     * A full fill of 1.0 re-solidifies essentially the whole cave network.
     */
    @Test
    void testGenerateSurfaceNormalFullFillClosesCaves() {
        settings.setOverworldCaveFill(1.0);
        Map<String, Material> store = new HashMap<>();
        ChunkData data = buildChunkData(store);
        runNormalSurface(data);

        assertTrue(countCaveAir(store) < CAVE_COUNT * 0.02,
                "Fill 1.0 should close nearly all caves, left " + countCaveAir(store));
    }

    /**
     * A partial fill thins the caves: fewer remain than at 0.0, more than at 1.0.
     */
    @Test
    void testGenerateSurfaceNormalPartialFillThinsCaves() {
        settings.setOverworldCaveFill(0.5);
        Map<String, Material> store = new HashMap<>();
        ChunkData data = buildChunkData(store);
        runNormalSurface(data);

        int remaining = countCaveAir(store);
        assertTrue(remaining > 0 && remaining < CAVE_COUNT,
                "Fill 0.5 should thin but not clear the caves, left " + remaining);
    }

    /** Runs generateSurface for a NORMAL world over chunk (0,0). */
    private void runNormalSurface(ChunkData data) {
        when(worldInfo.getEnvironment()).thenReturn(World.Environment.NORMAL);
        when(worldInfo.getMinHeight()).thenReturn(MIN_HEIGHT);
        when(worldInfo.getMaxHeight()).thenReturn(MAX_HEIGHT);
        lenient().when(worldInfo.getSeed()).thenReturn(123L);
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.NORMAL);
        cg.generateSurface(worldInfo, new Random(0), 0, 0, data);
    }

    private static int countCaveAir(Map<String, Material> store) {
        // Both air types count as cave: noise caves are AIR, carver caves are CAVE_AIR.
        return (int) store.values().stream()
                .filter(m -> m == Material.AIR || m == Material.CAVE_AIR).count();
    }

    /**
     * An in-memory {@link ChunkData} backed by a map. Unset blocks read as solid
     * STONE; a mid band is seeded with caves and the top with sky air so the surface
     * pass has both caves to thin and sky to seal. The cave band mixes plain AIR
     * (vanilla noise caves) and CAVE_AIR (carver caves) so the fill must handle both.
     */
    private ChunkData buildChunkData(Map<String, Material> store) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = CAVE_LOW; y < CAVE_HIGH; y++) {
                    // Lower half AIR (noise caves), upper half CAVE_AIR (carver caves).
                    store.put(key(x, y, z), y < (CAVE_LOW + CAVE_HIGH) / 2 ? Material.AIR : Material.CAVE_AIR);
                }
                for (int y = MAX_HEIGHT - 4; y < MAX_HEIGHT - 1; y++) {
                    store.put(key(x, y, z), Material.AIR);
                }
            }
        }
        ChunkData cd = mock(ChunkData.class);
        lenient().when(cd.getType(anyInt(), anyInt(), anyInt())).thenAnswer(inv ->
                store.getOrDefault(key(inv.getArgument(0), inv.getArgument(1), inv.getArgument(2)), Material.STONE));
        lenient().doAnswer(inv -> {
            store.put(key(inv.getArgument(0), inv.getArgument(1), inv.getArgument(2)), inv.getArgument(3));
            return null;
        }).when(cd).setBlock(anyInt(), anyInt(), anyInt(), any(Material.class));
        lenient().doAnswer(inv -> {
            int x0 = inv.getArgument(0), y0 = inv.getArgument(1), z0 = inv.getArgument(2);
            int x1 = inv.getArgument(3), y1 = inv.getArgument(4), z1 = inv.getArgument(5);
            Material m = inv.getArgument(6);
            for (int x = x0; x < x1; x++) {
                for (int y = y0; y < y1; y++) {
                    for (int z = z0; z < z1; z++) {
                        store.put(key(x, y, z), m);
                    }
                }
            }
            return null;
        }).when(cd).setRegion(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), any(Material.class));
        return cd;
    }

    private static String key(int x, int y, int z) {
        return x + "," + y + "," + z;
    }
}
