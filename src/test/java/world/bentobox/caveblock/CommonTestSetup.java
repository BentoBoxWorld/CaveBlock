package world.bentobox.caveblock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableSet;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.configuration.WorldSettings;
import world.bentobox.bentobox.api.user.Notifier;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.database.objects.Players;
import world.bentobox.bentobox.managers.BlueprintsManager;
import world.bentobox.bentobox.managers.FlagsManager;
import world.bentobox.bentobox.managers.HooksManager;
import world.bentobox.bentobox.managers.IslandWorldManager;
import world.bentobox.bentobox.managers.IslandsManager;
import world.bentobox.bentobox.managers.LocalesManager;
import world.bentobox.bentobox.managers.PlaceholdersManager;
import world.bentobox.bentobox.managers.PlayersManager;
import world.bentobox.bentobox.util.Util;

/**
 * Common test setup for CaveBlock tests. Call super.setUp() in subclass @BeforeEach.
 */
public abstract class CommonTestSetup {

    protected UUID uuid = UUID.randomUUID();

    @Mock
    protected Player mockPlayer;
    @Mock
    protected PluginManager pim;
    @Mock
    protected ItemFactory itemFactory;
    @Mock
    protected Location location;
    @Mock
    protected World world;
    @Mock
    protected IslandWorldManager iwm;
    @Mock
    protected IslandsManager im;
    @Mock
    protected Island island;
    @Mock
    protected BentoBox plugin;
    @Mock
    protected PlayerInventory inv;
    @Mock
    protected Notifier notifier;
    @Mock
    protected FlagsManager fm;
    @Mock
    protected Spigot spigot;
    @Mock
    protected HooksManager hooksManager;
    @Mock
    protected BlueprintsManager bm;
    @Mock
    protected BukkitScheduler sch;
    @Mock
    protected LocalesManager lm;
    @Mock
    protected PlaceholdersManager phm;

    protected ServerMock server;
    protected MockedStatic<Bukkit> mockedBukkit;
    protected MockedStatic<Util> mockedUtil;
    protected AutoCloseable closeable;

    @BeforeEach
    @SuppressWarnings("java:S1130")
    public void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        server = MockBukkit.mock();

        // Inject BentoBox singleton
        WhiteBox.setInternalState(BentoBox.class, "instance", plugin);

        // Force Tag static fields to initialise under the real server
        @SuppressWarnings("unused")
        var unusedTagRef = org.bukkit.Tag.LEAVES;

        // Static Bukkit mock
        mockedBukkit = Mockito.mockStatic(Bukkit.class, Mockito.RETURNS_DEEP_STUBS);
        mockedBukkit.when(Bukkit::getMinecraftVersion).thenReturn("1.21.10");
        mockedBukkit.when(Bukkit::getBukkitVersion).thenReturn("");
        mockedBukkit.when(Bukkit::getPluginManager).thenReturn(pim);
        mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);
        mockedBukkit.when(Bukkit::getScheduler).thenReturn(sch);

        // Location
        when(location.getWorld()).thenReturn(world);
        when(location.getBlockX()).thenReturn(0);
        when(location.getBlockY()).thenReturn(0);
        when(location.getBlockZ()).thenReturn(0);
        when(location.toVector()).thenReturn(new Vector(0, 0, 0));
        when(location.clone()).thenReturn(location);

        // PlayersManager
        PlayersManager pm = mock(PlayersManager.class);
        when(plugin.getPlayers()).thenReturn(pm);
        Players players = mock(Players.class);
        when(players.getMetaData()).thenReturn(Optional.empty());
        when(pm.getPlayer(any(UUID.class))).thenReturn(players);

        // Player
        when(mockPlayer.getUniqueId()).thenReturn(uuid);
        when(mockPlayer.getLocation()).thenReturn(location);
        when(mockPlayer.getWorld()).thenReturn(world);
        when(mockPlayer.getName()).thenReturn("tastybento");
        when(mockPlayer.getInventory()).thenReturn(inv);
        when(mockPlayer.spigot()).thenReturn(spigot);
        when(mockPlayer.getType()).thenReturn(EntityType.PLAYER);

        User.setPlugin(plugin);
        User.clearUsers();
        User.getInstance(mockPlayer);

        // IWM
        when(plugin.getIWM()).thenReturn(iwm);
        when(iwm.inWorld(any(Location.class))).thenReturn(true);
        when(iwm.inWorld(any(World.class))).thenReturn(true);
        when(iwm.getFriendlyName(any())).thenReturn("CaveBlock");
        when(iwm.getAddon(any())).thenReturn(Optional.empty());

        // WorldSettings
        WorldSettings worldSet = new TestWorldSettings();
        when(iwm.getWorldSettings(any())).thenReturn(worldSet);

        // IslandsManager
        when(plugin.getIslands()).thenReturn(im);
        when(im.getProtectedIslandAt(any())).thenReturn(Optional.of(island));
        when(island.isAllowed(any())).thenReturn(false);
        when(island.isAllowed(any(User.class), any())).thenReturn(false);
        when(island.getOwner()).thenReturn(uuid);
        when(island.getMemberSet()).thenReturn(ImmutableSet.of(uuid));

        // Locales & Placeholders
        when(lm.get(any(), any())).thenAnswer((Answer<String>) invocation -> invocation.getArgument(1, String.class));
        when(plugin.getPlaceholdersManager()).thenReturn(phm);
        when(phm.replacePlaceholders(any(), any())).thenAnswer((Answer<String>) invocation -> invocation.getArgument(1, String.class));
        when(plugin.getLocalesManager()).thenReturn(lm);

        // Notifier
        when(plugin.getNotifier()).thenReturn(notifier);

        // Logger — Addon.getLogger() delegates to plugin.getLogger()
        when(plugin.getLogger()).thenReturn(Logger.getLogger("CaveBlock-test"));

        // BentoBox settings (fake players feature)
        world.bentobox.bentobox.Settings settings = new world.bentobox.bentobox.Settings();
        when(plugin.getSettings()).thenReturn(settings);

        // Util static mock
        mockedUtil = Mockito.mockStatic(Util.class, Mockito.CALLS_REAL_METHODS);
        mockedUtil.when(() -> Util.getWorld(any())).thenReturn(mock(World.class));
        Util.setPlugin(plugin);
        mockedUtil.when(() -> Util.findFirstMatchingEnum(any(), any())).thenCallRealMethod();

        // Hooks
        when(hooksManager.getHook(anyString())).thenReturn(Optional.empty());
        when(plugin.getHooks()).thenReturn(hooksManager);

        // BlueprintsManager
        when(plugin.getBlueprintsManager()).thenReturn(bm);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mockedBukkit.closeOnDemand();
        mockedUtil.closeOnDemand();
        closeable.close();
        MockBukkit.unmock();
        User.clearUsers();
        Mockito.framework().clearInlineMocks();
        deleteAll(new File("database"));
        deleteAll(new File("database_backup"));
    }

    protected static void deleteAll(File file) throws IOException {
        if (file.exists()) {
            Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }
}
