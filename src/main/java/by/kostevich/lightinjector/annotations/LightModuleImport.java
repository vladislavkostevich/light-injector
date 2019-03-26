package by.kostevich.lightinjector.annotations;

import by.kostevich.lightinjector.LightInjectionModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LightModuleImport {

    Class<? extends LightInjectionModule>[] value();

}
