package world.bentobox.caveblock.generators;


import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
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
    private List<Ore> ores = new ArrayList<>();

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
        ores.add(new Ore(-64, 16, Material.DIAMOND_ORE, 1, 10, true));
        ores.add(new Ore(-64, 64, Material.LAPIS_ORE, 1, 7, true));
        ores.add(new Ore(-64, 30, Material.GOLD_ORE, 2, 9, true));
        ores.add(new Ore(0, 16, Material.TUFF, 2, 33, false));
        ores.add(new Ore(-64, 16, Material.REDSTONE_ORE, 8, 8, true));
        ores.add(new Ore(0, 16, Material.GRAVEL, 8 , 33, false));
        ores.add(new Ore(0, 79, Material.GRANITE, 5, 33, false));
        ores.add(new Ore(0, 79, Material.ANDESITE,5, 33, false));
        ores.add(new Ore(0, 79, Material.DIORITE,5, 33, false));
        ores.add(new Ore(32, 320, Material.EMERALD_ORE, 11, 1, true));
        ores.add(new Ore(95, 136, Material.COAL_ORE, 20, 17, false));
        ores.add(new Ore(0, 96, Material.COPPER_ORE, 20, 9, true));
        ores.add(new Ore(-64, 320, Material.IRON_ORE, 20, 9, true));
    }


    // ---------------------------------------------------------------------
    // Section: Methods
    // ---------------------------------------------------------------------

    @SuppressWarnings("deprecation")
    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        //BentoBox.getInstance().logDebug("Generate Chunk Data " + x + " " + z);
        return createChunkData(world);
    }
    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int x, int z, ChunkData chunkData) {
        chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, worldInfo.getMaxHeight(), 16, Material.STONE);
        chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, 7, 16, Material.DEEPSLATE);
        chunkData.setRegion(0, worldInfo.getMaxHeight() - 1, 0, 16, worldInfo.getMaxHeight(), 16, Material.BEDROCK);
        // Generate ores
        for (int y = worldInfo.getMinHeight(); y < worldInfo.getMaxHeight(); y++) {
            for (Ore o: ores) {
                if (o.minY() < y && o.maxY() > y && r.nextInt(100) <= o.chance()) {
                    pasteBlob(chunkData, y, o);
                    if (o.cont()) {
                        break;
                    }
                }
            }

        }
    }

    private void pasteBlob(ChunkData chunkData, int y, Ore o) {
        int blobSize = (int) (((double)r.nextInt(o.blob()) / 3) + 1);
        for (int x = 8 - blobSize; x < 8 + blobSize; x++) {
            for (int z = 8 - blobSize; z < 8 + blobSize; z++) {
                for (int yy = y - blobSize; yy < y + blobSize; yy++) {
                    if (r.nextBoolean()) {
                        chunkData.setBlock(x, yy, z, o.material());
                    }
                }
            }
        }

    }


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
