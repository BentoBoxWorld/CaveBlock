package world.bentobox.caveblock.generators.populators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import world.bentobox.caveblock.Utils;
import world.bentobox.caveblock.generators.Ore;

/**
 * @author tastybento
 */
public class NewMaterialPopulator extends BlockPopulator {
    private static final int BLOB_SIZE = 1;
    private static final Map<World.Environment, List<Ore>> ORES;

    static {
	Map<World.Environment, List<Ore>> ores = new EnumMap<>(World.Environment.class);
	// Source https://minecraft.fandom.com/wiki/Blob
	List<Ore> worldOres = new ArrayList<>();
	worldOres.add(new Ore(-64, 7, Material.DEEPSLATE_DIAMOND_ORE, 1, 10, true));
	worldOres.add(new Ore(7, 16, Material.DIAMOND_ORE, 1, 10, true));
	worldOres.add(new Ore(-64, 7, Material.DEEPSLATE_LAPIS_ORE, 1, 7, true));
	worldOres.add(new Ore(7, 64, Material.LAPIS_ORE, 1, 7, true));
	worldOres.add(new Ore(-64, 7, Material.DEEPSLATE_GOLD_ORE, 2, 9, true));
	worldOres.add(new Ore(7, 30, Material.GOLD_ORE, 2, 9, true));
	worldOres.add(new Ore(0, 16, Material.TUFF, 2, 33, false));
	worldOres.add(new Ore(-64, 7, Material.DEEPSLATE_REDSTONE_ORE, 8, 8, true));
	worldOres.add(new Ore(7, 16, Material.REDSTONE_ORE, 8, 8, true));
	worldOres.add(new Ore(0, 16, Material.GRAVEL, 8, 33, false));
	worldOres.add(new Ore(0, 79, Material.GRANITE, 5, 33, false));
	worldOres.add(new Ore(0, 79, Material.ANDESITE, 5, 33, false));
	worldOres.add(new Ore(0, 79, Material.DIORITE, 5, 33, false));
	worldOres.add(new Ore(32, 320, Material.EMERALD_ORE, 11, 1, true));
	worldOres.add(new Ore(95, 136, Material.COAL_ORE, 20, 17, false));
	worldOres.add(new Ore(0, 7, Material.DEEPSLATE_COPPER_ORE, 20, 9, true));
	worldOres.add(new Ore(7, 96, Material.COPPER_ORE, 20, 9, true));
	worldOres.add(new Ore(-64, 7, Material.DEEPSLATE_IRON_ORE, 20, 9, true));
	worldOres.add(new Ore(7, 320, Material.IRON_ORE, 20, 9, true));
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
	ORES = Collections.unmodifiableMap(ores);
    }

    private final int worldDepth;

    /**
     * @param worldDepth - Depth. If depth is set smaller than the world height,
     *                   then the area above will be empty. Should not be less than
     *                   cave height.
     */
    public NewMaterialPopulator(int worldDepth) {
	this.worldDepth = worldDepth;
    }

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
	final int worldHeight = Math.min(worldInfo.getMaxHeight(), this.worldDepth);
	for (int y = worldInfo.getMinHeight() + 1; y < worldHeight - 1; y++) {
	    for (Ore o : ORES.get(worldInfo.getEnvironment())) {
		if (y > o.minY() && y < o.maxY() && random.nextInt(100) <= o.chance()) {
		    pasteBlob(worldInfo, random, chunkX, chunkZ, limitedRegion, y, o);
		    if (o.cont()) {
			break;
		    }
		}
	    }
	}
    }

    private void pasteBlob(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion,
	    int y, Ore o) {
	int offset = random.nextInt(16);
	for (int x = Math.max(0, offset - BLOB_SIZE); x < Math.min(16, offset + BLOB_SIZE); x++) {
	    for (int z = Math.max(0, offset - BLOB_SIZE); z < Math.min(16, offset + BLOB_SIZE); z++) {
		for (int yy = Math.max(worldInfo.getMinHeight() + 1, y - BLOB_SIZE); yy < Math
			.min(worldInfo.getMaxHeight() - 1, y + BLOB_SIZE); yy++) {
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
