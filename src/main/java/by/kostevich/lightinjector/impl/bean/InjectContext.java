package by.kostevich.lightinjector.impl.bean;

import by.kostevich.lightinjector.LightInjectionModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InjectContext {

    private LightInjectionModule module;
    private Set<ComponentDefinition> componentDefinitions;
    private Map<UniqueComponentId, Object> components = new HashMap<>();

    public LightInjectionModule getModule() {
        return module;
    }

    public void setModule(LightInjectionModule module) {
        this.module = module;
    }

    public Set<ComponentDefinition> getComponentDefinitions() {
        return componentDefinitions;
    }

    public void setComponentDefinitions(Set<ComponentDefinition> componentDefinitions) {
        this.componentDefinitions = componentDefinitions;
    }

    public void addComponent(UniqueComponentId componentId, Object component) {
        components.put(componentId, component);
    }

    public Map<UniqueComponentId, Object> getComponents() {
        return components;
    }
}
