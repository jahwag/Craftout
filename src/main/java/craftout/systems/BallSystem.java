package craftout.systems;

import craftout.components.Ball;
import craftout.components.Paddle;
import craftout.components.Translation;
import craftout.components.Velocity;
import craftout.ecs.Entity;
import craftout.ecs.EntityComponentSystem;
import craftout.ecs.GameSystem;

import static com.mojang.sdk.Input.Button.FIRE;
import static com.mojang.sdk.Input.isButtonPressed;

public final class BallSystem implements GameSystem {

    @Override
    public void update(EntityComponentSystem entityComponentSystem) {
        for (var ballEntity : entityComponentSystem.findAll(Ball.class)) {
            var ball = ballEntity.getComponent(Ball.class);

            if (!ball.isFired()) {
                handlePaddleMove(entityComponentSystem, ballEntity);
                handleFire(ballEntity, isButtonPressed(FIRE));
            }
        }
    }

    void handlePaddleMove(EntityComponentSystem entityComponentSystem, Entity ballEntity) {
        var optionalPaddleEntity = entityComponentSystem.find(Paddle.class);
        if (optionalPaddleEntity.isEmpty()) {
            return;
        }
        var paddleEntity = optionalPaddleEntity.get();
        var paddleTranslation = paddleEntity.getComponent(Translation.class);
        var ballTranslation = ballEntity.getComponent(Translation.class);

        ballTranslation.setCenterX(paddleTranslation.getCenterX());
    }

    void handleFire(Entity ballEntity, boolean firePressed) {
        if (firePressed) {
            var ball = ballEntity.getComponent(Ball.class);
            ball.setFired(true);

            var ballVelocity = ballEntity.getComponent(Velocity.class);
            var levelVelocity = ball.getLevelVelocity();

            ballVelocity.setX(levelVelocity.getX());
            ballVelocity.setY(levelVelocity.getY());
        }
    }

}
