package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import sr.we.ContextProvider;
import sr.we.data.controller.UserAccessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.PaymentTransaction;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.TransactionsPrivilege;
import sr.we.ui.components.MyDialog;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDialog extends MyDialog {


    private final TransactionForm transactionForm;

    public TransactionDialog(BigDecimal rest, LocalDate initDate, Long businessId, Currency fromCurrency, Currency selectedCurrency, Reference reference, Long referenceId) {

        setHeaderTitle("Record a payment");
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> {
            close();
            UI.getCurrent().getPage().reload();
        });
        setCloseOnOutsideClick(false);
        setCloseOnEsc(false);
        setModal(true);
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getHeader().add(closeButton);

        Button cancelButton = new Button("Cancel"/*, (e) -> close()*/);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        getFooter().add(cancelButton);

        Button saveButton = new Button("Save", (e) -> close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        getFooter().add(saveButton);
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new TransactionsPrivilege(), Privileges.INSERT);
        saveButton.setVisible(hasAccess);


        transactionForm = new TransactionForm(rest, initDate, businessId, fromCurrency, selectedCurrency, reference, referenceId);
        add(transactionForm);

        saveButton.addClickListener(g -> {
            PaymentTransaction save = transactionForm.save();
            if(save != null && onSave != null){
                onSave.build();
            }
        });
    }

    public void disableExchange() {
        transactionForm.disableExchange();
    }

    public void disableAmount() {
        transactionForm.disableAmount();
    }

    public void setNextReferenceId(Long id) {
        transactionForm.setNextReferenceId(id);
    }

    private Executable refresh, onSave;

    public void setRefresh(Executable refresh) {
        this.refresh = refresh;
        transactionForm.setRefresh(refresh);
    }

    public void setOnSave(Executable onSave) {
        this.onSave = onSave;
    }
}
