package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import sr.we.ContextProvider;
import sr.we.data.controller.PaymentTransactionService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.vo.PaymentTransactionVO;
import sr.we.ui.components.MyDialog;

public class JournalentryDialog extends MyDialog {

    private final JournalentryView journalentryView;
    private Executable build;

    public JournalentryDialog(Long businessId) {
//        setMaxWidth("1000px");
//        setWidth("100%");
        setSizeFull();
        setHeaderTitle("Journal Entry");
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> {
            this.close();
        });
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        this.getHeader().add(closeButton);

        journalentryView = new JournalentryView();
        journalentryView.setBusinessId(businessId);
        add(journalentryView);


        Button cancelButton = new Button("Cancel", (e) -> this.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        this.getFooter().add(cancelButton);

        Button continueBtn = new Button("Save", (e) -> {
            PaymentTransactionVO paymentTransactionVO = journalentryView.getVO();
            PaymentTransactionService paymentTransactionService = ContextProvider.getBean(PaymentTransactionService.class);
            paymentTransactionService.create(AuthenticatedUser.token(), paymentTransactionVO);
            build.build();
            this.close();
        });
        continueBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        this.getFooter().add(continueBtn);


    }

    public void setRefresh(Executable build) {
        this.build = build;
    }
}
