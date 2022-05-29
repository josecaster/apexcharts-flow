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

public abstract class StateListenerLayout extends Scroller {

    private final VerticalLayout actionLayout;
    private final VerticalLayout layout;
    private final Button saveBtn;
    private final Button discardBtn;
    private boolean stateChanged;

    public StateListenerLayout() {
        layout = new VerticalLayout();
        setContent(layout);
        setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        getStyle().set("border-bottom", "1px solid var(--lumo-contrast-20pct)").set("padding", "var(--lumo-space-m)");
        saveBtn = new Button(getTranslation("sr.we.save"));
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        discardBtn = new Button((getTranslation("sr.we.discard")));
        discardBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        HorizontalLayout horizontalLayout = new HorizontalLayout(discardBtn, saveBtn);
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actionLayout = new VerticalLayout(horizontalLayout, new Hr());
        add(actionLayout);
        stateChanged(false, false);

        saveBtn.addClickListener(f -> onSave());
        discardBtn.addClickListener(f -> onDiscard());
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

    protected void stateChanged(boolean enable, boolean check) {
        if (check) {
            if (enable == stateChanged) {
                return;
            }
        }
        actionLayout.setVisible(enable);
        saveBtn.setEnabled(enable);
        stateChanged = enable;
        if (enable) {
            saveBtn.focus();
            saveBtn.scrollIntoView();
        }
    }

    protected void erase(HasValue... comp) {
        Arrays.stream(comp).forEach(cl -> cl.clear());
    }

    public void state(Component... components) {
        Arrays.stream(components).forEach(f -> {
            if (AbstractField.class.isAssignableFrom(f.getClass())) {
                ((AbstractField<?, ?>) f).addValueChangeListener(g -> {
                    boolean valid = validate();
                    stateChanged(valid, true);
                });
            }
        });
    }

    protected abstract void onSave();

    protected abstract void onDiscard();

    protected abstract boolean validate();
}
