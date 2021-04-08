package craftout.systems;

import craftout.components.Brick;
import craftout.components.CollisionBox;
import craftout.components.Score;
import craftout.ecs.Entity;
import craftout.ecs.EntityComponentSystem;
import craftout.engine.Engine;
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
class BrickSystemTest {

    @Mock
    Engine engine;

    @Mock
    EntityComponentSystem entityComponentSystem;

    @Mock
    Entity brickEntity;

    @Random
    CollisionBox collisionBox;

    @Mock
    Entity scoreEntity;

    final Score score = new Score().setValue(100);

    BrickSystem brickSystem;

    @BeforeEach
    void setUp() {
        this.brickSystem = new BrickSystem(engine);
    }

    @Test
    void update_givenBrickCollision_shouldRemoveBrickAndIncrementScore() {
        given(entityComponentSystem.findAll(Brick.class)).willReturn(List.of(brickEntity));
        given(brickEntity.getComponent(CollisionBox.class)).willReturn(collisionBox.setCollided(true));
        given(entityComponentSystem.find(Score.class)).willReturn(Optional.of(scoreEntity));
        given(scoreEntity.getComponent(Score.class)).willReturn(score);

        brickSystem.update(entityComponentSystem);

        InOrder inOrder = inOrder(engine, entityComponentSystem, brickEntity, scoreEntity);
        inOrder.verify(entityComponentSystem)
               .findAll(Brick.class);
        inOrder.verify(brickEntity)
               .getComponent(CollisionBox.class);
        inOrder.verify(engine)
               .remove(brickEntity);
        inOrder.verify(entityComponentSystem)
               .find(Score.class);
        inOrder.verify(scoreEntity)
               .getComponent(Score.class);
        inOrder.verifyNoMoreInteractions();

        assertThat(score.getValue(), equalTo(200));
    }

    @Test
    void update_givenNoScore_shouldReturn() {
        given(entityComponentSystem.findAll(Brick.class)).willReturn(List.of(brickEntity));
        given(brickEntity.getComponent(CollisionBox.class)).willReturn(collisionBox.setCollided(true));
        given(entityComponentSystem.find(Score.class)).willReturn(Optional.empty());

        brickSystem.update(entityComponentSystem);

        InOrder inOrder = inOrder(engine, entityComponentSystem, brickEntity, scoreEntity);
        inOrder.verify(entityComponentSystem)
               .findAll(Brick.class);
        inOrder.verify(brickEntity)
               .getComponent(CollisionBox.class);
        inOrder.verify(engine)
               .remove(brickEntity);
        inOrder.verify(entityComponentSystem)
               .find(Score.class);
        inOrder.verifyNoMoreInteractions();
    }

}