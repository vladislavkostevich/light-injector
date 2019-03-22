package by.kostevich.lightinjector.impl;

import by.kostevich.lightinjector.LightInjectionModule;
import by.kostevich.lightinjector.impl.bean.ComponentDefinition;
import by.kostevich.lightinjector.impl.bean.InjectContext;
import by.kostevich.lightinjector.impl.bean.PropertyValuesHolder;
import by.kostevich.lightinjector.impl.component.ModuleComponentsReader;
import by.kostevich.lightinjector.impl.properties.ModulePropertiesReader;

import java.util.Set;

public class InjectContextCreator {

    public static InjectContext createContext(LightInjectionModule module) {
        Set<ComponentDefinition> componentDefinitions = ModuleComponentsReader.readComponents(module);
        Set<PropertyValuesHolder> propertyValues = ModulePropertiesReader.readProperties(module);

        InjectContext injectContext = new InjectContext();
        injectContext.setModule(module);
        injectContext.setComponentDefinitions(componentDefinitions);
        injectContext.setPropertyValues(propertyValues);

        return injectContext;
    }
}
