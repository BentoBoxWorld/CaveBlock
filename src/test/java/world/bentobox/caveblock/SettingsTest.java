package world.bentobox.caveblock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the WorldSettings hooks that BentoBox core reads from CaveBlock's {@link Settings}.
 */
class SettingsTest extends CommonTestSetup {

    private Settings s;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        s = new Settings();
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    void testTeamsDisabled() {
        assertFalse(s.isTeamsDisabled());
        s.setTeamsDisabled(true);
        assertTrue(s.isTeamsDisabled());
    }

    @Test
    void testStructureSettingsDefaultsDisableLargeStructures() {
        Map<String, Boolean> structures = s.getStructureSettings();
        assertEquals(Boolean.FALSE, structures.get("ancient_city"));
        assertEquals(Boolean.FALSE, structures.get("trial_chambers"));
        assertEquals(Boolean.FALSE, structures.get("mansion"));
        assertEquals(Boolean.TRUE, structures.get("mineshaft"));
        assertEquals(Boolean.TRUE, structures.get("stronghold"));
    }

    @Test
    void testStructureSettingsIsTheWorldStructuresConfig() {
        Map<String, Boolean> map = new HashMap<>();
        map.put("monument", false);
        s.setGenerateStructures(map);
        // Core BentoBox reads getStructureSettings(); it must be the same world.structures map
        assertSame(map, s.getStructureSettings());
        Map<String, Boolean> other = new HashMap<>();
        s.setStructureSettings(other);
        assertSame(other, s.getGenerateStructures());
    }
}
