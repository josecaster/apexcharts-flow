package sr.we.ui.views.customers;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;
import sr.we.shekelflowcore.entity.CustomerContact;
import sr.we.shekelflowcore.entity.helper.vo.CustomerContactVO;

public class ContactView extends FormLayout {

    private final TextField firstnameField;
    private final TextField lastnameField;
    private final TextField emailField;
    private final TextField phoneField;
    private final TextField mobileField;

    private CustomerContactVO customerContactVO;
    private CustomerContact customerContact;

    public ContactView() {

        firstnameField = new TextField();
        firstnameField.setPlaceholder(getTranslation("sr.we.first.name"));

        lastnameField = new TextField();
        lastnameField.setPlaceholder(getTranslation("sr.we.last.name"));

        emailField = new TextField();
        emailField.setPlaceholder(getTranslation("sr.we.email"));

        phoneField = new TextField();
        phoneField.setPlaceholder(getTranslation("sr.we.phone"));

        mobileField = new TextField();
        mobileField.setPlaceholder(getTranslation("sr.we.mobile"));

        getElement().getStyle().set("align-self", "center");

        add(firstnameField, lastnameField, emailField, phoneField, mobileField);

        setMaxWidth("500px");
        setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1)/*,
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("500px", 2)*/);
    }

    public CustomerContactVO getCustomerContactVO() {
        customerContactVO = new CustomerContactVO();
        customerContactVO.setNew(customerContact == null);
        customerContactVO.setId(customerContact == null ? null : customerContact.getId());
        customerContactVO.setName(firstnameField.getValue());
        customerContactVO.setName(lastnameField.getValue());
        customerContactVO.setEmail(emailField.getValue());
        customerContactVO.setPhone(phoneField.getValue());
        customerContactVO.setMobile(mobileField.getValue());
        return customerContactVO;
    }

    public void setCustomerContact(CustomerContact customerContact) {
        this.customerContact = customerContact;
        if(customerContact == null){
            return;
        }
        clear();
        if (StringUtils.isNotBlank(customerContact.getName())) //
            firstnameField.setValue(customerContact.getName());
        if (StringUtils.isNotBlank(customerContact.getName())) //
            lastnameField.setValue(customerContact.getName());
        if (StringUtils.isNotBlank(customerContact.getEmail())) //
            emailField.setValue(customerContact.getEmail());
        if (StringUtils.isNotBlank(customerContact.getPhone())) //
            phoneField.setValue(customerContact.getPhone());
        if (StringUtils.isNotBlank(customerContact.getMobile())) //
            mobileField.setValue(customerContact.getMobile());
    }

    public void clear() {
        firstnameField.clear();
        lastnameField.clear();
        emailField.clear();
        phoneField.clear();
        mobileField.clear();
    }

    public boolean isValid() {
        return true;
    }

    public Component[] fields(){
        return new Component[]{firstnameField,lastnameField,emailField,phoneField,mobileField};
    }
}
