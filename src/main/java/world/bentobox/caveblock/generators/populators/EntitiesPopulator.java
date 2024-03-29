package world.bentobox.caveblock.generators.populators;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BoundingBox;
import org.eclipse.jdt.annotation.NonNull;

import world.bentobox.bentobox.util.Pair;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class populates generated chunk with entities by random chance.
 */
public class EntitiesPopulator extends BlockPopulator {

    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------

    /**
     * Water entities
     */
    private static final List<EntityType> WATER_ENTITIES = Arrays.asList(EntityType.GUARDIAN,
            EntityType.SQUID,
            EntityType.COD,
            EntityType.SALMON,
            EntityType.PUFFERFISH,
            EntityType.TROPICAL_FISH,
            EntityType.DROWNED,
            EntityType.DOLPHIN);
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
    public EntitiesPopulator(CaveBlock addon) {
        this.addon = addon;
        this.loadSettings();
    }

    // ---------------------------------------------------------------------
    // Section: Methods
    // ---------------------------------------------------------------------

    /**
     * This method load chances per environment.
     */
    private void loadSettings() {
        // Set up chances
        chances = new EnumMap<>(Environment.class);
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
     *
     * @param worldInfo     World where population must be.
     * @param random        Randomness
     * @param chunkX        X coordinate of chunk
     * @param chunkZ        Z coordinate of chunk
     * @param limitedRegion Region where population operates.
     */
    @Override
    public void populate(WorldInfo worldInfo, @NonNull Random random, int chunkX, int chunkZ, @NonNull LimitedRegion limitedRegion) {
        int minHeight = worldInfo.getMinHeight();
        int height = Math.min(worldInfo.getMaxHeight(), worldHeight) - 1;

        for (Map.Entry<EntityType, Pair<Pair<Double, Integer>, Boolean>> entry : chances.get(worldInfo.getEnvironment()).entityChanceMap.entrySet()) {
            Pair<Double, Integer> value = entry.getValue().x;
            boolean hasAI = entry.getValue().z;
            for (int subY = minHeight; subY < height; subY += 16) {
                // Use double so chance can be < 1
                if (random.nextDouble() * 100 < value.x) {
                    int y = Math.min(height - 2, subY + random.nextInt(15));
                    // Spawn only in middle of chunk because bounding box will grow out from here
                    this.tryToPlaceEntity(
                            worldInfo, Utils.getLocationFromChunkLocation(7, y, 7, chunkX, chunkZ), limitedRegion,
                            entry.getKey(), hasAI,
                            chances.get(worldInfo.getEnvironment()).mainMaterial
                    );
                }
            }
        }
    }

    /**
     * This method returns Entity frequently and pack size map.
     *
     * @param objectList List with objects that contains data.
     * @return Map that contains entity, its rarity and pack size.
     */
    private Map<EntityType, Pair<Pair<Double, Integer>, Boolean>> getEntityMap(List<String> objectList) {
        Map<EntityType, Pair<Pair<Double, Integer>, Boolean>> entityMap = new EnumMap<>(EntityType.class);

        Map<String, EntityType> entityTypeMap = Arrays.stream(EntityType.values()).
                collect(Collectors.toMap(Enum::name,
                        entityType -> entityType,
                        (a, b) -> b,
                        () -> new HashMap<>(EntityType.values().length)));

        // wrong material object.
        objectList.stream().
                filter(object -> object.startsWith("ENTITY")).
                map(object -> object.split(":")).
                filter(splitString -> splitString.length >= 4).
                forEach(splitString -> {
                    EntityType entity = entityTypeMap.getOrDefault(splitString[1], null);
                    boolean hasAI = splitString.length <= 4 || Boolean.parseBoolean(splitString[4]);

                    if (entity != null) {
                        entityMap.put(entity,
                                new Pair<>(
                                        new Pair<>(Double.parseDouble(splitString[2]), Integer.parseInt(splitString[3])),
                                        hasAI
                                )
                        );
                    }
                });

        return entityMap;
    }

    /**
     * Places entities if there is room for them.
     *
     * @param worldInfo        - World were mob must be spawned.
     * @param location         - Location that was chosen by random.
     * @param limitedRegion    - Region where entity must be spawned.
     * @param entityType       - Entity that must be spawned.
     * @param hasAI            - If entity has AI.
     * @param originalMaterial - replacement material.
     */
    private void tryToPlaceEntity(WorldInfo worldInfo, Location location, LimitedRegion limitedRegion, EntityType entityType, boolean hasAI, Material originalMaterial) {
        if (!limitedRegion.isInRegion(location)) return;
        if (!limitedRegion.getType(location).equals(originalMaterial)) return;

        BoundingBox bb = this.getEntityBoundingBox(entityType, location);

        for (int x = (int) Math.floor(bb.getMinX()); x < bb.getMaxX(); x++) {
            for (int z = (int) Math.floor(bb.getMinZ()); z < bb.getMaxZ(); z++) {
                int y = (int) Math.floor(bb.getMinY());
                if (!limitedRegion.isInRegion(x, y, z)) {
                    return;
                }

                for (; y <= bb.getMaxY(); y++) {
                    if (addon.getSettings().isDebug()) {
                        addon.log("DEBUG: Entity spawn: " + worldInfo.getName() + " " + x + " " + y + " " + z + " " + entityType);
                    }

                    if (!limitedRegion.isInRegion(x, y, z) || !limitedRegion.getType(x, y, z).equals(originalMaterial)) {
                        // Cannot place entity
                        return;
                    }
                    limitedRegion.setType(x, y, z, WATER_ENTITIES.contains(entityType) ? Material.WATER : Material.AIR);
                }
                // Add air block on top for all water entities (required for dolphin, okay for others)
                if (WATER_ENTITIES.contains(entityType) && limitedRegion.isInRegion(x, y, z) && limitedRegion.getType(x, y, z).equals(originalMaterial)) {
                    limitedRegion.setType(x, y, z, Material.CAVE_AIR);
                }
            }
        }

        Entity entity = limitedRegion.spawnEntity(location, entityType);

        if (entity instanceof LivingEntity livingEntity)
        {
            livingEntity.setAI(hasAI);
            livingEntity.setRemoveWhenFarAway(false);
        }
    }


    /**
     * This is manual bounding box calculation base on entity type.
     * @param entityType Entity type which bounding box should be created.
     * @param location Location of the bounding box.
     * @return Approximate bounding box of the entity type.
     */
    private BoundingBox getEntityBoundingBox(EntityType entityType, Location location)
    {
        BoundingBox boundingBox = new BoundingBox();
        // Set bounding box to 1 for all entities
        boundingBox.expand(1);
        // Shift to correct location.
        boundingBox.shift(location);

        switch (entityType)
        {
            // Turtles base size is 1.1
            case TURTLE -> boundingBox.expand(-0.05, 0, -0.05, 0.05, 0, 0.05);
            // Panda base size is 1.3 and height is 1.25
            case PANDA -> boundingBox.expand(-0.15, 0, -0.15, 0.15, 0.25, 0.15);
            // Sheep height is 1.3
            case SHEEP -> boundingBox.expand(0, 0, 0, 0, 0.3, 0);
            // Cow height is 1.4
            case COW, MUSHROOM_COW -> boundingBox.expand(0, 0, 0, 0, 0.4, 0);
            // Polar Bear base size is 1.3 and height is 1.4
            case POLAR_BEAR -> boundingBox.expand(-0.15, 0, -0.15, 0.15, 0.4, 0.15);
            // Horse base size is 1.3964
            case HORSE, ZOMBIE_HORSE, SKELETON_HORSE -> boundingBox.expand(-0.2, 0, -0.2, 0.2, 0.6, 0.2);
            // Llama height is 1.875
            case LLAMA -> boundingBox.expand(0, 0, 0, 0, 0.875, 0);
            // Ravager base size is 1.95 and height is 2.2
            case RAVAGER -> boundingBox.expand(-0.48, 0, -0.48, 0.48, 1.2, 0.48);
            // Spider base size is 1.4
            case SPIDER -> boundingBox.expand(-0.2, 0, -0.2, 0.2, 0, 0.2);
            // Creeper height 1.7
            case CREEPER -> boundingBox.expand(0, 0, 0, 0, 0.7, 0);
            // Blaze height 1.8
            case BLAZE -> boundingBox.expand(0, 0, 0, 0, 0.8, 0);
            // Zombie, evoker, villager, husk, witch, vindicator, illusioner, drowned, pigman, villager and pillager height is 1.95
            case ZOMBIE, EVOKER, VILLAGER, HUSK, WITCH, VINDICATOR, ILLUSIONER, DROWNED, PIGLIN, PIGLIN_BRUTE, ZOMBIFIED_PIGLIN, ZOMBIE_VILLAGER, PILLAGER, WANDERING_TRADER ->
                boundingBox.expand(0, 0, 0, 0, 0.95, 0);
            // Skeletons height is 1.99
            case SKELETON, STRAY -> boundingBox.expand(0, 0, 0, 0, 0.99, 0);
            // Elder Guardians base height is 2
            case ELDER_GUARDIAN -> boundingBox.expand(-0.5, 0, -0.5, 0.5, 1, 0.5);
            // Slimes are up to 2.04
            case SLIME -> boundingBox.expand(-0.5, 0, -0.5, 0.5, 1, 0.5);
            // Wither skeletons height is 2.4
            case WITHER_SKELETON -> boundingBox.expand(0, 0, 0, 0, 1.4, 0);
            // Wither height is 3.5
            case WITHER -> boundingBox.expand(0, 0, 0, 0, 2.5, 0);
            // Enderman height is 2.9
            case ENDERMAN -> boundingBox.expand(0, 0, 0, 0, 1.9, 0);
            // Ghast base size is 4
            case GHAST -> boundingBox.expand(-2, 0, -2, 2, 3, 2);
            // Iron Golem base size is 1.4 and height is 2.7
            case IRON_GOLEM -> boundingBox.expand(-0.2, 0, -0.2, 0.2, 1.7, 0.2);
            // Snowman height is 1.9
            case SNOWMAN -> boundingBox.expand(0, 0, 0, 0, 0.9, 0);
            // Hoglin base size is 1.4 and height is 1.3965
            case HOGLIN, ZOGLIN -> boundingBox.expand(-0.2, 0, -0.2, 0.2, 0.4, 0.2);
            // Warden height is 2.9
            case WARDEN -> boundingBox.expand(0, 0, 0, 0, 1.9, 0);
        }

        return boundingBox;
    }


    // ---------------------------------------------------------------------
    // Section: Private Classes
    // ---------------------------------------------------------------------

    /**
     * Chances class to store chances for environments and main material
     *
     * @param entityChanceMap - contains chances for each entity, and the boolean indicates that entity should have AI.
     * @param mainMaterial    - material on which entity can replace.
     */
    private record Chances(Map<EntityType, Pair<Pair<Double, Integer>, Boolean>> entityChanceMap,
                           Material mainMaterial) {
    }
}
