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


	@Override
	public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, ChunkGenerator.BiomeGrid biomeGrid)
	{
		ChunkData result;

		if (world.getEnvironment().equals(World.Environment.NETHER))
		{
			result = this.generateNetherChunks(world, random, chunkX, chunkZ, biomeGrid);
		}
		else
		{
			result = this.createChunkData(world);

			for (int x = 0; x < 16; x++)
			{
				for (int z = 0; z < 16; z++)
				{
					result.setRegion(0, 0, 0, 16, 200, 16, Material.STONE);

					if (world.getEnvironment().equals(World.Environment.NORMAL))
					{
						biomeGrid.setBiome(x, z, this.addon.getSettings().getDefaultBiome());
					}

					if (this.addon.getSettings().isBedRockFloor())
					{
						result.setBlock(x, 0, z, Material.BEDROCK);
					}

					if (this.addon.getSettings().isBedRockRoof())
					{
						result.setBlock(x,  150, z, Material.BEDROCK);
					}
				}
			}

			System.out.println("Chunk at " + chunkX + " " + chunkZ + " is generated");
		}

		return result;
	}


	@Override
	public boolean canSpawn(World world, int x, int z) {
		return true;
	}


	@Override
	public List<BlockPopulator> getDefaultPopulators(final World world) {
		return Arrays.asList(new BlockPopulator[0]);
	}


	/*
	 * Nether Section
	 */
	private ChunkData generateNetherChunks(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid)
	{
		ChunkData result = this.createChunkData(world);
		this.rand.setSeed(world.getSeed());
		this.gen = new PerlinOctaveGenerator((long) (random.nextLong() * random.nextGaussian()), 8);

		// This is a nether generator
		if (!world.getEnvironment().equals(World.Environment.NETHER))
		{
			return result;
		}

		if (this.addon.getSettings().isNetherRoof())
		{
			// Make the roof - common across the world
			for (int x = 0; x < 16; x++)
			{
				for (int z = 0; z < 16; z++)
				{
					// Do the ceiling
					int maxHeight = world.getMaxHeight();
					result.setBlock(x, (maxHeight - 1), z, Material.BEDROCK);
					// Next three layers are a mix of bedrock and netherrack
					for (int y = 2; y < 5; y++)
					{
						double r = this.gen.noise(x, (maxHeight - y), z, 0.5, 0.5);
						if (r > 0D)
						{
							result.setBlock(x, (maxHeight - y), z, Material.BEDROCK);
						}
					}
					// Next three layers are a mix of netherrack and air
					for (int y = 5; y < 8; y++)
					{
						double r = this.gen.noise(x, (double) maxHeight - y, z, 0.5, 0.5);

						if (r > 0D)
						{
							result.setBlock(x, (maxHeight - y), z, Material.NETHERRACK);
						}
						else
						{
							result.setBlock(x, (maxHeight - y), z, Material.AIR);
						}
					}

					// Layer 8 may be glowstone
					double r = this.gen.noise(x, (double) maxHeight - 8, z, random.nextFloat(), random.nextFloat());

					if (r > 0.5D)
					{
						// Have blobs of glowstone
						switch (random.nextInt(4))
						{
							case 1:
								// Single block
								result.setBlock(x, (maxHeight - 8), z, Material.GLOWSTONE);
								if (x < 14 && z < 14)
								{
									result.setBlock(x + 1, (maxHeight - 8), z + 1, Material.GLOWSTONE);
									result.setBlock(x + 2, (maxHeight - 8), z + 2, Material.GLOWSTONE);
									result.setBlock(x + 1, (maxHeight - 8), z + 2, Material.GLOWSTONE);
									result.setBlock(x + 1, (maxHeight - 8), z + 2, Material.GLOWSTONE);
								}
								break;
							case 2:
								// Stalatite
								for (int i = 0; i < random.nextInt(10); i++)
								{
									result.setBlock(x, (maxHeight - 8 - i), z, Material.GLOWSTONE);
								}
								break;
							case 3:
								result.setBlock(x, (maxHeight - 8), z, Material.GLOWSTONE);
								if (x > 3 && z > 3)
								{
									for (int xx = 0; xx < 3; xx++)
									{
										for (int zz = 0; zz < 3; zz++)
										{
											result.setBlock(x - xx,
												(maxHeight - 8 - random.nextInt(2)),
												z - xx,
												Material.GLOWSTONE);
										}
									}
								}
								break;
							default:
								result.setBlock(x, (maxHeight - 8), z, Material.GLOWSTONE);
								break;
						}

						result.setBlock(x, (maxHeight - 8), z, Material.GLOWSTONE);
					}
					else
					{
						result.setBlock(x, (maxHeight - 8), z, Material.AIR);
					}
				}
			}
		}

		return result;
	}


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


	private CaveBlock addon;

	/** Field rand  */
	private Random rand = new Random();

	private PerlinOctaveGenerator gen;
}
