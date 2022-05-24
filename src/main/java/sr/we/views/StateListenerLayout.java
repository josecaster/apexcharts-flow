package sr.we.views;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Arrays;

public abstract class StateListenerLayout extends VerticalLayout {

    private final VerticalLayout actionLayout;
    private Button saveBtn, discardBtn;
    private boolean stateChanged;

    public StateListenerLayout() {
        saveBtn = new Button(getTranslation("sr.we.save"));
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        discardBtn = new Button((getTranslation("sr.we.discard")));
        discardBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        HorizontalLayout horizontalLayout = new HorizontalLayout(discardBtn ,saveBtn);
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actionLayout = new VerticalLayout(horizontalLayout, new Hr());
        add(actionLayout);
        stateChanged(false, false);

        saveBtn.addClickListener(f -> onSave());
        discardBtn.addClickListener(f -> onDiscard());
    }

    protected void stateChanged(boolean enable, boolean check) {
        if(check) {
            if(enable==stateChanged){
                return;
            }
        }
            actionLayout.setVisible(enable);
            saveBtn.setEnabled(enable);
            stateChanged = enable;

    }

    public void state(Component... components) {
        Arrays.stream(components).forEach(f -> {
            if(AbstractField.class.isAssignableFrom(f.getClass())){
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
