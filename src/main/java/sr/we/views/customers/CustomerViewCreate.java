package sr.we.views.customers;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.CustomerService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.adapter.CustomerBody;
import sr.we.shekelflowcore.entity.helper.vo.CustomerBillingVO;
import sr.we.shekelflowcore.entity.helper.vo.CustomerShippingVO;
import sr.we.shekelflowcore.entity.helper.vo.CustomerVO;
import sr.we.views.MainLayout;
import sr.we.views.StateListenerLayout;
import sr.we.views.business.CurrencySelect;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@Route(value = "create-customer", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class CustomerViewCreate extends StateListenerLayout implements HasDynamicTitle , BeforeEnterObserver {

    private TextField customerName, accountNumber, website, phone, shipTo;

    private TextArea notes, instructions;
    private ContactView contactView;
    private AddressView shippingAddress;
    private AddressView billingAddress;
    private CurrencySelect currencySelect;

    public CustomerViewCreate() {

        basic();

        billing();

        shipping();

    }

    private void shipping() {
        add(new H4(getTranslation("sr.we.shipping")));
        shippingAddress = new AddressView();
        FormLayout shippingFormLayout = new FormLayout();

        shippingFormLayout.getElement().getStyle().set("align-self", "center");
        shippingFormLayout.setMaxWidth("500px");
        shippingFormLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );


        shipTo = new TextField();
        shipTo.setHelperText(getTranslation("sr.we.ship.to.info"));
        shipTo.setWidthFull();

        phone = new TextField();
        phone.setWidthFull();

        instructions = new TextArea();
        instructions.setWidthFull();

        shippingFormLayout.addFormItem(shipTo, getTranslation("sr.we.ship.to"));
        shippingFormLayout.addFormItem(shippingAddress, getTranslation("sr.we.shipping.address"));
        shippingFormLayout.addFormItem(phone, getTranslation("sr.we.phone"));
        shippingFormLayout.addFormItem(instructions, getTranslation("sr.we.shipping.instruction"));
        state(shipTo, phone, instructions);
        stateArray(shippingAddress.fields());
        add(shippingFormLayout);
    }

    private void billing() {
        add(new H4(getTranslation("sr.we.billing")));
        billingAddress = new AddressView();
        FormLayout billingFormLayout = new FormLayout();

        billingFormLayout.getElement().getStyle().set("align-self", "center");
        billingFormLayout.setMaxWidth("500px");
        billingFormLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );
        currencySelect = new CurrencySelect();
        currencySelect.setLabel(null);
        currencySelect.setHelperText(null);
        currencySelect.setWidthFull();
        billingFormLayout.addFormItem(currencySelect, currencySelect.getTranslation());
        billingFormLayout.addFormItem(billingAddress, getTranslation("sr.we.billing.address"));
        state(currencySelect);
        stateArray(billingAddress.fields());
        add(billingFormLayout);
        add(new Hr());
    }

    private void basic() {
        customerName = new TextField();
        customerName.setHelperText(getTranslation("sr.we.customer.name.info"));
        customerName.setRequired(true);
        customerName.setRequiredIndicatorVisible(true);
        customerName.setWidthFull();

        contactView = new ContactView();
        contactView.setWidthFull();

        accountNumber = new TextField();
        accountNumber.setWidthFull();

        website = new TextField();
        website.setWidthFull();

        notes = new TextArea();
        notes.setWidthFull();

        FormLayout basicFormLayout = new FormLayout();
        basicFormLayout.addFormItem(customerName, getTranslation("sr.we.customer.name"));
        basicFormLayout.addFormItem(contactView, getTranslation("sr.we.primary.contact"));
        basicFormLayout.addFormItem(accountNumber, getTranslation("sr.we.account.number"));
        basicFormLayout.addFormItem(website, getTranslation("sr.we.website"));
        basicFormLayout.addFormItem(notes, getTranslation("sr.we.notes"));
        state(customerName, accountNumber, website, notes);
        stateArray(contactView.fields());
        basicFormLayout.getElement().getStyle().set("align-self", "center");

        basicFormLayout.setMaxWidth("500px");

        basicFormLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );


        add(new H4(getTranslation("sr.we.basic.information")));

        add(basicFormLayout);

        add(new Hr());
    }

    @Override
    protected void onSave() {
        CustomerService customerService = ContextProvider.getBean(CustomerService.class);
        String token = AuthenticatedUser.token();
        CustomerBody vo = new CustomerBody();
        // basic
        CustomerVO customerVO = new CustomerVO();
        customerVO.setNew(true);
        customerVO.setName(customerName.getValue());
        customerVO.setAccount(accountNumber.getValue());
        customerVO.setLink(website.getValue());
        customerVO.setNotes(notes.getValue());
        customerVO.setBusiness(Long.valueOf(business));
        vo.setCustomerVO(customerVO);
        vo.setCustomerContactVO(contactView.getCustomerContactVO());

        // billing
        CustomerBillingVO customerBillingVO = new CustomerBillingVO();
        customerBillingVO.setCurrency(currencySelect.getOptionalValue().isPresent() ? currencySelect.getOptionalValue().get().getId() : null);
        vo.setCustomerBillingVO(customerBillingVO);
        vo.setBillingAddressVO(billingAddress.getCustomerAddressVO());

        // shipping
        CustomerShippingVO customerShippingVO = new CustomerShippingVO();
        customerShippingVO.setName(shipTo.getValue());
        customerShippingVO.setPhone(phone.getValue());
        customerShippingVO.setInstructions(instructions.getValue());
        vo.setCustomerShippingVO(customerShippingVO);
        vo.setShippingAddressVO(shippingAddress.getCustomerAddressVO());

        // create
        customerService.create(token, vo);
        UI.getCurrent().navigate(CustomersView.class, new RouteParameters(new RouteParam("business", business)));

    }

    @Override
    protected void onDiscard() {
        customerName.clear();
        contactView.clear();
        stateChanged(false, false);
    }

    @Override
    protected boolean validate() {
        if (customerName.isEmpty()) {
            return false;
        }
        if (!contactView.isValid()) {
            return false;
        }
        if (!billingAddress.isValid()) {
            return false;
        }
        return shippingAddress.isValid();
    }

    @Override
    public String getPageTitle() {
        return getTranslation("sr.we.create.new.customer");
    }

    private String business;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> business1 = event.getRouteParameters().get("business");
        if(business1.isPresent()){
            business = business1.get();
        }
    }
}
