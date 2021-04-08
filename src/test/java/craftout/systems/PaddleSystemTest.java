package craftout.systems;

import craftout.components.Paddle;
import craftout.components.Velocity;
import craftout.ecs.Entity;
import craftout.ecs.EntityComponentSystem;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

@ExtendWith({MockitoExtension.class, RandomBeansExtension.class})
class PaddleSystemTest {

    public static final double EXPECTED_SPEED = 15.0;

    @Mock
    EntityComponentSystem entityComponentSystem;

    @Mock
    Entity paddleEntity;

    PaddleSystem paddleSystem;

    @Random
    Velocity velocity;

    @BeforeEach
    void setUp() {
        this.paddleSystem = new PaddleSystem();
    }

    @Test
    void update_givenNonePressed_shouldSucceed() {
        given(entityComponentSystem.findAll(Paddle.class)).willReturn(List.of(paddleEntity));
        given(paddleEntity.getComponent(Velocity.class)).willReturn(velocity);

        paddleSystem.update(entityComponentSystem);

        InOrder inOrder = inOrder(entityComponentSystem, paddleEntity);
        inOrder.verify(entityComponentSystem)
               .findAll(Paddle.class);
        inOrder.verify(paddleEntity)
               .getComponent(Velocity.class);
        inOrder.verifyNoMoreInteractions();

        assertThat(velocity.getX(), equalTo(0.0));
    }

    @Test
    void update_givenLeftPressedAndRightPressed_shouldCancelOut() {
        given(paddleEntity.getComponent(Velocity.class)).willReturn(velocity);

        paddleSystem.update(paddleEntity, true, true);

        InOrder inOrder = inOrder(entityComponentSystem, paddleEntity);
        inOrder.verify(paddleEntity)
               .getComponent(Velocity.class);
        inOrder.verifyNoMoreInteractions();

        assertThat(velocity.getX(), equalTo(0.0));
    }

    @Test
    void update_givenLeftPressed_should() {
        given(paddleEntity.getComponent(Velocity.class)).willReturn(velocity);

        paddleSystem.update(paddleEntity, true, false);

        InOrder inOrder = inOrder(entityComponentSystem, paddleEntity);
        inOrder.verify(paddleEntity)
               .getComponent(Velocity.class);
        inOrder.verifyNoMoreInteractions();

        assertThat(velocity.getX(), equalTo(-EXPECTED_SPEED));
    }

    @Test
    void update_givenRightPressed_should() {
        given(paddleEntity.getComponent(Velocity.class)).willReturn(velocity);

        paddleSystem.update(paddleEntity, false, true);

        InOrder inOrder = inOrder(entityComponentSystem, paddleEntity);
        inOrder.verify(paddleEntity)
               .getComponent(Velocity.class);
        inOrder.verifyNoMoreInteractions();

        assertThat(velocity.getX(), equalTo(EXPECTED_SPEED));
    }

}