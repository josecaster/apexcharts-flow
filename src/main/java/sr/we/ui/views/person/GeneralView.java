package sr.we.ui.views.person;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import sr.we.data.controller.PersonService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Person;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.vo.PersonVO;
import sr.we.ui.components.TempDatePicker;
import sr.we.ui.views.StateListenerLayout;
import sr.we.ui.components.general.GenderSelect;
import sr.we.ui.views.settings.SettingsLayout;

import javax.annotation.security.RolesAllowed;
import java.util.concurrent.Executors;

@PageTitle("General")
@Route(value = "general-info", layout = SettingsLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class GeneralView extends StateListenerLayout implements BeforeEnterObserver {


    private final GenderSelect genderSelect;
    private final DatePicker birthDate;
    private final TextField firstName;
    private final TextField lastName;
    private final TextField ssn;
    private final VerticalLayout layout;
    private Person person;

    public GeneralView() {

        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setCenterItems();

        setSizeFull();

        FormLayout fLayout = new FormLayout();
        fLayout.addClassNames("my-cart-white", LumoUtility.BoxShadow.SMALL);
        fLayout.getElement().getStyle().set("align-self", "center");
        add(fLayout);
        fLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2));

        layout = new VerticalLayout();
        fLayout.add(layout);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        layout.add(img);

        H2 almost_there = new H2("First Step (1/2)");
        layout.add(almost_there);
        Paragraph paragraph = new Paragraph("Tell us a bit about yourself");
        layout.add(paragraph);

        almost_there.getElement().getStyle().set("align-self", "center");
        paragraph.getElement().getStyle().set("align-self", "center");
        img.getElement().getStyle().set("align-self", "center");


        firstName = new TextField(getTranslation("sr.we.first.name"));
        firstName.setRequired(true);
        firstName.setRequiredIndicatorVisible(true);

        lastName = new TextField(getTranslation("sr.we.last.name"));
        lastName.setRequired(true);
        lastName.setRequiredIndicatorVisible(true);

        ssn = new TextField(getTranslation("sr.we.ssn"));
        ssn.setRequired(true);
        ssn.setRequiredIndicatorVisible(true);

        genderSelect = new GenderSelect();
        genderSelect.setRequiredIndicatorVisible(true);

        birthDate = new TempDatePicker(getTranslation("sr.we.birth.date"));
        birthDate.setRequiredIndicatorVisible(true);


        FormLayout formLayout = new FormLayout();
        formLayout.add(firstName, lastName, ssn, genderSelect, birthDate);
        state(firstName, lastName, ssn, genderSelect, birthDate);
        formLayout.getElement().getStyle().set("align-self", "center");

        formLayout.setMaxWidth("500px");
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
        PersonService personService = ContextProvider.getBean(PersonService.class);
        String token = AuthenticatedUser.token();
        PersonVO vo = new PersonVO();
        vo.setId(person == null ? null : person.getId());
        vo.setFirstname(firstName.getValue());
        vo.setLastname(lastName.getValue());
        vo.setSsn(ssn.getValue());
        vo.setBirthdate(birthDate.getValue());
        vo.setGender(genderSelect.getValue());
        Person person = personService.edit(token, vo);

//        Notification notification = new Notification();
//        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//        notification.setText(getTranslation("sr.we.success"));
//        notification.setDuration(5000);
//        notification.setPosition(Notification.Position.MIDDLE);
//        notification.open();


    }

    @Override
    protected void onDiscard() {
        firstName.clear();
        genderSelect.clear();
        birthDate.clear();
        ssn.clear();
        lastName.clear();
        stateChanged(false, false);
    }

    @Override
    protected boolean validate() {
        if (firstName.isEmpty()) {
            return false;
        }
        if (genderSelect.getOptionalValue().isEmpty()) {
            return false;
        }
        if (birthDate.getOptionalValue().isEmpty()) {
            return false;
        }
        if (ssn.getOptionalValue().isEmpty()) {
            return false;
        }
        return lastName.getOptionalValue().isPresent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String token = AuthenticatedUser.token();
        UI current = UI.getCurrent();
        Executors.newSingleThreadExecutor().execute(() -> {
            PersonService businessService = ContextProvider.getBean(PersonService.class);
            person = businessService.me(token);
            current.access(() -> {
                if (StringUtils.isNotBlank(person.getFirstname()))
                    firstName.setValue(person.getFirstname());
                if (StringUtils.isNotBlank(person.getLastname()))
                    lastName.setValue(person.getLastname());
                if (StringUtils.isNotBlank(person.getSsn()))
                    ssn.setValue(person.getSsn());
                if (person.getBirthdate() != null)
                    birthDate.setValue(person.getBirthdate());
                if (person.getGender() != null)
                    genderSelect.setValue(person.getGender());
            });
        });
    }
}
