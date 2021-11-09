package world.bentobox.caveblock.generators.populators;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;

import java.util.Collections;
import java.util.List;

public class FlatBiomeProvider extends BiomeProvider {
    private final Settings settings;

    // ---------------------------------------------------------------------
    // Section: Constructor
    // ---------------------------------------------------------------------

    /**
     * @param addon - CaveBlock object
     */
    public FlatBiomeProvider(CaveBlock addon) {
        this.settings = addon.getSettings();
    }

    // ---------------------------------------------------------------------
    // Section: Methods
    // ---------------------------------------------------------------------

    private Biome getBiome(World.Environment environment) {
        return switch (environment) {
            case NETHER -> this.settings.getDefaultNetherBiome();
            case THE_END -> this.settings.getDefaultTheEndBiome();
            default -> this.settings.getDefaultBiome();
        };
    }

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        return getBiome(worldInfo.getEnvironment());
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return Collections.singletonList(getBiome(worldInfo.getEnvironment()));
    }
}
