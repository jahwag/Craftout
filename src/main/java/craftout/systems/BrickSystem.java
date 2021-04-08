package craftout.systems;

import craftout.components.Brick;
import craftout.components.CollisionBox;
import craftout.components.Score;
import craftout.ecs.EntityComponentSystem;
import craftout.ecs.GameSystem;
import craftout.engine.Engine;

import java.util.LinkedList;

public final class BrickSystem implements GameSystem {

    private final Engine engine;

    public BrickSystem(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void update(EntityComponentSystem entityComponentSystem) {
        for (var brickEntity : new LinkedList<>(entityComponentSystem.findAll(Brick.class))) {
            var collisionBox = brickEntity.getComponent(CollisionBox.class);

            if (collisionBox.isCollided()) {
                engine.remove(brickEntity);
                updateScore(entityComponentSystem);
            }
        }
    }

    void updateScore(EntityComponentSystem entityComponentSystem) {
        var optionalScoreEntity = entityComponentSystem.find(Score.class);
        if (optionalScoreEntity.isEmpty()) {
            return;
        }

        var scoreEntity = optionalScoreEntity.get();
        var score = scoreEntity.getComponent(Score.class);
        score.setValue(score.getValue() + 100);
    }

}
