package by.kostevich.lightinjector.bean;

import by.kostevich.lightinjector.LightInjectionModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class LightInjectContext {

    private LightInjectionModule module;
    private Set<LightComponentConfiguration> componentConfigurations;
    private Map<Class<?>, Object> components = new HashMap<>();

    public LightInjectionModule getModule() {
        return module;
    }

    public void setModule(LightInjectionModule module) {
        this.module = module;
    }

    public void setComponentConfigurations(Set<LightComponentConfiguration> componentConfigurations) {
        this.componentConfigurations = componentConfigurations;
    }

    public void addComponent(Class<?> componentClass, Object component) {
        components.put(componentClass, component);
    }

    public Map<Class<?>, Object> getComponents() {
        return components;
    }

    public LightComponentConfiguration findComponentConfiguration(DependencyDefinition dependencyDefinition) {
        List<LightComponentConfiguration> configurationByDirectType = componentConfigurations.stream()
                .filter(componentConfiguration ->
                        componentConfiguration.getComponentClass().equals(dependencyDefinition.getDependencyClass()))
                .collect(Collectors.toList());
        if (!configurationByDirectType.isEmpty()) {
            return findComponentConfigurationByName(configurationByDirectType, dependencyDefinition);
        }

        List<LightComponentConfiguration> configurationBySuperClasses = componentConfigurations.stream()
                .filter(componentConfiguration ->
                        componentConfiguration.getComponentSuperClasses().contains(dependencyDefinition.getDependencyClass()))
                .collect(Collectors.toList());
        if (!configurationBySuperClasses.isEmpty()) {
            return findComponentConfigurationByName(configurationBySuperClasses, dependencyDefinition);
        }

        List<LightComponentConfiguration> configurationByInterfaces = componentConfigurations.stream()
                .filter(componentConfiguration ->
                        componentConfiguration.getComponentInterfaces().contains(dependencyDefinition.getDependencyClass()))
                .collect(Collectors.toList());
        if (!configurationByInterfaces.isEmpty()) {
            return findComponentConfigurationByName(configurationByInterfaces, dependencyDefinition);
        }

        throw new IllegalStateException(
                format("Component is not found in the LightInjector context by class %s and name %s",
                        dependencyDefinition.getDependencyClass(),
                        dependencyDefinition.getDependencyName()));
    }

    private LightComponentConfiguration findComponentConfigurationByName(
            List<LightComponentConfiguration> configurations, DependencyDefinition dependencyDefinition) {

        String dependencyName = dependencyDefinition.getDependencyName();
        if (dependencyName == null) {
            if (configurations.size() > 1) {
                throw new IllegalStateException(
                        format("There is more than 1 component of the class %s", dependencyDefinition.getDependencyClass()));
            }
            return configurations.get(0);
        }

        List<LightComponentConfiguration> configurationsByName = configurations.stream()
                .filter(componentConfiguration ->
                        componentConfiguration.getComponentName().equalsIgnoreCase(dependencyDefinition.getDependencyName()))
                .collect(Collectors.toList());
        if (configurationsByName.isEmpty()) {
            throw new IllegalStateException(
                    format("No components found by name %s with class %s",
                            dependencyDefinition.getDependencyName(),
                            dependencyDefinition.getDependencyClass()));
        }
        if (configurationsByName.size() > 1) {
            throw new IllegalStateException(
                    format("There is more than 1 component found by name %s with class %s",
                            dependencyDefinition.getDependencyName(),
                            dependencyDefinition.getDependencyClass()));
        }
        return configurationsByName.get(0);
    }

    public Object findComponent(DependencyDefinition dependencyDefinition) {
        LightComponentConfiguration componentConfiguration = findComponentConfiguration(dependencyDefinition);
        return components.get(componentConfiguration.getComponentClass());
    }
}
