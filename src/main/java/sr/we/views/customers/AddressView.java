package sr.we.views.customers;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;
import sr.we.shekelflowcore.entity.CustomerAddress;
import sr.we.shekelflowcore.entity.CustomerContact;
import sr.we.shekelflowcore.entity.helper.vo.CustomerAddressVO;
import sr.we.shekelflowcore.entity.helper.vo.CustomerContactVO;
import sr.we.views.business.CountrySelect;

public class AddressView extends FormLayout {

    private final TextField addressFld;
    private final TextField address2Fld;
    private final TextField cityFld;
    private final TextField postalCodeFld;
    private final CountrySelect countrySelect;

    private final TextField stateFld;

    private CustomerAddressVO customerAddressVO;
    private CustomerAddress customerAddress;

    public AddressView() {



        addressFld = new TextField();
        addressFld.setPlaceholder(getTranslation("sr.we.address"));

        address2Fld = new TextField();
        address2Fld.setPlaceholder(getTranslation("sr.we.address2"));

        cityFld = new TextField();
        cityFld.setPlaceholder(getTranslation("sr.we.city"));

        postalCodeFld = new TextField();
        postalCodeFld.setPlaceholder(getTranslation("sr.we.postal.code"));

        countrySelect = new CountrySelect();
        countrySelect.setPlaceholder(getTranslation("sr.we.country"));
        countrySelect.setHelperText(null);

        stateFld = new TextField();
        stateFld.setPlaceholder(getTranslation("sr.we.state"));

        getElement().getStyle().set("align-self", "center");


        add(addressFld, 2);
        add(address2Fld, 2);
        add(countrySelect, 1);
        add(stateFld, 1);
        add(cityFld, 1);
        add(postalCodeFld, 1);

        setMaxWidth("500px");
        setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 2)/*,
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("500px", 2)*/);
    }

    public CustomerAddressVO getCustomerAddressVO() {
        customerAddressVO = new CustomerAddressVO();
        customerAddressVO.setAddress(addressFld.getValue());
        customerAddressVO.setAddress2(address2Fld.getValue());
        customerAddressVO.setCountry(countrySelect.getOptionalValue().isPresent() ? countrySelect.getOptionalValue().get().getId() : null);
        customerAddressVO.setCity(cityFld.getValue());
        customerAddressVO.setPostalCode(postalCodeFld.getValue());
        customerAddressVO.setState(stateFld.getValue());
        return customerAddressVO;
    }

    public void setCustomerAddress(CustomerAddress customerAddress) {
        this.customerAddress = customerAddress;
        clear();
        if (StringUtils.isNotBlank(customerAddress.getAddress())) //
            addressFld.setValue(customerAddress.getAddress());
        if (StringUtils.isNotBlank(customerAddress.getAddress2())) //
            address2Fld.setValue(customerAddress.getAddress2());
        if (StringUtils.isNotBlank(customerAddress.getCity())) //
            cityFld.setValue(customerAddress.getCity());
        if (StringUtils.isNotBlank(customerAddress.getPostalCode())) //
            postalCodeFld.setValue(customerAddress.getPostalCode());
        if (StringUtils.isNotBlank(customerAddress.getState())) //
            stateFld.setValue(customerAddress.getState());
        countrySelect.setValue(customerAddress.getCountry());
    }

    public void clear() {
        addressFld.clear();
        address2Fld.clear();
        cityFld.clear();
        postalCodeFld.clear();
        countrySelect.clear();
        stateFld.clear();
    }

    public boolean isValid() {
        return true;
    }

    public Component[] fields(){
        return new Component[]{addressFld,address2Fld};
    }
}
