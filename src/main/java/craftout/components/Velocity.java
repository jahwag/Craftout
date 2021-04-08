package craftout.components;

import craftout.ecs.Component;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Velocity implements Component {

    double x;

    double y;

    public boolean isZero() {
        return x == 0 && y == 0;
    }

}
