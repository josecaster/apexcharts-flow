package sr.we.views;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Arrays;

public abstract class TableLayout extends Scroller {

    private final VerticalLayout actionLayout;
    private final VerticalLayout layout;
    private final Button createBtn;

    public TableLayout() {
        layout = new VerticalLayout();
        setContent(layout);
        setScrollDirection(ScrollDirection.VERTICAL);
        getStyle().set("border-bottom", "1px solid var(--lumo-contrast-20pct)").set("padding", "var(--lumo-space-m)");
        createBtn = new Button(getTranslation("sr.we.create"));
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout horizontalLayout = new HorizontalLayout(createBtn);
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actionLayout = new VerticalLayout(horizontalLayout, new Hr());
        add(actionLayout);

        createBtn.addClickListener(f -> onCreateClick());
    }

    public void add(Component... components) {
        layout.add(components);
    }

    public void setJustifyContentMode(FlexComponent.JustifyContentMode justifyContentMode) {
        layout.setJustifyContentMode(justifyContentMode);
    }

    protected void setAlignItems(FlexComponent.Alignment center) {
        layout.setAlignItems(center);
    }



    protected void erase(HasValue... comp) {
        Arrays.stream(comp).forEach(cl -> cl.clear());
    }



    protected abstract void onCreateClick();
}
