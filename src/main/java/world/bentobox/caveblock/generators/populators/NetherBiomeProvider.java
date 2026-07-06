package world.bentobox.caveblock.generators.populators;

import java.util.List;
import java.util.Random;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;

/**
 * Distributes the nether biomes into natural regions across the world instead
 * of painting everything with one flat biome.
 *
 * <p>Each candidate biome is given its own low-frequency Simplex noise field.
 * For a given column the biome whose field is highest wins. Because the fields
 * are independent, every biome claims roughly an equal share of the area, and
 * the winning regions form smooth, blobby patches — a natural-looking layout
 * rather than stripes or a random per-block scatter. Several biomes therefore
 * appear within a single island's footprint.</p>
 *
 * <p>The result is deterministic for a given world seed, and the configured
 * {@link Settings#getDefaultNetherBiome() default nether biome} is always
 * included so an admin's choice still shows up.</p>
 *
 * @author tastybento
 */
public class NetherBiomeProvider extends BiomeProvider {

    /** The nether biomes shared out across the world. */
    private static final List<Biome> NETHER_BIOMES = List.of(
            Biome.NETHER_WASTES,
            Biome.CRIMSON_FOREST,
            Biome.WARPED_FOREST,
            Biome.SOUL_SAND_VALLEY,
            Biome.BASALT_DELTAS);

    /** Region scale. Larger = bigger biome patches. ~1/frequency blocks across. */
    private static final double BIOME_FREQUENCY = 0.02;

    private final Settings settings;
    /** One noise field per biome; lazily built once the world seed is known. */
    private NoiseGenerator[] fields;
    private long fieldsSeed;
    private boolean fieldsBuilt;

    /**
     * @param addon CaveBlock addon
     */
    public NetherBiomeProvider(CaveBlock addon) {
        this.settings = addon.getSettings();
    }

    /**
     * Builds (or rebuilds when the seed changes) one independent noise field per
     * biome so region placement is deterministic for the world seed.
     */
    private void ensureFields(long seed) {
        if (fieldsBuilt && fieldsSeed == seed) {
            return;
        }
        NoiseGenerator[] built = new NoiseGenerator[NETHER_BIOMES.size()];
        for (int i = 0; i < built.length; i++) {
            // Offset the seed per biome so each field is different but deterministic.
            built[i] = new SimplexNoiseGenerator(new Random(seed + i * 0x9E3779B97F4A7C15L));
        }
        this.fields = built;
        this.fieldsSeed = seed;
        this.fieldsBuilt = true;
    }

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        ensureFields(worldInfo.getSeed());
        // Biome is chosen per column (ignores y) so a region is the same biome
        // top to bottom, which suits a solid cave world.
        int best = 0;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < fields.length; i++) {
            double value = fields[i].noise(x * BIOME_FREQUENCY, z * BIOME_FREQUENCY);
            if (value > bestValue) {
                bestValue = value;
                best = i;
            }
        }
        return NETHER_BIOMES.get(best);
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        // Include the configured default too, in case it is outside the standard set.
        Biome configured = settings.getDefaultNetherBiome();
        if (configured != null && !NETHER_BIOMES.contains(configured)) {
            return List.of(configured, Biome.NETHER_WASTES, Biome.CRIMSON_FOREST,
                    Biome.WARPED_FOREST, Biome.SOUL_SAND_VALLEY, Biome.BASALT_DELTAS);
        }
        return NETHER_BIOMES;
    }
}
