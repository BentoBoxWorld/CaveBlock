package world.bentobox.caveblock.generators;

import org.bukkit.Material;

/**
 * @param minY     minimum Y level this ore should appear
 * @param minY     maximum Y level this ore should appear
 * @param material Material
 * @param chance   chance
 * @param blob     maximum size of blob to generate
 * @param cont     whether the generator should continue to try to make other ores at this level after making this one
 * @author tastybento
 */
public record Ore(int minY, int maxY, Material material, int chance, int blob, boolean cont) {

}
