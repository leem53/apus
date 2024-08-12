package swiss.fihlon.apus.plugin.event.jfs;

import org.junit.jupiter.api.Test;
import swiss.fihlon.apus.configuration.Configuration;
import swiss.fihlon.apus.event.Language;
import swiss.fihlon.apus.event.Room;
import swiss.fihlon.apus.event.Session;
import swiss.fihlon.apus.event.Speaker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JavaForumStuttgartPluginTest {

    @Test
    void isEnabled() {
        final var configuration = mock(Configuration.class);
        final var jfsConfig = new JavaForumStuttgartConfig("test");
        when(configuration.getJfs()).thenReturn(jfsConfig);

        final var jfsPlugin = new JavaForumStuttgartPlugin(configuration);
        assertTrue(jfsPlugin.isEnabled());
    }

    @Test
    void isDisabledBecauseNull() {
        final var configuration = mock(Configuration.class);
        final var jfsConfig = new JavaForumStuttgartConfig(null);
        when(configuration.getJfs()).thenReturn(jfsConfig);

        final var jfsPlugin = new JavaForumStuttgartPlugin(configuration);
        assertFalse(jfsPlugin.isEnabled());
    }

    @Test
    void isDisabledBecauseEmpty() {
        final var configuration = mock(Configuration.class);
        final var jfsConfig = new JavaForumStuttgartConfig("");
        when(configuration.getJfs()).thenReturn(jfsConfig);

        final var jfsPlugin = new JavaForumStuttgartPlugin(configuration);
        assertFalse(jfsPlugin.isEnabled());
    }

    @Test
    void getSessions() {
        final var configuration = mock(Configuration.class);
        final var jfsConfig = new JavaForumStuttgartConfig("file:src/test/resources/jfs.db");
        when(configuration.getJfs()).thenReturn(jfsConfig);

        final var jfsPlugin = new JavaForumStuttgartPlugin(configuration);
        final var sessions = jfsPlugin.getSessions().toList();
        assertEquals(8, sessions.size());

        final var sessionIds = sessions.stream().map(Session::id).toList();
        for (String id : List.of("A1", "A2", "A3", "B4", "A5", "B6", "A7", "A8")) {
            final var sessionId = String.format("JFS:%s", id);
            assertTrue(sessionIds.contains(sessionId));
        }

        // full check of session with ID "JFS:5"
        final var session = sessions.get(5);
        final var today = LocalDate.now();
        assertEquals("JFS:A5", session.id());
        assertEquals(LocalDateTime.of(today, LocalTime.of(16, 0)), session.startDate());
        assertEquals(LocalDateTime.of(today, LocalTime.of(16, 45)), session.endDate());
        assertEquals(new Room("Room A"), session.room());
        assertEquals("Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat", session.title());
        assertEquals(2, session.speakers().size());
        assertEquals(new Speaker("Saul Goodman"), session.speakers().get(0));
        assertEquals(new Speaker("Mike Ehrmantraut"), session.speakers().get(1));
        assertEquals(Language.UNKNOWN, session.language());
    }

}
