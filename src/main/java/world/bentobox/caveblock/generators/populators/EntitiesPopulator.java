package world.bentobox.caveblock.generators.populators;


import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;
import java.util.*;
import java.util.stream.Collectors;

import world.bentobox.bentobox.util.Pair;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;


/**
 * This class populates generated chunk with enitites by random chance.
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
		this.settings = addon.getSettings();
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
		Map<EntityType, Pair<Integer, Integer>> entityChanceMap;
		Material mainMaterial;

		if (world.getEnvironment().equals(World.Environment.NETHER))
		{
			entityChanceMap = this.getEntityMap(this.settings.getNetherBlocks());
			mainMaterial = this.settings.getNetherMainBlock();
		}
		else if (world.getEnvironment().equals(World.Environment.THE_END))
		{
			entityChanceMap = this.getEntityMap(this.settings.getEndBlocks());
			mainMaterial = this.settings.getEndMainBlock();
		}
		else
		{
			entityChanceMap = this.getEntityMap(this.settings.getNormalBlocks());
			mainMaterial = this.settings.getNormalMainBlock();
		}

		final int generationTry = this.settings.getNumberOfBlockGenerationTries();
		final int worldHeight = this.settings.getWorldDepth() - 1;

		for (Map.Entry<EntityType, Pair<Integer, Integer>> entry : entityChanceMap.entrySet())
		{
			for (int subY = 1; subY < worldHeight; subY += 16)
			{
				for (int tries = 0; tries < generationTry; tries++)
				{
					if (random.nextInt(100) < entry.getValue().x)
					{
						int x = random.nextInt(15);
						int z = random.nextInt(15);
						int y = subY + random.nextInt(15);

						this.tryToPlaceEntity(world, chunk.getBlock(x, y, z), entry.getKey(), x, z, mainMaterial);
					}
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



	private void tryToPlaceEntity(World world, Block block, EntityType entity, int x, int z, Material originalMaterial)
	{
		if (this.isValidBlock(world, block, x, z) && block.getType().equals(originalMaterial))
		{
			if (entity.isAlive())
			{
				int height = 0;
				int width = 0;
				int length = 0;
				boolean water = false;

				switch (entity)
				{
					case SPIDER:
						width = 1;
						length = 1;
						break;
					case SLIME:
					case ELDER_GUARDIAN:
					case GHAST:
					case MAGMA_CUBE:
					case WITHER:
						height = 2;
						width = 2;
						length = 2;
						break;
					case ENDERMAN:
					case IRON_GOLEM:
						height = 2;
						break;
					case WITHER_SKELETON:
					case STRAY:
					case HUSK:
					case ZOMBIE_VILLAGER:
					case EVOKER:
					case VINDICATOR:
					case ILLUSIONER:
					case CREEPER:
					case SKELETON:
					case ZOMBIE:
					case BLAZE:
					case SNOWMAN:
					case VILLAGER:
					case PIG_ZOMBIE:
					case WITCH:
					case SHULKER:
					case SHEEP:
					case COW:
					case MUSHROOM_COW:
						height = 12;
						break;
					case SKELETON_HORSE:
					case ZOMBIE_HORSE:
					case DONKEY:
					case MULE:
					case HORSE:
					case POLAR_BEAR:
					case LLAMA:
						height = 1;
						width = 1;
						break;
					case GUARDIAN:
					case SQUID:
					case COD:
					case SALMON:
					case PUFFERFISH:
					case TROPICAL_FISH:
						water = true;
						break;
					case DROWNED:
					case DOLPHIN:
						water = true;
						height = 1;
						break;
				}
			}
			else
			{
				block.setType(Material.CAVE_AIR);
				world.spawnEntity(block.getLocation(), entity);
			}
		}
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
}
