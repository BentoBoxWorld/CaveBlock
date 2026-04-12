package world.bentobox.caveblock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import world.bentobox.bentobox.api.addons.AddonDescription;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.AbstractDatabaseHandler;
import world.bentobox.bentobox.database.DatabaseSetup;
import world.bentobox.bentobox.managers.AddonsManager;
import world.bentobox.bentobox.managers.CommandsManager;
import world.bentobox.caveblock.generators.ChunkGeneratorWorld;

/**
 * Tests for the main CaveBlock addon class.
 * @author tastybento
 */
class CaveBlockTest extends CommonTestSetup {

    @Mock
    private User user;

    private CaveBlock addon;
    private MockedStatic<DatabaseSetup> mockDb;

    @SuppressWarnings("unchecked")
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        // Database mock
        AbstractDatabaseHandler<Object> h = mock(AbstractDatabaseHandler.class);
        mockDb = Mockito.mockStatic(DatabaseSetup.class);
        DatabaseSetup dbSetup = mock(DatabaseSetup.class);
        mockDb.when(DatabaseSetup::getDatabase).thenReturn(dbSetup);
        when(dbSetup.getHandler(any())).thenReturn(h);
        when(h.saveObject(any())).thenReturn(CompletableFuture.completedFuture(true));

        // Command manager
        CommandsManager cm = mock(CommandsManager.class);
        when(plugin.getCommandsManager()).thenReturn(cm);

        // User
        when(user.isOp()).thenReturn(false);
        UUID userUuid = UUID.randomUUID();
        when(user.getUniqueId()).thenReturn(userUuid);
        when(user.getPlayer()).thenReturn(mockPlayer);
        when(user.getName()).thenReturn("tastybento");
        User.setPlugin(plugin);

        // Island mock
        when(im.getIsland(any(), any(UUID.class))).thenReturn(island);

        // Locales answer
        when(user.getTranslation(Mockito.anyString()))
                .thenAnswer((Answer<String>) invocation -> invocation.getArgument(0, String.class));

        // Addon setup — create a minimal addon.jar with a config.yml inside
        addon = new CaveBlock();
        File jFile = new File("addon.jar");
        List<String> lines = Arrays.asList("# CaveBlock Configuration", "uniqueId: config");
        Path path = Paths.get("config.yml");
        Files.write(path, lines, Charset.forName("UTF-8"));
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jFile))) {
            try (FileInputStream fis = new FileInputStream(path.toFile())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                JarEntry entry = new JarEntry(path.toString());
                jos.putNextEntry(entry);
                while ((bytesRead = fis.read(buffer)) != -1) {
                    jos.write(buffer, 0, bytesRead);
                }
            }
        }
        File dataFolder = new File("addons/CaveBlock");
        addon.setDataFolder(dataFolder);
        addon.setFile(jFile);
        AddonDescription desc = new AddonDescription.Builder("bentobox", "CaveBlock", "1.21.0")
                .description("test").authors("BONNe").build();
        addon.setDescription(desc);

        // AddonsManager
        AddonsManager am = mock(AddonsManager.class);
        when(plugin.getAddonsManager()).thenReturn(am);

        // FlagsManager
        when(plugin.getFlagsManager()).thenReturn(fm);
        when(fm.getFlags()).thenReturn(Collections.emptyList());
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        if (mockDb != null) {
            mockDb.closeOnDemand();
        }
        super.tearDown();
        new File("addon.jar").delete();
        new File("config.yml").delete();
        deleteAll(new File("addons"));
    }

    /**
     * Test method for {@link CaveBlock#onLoad()}.
     */
    @Test
    void testOnLoad() {
        addon.onLoad();
        File check = new File("addons/CaveBlock", "config.yml");
        assertTrue(check.exists());
    }

    /**
     * Test method for {@link CaveBlock#onEnable()}.
     */
    @Test
    void testOnEnable() {
        testOnLoad();
        addon.onEnable();
        assertTrue(addon.getPlayerCommand().isPresent());
        assertTrue(addon.getAdminCommand().isPresent());
    }

    /**
     * Test method for {@link CaveBlock#onReload()}.
     */
    @Test
    void testOnReload() {
        addon.onReload();
        File check = new File("addons/CaveBlock", "config.yml");
        assertTrue(check.exists());
    }

    /**
     * Test method for {@link CaveBlock#createWorlds()}.
     */
    @Test
    void testCreateWorlds() {
        addon.onLoad();
        addon.createWorlds();
        Mockito.verify(plugin, Mockito.atLeastOnce()).logDebug(any());
    }

    /**
     * Test method for {@link CaveBlock#getSettings()}.
     */
    @Test
    void testGetSettings() {
        addon.onLoad();
        assertNotNull(addon.getSettings());
    }

    /**
     * Test method for {@link CaveBlock#getWorldSettings()}.
     */
    @Test
    void testGetWorldSettings() {
        addon.onLoad();
        assertEquals(addon.getSettings(), addon.getWorldSettings());
    }

    /**
     * Test method for {@link CaveBlock#getDefaultWorldGenerator(String, String)}.
     * Generators are created in onLoad(); verify they are ChunkGeneratorWorld instances.
     */
    @Test
    void testGetDefaultWorldGeneratorNormal() {
        addon.onLoad();
        assertNotNull(addon.getDefaultWorldGenerator("caveblock-world", ""));
        assertTrue(addon.getDefaultWorldGenerator("caveblock-world", "") instanceof ChunkGeneratorWorld);
    }

    /**
     * Test that the nether world name routes to the nether generator.
     */
    @Test
    void testGetDefaultWorldGeneratorNether() {
        addon.onLoad();
        assertNotNull(addon.getDefaultWorldGenerator("caveblock-world_nether", ""));
        assertTrue(addon.getDefaultWorldGenerator("caveblock-world_nether", "") instanceof ChunkGeneratorWorld);
    }

    /**
     * Test that the end world name routes to the end generator.
     */
    @Test
    void testGetDefaultWorldGeneratorEnd() {
        addon.onLoad();
        assertNotNull(addon.getDefaultWorldGenerator("caveblock-world_the_end", ""));
        assertTrue(addon.getDefaultWorldGenerator("caveblock-world_the_end", "") instanceof ChunkGeneratorWorld);
    }

    /**
     * Test that {@link CaveBlock#isUsesNewChunkGeneration()} returns true.
     */
    @Test
    void testIsUsesNewChunkGeneration() {
        assertTrue(addon.isUsesNewChunkGeneration());
    }

    /**
     * Test that {@link CaveBlock#SKY_WALKER_FLAG} is not null.
     */
    @Test
    void testSkyWalkerFlagNotNull() {
        assertNotNull(CaveBlock.SKY_WALKER_FLAG);
    }
}
