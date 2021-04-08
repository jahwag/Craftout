package craftout.ecs;

import craftout.components.Ball;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class EntityComponentSystemTest {

    private static final int EXPECTED_ID = 1;

    @Mock
    GameSystem system;

    EntityComponentSystem entityComponentSystem;

    @BeforeEach
    void setUp() {
        this.entityComponentSystem = new EntityComponentSystem();
    }

    @Test
    void add_givenValidInput_shouldSucceed() {
        entityComponentSystem.add(system);

        Collection<GameSystem> systems = entityComponentSystem.getSystems();
        assertTrue(systems.contains(system));
    }

    @Test
    void add_givenNull_shouldThrow() {
        assertThrows(NullPointerException.class, () -> entityComponentSystem.add(null));
    }

    @Test
    void update_shouldSucceed() {
        entityComponentSystem.add(system);

        entityComponentSystem.update(system.getClass());

        verify(system).update(entityComponentSystem);
        verifyNoMoreInteractions(system);
    }

    @Test
    void createEntity() {
        Entity entity = entityComponentSystem.createEntity();

        assertEquals(EXPECTED_ID, entity.id());
    }

    @Test
    void remove() {
        Entity entity = entityComponentSystem.createEntity();
        entityComponentSystem.add(entity, new Ball());
        entityComponentSystem.remove(entity);

        assertEquals(emptyList(), entityComponentSystem.findAll());
    }

}