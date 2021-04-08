package craftout;

import craftout.ecs.EntityComponentSystem;
import craftout.engine.Engine;
import craftout.systems.BallSystem;
import craftout.systems.BrickSystem;
import craftout.systems.DrawSystem;
import craftout.systems.HudSystem;
import craftout.systems.LevelSystem;
import craftout.systems.PaddleSystem;
import craftout.systems.PhysicsSystem;

public final class Craftout {

    public static final int WINDOW_WIDTH = 640;

    public static final int WINDOW_HEIGHT = 360;

    public static final String TITLE = "Craftout";

    Craftout() {
    }

    public static void main(String[] args) {
        Engine engine = Engine.create(WINDOW_WIDTH, WINDOW_HEIGHT, TITLE);
        EntityComponentSystem entityComponentSystem = engine.getEntityComponentSystem();
        entityComponentSystem.add(new LevelSystem(engine));
        entityComponentSystem.add(new PaddleSystem());
        entityComponentSystem.add(new BallSystem());
        entityComponentSystem.add(new HudSystem());
        entityComponentSystem.add(new BrickSystem(engine));
        entityComponentSystem.add(new PhysicsSystem());
        entityComponentSystem.add(new DrawSystem(engine.getWindow(), false));

        engine.run();
    }

}
