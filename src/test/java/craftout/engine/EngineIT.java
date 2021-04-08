package craftout.engine;

import com.mojang.sdk.Window;
import craftout.ecs.EntityComponentSystem;
import craftout.systems.BallSystem;
import craftout.systems.BrickSystem;
import craftout.systems.HudSystem;
import craftout.systems.LevelSystem;
import craftout.systems.PaddleSystem;
import craftout.systems.PhysicsSystem;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith({MockitoExtension.class, RandomBeansExtension.class})
class EngineIT {

    @Mock
    Window window;

    Engine engine;

    @BeforeEach
    void setUp() {
        this.engine = new Engine(window);

        EntityComponentSystem entityComponentSystem = engine.getEntityComponentSystem();
        entityComponentSystem.add(new LevelSystem(engine));
        entityComponentSystem.add(new PaddleSystem());
        entityComponentSystem.add(new BallSystem());
        entityComponentSystem.add(new HudSystem());
        entityComponentSystem.add(new BrickSystem(engine));
        entityComponentSystem.add(new PhysicsSystem());
    }

    @Test
    void run() {
        given(window.getSecondsSinceCreation()).willReturn(0d);
        given(window.isOpen()).willReturn(false);

        engine.run();

        InOrder inOrder = inOrder(window);
        inOrder.verify(window)
               .getSecondsSinceCreation();
        inOrder.verify(window, times(1))
               .isOpen();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void update() {
        engine.update();

        verifyNoMoreInteractions(window);
    }

}