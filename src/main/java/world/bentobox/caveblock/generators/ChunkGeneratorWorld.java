package world.bentobox.caveblock.generators;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;
import world.bentobox.caveblock.generators.populators.EntitiesPopulator;
import world.bentobox.caveblock.generators.populators.MaterialPopulator;


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

        reload();
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

        // Populate chunk with necessary information
        if (world.getEnvironment().equals(World.Environment.NETHER))
        {
            this.populateNetherChunk(world, result, biomeGrid);
        }
        else if (world.getEnvironment().equals(World.Environment.THE_END))
        {
            this.populateTheEndChunk(world, result, biomeGrid);
        }
        else
        {
            this.populateOverWorldChunk(world, result, biomeGrid);
        }

        return result;
    }


    /**
     * This method populates The End world chunk data.
     * @param world world where chunks are generated.
     * @param chunkData ChunkData that must be populated.
     * @param biomeGrid BiomeGrid for this chunk.
     */
    private void populateTheEndChunk(World world, ChunkData chunkData, BiomeGrid biomeGrid)
    {
        // because everything starts at 0 and ends at 255
        final int worldHeight = this.settings.getWorldDepth();

        // Fill all blocks
        chunkData.setRegion(0, 1, 0,
                16, worldHeight - 1, 16,
                this.settings.getEndMainBlock());

        // Generate ground and ceiling.
        chunkData.setRegion(0, 0, 0,
                16, 1, 16,
                this.settings.isEndFloor() ? Material.BEDROCK : this.settings.getEndMainBlock());
        chunkData.setRegion(0, worldHeight - 1, 0,
                16, worldHeight, 16,
                this.settings.isEndRoof() ? Material.BEDROCK : this.settings.getEndMainBlock());

        // Set biome
        for (int x = 0; x < 16; x += 4)
        {
            for (int y = 0; y < world.getMaxHeight(); y += 4)
            {
                for (int z = 0; z < 16; z += 4)
                {
                    biomeGrid.setBiome(x, y, z, this.settings.getDefaultTheEndBiome());
                }
            }
        }
    }


    /**
     * This method populates nether world chunk data.
     * @param world world where chunks are generated.
     * @param chunkData ChunkData that must be populated.
     * @param biomeGrid BiomeGrid for this chunk.
     */
    private void populateNetherChunk(World world, ChunkData chunkData, BiomeGrid biomeGrid)
    {
        // because everything starts at 0 and ends at 255
        final int worldHeight = this.settings.getWorldDepth();

        // Fill all blocks
        chunkData.setRegion(0, 1, 0,
                16, worldHeight - 1, 16,
                this.settings.getNetherMainBlock());

        // Generate ground and ceiling.
        chunkData.setRegion(0, 0, 0,
                16, 1, 16,
                this.settings.isNetherFloor() ? Material.BEDROCK : this.settings.getNetherMainBlock());
        chunkData.setRegion(0, worldHeight - 1, 0,
                16, worldHeight, 16,
                this.settings.isNetherRoof() ? Material.BEDROCK : this.settings.getNetherMainBlock());

        // Set biome
        for (int x = 0; x < 16; x += 4)
        {
            for (int y = 0; y < world.getMaxHeight(); y += 4)
            {
                for (int z = 0; z < 16; z += 4)
                {
                    biomeGrid.setBiome(x, y, z, this.settings.getDefaultNetherBiome());
                }
            }
        }
    }


    /**
     * This method populates Over world chunk data.
     * @param world world where chunks are generated.
     * @param chunkData ChunkData that must be populated.
     * @param biomeGrid BiomeGrid for this chunk.
     */
    private void populateOverWorldChunk(World world, ChunkData chunkData, BiomeGrid biomeGrid)
    {
        // because everything starts at 0 and ends at 255
        final int worldHeight = this.settings.getWorldDepth();

        // Fill all blocks
        chunkData.setRegion(0, 1, 0,
                16, worldHeight - 1, 16,
                this.settings.getNormalMainBlock());

        // Generate ground and ceiling.
        chunkData.setRegion(0, 0, 0,
                16, 1, 16,
                this.settings.isNormalFloor() ? Material.BEDROCK : this.settings.getNormalMainBlock());
        chunkData.setRegion(0, worldHeight - 1, 0,
                16, worldHeight, 16,
                this.settings.isNormalRoof() ? Material.BEDROCK : this.settings.getNormalMainBlock());

        // Set biome
        for (int x = 0; x < 16; x += 4)
        {
            for (int y = 0; y < world.getMaxHeight(); y += 4)
            {
                for (int z = 0; z < 16; z += 4)
                {
                    biomeGrid.setBiome(x, y, z, this.settings.getDefaultBiome());
                }
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
        return this.blockPopulators;
    }

    /**
     * Called when config is reloaded
     */
    public void reload() {
        this.blockPopulators = new ArrayList<>(2);

        this.blockPopulators.add(new MaterialPopulator(this.addon));
        this.blockPopulators.add(new EntitiesPopulator(this.addon));

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
     * This list contains block populators that will be applied after chunk is generated.
     */
    private List<BlockPopulator> blockPopulators;
}
