package world.bentobox.caveblock.generators.populators;


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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.BoundingBox;

import world.bentobox.bentobox.util.Pair;
import world.bentobox.caveblock.CaveBlock;


/**
 * This class populates generated chunk with entites by random chance.
 */
public class EntitiesPopulator extends BlockPopulator
{

    /**
     * This is default constructor
     * @param addon CaveBlock addon.
     */
    public EntitiesPopulator(CaveBlock addon)
    {
        this.addon = addon;
        this.loadSettings();
    }


    /**
     * This method load chances per environment.
     */
    private void loadSettings() {
        // Set up chances
        chances = new HashMap<>();
        // Normal
        chances.put(Environment.NORMAL, new Chances(this.getEntityMap(addon.getSettings().getNormalBlocks()), addon.getSettings().getNormalMainBlock()));
        // Nether
        chances.put(Environment.NETHER, new Chances(this.getEntityMap(addon.getSettings().getNetherBlocks()), addon.getSettings().getNetherMainBlock()));
        // End
        chances.put(Environment.THE_END, new Chances(this.getEntityMap(addon.getSettings().getEndBlocks()), addon.getSettings().getEndMainBlock()));
        // Other settings
        worldHeight = addon.getSettings().getWorldDepth();
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
        int minHeight = world.getMinHeight();
        int height = Math.min(world.getMaxHeight(), worldHeight) - 1;

        for (Map.Entry<EntityType, Pair<Double, Integer>> entry : chances.get(world.getEnvironment()).entityChanceMap.entrySet())
        {
            for (int subY = minHeight; subY < height; subY += 16)
            {
                // Use double so chance can be < 1
                if (random.nextDouble() * 100 < entry.getValue().x)
                {
                    int y = Math.min(height - 2, subY + random.nextInt(15));
                    // Spawn only in middle of chunk because bounding box will grow out from here
                    this.tryToPlaceEntity(world, chunk.getBlock(7, y, 7), entry.getKey(), chances.get(world.getEnvironment()).mainMaterial);
                }
            }
        }
    }


    /**
     * This method returns Entity frequently and pack size map.
     * @param objectList List with objects that contains data.
     * @return Map that contains entity, its rarity and pack size.
     */
    private Map<EntityType, Pair<Double, Integer>> getEntityMap(List<String> objectList)
    {
        Map<EntityType, Pair<Double, Integer>> entityMap = new HashMap<>(objectList.size());

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
                        new Pair<>(Double.parseDouble(splitString[2]), Integer.parseInt(splitString[3])));
            }
        });

        return entityMap;
    }

    /**
     * Places entities if there is room for them.
     * @param world - World were mob must be spawned.
     * @param block - Block that was chosen by random.
     * @param entity - Entity that must be spawned.
     * @param originalMaterial - replacement material.
     */
    private void tryToPlaceEntity(World world, Block block, EntityType entity, Material originalMaterial)
    {
        if (block.getType().equals(originalMaterial)) {
            // Spawn entity
            Entity e = world.spawnEntity(block.getLocation().add(0.5, 0, 0.5), entity);
            if (e instanceof LivingEntity) {
                // Do not despawn
                ((LivingEntity)e).setRemoveWhenFarAway(false);
            }
            // Make space for entity based on the entity's size
            BoundingBox bb = e.getBoundingBox();

            for (int x = (int) Math.floor(bb.getMinX()); x < bb.getMaxX(); x++) {
                for (int z = (int) Math.floor(bb.getMinZ()); z < bb.getMaxZ(); z++) {
                    int y = (int) Math.floor(bb.getMinY());
                    Block b = world.getBlockAt(x, y, z);
                    for (; y < bb.getMaxY(); y++) {
                        if (addon.getSettings().isDebug()) {
                            addon.log("DEBUG: Entity spawn: " + world.getName() + " " + x + " " + y + " " + z + " " + e.getType());
                        }
                        b = world.getBlockAt(x, y, z);
                        if (!b.getType().equals(originalMaterial)) {
                            // Cannot place entity
                            e.remove();
                            return;
                        }
                        b.setType(WATER_ENTITIES.contains(entity) ? Material.WATER : Material.AIR);
                    }
                    // Add air block on top for all water entities (required for dolphin, okay for others)
                    if (WATER_ENTITIES.contains(entity) && b.getRelative(BlockFace.UP).getType().equals(originalMaterial)) {
                        b.getRelative(BlockFace.UP).setType(Material.CAVE_AIR);
                    }
                }
            }
        }
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
         * @param entityChanceMap - contains chances for each entity.
         * @param mainMaterial - material on which entity can replace.
         */
        Chances(Map<EntityType, Pair<Double, Integer>> entityChanceMap, Material mainMaterial)
        {
            this.entityChanceMap = entityChanceMap;
            this.mainMaterial = mainMaterial;
        }


        /**
         * Map that contains chances for entity to spawn.
         */
        final Map<EntityType, Pair<Double, Integer>> entityChanceMap;

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

    /**
     * Water entities
     */
    private final static List<EntityType> WATER_ENTITIES = Arrays.asList(EntityType.GUARDIAN,
            EntityType.SQUID,
            EntityType.COD,
            EntityType.SALMON,
            EntityType.PUFFERFISH,
            EntityType.TROPICAL_FISH,
            EntityType.DROWNED,
            EntityType.DOLPHIN);
}
