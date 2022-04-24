package world.bentobox.caveblock;


import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.eclipse.jdt.annotation.NonNull;

import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.commands.admin.DefaultAdminCommand;
import world.bentobox.bentobox.api.commands.island.DefaultPlayerCommand;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.api.configuration.WorldSettings;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.caveblock.commands.IslandAboutCommand;
import world.bentobox.caveblock.generators.ChunkGeneratorWorld;
import world.bentobox.caveblock.listeners.CustomHeightLimitations;


public class CaveBlock extends GameModeAddon
{

    /**
     * Executes code when loading the addon. This is called before {@link #onEnable()}. This should preferably
     * be used to set up configuration and worlds.
     */
    @Override
    public void onLoad()
    {
        super.onLoad();

        this.saveDefaultConfig();
        this.loadSettings();

        this.chunkNormalGenerator = new ChunkGeneratorWorld(this, World.Environment.NORMAL);
        this.chunkNetherGenerator = new ChunkGeneratorWorld(this, World.Environment.NETHER);
        this.chunkEndGenerator = new ChunkGeneratorWorld(this, World.Environment.THE_END);

        // Player Command
        this.playerCommand = new DefaultPlayerCommand(this)
        {
            @Override
            public void setup()
            {
                super.setup();
                new IslandAboutCommand(this);
            }
        };

        // Admin command.
        this.adminCommand = new DefaultAdminCommand(this) {};
    }


    /**
     * Executes code when enabling the addon. This is called after {@link #onLoad()}.
     */
    @Override
    public void onEnable()
    {

        // Register flags
        CaveBlock.SKY_WALKER_FLAG.addGameModeAddon(this);
        this.getPlugin().getFlagsManager().registerFlag(CaveBlock.SKY_WALKER_FLAG);

        // Register listener
        this.registerListener(new CustomHeightLimitations(this));
    }


    /**
     * Executes code when reloading the addon.
     */
    @Override
    public void onReload()
    {
        super.onReload();
        this.loadSettings();
    }


    /**
     * Executes code when disabling the addon.
     */
    @Override
    public void onDisable()
    {
        // Do nothing
    }


    /**
     * This method loads CaveBlock settings
     */
    private void loadSettings()
    {
        this.settings = settingsConfig.loadConfigObject();

        if (this.settings == null)
        {
            // Disable
            this.logError("CaveBlock settings could not load! Addon disabled.");
            this.setState(State.DISABLED);
        }
    }

    /* (non-Javadoc)
     * @see world.bentobox.bentobox.api.addons.Addon#allLoaded()
     */
    @Override
    public void allLoaded() {
        // Reload settings and save them. This will occur after all addons have loaded
        this.loadSettings();
        this.saveWorldSettings();
    }

    // ---------------------------------------------------------------------
    // Section: World generators
    // ---------------------------------------------------------------------


    /**
     * Make the worlds for this GameMode in this method. BentoBox will call it after onLoad() and before
     * onEnable(). {@code islandWorld} must be created and assigned, {@code netherWorld} and {@code
     * endWorld} are optional and may be null.
     */
    @Override
    public void createWorlds()
    {
        String worldName = this.settings.getWorldName().toLowerCase();

        if (this.getServer().getWorld(worldName) == null)
        {
            this.getLogger().info("Creating CaveBlock world ...");
        }

        // Create the world if it does not exist
        this.islandWorld = WorldCreator.name(worldName).
                environment(World.Environment.NORMAL).
                generator(this.chunkNormalGenerator).
                createWorld();
        // Set spawn rates
        setSpawnRates(islandWorld);


        // Make the nether if it does not exist
        if (this.settings.isNetherGenerate())
        {
            if (this.getServer().getWorld(worldName + NETHER) == null)
            {
                this.log("Creating CaveBlock's Nether...");
            }

            if (!this.settings.isNetherIslands())
            {
                this.netherWorld = WorldCreator.name(worldName + NETHER).
                        type(WorldType.NORMAL).
                        environment(World.Environment.NETHER).
                        createWorld();
            }
            else
            {
                this.netherWorld = WorldCreator.name(worldName + NETHER).
                        type(WorldType.FLAT).
                        generator(this.chunkNetherGenerator).
                        environment(World.Environment.NETHER).
                        createWorld();
            }
            setSpawnRates(netherWorld);
        }

        // Make the end if it does not exist
        if (this.settings.isEndGenerate())
        {
            if (this.getServer().getWorld(worldName + THE_END) == null)
            {
                this.log("Creating CaveBlock's End World...");
            }
            if (!this.settings.isEndIslands())
            {
                this.endWorld = WorldCreator.name(worldName + THE_END).
                        type(WorldType.NORMAL).
                        environment(World.Environment.THE_END).
                        createWorld();
            }
            else
            {
                this.endWorld = WorldCreator.name(worldName + THE_END).
                        type(WorldType.FLAT).
                        generator(this.chunkEndGenerator).
                        environment(World.Environment.THE_END).
                        createWorld();
            }
            setSpawnRates(endWorld);
        }
    }


    private void setSpawnRates(World w) {
        if (w != null) {
            if (getSettings().getSpawnLimitMonsters() > 0) {
                w.setMonsterSpawnLimit(getSettings().getSpawnLimitMonsters());
            }
            if (getSettings().getSpawnLimitAmbient() > 0) {
                w.setAmbientSpawnLimit(getSettings().getSpawnLimitAmbient());
            }
            if (getSettings().getSpawnLimitAnimals() > 0) {
                w.setAnimalSpawnLimit(getSettings().getSpawnLimitAnimals());
            }
            if (getSettings().getSpawnLimitWaterAnimals() > 0) {
                w.setWaterAnimalSpawnLimit(getSettings().getSpawnLimitWaterAnimals());
            }
            if (getSettings().getTicksPerAnimalSpawns() > 0) {
                w.setTicksPerAnimalSpawns(getSettings().getTicksPerAnimalSpawns());
            }
            if (getSettings().getTicksPerMonsterSpawns() > 0) {
                w.setTicksPerMonsterSpawns(getSettings().getTicksPerMonsterSpawns());
            }
        }

    }


    /**
     * Defines the world generator for this game mode
     *
     * @param worldName - name of world that this applies to
     * @param id - id if any
     * @return Chunk generator
     * @since 1.2.0
     */
    @Override
    public @NonNull ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
    {
        if (worldName.endsWith("_nether"))
        {
            return this.chunkNetherGenerator;
        }
        else if (worldName.endsWith("_the_end"))
        {
            return this.chunkEndGenerator;
        }
        else
        {
            return this.chunkNormalGenerator;
        }
    }

    // ---------------------------------------------------------------------
    // Section: Getters
    // ---------------------------------------------------------------------


    /**
     * @return WorldSettings for this GameMode
     */
    @Override
    public WorldSettings getWorldSettings()
    {
        return this.settings;
    }


    /**
     * @return Settings for this GameMode
     */
    public Settings getSettings()
    {
        return this.settings;
    }


    @Override
    public void saveWorldSettings()
    {
        if (this.settings != null)
        {
            settingsConfig.saveConfigObject(this.settings);
        }
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------

    /**
     * Config object
     */
    private final Config<Settings> settingsConfig = new Config<>(this, Settings.class);

    /**
     * This stores CaveBlock addon settings.
     */
    private Settings settings;

    /**
     * This stores CaveBlock addon WorldGenerator for overworld.
     */
    private ChunkGeneratorWorld chunkNormalGenerator;

    /**
     * This stores CaveBlock addon WorldGenerator for the nether.
     */
    private ChunkGeneratorWorld chunkNetherGenerator;

    /**
     * This stores CaveBlock addon WorldGenerator for the end.
     */
    private ChunkGeneratorWorld chunkEndGenerator;


    // ---------------------------------------------------------------------
    // Section: Constants
    // ---------------------------------------------------------------------


    /**
     * This flag allows enables and disables to walk on top of the world without a
     * permission. When enabled, players will be able to reach other player islands through
     * top of the world.
     */
    public final static Flag SKY_WALKER_FLAG =
            new Flag.Builder("SKY_WALKER_FLAG", Material.FEATHER).
            type(Flag.Type.WORLD_SETTING).
            defaultSetting(false).
            build();

    /**
     * String for nether world.
     */
    private static final String NETHER = "_nether";

    /**
     * String for the end world.
     */
    private static final String THE_END = "_the_end";
}
