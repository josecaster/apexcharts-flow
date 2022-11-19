package sr.we.ui.views.business;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.vo.BusinessVO;
import sr.we.ui.components.business.BusinessOrganisationTypeSelect;
import sr.we.ui.components.business.BusinessTypeSelect;
import sr.we.ui.components.general.CountrySelect;
import sr.we.ui.components.general.BusinessCurrencySelect;
import sr.we.ui.views.ReRouteLayout;
import sr.we.ui.views.StateListenerLayout;
import sr.we.ui.views.settings.SettingsLayout;

import javax.annotation.security.RolesAllowed;

@Route(value = "create-business", layout = SettingsLayout.class)
@RolesAllowed({Role.user,Role.staff,Role.owner,Role.admin})
public class BusinessViewCreate extends StateListenerLayout implements HasDynamicTitle {

    private final TextField companyName;
    private final BusinessTypeSelect typeOfBusiness;
    private final CountrySelect country;
    private final BusinessCurrencySelect businessCurrency;
    private final BusinessOrganisationTypeSelect typeOfOrganization;

    public BusinessViewCreate() {
        companyName = new TextField(getTranslation("sr.we.company.name"));
        companyName.setRequired(true);
        companyName.setRequiredIndicatorVisible(true);

        typeOfBusiness = new BusinessTypeSelect();
        typeOfBusiness.setRequiredIndicatorVisible(true);

        country = new CountrySelect();
        country.setRequiredIndicatorVisible(true);

        businessCurrency = new BusinessCurrencySelect();
        businessCurrency.setRequiredIndicatorVisible(true);

        typeOfOrganization = new BusinessOrganisationTypeSelect();
        typeOfOrganization.setRequiredIndicatorVisible(true);


        FormLayout formLayout = new FormLayout();
        formLayout.add(
                companyName, typeOfBusiness, country, businessCurrency, typeOfOrganization
        );
        state(companyName, typeOfBusiness, country, businessCurrency, typeOfOrganization);
        formLayout.getElement().getStyle().set("align-self", "center");

        formLayout.setWidth("500px");
        add(formLayout);
        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1)/*,
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("500px", 2)*/
        );
//// Stretch the username field over 2 columns
//        formLayout.setColspan(username, 2);
    }

    @Override
    protected void onSave() {
        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
        String token = AuthenticatedUser.token();
        BusinessVO vo = new BusinessVO();
        vo.setBusinessType(typeOfBusiness.getValue().getId());
        vo.setBusinessOrganisationType(typeOfOrganization.getValue().getId());
        vo.setCountry(country.getValue().getId());
        vo.setCurrency(businessCurrency.getValue().getId());
        vo.setName(companyName.getValue());
        Business business = businessService.create(token, vo);

//        Notification notification = new Notification();
//        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//        notification.setText(getTranslation("sr.we.success"));
//        notification.setDuration(5000);
//        notification.setPosition(Notification.Position.MIDDLE);
//        notification.open();

        UI.getCurrent().navigate(ReRouteLayout.class);


    }

    @Override
    protected void onDiscard() {
        companyName.clear(); typeOfBusiness.clear(); country.clear(); businessCurrency.clear(); typeOfOrganization.clear();
        stateChanged(false, false);
    }

    @Override
    protected boolean validate() {
        if(companyName.isEmpty()){
            return false;
        }
        if(typeOfBusiness.getOptionalValue().isEmpty()){
            return false;
        }
        if(country.getOptionalValue().isEmpty()){
            return false;
        }
        if(businessCurrency.getOptionalValue().isEmpty()){
            return false;
        }
        return typeOfOrganization.getOptionalValue().isPresent();
    }

    @Override
    public String getPageTitle() {
        return getTranslation("sr.we.create.new.business");
    }
}
