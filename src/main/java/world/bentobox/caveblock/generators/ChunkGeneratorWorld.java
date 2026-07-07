package world.bentobox.caveblock.generators;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;
import world.bentobox.caveblock.generators.populators.CaveDecorationPopulator;
import world.bentobox.caveblock.generators.populators.FlatBiomeProvider;
import world.bentobox.caveblock.generators.populators.NetherBiomeProvider;
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

    /** Blocks of solid rock kept above the floor before caves may start. */
    private static final int CAVE_FLOOR_MARGIN = 5;
    /** Blocks of solid rock kept below the roof so caves never breach it. */
    private static final int CAVE_ROOF_MARGIN = 6;
    /** Height above the cave floor that nether cave voids fill with lava. */
    private static final int NETHER_LAVA_DEPTH = 4;
    /** Cave field cut-off: larger carves wider, more connected caves. */
    private static final double CAVE_THRESHOLD = 0.15;

    private final CaveBlock addon;
    private Settings settings;
    private final World.Environment environment;
    private final List<BlockPopulator> blockPopulators;
    /** Lazily built, cached per world seed so chunks regenerate identically. */
    private NoiseCaveGenerator caveGenerator;
    private long caveGeneratorSeed;

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
     * Called when config is reloaded. Re-reads the live settings (a reload replaces
     * the {@link Settings} instance) and rebuilds block populators so that changes
     * such as world depth, roof/floor and main blocks take effect on newly
     * generated chunks without a full server restart.
     */
    public void reload() {
        this.settings = addon.getSettings();
        this.blockPopulators.clear();
        // Overworld uses vanilla decorations (shouldGenerateDecorations = true).
        // Nether and End place ore/block veins (NewMaterialPopulator) and then
        // biome-aware surface features (CaveDecorationPopulator).
        if (this.environment != World.Environment.NORMAL) {
            this.blockPopulators.add(new NewMaterialPopulator(this.settings.getWorldDepth()));
            this.blockPopulators.add(new CaveDecorationPopulator(this.settings.getWorldDepth()));
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
     * Enable vanilla carver caves for the overworld (ravines, round carver tunnels).
     * These stack on top of the noise caves already provided by {@link #shouldGenerateNoise}.
     * Disabled for nether/end (they use the NewMaterialPopulator).
     */
    @Override
    public boolean shouldGenerateCaves() {
        return this.environment == World.Environment.NORMAL && this.settings.isOverworldCarvers();
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
     * Fills nether and end chunks with their base material and carves
     * noise-based caves through them. Not called for the overworld — vanilla
     * noise handles it when {@link #shouldGenerateNoise} is true.
     *
     * <p>Unlike the old approach (a solid fill peppered with random single-block
     * holes, floating fire and stray lava by the populator), the rock is now
     * carved by {@link NoiseCaveGenerator} into connected tunnels and chambers.
     * A margin of solid rock is kept against the floor and roof so the world
     * stays fully enclosed, and in the nether the lowest cave voids fill with a
     * lava sea instead of open air.</p>
     */
    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        // Only called for NETHER and THE_END; overworld is handled by vanilla.
        final World.Environment env = worldInfo.getEnvironment();
        final int minHeight = worldInfo.getMinHeight();
        final int worldHeight = Math.min(worldInfo.getMaxHeight(), this.settings.getWorldDepth());
        final Material base = switch (env) {
            case NETHER -> this.settings.getNetherMainBlock();
            case THE_END -> this.settings.getEndMainBlock();
            default -> this.settings.getNormalMainBlock();
        };

        // Fill the whole column solid first (one fast region write), then carve
        // caves out of it. This keeps the world solid by default.
        chunkData.setRegion(0, minHeight + 1, 0, 16, worldHeight - 1, 16, base);

        final NoiseCaveGenerator caves = getCaveGenerator(worldInfo.getSeed());
        // Caves are confined to the middle band so the floor and roof stay solid.
        final int caveBottom = minHeight + CAVE_FLOOR_MARGIN;
        final int caveTop = worldHeight - CAVE_ROOF_MARGIN;
        final int lavaLevel = caveBottom + NETHER_LAVA_DEPTH;
        final boolean nether = env == World.Environment.NETHER;

        // Carve the caves. Surface features (fire, vegetation, end rods, chorus)
        // are added afterwards by the CaveDecorationPopulator, which can see the
        // biome at each spot.
        for (int x = 0; x < 16; x++) {
            final int worldX = (chunkX << 4) + x;
            for (int z = 0; z < 16; z++) {
                final int worldZ = (chunkZ << 4) + z;
                for (int y = caveBottom + 1; y < caveTop; y++) {
                    if (!caves.isCave(worldX, y, worldZ, CAVE_THRESHOLD)) {
                        continue;
                    }
                    // Nether cave floors pool with lava; everything else is open air.
                    chunkData.setBlock(x, y, z, nether && y <= lavaLevel ? Material.LAVA : Material.AIR);
                }
            }
        }
    }

    /**
     * Returns the cave generator for the given seed, rebuilding it only when the
     * seed changes so repeated chunk generation reuses the noise fields.
     */
    private NoiseCaveGenerator getCaveGenerator(long seed) {
        if (this.caveGenerator == null || this.caveGeneratorSeed != seed) {
            this.caveGenerator = new NoiseCaveGenerator(seed);
            this.caveGeneratorSeed = seed;
        }
        return this.caveGenerator;
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
            // Optional density control: re-solidify a fraction of the vanilla caves
            // so the overworld is not "nothing but passageways" (issue #111).
            final double caveFill = settings.getOverworldCaveFill();
            final NoiseCaveGenerator fillField = caveFill > 0 ? getCaveGenerator(worldInfo.getSeed()) : null;
            for (int x = 0; x < 16; x++) {
                final int worldX = (chunkX << 4) + x;
                for (int z = 0; z < 16; z++) {
                    capColumn(chunkData, x, z, minHeight, maxHeight, fillMaterial);
                    if (fillField != null) {
                        fillCaves(chunkData, x, z, worldX, (chunkZ << 4) + z, minHeight, maxHeight,
                                fillMaterial, fillField, caveFill);
                    }
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
        return switch (worldInfo.getEnvironment()) {
            // Vanilla biome placement — underground biomes form naturally.
            case NORMAL -> null;
            // Share the nether biomes out into natural regions across the world.
            case NETHER -> new NetherBiomeProvider(this.addon);
            // End keeps a single configurable biome.
            default -> new FlatBiomeProvider(this.addon);
        };
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

    /**
     * Re-solidifies a fraction of the vanilla overworld cave air to thin the cave
     * network. Runs <b>after</b> {@link #capColumn} has already sealed this column's
     * sky, so any remaining {@code AIR} or {@code CAVE_AIR} below the cap is genuine
     * underground cave.
     *
     * <p>Both air types must be matched: the vanilla 1.18+ <i>noise</i> caves (the
     * dense "cheese and spaghetti" network) are placed as plain {@code AIR}, while
     * the older carvers use {@code CAVE_AIR}. Matching only {@code CAVE_AIR} would
     * miss the noise caves entirely — which are exactly the passageways this setting
     * is meant to thin. A low-frequency noise field decides which blocks to fill, so
     * caves close in broad connected patches rather than as a random speckle, and the
     * decision is deterministic for the world seed.</p>
     *
     * @param chunkData    chunk being generated
     * @param x            block X within chunk (0-15)
     * @param z            block Z within chunk (0-15)
     * @param worldX       absolute world X of this column
     * @param worldZ       absolute world Z of this column
     * @param minHeight    world min height
     * @param maxHeight    world max height (roof reserved at maxHeight-1)
     * @param fillMaterial material used to fill caves (normally the main block)
     * @param fillField    noise field seeded from the world seed
     * @param ratio        fraction to fill; a block is filled when its field value is below this
     */
    private void fillCaves(ChunkData chunkData, int x, int z, int worldX, int worldZ, int minHeight, int maxHeight,
            Material fillMaterial, NoiseCaveGenerator fillField, double ratio) {
        // Leave the floor (minHeight, set by generateBedrock) and roof (maxHeight-1) alone.
        for (int y = maxHeight - 2; y > minHeight; y--) {
            Material type = chunkData.getType(x, y, z);
            if ((type == Material.AIR || type == Material.CAVE_AIR)
                    && fillField.fillField(worldX, y, worldZ) < ratio) {
                chunkData.setBlock(x, y, z, fillMaterial);
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
