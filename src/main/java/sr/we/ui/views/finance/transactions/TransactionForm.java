package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.dom.Element;
import org.springframework.lang.NonNull;
import sr.we.ContextProvider;
import sr.we.data.controller.ExchangeRateService;
import sr.we.data.controller.PaymentTransactionService;
import sr.we.data.controller.UserAccessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.PaymentTransaction;
import sr.we.shekelflowcore.entity.helper.Build;
import sr.we.shekelflowcore.entity.helper.vo.PaymentTransactionVO;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.CurrencyPrivilege;
import sr.we.shekelflowcore.security.privileges.TransactionsPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.TempDatePicker;
import sr.we.ui.components.finance.AccountSelect;
import sr.we.ui.components.finance.PaymentMethodSelect;
import sr.we.ui.components.general.CurrencySelect;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionForm extends FormLayout {

    private final CurrencySelect currencySelect;
    private final BigDecimalField exchangeRate;
    private final H4 convertedAmountLbl;
    private final BigDecimalField amountFld;
    private final PaymentMethodSelect paymentMethodSelect;
    private final AccountSelect accountSelect;
    private final TextArea memoFld;
    private final DatePicker dateFld;

    private final PaymentTransaction.Reference reference;
    private final CurrencySelect currencyFrom;
    private Long referenceId;
    private final PaymentTransaction.PlusMin plusMin;
    private final Long businessId;
    private Long nextReferenceId;

    private Build refresh;


    public TransactionForm(BigDecimal rest, LocalDate initDate, Long businessId, Currency fromCurrency, Currency selectedCurrency, PaymentTransaction.Reference reference, Long referenceId, PaymentTransaction.PlusMin plusMin) {

        this.businessId = businessId;
        this.reference = reference;
        this.referenceId = referenceId;
        this.plusMin = plusMin;

        // init
        amountFld = new BigDecimalField();
        exchangeRate = new BigDecimalField();
        dateFld = new TempDatePicker();
        convertedAmountLbl = new H4(rest == null ? null : Constants.CURRENCY_FORMAT.format(rest));
        Element element = convertedAmountLbl.getElement();
        element.setAttribute("theme", "badge primary");
        element.getStyle().set("font-size", "var(--lumo-font-size-xl)");
        paymentMethodSelect = new PaymentMethodSelect();
        accountSelect = new AccountSelect(businessId);
        memoFld = new TextArea();
        currencySelect = new CurrencySelect();
        currencySelect.setLabel("To");
        currencySelect.setHelperText(null);
        currencySelect.setWidthFull();
        HorizontalLayout convertedAmountLayout = new HorizontalLayout(convertedAmountLbl);

        currencyFrom = new CurrencySelect();
        currencyFrom.setLabel("From");
        currencyFrom.setHelperText(null);
        currencyFrom.setReadOnly(true);
        currencyFrom.setWidthFull();

        // style
        dateFld.setWidthFull();
        amountFld.setWidthFull();
        exchangeRate.setWidthFull();
        convertedAmountLbl.setWidthFull();
        paymentMethodSelect.setWidthFull();
        accountSelect.setWidthFull();
        memoFld.setWidthFull();
        convertedAmountLbl.setWidth("-1px");
        convertedAmountLayout.setWidthFull();
        convertedAmountLayout.setAlignItems(FlexComponent.Alignment.END);
        convertedAmountLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);


        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new CurrencyPrivilege(), Privileges.UPDATE);
        exchangeRate.setEnabled(hasAccess);

        // listeners
        exchangeRate.addValueChangeListener(h -> {
            BigDecimal val = amountFld.getValue().multiply(h.getValue());
            convertedAmountLbl.setText(Constants.CURRENCY_FORMAT.format(val));
        });

        currencySelect.addValueChangeListener(g -> {
            if(currencyFrom.getValue() == null){
                exchangeRate.setValue(BigDecimal.ZERO);
                return;
            }
            ExchangeRateService exchangeRateService = ContextProvider.getBean(ExchangeRateService.class);
            try {
                BigDecimal exchange = exchangeRateService.exchange(currencyFrom.getValue().getCode(), g.getValue().getCode(), businessId, AuthenticatedUser.token());
                exchangeRate.setValue(exchange);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // add default values
        amountFld.setValue(rest == null ? BigDecimal.ZERO : rest);
        exchangeRate.setValue(BigDecimal.ONE);
        dateFld.setValue(initDate);
        currencyFrom.setValue(fromCurrency);
        currencySelect.setValue(selectedCurrency);

        amountFld.addValueChangeListener(g -> {

            if(amountFld.getValue() == null){
                amountFld.setValue(BigDecimal.ZERO);
                return;
            }
            if (rest != null && g.getValue().compareTo(rest) > 0) {
                amountFld.setValue(rest);
            } else {
                if (exchangeRate.getValue() != null) {
                    BigDecimal val = amountFld.getValue().multiply(exchangeRate.getValue());
                    convertedAmountLbl.setText(Constants.CURRENCY_FORMAT.format(val));
                }
            }


        });

        // build
        addFormItem(currencyFrom, "Currency");
        addFormItem(currencySelect, "");
        addFormItem(dateFld, "Payment date");
        addFormItem(amountFld, "Amount");
        addFormItem(exchangeRate, "Exchange rate");
        addFormItem(paymentMethodSelect, "Payment method");
        addFormItem(accountSelect, "Account");
        add(convertedAmountLayout);
        addFormItem(memoFld, "Memo");
        setResponsiveSteps(new ResponsiveStep("0", 1));
        setMaxWidth("500px");
    }


    public void save() {
        PaymentTransactionVO paymentTransactionVO = new PaymentTransactionVO();
        paymentTransactionVO.setAmount(amountFld.getValue());
        paymentTransactionVO.setAccount(accountSelect.getValue().getId());
        paymentTransactionVO.setPaymentDate(dateFld.getValue());
        paymentTransactionVO.setPaymentMethod(paymentMethodSelect.getValue().getId());
        paymentTransactionVO.setMemo(memoFld.getValue());
        paymentTransactionVO.setExchangeRate(exchangeRate.getValue());
        paymentTransactionVO.setConvertedAmount(paymentTransactionVO.getAmount().multiply(paymentTransactionVO.getExchangeRate()));
        paymentTransactionVO.setPlusMin(plusMin);
        paymentTransactionVO.setReference(reference);
        paymentTransactionVO.setReferenceId(referenceId);
        paymentTransactionVO.setNextReferenceId(nextReferenceId);
        paymentTransactionVO.setBusiness(businessId);

        if (paymentTransactionVO.getAccount() != null && paymentTransactionVO.getPaymentDate() != null && paymentTransactionVO.getPaymentMethod() != null && paymentTransactionVO.getExchangeRate() != null) {
            PaymentTransactionService paymentTransactionService = ContextProvider.getBean(PaymentTransactionService.class);
            paymentTransactionService.create(AuthenticatedUser.token(), paymentTransactionVO);
            if(this.refresh == null) {
                UI.getCurrent().getPage().reload();
            } else {
                refresh.build();
            }
        } else {
            throw new ValidationException("Not all required fields are filled");
        }
    }

    public void disableExchange() {
        currencySelect.setReadOnly(true);
        exchangeRate.setReadOnly(true);
    }

    public void disableAmount() {
        amountFld.setReadOnly(true);
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public void setAmount(BigDecimal transactionBalance) {
        amountFld.setValue(transactionBalance);
    }

    public Long getNextReferenceId() {
        return nextReferenceId;
    }

    public void setNextReferenceId(Long nextReferenceId) {
        this.nextReferenceId = nextReferenceId;
    }

    public void setCurrency(Currency currency) {
        currencyFrom.setValue(currency);
        currencySelect.setValue(currency);
    }

    public void setRefresh(Build refresh) {
        this.refresh = refresh;
    }
}
