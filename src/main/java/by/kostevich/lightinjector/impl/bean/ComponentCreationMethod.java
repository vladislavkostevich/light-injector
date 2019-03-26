package by.kostevich.lightinjector.impl.bean;

import by.kostevich.lightinjector.LightInjectionModule;

import java.lang.reflect.Method;

public class ComponentCreationMethod {

    private Method javaMethod;
    private LightInjectionModule module;

    public ComponentCreationMethod(Method javaMethod, LightInjectionModule module) {
        this.javaMethod = javaMethod;
        this.module = module;
    }

    public Method getJavaMethod() {
        return javaMethod;
    }

    public void setJavaMethod(Method javaMethod) {
        this.javaMethod = javaMethod;
    }

    public LightInjectionModule getModule() {
        return module;
    }

    public void setModule(LightInjectionModule module) {
        this.module = module;
    }
}
