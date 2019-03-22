package by.kostevich.lightinjector.impl;

import by.kostevich.lightinjector.impl.bean.DependencyDefinition;
import by.kostevich.lightinjector.impl.bean.ComponentDefinition;
import by.kostevich.lightinjector.impl.bean.InjectContext;
import by.kostevich.lightinjector.impl.bean.UniqueComponentId;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class ContextLookup {

    public static ComponentDefinition findComponentConfiguration(
            InjectContext context,
            DependencyDefinition dependencyDefinition) {

        Set<ComponentDefinition> componentConfigurations = context.getComponentDefinitions();

        List<ComponentDefinition> configurationByDirectType = componentConfigurations.stream()
                .filter(componentConfiguration ->
                        componentConfiguration.getComponentId().getComponentClass().equals(dependencyDefinition.getDependencyClass()))
                .collect(Collectors.toList());
        if (!configurationByDirectType.isEmpty()) {
            return findComponentConfigurationByName(configurationByDirectType, dependencyDefinition);
        }

        List<ComponentDefinition> configurationBySuperClasses = componentConfigurations.stream()
                .filter(componentConfiguration ->
                        componentConfiguration.getComponentSuperClasses().contains(dependencyDefinition.getDependencyClass()))
                .collect(Collectors.toList());
        if (!configurationBySuperClasses.isEmpty()) {
            return findComponentConfigurationByName(configurationBySuperClasses, dependencyDefinition);
        }

        List<ComponentDefinition> configurationByInterfaces = componentConfigurations.stream()
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

    private static ComponentDefinition findComponentConfigurationByName(
            List<ComponentDefinition> configurations, DependencyDefinition dependencyDefinition) {

        String dependencyName = dependencyDefinition.getDependencyName();
        if (dependencyName == null) {
            if (configurations.size() > 1) {
                throw new IllegalStateException(
                        format("There is more than 1 component of the class %s", dependencyDefinition.getDependencyClass()));
            }
            return configurations.get(0);
        }

        List<ComponentDefinition> configurationsByName = configurations.stream()
                .filter(componentConfiguration ->
                        componentConfiguration.getComponentId().getComponentName().equalsIgnoreCase(dependencyDefinition.getDependencyName()))
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

    public static Object findComponent(InjectContext context, DependencyDefinition dependencyDefinition) {
        Map<UniqueComponentId, Object> components = context.getComponents();

        ComponentDefinition componentConfiguration = findComponentConfiguration(context, dependencyDefinition);
        return components.get(componentConfiguration.getComponentId());
    }

}
