package sr.we.ui.views.settings.users;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.UserService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Privilege;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.adapter.InviteStaffVO;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.settings.SettingsLayout;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * A Designer generated component for the new-staff template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("new-staff")
@JsModule("./src/views/settings/new-staff.ts")
@PageTitle("Users and permissions")
@Route(value = "new-users-and-permissions", layout = SettingsLayout.class)
@RolesAllowed({Role.staff, Role.owner, Role.admin})
public class NewStaff extends LitTemplate implements BeforeEnterObserver {

    private final FormLayout permissionForm;
    @Id("back-button")
    private Button backButton;
    @Id("send-invite")
    private Button sendInvite;
    @Id("staff-form")
    private StaffForm staffForm;
    private Business business;
    private List<Long> list;

    /**
     * Creates a new NewStaff.
     */
    public NewStaff() {
        // You can initialise any data required for the connected UI components here.
        backButton.addClickListener(f -> {
            UI.getCurrent().navigate(UsersAndPermissions.class);
        });
        backButton.setIcon(new LineAwesomeIcon("la la-arrow-left"));
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_ICON);

        permissionForm = staffForm.getPermissionForm();
        permissionForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0",1),
                new FormLayout.ResponsiveStep("500",3));

        sendInvite.addClickListener(f -> {
            InviteStaffVO inviteStaffVO = new InviteStaffVO();
            inviteStaffVO.setBusinessId(business.getId());
            inviteStaffVO.setEmail(staffForm.getEmail().getValue());
            inviteStaffVO.setFirstName(staffForm.getFirstName().getValue());
            inviteStaffVO.setLastName(staffForm.getLastName().getValue());
            inviteStaffVO.setPrivileges(list);

            UserService userService = ContextProvider.getBean(UserService.class);
            userService.inviteStaff(AuthenticatedUser.token(),inviteStaffVO);
            backButton.click();
        });

        list = new ArrayList<>();

    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getSendInvite() {
        return sendInvite;
    }

    public StaffForm getStaffForm() {
        return staffForm;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String token = AuthenticatedUser.token();

        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
//        List<Business> businesses = businessService.list(token);
        Business business = businessService.get(null, AuthenticatedUser.token());
//        Optional<Business> max = businesses.stream().filter(f -> f.getCounter().compareTo(0L) != 0).max(Comparator.comparingLong(Business::getCounter));
//        if (max.isPresent()) {
//            business = max.get();
//        }


        UserService userService = ContextProvider.getBean(UserService.class);
        List<Privilege> privileges =
                userService.staffPerms(token);
        if(privileges != null && !privileges.isEmpty()) {

            for(Privilege privilege : privileges) {
                Checkbox checkbox = new Checkbox();
                checkbox.setLabel(privilege.getName());
                permissionForm.add(new VerticalLayout(checkbox,new Label(getTranslation("sr.we.priv."+privilege.getName().replace("_", ".").toLowerCase()))));
                checkbox.addValueChangeListener(f -> {
                    if(f.getValue()){
                        list.add(privilege.getId());
                    } else {
                        list.remove(privilege.getId());
                    }
                });
            }
        }
    }
}
