package world.bentobox.caveblock.generators.populators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.block.Biome;
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

/**
 * Tests for {@link NetherBiomeProvider}.
 */
@ExtendWith(MockitoExtension.class)
class NetherBiomeProviderTest {

    @Mock
    private CaveBlock addon;
    @Mock
    private WorldInfo worldInfo;

    private Settings settings;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        settings = new Settings();
        when(addon.getSettings()).thenReturn(settings);
        lenient().when(worldInfo.getEnvironment()).thenReturn(World.Environment.NETHER);
        lenient().when(worldInfo.getSeed()).thenReturn(123456789L);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void testGetBiomesNotEmpty() {
        NetherBiomeProvider p = new NetherBiomeProvider(addon);
        assertFalse(p.getBiomes(worldInfo).isEmpty());
    }

    @Test
    void testDeterministicForSameSeed() {
        NetherBiomeProvider a = new NetherBiomeProvider(addon);
        NetherBiomeProvider b = new NetherBiomeProvider(addon);
        for (int i = 0; i < 40; i++) {
            assertEquals(a.getBiome(worldInfo, i * 4, 0, i * 7),
                    b.getBiome(worldInfo, i * 4, 0, i * 7),
                    "Same seed and coordinates must give the same biome");
        }
    }

    @Test
    void testBiomeIsColumnConstant() {
        NetherBiomeProvider p = new NetherBiomeProvider(addon);
        Biome top = p.getBiome(worldInfo, 10, 100, 20);
        Biome bottom = p.getBiome(worldInfo, 10, 5, 20);
        assertEquals(top, bottom, "Biome should not depend on Y");
    }

    @Test
    void testMultipleBiomesAcrossAnIslandSizedArea() {
        NetherBiomeProvider p = new NetherBiomeProvider(addon);
        Set<Biome> seen = new HashSet<>();
        // Sample a 100x100 area (an island's footprint) on a grid.
        for (int x = 0; x < 100; x += 5) {
            for (int z = 0; z < 100; z += 5) {
                seen.add(p.getBiome(worldInfo, x, 40, z));
            }
        }
        assertTrue(seen.size() > 1, "An island-sized area should contain more than one biome, saw " + seen);
    }
}
