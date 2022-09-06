package sr.we.ui.components;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Div;

@Tag("fieldset")
public class FieldSet extends Component implements HasSize {


    private final Div div;

    public FieldSet(String caption) {
        Legend legend = new Legend(caption);
        getElement().appendChild(legend.getElement());
        div = new Div();
        getElement().appendChild(div.getElement());
        getElement().getStyle().set("border", "dashed");
        getElement().getStyle().set("border-width", "thin");
        getElement().getStyle().set("border-color", "var(--lumo-shade-10pct)");
        getElement().getStyle().set("padding-right", "0px");
        getElement().getStyle().set("padding-left", "0px");
        getElement().getStyle().set("margin", "0px");
        getElement().getStyle().set("border-radius", "calc(var(--lumo-size-m) / 4)");

    }

    public Div getContent(){
        return div;
    }


    @Override
    public void setWidth(String width) {
        getElement().getStyle().set("width", width);
    }

    @Override
    public void setHeight(String height) {
        getElement().getStyle().set("height", height);
    }

    @Override
    public void setWidthFull() {
        getElement().getStyle().set("width", "100%");
    }

    @Override
    public void setHeightFull() {
        getElement().getStyle().set("height", "100%");
    }

    @Override
    public void setSizeFull() {
        setWidthFull();
        setHeightFull();
    }
}
