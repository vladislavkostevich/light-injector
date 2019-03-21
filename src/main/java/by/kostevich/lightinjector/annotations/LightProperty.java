package by.kostevich.lightinjector.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface LightProperty {

    String value();

    String defaultValue() default  "LIGHT_INJECTOR_DEFAULT_PROPERTY_VALUE";
}
