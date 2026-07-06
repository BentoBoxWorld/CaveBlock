package world.bentobox.caveblock.generators;

import java.util.Random;

import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

/**
 * Noise-based 3D cave carver used by the CaveBlock nether and end.
 *
 * <p>The nether and end are filled solid by
 * {@link ChunkGeneratorWorld#generateNoise}. Because that manual fill does not
 * get vanilla's 1.18+ noise-cave system, this class provides an equivalent:
 * connected tunnels and rounded chambers carved out of the solid rock instead
 * of the old populator's random single-block holes.</p>
 *
 * <p>The algorithm combines two 3D Simplex noise fields. Each field is folded
 * with {@link Math#abs} so its zero-crossings become sharp creases; taking the
 * maximum of the two folded fields keeps only the places where <b>both</b> are
 * near zero, which traces long intersecting tunnels rather than isolated blobs.
 * A third, lower-frequency field opens occasional larger "cheese" chambers.</p>
 *
 * <p>The result is deterministic for a given world seed, so a chunk regenerates
 * identically. Inspired by the StrangerRealms cave generator.</p>
 *
 * @author tastybento
 */
public class NoiseCaveGenerator {

    /** Higher frequency traces the winding tunnels. Smaller = larger tunnels. */
    private static final double TUNNEL_FREQUENCY = 0.035;
    /** Lower frequency opens the big rounded chambers. */
    private static final double CHEESE_FREQUENCY = 0.08;
    /** Values above this in the cheese field start carving a chamber. */
    private static final double CHEESE_THRESHOLD = 0.3;
    /** How strongly the cheese field eats into the rock. */
    private static final double CHEESE_STRENGTH = 0.3;

    private final NoiseGenerator noiseGen1;
    private final NoiseGenerator noiseGen2;

    /**
     * Creates a cave generator that is deterministic for the given seed. Two
     * independent noise fields are derived from the one seed so the tunnels are
     * reproducible yet not identical to each other.
     *
     * @param seed the world seed
     */
    public NoiseCaveGenerator(long seed) {
        this.noiseGen1 = new SimplexNoiseGenerator(new Random(seed));
        // Mutate the seed so the second field is different but still deterministic.
        this.noiseGen2 = new SimplexNoiseGenerator(new Random(seed ^ 0x5DEECE66DL));
    }

    /**
     * Computes the cave field value at the given world coordinates.
     *
     * <p>Values close to {@code 0} are cave (empty), larger values are solid
     * rock. Compare the result against a threshold to decide whether a block is
     * carved away.</p>
     *
     * @param x world X
     * @param y world Y
     * @param z world Z
     * @return the cave field value; lower means more likely to be a cave
     */
    public double noise(double x, double y, double z) {
        double xf = x * TUNNEL_FREQUENCY;
        double yf = y * TUNNEL_FREQUENCY;
        double zf = z * TUNNEL_FREQUENCY;

        // Fold both fields so their zero-crossings become sharp tunnel creases,
        // then keep only where both are low (their intersection) => long tunnels.
        double tunnels = Math.max(Math.abs(noiseGen1.noise(xf, yf, zf)),
                Math.abs(noiseGen2.noise(xf, yf, zf)));

        // A lower-frequency field carves occasional large chambers. Y is
        // stretched so chambers are wider than they are tall.
        double cheese = noiseGen1.noise(x * CHEESE_FREQUENCY, y * CHEESE_FREQUENCY / 2.0, z * CHEESE_FREQUENCY);
        double cheeseModifier = (cheese - CHEESE_THRESHOLD) * CHEESE_STRENGTH;
        if (cheeseModifier > 0) {
            tunnels -= cheeseModifier;
        }
        return tunnels;
    }

    /**
     * @param x world X
     * @param y world Y
     * @param z world Z
     * @param threshold cave cut-off; larger values carve wider, more numerous caves
     * @return {@code true} if this block should be an open cave
     */
    public boolean isCave(double x, double y, double z, double threshold) {
        return noise(x, y, z) < threshold;
    }
}
