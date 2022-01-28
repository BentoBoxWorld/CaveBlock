package world.bentobox.caveblock;

import org.bukkit.Location;

public class Utils {

    /**
     * Convert chunk location to world location
     *
     * @param x      the x coordinate of the chunk location
     * @param y      the y coordinate
     * @param z      the z coordinate of the chunk location
     * @param chunkX the x coordinate of the chunk
     * @param chunkZ the z coordinate of the chunk
     * @return the world location
     */
    public static Location getLocationFromChunkLocation(int x, int y, int z, int chunkX, int chunkZ) {
        return new Location(null, x + (chunkX * 16D), y, z + (chunkZ * 16D));
    }
}
