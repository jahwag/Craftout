package craftout.components;

import com.mojang.sdk.SpriteType;
import craftout.ecs.Component;
import lombok.Data;

@Data
public class Sprite implements Component {

    private final SpriteType type;

    private final int width;

    private final int height;

    private final int offsetX;

    private final int offsetY;

    int id = -1;

}
