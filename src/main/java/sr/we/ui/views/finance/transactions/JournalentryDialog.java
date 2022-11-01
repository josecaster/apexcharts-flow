package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import sr.we.ui.components.MyDialog;

public class JournalentryDialog extends MyDialog {

    public JournalentryDialog(Long businessId) {
        setSizeFull();
        setHeaderTitle("Journal Entry");
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> {
            this.close();
        });
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        this.getHeader().add(closeButton);


        Button cancelButton = new Button("Cancel", (e) -> this.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        this.getFooter().add(cancelButton);

        Button continueBtn = new Button("Save", (e) -> {
//            executable.build(getVO());
            this.close();
        });
        continueBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        this.getFooter().add(continueBtn);

        JournalentryView journalentryView = new JournalentryView();
        journalentryView.setBusinessId(businessId);
        add(journalentryView);
    }
}
