package craftout.systems;

import craftout.Craftout;
import craftout.components.CollisionBox;
import craftout.components.Sprite;
import craftout.components.Translation;
import craftout.components.Velocity;
import craftout.ecs.Entity;
import craftout.ecs.EntityComponentSystem;
import craftout.ecs.GameSystem;
import craftout.geometry.Vector2D;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public final class PhysicsSystem implements GameSystem {

    @Override
    public void update(EntityComponentSystem entityComponentSystem) {
        for (var entity : entityComponentSystem.findAll(CollisionBox.class)) {
            updateCollisionBox(entity);

            for (var otherEntity : entityComponentSystem.findAll(CollisionBox.class)) {
                if (entity != otherEntity) {
                    handleCollision(otherEntity, entity);
                }
            }

            applyTranslation(entity);
        }
    }

    private void updateCollisionBox(Entity entity) {
        var collisionBox = entity.getComponent(CollisionBox.class);
        if (!entity.contains(Sprite.class)) {
            return;
        }

        var translation = entity.getComponent(Translation.class);
        var x = translation.getCenterX();
        var y = translation.getCenterY();
        var minX = translation.getCenterX();
        var maxX = translation.getCenterX();
        var minY = translation.getCenterY();
        var maxY = translation.getCenterY();

        for (var sprite : entity.getComponents(Sprite.class)) {
            x = min(x, translation.getCenterX() + sprite.getOffsetX());
            y = min(y, translation.getCenterY() + sprite.getOffsetY());
            minX = min(minX, translation.getCenterX() + sprite.getOffsetX());
            maxX = max(minX, translation.getCenterX() + sprite.getOffsetX() + sprite.getWidth());
            minY = min(minY, translation.getCenterY() + sprite.getOffsetY());
            maxY = max(maxY, translation.getCenterY() + sprite.getOffsetY() + sprite.getHeight());
        }

        collisionBox.setX(x);
        collisionBox.setY(y);
        collisionBox.setWidth((int) (maxX - minX));
        collisionBox.setHeight((int) (maxY - minY));
    }

    private void handleCollision(Entity otherEntity, Entity entity) {
        var self = entity.getComponent(CollisionBox.class);
        var velocity = entity.getComponent(Velocity.class);
        if (velocity.isZero()) {
            return;
        }

        for (var other : otherEntity.getComponents(CollisionBox.class)) {
            if (0 >= self.getX()) {
                velocity.setX(abs(velocity.getX()));
            } else if (Craftout.WINDOW_WIDTH < self.getX() + self.getWidth()) {
                velocity.setX(-abs(velocity.getX()));
            } else if (0 > self.getY()) {
                velocity.setY(abs(velocity.getY()));
            } else if (CollisionBox.Behavior.GOES_FLYING == self.getBehavior() &&
                    self.getX() < other.getX() + other.getWidth() &&
                    self.getX() + self.getWidth() > other.getX() &&
                    self.getY() < other.getY() + other.getHeight() &&
                    self.getY() + self.getHeight() > other.getY()) {
                other.setCollided(true);

                var magnitude = new Vector2D(velocity.getX(), velocity.getY()).magnitude();
                var terminalPoint = new Vector2D((other.getX() + other.getWidth()) / 2f, (other.getY() + other.getHeight()) / 2f);
                var startingPoint = new Vector2D((self.getX() + self.getWidth()) / 2f, (self.getY() + self.getHeight()) / 2f);
                var newVelocity = startingPoint.subtract(terminalPoint)
                                               .normalize()
                                               .multiply(magnitude);

                velocity.setX(newVelocity.getX());
                velocity.setY(newVelocity.getY());
            }
        }
    }

    private void applyTranslation(Entity entity) {
        if (!entity.contains(Velocity.class)) {
            return;
        }

        var velocity = entity.getComponent(Velocity.class);
        var position = entity.getComponent(Translation.class);

        position.setCenterX((float) (position.getCenterX() + velocity.getX()));
        position.setCenterY((float) (position.getCenterY() + velocity.getY()));
    }

}
