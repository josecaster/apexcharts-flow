package sr.we.ui.views;

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
import sr.we.CustomNotificationHandler;
import sr.we.shekelflowcore.exception.SuccessThrowable;

import java.util.Arrays;

public abstract class StateListenerLayout extends Scroller {

    protected final VerticalLayout actionLayout;
    private final VerticalLayout layout;
    private final Button saveBtn;
    private boolean stateChanged;

    public StateListenerLayout() {
        layout = new VerticalLayout();

        setScrollDirection(Scroller.ScrollDirection.VERTICAL);
//        getStyle().set("border-bottom", "1px solid var(--lumo-contrast-20pct)").set("padding", "var(--lumo-space-m)");
        saveBtn = new Button(getTranslation("sr.we.save"));
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button discardBtn = new Button((getTranslation("sr.we.discard")));
        discardBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        HorizontalLayout horizontalLayout = new HorizontalLayout(discardBtn, saveBtn);
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        horizontalLayout.setPadding(false);
        horizontalLayout.setMargin(false);
        actionLayout = new VerticalLayout(horizontalLayout);
        actionLayout.setPadding(false);
        actionLayout.setMargin(false);
        setContent(new VerticalLayout(layout, new Hr(), actionLayout));
        stateChanged(false, false);

        saveBtn.addClickListener(f -> {
            onSave();
            CustomNotificationHandler.notify_(new SuccessThrowable());
        });
        discardBtn.addClickListener(f -> onDiscard());

        layout.setPadding(false);
        layout.setMargin(false);
    }

    public void add(Component... components) {
        layout.add(components);
    }

    public void setJustifyContentMode(FlexComponent.JustifyContentMode justifyContentMode) {
        layout.setJustifyContentMode(justifyContentMode);
    }

    protected void setCenterItems() {
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
    }

    protected void stateChanged(boolean enable, boolean check) {
        if (check) {
            if (enable == stateChanged) {
                return;
            }
        }
//        actionLayout.setVisible(enable);
        saveBtn.setEnabled(enable);
        stateChanged = enable;
        if (enable) {
//            saveBtn.focus();
//            saveBtn.scrollIntoView();
        }
    }

    protected void erase(@SuppressWarnings("rawtypes") HasValue... comp) {
        Arrays.stream(comp).forEach(HasValue::clear);
    }

    public void state(Component... components) {
        Arrays.stream(components).forEach(f -> {
            if (AbstractField.class.isAssignableFrom(f.getClass())) {
                ((AbstractField<?, ?>) f).addValueChangeListener(g -> {
                    listenToState();
                });
            }
        });
    }

    protected void listenToState() {
        boolean valid = validate();
        stateChanged(valid, true);
    }

    public void stateArray(Component[] components) {
        Arrays.stream(components).forEach(f -> {
            if (AbstractField.class.isAssignableFrom(f.getClass())) {
                ((AbstractField<?, ?>) f).addValueChangeListener(g -> {
                    listenToState();
                });
            }
        });
    }

    protected abstract void onSave();

    protected abstract void onDiscard();

    protected abstract boolean validate();
}
