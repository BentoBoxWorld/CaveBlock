package world.bentobox.caveblock;


import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.eclipse.jdt.annotation.NonNull;

import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.api.configuration.WorldSettings;
import world.bentobox.caveblock.commands.AdminCommand;
import world.bentobox.caveblock.commands.IslandCommand;
import world.bentobox.caveblock.generators.ChunkGeneratorWorld;


public class CaveBlock extends GameModeAddon
{
    /**
     * Executes code when loading the addon. This is called before {@link #onEnable()}. This should preferably
     * be used to setup configuration and worlds.
     */
    @Override
    public void onLoad()
    {
        super.onLoad();

        this.saveDefaultConfig();
        this.loadSettings();
    }


    /**
     * Executes code when enabling the addon. This is called after {@link #onLoad()}.
     */
    @Override
    public void onEnable()
    {
        this.playerCommand = new IslandCommand(this);
        this.adminCommand = new AdminCommand(this);
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
        if (this.settings != null)
        {
            new Config<>(this, Settings.class).saveConfigObject(this.settings);
        }
    }


    /**
     * This method loads CaveBlock settings
     */
    private void loadSettings()
    {
        this.settings = new Config<>(this, Settings.class).loadConfigObject();

        if (this.settings == null)
        {
            // Disable
            this.logError("CaveBlock settings could not load! Addon disabled.");
            this.setState(State.DISABLED);
        }
    }

    // ---------------------------------------------------------------------
    // Section: World generators
    // ---------------------------------------------------------------------


    /**
     * Make the worlds for this GameMode in this method. BentoBox will call it after onLoad() and before
     * onEnable(). {@link #islandWorld} must be created and assigned, {@link #netherWorld} and {@link
     * #endWorld} are optional and may be null.
     */
    @Override
    public void createWorlds()
    {
        String worldName = this.settings.getWorldName();

        if (this.getServer().getWorld(worldName) == null)
        {
            this.getLogger().info("Creating CaveBlock world ...");
        }

        this.chunkGenerator = new ChunkGeneratorWorld(this);

        // Create the world if it does not exist
        this.islandWorld = WorldCreator.name(worldName).
                type(WorldType.FLAT).
                environment(World.Environment.NORMAL).
                generator(this.chunkGenerator).
                createWorld();



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
                        generator(this.chunkGenerator).
                        environment(World.Environment.NETHER).
                        createWorld();
            }
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
                        generator(this.chunkGenerator).
                        environment(World.Environment.THE_END).
                        createWorld();
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
        return this.chunkGenerator;
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


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------


    /**
     * This stores CaveBlock addon settings.
     */
    private Settings settings;

    /**
     * This stores CaveBlock addon WorldGenerator.
     */
    private ChunkGeneratorWorld chunkGenerator;


    // ---------------------------------------------------------------------
    // Section: Constants
    // ---------------------------------------------------------------------


    /**
     * String for nether world.
     */
    private static final String NETHER = "_nether";

    /**
     * String for the end world.
     */
    private static final String THE_END = "_the_end";
}
