package world.bentobox.caveblock.generators.populators;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;

import world.bentobox.bentobox.util.Pair;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;


/**
 * This class populates generated chunk with enitites by random chance.
 */
public class EntitiesPopulator extends BlockPopulator
{

    private Map<Environment, Chances> chances;

    private int worldHeight;


    /**
     * This is default constructor
     * @param addon CaveBlock addon.
     */
    public EntitiesPopulator(CaveBlock addon)
    {
        this.addon = addon;
        this.settings = addon.getSettings();
        // Set up chances
        chances = new HashMap<>();
        // Normal
        chances.put(Environment.NORMAL, new Chances(this.getEntityMap(this.settings.getNormalBlocks()), this.settings.getNormalMainBlock()));
        // Nether
        chances.put(Environment.NETHER, new Chances(this.getEntityMap(this.settings.getNetherBlocks()), this.settings.getNetherMainBlock()));
        // End
        chances.put(Environment.THE_END, new Chances(this.getEntityMap(this.settings.getEndBlocks()), this.settings.getEndMainBlock()));
        // Other settings
        worldHeight = this.settings.getWorldDepth() - 1;
    }


    /**
     * This method populates chunk with entities.
     * @param world World where population must be.
     * @param random Randomness
     * @param chunk Chunk were populator operates.
     */
    @Override
    public void populate(World world, Random random, Chunk chunk)
    {
        for (Map.Entry<EntityType, Pair<Integer, Integer>> entry : chances.get(world.getEnvironment()).entityChanceMap.entrySet())
        {
            for (int subY = 0; subY < worldHeight; subY += 16)
            {
                if (random.nextInt(100) < entry.getValue().x)
                {
                    // Do not pick blocks at edge of chunk
                    int x = random.nextInt(13)+2;
                    int z = random.nextInt(13)+2;
                    int y = Math.min(worldHeight - 3, subY + random.nextInt(15));
                    this.tryToPlaceEntity(world, chunk.getBlock(x, y, z), entry.getKey(), chances.get(world.getEnvironment()).mainMaterial);
                }
            }
        }
    }


    /**
     * This method returns Entity frequently and pack size map.
     * @param objectList List with objects that contains data.
     * @return Map that contains entity, its rarity and pack size.
     */
    private Map<EntityType, Pair<Integer, Integer>> getEntityMap(List<String> objectList)
    {
        Map<EntityType, Pair<Integer, Integer>> entityMap = new HashMap<>(objectList.size());

        Map<String, EntityType> entityTypeMap = Arrays.stream(EntityType.values()).
                collect(Collectors.toMap(Enum::name,
                        entityType -> entityType,
                        (a, b) -> b,
                        () -> new HashMap<>(EntityType.values().length)));

        // wrong material object.
        objectList.stream().
        filter(object -> object.startsWith("ENTITY")).
        map(object -> object.split(":")).
        filter(splitString -> splitString.length == 4).
        forEach(splitString -> {
            EntityType entity = entityTypeMap.getOrDefault(splitString[1], null);

            if (entity != null)
            {
                entityMap.put(entity,
                        new Pair<>(Integer.parseInt(splitString[2]), Integer.parseInt(splitString[3])));
            }
        });

        return entityMap;
    }


    /**
     * This method is not completed. It must reserve space for entities to spawn, but
     * current implementation just allows to spawn 2 high mobs that can be in single
     * place.
     * @param world - World were mob must be spawned.
     * @param block - Block that was chosen by random.
     * @param entity - Entity that must be spawned.
     * @param originalMaterial - material to be replaced
     */
    private void tryToPlaceEntity(World world, Block block, EntityType entity, Material originalMaterial)
    {
        space.clear();
        space.add(block);
        space.add(block.getRelative(BlockFace.UP));
        space.add(block.getRelative(BlockFace.WEST));
        space.add(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST));
        space.add(block.getRelative(BlockFace.NORTH));
        space.add(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH));
        space.add(block.getRelative(BlockFace.NORTH_WEST));
        space.add(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST));
        if (space.stream().allMatch(b -> b.getType().equals(originalMaterial)))
        {
            space.forEach(b -> b.setType(WATER_ENTITIES.contains(entity) ? Material.WATER : Material.CAVE_AIR));
            world.spawnEntity(block.getLocation().add(0.5, 0, 0.5), entity);
        }
    }



    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------

    /**
     * CaveBlock addon.
     */
    private CaveBlock addon;

    List<Block> space = new ArrayList<>();

    private final static List<EntityType> WATER_ENTITIES = Arrays.asList(EntityType.GUARDIAN,
            EntityType.SQUID,
            EntityType.COD,
            EntityType.SALMON,
            EntityType.PUFFERFISH,
            EntityType.TROPICAL_FISH,
            EntityType.DROWNED,
            EntityType.DOLPHIN);

    /**
     * CaveBlock settings.
     */
    private Settings settings;

    /**
     * Chances class to store chances for environments and main material
     *
     */
    private class Chances {
        final Map<EntityType, Pair<Integer, Integer>> entityChanceMap;
        final Material mainMaterial;

        /**
         * @param materialChanceMap
         * @param mainMaterial
         */
        public Chances(Map<EntityType, Pair<Integer, Integer>> entityChanceMap, Material mainMaterial) {
            this.entityChanceMap = entityChanceMap;
            this.mainMaterial = mainMaterial;
        }
    }
}
