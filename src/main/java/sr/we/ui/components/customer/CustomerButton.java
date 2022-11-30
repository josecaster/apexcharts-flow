package sr.we.ui.components.customer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import sr.we.ContextProvider;
import sr.we.data.controller.CustomerService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Customer;
import sr.we.shekelflowcore.entity.helper.adapter.CustomerBody;
import sr.we.shekelflowcore.entity.helper.vo.*;
import sr.we.ui.components.MyDialog;
import sr.we.ui.views.finance.loanrequests.CustomerCmb;

public class CustomerButton extends Button {

    private Customer customer;

    private Business business;

    private  Long businessId,customerId;


    public CustomerButton() {

        setText("Click to add customer");

        ContextMenu contextMenu = new ContextMenu(this);
        contextMenu.setOpenOnClick(true);
        contextMenu.addItem("Create new customer", g -> {
            VerticalLayout customerLayout = new VerticalLayout();
            TextField lastNameFld = new TextField();
            lastNameFld.setHelperText(getTranslation("sr.we.customer.name.info"));
            lastNameFld.setRequired(true);
            lastNameFld.setRequiredIndicatorVisible(true);
            lastNameFld.setWidthFull();

            TextField firstNameFld = new TextField();
            firstNameFld.setHelperText(getTranslation("sr.we.customer.first.name.info"));
            firstNameFld.setWidthFull();
            TextField mobileNumberFld = new TextField();
            EmailField emailFld = new EmailField();

            firstNameFld.setPlaceholder(getTranslation("sr.we.customer.first.name"));
            lastNameFld.setPlaceholder(getTranslation("sr.we.customer.name"));
            mobileNumberFld.setPlaceholder("Mobile number");
            emailFld.setPlaceholder("Email-address");

            firstNameFld.setWidthFull();
            lastNameFld.setWidthFull();
            mobileNumberFld.setWidthFull();
            emailFld.setWidthFull();

            customerLayout.add(lastNameFld,firstNameFld, mobileNumberFld, emailFld);
            Dialog dialog1 = new MyDialog();
            dialog1.setHeaderTitle("Add new customer");
            dialog1.add(customerLayout);
            Button cancel = new Button("Cancel", (e) -> dialog1.close());
            cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog1.getFooter().add(cancel);
            Button save = new Button("Save");
            save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            dialog1.getFooter().add(save);
            dialog1.open();


            save.addClickListener(f -> {
                CustomerVO customerVO = new CustomerVO();
                customerVO.setNew(true);
                customerVO.setFirstName(firstNameFld.getValue());
                customerVO.setName(lastNameFld.getValue());
                customerVO.setBusiness(businessId);

                CustomerBody customerBody = new CustomerBody();
                customerBody.setNew(true);
                customerBody.setCustomerVO(customerVO);
                CustomerContactVO customerContactVO = new CustomerContactVO();
                customerContactVO.setMobile(mobileNumberFld.getValue());
                customerContactVO.setEmail(emailFld.getValue());

                customerBody.setCustomerContactVO(customerContactVO);
                CustomerService customerService = ContextProvider.getBean(CustomerService.class);

                customerBody.setCustomerBillingVO(new CustomerBillingVO());
                customerBody.setCustomerShippingVO(new CustomerShippingVO());
                customerBody.setShippingAddressVO(new CustomerAddressVO());
                customerBody.setBillingAddressVO(new CustomerAddressVO());
                Customer customer = customerService.create(AuthenticatedUser.token(), customerBody);
                setCustomer(customer);
                dialog1.close();
            });

        });
        contextMenu.addItem("Choose existing customer", g -> {
            CustomerCmb existingCustomersCmb = new CustomerCmb();
            existingCustomersCmb.setWidthFull();
            existingCustomersCmb.setPlaceholder("Choose existing customer");
            existingCustomersCmb.load(businessId);
            Dialog dialog1 = new MyDialog();
            dialog1.setHeaderTitle("Select customer");
            dialog1.add(existingCustomersCmb);
            Button cancel = new Button("Cancel", (e) -> dialog1.close());
            cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog1.getFooter().add(cancel);
            Button save = new Button("Save");
            save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            dialog1.getFooter().add(save);
            dialog1.open();

            existingCustomersCmb.addValueChangeListener(f -> setCustomer(f.getValue()));
        });
    }

    public Long getCustomer() {
        return customerId;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if(customer!=null) {
            this.customerId = customer.getId();
            setText(customer.getName() + ", " + customer.getFirstName());
        }
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
        if(business  !=  null){
            this.businessId = business.getId();
        }
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
        if(customerId!=  null) {
            CustomerService customerService = ContextProvider.getBean(CustomerService.class);
            setCustomer(customerService.get(customerId, AuthenticatedUser.token()));
        }
    }
}
