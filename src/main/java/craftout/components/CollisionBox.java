package craftout.components;

import craftout.ecs.Component;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollisionBox implements Component {

    float x;

    float y;

    int width;

    int height;

    Behavior behavior = Behavior.FROZEN;

    boolean collided;

    public enum Behavior {
        FROZEN,
        GOES_FLYING,
    }

}
