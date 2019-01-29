package world.bentobox.caveblock.generators;


import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
			this.populateNetherChunk(result, random, biomeGrid);
		}
		else if (world.getEnvironment().equals(World.Environment.THE_END))
		{
			this.populateTheEndChunk(result, random, biomeGrid);
		}
		else
		{
			this.populateOverWorldChunk(result, random, biomeGrid);
		}

		return result;
	}


	/**
	 * This method populates The End world chunk data.
	 * @param chunkData ChunkData that must be populated.
	 * @param random Randomness in given world.
	 * @param biomeGrid BiomeGrid for this chunk.
	 */
	private void populateTheEndChunk(ChunkData chunkData, Random random, BiomeGrid biomeGrid)
	{
		Settings addonSettings = this.addon.getSettings();

		final int worldHeight = addonSettings.getMountineHeight();
		final boolean generateFloor = addonSettings.isBedRockFloor();
		final boolean generateCeiling = addonSettings.isBedRockRoof();

		chunkData.setRegion(0, 0, 0, 16, worldHeight, 16, Material.END_STONE);

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
	private void populateNetherChunk(ChunkData chunkData, Random random, BiomeGrid biomeGrid)
	{
		final int worldHeight = this.addon.getSettings().getMountineHeight();
		final boolean generateFloor = this.addon.getSettings().isBedRockFloor();
		final boolean generateCeiling = this.addon.getSettings().isBedRockRoof();

		chunkData.setRegion(0, 0, 0, 16, worldHeight, 16, Material.NETHERRACK);

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
	private void populateOverWorldChunk(ChunkData chunkData, Random random, BiomeGrid biomeGrid)
	{
		Settings addonSettings = this.addon.getSettings();

		final int worldHeight = addonSettings.getMountineHeight();
		final boolean generateFloor = addonSettings.isBedRockFloor();
		final boolean generateCeiling = addonSettings.isBedRockRoof();

		chunkData.setRegion(0, 0, 0, 16, worldHeight, 16, Material.STONE);

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


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


	/**
	 * CaveBlock addon.
	 */
	private CaveBlock addon;

	/**
	 * Generator that allows to generate custom structures.
	 */
	private PerlinOctaveGenerator generator;
}
