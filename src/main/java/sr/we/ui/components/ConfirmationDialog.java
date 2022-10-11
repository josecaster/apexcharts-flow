package sr.we.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;

public class ConfirmationDialog extends MyDialog {

    private Button continueBtn;
    private Button cancelButton;

    public ConfirmationDialog(String header, String content, Component contentComponent) {
        super();

        init(header, content);
        this.add(contentComponent);
        open();
    }

    public ConfirmationDialog(String header, String content) {
        super();

        init(header, content);
        open();
    }

    private void init(String header, String content) {
        this.setHeaderTitle(header);
        this.add(content);
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> {
            this.close();
            UI.getCurrent().getPage().reload();
        });
        this.setCloseOnOutsideClick(false);
        this.setCloseOnEsc(false);
        this.setModal(true);
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        this.getHeader().add(closeButton);

        cancelButton = new Button("Cancel", (e) -> this.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        this.getFooter().add(cancelButton);

        continueBtn = new Button("Continue", (e) -> this.close());
        continueBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        this.getFooter().add(continueBtn);
    }

    public Button getContinueBtn() {
        return continueBtn;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
}
