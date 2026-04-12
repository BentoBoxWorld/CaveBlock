package world.bentobox.caveblock.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.World;
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
    void testGetDefaultPopulatorsNetherHasOne() {
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.NETHER);
        assertFalse(cg.getDefaultPopulators(world).isEmpty(),
                "Nether should have the NewMaterialPopulator");
        assertEquals(1, cg.getDefaultPopulators(world).size());
    }

    @Test
    void testGetDefaultPopulatorsEndHasOne() {
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.THE_END);
        assertFalse(cg.getDefaultPopulators(world).isEmpty(),
                "End should have the NewMaterialPopulator");
        assertEquals(1, cg.getDefaultPopulators(world).size());
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
    void testGetDefaultBiomeProviderNetherReturnsFlatBiomeProvider() {
        when(worldInfo.getEnvironment()).thenReturn(World.Environment.NETHER);
        ChunkGeneratorWorld cg = new ChunkGeneratorWorld(addon, World.Environment.NETHER);
        assertNotNull(cg.getDefaultBiomeProvider(worldInfo));
        assertTrue(cg.getDefaultBiomeProvider(worldInfo) instanceof FlatBiomeProvider);
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
        assertEquals(1, cg.getDefaultPopulators(world).size());
    }
}
