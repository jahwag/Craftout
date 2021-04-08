package craftout.ecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;

public final class EntityComponentSystem {

    private final AtomicInteger nextId = new AtomicInteger(1);

    private final Map<Class<?>, GameSystem> systems = new HashMap<>();

    private final List<Entity> entities = new ArrayList<>();

    private final Map<Class<?>, Collection<Entity>> componentMap = new HashMap<>();

    public EntityComponentSystem add(GameSystem system) {
        checkNotNull(system, "system must be non-null");
        systems.put(system.getClass(), system);
        return this;
    }

    public Entity createEntity() {
        Entity entity = new Entity(nextId.getAndIncrement());
        entities.add(entity);
        return entity;
    }

    public void update(Class<? extends GameSystem> systemClass) {
        GameSystem gameSystem = systems.get(systemClass);
        if (gameSystem != null) {
            gameSystem.update(this);
        }
    }

    public Collection<GameSystem> getSystems() {
        return systems.values();
    }

    public Collection<Entity> findAll(Class<? extends Component> componentClass) {
        return unmodifiableCollection(componentMap.getOrDefault(componentClass, emptyList()));
    }

    public Optional<Entity> find(Class<? extends Component> componentClass) {
        return componentMap.getOrDefault(componentClass, emptyList())
                           .stream()
                           .findFirst();
    }

    public EntityComponentSystem add(Entity entity, Component component) {
        entity.addComponent(component, component.getClass());
        Class<? extends Component> componentClass = component.getClass();
        add(entity, componentClass);

        return this;
    }

    private void add(Entity entity, Class<? extends Component> componentClass) {
        componentMap.computeIfAbsent(componentClass, k -> new ArrayList<>())
                    .add(entity);
    }

    public void remove(Entity entity) {
        entities.remove(entity);
        for (var componentClass : entity.getComponentTypes()) {
            componentMap.get(componentClass)
                        .remove(entity);
        }
    }

    public List<Entity> findAll() {
        return entities;
    }

}
