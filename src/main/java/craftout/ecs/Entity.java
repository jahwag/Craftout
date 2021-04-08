package craftout.ecs;

import craftout.exceptions.ComponentNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;

public final class Entity {

    private final int id;

    private final Map<Class<?>, List<Component>> components = new HashMap<>();

    public Entity(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentClass) throws ComponentNotFoundException {
        List<Component> matches = components.getOrDefault(componentClass, emptyList());
        if (matches.isEmpty()) {
            throw new ComponentNotFoundException(componentClass, this);
        }

        return (T) matches.get(0);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> List<T> getComponents(Class<T> componentClass) {
        return (List<T>) components.getOrDefault(componentClass, emptyList());
    }

    void addComponent(Component component, Class<? extends Component> componentClass) {
        components.computeIfAbsent(componentClass, k -> new ArrayList<>())
                  .add(component);
    }

    public boolean contains(Class<? extends Component> componentClass) {
        return components.containsKey(componentClass);
    }

    public Set<Class<?>> getComponentTypes() {
        return components.keySet();
    }
}
