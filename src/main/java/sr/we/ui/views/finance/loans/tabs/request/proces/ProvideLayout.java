package sr.we.ui.views.finance.loans.tabs.request.proces;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.BigDecimalField;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.ui.components.general.BusinessCurrencySelect;
import sr.we.ui.views.finance.transactions.TransactionDialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * A Designer generated component for the front-page template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("provide-layout")
@JsModule("./src/views/finance/loan/proces/provide-layout.ts")
public class ProvideLayout extends LitTemplate {


    @Id("label")
    private Label label;
    @Id("currencySelect")
    private BusinessCurrencySelect vaadinSelect;
    @Id("vaadinFormLayout")
    private FormLayout vaadinFormLayout;
    @Id("amountFld")
    private BigDecimalField amountFld;
    @Id("vaadinButton")
    private Button vaadinButton;
    private LoanRequest loanRequest;
    @Id("loanProvideProgress")
    private ProgressBar loanProvideProgress;
    @Id("label1")
    private Label label1;
    private Executable refresh;

    /**
     * Creates a new FrontPage.
     */
    public ProvideLayout() {
//        Animated.animate(this, Animated.Animation.SLIDE_IN_RIGHT);
        vaadinFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("500", 2));

        // You can initialise any data required for the connected UI components here.
        vaadinSelect.load();

        vaadinSelect.setLabel(null);
        vaadinSelect.setHelperText(null);
        vaadinSelect.setReadOnly(true);


        amountFld.addValueChangeListener(f -> {
            if (amountFld.getValue().compareTo(loanRequest.getRest()) > 0 || amountFld.getValue().compareTo(BigDecimal.ZERO) < 0 || (amountFld.getValue().compareTo(BigDecimal.ZERO) == 0 && loanRequest.getRest().compareTo(BigDecimal.ZERO) != 0)) {
                amountFld.setInvalid(true);
                amountFld.setErrorMessage("Maximum amount should be [" + loanRequest.getRest() + "]");
                vaadinButton.setEnabled(false);
                amountFld.setValue(loanRequest.getRest());
            } else if (amountFld.getValue().compareTo(BigDecimal.ZERO) == 0 && loanRequest.getRest().compareTo(BigDecimal.ZERO) == 0) {
                amountFld.setInvalid(false);
                validate();
            } else {
                amountFld.setInvalid(false);
                validate();
            }
        });
        vaadinSelect.addValueChangeListener(f -> {
            validate();
        });
        vaadinButton.addClickListener(f -> {
            BigDecimal rest = amountFld.getValue();
            LocalDate initDate = loanRequest.getEstimatedDate();
            Long businessId = loanRequest.getLoan().getBusiness().getId();
            Currency fromCurrency = loanRequest.getCurrency();
            Currency selectedCurrency = vaadinSelect.getValue();
            Reference reference = Reference.LOAN_REQUEST;
            Long referenceId = loanRequest.getId();
            TransactionDialog transactionDialog = new TransactionDialog(rest, initDate, businessId, fromCurrency, selectedCurrency, reference, referenceId,(loanRequest.getCustomer() == null ? null : loanRequest.getCustomer().getId()));
            transactionDialog.open();
            transactionDialog.setRefresh(refresh);
            transactionDialog.disableExchange();
            transactionDialog.disableAmount();
        });
    }

    public void setLoanRequest(LoanRequest loanRequest, Executable refresh) {
        this.loanRequest = loanRequest;
        this.refresh = refresh;
        amountFld.setValue(loanRequest.getRest());
        vaadinSelect.setValue(loanRequest.getCurrency());
        loanProvideProgress.setMax(loanRequest.getAmount().doubleValue());
        BigDecimal subtract = loanRequest.getAmount().subtract(loanRequest.getRest());
        loanProvideProgress.setValue(subtract.doubleValue());
        if (subtract.compareTo(BigDecimal.ZERO) == 0) {
            label1.setText("0.00 %");
        } else {
            label1.setText(loanRequest.getAmount().divide(subtract, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) + " %");
        }
        if (loanRequest.getRest().compareTo(BigDecimal.ZERO) == 0) {
            amountFld.setEnabled(false);
            vaadinButton.setEnabled(false);
        }
        vaadinButton.setVisible(false);
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.APPROVED) == 0) {
            vaadinButton.setVisible(true);
        }
    }

    private void validate() {
        vaadinButton.setEnabled(vaadinSelect.getValue() != null && amountFld.getValue() != null);
    }

}
