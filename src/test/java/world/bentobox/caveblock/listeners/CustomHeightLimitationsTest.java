package world.bentobox.caveblock.listeners;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.managers.IslandWorldManager;
import world.bentobox.bentobox.managers.PlayersManager;
import world.bentobox.bentobox.util.Util;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;
import world.bentobox.caveblock.WhiteBox;

import org.mockbukkit.mockbukkit.MockBukkit;

/**
 * Tests for {@link CustomHeightLimitations}.
 */
class CustomHeightLimitationsTest {

    /** World depth configured in Settings — ceiling is at worldDepth - 1 = 318. */
    private static final int WORLD_DEPTH = 319;
    /** Y value safely below the ceiling. */
    private static final double BELOW_LIMIT = 100.0;
    /** Y value at the ceiling (should trigger cancel). */
    private static final double AT_LIMIT = WORLD_DEPTH - 1;

    @Mock
    private CaveBlock addon;
    @Mock
    private Settings settings;
    @Mock
    private BentoBox plugin;
    @Mock
    private IslandWorldManager iwm;
    @Mock
    private Player player;
    @Mock
    private World world;
    @Mock
    private PlayersManager playersManager;

    private MockedStatic<Util> mockedUtil;
    private MockedStatic<CaveBlock> mockedCaveBlock;
    private AutoCloseable closeable;

    private Location from;
    private Location to;

    private CustomHeightLimitations listener;

    @BeforeEach
    public void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        MockBukkit.mock();

        // Inject BentoBox singleton so User can function
        WhiteBox.setInternalState(BentoBox.class, "instance", plugin);

        // Settings stub — world depth drives the height ceiling
        when(addon.getSettings()).thenReturn(settings);
        when(settings.getWorldDepth()).thenReturn(WORLD_DEPTH);

        // Player defaults — normal survival, alive, no special perms
        when(player.isOp()).thenReturn(false);
        when(player.isDead()).thenReturn(false);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.hasPermission("caveblock.skywalker")).thenReturn(false);
        when(player.getWorld()).thenReturn(world);

        // PlayersManager — player is not in a teleport by default
        when(addon.getPlayers()).thenReturn(playersManager);
        when(playersManager.isInTeleport(any())).thenReturn(false);

        // Over world stub
        when(addon.getOverWorld()).thenReturn(world);

        // User for the player (needed for sendMessage in the listener)
        world.bentobox.bentobox.managers.LocalesManager lm =
                mock(world.bentobox.bentobox.managers.LocalesManager.class);
        when(lm.get(any(), anyString())).thenAnswer(inv -> inv.getArgument(1, String.class));
        when(plugin.getLocalesManager()).thenReturn(lm);
        world.bentobox.bentobox.managers.PlaceholdersManager phm =
                mock(world.bentobox.bentobox.managers.PlaceholdersManager.class);
        when(phm.replacePlaceholders(any(), anyString())).thenAnswer(inv -> inv.getArgument(1, String.class));
        when(plugin.getPlaceholdersManager()).thenReturn(phm);
        User.setPlugin(plugin);
        User.clearUsers();
        User.getInstance(player);

        // IWM — needed by SKY_WALKER_FLAG.isSetForWorld() and User.translate() ([friendly_name] replacement)
        when(plugin.getIWM()).thenReturn(iwm);
        // Return false from inWorld so SKY_WALKER_FLAG.isSetForWorld() short-circuits to false (no bypass)
        when(iwm.inWorld(any(World.class))).thenReturn(false);
        when(iwm.getFriendlyName(any())).thenReturn("CaveBlock");

        // Util.sameWorld — return true by default (player is in the correct world)
        mockedUtil = Mockito.mockStatic(Util.class, Mockito.CALLS_REAL_METHODS);
        mockedUtil.when(() -> Util.sameWorld(any(), any())).thenReturn(true);

        // Create locations
        from = buildLocation(0, BELOW_LIMIT, 0);
        to   = buildLocation(0, AT_LIMIT, 0);

        listener = new CustomHeightLimitations(addon);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mockedUtil.closeOnDemand();
        closeable.close();
        MockBukkit.unmock();
        User.clearUsers();
        Mockito.framework().clearInlineMocks();
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Location buildLocation(int x, double y, int z) {
        Location loc = mock(Location.class);
        when(loc.getBlockX()).thenReturn(x);
        when(loc.getBlockY()).thenReturn((int) y);
        when(loc.getY()).thenReturn(y);
        when(loc.getBlockZ()).thenReturn(z);
        when(loc.getWorld()).thenReturn(world);
        return loc;
    }

    // -------------------------------------------------------------------------
    // PlayerMoveEvent — ceiling enforcement
    // -------------------------------------------------------------------------

    @Test
    void testMoveAboveLimitIsCancelled() {
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        listener.onPlayerMove(event);
        assertTrue(event.isCancelled(), "Move above world depth limit should be cancelled");
    }

    @Test
    void testMoveBelowLimitIsNotCancelled() {
        Location safeFrom = buildLocation(0, BELOW_LIMIT - 10, 0);
        Location safeTo   = buildLocation(0, BELOW_LIMIT, 0);
        PlayerMoveEvent event = new PlayerMoveEvent(player, safeFrom, safeTo);
        listener.onPlayerMove(event);
        assertFalse(event.isCancelled(), "Move below world depth limit should not be cancelled");
    }

    @Test
    void testMoveAboveLimitOpNotCancelled() {
        when(player.isOp()).thenReturn(true);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        listener.onPlayerMove(event);
        assertFalse(event.isCancelled(), "Op player should be able to move above limit");
    }

    @Test
    void testMoveAboveLimitCreativeNotCancelled() {
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        listener.onPlayerMove(event);
        assertFalse(event.isCancelled(), "Creative player should not be restricted");
    }

    @Test
    void testMoveAboveLimitSpectatorNotCancelled() {
        when(player.getGameMode()).thenReturn(GameMode.SPECTATOR);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        listener.onPlayerMove(event);
        assertFalse(event.isCancelled(), "Spectator player should not be restricted");
    }

    @Test
    void testMoveAboveLimitSkywalkerPermissionNotCancelled() {
        when(player.hasPermission("caveblock.skywalker")).thenReturn(true);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        listener.onPlayerMove(event);
        assertFalse(event.isCancelled(), "Player with skywalker permission should not be restricted");
    }

    @Test
    void testMoveAboveLimitDeadPlayerNotCancelled() {
        when(player.isDead()).thenReturn(true);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        listener.onPlayerMove(event);
        assertFalse(event.isCancelled(), "Dead player movement should not be restricted");
    }

    @Test
    void testMoveAboveLimitDifferentWorldNotCancelled() {
        mockedUtil.when(() -> Util.sameWorld(any(), any())).thenReturn(false);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        listener.onPlayerMove(event);
        assertFalse(event.isCancelled(), "Movement in a different world should not be restricted");
    }

    @Test
    void testMoveAboveLimitInTeleportNotCancelled() {
        when(playersManager.isInTeleport(any())).thenReturn(true);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        listener.onPlayerMove(event);
        assertFalse(event.isCancelled(), "Player in teleport should not be restricted");
    }

    @Test
    void testMoveDownwardSameColumnNotCancelled() {
        // Moving down in the same X/Z column from above-limit to above-limit should be allowed
        Location highFrom = buildLocation(5, AT_LIMIT + 1, 7);
        Location highTo   = buildLocation(5, AT_LIMIT, 7);
        // from.getY() >= nextY and same X/Z — the shouldNotBeCancelled condition is met
        PlayerMoveEvent event = new PlayerMoveEvent(player, highFrom, highTo);
        listener.onPlayerMove(event);
        assertFalse(event.isCancelled(), "Moving downward in the same column should not be cancelled");
    }

    // -------------------------------------------------------------------------
    // PlayerTeleportEvent — ceiling enforcement
    // -------------------------------------------------------------------------

    @Test
    void testTeleportAboveLimitIsCancelled() {
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, from, to,
                PlayerTeleportEvent.TeleportCause.COMMAND);
        listener.onPlayerTeleport(event);
        assertTrue(event.isCancelled(), "Teleport above world depth limit should be cancelled");
    }

    @Test
    void testTeleportBelowLimitIsNotCancelled() {
        Location safeTo = buildLocation(0, BELOW_LIMIT, 0);
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, from, safeTo,
                PlayerTeleportEvent.TeleportCause.COMMAND);
        listener.onPlayerTeleport(event);
        assertFalse(event.isCancelled(), "Teleport below world depth limit should not be cancelled");
    }

    @Test
    void testTeleportAboveLimitOpNotCancelled() {
        when(player.isOp()).thenReturn(true);
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, from, to,
                PlayerTeleportEvent.TeleportCause.COMMAND);
        listener.onPlayerTeleport(event);
        assertFalse(event.isCancelled(), "Op player teleport above limit should not be cancelled");
    }

    @Test
    void testTeleportAboveLimitCreativeNotCancelled() {
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, from, to,
                PlayerTeleportEvent.TeleportCause.COMMAND);
        listener.onPlayerTeleport(event);
        assertFalse(event.isCancelled(), "Creative player teleport should not be restricted");
    }

    @Test
    void testTeleportAboveLimitSkywalkerPermissionNotCancelled() {
        when(player.hasPermission("caveblock.skywalker")).thenReturn(true);
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, from, to,
                PlayerTeleportEvent.TeleportCause.COMMAND);
        listener.onPlayerTeleport(event);
        assertFalse(event.isCancelled(), "Player with skywalker permission teleport should not be restricted");
    }
}
