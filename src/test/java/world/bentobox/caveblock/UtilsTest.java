package world.bentobox.caveblock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Utils}.
 */
class UtilsTest {

    /**
     * Test method for {@link Utils#getLocationFromChunkLocation(int, int, int, int, int)}.
     * Verifies that chunk-relative coordinates are correctly converted to world coordinates.
     */
    @Test
    void testGetLocationFromChunkLocationOrigin() {
        Location loc = Utils.getLocationFromChunkLocation(0, 64, 0, 0, 0);
        assertNull(loc.getWorld(), "World should be null when created without a world reference");
        assertEquals(0.0, loc.getX(), 0.001);
        assertEquals(64.0, loc.getY(), 0.001);
        assertEquals(0.0, loc.getZ(), 0.001);
    }

    /**
     * Verifies the chunk-to-world coordinate conversion formula (x + chunkX*16, y, z + chunkZ*16).
     */
    @Test
    void testGetLocationFromChunkLocationOffset() {
        Location loc = Utils.getLocationFromChunkLocation(5, 80, 3, 2, 4);
        assertEquals(5 + 2 * 16.0, loc.getX(), 0.001);
        assertEquals(80.0, loc.getY(), 0.001);
        assertEquals(3 + 4 * 16.0, loc.getZ(), 0.001);
    }

    /**
     * Verifies negative chunk coordinates are handled correctly.
     */
    @Test
    void testGetLocationFromChunkLocationNegativeChunk() {
        Location loc = Utils.getLocationFromChunkLocation(0, 0, 0, -1, -1);
        assertEquals(-16.0, loc.getX(), 0.001);
        assertEquals(0.0, loc.getY(), 0.001);
        assertEquals(-16.0, loc.getZ(), 0.001);
    }
}
