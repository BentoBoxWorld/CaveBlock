
package world.bentobox.caveblock.generators.populators;


import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import world.bentobox.bentobox.util.Pair;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;


/**
 * This class allows to fill given chunk with necessary blocks.
 */
public class MaterialPopulator extends BlockPopulator
{
	/**
	 * This is default constructor
	 * @param addon CaveBlock addon.
	 */
	public MaterialPopulator(CaveBlock addon)
	{
		this.addon = addon;
		this.settings = addon.getSettings();
	}


	/**
	 * This method populates chunk with blocks.
	 * @param world World where population must be.
	 * @param random Randomness
	 * @param chunk Chunk were populator operates.
	 */
	@Override
	public void populate(World world, Random random, Chunk chunk)
	{
		Map<Material, Pair<Integer, Integer>> materialChanceMap;
		Material mainMaterial;

		if (world.getEnvironment().equals(World.Environment.NETHER))
		{
			materialChanceMap = this.getMaterialMap(this.settings.getNetherBlocks());
			mainMaterial = this.settings.getNetherMainBlock();
		}
		else if (world.getEnvironment().equals(World.Environment.THE_END))
		{
			materialChanceMap = this.getMaterialMap(this.settings.getEndBlocks());
			mainMaterial = this.settings.getEndMainBlock();
		}
		else
		{
			materialChanceMap = this.getMaterialMap(this.settings.getNormalBlocks());
			mainMaterial = this.settings.getNormalMainBlock();
		}

		final int generationTry = this.settings.getNumberOfBlockGenerationTries();
		final int worldHeight = this.settings.getWorldDepth() - 1;

		for (Map.Entry<Material, Pair<Integer, Integer>> entry : materialChanceMap.entrySet())
		{
			for (int subY = 0; subY < worldHeight; subY += 16)
			{
				for (int tries = 0; tries < generationTry; tries++)
				{
					if (random.nextInt(100) < entry.getValue().x)
					{
						int x = random.nextInt(15);
						int z =  random.nextInt(15);
						int y = Math.min(worldHeight - 2, subY + random.nextInt(15));

						Block block = chunk.getBlock(x, y, z);

						if (block.getType().equals(mainMaterial) &&
							this.isValidBlock(world, block, x, z))
						{
							int packSize = random.nextInt(entry.getValue().z);

							boolean continuePlacing = true;

							while (continuePlacing)
							{
								if (!block.getType().equals(entry.getKey()))
								{
									block.setType(entry.getKey());
									packSize--;
								}

								// The direction chooser
								switch (random.nextInt(5))
								{
									case 0:
										x = Math.min(15, x + 1);
										break;
									case 1:
										y = Math.min(worldHeight - 2, y + 1);
										break;
									case 2:
										z = Math.min(15, z + 1);
										break;
									case 3:
										x = Math.max(0, x - 1);
										break;
									case 4:
										y = Math.max(1, y - 1);
										break;
									case 5:
										z = Math.max(0, z - 1);
										break;
								}

								block = chunk.getBlock(x, y, z);

								continuePlacing = this.isValidBlock(world, block, x, z) &&
									packSize > 0 &&
									(block.getType().equals(mainMaterial) ||
									block.getType().equals(entry.getKey()));
							}
						}
					}
				}
			}
		}
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
				Material material = Material.getMaterial(splitString[1]);

				if (material != null)
				{
					materialMap.put(material,
						new Pair<>(Integer.parseInt(splitString[2]), Integer.parseInt(splitString[3])));
				}
			});

		return materialMap;
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
