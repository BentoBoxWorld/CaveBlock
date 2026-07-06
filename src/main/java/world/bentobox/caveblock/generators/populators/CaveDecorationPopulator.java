package world.bentobox.caveblock.generators.populators;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

/**
 * Adds biome-aware surface features to the nether and end caves.
 *
 * <p>The chunk generator only shapes the rock and carves the caves; this
 * populator dresses the cave floors and ceilings so each biome region looks
 * distinct rather than being bare netherrack or end stone. It works on a
 * bounded number of random samples per chunk (never a full scan) so it stays
 * cheap, and it only ever places blocks into existing air resting on solid
 * ground — nothing floats.</p>
 *
 * <ul>
 *   <li><b>Crimson forest</b> — crimson nylium floors, crimson roots and fungi,
 *       shroomlight, weeping vines hanging from the ceiling.</li>
 *   <li><b>Warped forest</b> — warped nylium, warped roots and fungi, nether
 *       sprouts, twisting vines climbing from the floor.</li>
 *   <li><b>Soul sand valley</b> — soul sand and soul soil, blue soul fire, bones.</li>
 *   <li><b>Basalt deltas</b> — blackstone, basalt and magma floors, basalt
 *       columns, small fires over magma.</li>
 *   <li><b>Nether wastes</b> — kept barren with the occasional open flame.</li>
 *   <li><b>The End</b> — end rods and chorus flowers, with hanging end rods.</li>
 * </ul>
 *
 * @author tastybento
 */
public class CaveDecorationPopulator extends BlockPopulator {

    /** Random floor samples attempted per chunk. */
    private static final int FLOOR_ATTEMPTS = 48;
    /** Random ceiling samples attempted per chunk. */
    private static final int CEILING_ATTEMPTS = 16;
    /** How far up/down a sample searches for a floor or ceiling. */
    private static final int SCAN_RANGE = 24;
    /** Solid margin kept clear of decoration above the floor and below the roof. */
    private static final int FLOOR_MARGIN = 6;
    private static final int ROOF_MARGIN = 7;

    private final int worldDepth;

    /**
     * @param worldDepth configured world depth (roof height)
     */
    public CaveDecorationPopulator(int worldDepth) {
        this.worldDepth = worldDepth;
    }

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion region) {
        final World.Environment env = worldInfo.getEnvironment();
        if (env == World.Environment.NORMAL) {
            return;
        }
        final int minY = worldInfo.getMinHeight() + FLOOR_MARGIN;
        final int maxY = Math.min(worldInfo.getMaxHeight(), worldDepth) - ROOF_MARGIN;
        if (maxY - minY < 2) {
            return;
        }
        final boolean nether = env == World.Environment.NETHER;

        for (int i = 0; i < FLOOR_ATTEMPTS; i++) {
            int x = (chunkX << 4) + random.nextInt(16);
            int z = (chunkZ << 4) + random.nextInt(16);
            int startY = minY + random.nextInt(maxY - minY);
            int y = findFloor(region, x, startY, z, minY);
            if (y == Integer.MIN_VALUE) {
                continue;
            }
            if (nether) {
                decorateNetherFloor(region, x, y, z, random);
            } else {
                decorateEndFloor(region, x, y, z, random);
            }
        }
        for (int i = 0; i < CEILING_ATTEMPTS; i++) {
            int x = (chunkX << 4) + random.nextInt(16);
            int z = (chunkZ << 4) + random.nextInt(16);
            int startY = minY + random.nextInt(maxY - minY);
            int y = findCeiling(region, x, startY, z, maxY);
            if (y == Integer.MIN_VALUE) {
                continue;
            }
            if (nether) {
                decorateNetherCeiling(region, x, y, z, random);
            } else {
                decorateEndCeiling(region, x, y, z, random);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Section: Floor / ceiling finding
    // -------------------------------------------------------------------------

    /**
     * @return the Y of an air block sitting on solid ground near {@code startY},
     *         or {@link Integer#MIN_VALUE} if none was found within range
     */
    private int findFloor(LimitedRegion region, int x, int startY, int z, int minY) {
        int limit = Math.max(minY, startY - SCAN_RANGE);
        for (int y = startY; y > limit; y--) {
            if (region.isInRegion(x, y, z) && region.getType(x, y, z) == Material.AIR
                    && region.isInRegion(x, y - 1, z) && region.getType(x, y - 1, z).isSolid()) {
                return y;
            }
        }
        return Integer.MIN_VALUE;
    }

    /**
     * @return the Y of an air block hanging beneath a solid ceiling near
     *         {@code startY}, or {@link Integer#MIN_VALUE} if none was found
     */
    private int findCeiling(LimitedRegion region, int x, int startY, int z, int maxY) {
        int limit = Math.min(maxY, startY + SCAN_RANGE);
        for (int y = startY; y < limit; y++) {
            if (region.isInRegion(x, y, z) && region.getType(x, y, z) == Material.AIR
                    && region.isInRegion(x, y + 1, z) && region.getType(x, y + 1, z).isSolid()) {
                return y;
            }
        }
        return Integer.MIN_VALUE;
    }

    // -------------------------------------------------------------------------
    // Section: Nether decoration
    // -------------------------------------------------------------------------

    private void decorateNetherFloor(LimitedRegion region, int x, int y, int z, Random random) {
        Biome biome = region.getBiome(x, y, z);
        Material floor = region.getType(x, y - 1, z);
        double r = random.nextDouble();
        if (biome == Biome.CRIMSON_FOREST) {
            setIfNetherrack(region, x, y - 1, z, floor, Material.CRIMSON_NYLIUM);
            if (r < 0.45) {
                region.setType(x, y, z, Material.CRIMSON_ROOTS);
            } else if (r < 0.6) {
                region.setType(x, y, z, Material.CRIMSON_FUNGUS);
            } else if (r < 0.68) {
                region.setType(x, y, z, Material.NETHER_SPROUTS);
            } else if (r < 0.72) {
                region.setType(x, y - 1, z, Material.SHROOMLIGHT);
            }
        } else if (biome == Biome.WARPED_FOREST) {
            setIfNetherrack(region, x, y - 1, z, floor, Material.WARPED_NYLIUM);
            if (r < 0.4) {
                region.setType(x, y, z, Material.WARPED_ROOTS);
            } else if (r < 0.52) {
                region.setType(x, y, z, Material.WARPED_FUNGUS);
            } else if (r < 0.62) {
                region.setType(x, y, z, Material.NETHER_SPROUTS);
            } else if (r < 0.74) {
                growUpwards(region, x, y, z, Material.TWISTING_VINES, 1 + random.nextInt(3));
            } else if (r < 0.78) {
                region.setType(x, y - 1, z, Material.SHROOMLIGHT);
            }
        } else if (biome == Biome.SOUL_SAND_VALLEY) {
            setIfNetherrack(region, x, y - 1, z, floor, r < 0.5 ? Material.SOUL_SAND : Material.SOUL_SOIL);
            if (r < 0.06) {
                // Blue soul fire needs soul soil beneath it to stay lit.
                region.setType(x, y - 1, z, Material.SOUL_SOIL);
                region.setType(x, y, z, Material.SOUL_FIRE);
            } else if (r < 0.1) {
                region.setType(x, y - 1, z, Material.BONE_BLOCK);
            }
        } else if (biome == Biome.BASALT_DELTAS) {
            if (floor == Material.NETHERRACK) {
                region.setType(x, y - 1, z, r < 0.5 ? Material.BLACKSTONE : Material.BASALT);
            }
            if (r < 0.05) {
                // Fire over magma stays lit and gives the deltas their glow.
                region.setType(x, y - 1, z, Material.MAGMA_BLOCK);
                region.setType(x, y, z, Material.FIRE);
            } else if (r < 0.14) {
                growUpwards(region, x, y, z, Material.BASALT, 1 + random.nextInt(2));
            }
        } else {
            // Nether wastes and anything else: barren, with the odd open flame
            // on the netherrack so it stays lit without lag.
            if (r < 0.03 && floor == Material.NETHERRACK) {
                region.setType(x, y, z, Material.FIRE);
            }
        }
    }

    private void decorateNetherCeiling(LimitedRegion region, int x, int y, int z, Random random) {
        double r = random.nextDouble();
        if (r < 0.08) {
            // A glowstone patch on the ceiling lights the cavern.
            region.setType(x, y + 1, z, Material.GLOWSTONE);
        } else if (r < 0.2 && region.getBiome(x, y, z) == Biome.CRIMSON_FOREST) {
            growDownwards(region, x, y, z, Material.WEEPING_VINES, 1 + random.nextInt(3));
        }
    }

    // -------------------------------------------------------------------------
    // Section: End decoration
    // -------------------------------------------------------------------------

    private void decorateEndFloor(LimitedRegion region, int x, int y, int z, Random random) {
        Material floor = region.getType(x, y - 1, z);
        double r = random.nextDouble();
        if (r < 0.05) {
            region.setType(x, y, z, Material.END_ROD);
        } else if (r < 0.12 && floor == Material.END_STONE) {
            // A chorus flower on end stone grows into a chorus plant over time.
            region.setType(x, y, z, Material.CHORUS_FLOWER);
        } else if (r < 0.14 && floor == Material.END_STONE) {
            region.setType(x, y - 1, z, Material.END_STONE_BRICKS);
        }
    }

    private void decorateEndCeiling(LimitedRegion region, int x, int y, int z, Random random) {
        if (random.nextDouble() < 0.05) {
            // A downward-facing end rod hanging from the ceiling for light.
            BlockData rod = Material.END_ROD.createBlockData();
            if (rod instanceof Directional directional) {
                directional.setFacing(BlockFace.DOWN);
            }
            region.setBlockData(x, y, z, rod);
        }
    }

    // -------------------------------------------------------------------------
    // Section: Helpers
    // -------------------------------------------------------------------------

    /** Replaces plain netherrack with {@code replacement}; leaves ore/other blocks alone. */
    private void setIfNetherrack(LimitedRegion region, int x, int y, int z, Material current, Material replacement) {
        if (current == Material.NETHERRACK) {
            region.setType(x, y, z, replacement);
        }
    }

    /** Stacks {@code material} upwards from the floor air block while there is room. */
    private void growUpwards(LimitedRegion region, int x, int y, int z, Material material, int height) {
        for (int i = 0; i < height; i++) {
            int yy = y + i;
            if (!region.isInRegion(x, yy, z) || region.getType(x, yy, z) != Material.AIR) {
                break;
            }
            region.setType(x, yy, z, material);
        }
    }

    /** Hangs {@code material} downwards from the ceiling air block while there is room. */
    private void growDownwards(LimitedRegion region, int x, int y, int z, Material material, int height) {
        for (int i = 0; i < height; i++) {
            int yy = y - i;
            if (!region.isInRegion(x, yy, z) || region.getType(x, yy, z) != Material.AIR) {
                break;
            }
            region.setType(x, yy, z, material);
        }
    }
}
