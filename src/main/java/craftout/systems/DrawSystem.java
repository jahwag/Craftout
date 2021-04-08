package craftout.systems;

import com.mojang.sdk.Window;
import craftout.components.CollisionBox;
import craftout.components.HudText;
import craftout.components.Sprite;
import craftout.components.Translation;
import craftout.ecs.EntityComponentSystem;
import craftout.ecs.GameSystem;

public final class DrawSystem implements GameSystem {

    private final Window window;

    private final boolean debug;

    public DrawSystem(Window window, boolean debug) {
        this.window = window;
        this.debug = debug;
    }

    @Override
    public void update(EntityComponentSystem entityComponentSystem) {
        updateSprites(entityComponentSystem);
        updateHudTexts(entityComponentSystem);

        if (debug) {
            updateDebugLines(entityComponentSystem);
        }
    }

    private void updateSprites(EntityComponentSystem entityComponentSystem) {
        for (var spriteEntity : entityComponentSystem.findAll(Sprite.class)) {
            for (var sprite : spriteEntity.getComponents(Sprite.class)) {
                var translation = spriteEntity.getComponent(Translation.class);

                if (sprite.getId() == -1) {
                    int id = window.createSprite(sprite.getType(), 0, 0);
                    sprite.setId(id);
                }

                window.moveSprite(sprite.getId(), translation.getCenterX() + sprite.getOffsetX(), translation.getCenterY() + sprite.getOffsetY());
            }
        }
    }

    private void updateHudTexts(EntityComponentSystem entityComponentSystem) {
        for (var hudTextEntity : entityComponentSystem.findAll(HudText.class)) {
            var hudText = hudTextEntity.getComponent(HudText.class);
            var translation = hudTextEntity.getComponent(Translation.class);

            if (hudText.getId() == -1) {
                var id = window.createText(hudText.getText(), hudText.getSize(), translation.getCenterX(), translation.getCenterY());
                hudText.setId(id);
            }

            window.updateText(hudText.getId(), hudText.getText());
        }
    }

    private void updateDebugLines(EntityComponentSystem entityComponentSystem) {
        for (var entity : entityComponentSystem.findAll(CollisionBox.class)) {
            for (var boundingBox : entity.getComponents(CollisionBox.class)) {
                window.debugDrawLine(boundingBox.getX(), boundingBox.getY(), boundingBox.getX(), boundingBox.getY() + boundingBox.getHeight()); //left
                window.debugDrawLine(boundingBox.getX(), boundingBox.getY(), boundingBox.getX() + boundingBox.getWidth(), boundingBox.getY());//top
                window.debugDrawLine(boundingBox.getX() + boundingBox.getWidth(), boundingBox.getY(), boundingBox.getX() + boundingBox.getWidth(), boundingBox.getY() + boundingBox.getHeight());//right
                window.debugDrawLine(boundingBox.getX(), boundingBox.getY() + boundingBox.getHeight(), boundingBox.getX() + boundingBox.getWidth(), boundingBox.getY() + boundingBox.getHeight());//bottom
            }
        }
    }

}
