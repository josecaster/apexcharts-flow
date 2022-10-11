package sr.we.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;

public class ArrowDownButton extends Button {


    public ArrowDownButton() {
        Image img = new Image("icons/icons8_expand_arrow_48px.png", "icon by Icons8");
        img.setWidth("21px");
        setIcon(img);
        addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        getElement().getStyle().set("border","solid 1px");
        setWidth("25px");
        setHeight("25px");
    }
}
