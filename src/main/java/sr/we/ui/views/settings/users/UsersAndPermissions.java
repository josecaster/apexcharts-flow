package sr.we.ui.views.settings.users;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.UserService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.shekelflowcore.settings.util.DateUtil;
import sr.we.ui.views.settings.SettingsLayout;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * A Designer generated component for the users-and-permissions template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("users-and-permissions")
@JsModule("./src/views/settings/users-and-permissions.ts")
@PageTitle("Users and permissions")
@Route(value = "users-and-permissions", layout = SettingsLayout.class)
@RolesAllowed({Role.staff, Role.owner, Role.admin})
public class UsersAndPermissions extends LitTemplate implements BeforeEnterObserver {

    @Id("store-owner")
    private StoreOwner storeOwner;
    @Id("add-staff")
    private AddStaff addStaff;
    @Id("company-name")
    private H2 companyName;

    /**
     * Creates a new UsersAndPermissions.
     */
    public UsersAndPermissions() {
        // You can initialise any data required for the connected UI components here.
        addStaff.getAddStaff().addClickListener(f -> {
            UI.getCurrent().navigate(NewStaff.class);
        });
        addStaff.getStaffLayout().setVisible(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String token = AuthenticatedUser.token();
        VerticalLayout staffLayout = addStaff.getStaffLayout();
        staffLayout.removeAll();
        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
        Business business = businessService.get(null, AuthenticatedUser.token());
//        List<Business> businesses = businessService.list(token);
//        Optional<Business> max = businesses.stream().filter(f -> f.getCounter().compareTo(0L) != 0).max(Comparator.comparingLong(Business::getCounter));
        if (business != null) {
//            Business business = max.get();
            ThisUser thisUser = business.getThisUser();
            Avatar avatar = storeOwner.getAvatar();
            avatar.setColorIndex(new Random().nextInt(7 - 1 + 1) + 1);
            Person person = thisUser.getPerson();
            String name = person.getFirstname() + " " + person.getLastname();
            avatar.setName(name);
            storeOwner.getStoreOwnerLink().setText(name);
            companyName.setText(business.getName());
            Paragraph lastLogin = storeOwner.getLastLogin();
            String text = "Last login was " + (thisUser.getLastLogin() == null ? "..." : Constants.SIMPLE_DATE_TIME_FORMAT_24H.format(DateUtil.convertToDateViaInstant(thisUser.getLastLogin())));
            lastLogin.setText(text);

            UserService userService = ContextProvider.getBean(UserService.class);
            List<UsersRoles> users = userService.getStaff(AuthenticatedUser.token(), business.getId());
            if (users != null && !users.isEmpty()) {
                addStaff.setCount(users.size());
                staffLayout.setVisible(true);
                for(UsersRoles usersRoles : users){
                    addStaffToLayout(users, staffLayout, usersRoles);
                }
            }
        }

    }

    private void addStaffToLayout(List<UsersRoles> users, VerticalLayout staffLayout, UsersRoles usersRoles) {
        if(users.indexOf(usersRoles) != 0){
            Hr hr = new Hr();
            hr.setWidthFull();
            staffLayout.add(hr);
        }
        Person person = usersRoles.getThisUser().getPerson();
        String name = person == null ? usersRoles.getThisUser().getUsername() : (person.getFirstname() + " " + person.getLastname());
        Avatar avatar1 = new Avatar(name);
        avatar1.setColorIndex(new Random().nextInt(7 - 1 + 1) + 1);

        String name1 = UUID.randomUUID().toString();
        RouterLink routerLink = new RouterLink(name,NewStaff.class, new RouteParameters(new RouteParam("id", name1)));
        UI.getCurrent().getSession().setAttribute(name1,usersRoles);
        String text = "Last login was " + (usersRoles.getThisUser().getLastLogin() == null ? "..." : Constants.SIMPLE_DATE_TIME_FORMAT_24H.format(DateUtil.convertToDateViaInstant(usersRoles.getThisUser().getLastLogin())));
        Label lastLogin = new Label(text);
        VerticalLayout layout = new VerticalLayout(routerLink, lastLogin);
        HorizontalLayout horizontalLayout = new HorizontalLayout(avatar1, layout);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        staffLayout.add(horizontalLayout);
    }
}
