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
	// Nether veins. These only replace solid blocks, so they stay embedded in
	// the rock and are exposed on the walls of the noise-carved caves. No FIRE
	// (laggy, ugly), no scattered CAVE_AIR or LAVA — caves and the lava sea are
	// now shaped by the noise generator, keeping the world solid.
	List<Ore> netherOres = new ArrayList<>();
	netherOres.add(new Ore(1, 22, Material.ANCIENT_DEBRIS, 1, 5, true));
	netherOres.add(new Ore(-64, 30, Material.NETHER_GOLD_ORE, 2, 9, true));
	netherOres.add(new Ore(0, 96, Material.NETHER_QUARTZ_ORE, 20, 9, true));
	netherOres.add(new Ore(0, 16, Material.GRAVEL, 8, 33, false));
	netherOres.add(new Ore(0, 320, Material.BASALT, 6, 33, false));
	netherOres.add(new Ore(0, 320, Material.BLACKSTONE, 6, 33, false));
	netherOres.add(new Ore(0, 320, Material.GLOWSTONE, 4, 12, false));
	netherOres.add(new Ore(0, 24, Material.MAGMA_BLOCK, 6, 20, false));
	netherOres.add(new Ore(-64, 40, Material.SOUL_SOIL, 10, 17, false));
	netherOres.add(new Ore(-64, 320, Material.BONE_BLOCK, 6, 9, false));
	ores.put(World.Environment.NETHER, netherOres);
	// End veins. Caves are shaped by the noise generator, so no scattered CAVE_AIR.
	List<Ore> endOres = new ArrayList<>();
	endOres.add(new Ore(32, 320, Material.PURPUR_BLOCK, 8, 12, false));
	endOres.add(new Ore(0, 320, Material.OBSIDIAN, 4, 9, false));
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

    /**
     * Scatters a vein of {@code o.material()} around a random centre in this chunk.
     *
     * <p>The number of blocks placed is driven by {@link Ore#blob()} (the vein size),
     * spread over a radius derived from the cube root of that size so a vein of N
     * blocks occupies a roughly N-block volume instead of collapsing to a single
     * point. The X, Z and Y offsets are chosen independently so veins are not pinned
     * to the chunk diagonal.</p>
     */
    private void pasteBlob(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion,
	    int y, Ore o) {
	final int blobSize = Math.max(1, o.blob());
	// Half-extent of the vein: scales with the cube root of the requested size.
	final int radius = Math.max(1, (int) Math.round(Math.cbrt(blobSize)));
	final int worldHeight = Math.min(worldInfo.getMaxHeight(), this.worldDepth);
	final int centreX = random.nextInt(16);
	final int centreZ = random.nextInt(16);

	int placed = 0;
	// Bound the attempts so a vein in a region with few solid blocks cannot loop forever.
	final int maxAttempts = blobSize * 8;
	for (int attempt = 0; placed < blobSize && attempt < maxAttempts; attempt++) {
	    int x = centreX + random.nextInt(2 * radius + 1) - radius;
	    int z = centreZ + random.nextInt(2 * radius + 1) - radius;
	    int yy = y + random.nextInt(2 * radius + 1) - radius;
	    boolean inChunk = x >= 0 && x <= 15 && z >= 0 && z <= 15;
	    boolean inDepth = yy > worldInfo.getMinHeight() && yy < worldHeight - 1;
	    if (inChunk && inDepth) {
		Location location = Utils.getLocationFromChunkLocation(x, yy, z, chunkX, chunkZ);
		if (limitedRegion.isInRegion(location) && limitedRegion.getType(location).isSolid()) {
		    limitedRegion.setType(location, o.material());
		    placed++;
		}
	    }
	}
    }
}
