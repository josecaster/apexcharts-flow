package sr.we.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Arrays;

public abstract class TableLayout extends VerticalLayout {

    private final VerticalLayout actionLayout;
    protected final VerticalLayout layout;
    private final Button createBtn;

    public TableLayout() {
        layout = new VerticalLayout();
        super.add(layout);
//        setScrollDirection(ScrollDirection.VERTICAL);
//        getStyle().set("border-bottom", "1px solid var(--lumo-contrast-20pct)").set("padding", "var(--lumo-space-m)");
        createBtn = new Button(getTranslation("sr.we.create"));
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createVisible(false);
        HorizontalLayout horizontalLayout = new HorizontalLayout(createBtn);
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actionLayout = new VerticalLayout(horizontalLayout);
        add(actionLayout);

        createBtn.addClickListener(f -> onCreateClick());
        setPadding(false);
        setMargin(false);
        layout.setPadding(false);
        layout.setMargin(false);
    }

    protected void createVisible(boolean visible) {
        createBtn.setVisible(visible);
    }

    public void add(Component... components) {
        layout.add(components);
    }

    public void setJustifyContentMode(FlexComponent.JustifyContentMode justifyContentMode) {
        layout.setJustifyContentMode(justifyContentMode);
    }

    public void setAlignItems(FlexComponent.Alignment center) {
        layout.setAlignItems(center);
    }



    protected void erase(HasValue... comp) {
        Arrays.stream(comp).forEach(cl -> cl.clear());
    }



    protected abstract void onCreateClick();

    public Button getCreateBtn() {
        return createBtn;
    }
}
