package craftout.components;

import craftout.Craftout;
import craftout.ecs.Component;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Translation implements Component {

    float centerX;

    float centerY;

    public boolean isOffscreen() {
        return getCenterY() > Craftout.WINDOW_HEIGHT;
    }

}
