package sr.we.ui.components;

import com.vaadin.flow.component.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface BreadCrumb {

    Class<? extends Component> parentNavigationTarget() default NONE.class;

    String titleKey() default "";

    class NONE extends Component {

    }
}
