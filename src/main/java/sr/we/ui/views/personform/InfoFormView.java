package sr.we.ui.views.personform;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.PersonFormService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.PersonForm;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.vo.PersonFormVO;
import sr.we.ui.views.StateListenerLayout;
import sr.we.ui.components.general.CountrySelect;
import sr.we.ui.components.EmailAddress;
import sr.we.ui.views.settings.SettingsLayout;

import javax.annotation.security.RolesAllowed;

@PageTitle("Info Form")
@Route(value = "info-form", layout = SettingsLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class InfoFormView extends StateListenerLayout implements BeforeEnterObserver {


    private final TextField streetAddress;
    private final TextField state;
    private final TextField postalCode;
    private final TextField city;
    private final TextField occupation;
    private final EmailAddress emailAddress;
    private final CountrySelect countrySelect;
    private boolean userPresent;
    private PersonForm personForm;


    public InfoFormView() {

        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setCenterItems();

        setSizeFull();

        FormLayout fLayout = new FormLayout();
        fLayout.addClassNames("my-cart-white", LumoUtility.BoxShadow.SMALL);
        fLayout.getElement().getStyle().set("align-self", "center");
        add(fLayout);
        fLayout.setResponsiveSteps(
                // Use one column by defaultabc
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2));

        VerticalLayout layout = new VerticalLayout();


        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        layout.add(img);

        H2 almost_there = new H2("Last Step (2/2)");
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
        formLayout.add(streetAddress, postalCode, countrySelect, city, state, emailAddress, occupation);
        state(streetAddress, postalCode, countrySelect, city, state, emailAddress, occupation);
        formLayout.getElement().getStyle().set("align-self", "center");

        formLayout.setMaxWidth("500px");
        fLayout.add(layout);
        fLayout.add(formLayout);
        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1)/*,
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("500px", 2)*/);
//// Stretch the username field over 2 columns
//        formLayout.setColspan(username, 2);
    }

    @Override
    protected void onSave() {
        PersonFormService personFormService = ContextProvider.getBean(PersonFormService.class);
        String token = AuthenticatedUser.token();
        PersonFormVO vo = new PersonFormVO();
        AuthenticatedUser bean = ContextProvider.getBean(AuthenticatedUser.class);
        vo.setId(personForm == null ? null : personForm.getId());
        vo.setStreetAddress(streetAddress.getValue());
        vo.setPostalCode(postalCode.getValue());
        vo.setCity(city.getValue());
        vo.setState(state.getValue());
        vo.setCountry(countrySelect.getValue().getCode());
        vo.setEmailAddress(emailAddress.getValue());
        vo.setOccupation(occupation.getValue());
        PersonForm personForm = personFormService.edit(token, vo);

    }

    @Override
    protected void onDiscard() {
        erase(streetAddress, postalCode, countrySelect, city, state, emailAddress, occupation);
        stateChanged(false, false);
    }

    @Override
    protected boolean validate() {
        if (streetAddress.isEmpty()) {
            return false;
        }
        if (countrySelect.getOptionalValue().isEmpty()) {
            return false;
        }
        if (city.getOptionalValue().isEmpty()) {
            return false;
        }
        return state.getOptionalValue().isPresent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String token = AuthenticatedUser.token();
        UI current = UI.getCurrent();
        new Thread(() -> {
            PersonFormService businessService = ContextProvider.getBean(PersonFormService.class);
            personForm = businessService.me(token);
            current.access(() -> {
                streetAddress.setValue(personForm.getStreetAddress());
                if (StringUtils.isNotBlank(personForm.getPostalCode()))//
                    postalCode.setValue(personForm.getPostalCode());
                if (StringUtils.isNotBlank(personForm.getCity()))//
                    city.setValue(personForm.getCity());
                if (StringUtils.isNotBlank(personForm.getState()))//
                    state.setValue(personForm.getState());
                if (StringUtils.isNotBlank(personForm.getCountry()))//
                    countrySelect.setValue(countrySelect.getByCode(personForm.getCountry()));
                if (StringUtils.isNotBlank(personForm.getEmailAddress()))//
                    emailAddress.setValue(personForm.getEmailAddress());
                if (StringUtils.isNotBlank(personForm.getOccupation()))//
                    occupation.setValue(personForm.getOccupation());
            });
        }).start();
    }
}
