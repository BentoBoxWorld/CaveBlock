package world.bentobox.caveblock.generators;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;
import world.bentobox.caveblock.generators.populators.FlatBiomeProvider;
import world.bentobox.caveblock.generators.populators.NewMaterialPopulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * CaveBlock chunk generator.
 *
 * <p>For the overworld, this generator delegates entirely to vanilla noise generation
 * (which includes the full 1.18+ cave system: cheese caves, spaghetti caves, lush caves,
 * dripstone caves, deep dark, aquifer water pockets, etc.). After vanilla noise runs,
 * {@link #generateSurface} caps the sky with stone so there is no open surface — the
 * world is solid rock from bedrock to the configured roof, carved through by vanilla caves.
 * Vanilla decorations (ores, dripstone, moss, glow-berries) and structures (trial
 * chambers, mineshafts, dungeons, ancient cities, amethyst geodes) are enabled
 * automatically. Underground biomes are placed naturally by the vanilla biome provider.</p>
 *
 * <p>For the nether and end, the existing fill-and-decorate approach is kept, using
 * {@link NewMaterialPopulator} for ore placement and {@link FlatBiomeProvider} for biomes.</p>
 *
 * @author BONNe, tastybento
 */
public class ChunkGeneratorWorld extends ChunkGenerator {

    // -------------------------------------------------------------------------
    // Section: Variables
    // -------------------------------------------------------------------------

    private final CaveBlock addon;
    private final Settings settings;
    private final World.Environment environment;
    private final List<BlockPopulator> blockPopulators;

    // -------------------------------------------------------------------------
    // Section: Constructor
    // -------------------------------------------------------------------------

    /**
     * @param addon       CaveBlock addon
     * @param environment World environment (NORMAL, NETHER, THE_END)
     */
    public ChunkGeneratorWorld(CaveBlock addon, World.Environment environment) {
        this.addon = addon;
        this.settings = addon.getSettings();
        this.environment = environment;
        this.blockPopulators = new ArrayList<>();
        reload();
    }

    // -------------------------------------------------------------------------
    // Section: Reload
    // -------------------------------------------------------------------------

    /**
     * Called when config is reloaded. Rebuilds block populators.
     */
    public void reload() {
        this.blockPopulators.clear();
        // Overworld uses vanilla decorations (shouldGenerateDecorations = true).
        // Nether and End use NewMaterialPopulator for ore/block placement.
        if (this.environment != World.Environment.NORMAL) {
            this.blockPopulators.add(new NewMaterialPopulator(this.settings.getWorldDepth()));
        }
    }

    // -------------------------------------------------------------------------
    // Section: Delegation flags — what vanilla passes should run
    // -------------------------------------------------------------------------

    /**
     * Let vanilla noise run for the overworld. This gives us the full 1.18+ cave
     * system (noise caves, aquifers, underground biome transitions). For nether/end
     * we fill manually via {@link #generateNoise}.
     */
    @Override
    public boolean shouldGenerateNoise() {
        return this.environment == World.Environment.NORMAL;
    }

    /**
     * Never use vanilla surface generation. We handle it ourselves in
     * {@link #generateSurface} to remove the sky and replace surface soil.
     */
    @Override
    public boolean shouldGenerateSurface() {
        return false;
    }

    /**
     * Never use vanilla bedrock generation. We place floor/roof bedrock ourselves
     * via {@link #generateBedrock}.
     */
    @Override
    public boolean shouldGenerateBedrock() {
        return false;
    }

    /**
     * Enable vanilla carver caves for the overworld (ravines, round carver tunnels).
     * These stack on top of the noise caves already provided by {@link #shouldGenerateNoise}.
     * Disabled for nether/end (they use the NewMaterialPopulator).
     */
    @Override
    public boolean shouldGenerateCaves() {
        return this.environment == World.Environment.NORMAL;
    }

    /**
     * Enable vanilla decorations for the overworld: ores (coal, iron, diamond, …),
     * cave decorations (dripstone, pointed dripstone, moss, glow-berries, spore
     * blossoms, sculk blocks, …), and amethyst geodes.
     * Nether/End use {@link NewMaterialPopulator} instead.
     */
    @Override
    public boolean shouldGenerateDecorations() {
        return this.environment == World.Environment.NORMAL;
    }

    /**
     * Enable vanilla structures for the overworld: trial chambers, mineshafts,
     * dungeons, strongholds, fossils, ancient cities, ruined portals, etc.
     * Disabled for nether/end.
     */
    @Override
    public boolean shouldGenerateStructures() {
        return this.environment == World.Environment.NORMAL;
    }

    /**
     * Do not spawn mobs during chunk generation. Natural mob spawning handles
     * population of the cave world after generation.
     */
    @Override
    public boolean shouldGenerateMobs() {
        return false;
    }

    // -------------------------------------------------------------------------
    // Section: Generation methods
    // -------------------------------------------------------------------------

    /**
     * Fills nether and end chunks with their base material. Not called for the
     * overworld — vanilla noise handles it when {@link #shouldGenerateNoise} is true.
     */
    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        // Only called for NETHER and THE_END; overworld is handled by vanilla.
        final int minHeight = worldInfo.getMinHeight();
        final int worldHeight = Math.min(worldInfo.getMaxHeight(), this.settings.getWorldDepth());

        switch (worldInfo.getEnvironment()) {
            case NETHER -> {
                // Soul sand layer at the bottom, netherrack above
                if (worldHeight + 1 > 34) {
                    chunkData.setRegion(0, minHeight + 1, 0, 16, 34, 16, Material.SOUL_SAND);
                    chunkData.setRegion(0, 34, 0, 16, worldHeight - 1, 16, Material.NETHERRACK);
                } else {
                    chunkData.setRegion(0, minHeight + 1, 0, 16, worldHeight - 1, 16, Material.NETHERRACK);
                }
            }
            case THE_END -> chunkData.setRegion(0, minHeight + 1, 0, 16, worldHeight - 1, 16, Material.END_STONE);
            default -> {
                // Fallback for normal world (should not reach here when shouldGenerateNoise() = true)
                chunkData.setRegion(0, minHeight + 1, 0, 16, worldHeight - 1, 16, Material.STONE);
            }
        }
    }

    /**
     * Post-processes the terrain after noise generation.
     *
     * <p>For the <b>overworld</b>, vanilla noise has already carved the terrain including
     * all cave types. This method scans each block column from the top downwards,
     * replacing sky air ({@code AIR}) and surface water ({@code WATER}) with stone
     * until the first underground block is encountered ({@code STONE}, {@code DEEPSLATE},
     * {@code CAVE_AIR}, {@code LAVA}, etc.). This removes the open sky while preserving
     * every underground cave, tunnel, and water pocket exactly as vanilla generated them.
     * Finally, a bedrock or stone roof slab is placed at the top if configured.</p>
     *
     * <p>For <b>nether/end</b>, only the configurable roof layer is placed.</p>
     */
    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        final int minHeight = worldInfo.getMinHeight();
        final int maxHeight = Math.min(worldInfo.getMaxHeight(), this.settings.getWorldDepth());
        final World.Environment env = worldInfo.getEnvironment();

        if (env == World.Environment.NORMAL) {
            // Cap every column: replace sky air and surface water with stone.
            // CAVE_AIR (vanilla cave pockets) marks the underground boundary and is left alone.
            final Material fillMaterial = settings.getNormalMainBlock();
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    capColumn(chunkData, x, z, minHeight, maxHeight, fillMaterial);
                }
            }
            // Roof at the very top of the world
            Material roofMaterial = settings.isNormalRoof() ? Material.BEDROCK : settings.getNormalMainBlock();
            chunkData.setRegion(0, maxHeight - 1, 0, 16, maxHeight, 16, roofMaterial);
        } else {
            // Nether / End: just place the roof at worldDepth - 1
            int worldHeight = Math.min(maxHeight, settings.getWorldDepth());
            Material roofMaterial = getGroundRoofMaterial(env);
            chunkData.setRegion(0, worldHeight - 1, 0, 16, worldHeight, 16, roofMaterial);
        }
    }

    /**
     * Places floor bedrock (or configured material) at the bottom of the world
     * for all environments.
     */
    @Override
    public void generateBedrock(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        final int minHeight = worldInfo.getMinHeight();
        Material floorMaterial = getGroundFloorMaterial(worldInfo.getEnvironment());
        chunkData.setRegion(0, minHeight, 0, 16, minHeight + 1, 16, floorMaterial);
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return this.blockPopulators;
    }

    /**
     * Returns the biome provider.
     * <ul>
     *   <li>Overworld: {@code null} → Minecraft's own biome engine runs, naturally
     *       placing underground biomes (LUSH_CAVES, DRIPSTONE_CAVES, DEEP_DARK, …)
     *       at the appropriate depths.</li>
     *   <li>Nether / End: {@link FlatBiomeProvider} for a single configurable biome.</li>
     * </ul>
     */
    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
        if (worldInfo.getEnvironment() == World.Environment.NORMAL) {
            return null; // vanilla biome placement — underground biomes form naturally
        }
        return new FlatBiomeProvider(this.addon);
    }

    // -------------------------------------------------------------------------
    // Section: Helpers
    // -------------------------------------------------------------------------

    /**
     * Caps a single block column by replacing sky air and surface water with stone.
     *
     * <p>Scanning top-down, every {@code AIR} block (sky) and {@code WATER} block
     * (open ocean/river surface) is overwritten with {@code fillMaterial}. The scan
     * stops as soon as a block that is clearly underground is encountered:
     * {@code CAVE_AIR} (a vanilla-carved cave pocket), any solid terrain block
     * (STONE, DEEPSLATE, LAVA pools, …), or bedrock.</p>
     *
     * @param chunkData    chunk being generated
     * @param x            block X within chunk (0-15)
     * @param z            block Z within chunk (0-15)
     * @param minHeight    world min height
     * @param maxHeight    world max height (roof placed separately; scan stops at maxHeight-2)
     * @param fillMaterial material used to fill sky gaps (normally STONE)
     */
    private void capColumn(ChunkData chunkData, int x, int z, int minHeight, int maxHeight, Material fillMaterial) {
        // maxHeight - 1 is reserved for the roof layer set in generateSurface().
        for (int y = maxHeight - 2; y >= minHeight; y--) {
            Material type = chunkData.getType(x, y, z);

            if (type == Material.AIR) {
                // Sky air above the terrain — fill solid.
                chunkData.setBlock(x, y, z, fillMaterial);
            } else if (type == Material.WATER) {
                // Open surface water (ocean, river) — seal it with stone.
                // Underground water in aquifer caves is enclosed by stone and
                // will never appear as the first non-air block from the top.
                chunkData.setBlock(x, y, z, fillMaterial);
            } else {
                // CAVE_AIR, STONE, DEEPSLATE, LAVA, BEDROCK — we are underground.
                // Everything below is part of the genuine cave system; leave it alone.
                break;
            }
        }
    }

    private Material getGroundRoofMaterial(World.Environment env) {
        return switch (env) {
            case NETHER  -> settings.isNetherRoof() ? Material.BEDROCK : settings.getNetherMainBlock();
            case THE_END -> settings.isEndRoof()    ? Material.BEDROCK : settings.getEndMainBlock();
            default      -> settings.isNormalRoof() ? Material.BEDROCK : settings.getNormalMainBlock();
        };
    }

    private Material getGroundFloorMaterial(World.Environment env) {
        return switch (env) {
            case NETHER  -> settings.isNetherFloor() ? Material.BEDROCK : settings.getNetherMainBlock();
            case THE_END -> settings.isEndFloor()    ? Material.BEDROCK : settings.getEndMainBlock();
            default      -> settings.isNormalFloor() ? Material.BEDROCK : settings.getNormalMainBlock();
        };
    }
}
