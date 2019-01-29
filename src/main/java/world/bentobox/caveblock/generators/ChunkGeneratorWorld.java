package world.bentobox.caveblock.generators;


import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import java.util.*;
import java.util.stream.Collectors;

import world.bentobox.bentobox.util.Pair;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;


/**
 * Class ChunkGeneratorWorld ...
 *
 * @author BONNe
 * Created on 27.01.2019
 */
public class ChunkGeneratorWorld extends ChunkGenerator
{
// ---------------------------------------------------------------------
// Section: Constructor
// ---------------------------------------------------------------------


	/**
	 * @param addon - CaveBlock object
	 */
	public ChunkGeneratorWorld(CaveBlock addon)
	{
		super();
		this.addon = addon;
		this.settings = addon.getSettings();
	}


// ---------------------------------------------------------------------
// Section: Methods
// ---------------------------------------------------------------------



	/**
	 * This method sets if given coordinates can be set as spawn location
	 */
	@Override
	public boolean canSpawn(World world, int x, int z)
	{
		return true;
	}


	/**
	 * This method generates given chunk.
	 * @param world World where chunk must be generated.
	 * @param random Random that allows define object randomness.
	 * @param chunkX Chunk X coordinate.
	 * @param chunkZ Chunk Z coordinate.
	 * @param biomeGrid BiomeGrid that contains biomes.
	 * @return new ChunkData for given chunk.
	 */
	@Override
	public ChunkData generateChunkData(World world,
		Random random,
		int chunkX,
		int chunkZ,
		ChunkGenerator.BiomeGrid biomeGrid)
	{
		ChunkData result = this.createChunkData(world);
		this.generator = new PerlinOctaveGenerator((long) (random.nextLong() * random.nextGaussian()), 8);

		// Populate chunk with necessary information
		if (world.getEnvironment().equals(World.Environment.NETHER))
		{
			this.populateNetherChunk(world, result, random, biomeGrid);
		}
		else if (world.getEnvironment().equals(World.Environment.THE_END))
		{
			this.populateTheEndChunk(world, result, random, biomeGrid);
		}
		else
		{
			this.populateOverWorldChunk(world, result, random, biomeGrid);
		}

		return result;
	}


	/**
	 * This method populates The End world chunk data.
	 * @param chunkData ChunkData that must be populated.
	 * @param random Randomness in given world.
	 * @param biomeGrid BiomeGrid for this chunk.
	 */
	private void populateTheEndChunk(World world, ChunkData chunkData, Random random, BiomeGrid biomeGrid)
	{
		final int worldHeight = this.settings.getWorldDepth();
		final boolean generateFloor = this.settings.isEndFloor();
		final boolean generateCeiling = this.settings.isEndRoof();

		chunkData.setRegion(0, 0, 0, 16, worldHeight, 16, this.settings.getEndMainBlock());

		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				chunkData.setBlock(x, 0, z, generateFloor ? Material.BEDROCK : Material.END_STONE);
				chunkData.setBlock(x, worldHeight, z, generateCeiling ? Material.BEDROCK : Material.END_STONE);
			}
		}
	}


	/**
	 * This method populates nether world chunk data.
	 * @param chunkData ChunkData that must be populated.
	 * @param random Randomness in given world.
	 * @param biomeGrid BiomeGrid for this chunk.
	 */
	private void populateNetherChunk(World world, ChunkData chunkData, Random random, BiomeGrid biomeGrid)
	{
		final int worldHeight = this.settings.getWorldDepth();
		final boolean generateFloor = this.settings.isNetherFloor();
		final boolean generateCeiling = this.settings.isNetherRoof();

		chunkData.setRegion(0, 0, 0, 16, worldHeight, 16, this.settings.getNetherMainBlock());

		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				chunkData.setBlock(x, 0, z, generateFloor ? Material.BEDROCK : Material.NETHERRACK);
				chunkData.setBlock(x, worldHeight, z, generateCeiling ? Material.BEDROCK : Material.NETHERRACK);

				// Populate with GlowStone randomness

				// Next three layers are a mix of bedrock
				for (int y = 2; y < 5; y++)
				{
					double r = this.generator.noise(x, (worldHeight - y), z, 0.5, 0.5);

					if (r > 0D)
					{
						chunkData.setBlock(x, (worldHeight - y), z, Material.BEDROCK);
					}
				}

				// Layer 8 may be glowstone
				double r = this.generator.noise(x,
					(double) worldHeight - 8,
					z,
					random.nextFloat(),
					random.nextFloat());

				if (r > 0.5D)
				{
					// Have blobs of glowstone
					switch (random.nextInt(4))
					{
						case 1:
							// Single block
							chunkData.setBlock(x, (worldHeight - 8), z, Material.GLOWSTONE);

							if (x < 14 && z < 14)
							{
								chunkData.setBlock(x + 1,
									(worldHeight - 8),
									z + 1,
									Material.GLOWSTONE);
								chunkData.setBlock(x + 2,
									(worldHeight - 8),
									z + 2,
									Material.GLOWSTONE);
								chunkData.setBlock(x + 1,
									(worldHeight - 8),
									z + 2,
									Material.GLOWSTONE);
								chunkData.setBlock(x + 1,
									(worldHeight - 8),
									z + 2,
									Material.GLOWSTONE);
							}
							break;
						case 2:
							// Stalatite
							for (int i = 0; i < random.nextInt(10); i++)
							{
								chunkData.setBlock(x,
									(worldHeight - 8 - i),
									z,
									Material.GLOWSTONE);
							}
							break;
						case 3:
							chunkData.setBlock(x, (worldHeight - 8), z, Material.GLOWSTONE);

							if (x > 3 && z > 3)
							{
								for (int xx = 0; xx < 3; xx++)
								{
									for (int zz = 0; zz < 3; zz++)
									{
										chunkData.setBlock(x - xx,
											(worldHeight - 8 - random.nextInt(2)),
											z - xx,
											Material.GLOWSTONE);
									}
								}
							}
							break;
						default:
							chunkData.setBlock(x, (worldHeight - 8), z, Material.GLOWSTONE);
							break;
					}

					chunkData.setBlock(x, (worldHeight - 8), z, Material.GLOWSTONE);
				}
			}
		}
	}


	/**
	 * This method populates Over world chunk data.
	 * @param chunkData ChunkData that must be populated.
	 * @param random Randomness in given world.
	 * @param biomeGrid BiomeGrid for this chunk.
	 */
	private void populateOverWorldChunk(World world, ChunkData chunkData, Random random, BiomeGrid biomeGrid)
	{
		final int worldHeight = this.settings.getWorldDepth();
		final boolean generateFloor = this.settings.isNormalFloor();
		final boolean generateCeiling = this.settings.isNormalRoof();

		chunkData.setRegion(0, 0, 0, 16, worldHeight, 16, this.settings.getNormalMainBlock());

		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				chunkData.setBlock(x, 0, z, generateFloor ? Material.BEDROCK : Material.STONE);
				chunkData.setBlock(x, worldHeight, z, generateCeiling ? Material.BEDROCK : Material.STONE);

				biomeGrid.setBiome(x, z, this.addon.getSettings().getDefaultBiome());
			}
		}
	}


	/**
	 * This method set world block populators.
	 * @param world World where this must apply.
	 * @return List with block populators.
	 */
	@Override
	public List<BlockPopulator> getDefaultPopulators(final World world)
	{
		return Arrays.asList(new BlockPopulator[0]);
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
				Material material = Material.getMaterial(splitString[0]);

				if (material != null)
				{
					materialMap.put(material,
						new Pair<>(Integer.parseInt(splitString[1]), Integer.parseInt(splitString[2])));
				}
			});

		return materialMap;
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
						new Pair<>(Integer.parseInt(splitString[1]), Integer.parseInt(splitString[2])));
				}
			});

		return entityMap;
	}


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


	/**
	 * CaveBlock addon.
	 */
	private CaveBlock addon;

	/**
	 * Addon settings.
	 */
	private Settings settings;

	/**
	 * Generator that allows to generate custom structures.
	 */
	private PerlinOctaveGenerator generator;
}
