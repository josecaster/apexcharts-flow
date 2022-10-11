package sr.we.ui.views.finance.loanrequests;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;
import sr.we.shekelflowcore.entity.Customer;
import sr.we.shekelflowcore.entity.CustomerContact;
import sr.we.shekelflowcore.entity.Loan;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.adapter.LoanRequestBody;
import sr.we.ui.components.finance.FrequencyField;

import java.math.BigDecimal;

/**
 * A Designer generated component for the requestor-form template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("requestor-form")
@JsModule("./src/views/finance/loanrequests/requestor-form.ts")
public class RequestorForm extends LitTemplate {

    @Id("avatar-fld")
    private Avatar avatarFld;
    @Id("name-fld")
    private TextField nameFld;
    @Id("firstname-fld")
    private TextField firstnameFld;
    @Id("customer-cmb")
    private CustomerCmb customerCmb;
    @Id("mobile-fld")
    private TextField mobileFld;
    @Id("email-fld")
    private EmailField emailFld;
    @Id("request-date-fld")
    private DatePicker requestDateFld;
    @Id("duration-fld")
    private FrequencyField durationFld;
    @Id("amount-fld")
    private NumberField amountFld;
    private String business;
    @Id("loan-cmb")
    private LoanCmb loanCmb;
    private LoanRequest loanRequest;
    @Id("intrest-first-chk")
    private Checkbox intrestFirstChk;

    /**
     * Creates a new RequestorForm.
     */
    public RequestorForm() {
        // You can initialise any data required for the connected UI components here.
//        Animated.animate(this, Animated.Animation.SLIDE_IN_RIGHT);

        customerCmb.addValueChangeListener(f -> {
            if (!f.isFromClient()) {
                return;
            }

            Customer customer = f.getValue();
            setCustomer(customer);
        });
    }

    private void setCustomer(Customer customer) {
        if (customer != null) {
            nameFld.setValue(StringUtils.isBlank(customer.getName()) ? "" : customer.getName());
            firstnameFld.setValue(StringUtils.isBlank(customer.getFirstName()) ? "" : customer.getFirstName());
            CustomerContact primaryCustomerContacts = customer.getPrimaryCustomerContacts();
            if (primaryCustomerContacts != null) {
                mobileFld.setValue(StringUtils.isBlank(primaryCustomerContacts.getMobile()) ? "" : primaryCustomerContacts.getMobile());
                emailFld.setValue(StringUtils.isBlank(primaryCustomerContacts.getEmail()) ? "" : primaryCustomerContacts.getEmail());
            }
        }
    }

    public LoanRequestBody getLoanRequestBody() {
        Loan loan = loanCmb.getValue();
        LoanRequestBody loanRequestBody = new LoanRequestBody();
        loanRequestBody.setCustomer(customerCmb.getValue() == null ? null : customerCmb.getValue().getId());
        loanRequestBody.setBusinessId(StringUtils.isBlank(business) ? null : Long.valueOf(business));
        loanRequestBody.setLoanId(loan == null ? null : loan.getId());
        loanRequestBody.setName(nameFld.getValue());
        loanRequestBody.setFirstName(firstnameFld.getValue());
        loanRequestBody.setMobile(mobileFld.getValue());
        loanRequestBody.setEmail(emailFld.getValue());
        loanRequestBody.setCurrency(loan == null ? null : loan.getCurrency().getId());
        FrequencyField.Value value = durationFld.getValue1();
        loanRequestBody.setFreq(value.getFreq() == null ? null : value.getFreq());
        loanRequestBody.setFrequencyAmount(value.getFactor() == null ? null : value.getFactor().longValue());
        loanRequestBody.setAmount(amountFld.getValue() == null ? null : BigDecimal.valueOf(amountFld.getValue()));
        loanRequestBody.setDate(requestDateFld.getValue());
        loanRequestBody.setNew(true);
        loanRequestBody.setIntrestFirst(intrestFirstChk.getValue());
        return loanRequestBody;
    }

    public void setBusiness(String business) {
        this.business = business;
        customerCmb.load(Long.valueOf(business));
        loanCmb.load(Long.valueOf(business));
    }

    protected void setLoanRequest(LoanRequest loanRequest, Executable refresh) {
        this.loanRequest = loanRequest;
        customerCmb.setValue(loanRequest.getCustomer());
        setCustomer(loanRequest.getCustomer());
        durationFld.setValue(new FrequencyField.Value(loanRequest.getFreq(), loanRequest.getFreqVal().doubleValue()));
        amountFld.setValue(loanRequest.getAmount().doubleValue());
        requestDateFld.setValue(loanRequest.getEstimatedDate());
        loanCmb.setValue(loanRequest.getLoan());
        intrestFirstChk.setValue(loanRequest.getIntrestFirst());

        loanCmb.setReadOnly(true);
        requestDateFld.setReadOnly(true);
        amountFld.setReadOnly(true);
        durationFld.setReadOnly(true);
        customerCmb.setReadOnly(true);
        nameFld.setReadOnly(true);
        firstnameFld.setReadOnly(true);
        mobileFld.setReadOnly(true);
        emailFld.setReadOnly(true);
        intrestFirstChk.setReadOnly(true);
    }
}
