package craftout.systems;

import craftout.components.Ball;
import craftout.components.HudText;
import craftout.components.Lives;
import craftout.components.Translation;
import craftout.ecs.Entity;
import craftout.ecs.EntityComponentSystem;
import craftout.engine.Engine;
import craftout.exceptions.LoadLevelException;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith({MockitoExtension.class, RandomBeansExtension.class})
class LevelSystemTest {

    @Mock
    Engine engine;

    @Mock
    EntityComponentSystem entityComponentSystem;

    @Mock
    Entity livesEntity;

    @Mock
    Entity gameOverEntity;

    @Mock
    Lives lives;

    LevelSystem levelSystem;

    private static Stream<Arguments> provideLevelFromCsvExpectedBricksAndComponents() {
        return Stream.of(
                Arguments.of(1, 1),
                Arguments.of(2, 36),
                Arguments.of(3, 48),
                Arguments.of(4, 96)
        );
    }

    @BeforeEach
    void setUp() {
        this.levelSystem = new LevelSystem(engine);
    }

    @Test
    void update_givenStatusNotStarted_shouldSucceed() {
        given(entityComponentSystem.find(Lives.class)).willReturn(Optional.of(livesEntity));
        given(livesEntity.getComponent(Lives.class)).willReturn(lives);
        given(lives.getRemaining()).willReturn(3);
        given(entityComponentSystem.createEntity()).willReturn(gameOverEntity);
        given(entityComponentSystem.find(Ball.class)).willReturn(Optional.empty());

        levelSystem.update(entityComponentSystem);

        InOrder inOrder = inOrder(engine, entityComponentSystem, livesEntity);
        inOrder.verify(entityComponentSystem)
               .find(Lives.class);
        inOrder.verify(livesEntity)
               .getComponent(Lives.class);
        inOrder.verify(engine)
               .removeAllEntities();
        inOrder.verify(entityComponentSystem)
               .createEntity();
        inOrder.verify(entityComponentSystem, times(28))
               .add(any(), any());
        inOrder.verify(entityComponentSystem)
               .find(Ball.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void handleGameOver_shouldSucceed() {
        given(entityComponentSystem.find(Lives.class)).willReturn(Optional.of(livesEntity));
        given(livesEntity.getComponent(Lives.class)).willReturn(lives);
        given(entityComponentSystem.createEntity()).willReturn(gameOverEntity);

        levelSystem.handleGameOver(entityComponentSystem);

        InOrder inOrder = inOrder(engine, entityComponentSystem, livesEntity);
        inOrder.verify(entityComponentSystem)
               .find(Lives.class);
        inOrder.verify(livesEntity)
               .getComponent(Lives.class);
        inOrder.verify(engine)
               .removeAllEntities();
        inOrder.verify(entityComponentSystem)
               .createEntity();
        inOrder.verify(entityComponentSystem)
               .add(eq(gameOverEntity), any(HudText.class));
        inOrder.verify(entityComponentSystem)
               .add(eq(gameOverEntity), any(Translation.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void incrementLives_shouldSucceed() {
        int expectedLives = 4;
        Lives lives = new Lives(expectedLives - 1);
        given(entityComponentSystem.find(Lives.class)).willReturn(Optional.of(livesEntity));
        given(livesEntity.getComponent(Lives.class)).willReturn(lives);

        levelSystem.incrementLives(entityComponentSystem);

        InOrder inOrder = inOrder(engine, entityComponentSystem, livesEntity);
        inOrder.verify(entityComponentSystem)
               .find(Lives.class);
        inOrder.verify(livesEntity)
               .getComponent(Lives.class);
        inOrder.verifyNoMoreInteractions();

        assertThat(lives.getRemaining(), equalTo(expectedLives));
    }

    @Test
    void decrementLives_shouldSucceed() {
        int expectedLives = 2;
        Lives lives = new Lives(expectedLives + 1);
        given(entityComponentSystem.find(Lives.class)).willReturn(Optional.of(livesEntity));
        given(livesEntity.getComponent(Lives.class)).willReturn(lives);

        levelSystem.decrementLives(entityComponentSystem);

        InOrder inOrder = inOrder(engine, entityComponentSystem, livesEntity);
        inOrder.verify(entityComponentSystem)
               .find(Lives.class);
        inOrder.verify(livesEntity)
               .getComponent(Lives.class);
        inOrder.verifyNoMoreInteractions();

        assertThat(lives.getRemaining(), equalTo(expectedLives));
    }

    @ParameterizedTest
    @MethodSource("provideLevelFromCsvExpectedBricksAndComponents")
    void loadLevelFromCsv_givenLevelCsv_shouldSucceed(int levelId, int brickCount) {
        given(entityComponentSystem.createEntity()).willReturn(gameOverEntity);

        levelSystem.loadLevelFromCsv(entityComponentSystem, String.format("/level%s.csv", levelId));

        InOrder inOrder = inOrder(engine, entityComponentSystem, livesEntity);
        inOrder.verify(entityComponentSystem, times(brickCount))
               .createEntity();
        inOrder.verify(entityComponentSystem, atLeastOnce())
               .add(any(), any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void loadLevelFromCsv_givenInvalidInput_shouldThrow() {
        assertThrows(LoadLevelException.class, () -> levelSystem.loadLevelFromCsv(entityComponentSystem, "hello.csv"));

        verifyNoMoreInteractions(entityComponentSystem);
    }


}