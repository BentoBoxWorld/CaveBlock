package world.bentobox.caveblock.generators;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;
import world.bentobox.caveblock.generators.populators.EntitiesPopulator;
import world.bentobox.caveblock.generators.populators.FlatBiomeProvider;
import world.bentobox.caveblock.generators.populators.MaterialPopulator;
import world.bentobox.caveblock.generators.populators.NewMaterialPopulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class ChunkGeneratorWorld ...
 *
 * @author BONNe
 * Created on 27.01.2019
 */
public class ChunkGeneratorWorld extends ChunkGenerator {
    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------

    private final CaveBlock addon;
    private final Settings settings;
    private final List<BlockPopulator> blockPopulators;
    private BiomeProvider biomeProvider;

    // ---------------------------------------------------------------------
    // Section: Constructor
    // ---------------------------------------------------------------------

    /**
     * @param addon - CaveBlock object
     */
    public ChunkGeneratorWorld(CaveBlock addon) {
        this.addon = addon;
        this.settings = addon.getSettings();
        this.blockPopulators = new ArrayList<>(2);

        reload();
    }

    // ---------------------------------------------------------------------
    // Section: Methods
    // ---------------------------------------------------------------------

    private Material getGroundCeilMaterial(World.Environment environment) {
        return switch (environment) {
            case NETHER -> this.settings.isNetherRoof() ? Material.BEDROCK : this.settings.getNetherMainBlock();
            case THE_END -> this.settings.isEndFloor() ? Material.BEDROCK : this.settings.getEndMainBlock();
            default -> this.settings.isNormalFloor() ? Material.BEDROCK : this.settings.getNormalMainBlock();
        };
    }

    private Material getBaseMaterial(World.Environment environment) {
        return switch (environment) {
            case NETHER -> this.settings.getNetherMainBlock();
            case THE_END -> this.settings.getEndMainBlock();
            default -> this.settings.getNormalMainBlock();
        };
    }

    @Override
    public void generateBedrock(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        final int minHeight = worldInfo.getMinHeight();
        Material material = getGroundCeilMaterial(worldInfo.getEnvironment());
        chunkData.setRegion(0, minHeight, 0, 16, minHeight + 1, 16, material);
    }

    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        final int worldHeight = Math.min(worldInfo.getMaxHeight(), this.settings.getWorldDepth());
        Material material = getGroundCeilMaterial(worldInfo.getEnvironment());
        chunkData.setRegion(0, worldHeight - 1, 0, 16, worldHeight, 16, material);
    }

    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        final int minHeight = worldInfo.getMinHeight();
        final int worldHeight = Math.min(worldInfo.getMaxHeight(), this.settings.getWorldDepth());
        Material material = getBaseMaterial(worldInfo.getEnvironment());
        chunkData.setRegion(0, minHeight + 1, 0, 16, worldHeight - 1, 16, material);
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(final World world) {
        return this.blockPopulators;
    }

    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
        return biomeProvider;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return true;
    }

    @Override
    public boolean shouldGenerateBedrock() {
        return true;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return this.settings.isNewMaterialGenerator();
    }

    /**
     * Called when config is reloaded
     */
    public void reload() {
        this.blockPopulators.clear();

        if (this.settings.isNewMaterialGenerator()) {
            this.blockPopulators.add(new NewMaterialPopulator());
        } else {
            this.blockPopulators.add(new MaterialPopulator(this.addon));
        }
        this.blockPopulators.add(new EntitiesPopulator(this.addon));

        this.biomeProvider = new FlatBiomeProvider(this.addon);
    }
}
