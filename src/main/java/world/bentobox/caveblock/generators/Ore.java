package world.bentobox.caveblock.generators;

import org.bukkit.Material;

/**
 * @author tastybento
 *
 */
public record Ore (int minY, int maxY, Material material, int chance, int blob, boolean cont){

}
