package craftout.engine;

import com.mojang.sdk.Window;
import craftout.components.HudText;
import craftout.components.Sprite;
import craftout.ecs.Entity;
import craftout.ecs.EntityComponentSystem;
import craftout.exceptions.LwjglException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class Engine {

    private static final double TICKS_PER_SECOND = 1d / 60d;

    private final EntityComponentSystem entityComponentSystem;

    private final Window window;

    Engine(Window window) {
        this.entityComponentSystem = new EntityComponentSystem();
        this.window = window;
    }

    public static Engine create(int width, int height, String title) {
        checkArgument(width % 2 == 0, "width must be a power of 2");
        checkArgument(height % 2 == 0, "height must be a power of 2");
        checkArgument(!title.isBlank(), "title cannot be empty");
        try {
            Window window = new Window(width, height, title);
            return new Engine(window);
        } catch (IOException e) {
            throw new LwjglException("Lwjgl failure", e);
        }
    }

    public Window getWindow() {
        return window;
    }

    public EntityComponentSystem getEntityComponentSystem() {
        return entityComponentSystem;
    }

    public void run() {
        var previous = window.getSecondsSinceCreation();
        var ticks = 0d;

        while (window.isOpen()) {
            var current = window.getSecondsSinceCreation();
            var elapsed = current - previous;
            previous = current;
            ticks += elapsed;

            while (ticks >= TICKS_PER_SECOND) {
                update();
                ticks -= TICKS_PER_SECOND;
            }

            window.draw();

            var endTime = current + TICKS_PER_SECOND;
            while (endTime > window.getSecondsSinceCreation()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    Thread currentThread = Thread.currentThread();
                    currentThread.interrupt();
                    ex.printStackTrace();
                }
            }
        }
    }

    void update() {
        for (var system : entityComponentSystem.getSystems()) {
            system.update(entityComponentSystem);
        }
    }

    public void removeAllEntities() {
        List<Entity> entities = new LinkedList<>(entityComponentSystem.findAll());
        for (var entity : entities) {
            remove(entity);
        }
    }

    public void remove(Entity entity) {
        for (var sprite : entity.getComponents(Sprite.class)) {
            window.removeSprite(sprite.getId());
        }
        for (var hudText : entity.getComponents(HudText.class)) {
            window.removeText(hudText.getId());
        }

        entityComponentSystem.remove(entity);
    }

}
