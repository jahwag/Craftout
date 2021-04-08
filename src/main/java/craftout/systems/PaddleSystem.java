package craftout.systems;

import craftout.components.Paddle;
import craftout.components.Velocity;
import craftout.ecs.Entity;
import craftout.ecs.EntityComponentSystem;
import craftout.ecs.GameSystem;

import static com.mojang.sdk.Input.Button.LEFT;
import static com.mojang.sdk.Input.Button.RIGHT;
import static com.mojang.sdk.Input.isButtonPressed;

public final class PaddleSystem implements GameSystem {

    private static final float INITIAL_SPEED = 15f;

    @Override
    public void update(EntityComponentSystem entityComponentSystem) {
        for (var paddleEntity : entityComponentSystem.findAll(Paddle.class)) {
            update(paddleEntity, isButtonPressed(LEFT), isButtonPressed(RIGHT));
        }
    }

    void update(Entity paddleEntity, boolean leftButtonPressed, boolean rightButtonPressed) {
        var left = leftButtonPressed ? -INITIAL_SPEED : 0;
        var right = rightButtonPressed ? INITIAL_SPEED : 0;

        var paddleVelocity = paddleEntity.getComponent(Velocity.class);
        paddleVelocity.setX(left + right);
    }

}
