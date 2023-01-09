package sr.we.ui.components.buttons;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class FilterButton extends Image{

    public FilterButton() {
        super("icons/icons8_conversion_48px.png", "icon by Icons8");
        setWidth("24px");
        setHeight("24px");
        //getElement().getStyle().set("margin-inline-end", "var(--lumo-space-s)");
        //getElement().getStyle().set("margin-top", "calc(var(--lumo-space-xs) * 0.5)");

//        LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon();
        addClassName("clickable-layout");
//        getElement().getStyle().set("border-radius","100px");
        getElement().getStyle().set("background","var(--lumo-contrast-10pct)");
        getElement().getStyle().set("padding","2px");
        addClassName(LumoUtility.BoxShadow.SMALL);
//        setVisible(false);

    }
}
