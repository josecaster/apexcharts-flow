package sr.we.ui.components;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;

/**
 * A Designer generated component for the my-legend template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("my-fieldset")
@JsModule("./src/views/components/my-fieldset.ts")
public class MyFieldset extends LitTemplate implements HasSize {

    @Id("legend")
    private Legend legend;

    @Id("div")
    private Div div;

    /**
     * Creates a new MyLegend.
     */
    public MyFieldset(String caption) {
        // You can initialise any data required for the connected UI components here.
        legend.setCaption(caption);
        getElement().appendChild(legend.getElement());
        div = new Div();
        getElement().appendChild(div.getElement());
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
