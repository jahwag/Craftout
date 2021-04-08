package craftout.components;

import craftout.ecs.Component;
import lombok.Data;

@Data
public class HudText implements Component {

    private final int size;

    int id = -1;

    private String text = "";

}
