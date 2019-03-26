package by.kostevich.lightinjector.impl;

import by.kostevich.lightinjector.LightInjectionModule;
import by.kostevich.lightinjector.annotations.LightModuleImport;
import by.kostevich.lightinjector.impl.bean.ComponentDefinition;
import by.kostevich.lightinjector.impl.bean.InjectContext;
import by.kostevich.lightinjector.impl.bean.PropertyValuesHolder;
import by.kostevich.lightinjector.impl.component.ModuleComponentsReader;
import by.kostevich.lightinjector.impl.properties.ModulePropertiesReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class InjectContextCreator {

    public static InjectContext createContext(LightInjectionModule mainModule) {
        Set<LightInjectionModule> allModules = loadWithImportedModules(mainModule);

        Set<ComponentDefinition> componentDefinitions = new HashSet<>();
        Set<PropertyValuesHolder> propertyValues = new HashSet<>();

        allModules.forEach(module -> {
            componentDefinitions.addAll(ModuleComponentsReader.readComponents(module));
            propertyValues.addAll(ModulePropertiesReader.readProperties(module));
        });

        InjectContext injectContext = new InjectContext();
        injectContext.setModule(mainModule);
        injectContext.setComponentDefinitions(componentDefinitions);
        injectContext.setPropertyValues(propertyValues);
        injectContext.setLightInjector(createLightInjector());

        return injectContext;
    }

    private static Set<LightInjectionModule> loadWithImportedModules(LightInjectionModule mainModule) {
        Set<LightInjectionModule> allModules = new HashSet<>();

        Set<LightInjectionModule> importedModules = getImportedModulesClasses(mainModule.getClass()).stream()
                .map(importedModuleClass -> {
                    try {
                        Constructor<? extends LightInjectionModule> constructor = importedModuleClass.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        return constructor.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());

        allModules.addAll(importedModules);
        allModules.add(mainModule);

        allModules.forEach(module -> {
            try {
                Method configureMethod = module.getClass().getDeclaredMethod("configureInjections");
                configureMethod.setAccessible(true);
                configureMethod.invoke(module);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return allModules;
    }

    private static Set<Class<? extends LightInjectionModule>> getImportedModulesClasses(
            Class<? extends LightInjectionModule> moduleClass) {

        LightModuleImport moduleImportAnnotation = moduleClass.getAnnotation(LightModuleImport.class);
        if (moduleImportAnnotation == null) {
            return Collections.emptySet();
        }
        Set<Class<? extends LightInjectionModule>> importedModulesClasses = new HashSet<>();
        Arrays.stream(moduleImportAnnotation.value()).forEach(importedModule -> {
            importedModulesClasses.addAll(getImportedModulesClasses(importedModule));
            importedModulesClasses.add(importedModule);
        });
        return importedModulesClasses;
    }

    private static LightInjectorImpl createLightInjector() {
        try {
            Constructor<LightInjectorImpl> constructor = LightInjectorImpl.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
