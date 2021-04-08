package craftout.systems;

import craftout.components.Ball;
import craftout.components.Paddle;
import craftout.components.Translation;
import craftout.components.Velocity;
import craftout.ecs.Entity;
import craftout.ecs.EntityComponentSystem;
import craftout.geometry.Vector2D;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

@ExtendWith({MockitoExtension.class, RandomBeansExtension.class})
class BallSystemTest {

    @Mock
    EntityComponentSystem entityComponentSystem;

    @Mock
    Entity ballEntity;

    @Mock
    Ball ball;

    @Mock
    Entity paddleEntity;

    @Random
    Translation ballTranslation;

    @Random
    Translation paddleTranslation;

    @Random
    Velocity ballVelocity;

    BallSystem ballSystem;

    @BeforeEach
    void setUp() {
        this.ballSystem = new BallSystem();
    }

    @Test
    void update_givenUnfiredBall_shouldSucceed() {
        given(entityComponentSystem.findAll(Ball.class)).willReturn(List.of(ballEntity));
        given(ballEntity.getComponent(Ball.class)).willReturn(ball);
        given(entityComponentSystem.find(Paddle.class)).willReturn(Optional.of(paddleEntity));
        given(paddleEntity.getComponent(Translation.class)).willReturn(paddleTranslation);
        given(ballEntity.getComponent(Translation.class)).willReturn(ballTranslation);

        ballSystem.update(entityComponentSystem);

        InOrder inOrder = inOrder(entityComponentSystem, ballEntity, paddleEntity, ball);
        inOrder.verify(entityComponentSystem)
               .findAll(Ball.class);
        inOrder.verify(ballEntity)
               .getComponent(Ball.class);
        inOrder.verify(ball)
               .isFired();
        inOrder.verify(entityComponentSystem)
               .find(Paddle.class);
        inOrder.verify(paddleEntity)
               .getComponent(Translation.class);
        inOrder.verify(ballEntity)
               .getComponent(Translation.class);
        inOrder.verifyNoMoreInteractions();

        assertThat(ballTranslation.getCenterX(), equalTo(paddleTranslation.getCenterX()));
    }

    @Test
    void handlePaddleMove_givenNoPaddle_shouldReturn() {
        given(entityComponentSystem.findAll(Ball.class)).willReturn(List.of(ballEntity));
        given(ballEntity.getComponent(Ball.class)).willReturn(ball);
        given(entityComponentSystem.find(Paddle.class)).willReturn(Optional.empty());

        ballSystem.update(entityComponentSystem);

        InOrder inOrder = inOrder(entityComponentSystem, ballEntity, paddleEntity, ball);
        inOrder.verify(entityComponentSystem)
               .findAll(Ball.class);
        inOrder.verify(ballEntity)
               .getComponent(Ball.class);
        inOrder.verify(ball)
               .isFired();
        inOrder.verify(entityComponentSystem)
               .find(Paddle.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void handlePaddleMove_givenPaddle_shouldSucceed() {
        given(entityComponentSystem.find(Paddle.class)).willReturn(Optional.of(paddleEntity));
        given(paddleEntity.getComponent(Translation.class)).willReturn(paddleTranslation);
        given(ballEntity.getComponent(Translation.class)).willReturn(ballTranslation);

        ballSystem.handlePaddleMove(entityComponentSystem, ballEntity);

        InOrder inOrder = inOrder(entityComponentSystem, ballEntity, paddleEntity, ball);
        inOrder.verify(paddleEntity)
               .getComponent(Translation.class);
        inOrder.verify(ballEntity)
               .getComponent(Translation.class);
        inOrder.verifyNoMoreInteractions();

        assertThat(ballTranslation.getCenterX(), equalTo(paddleTranslation.getCenterX()));
    }

    @Test
    void handleFire_shouldSucceed() {
        given(ballEntity.getComponent(Ball.class)).willReturn(ball);
        given(ball.getLevelVelocity()).willReturn(new Vector2D(0, -5f));
        given(ballEntity.getComponent(Velocity.class)).willReturn(ballVelocity);

        ballSystem.handleFire(ballEntity, true);

        InOrder inOrder = inOrder(entityComponentSystem, ballEntity, paddleEntity, ball);
        inOrder.verify(ballEntity)
               .getComponent(Ball.class);
        inOrder.verify(ballEntity)
               .getComponent(Velocity.class);
        inOrder.verify(ball)
               .getLevelVelocity();
        inOrder.verifyNoMoreInteractions();

        assertThat(0.0, equalTo(ballVelocity.getX()));
        assertThat(-5.0, equalTo(ballVelocity.getY()));
    }

    @Test
    void update_givenFiredBall_shouldReturn() {
        given(entityComponentSystem.findAll(Ball.class)).willReturn(List.of(ballEntity));
        given(ballEntity.getComponent(Ball.class)).willReturn(ball);
        given(ball.isFired()).willReturn(true);

        ballSystem.update(entityComponentSystem);

        InOrder inOrder = inOrder(entityComponentSystem, ballEntity, paddleEntity, ball);
        inOrder.verify(entityComponentSystem)
               .findAll(Ball.class);
        inOrder.verify(ballEntity)
               .getComponent(Ball.class);
        inOrder.verify(ball)
               .isFired();
        inOrder.verifyNoMoreInteractions();

        assertThat(ballTranslation.getCenterX(), equalTo(ballTranslation.getCenterX()));
    }
}