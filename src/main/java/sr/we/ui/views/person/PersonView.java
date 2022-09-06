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
import sr.we.ContextProvider;
import sr.we.data.controller.PersonService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Person;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.helper.vo.PersonVO;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.ui.components.TempDatePicker;
import sr.we.ui.views.StateListenerLayout;
import sr.we.ui.components.general.GenderSelect;
import sr.we.ui.views.personform.PersonFormView;
import sr.we.ui.views.ReRouteLayout;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@PageTitle("Personal info main")
@Route(value = "main-info", absolute = true)
@RolesAllowed({Role.user,Role.staff,Role.owner,Role.admin})
public class PersonView extends StateListenerLayout implements BeforeEnterObserver {


    private final TextField firstName;
    private final TextField lastName;
    private final TextField ssn;
    private final GenderSelect genderSelect;
    private final DatePicker birthDate;
    private ThisUser user;

    public PersonView() {

        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setCenterItems();

        setSizeFull();

        FormLayout fLayout = new FormLayout();
        fLayout.getElement().getStyle().set("align-self", "center");
        add(fLayout);
        fLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2)
        );

        VerticalLayout layout = new VerticalLayout();
        fLayout.add(layout);

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
        formLayout.add(
                firstName,lastName,ssn, genderSelect, birthDate
        );
        state(firstName,lastName,ssn, genderSelect, birthDate);
        formLayout.getElement().getStyle().set("align-self", "center");

        formLayout.setMaxWidth("500px");
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
        PersonService personService = ContextProvider.getBean(PersonService.class);
        String token = AuthenticatedUser.token();
        PersonVO vo = new PersonVO();
        vo.setFirstname(firstName.getValue());
        vo.setLastname(firstName.getValue());
        vo.setSsn(ssn.getValue());
        vo.setBirthdate(birthDate.getValue());
        vo.setGender(genderSelect.getValue());
        Person person = personService.create(token, vo);

//        Notification notification = new Notification();
//        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//        notification.setText(getTranslation("sr.we.success"));
//        notification.setDuration(5000);
//        notification.setPosition(Notification.Position.MIDDLE);
//        notification.open();


        user.setPerson(person);

        UI.getCurrent().navigate(PersonFormView.class);


    }

    @Override
    protected void onDiscard() {
        firstName.clear(); genderSelect.clear(); birthDate.clear(); ssn.clear(); lastName.clear();
        stateChanged(false, false);
    }

    @Override
    protected boolean validate() {
        if(firstName.isEmpty()){
            return false;
        }
        if(genderSelect.getOptionalValue().isEmpty()){
            return false;
        }
        if(birthDate.getOptionalValue().isEmpty()){
            return false;
        }
        if(ssn.getOptionalValue().isEmpty()){
            return false;
        }
        return lastName.getOptionalValue().isPresent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        AuthenticatedUser bean = ContextProvider.getBean(AuthenticatedUser.class);
        Optional<ThisUser> thisUser = bean.get();
        boolean present = thisUser.isPresent();
        if(!present){
            bean.logout();
            throw new ValidationException("Invalid Authentication");
        }
        user = thisUser.get();
        if(user.getPerson() != null){
            // navigate to profile
            event.forwardTo(ReRouteLayout.class);
        }
    }
}
