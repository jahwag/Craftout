package craftout.components;

import craftout.ecs.Component;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Lives implements Component {

    int remaining;

}
