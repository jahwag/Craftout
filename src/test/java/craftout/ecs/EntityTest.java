package craftout.ecs;

import craftout.components.Ball;
import craftout.exceptions.ComponentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EntityTest {

    private static final int ID = 1;

    @Mock
    Component component;

    Entity entity;

    @BeforeEach
    void setUp() {
        this.entity = new Entity(ID);
        entity.addComponent(component, component.getClass());
    }

    @Test
    void id() {
        assertEquals(ID, entity.id());
    }

    @Test
    void getComponent() {
        Component actual = entity.getComponent(this.component.getClass());

        assertEquals(this.component, actual);
    }

    @Test
    void getComponent_givenInvalidInput_shouldThrow() {
        assertThrows(ComponentNotFoundException.class, () -> entity.getComponent(Ball.class));
    }
    
    @Test
    void addComponent() {
        entity.addComponent(component, component.getClass());

        assertTrue(entity.contains(component.getClass()));
    }


    @Test
    void getComponents() {
        List<? extends Component> actual = entity.getComponents(this.component.getClass());

        assertEquals(List.of(this.component), actual);
    }


    @Test
    void contains() {
        assertTrue(entity.contains(this.component.getClass()));
    }

    @Test
    void getComponentTypes() {
        assertEquals(Set.of(this.component.getClass()), entity.getComponentTypes());
    }

}