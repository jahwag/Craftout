package craftout.components;

import craftout.ecs.Component;
import craftout.geometry.Vector2D;
import lombok.Data;

@Data
public class Ball implements Component {

    boolean fired;

    Vector2D levelVelocity;

}
