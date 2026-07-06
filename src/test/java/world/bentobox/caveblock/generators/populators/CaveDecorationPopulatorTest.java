package world.bentobox.caveblock.generators.populators;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link CaveDecorationPopulator}.
 */
@ExtendWith(MockitoExtension.class)
class CaveDecorationPopulatorTest {

    @Mock
    private WorldInfo worldInfo;
    @Mock
    private LimitedRegion region;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        lenient().when(worldInfo.getMinHeight()).thenReturn(0);
        lenient().when(worldInfo.getMaxHeight()).thenReturn(128);
        // A world of alternating solid rock (even Y) and air (odd Y) so every
        // odd Y is an air block resting on solid ground — a floor to decorate.
        lenient().when(region.isInRegion(anyInt(), anyInt(), anyInt())).thenReturn(true);
        lenient().when(region.getType(anyInt(), anyInt(), anyInt()))
                .thenAnswer(inv -> (int) inv.getArgument(1) % 2 == 0 ? Material.NETHERRACK : Material.AIR);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void testNormalEnvironmentDoesNothing() {
        when(worldInfo.getEnvironment()).thenReturn(World.Environment.NORMAL);
        new CaveDecorationPopulator(128).populate(worldInfo, new Random(0), 0, 0, region);
        verify(region, never()).setType(anyInt(), anyInt(), anyInt(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void testNetherPlacesDecoration() {
        when(worldInfo.getEnvironment()).thenReturn(World.Environment.NETHER);
        // Crimson forest always turns a found netherrack floor into nylium.
        when(region.getBiome(anyInt(), anyInt(), anyInt())).thenReturn(Biome.CRIMSON_FOREST);
        new CaveDecorationPopulator(128).populate(worldInfo, new Random(0), 0, 0, region);
        verify(region, atLeastOnce()).setType(anyInt(), anyInt(), anyInt(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void testEndRunsWithoutError() {
        when(worldInfo.getEnvironment()).thenReturn(World.Environment.THE_END);
        // No exception should be thrown while decorating the end.
        new CaveDecorationPopulator(128).populate(worldInfo, new Random(0), 0, 0, region);
    }
}
