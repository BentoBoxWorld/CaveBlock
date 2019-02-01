
package world.bentobox.caveblock.generators.populators;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import world.bentobox.bentobox.util.Pair;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;


/**
 * This class allows to fill given chunk with necessary blocks.
 */
public class MaterialPopulator extends BlockPopulator
{
    private Map<Environment, Chances> chances;

    private final int generationTry;

    private int worldHeight;

    /**
     * This is default constructor
     * @param addon CaveBlock addon.
     */
    public MaterialPopulator(CaveBlock addon)
    {
        this.addon = addon;
        this.settings = addon.getSettings();
        // Set up chances
        chances = new HashMap<>();
        // Normal
        chances.put(Environment.NORMAL, new Chances(this.getMaterialMap(this.settings.getNormalBlocks()), this.settings.getNormalMainBlock()));
        // Nether
        chances.put(Environment.NETHER, new Chances(this.getMaterialMap(this.settings.getNetherBlocks()), this.settings.getNetherMainBlock()));
        // End
        chances.put(Environment.THE_END, new Chances(this.getMaterialMap(this.settings.getEndBlocks()), this.settings.getEndMainBlock()));
        // Other settings
        generationTry = this.settings.getNumberOfBlockGenerationTries();
        worldHeight = this.settings.getWorldDepth() - 1;

    }


    /**
     * This method populates chunk with blocks.
     * @param world World where population must be.
     * @param random Randomness
     * @param chunk Chunk were populator operates.
     */
    @Override
    public void populate(World world, Random random, Chunk chunk)
    {
        for (Map.Entry<Material, Pair<Integer, Integer>> entry : chances.get(world.getEnvironment()).materialChanceMap.entrySet())
        {
            for (int subY = 0; subY < worldHeight; subY += 16)
            {
                for (int tries = 0; tries < generationTry; tries++)
                {
                    if (random.nextInt(100) < entry.getValue().x)
                    {
                        int x = random.nextInt(15);
                        int z =  random.nextInt(15);
                        int y = Math.min(worldHeight - 2, subY + random.nextInt(15));

                        Block block = chunk.getBlock(x, y, z);

                        if (block.getType().equals(chances.get(world.getEnvironment()).mainMaterial) &&
                                this.isValidBlock(world, block, x, z))
                        {
                            int packSize = random.nextInt(entry.getValue().z);

                            boolean continuePlacing = true;

                            while (continuePlacing)
                            {
                                if (!block.getType().equals(entry.getKey()))
                                {
                                    block.setType(entry.getKey());
                                    packSize--;
                                }

                                // The direction chooser
                                switch (random.nextInt(5))
                                {
                                case 0:
                                    x = Math.min(15, x + 1);
                                    break;
                                case 1:
                                    y = Math.min(worldHeight - 2, y + 1);
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

                                block = chunk.getBlock(x, y, z);

                                continuePlacing = this.isValidBlock(world, block, x, z) &&
                                        packSize > 0 &&
                                        (block.getType().equals(chances.get(world.getEnvironment()).mainMaterial) ||
                                                block.getType().equals(entry.getKey()));
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * This method checks if all chunks around given block is generated.
     * @param world World in which block is located
     * @param block Block that must be checked.
     * @param x Block x-index in chunk
     * @param z Block z-index in chunk
     * @return true, if all chunks around given block are generated.
     */
    private boolean isValidBlock(World world, Block block, int x, int z)
    {
        return x > 0 && x < 15 && z > 0 && z < 15 ||
                world.isChunkGenerated(block.getX() + 1, block.getZ()) &&
                world.isChunkGenerated(block.getX() - 1, block.getZ()) &&
                world.isChunkGenerated(block.getX(), block.getZ() - 1) &&
                world.isChunkGenerated(block.getX(), block.getZ() + 1);
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
     * CaveBlock settings.
     */
    private Settings settings;


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
