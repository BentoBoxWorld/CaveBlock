package world.bentobox.caveblock.generators;


import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;


/**
 * Class ChunkGeneratorWorld ...
 *
 * @author BONNe
 * Created on 27.01.2019
 */
public class ChunkGeneratorWorld extends ChunkGenerator
{
    private CaveBlock addon;
    private Settings settings;
    private Map<Environment, ChunkData> map = new EnumMap<>(Environment.class);
    private final Random r = new Random();
    private Map<Environment, List<Ore>> ores = new EnumMap<>(Environment.class);

    // ---------------------------------------------------------------------
    // Section: Constructor
    // ---------------------------------------------------------------------


    /**
     * @param addon - CaveBlock object
     */
    public ChunkGeneratorWorld(CaveBlock addon)
    {
        super();
        this.addon = addon;
        this.settings = addon.getSettings();
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

    }


    // ---------------------------------------------------------------------
    // Section: Methods
    // ---------------------------------------------------------------------

    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int x, int z, ChunkData chunkData) {
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
            for (Ore o: ores.get(worldInfo.getEnvironment())) {
                if (y > o.minY() && y < o.maxY() && r.nextInt(100) <= o.chance()) {
                    pasteBlob(chunkData, y, o);
                    if (o.cont()) {
                        break;
                    }
                }
            }

        }
    }

    private void pasteBlob(ChunkData chunkData, int y, Ore o) {
        //int blobSize = (int) (((double)r.nextInt(o.blob()) / 3) + 1);
        int blobSize = 1;
        int offset = r.nextInt(16);
        for (int x = Math.max(0, offset - blobSize); x < Math.min(16, offset + blobSize); x++) {
            for (int z = Math.max(0, offset - blobSize); z < Math.min(16, offset + blobSize); z++) {
                for (int yy = Math.max(chunkData.getMinHeight(), y - blobSize); yy < Math.min(chunkData.getMaxHeight(),y + blobSize); yy++) {
                    BlockData bd = chunkData.getBlockData(x, yy, z);
                    if (bd.getMaterial().isSolid() && r.nextBoolean()) {
                        chunkData.setBlock(x, yy, z, o.material());
                    }
                }
            }
        }
    }

    /*
    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int x, int z, ChunkData chunkData) {
        //BentoBox.getInstance().logDebug("generateSurface " + x + " " + z + " " + chunkData);
    }

    @Override
    public void generateBedrock(WorldInfo worldInfo, Random random, int x, int z, ChunkData chunkData) {
        //BentoBox.getInstance().logDebug("generateBedrock " + x + " " + z + " " + chunkData);
    }
    @Override
    public void generateCaves(WorldInfo worldInfo, Random random, int x, int z, ChunkData chunkData) {
        //BentoBox.getInstance().logDebug("generateCaves " + x + " " + z + " " + chunkData);
    }
     */
    /**
     * This method sets if given coordinates can be set as spawn location
     */
    @Override
    public boolean canSpawn(World world, int x, int z)
    {
        return true;
    }

    @Override
    public boolean shouldGenerateNoise() {
        return false;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return true;
    }

    @Override
    public boolean shouldGenerateBedrock() {
        return true;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return true;
    }



}
