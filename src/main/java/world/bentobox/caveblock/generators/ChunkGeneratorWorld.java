package world.bentobox.caveblock.generators;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import world.bentobox.bentobox.util.Pair;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;
import world.bentobox.caveblock.generators.populators.EntitiesPopulator;


/**
 * Class ChunkGeneratorWorld ...
 *
 * @author BONNe
 * Created on 27.01.2019
 */
public class ChunkGeneratorWorld extends ChunkGenerator
{
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

        this.blockPopulators = new ArrayList<>(1);

        this.blockPopulators.add(new EntitiesPopulator(this.addon));

        // Set up chances
        chances = new HashMap<>();
        // Normal
        chances.put(Environment.NORMAL, new Chances(this.getMaterialMap(this.settings.getNormalBlocks()), this.settings.getNormalMainBlock()));
        // Nether
        chances.put(Environment.NETHER, new Chances(this.getMaterialMap(this.settings.getNetherBlocks()), this.settings.getNetherMainBlock()));
        // End
        chances.put(Environment.THE_END, new Chances(this.getMaterialMap(this.settings.getEndBlocks()), this.settings.getEndMainBlock()));
        // Other settings
        worldHeight = this.settings.getWorldDepth() - 1;

    }


    // ---------------------------------------------------------------------
    // Section: Methods
    // ---------------------------------------------------------------------



    /**
     * This method sets if given coordinates can be set as spawn location
     */
    @Override
    public boolean canSpawn(World world, int x, int z)
    {
        return true;
    }


    /**
     * This method generates given chunk.
     * @param world World where chunk must be generated.
     * @param random Random that allows define object randomness.
     * @param chunkX Chunk X coordinate.
     * @param chunkZ Chunk Z coordinate.
     * @param biomeGrid BiomeGrid that contains biomes.
     * @return new ChunkData for given chunk.
     */
    @Override
    public ChunkData generateChunkData(World world,
            Random random,
            int chunkX,
            int chunkZ,
            ChunkGenerator.BiomeGrid biomeGrid)
    {
        ChunkData result = this.createChunkData(world);
        int maxHeight = Math.min(worldHeight, world.getMaxHeight());
        // Fill all blocks
        result.setRegion(0, 1, 0,
                16, maxHeight, 16,
                chances.get(world.getEnvironment()).mainMaterial);

        // Generate ground and ceiling.
        result.setRegion(0, 0, 0,
                16, 1, 16,
                this.settings.isNormalFloor() ? Material.BEDROCK : chances.get(world.getEnvironment()).mainMaterial);
        result.setRegion(0, worldHeight - 1, 0,
                16, maxHeight, 16,
                this.settings.isNormalRoof() ? Material.BEDROCK : chances.get(world.getEnvironment()).mainMaterial);

        // Set biome
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                biomeGrid.setBiome(x, z, this.settings.getDefaultBiome());
            }
        }

        // Create ores
        generateOres(world, random, result, maxHeight);

        return result;
    }

    private void generateOres(World world, Random random, ChunkData result, int maxHeight)
    {
        for (Map.Entry<Material, Pair<Integer, Integer>> entry : chances.get(world.getEnvironment()).materialChanceMap.entrySet())
        {
            for (int subY = 1; subY < maxHeight; subY += 16)
            {
                if (random.nextInt(100) < entry.getValue().x)
                {
                    int x = random.nextInt(15);
                    int z =  random.nextInt(15);
                    int y = Math.min(maxHeight - 2, subY + random.nextInt(15));

                    for (int packSize = 0; packSize < entry.getValue().z; packSize++)
                    {
                        result.setBlock(x,y,z,entry.getKey());
                        // The direction chooser
                        switch (random.nextInt(5))
                        {
                        case 0:
                            x = Math.min(15, x + 1);
                            break;
                        case 1:
                            y = Math.min(maxHeight - 2, y + 1);
                            break;
                        case 2:
                            z = Math.min(15, z + 1);
                            break;
                        case 3:
                            x = Math.max(0, x - 1);
                            break;
                        case 4:
                            y = Math.max(1, y - 1);
                            break;
                        case 5:
                            z = Math.max(0, z - 1);
                            break;
                        }

                    }
                }
            }
        }
    }



    /**
     * This method set world block populators.
     * @param world World where this must apply.
     * @return List with block populators.
     */
    @Override
    public List<BlockPopulator> getDefaultPopulators(final World world)
    {
        return this.blockPopulators;
    }

    /**
     * This method returns material frequently and pack size map.
     * @param objectList List with objects that contains data.
     * @return Map that contains material, its rarity and pack size.
     */
    private Map<Material, Pair<Integer, Integer>> getMaterialMap(List<String> objectList)
    {
        Map<Material, Pair<Integer, Integer>> materialMap = new HashMap<>(objectList.size());

        // wrong material object.
        objectList.stream().
        filter(object -> object.startsWith("MATERIAL")).
        map(object -> object.split(":")).
        filter(splitString -> splitString.length == 4).
        forEach(splitString -> {
            Material material = Material.getMaterial(splitString[1]);

            if (material != null)
            {
                materialMap.put(material,
                        new Pair<>(Integer.parseInt(splitString[2]), Integer.parseInt(splitString[3])));
            }
        });
        return materialMap;
    }

    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------


    /**
     * CaveBlock addon.
     */
    private CaveBlock addon;

    /**
     * Addon settings.
     */
    private Settings settings;

    private Map<Environment, Chances> chances;

    private int worldHeight;


    /**
     * This list contains block populators that will be applied after chunk is generated.
     */
    private List<BlockPopulator> blockPopulators;

    // Private class
    /**
     * Chances class to store chances for environments and main material
     *
     */
    private class Chances {
        final Map<Material, Pair<Integer, Integer>> materialChanceMap;
        final Material mainMaterial;

        /**
         * @param materialChanceMap
         * @param mainMaterial
         */
        public Chances(Map<Material, Pair<Integer, Integer>> materialChanceMap, Material mainMaterial) {
            this.materialChanceMap = materialChanceMap;
            this.mainMaterial = mainMaterial;
        }
    }
}
