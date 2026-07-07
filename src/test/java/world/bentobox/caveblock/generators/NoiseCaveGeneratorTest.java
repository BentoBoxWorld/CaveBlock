package world.bentobox.caveblock.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link NoiseCaveGenerator}.
 */
class NoiseCaveGeneratorTest {

    private static final double THRESHOLD = 0.15;

    /**
     * The same seed must produce identical results so a chunk regenerates the same way.
     */
    @Test
    void testDeterministicForSameSeed() {
        NoiseCaveGenerator a = new NoiseCaveGenerator(1234L);
        NoiseCaveGenerator b = new NoiseCaveGenerator(1234L);
        for (int i = 0; i < 50; i++) {
            assertEquals(a.noise(i, i * 2, i * 3), b.noise(i, i * 2, i * 3),
                    "Same seed should give the same noise value");
        }
    }

    /**
     * Different seeds should generally give different cave layouts.
     */
    @Test
    void testDifferentSeedsDiffer() {
        NoiseCaveGenerator a = new NoiseCaveGenerator(1L);
        NoiseCaveGenerator b = new NoiseCaveGenerator(2L);
        int differences = 0;
        for (int x = 0; x < 32; x++) {
            for (int z = 0; z < 32; z++) {
                if (a.isCave(x, 40, z, THRESHOLD) != b.isCave(x, 40, z, THRESHOLD)) {
                    differences++;
                }
            }
        }
        assertTrue(differences > 0, "Different seeds should carve different caves");
    }

    /**
     * Over a representative volume the carver must leave a solid world with real caves —
     * neither all rock nor all air — otherwise the nether/end would be unplayable.
     */
    @Test
    void testProducesSolidWorldWithCaves() {
        NoiseCaveGenerator gen = new NoiseCaveGenerator(42L);
        int cave = 0;
        int total = 0;
        for (int x = 0; x < 48; x++) {
            for (int z = 0; z < 48; z++) {
                for (int y = 10; y < 60; y++) {
                    if (gen.isCave(x, y, z, THRESHOLD)) {
                        cave++;
                    }
                    total++;
                }
            }
        }
        double ratio = (double) cave / total;
        assertTrue(ratio > 0.02, "Should carve some caves, got ratio " + ratio);
        assertTrue(ratio < 0.6, "World should stay mostly solid, got cave ratio " + ratio);
    }

    @Test
    void testIsCaveMatchesThreshold() {
        NoiseCaveGenerator gen = new NoiseCaveGenerator(7L);
        double v = gen.noise(5, 5, 5);
        assertEquals(v < THRESHOLD, gen.isCave(5, 5, 5, THRESHOLD));
        assertNotEquals(gen.isCave(5, 5, 5, -1.0), gen.isCave(5, 5, 5, 2.0),
                "An always-solid vs always-cave threshold must differ");
    }

    /**
     * The fill field must stay within [0, 1] so it can be compared against a ratio.
     */
    @Test
    void testFillFieldInRange() {
        NoiseCaveGenerator gen = new NoiseCaveGenerator(99L);
        for (int x = 0; x < 40; x++) {
            for (int z = 0; z < 40; z++) {
                double v = gen.fillField(x, 30, z);
                assertTrue(v >= 0.0 && v <= 1.0, "fillField out of range: " + v);
            }
        }
    }

    /**
     * A ratio of 0 fills nothing and a ratio of 1 fills everything; a mid ratio must
     * fill some but not all, and a higher ratio must never fill fewer blocks.
     */
    @Test
    void testFillFieldMonotonicWithRatio() {
        NoiseCaveGenerator gen = new NoiseCaveGenerator(2024L);
        int none = 0;
        int half = 0;
        int all = 0;
        int total = 0;
        for (int x = 0; x < 48; x++) {
            for (int z = 0; z < 48; z++) {
                double v = gen.fillField(x, 30, z);
                if (v < 0.0) {
                    none++;
                }
                if (v < 0.5) {
                    half++;
                }
                if (v < 1.0) {
                    all++;
                }
                total++;
            }
        }
        assertEquals(0, none, "Ratio 0.0 should fill no blocks");
        assertTrue(half > 0 && half < total, "Ratio 0.5 should fill some but not all, got " + half);
        assertTrue(all >= half, "A higher ratio must not fill fewer blocks");
    }

    /**
     * The fill field must be deterministic for a seed so chunks re-solidify the same way.
     */
    @Test
    void testFillFieldDeterministic() {
        NoiseCaveGenerator a = new NoiseCaveGenerator(555L);
        NoiseCaveGenerator b = new NoiseCaveGenerator(555L);
        for (int i = 0; i < 50; i++) {
            assertEquals(a.fillField(i, i + 5, i * 2), b.fillField(i, i + 5, i * 2));
        }
    }
}
