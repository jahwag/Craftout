package craftout.exceptions;

import craftout.ecs.Component;
import craftout.ecs.Entity;

public class ComponentNotFoundException extends RuntimeException {

    public ComponentNotFoundException(Class<? extends Component> componentClass, Entity entity) {
        super(String.format("Component with type %s not found on entity with id %s", componentClass.getSimpleName(), entity.id()));
    }
}
