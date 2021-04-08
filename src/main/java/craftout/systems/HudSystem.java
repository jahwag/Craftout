package craftout.systems;

import craftout.components.HudText;
import craftout.components.Lives;
import craftout.components.Score;
import craftout.ecs.EntityComponentSystem;
import craftout.ecs.GameSystem;

public final class HudSystem implements GameSystem {

    private static final String LIVES_FORMAT = "LIVES %s";

    private static final String SCORE_FORMAT = "SCORE %s";

    @Override
    public void update(EntityComponentSystem entityComponentSystem) {
        updateScore(entityComponentSystem);

        updateLives(entityComponentSystem);
    }

    private void updateScore(EntityComponentSystem entityComponentSystem) {
        var optionalScoreEntity = entityComponentSystem.find(Score.class);
        if (optionalScoreEntity.isEmpty()) {
            return;
        }

        var scoreEntity = optionalScoreEntity.get();
        var score = scoreEntity.getComponent(Score.class);

        var hudText = scoreEntity.getComponent(HudText.class);
        hudText.setText(String.format(SCORE_FORMAT, score.getValue()));
    }

    private void updateLives(EntityComponentSystem entityComponentSystem) {
        var optional = entityComponentSystem.find(Lives.class);
        if (optional.isEmpty()) {
            return;
        }

        var entity = optional.get();
        var lives = entity.getComponent(Lives.class);

        var hudText = entity.getComponent(HudText.class);
        hudText.setText(String.format(LIVES_FORMAT, lives.getRemaining()));
    }

}
