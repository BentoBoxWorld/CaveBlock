package world.bentobox.caveblock.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class ChunkGeneratorWorld ...
 *
 * @author tastybento
 */
public class ChunkGeneratorWorld extends ChunkGenerator {
  
    private static final int BLOB_SIZE = 1;
    private static final Map<Environment, List<Ore>> ORES;
    static {
        Map<Environment, List<Ore>> ores = new EnumMap<>(Environment.class);
        // Source https://minecraft.fandom.com/wiki/Blob
        List<Ore> worldOres = new ArrayList<>();
        worldOres.add(new Ore(-64, 16, Material.DIAMOND_ORE, 1, 10, true));
        worldOres.add(new Ore(-64, 64, Material.LAPIS_ORE, 1, 7, true));
        worldOres.add(new Ore(-64, 30, Material.GOLD_ORE, 2, 9, true));
        worldOres.add(new Ore(0, 16, Material.TUFF, 2, 33, false));
        worldOres.add(new Ore(-64, 16, Material.REDSTONE_ORE, 8, 8, true));
        worldOres.add(new Ore(0, 16, Material.GRAVEL, 8 , 33, false));
        worldOres.add(new Ore(0, 79, Material.GRANITE, 5, 33, false));
        worldOres.add(new Ore(0, 79, Material.ANDESITE,5, 33, false));
        worldOres.add(new Ore(0, 79, Material.DIORITE,5, 33, false));
        worldOres.add(new Ore(32, 320, Material.EMERALD_ORE, 11, 1, true));
        worldOres.add(new Ore(95, 136, Material.COAL_ORE, 20, 17, false));
        worldOres.add(new Ore(0, 96, Material.COPPER_ORE, 20, 9, true));
        worldOres.add(new Ore(-64, 320, Material.IRON_ORE, 20, 9, true));
        worldOres.add(new Ore(-64, 320, Material.CAVE_AIR, 8 , 33, false));
        ores.put(Environment.NORMAL, worldOres);
        List<Ore> netherOres = new ArrayList<>();
        netherOres.add(new Ore(1, 22, Material.ANCIENT_DEBRIS, 1, 5, true));
        netherOres.add(new Ore(-64, 30, Material.NETHER_GOLD_ORE, 2, 9, true));
        netherOres.add(new Ore(0, 16, Material.GRAVEL, 8 , 33, false));
        netherOres.add(new Ore(0, 320, Material.BASALT, 8 , 33, false));
        netherOres.add(new Ore(0, 320, Material.BLACKSTONE, 8 , 33, false));
        netherOres.add(new Ore(0, 320, Material.FIRE, 8 , 33, false));
        netherOres.add(new Ore(200, 320, Material.GLOWSTONE, 8 , 33, false));
        netherOres.add(new Ore(-64, 320, Material.CAVE_AIR, 8 , 33, false));
        netherOres.add(new Ore(-64, 320, Material.LAVA, 8 , 33, false));
        netherOres.add(new Ore(0, 16, Material.MAGMA_BLOCK, 8 , 33, false));
        netherOres.add(new Ore(0, 320, Material.CRIMSON_FUNGUS, 8 , 33, false));
        netherOres.add(new Ore(0, 320, Material.WARPED_FUNGUS, 8 , 33, false));
        netherOres.add(new Ore(0, 320, Material.CRIMSON_NYLIUM, 8 , 33, false));
        netherOres.add(new Ore(0, 320, Material.WARPED_NYLIUM, 8 , 33, false));
        netherOres.add(new Ore(0, 320, Material.SHROOMLIGHT, 8 , 33, false));
        netherOres.add(new Ore(0, 320, Material.CRIMSON_STEM, 8 , 33, false));
        netherOres.add(new Ore(0, 320, Material.WARPED_STEM, 8 , 33, false));
        netherOres.add(new Ore(-64, 34, Material.SOUL_SOIL, 20, 17, false));
        netherOres.add(new Ore(0, 96, Material.NETHER_QUARTZ_ORE, 20, 9, true));
        netherOres.add(new Ore(-64, 320, Material.BONE_BLOCK, 20, 9, true));
        ores.put(Environment.NETHER, netherOres);
        List<Ore> endOres = new ArrayList<>();
        endOres.add(new Ore(32, 320, Material.PURPUR_BLOCK, 11, 1, true));
        endOres.add(new Ore(95, 136, Material.OBSIDIAN, 20, 17, false));
        endOres.add(new Ore(-64, 320, Material.CAVE_AIR, 8 , 33, false));
        ores.put(Environment.THE_END, endOres);
        ORES = Collections.unmodifiableMap(ores);
    }

    // ---------------------------------------------------------------------
    // Section: Methods
    // ---------------------------------------------------------------------

    @Override
    public void generateNoise(WorldInfo worldInfo, Random r, int x, int z, ChunkData chunkData) {
        switch(worldInfo.getEnvironment()) {
        default:
            chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, worldInfo.getMaxHeight(), 16, Material.STONE);
            chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, 7, 16, Material.DEEPSLATE);
            chunkData.setRegion(0, worldInfo.getMaxHeight() - 1, 0, 16, worldInfo.getMaxHeight(), 16, Material.BEDROCK);
            break;
        case NETHER:
            chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, worldInfo.getMaxHeight(), 16, Material.NETHERRACK);
            chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, 34, 16, Material.SOUL_SAND);
            chunkData.setRegion(0, worldInfo.getMaxHeight() - 1, 0, 16, worldInfo.getMaxHeight(), 16, Material.BEDROCK);
            break;
        case THE_END:
            chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, worldInfo.getMaxHeight(), 16, Material.END_STONE);
            chunkData.setRegion(0, worldInfo.getMaxHeight() - 1, 0, 16, worldInfo.getMaxHeight(), 16, Material.BEDROCK);
            break;
        }

        // Generate ores
        for (int y = worldInfo.getMinHeight(); y < worldInfo.getMaxHeight(); y++) {
            for (Ore o: ORES.get(worldInfo.getEnvironment())) {
                if (y > o.minY() && y < o.maxY() && r.nextInt(100) <= o.chance()) {
                    pasteBlob(chunkData, y, o, r);
                    if (o.cont()) {
                        break;
                    }
                }
            }

        }
    }

    private void pasteBlob(ChunkData chunkData, int y, Ore o, Random r) {
        int offset = r.nextInt(16);
        for (int x = Math.max(0, offset - BLOB_SIZE); x < Math.min(16, offset + BLOB_SIZE); x++) {
            for (int z = Math.max(0, offset - BLOB_SIZE); z < Math.min(16, offset + BLOB_SIZE); z++) {
                for (int yy = Math.max(chunkData.getMinHeight(), y - BLOB_SIZE); yy < Math.min(chunkData.getMaxHeight(),y + BLOB_SIZE); yy++) {
                    BlockData bd = chunkData.getBlockData(x, yy, z);
                    if (bd.getMaterial().isSolid() && r.nextBoolean()) {
                        chunkData.setBlock(x, yy, z, o.material());
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldGenerateSurface() {
        return false;
    }

    @Override
    public boolean shouldGenerateBedrock() {
        return true;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return true;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return true;
    }

}
