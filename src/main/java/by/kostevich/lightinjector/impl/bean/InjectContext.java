package by.kostevich.lightinjector.impl.bean;

import by.kostevich.lightinjector.LightInjectionModule;
import by.kostevich.lightinjector.impl.LightInjectorImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InjectContext {

    private LightInjectionModule module;
    private Set<ComponentDefinition> componentDefinitions;
    private Set<PropertyValuesHolder> propertyValues;
    private Map<UniqueComponentId, Object> components = new HashMap<>();
    private LightInjectorImpl lightInjector;

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

    public Set<PropertyValuesHolder> getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(Set<PropertyValuesHolder> propertyValues) {
        this.propertyValues = propertyValues;
    }

    public void addComponent(UniqueComponentId componentId, Object component) {
        components.put(componentId, component);
    }

    public Map<UniqueComponentId, Object> getComponents() {
        return components;
    }

    public LightInjectorImpl getLightInjector() {
        return lightInjector;
    }

    public void setLightInjector(LightInjectorImpl lightInjector) {
        this.lightInjector = lightInjector;
    }
}
