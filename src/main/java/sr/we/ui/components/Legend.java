package sr.we.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;

@Tag("legend")
public class Legend extends Component {
    public Legend(String caption) {

        setCaption(caption);
        getElement().getStyle().set("color","var(--lumo-secondary-text-color)");
        getElement().getStyle().set("font-size","var(--lumo-font-size-xs)");

    }

    public void setCaption(String caption) {
        getElement().setText(caption);
    }

    public Legend() {

        getElement().getStyle().set("color","var(--lumo-secondary-text-color)");
        getElement().getStyle().set("font-size","var(--lumo-font-size-xs)");

    }
}
