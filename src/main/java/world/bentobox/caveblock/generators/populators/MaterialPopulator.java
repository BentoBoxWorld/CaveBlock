package world.bentobox.caveblock.generators.populators;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import world.bentobox.bentobox.util.Pair;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Utils;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class allows filling given chunk with necessary blocks.
 */
public class MaterialPopulator extends BlockPopulator {

    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------

    /**
     * CaveBlock addon.
     */
    private final CaveBlock addon;
    /**
     * Map that contains chances for spawning per environment.
     */
    private Map<Environment, Chances> chances;
    /**
     * World height
     */
    private int worldHeight;

    // ---------------------------------------------------------------------
    // Section: Constructor
    // ---------------------------------------------------------------------

    /**
     * This is default constructor
     *
     * @param addon CaveBlock addon.
     */
    public MaterialPopulator(CaveBlock addon) {
        this.addon = addon;
        // Load settings
        this.loadSettings();
    }

    // ---------------------------------------------------------------------
    // Section: Methods
    // ---------------------------------------------------------------------

    /**
     * Loads chances for Material Populator
     */
    private void loadSettings() {
        // Set up chances
        chances = new EnumMap<>(Environment.class);
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
     *
     * @param worldInfo     World where population must be.
     * @param random        Randomness
     * @param chunkX        X coordinate of chunk
     * @param chunkZ        Z coordinate of chunk
     * @param limitedRegion Region were populator operates.
     */
    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        int minHeight = worldInfo.getMinHeight();
        int height = Math.min(worldInfo.getMaxHeight(), worldHeight) - 1;
        Chances envChances = this.chances.get(worldInfo.getEnvironment());
        for (Map.Entry<Material, Pair<Double, Integer>> entry : envChances.materialChanceMap.entrySet()) {
            for (int subY = minHeight + 1; subY < height; subY += 16) {
                if (random.nextDouble() * 100 >= entry.getValue().x) {
                    continue;
                }

                // Blocks must be 1 away from edge to avoid adjacent chunk loading
                Location location = Utils.getLocationFromChunkLocation(
                        random.nextInt(13) + 1,
                        Math.min(height - 2, subY + random.nextInt(15)),
                        random.nextInt(13) + 1,
                        chunkX, chunkZ);

                if (!limitedRegion.isInRegion(location)) {
                    continue;
                }

                Material material = limitedRegion.getType(location);
                if (!material.equals(envChances.mainMaterial)) {
                    continue;
                }

                int packSize = random.nextInt(entry.getValue().z);
                boolean continuePlacing = true;
                while (continuePlacing) {
                    if (!material.equals(entry.getKey())) {
                        limitedRegion.setType(location, entry.getKey());
                        packSize--;
                    }

                    switch (random.nextInt(6)) {
                        case 0 -> location.setX(location.getX() + 1);
                        case 1 -> location.setY(location.getY() + 1);
                        case 2 -> location.setZ(location.getZ() + 1);
                        case 3 -> location.setX(location.getX() - 1);
                        case 4 -> location.setY(location.getY() - 1);
                        case 5 -> location.setZ(location.getZ() - 1);
                        default -> {
                            continuePlacing = false;
                            continue;
                        }
                    }

                    continuePlacing = packSize > 0 && limitedRegion.isInRegion(location) && location.getY() > minHeight;
                    if (continuePlacing) {
                        material = limitedRegion.getType(location);
                        continuePlacing = material.equals(envChances.mainMaterial) || material.equals(entry.getKey());
                    }
                }
            }
        }
    }

    /**
     * This method returns material frequently and pack size map.
     *
     * @param objectList List with objects that contains data.
     * @return Map that contains material, its rarity and pack size.
     */
    private Map<Material, Pair<Double, Integer>> getMaterialMap(List<String> objectList) {
        Map<Material, Pair<Double, Integer>> materialMap = new EnumMap<>(Material.class);

        // wrong material object.
        objectList.stream().
                filter(object -> object.startsWith("MATERIAL")).
                map(object -> object.split(":")).
                filter(splitString -> splitString.length == 4).
                forEach(splitString -> {
                    Material material = Material.getMaterial(splitString[1]);
                    // Material must be a block otherwise the chunk cannot be populated
                    if (material != null && material.isBlock()) {
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
     *
     * @param materialChanceMap - contains chances for each material.
     * @param mainMaterial      - material on which material can replace.
     */
    private record Chances(Map<Material, Pair<Double, Integer>> materialChanceMap, Material mainMaterial) {
    }
}
