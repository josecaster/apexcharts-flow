package sr.we.ui.views.finance.loans.tabs.request;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Loan;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.helper.adapter.LoanRequestBody;
import sr.we.ui.components.EmailAddress;
import sr.we.ui.components.TempDatePicker;
import sr.we.ui.components.finance.FrequencyField;
import sr.we.ui.components.general.BusinessCurrencySelect;
import sr.we.ui.views.StateListenerLayout;
import sr.we.ui.views.finance.loans.tabs.LTabRequests;

public class LRCreateLayout extends StateListenerLayout {


    private final TextField nameFld, firstNameFld;
    private final TextField mobileFld;
    private final EmailAddress emailAddress;
    private final BusinessCurrencySelect currencyFld;
    private final FrequencyField frequencyFld;
    private final BigDecimalField requestedAmountFld;
    private final DatePicker datePicker;

    private final FormLayout layout;

    private String business;
    private Loan loan;


    public LRCreateLayout() {
        layout = new FormLayout();


        add(layout);
        layout.setMaxWidth("500px");

        nameFld = new TextField();
        firstNameFld = new TextField();
        mobileFld = new TextField();
        emailAddress = new EmailAddress();
        currencyFld = new BusinessCurrencySelect();
        frequencyFld = new FrequencyField();
        requestedAmountFld = new BigDecimalField();
        datePicker = new TempDatePicker();

        nameFld.setWidthFull();
        firstNameFld.setWidthFull();
        mobileFld.setWidthFull();
        emailAddress.setWidthFull();
        currencyFld.setWidthFull();
        frequencyFld.setWidthFull();
        requestedAmountFld.setWidthFull();
        datePicker.setWidthFull();

        emailAddress.setLabel(null);
        frequencyFld.setLabel(null);
        frequencyFld.setHelperText(null);
        currencyFld.setLabel(null);
        currencyFld.setHelperText(null);

        // info
        layout.addFormItem(nameFld, getTranslation("sr.we.name"));
        layout.addFormItem(firstNameFld, getTranslation("sr.we.first.name"));
        layout.addFormItem(mobileFld, getTranslation("sr.we.mobile"));
        layout.addFormItem(emailAddress, getTranslation("sr.we.email"));

        // start at
        layout.addFormItem(datePicker, getTranslation("sr.we.date"));
        layout.addFormItem(frequencyFld, getTranslation("sr.we.freq"));

        // amount
        layout.addFormItem(currencyFld, getTranslation("sr.we.currency"));
        layout.addFormItem(requestedAmountFld, getTranslation("sr.we.loan.requested.amount"));


        nameFld.setRequired(true);
        nameFld.setRequiredIndicatorVisible(true);

        firstNameFld.setRequired(true);
        firstNameFld.setRequiredIndicatorVisible(true);

//        mobileFld.setRequired(true);
//        mobileFld.setRequiredIndicatorVisible(true);

//        emailAddress.setRequiredIndicatorVisible(true);

        currencyFld.setRequiredIndicatorVisible(true);

        frequencyFld.setRequiredIndicatorVisible(true);

        requestedAmountFld.setRequiredIndicatorVisible(true);

        datePicker.setRequired(true);
        datePicker.setRequiredIndicatorVisible(true);

        state(nameFld, firstNameFld, /*mobileFld, emailAddress,*/ currencyFld, frequencyFld, requestedAmountFld, datePicker);

        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
    }

    @Override
    protected void onSave() {

        LoanRequestBody loanRequestBody = new LoanRequestBody();
        loanRequestBody.setBusinessId(StringUtils.isBlank(business) ? null : Long.valueOf(business));
        loanRequestBody.setLoanId(loan == null ? null : loan.getId());
        loanRequestBody.setName(nameFld.getValue());
        loanRequestBody.setFirstName(firstNameFld.getValue());
        loanRequestBody.setMobile(mobileFld.getValue());
        loanRequestBody.setEmail(emailAddress.getValue());
        loanRequestBody.setCurrency(currencyFld.getValue() == null ? null : currencyFld.getValue().getId());
        FrequencyField.Value value = frequencyFld.getValue1();
        loanRequestBody.setFreq(value.getFreq() == null ? null : value.getFreq());
        loanRequestBody.setFrequencyAmount(value.getFactor() == null ? null : value.getFactor().longValue());
        loanRequestBody.setAmount(requestedAmountFld.getValue());
        loanRequestBody.setDate(datePicker.getValue());

        LoanRequestService bean = ContextProvider.getBean(LoanRequestService.class);
        bean.create(AuthenticatedUser.token(), loanRequestBody);
        redirectToParent();
    }

    private void redirectToParent() {
//        QueryParameters queryParameters = QueryParameters.fromString("id=" + loan.getId());
//        UI.getCurrent().navigate(LoansViewTabRequests.getLocation(business), queryParameters);
        UI.getCurrent().navigate(LTabRequests.class, //
                new RouteParameters(//
                        new RouteParam("business", business),//
                        new RouteParam("loan", loan.getId().toString())));
    }

    @Override
    protected void onDiscard() {
        nameFld.clear();
        firstNameFld.clear();
        mobileFld.clear();
        emailAddress.clear();
        currencyFld.clear();
        frequencyFld.clear();
        requestedAmountFld.clear();
        datePicker.clear();
    }

    @Override
    protected boolean validate() {
        if (nameFld.isEmpty()) {
            return false;
        }
        if (firstNameFld.isEmpty()) {
            return false;
        }
//        if (mobileFld.isEmpty()) {
//            return false;
//        }
//        if (emailAddress.isEmpty()) {
//            return false;
//        }
        if (currencyFld.isEmpty()) {
            return false;
        }
        if (frequencyFld.getValue1().getFactor() == null || frequencyFld.getValue1().getFreq() == null) {
            return false;
        }
        if (requestedAmountFld.isEmpty()) {
            return false;
        }
        return !datePicker.isEmpty();
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public void setLoanRequest(LoanRequest loanRequest) {
        firstNameFld.setValue(StringUtils.isBlank(loanRequest.getCustomer().getFirstName()) ? "" :loanRequest.getCustomer().getFirstName());
        nameFld.setValue(loanRequest.getCustomer().getName());
        mobileFld.setValue(loanRequest.getCustomer().getPrimaryCustomerContacts().getMobile());
        emailAddress.setValue(loanRequest.getCustomer().getPrimaryCustomerContacts().getEmail());
        currencyFld.setValue(loanRequest.getCurrency());
        frequencyFld.setValue(new FrequencyField.Value(loanRequest.getFreq(), loanRequest.getFreqVal().doubleValue()));
        requestedAmountFld.setValue(loanRequest.getAmount());
        datePicker.setValue(loanRequest.getEstimatedDate());
        setReadOnly(nameFld, firstNameFld, mobileFld, emailAddress, currencyFld, frequencyFld, requestedAmountFld, datePicker);
        actionLayout.setVisible(false);
    }

    private void setReadOnly(TextField nameFld, TextField firstNameFld, TextField mobileFld, EmailAddress emailAddress, BusinessCurrencySelect currencyFld, FrequencyField frequencyFld, BigDecimalField requestedAmountFld, DatePicker datePicker) {
        firstNameFld.setReadOnly(true);
        nameFld.setReadOnly(true);
        mobileFld.setReadOnly(true);
        emailAddress.setReadOnly(true);
        currencyFld.setReadOnly(true);
        frequencyFld.setReadOnly(true);
        requestedAmountFld.setReadOnly(true);
        datePicker.setReadOnly(true);
    }

}
