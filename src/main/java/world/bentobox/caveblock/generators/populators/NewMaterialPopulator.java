package world.bentobox.caveblock.generators.populators;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import world.bentobox.caveblock.Utils;
import world.bentobox.caveblock.generators.Ore;

import java.util.*;

public class NewMaterialPopulator extends BlockPopulator {
    private final Map<World.Environment, List<Ore>> ores = new EnumMap<>(World.Environment.class);

    public NewMaterialPopulator() {
        // Source https://minecraft.fandom.com/wiki/Blob
        List<Ore> worldOres = new ArrayList<>();
        worldOres.add(new Ore(-64, 16, Material.DIAMOND_ORE, 1, 10, true));
        worldOres.add(new Ore(-64, 64, Material.LAPIS_ORE, 1, 7, true));
        worldOres.add(new Ore(-64, 30, Material.GOLD_ORE, 2, 9, true));
        worldOres.add(new Ore(0, 16, Material.TUFF, 2, 33, false));
        worldOres.add(new Ore(-64, 16, Material.REDSTONE_ORE, 8, 8, true));
        worldOres.add(new Ore(0, 16, Material.GRAVEL, 8, 33, false));
        worldOres.add(new Ore(0, 79, Material.GRANITE, 5, 33, false));
        worldOres.add(new Ore(0, 79, Material.ANDESITE, 5, 33, false));
        worldOres.add(new Ore(0, 79, Material.DIORITE, 5, 33, false));
        worldOres.add(new Ore(32, 320, Material.EMERALD_ORE, 11, 1, true));
        worldOres.add(new Ore(95, 136, Material.COAL_ORE, 20, 17, false));
        worldOres.add(new Ore(0, 96, Material.COPPER_ORE, 20, 9, true));
        worldOres.add(new Ore(-64, 320, Material.IRON_ORE, 20, 9, true));
        worldOres.add(new Ore(-64, 320, Material.CAVE_AIR, 8, 33, false));
        ores.put(World.Environment.NORMAL, worldOres);
        List<Ore> netherOres = new ArrayList<>();
        netherOres.add(new Ore(1, 22, Material.ANCIENT_DEBRIS, 1, 5, true));
        netherOres.add(new Ore(-64, 30, Material.NETHER_GOLD_ORE, 2, 9, true));
        netherOres.add(new Ore(0, 16, Material.GRAVEL, 8, 33, false));
        netherOres.add(new Ore(0, 320, Material.BASALT, 8, 33, false));
        netherOres.add(new Ore(0, 320, Material.BLACKSTONE, 8, 33, false));
        netherOres.add(new Ore(0, 320, Material.FIRE, 8, 33, false));
        netherOres.add(new Ore(200, 320, Material.GLOWSTONE, 8, 33, false));
        netherOres.add(new Ore(-64, 320, Material.CAVE_AIR, 8, 33, false));
        netherOres.add(new Ore(-64, 320, Material.LAVA, 8, 33, false));
        netherOres.add(new Ore(0, 16, Material.MAGMA_BLOCK, 8, 33, false));
        netherOres.add(new Ore(0, 320, Material.CRIMSON_FUNGUS, 8, 33, false));
        netherOres.add(new Ore(0, 320, Material.WARPED_FUNGUS, 8, 33, false));
        netherOres.add(new Ore(0, 320, Material.CRIMSON_NYLIUM, 8, 33, false));
        netherOres.add(new Ore(0, 320, Material.WARPED_NYLIUM, 8, 33, false));
        netherOres.add(new Ore(0, 320, Material.SHROOMLIGHT, 8, 33, false));
        netherOres.add(new Ore(0, 320, Material.CRIMSON_STEM, 8, 33, false));
        netherOres.add(new Ore(0, 320, Material.WARPED_STEM, 8, 33, false));
        netherOres.add(new Ore(-64, 34, Material.SOUL_SOIL, 20, 17, false));
        netherOres.add(new Ore(0, 96, Material.NETHER_QUARTZ_ORE, 20, 9, true));
        netherOres.add(new Ore(-64, 320, Material.BONE_BLOCK, 20, 9, true));
        ores.put(World.Environment.NETHER, netherOres);
        List<Ore> endOres = new ArrayList<>();
        endOres.add(new Ore(32, 320, Material.PURPUR_BLOCK, 11, 1, true));
        endOres.add(new Ore(95, 136, Material.OBSIDIAN, 20, 17, false));
        endOres.add(new Ore(-64, 320, Material.CAVE_AIR, 8, 33, false));
        ores.put(World.Environment.THE_END, endOres);
    }

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        for (int y = worldInfo.getMinHeight(); y < worldInfo.getMaxHeight(); y++) {
            for (Ore o : ores.get(worldInfo.getEnvironment())) {
                if (y > o.minY() && y < o.maxY() && random.nextInt(100) <= o.chance()) {
                    pasteBlob(worldInfo, random, chunkX, chunkZ, limitedRegion, y, o);
                    if (o.cont()) {
                        break;
                    }
                }
            }
        }
    }

    private void pasteBlob(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion, int y, Ore o) {
        //int blobSize = (int) (((double)random.nextInt(o.blob()) / 3) + 1);
        int blobSize = 1;
        int offset = random.nextInt(16);
        for (int x = Math.max(0, offset - blobSize); x < Math.min(16, offset + blobSize); x++) {
            for (int z = Math.max(0, offset - blobSize); z < Math.min(16, offset + blobSize); z++) {
                for (int yy = Math.max(worldInfo.getMinHeight(), y - blobSize); yy < Math.min(worldInfo.getMaxHeight(), y + blobSize); yy++) {
                    Location location = Utils.getLocationFromChunkLocation(x, yy, z, chunkX, chunkZ);
                    if (!limitedRegion.isInRegion(location)) {
                        continue;
                    }
                    if (limitedRegion.getType(location).isSolid() && random.nextBoolean()) {
                        limitedRegion.setType(location, o.material());
                    }
                }
            }
        }
    }
}
