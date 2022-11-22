package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.theme.lumo.LumoUtility;
import sr.we.ContextProvider;
import sr.we.data.controller.ExchangeRateService;
import sr.we.data.controller.PaymentTransactionService;
import sr.we.data.controller.UserAccessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.CurrencyExchange;
import sr.we.shekelflowcore.entity.PaymentTransaction;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.TransactionCategory;
import sr.we.shekelflowcore.entity.helper.vo.PaymentTransactionVO;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.enums.TransactionType;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.CurrencyExchangePrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.TempDatePicker;
import sr.we.ui.components.UIUtil;
import sr.we.ui.components.finance.AccountSelect;
import sr.we.ui.components.finance.PaymentMethodSelect;
import sr.we.ui.components.general.CurrencySelect;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

public class TransactionForm extends FormLayout {

    private final CurrencySelect currencySelect;
    private final BigDecimalField exchangeRate;
    private final H4 convertedAmountLbl, changeLbl;
    private final BigDecimalField amountFld, currencyAmountFld, receivedFld;
    private final PaymentMethodSelect paymentMethodSelect;
    private final AccountSelect accountSelect;
    private final CategorySelect categorySelect;
    private final TextArea memoFld;
    private final DatePicker dateFld;

    private final Reference reference;
    private final CurrencySelect currencyFrom;
    private final TransactionType transactionType;
    private final Long businessId;
    private final FormItem exchanged_amount;
    private BigDecimal change = BigDecimal.ZERO;
    private Long referenceId;
    private Long nextReferenceId;

    private Executable refresh;
    private FormItem category;
    private boolean fromToCurrency,exchange = false;
    private CurrencyExchange currencyExchange;


    public TransactionForm(BigDecimal rest, LocalDate initDate, Long businessId, Currency fromCurrency, Currency selectedCurrency, Reference reference, Long referenceId, Long customerId) {
        this(rest, initDate, businessId, fromCurrency, selectedCurrency, reference, referenceId, reference.getPlusMin(),customerId);
    }

    public TransactionForm(BigDecimal rest, LocalDate initDate, Long businessId, Currency fromCurrency, Currency selectedCurrency, Reference reference, Long referenceId, TransactionType transactionType, Long customerId) {

        this.businessId = businessId;
        this.reference = reference;
        this.referenceId = referenceId;
        this.transactionType = transactionType;

        // init
        amountFld = new BigDecimalField();
        currencyAmountFld = new BigDecimalField();
        receivedFld = new BigDecimalField();
        receivedFld.setPlaceholder("Received");
        exchangeRate = new BigDecimalField();
        dateFld = new TempDatePicker();
        convertedAmountLbl = new H4(rest == null ? null : Constants.CURRENCY_FORMAT.format(rest));
        convertedAmountLbl.setClassName(LumoUtility.Margin.NONE);
        Element element = convertedAmountLbl.getElement();
        element.setAttribute("theme", UIUtil.Badge.PILL+" primary");
        element.getStyle().set("font-size", "var(--lumo-font-size-xl)");

        changeLbl = new H4("0.00");
        changeLbl.setClassName(LumoUtility.Margin.NONE);
        Element element1 = changeLbl.getElement();
        element1.setAttribute("theme", UIUtil.Badge.PILL);
        element1.getStyle().set("font-size", "var(--lumo-font-size-xl)");
        paymentMethodSelect = new PaymentMethodSelect();
        if (reference == null) {
            accountSelect = new AccountSelect(businessId, transactionType);
        } else {
            accountSelect = new AccountSelect(businessId, reference);
        }
        categorySelect = new CategorySelect(transactionType, businessId);
        memoFld = new TextArea();
        currencySelect = new CurrencySelect(true);
        currencyAmountFld.setPrefixComponent(currencySelect);
//        currencySelect.setLabel("To");
        currencySelect.setHelperText(null);
        currencySelect.setWidth("100px");
//        HorizontalLayout convertedAmountLayout = new HorizontalLayout(convertedAmountLbl);
//        HorizontalLayout convertedAmountLayout1 = new HorizontalLayout(receivedFld);
//        HorizontalLayout convertedAmountLayout2 = new HorizontalLayout(changeLbl);

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
        categorySelect.setWidthFull();
        memoFld.setWidthFull();
        convertedAmountLbl.setWidth("-1px");
//        convertedAmountLayout.setWidthFull();
//        convertedAmountLayout.setAlignItems(FlexComponent.Alignment.END);
//        convertedAmountLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
//
//        convertedAmountLayout1.setWidthFull();
//        convertedAmountLayout1.setAlignItems(FlexComponent.Alignment.END);
//        convertedAmountLayout1.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
//
//        convertedAmountLayout2.setWidthFull();
//        convertedAmountLayout2.setAlignItems(FlexComponent.Alignment.END);
//        convertedAmountLayout2.setJustifyContentMode(FlexComponent.JustifyContentMode.END);


        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new CurrencyExchangePrivilege(), Privileges.UPDATE);
        exchangeRate.setEnabled(hasAccess);

        // listeners
        exchangeRate.addValueChangeListener(h -> {
            BigDecimal val = amountFld.getValue().multiply(h.getValue());
            currencyAmountFld.setValue(val);
            convertedAmountLbl.setText(selectedCurrency.getCode() + " " + Constants.CURRENCY_FORMAT.format(val));
//            currencyAmountFld.setReadOnly(exchangeRate.getValue().compareTo(BigDecimal.ONE) == 0);
            if (exchangeRate.getValue().compareTo(BigDecimal.ONE) == 0) {
                currencyAmountFld.setHelperText(null);
            } else {
                currencyAmountFld.setHelperText(currencyFrom.getValue().getCode() + " " + Constants.CURRENCY_FORMAT.format(amountFld.getValue()) +//
                        " - " +//
                        currencySelect.getValue().getCode() + " " + Constants.CURRENCY_FORMAT.format(val) + " | fx-rate: " + Constants.CURRENCY_FORMAT.format(h.getValue()));
            }
        });

        currencySelect.addValueChangeListener(g -> {
            if(g.getValue() == null){
                return;
            }
            if(exchange){
                if(g.getValue().getId().compareTo(currencyFrom.getValue().getId()) == 0){
                    currencyFrom.setValue(currencyExchange.getCurrencyFrom().getId().compareTo(g.getValue().getId()) == 0 ? currencyExchange.getCurrencyTo() : currencyExchange.getCurrencyFrom());
                }
            }

            accountSelect.setCurrency(g.getValue() == null ? null : g.getValue().getId());
            if (currencyFrom.getValue() == null) {
                exchangeRate.setValue(rest);
                return;
            }
            ExchangeRateService exchangeRateService = ContextProvider.getBean(ExchangeRateService.class);
            try {
                BigDecimal exchange = exchangeRateService.exchange(currencyFrom.getValue().getCode(), g.getValue().getCode(), businessId,dateFld.getValue(), AuthenticatedUser.token());
                exchangeRate.setValue(exchange);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        categorySelect.addValueChangeListener(g -> {
            if (g.getValue() != null) {
                TransactionCategory value = g.getValue();
                if (value.getReference().compareTo(Reference.EXCHANGE) == 0) {
                    Optional<CurrencyExchange> any = categorySelect.getExchanges().stream().filter(f -> f.getId().compareTo(g.getValue().getReferenceId()) == 0).findAny();
                    if (any.isPresent()) {
                        currencyExchange = any.get();
                        currencySelect.setItems(currencyExchange.getCurrencyTo(), currencyExchange.getCurrencyFrom());
                        setCurrency(currencyExchange.getCurrencyFrom());
                        exchange = true;
                        return;
                    }
                }
            }
            exchange = false;
        });

        // add default values
        amountFld.setValue(rest == null ? BigDecimal.ZERO : rest);
        receivedFld.setValue(amountFld.getValue());
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
                if (exchangeRate.getValue() != null) {
                    BigDecimal val = amountFld.getValue().multiply(exchangeRate.getValue());
                    convertedAmountLbl.setText(selectedCurrency.getCode() + " " + Constants.CURRENCY_FORMAT.format(val));
                    currencyAmountFld.setValue(val);
                }
            } else {
                if (!fromToCurrency) {
                    if (exchangeRate.getValue() != null) {
                        BigDecimal val = amountFld.getValue().multiply(exchangeRate.getValue());
                        convertedAmountLbl.setText(selectedCurrency.getCode() + " " + Constants.CURRENCY_FORMAT.format(val));
                        currencyAmountFld.setValue(val);
                    }
                }
            }
            fromToCurrency = false;

        });
        currencyAmountFld.addValueChangeListener(f -> {
            if (f.isFromClient()) {

                if (exchangeRate.getValue() != null && exchangeRate.getValue().compareTo(BigDecimal.ONE) == 0) {
                    if (rest != null && (currencyAmountFld.getValue() == null || currencyAmountFld.getValue().compareTo(rest) >= 0)) {
                        currencyAmountFld.setValue(amountFld.getValue());
                        receivedFld.setValue(amountFld.getValue());
                    }
                    amountFld.setValue(f.getValue());
                } else if (exchangeRate.getValue() != null) {
                    BigDecimal val = f.getValue().divide(exchangeRate.getValue(), 4, RoundingMode.HALF_UP);
                    fromToCurrency = true;
                    amountFld.setValue(val);
                    convertedAmountLbl.setText(selectedCurrency.getCode() + " " + Constants.CURRENCY_FORMAT.format(currencyAmountFld.getValue()));

                    currencyAmountFld.setHelperText(currencyFrom.getValue().getCode() + " " + Constants.CURRENCY_FORMAT.format(amountFld.getValue()) +//
                            " - " +//
                            currencySelect.getValue().getCode() + " " + Constants.CURRENCY_FORMAT.format(currencyAmountFld.getValue()) + " | fx-rate: " + Constants.CURRENCY_FORMAT.format(exchangeRate.getValue()));
                }
            }
            receivedFld.setValue(f.getValue());
        });

        receivedFld.addValueChangeListener(f -> {

            if (receivedFld.getValue() != null && currencyAmountFld.getValue() != null) {
                change = receivedFld.getValue().subtract(currencyAmountFld.getValue());
                if (change.compareTo(BigDecimal.ZERO) < 0) {
                    receivedFld.setValue(currencyAmountFld.getValue());
                }
                changeLbl.setText(Constants.CURRENCY_FORMAT.format(change));
            } else {
                changeLbl.setText("0.00");
            }
        });

        // build
        FormItem payment_date = addFormItem(dateFld, "Payment date");
        setColspan(payment_date, 2);
//        addFormItem(currencyFrom, "Currency");
//        addFormItem(currencySelect, "Currency to");
//        addFormItem(amountFld, "Amount");
        exchanged_amount = addFormItem(currencyAmountFld, "Amount");
        FormItem exchange_rate = addFormItem(exchangeRate, "Exchange rate");
        addFormItem(paymentMethodSelect, "Payment method");
//        VerticalLayout field = new VerticalLayout(accountSelect, convertedAmountLbl, receivedFld, changeLbl);
//        field.setSpacing(false);
//        field.setMargin(false);
//        field.setPadding(false);
        addFormItem(accountSelect, "Account");
        if (reference == null || referenceId == null) {
            category = addFormItem(categorySelect, "Category");
        }
//        addFormItem(convertedAmountLbl, "Exchnaged amount");
        addFormItem(receivedFld, "Paid");
        addFormItem(changeLbl, "Change");
        convertedAmountLbl.setWidthFull();
        receivedFld.setWidthFull();
        changeLbl.setWidthFull();
        FormItem memo = addFormItem(memoFld, "Memo");
        setColspan(memo, 2);
        setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("500", 2));
        setMaxWidth("750px");
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
        paymentTransactionVO.setTransactionType(transactionType);
        if (reference != null && referenceId != null) {
            paymentTransactionVO.setReference(reference);
            paymentTransactionVO.setReferenceId(referenceId);
        } else {
            TransactionCategory value = categorySelect.getValue();
            if (value != null) {
                paymentTransactionVO.setReference(value.getReference());
                paymentTransactionVO.setReferenceId(value.getReferenceId());
            }
        }
        paymentTransactionVO.setNextReferenceId(nextReferenceId);
        paymentTransactionVO.setBusiness(businessId);
        paymentTransactionVO.setCurrencyFrom(currencyFrom.getValue().getId());
        paymentTransactionVO.setCurrencyTo(currencySelect.getValue().getId());
        paymentTransactionVO.setReceived(receivedFld.getValue());
        paymentTransactionVO.setChange(change);
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
