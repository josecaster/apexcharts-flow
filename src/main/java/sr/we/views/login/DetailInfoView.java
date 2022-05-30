package sr.we.views.login;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.SpringVaadinSession;
import org.apache.catalina.security.SecurityUtil;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.PersonFormService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Person;
import sr.we.shekelflowcore.entity.PersonForm;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.helper.vo.PersonFormVO;
import sr.we.shekelflowcore.entity.helper.vo.PersonVO;
import sr.we.views.StateListenerLayout;
import sr.we.views.business.CountrySelect;
import sr.we.views.dashboard.DashboardView;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@PageTitle("Detail info")
@Route(value = "detail-info", absolute = true)
@RolesAllowed({Role.user,Role.staff,Role.owner,Role.admin})
public class DetailInfoView extends StateListenerLayout implements BeforeEnterObserver {


    private TextField streetAddress, state, postalCode, city, occupation;
    private EmailAddress emailAddress;
    private CountrySelect countrySelect;
    private ThisUser user;
    private boolean userPresent;

    public DetailInfoView() {

        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);

        setSizeFull();

        FormLayout fLayout = new FormLayout();
        fLayout.getElement().getStyle().set("align-self", "center");
        add(fLayout);
        fLayout.setResponsiveSteps(
                // Use one column by defaultabc
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2)
        );

        VerticalLayout layout = new VerticalLayout();


        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        layout.add(img);

        H2 almost_there = new H2("Almost there");
        layout.add(almost_there);
        Paragraph paragraph = new Paragraph("Tell us a bit about yourself");
        layout.add(paragraph);

        almost_there.getElement().getStyle().set("align-self", "center");
        paragraph.getElement().getStyle().set("align-self", "center");
        img.getElement().getStyle().set("align-self", "center");

        streetAddress = new TextField(getTranslation("sr.we.street.address"));
        streetAddress.setRequired(true);
        streetAddress.setRequiredIndicatorVisible(true);

        postalCode = new TextField(getTranslation("sr.we.postal.code"));

        countrySelect = new CountrySelect();
        countrySelect.setRequiredIndicatorVisible(true);
        countrySelect.setHelperText("");

        city = new TextField(getTranslation("sr.we.city"));
        city.setRequired(true);
        city.setRequiredIndicatorVisible(true);

        state = new TextField(getTranslation("sr.we.state"));
        state.setRequired(true);
        state.setRequiredIndicatorVisible(true);

        emailAddress = new EmailAddress();

        occupation = new TextField(getTranslation("sr.we.occupation"));


        FormLayout formLayout = new FormLayout();
        formLayout.add(
                streetAddress,  postalCode, countrySelect,city,state,emailAddress,occupation
        );
        state(streetAddress,  postalCode, countrySelect,city,state,emailAddress,occupation);
        formLayout.getElement().getStyle().set("align-self", "center");

        formLayout.setMaxWidth("500px");
        fLayout.add(layout);
        fLayout.add(formLayout);
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
        PersonFormService personFormService = ContextProvider.getBean(PersonFormService.class);
        String token = (String) SpringVaadinSession.getCurrent().getAttribute("Token");
        PersonFormVO vo = new PersonFormVO();
        AuthenticatedUser bean = ContextProvider.getBean(AuthenticatedUser.class);
        vo.setPerson(userPresent ? user.getId() : null);
        vo.setStreetAddress(streetAddress.getValue());
        vo.setPostalCode(postalCode.getValue());
        vo.setCity(city.getValue());
        vo.setState(state.getValue());
        vo.setCountry(countrySelect.getValue().getCode());
        vo.setEmailAddress(emailAddress.getValue());
        vo.setOccupation(occupation.getValue());
        PersonForm personForm = personFormService.create(token, vo);
        user.getPerson().setForms(new HashSet<>(Arrays.asList(personForm)));

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
        erase(streetAddress,  postalCode, countrySelect,city,state,emailAddress,occupation);
        stateChanged(false, false);
    }



    @Override
    protected boolean validate() {
        if(streetAddress.isEmpty()){
            return false;
        }
        if(!countrySelect.getOptionalValue().isPresent()){
            return false;
        }
        if(!city.getOptionalValue().isPresent()){
            return false;
        }
        if(!state.getOptionalValue().isPresent()){
            return false;
        }
        return true;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        AuthenticatedUser bean = ContextProvider.getBean(AuthenticatedUser.class);
        Optional<ThisUser> thisUser = bean.get();
        userPresent = thisUser.isPresent();
        user = thisUser.get();
        if(user.getPerson() == null){
            // navigate to profile
            event.forwardTo(MainInfoView.class);
        } else if (user.getPerson().getDefaultForms() != null){
            // navigate to profile
            event.forwardTo(DashboardView.class);
        }
    }
}
