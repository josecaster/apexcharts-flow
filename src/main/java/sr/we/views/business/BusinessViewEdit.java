package sr.we.views.business;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.SpringVaadinSession;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.vo.BusinessVO;
import sr.we.views.MainLayout;
import sr.we.views.StateListenerLayout;
import sr.we.views.dashboard.DashboardView;
import sr.we.views.settings.SettingsLayout;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;

@Route(value = "editbusiness", layout = SettingsLayout.class)
@RolesAllowed({Role.user,Role.staff,Role.owner,Role.admin})
public class BusinessViewEdit extends StateListenerLayout implements HasDynamicTitle, BeforeEnterObserver {

    private final TextField companyName;
    private final BusinessTypeSelect typeOfBusiness;
    private final CountrySelect country;
    private final CurrencySelect businessCurrency;
    private final BusinessOrganisationTypeSelect typeOfOrganization;
    private Business business;

    public BusinessViewEdit() {
        companyName = new TextField(getTranslation("sr.we.company.name"));
        companyName.setRequired(true);
        companyName.setRequiredIndicatorVisible(true);

        typeOfBusiness = new BusinessTypeSelect();
        typeOfBusiness.setRequiredIndicatorVisible(true);

        country = new CountrySelect();
        country.setRequiredIndicatorVisible(true);
        country.setReadOnly(true);

        businessCurrency = new CurrencySelect();
        businessCurrency.setRequiredIndicatorVisible(true);
        businessCurrency.setReadOnly(true);

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
        String token = (String) SpringVaadinSession.getCurrent().getAttribute("Token");
        BusinessVO vo = new BusinessVO();
        vo.setBusinessType(typeOfBusiness.getValue().getId());
        vo.setBusinessOrganisationType(typeOfOrganization.getValue().getId());
        vo.setCountry(country.getValue().getId());
        vo.setCurrency(businessCurrency.getValue().getId());
        vo.setName(companyName.getValue());
        vo.setId(business.getId());
        Business business = businessService.edit(token, vo);

        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setText(getTranslation("sr.we.success"));
        notification.setDuration(5000);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();

        UI.getCurrent().navigate(DashboardView.class);


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
        if(!typeOfBusiness.getOptionalValue().isPresent()){
            return false;
        }
        if(!country.getOptionalValue().isPresent()){
            return false;
        }
        if(!businessCurrency.getOptionalValue().isPresent()){
            return false;
        }
        if(!typeOfOrganization.getOptionalValue().isPresent()){
            return false;
        }
        return true;
    }

    @Override
    public String getPageTitle() {
        return getTranslation("sr.we.update.business");
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        QueryParameters routeParameters = event.getLocation().getQueryParameters();
        List<String> id1 = routeParameters.getParameters().get("id");
        Optional<String> id = id1.stream().findAny();
        if(!id.isPresent()){
            event.forwardTo(BusinessView.class);
        }
        String token = (String) SpringVaadinSession.getCurrent().getAttribute("Token");
        UI current = UI.getCurrent();
        new Thread(new Runnable() {
            @Override
            public void run() {
                BusinessService businessService = ContextProvider.getBean(BusinessService.class);
                business = businessService.get(Long.valueOf(id.get()), token);
                current.access(() -> {
                    typeOfBusiness.setValue(business.getBusinessType());
                    typeOfOrganization.setValue(business.getBusinessOrganisationType());
                    country.setValue(business.getCountry());
                    businessCurrency.setValue(business.getCurrency());
                    companyName.setValue(business.getName());
                });
            }
        }).start();





//        vo.setBusinessType(typeOfBusiness.getValue().getId());
//        vo.setBusinessOrganisationType(typeOfOrganization.getValue().getId());
//        vo.setCountry(country.getValue().getId());
//        vo.setCurrency(businessCurrency.getValue().getId());
//        vo.setName(companyName.getValue());
    }
}
