package craftout.systems;

import com.mojang.sdk.SpriteType;
import craftout.Craftout;
import craftout.components.Ball;
import craftout.components.Brick;
import craftout.components.CollisionBox;
import craftout.components.CollisionBox.Behavior;
import craftout.components.HudText;
import craftout.components.Lives;
import craftout.components.Paddle;
import craftout.components.Score;
import craftout.components.Sprite;
import craftout.components.Translation;
import craftout.components.Velocity;
import craftout.ecs.Entity;
import craftout.ecs.EntityComponentSystem;
import craftout.ecs.GameSystem;
import craftout.engine.Engine;
import craftout.exceptions.LoadLevelException;
import craftout.geometry.Vector2D;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mojang.sdk.Input.Button.FIRE;
import static com.mojang.sdk.Input.isButtonPressed;
import static craftout.systems.LevelSystem.Status.GAME_OVER;
import static craftout.systems.LevelSystem.Status.NOT_STARTED;
import static craftout.systems.LevelSystem.Status.STARTED;

public final class LevelSystem implements GameSystem {

    private static final int HUD_REGULAR_TEXT_FONT_SIZE = 20;

    private static final int HUD_LARGE_TEXT_FONT_SIZE = HUD_REGULAR_TEXT_FONT_SIZE * 2;

    private static final int BRICK_WIDTH = 32;

    private static final int BRICK_HEIGHT = 16;

    private static final int INITIAL_LIVES = 2;

    private static final int PADDLE_INITIAL_X = Craftout.WINDOW_WIDTH / 2;

    private static final int PADDLE_INITIAL_Y = (int) (Craftout.WINDOW_HEIGHT * (9f / 10f));

    private static final int TOP = 40;

    private static final double BALL_LEVEL_INCREASE = 1.4d;

    private static final double INITIAL_BALL = -4d;

    private final Engine engine;

    private Status status = NOT_STARTED;

    private int level = 0;

    public LevelSystem(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void update(EntityComponentSystem entityComponentSystem) {
        handleGameOver(entityComponentSystem);

        boolean noBricks = entityComponentSystem.findAll(Brick.class)
                                                .isEmpty();
        if (status == NOT_STARTED || status == GAME_OVER && isButtonPressed(FIRE)) {
            handleNewGame(entityComponentSystem);
            status = STARTED;
        } else if (status != GAME_OVER && noBricks) {
            loadLevelFromCsv(entityComponentSystem, String.format("/level%s.csv", ++level));
            incrementLives(entityComponentSystem);
            increaseBallVelocity(entityComponentSystem);
        }

        handleDeath(entityComponentSystem);
    }

    void handleGameOver(EntityComponentSystem entityComponentSystem) {
        var optionalLivesEntity = entityComponentSystem.find(Lives.class);
        if (optionalLivesEntity.isEmpty()) {
            return;
        }

        var livesRemaining = optionalLivesEntity.get()
                                                .getComponent(Lives.class)
                                                .getRemaining();
        if (livesRemaining > 0) {
            return;
        }

        status = GAME_OVER;
        engine.removeAllEntities();
        var gameOver = entityComponentSystem.createEntity();
        entityComponentSystem.add(gameOver, new HudText(HUD_LARGE_TEXT_FONT_SIZE).setText("Game Over"));
        entityComponentSystem.add(gameOver, new Translation(Craftout.WINDOW_WIDTH / 2f - 148, Craftout.WINDOW_HEIGHT / 2f - 10));
    }

    void handleNewGame(EntityComponentSystem entityComponentSystem) {
        engine.removeAllEntities();

        var paddle = entityComponentSystem.createEntity();
        entityComponentSystem.add(paddle, new Paddle());
        entityComponentSystem.add(paddle, new Sprite(SpriteType.PADDLE_LEFT, 8, 16, -48, 0));
        entityComponentSystem.add(paddle, new Sprite(SpriteType.PADDLE_MID, 8, 16, -40, 0));
        entityComponentSystem.add(paddle, new Sprite(SpriteType.PADDLE_MID, 8, 16, -32, 0));
        entityComponentSystem.add(paddle, new Sprite(SpriteType.PADDLE_MID, 8, 16, -24, 0));
        entityComponentSystem.add(paddle, new Sprite(SpriteType.PADDLE_MID, 8, 16, -16, 0));
        entityComponentSystem.add(paddle, new Sprite(SpriteType.PADDLE_MID, 8, 16, -8, 0));
        entityComponentSystem.add(paddle, new Sprite(SpriteType.PADDLE_MID, 8, 16, 0, 0));
        entityComponentSystem.add(paddle, new Sprite(SpriteType.PADDLE_MID, 8, 16, 8, 0));
        entityComponentSystem.add(paddle, new Sprite(SpriteType.PADDLE_MID, 8, 16, 16, 0));
        entityComponentSystem.add(paddle, new Sprite(SpriteType.PADDLE_MID, 8, 16, 24, 0));
        entityComponentSystem.add(paddle, new Sprite(SpriteType.PADDLE_MID, 8, 16, 32, 0));
        entityComponentSystem.add(paddle, new Sprite(SpriteType.PADDLE_MID, 8, 16, 40, 0));
        entityComponentSystem.add(paddle, new Sprite(SpriteType.PADDLE_RIGHT, 8, 16, 48, 0));
        entityComponentSystem.add(paddle, new Translation(PADDLE_INITIAL_X, PADDLE_INITIAL_Y));
        entityComponentSystem.add(paddle, new Velocity(0f, 0f));
        entityComponentSystem.add(paddle, new CollisionBox().setBehavior(Behavior.FROZEN));

        var ball = entityComponentSystem.createEntity();
        entityComponentSystem.add(ball, new Ball().setLevelVelocity(new Vector2D(0, INITIAL_BALL)));
        entityComponentSystem.add(ball, new Sprite(SpriteType.BALL, 8, 8, 0, 0));
        entityComponentSystem.add(ball, new Translation(PADDLE_INITIAL_X, PADDLE_INITIAL_Y - 8f));
        entityComponentSystem.add(ball, new Velocity(0f, 0f));
        entityComponentSystem.add(ball, new CollisionBox().setBehavior(Behavior.GOES_FLYING));

        var lives = entityComponentSystem.createEntity();
        entityComponentSystem.add(lives, new HudText(HUD_REGULAR_TEXT_FONT_SIZE));
        entityComponentSystem.add(lives, new Lives(INITIAL_LIVES));
        entityComponentSystem.add(lives, new Translation(8, 8));

        var score = entityComponentSystem.createEntity();
        entityComponentSystem.add(score, new HudText(HUD_REGULAR_TEXT_FONT_SIZE));
        entityComponentSystem.add(score, new Score());
        entityComponentSystem.add(score, new Translation((Craftout.WINDOW_WIDTH / 2f) - 64, 8));
    }

    void incrementLives(EntityComponentSystem entityComponentSystem) {
        var optionalLivesEntity = entityComponentSystem.find(Lives.class);
        if (optionalLivesEntity.isPresent()) {
            var livesEntity = optionalLivesEntity.get();
            var lives = livesEntity.getComponent(Lives.class);
            lives.setRemaining(lives.getRemaining() + 1);
        }
    }

    void handleDeath(EntityComponentSystem entityComponentSystem) {
        var optionalBallEntity = entityComponentSystem.find(Ball.class);
        if (optionalBallEntity.isEmpty()) {
            return;
        }

        var ballEntity = optionalBallEntity.get();
        var ballTranslation = ballEntity.getComponent(Translation.class);
        var dead = ballTranslation.isOffscreen();

        if (dead) {
            level = 0;
            decrementLives(entityComponentSystem);
            resetBall(entityComponentSystem, ballEntity, ballTranslation);
        }
    }

    void loadLevelFromCsv(EntityComponentSystem entityComponentSystem, String fileName) {
        List<String> lines = new LinkedList<>();
        try (var br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)))) {
            while (br.ready()) {
                lines.add(br.readLine());

            }
        } catch (Exception e) {
            throw new LoadLevelException(fileName, e);
        }
        List<List<String>> matrix = lines.stream()
                                         .map(s -> s.split(","))
                                         .map(Arrays::asList)
                                         .collect(Collectors.toList());
        matrix.remove(0);

        for (int x = 0; x < matrix.size(); x++) {
            for (int y = 0; y < matrix.get(x)
                                      .size(); y++) {
                String column = matrix.get(x)
                                      .get(y)
                                      .trim();
                if (column.equalsIgnoreCase("X")) {
                    var brick = entityComponentSystem.createEntity();
                    entityComponentSystem.add(brick, new Brick());
                    entityComponentSystem.add(brick, new Sprite(SpriteType.BRICK, 32, 16, 0, 0));
                    entityComponentSystem.add(brick, new Translation((float) y * BRICK_WIDTH, (float) x * BRICK_HEIGHT + TOP));
                    entityComponentSystem.add(brick, new Velocity(0, 0));
                    entityComponentSystem.add(brick, new CollisionBox().setBehavior(Behavior.FROZEN));
                }
            }
        }

    }

    void increaseBallVelocity(EntityComponentSystem entityComponentSystem) {
        var optionalBallEntity = entityComponentSystem.find(Ball.class);
        if (optionalBallEntity.isEmpty()) {
            return;
        }

        var ballEntity = optionalBallEntity.get();
        var velocity = ballEntity.getComponent(Velocity.class);
        velocity.setX(velocity.getX() * BALL_LEVEL_INCREASE);
        velocity.setY(velocity.getY() * BALL_LEVEL_INCREASE);

        var ball = ballEntity.getComponent(Ball.class);
        ball.setLevelVelocity(ball.getLevelVelocity()
                                  .multiply(BALL_LEVEL_INCREASE));
    }

    void decrementLives(EntityComponentSystem entityComponentSystem) {
        var optionalLivesEntity = entityComponentSystem.find(Lives.class);
        if (optionalLivesEntity.isEmpty()) {
            return;
        }

        var livesEntity = optionalLivesEntity.get();
        var lives = livesEntity.getComponent(Lives.class);
        lives.setRemaining(lives.getRemaining() - 1);
    }

    void resetBall(EntityComponentSystem entityComponentSystem, Entity ballEntity, Translation ballTranslation) {
        var ball = ballEntity.getComponent(Ball.class);
        var sprite = ballEntity.getComponent(Sprite.class);
        var optionalPaddleEntity = entityComponentSystem.find(Paddle.class);
        if (optionalPaddleEntity.isEmpty()) {
            return;
        }

        var paddleEntity = optionalPaddleEntity.get();
        var paddleTranslation = paddleEntity.getComponent(Translation.class);
        ballTranslation.setCenterX(paddleTranslation.getCenterX());
        ballTranslation.setCenterY(paddleTranslation.getCenterY() - sprite.getHeight());

        var velocity = ballEntity.getComponent(Velocity.class);
        velocity.setX(0);
        velocity.setY(0);

        ball.setFired(false);
    }

    enum Status {
        NOT_STARTED,
        STARTED,
        GAME_OVER
    }

}
