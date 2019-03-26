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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static java.lang.String.format;

public class InjectContextCreator {

    public static InjectContext createContext(LightInjectionModule mainModule) {
        Set<LightInjectionModule> allModules = loadWithImportedModules(mainModule);

        Set<ComponentDefinition> componentDefinitions = new HashSet<>();
        Set<PropertyValuesHolder> propertyValues = new HashSet<>();

        long modulesConfigsReadingStartTime = System.currentTimeMillis();
        allModules.forEach(module -> {
            componentDefinitions.addAll(ModuleComponentsReader.readComponents(module));
            propertyValues.addAll(ModulePropertiesReader.readProperties(module));
        });
        System.out.println(format("[DEBUG] LightInject - Modules Configurations Loaded in %s ms", System.currentTimeMillis() - modulesConfigsReadingStartTime));

        InjectContext injectContext = new InjectContext();
        injectContext.setModule(mainModule);
        injectContext.setComponentDefinitions(componentDefinitions);
        injectContext.setPropertyValues(propertyValues);
        injectContext.setLightInjector(createLightInjector());

        return injectContext;
    }

    private static Set<LightInjectionModule> loadWithImportedModules(LightInjectionModule mainModule) {
        try {
            Set<LightInjectionModule> allModules = new HashSet<>();

            for (Class<? extends LightInjectionModule> importedModuleClass : getImportedModulesClasses(mainModule.getClass())) {
                Constructor<? extends LightInjectionModule> constructor = importedModuleClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                allModules.add(constructor.newInstance());
            }
            allModules.add(mainModule);

            for (LightInjectionModule module : allModules) {
                long moduleConfigurationStartTime = System.currentTimeMillis();

                Class<? extends LightInjectionModule> moduleClass = module.getClass();
                Method configureMethod = moduleClass.getDeclaredMethod("configureInjections");
                configureMethod.setAccessible(true);
                configureMethod.invoke(module);

                System.out.println(format(
                        "[DEBUG] LightInject - Injections Configured for Module %s in %s ms",
                        moduleClass.getSimpleName(),
                        System.currentTimeMillis() - moduleConfigurationStartTime
                ));
            }

            return allModules;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Set<Class<? extends LightInjectionModule>> getImportedModulesClasses(
            Class<? extends LightInjectionModule> moduleClass) {

        Set<Class<? extends LightInjectionModule>> importedModulesClasses = new HashSet<>();

        LinkedList<Class<? extends LightInjectionModule>> moduleClassesQueue = new LinkedList<>();
        moduleClassesQueue.add(moduleClass);
        while (!moduleClassesQueue.isEmpty()) {
            Class<? extends LightInjectionModule> currentModuleClass = moduleClassesQueue.poll();
            LightModuleImport moduleImportAnnotation = currentModuleClass.getAnnotation(LightModuleImport.class);
            if (moduleImportAnnotation == null) {
                continue;
            }

            Class<? extends LightInjectionModule>[] currentImportedModulesClasses = moduleImportAnnotation.value();
            for (Class<? extends LightInjectionModule> currentImportedModulesClass : currentImportedModulesClasses) {
                importedModulesClasses.add(currentImportedModulesClass);
                moduleClassesQueue.push(currentImportedModulesClass);
            }
        }

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
