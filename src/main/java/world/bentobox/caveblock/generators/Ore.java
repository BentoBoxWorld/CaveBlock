package world.bentobox.caveblock.generators;

import org.bukkit.Material;

public record Ore (int minY, int maxY, Material material, int chance){

}
