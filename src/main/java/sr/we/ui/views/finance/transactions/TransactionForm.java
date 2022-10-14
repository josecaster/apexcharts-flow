package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.dom.Element;
import sr.we.ContextProvider;
import sr.we.data.controller.ExchangeRateService;
import sr.we.data.controller.PaymentTransactionService;
import sr.we.data.controller.UserAccessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.PaymentTransaction;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.vo.PaymentTransactionVO;
import sr.we.shekelflowcore.enums.PlusMin;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.CurrencyPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.TempDatePicker;
import sr.we.ui.components.finance.AccountSelect;
import sr.we.ui.components.finance.PaymentMethodSelect;
import sr.we.ui.components.general.CurrencySelect;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class TransactionForm extends FormLayout {

    private final CurrencySelect currencySelect;
    private final BigDecimalField exchangeRate;
    private final H4 convertedAmountLbl;
    private final BigDecimalField amountFld, currencyAmountFld;
    private final PaymentMethodSelect paymentMethodSelect;
    private final AccountSelect accountSelect;
    private final TextArea memoFld;
    private final DatePicker dateFld;

    private final Reference reference;
    private final CurrencySelect currencyFrom;
    private final PlusMin plusMin;
    private final Long businessId;
    private FormItem exchanged_amount;
    private Long referenceId;
    private Long nextReferenceId;

    private Executable refresh;


    public TransactionForm(BigDecimal rest, LocalDate initDate, Long businessId, Currency fromCurrency, Currency selectedCurrency, Reference reference, Long referenceId) {

        this.businessId = businessId;
        this.reference = reference;
        this.referenceId = referenceId;
        this.plusMin = reference.getPlusMin();

        // init
        amountFld = new BigDecimalField();
        currencyAmountFld = new BigDecimalField();
        exchangeRate = new BigDecimalField();
        dateFld = new TempDatePicker();
        convertedAmountLbl = new H4(rest == null ? null : Constants.CURRENCY_FORMAT.format(rest));
        Element element = convertedAmountLbl.getElement();
        element.setAttribute("theme", "badge primary");
        element.getStyle().set("font-size", "var(--lumo-font-size-xl)");
        paymentMethodSelect = new PaymentMethodSelect();
        accountSelect = new AccountSelect(businessId, reference);
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
        currencyAmountFld.setWidthFull();
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
            currencyAmountFld.setValue(val);
            convertedAmountLbl.setText(Constants.CURRENCY_FORMAT.format(val));
            if(exchangeRate.getValue().compareTo(BigDecimal.ONE) != 0){
                currencyAmountFld.setReadOnly(false);
            } else {
                currencyAmountFld.setReadOnly(true);
            }
        });

        currencySelect.addValueChangeListener(g -> {
            if (currencyFrom.getValue() == null) {
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

            if (amountFld.getValue() == null) {
                amountFld.setValue(BigDecimal.ZERO);
                return;
            }
            if (rest != null && g.getValue().compareTo(rest) > 0) {
                amountFld.setValue(rest);
            } else {
                if (exchangeRate.getValue() != null) {
                    BigDecimal val = amountFld.getValue().multiply(exchangeRate.getValue());
                    convertedAmountLbl.setText(Constants.CURRENCY_FORMAT.format(val));
                    currencyAmountFld.setValue(val);
                }
            }


        });
        currencyAmountFld.addValueChangeListener(f -> {
            if (f.isFromClient()) {

                if (exchangeRate.getValue() != null && exchangeRate.getValue().compareTo(BigDecimal.ONE) == 0) {
                    currencyAmountFld.setValue(amountFld.getValue());
                } else if (exchangeRate.getValue() != null) {
                    BigDecimal val = amountFld.getValue().divide(exchangeRate.getValue(), 2, RoundingMode.HALF_UP);
                    amountFld.setValue(val);
                    convertedAmountLbl.setText(Constants.CURRENCY_FORMAT.format(currencyAmountFld.getValue()));
                }
            }
        });

        // build
        addFormItem(currencyFrom, "Currency");
        addFormItem(currencySelect, "");
        addFormItem(dateFld, "Payment date");
        addFormItem(amountFld, "Amount");
        exchanged_amount = addFormItem(currencyAmountFld, "Exchanged amount");
        addFormItem(exchangeRate, "Exchange rate");
        addFormItem(paymentMethodSelect, "Payment method");
        addFormItem(accountSelect, "Account");
        add(convertedAmountLayout);
        addFormItem(memoFld, "Memo");
        setResponsiveSteps(new ResponsiveStep("0", 1));
        setMaxWidth("500px");
    }


    public PaymentTransaction save() {
        PaymentTransactionVO paymentTransactionVO = new PaymentTransactionVO();
        paymentTransactionVO.setAmount(amountFld.getValue());
        paymentTransactionVO.setAccount(accountSelect.getValue().getId());
        paymentTransactionVO.setPaymentDate(dateFld.getValue());
        paymentTransactionVO.setPaymentMethod(paymentMethodSelect.getValue().getId());
        paymentTransactionVO.setMemo(memoFld.getValue());
        paymentTransactionVO.setExchangeRate(exchangeRate.getValue());
        BigDecimal multiply = paymentTransactionVO.getAmount().multiply(paymentTransactionVO.getExchangeRate());
        paymentTransactionVO.setConvertedAmount(currencyAmountFld.getValue());
        paymentTransactionVO.setPlusMin(plusMin);
        paymentTransactionVO.setReference(reference);
        paymentTransactionVO.setReferenceId(referenceId);
        paymentTransactionVO.setNextReferenceId(nextReferenceId);
        paymentTransactionVO.setBusiness(businessId);
        paymentTransactionVO.setCurrencyFrom(currencyFrom.getValue().getId());
        paymentTransactionVO.setCurrencyTo(currencySelect.getValue().getId());

        if (paymentTransactionVO.getAccount() != null && paymentTransactionVO.getPaymentDate() != null && paymentTransactionVO.getPaymentMethod() != null && paymentTransactionVO.getExchangeRate() != null) {
            PaymentTransactionService paymentTransactionService = ContextProvider.getBean(PaymentTransactionService.class);
            PaymentTransaction paymentTransaction = paymentTransactionService.create(AuthenticatedUser.token(), paymentTransactionVO);
            if (this.refresh == null) {
                UI.getCurrent().getPage().reload();
            } else {
                refresh.build();
            }
            return paymentTransaction;
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
        currencyAmountFld.setReadOnly(true);
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

    public void setRefresh(Executable refresh) {
        this.refresh = refresh;
    }
}
