
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


/**
 * This class allows to fill given chunk with necessary blocks.
 */
public class MaterialPopulator extends BlockPopulator
{

    /**
     * This is default constructor
     * @param addon CaveBlock addon.
     */
    public MaterialPopulator(CaveBlock addon)
    {
        this.addon = addon;
        // Load settings
        this.loadSettings();
    }


    /**
     * Loads chances for Material Populator
     */
    private void loadSettings() {
        // Set up chances
        chances = new HashMap<>();
        // Normal
        chances.put(Environment.NORMAL, new Chances(this.getMaterialMap(addon.getSettings().getNormalBlocks()), addon.getSettings().getNormalMainBlock()));
        // Nether
        chances.put(Environment.NETHER, new Chances(this.getMaterialMap(addon.getSettings().getNetherBlocks()), addon.getSettings().getNetherMainBlock()));
        // End
        chances.put(Environment.THE_END, new Chances(this.getMaterialMap(addon.getSettings().getEndBlocks()), addon.getSettings().getEndMainBlock()));
        // Other settings
        worldHeight = addon.getSettings().getWorldDepth();
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
        int minHeight = world.getMinHeight();
        int height = Math.min(world.getMaxHeight(), worldHeight) - 1;
        Chances chances = this.chances.get(world.getEnvironment());

        for (Map.Entry<Material, Pair<Double, Integer>> entry : chances.materialChanceMap.entrySet())
        {
            for (int subY = minHeight + 1; subY < height; subY += 16)
            {
                if (random.nextDouble() * 100 < entry.getValue().x)
                {

                    // Blocks must be 1 away from edge to avoid adjacent chunk loading
                    int x = random.nextInt(13) + 1;
                    int z =  random.nextInt(13) + 1;
                    int y = Math.min(height - 2, subY + random.nextInt(15));
                    /*
                     * TODO: remove
                    if (addon.getSettings().isDebug()) {
                        addon.log("DEBUG: Material: " + world.getName() + " " + x + " " + y + " " + z + " " + entry.getKey());
                    }
                     */
                    Block block = chunk.getBlock(x, y, z);

                    if (block.getType().equals(chances.mainMaterial))
                    {
                        int packSize = random.nextInt(entry.getValue().z);

                        boolean continuePlacing = true;

                        while (continuePlacing)
                        {
                            if (!block.getType().equals(entry.getKey()))
                            {
                                // Set type without physics is required otherwise server goes into an infinite loop
                                block.setType(entry.getKey(), false);
                                packSize--;
                            }

                            // The direction chooser
                            switch (random.nextInt(5))
                            {
                            case 0:
                                x = Math.min(15, x + 1);
                                break;
                            case 1:
                                y = Math.min(height - 2, y + 1);
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

                            continuePlacing = packSize > 0 && (block.getType().equals(chances.mainMaterial) ||
                                    block.getType().equals(entry.getKey()));
                        }
                    }
                }
            }
        }
    }

    /**
     * This method returns material frequently and pack size map.
     * @param objectList List with objects that contains data.
     * @return Map that contains material, its rarity and pack size.
     */
    private Map<Material, Pair<Double, Integer>> getMaterialMap(List<String> objectList)
    {
        Map<Material, Pair<Double, Integer>> materialMap = new HashMap<>(objectList.size());

        // wrong material object.
        objectList.stream().
        filter(object -> object.startsWith("MATERIAL")).
        map(object -> object.split(":")).
        filter(splitString -> splitString.length == 4).
        forEach(splitString -> {
            Material material = Material.getMaterial(splitString[1]);
            // Material must be a block otherwise the chunk cannot be populated
            if (material != null && material.isBlock())
            {
                materialMap.put(material,
                        new Pair<>(Double.parseDouble(splitString[2]), Integer.parseInt(splitString[3])));
            } else {
                addon.logError("Could not parse MATERIAL in config.yml: " + splitString[1] + " is not a valid block.");
            }
        });

        return materialMap;
    }


    // ---------------------------------------------------------------------
    // Section: Private Classes
    // ---------------------------------------------------------------------


    /**
     * Chances class to store chances for environments and main material
     */
    private class Chances
    {
        /**
         * @param materialChanceMap - contains chances for each material.
         * @param mainMaterial - material on which material can replace.
         */
        Chances(Map<Material, Pair<Double, Integer>> materialChanceMap, Material mainMaterial)
        {
            this.materialChanceMap = materialChanceMap;
            this.mainMaterial = mainMaterial;
        }


        /**
         * Map that contains chances for entity to spawn.
         */
        final Map<Material, Pair<Double, Integer>> materialChanceMap;

        /**
         * Main material that can be replaced.
         */
        final Material mainMaterial;
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------


    /**
     * CaveBlock addon.
     */
    private CaveBlock addon;

    /**
     * Map that contains chances for spawning per environment.
     */
    private Map<Environment, Chances> chances;

    /**
     * World height
     */
    private int worldHeight;
}
