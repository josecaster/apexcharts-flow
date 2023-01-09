package sr.we.ui.components;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;

public class MyIcon extends Image {

    public MyIcon(String src, String alt) {
        super(src,alt);
//        img.setWidth("21px");
//        setIcon(img);
//        addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
//        getElement().getStyle().set("border","solid 1px");
        setWidth("25px");
        setHeight("25px");
    }
}
