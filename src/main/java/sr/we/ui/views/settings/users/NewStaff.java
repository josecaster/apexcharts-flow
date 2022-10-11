package sr.we.ui.views.settings.users;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.UserService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Privilege;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.UsersRoles;
import sr.we.shekelflowcore.entity.helper.adapter.InviteStaffVO;
import sr.we.shekelflowcore.security.PrivilegeModeAbstract;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.settings.SettingsLayout;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Designer generated component for the new-staff template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("new-staff")
@JsModule("./src/views/settings/new-staff.ts")
@PageTitle("Users and permissions")
@Route(value = "new-users-and-permissions/:id?", layout = SettingsLayout.class)
@RolesAllowed({Role.staff, Role.owner, Role.admin})
public class NewStaff extends LitTemplate implements BeforeEnterObserver {

    private final VerticalLayout permissionForm;
    List<Item> checkboxes;
    @Id("back-button")
    private Button backButton;
    @Id("send-invite")
    private Button sendInvite;
    @Id("staff-form")
    private StaffForm staffForm;
    private Business business;
    private List<Long> list;
    private UsersRoles usersRoles;

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
        permissionForm.setMargin(true);
        permissionForm.setPadding(true);

        sendInvite.addClickListener(f -> {
            InviteStaffVO inviteStaffVO = new InviteStaffVO();
            inviteStaffVO.setBusinessId(business.getId());
            inviteStaffVO.setEmail(staffForm.getEmail().getValue());
            inviteStaffVO.setFirstName(staffForm.getFirstName().getValue());
            inviteStaffVO.setLastName(staffForm.getLastName().getValue());
            inviteStaffVO.setPrivileges(list);
            if (usersRoles == null) {

                UserService userService = ContextProvider.getBean(UserService.class);
                userService.inviteStaff(AuthenticatedUser.token(), inviteStaffVO);
                backButton.click();
            } else {
                inviteStaffVO.setUserRolesId(usersRoles.getId());
                UserService userService = ContextProvider.getBean(UserService.class);
                userService.inviteStaff(AuthenticatedUser.token(), inviteStaffVO);
                backButton.click();
            }
        });

        list = new ArrayList<>();

        staffForm.getPermissionStateBtn().addClickListener(f -> {
            checkboxes.stream().forEach(c -> {
                c.getCheckbox().setValue(true);
            });
        });

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
    public void beforeEnter(BeforeEnterEvent event) {
        String token = AuthenticatedUser.token();

        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
//        List<Business> businesses = businessService.list(token);
        business = businessService.get(null, AuthenticatedUser.token());
//        Optional<Business> max = businesses.stream().filter(f -> f.getCounter().compareTo(0L) != 0).max(Comparator.comparingLong(Business::getCounter));
//        if (max.isPresent()) {
//            business = max.get();
//        }


        UserService userService = ContextProvider.getBean(UserService.class);
        List<Privilege> privileges =
                userService.staffPerms(token);
        if (privileges != null && !privileges.isEmpty()) {

            checkboxes = new ArrayList<>();
            add(PrivilegeModeAbstract.Group.GlobalOperations, privileges);
            add(PrivilegeModeAbstract.Group.BuySellOperations, privileges);
            add(PrivilegeModeAbstract.Group.LenderOperations, privileges);
            add(PrivilegeModeAbstract.Group.Accounting, privileges);
            add(PrivilegeModeAbstract.Group.CompanyAdministration, privileges);
            add(PrivilegeModeAbstract.Group.Reporting, privileges);
        }

        Optional<String> business1 = event.getRouteParameters().get("id");
        if (business1.isPresent()) {
            usersRoles = (UsersRoles) UI.getCurrent().getSession().getAttribute(business1.get());
            sendInvite.setText("Save");

            staffForm.getEmail().setReadOnly(true);
            staffForm.getFirstName().setVisible(false);
            staffForm.getLastName().setVisible(false);

            staffForm.getEmail().setValue(usersRoles.getThisUser().getUsername());

            checkboxes.forEach(f -> {
                boolean present = usersRoles.getRestrictions().stream().anyMatch(g -> g.getId().compareTo(f.getPrivilege().getId()) == 0);
                if(!present){
                    f.getCheckbox().setValue(true);
                }
            });
        }
    }

    private void add(PrivilegeModeAbstract.Group group, List<Privilege> privilegeList) {
        List<Privilege> privileges = privilegeList.stream().filter(f -> f.getGroup().compareTo(group) == 0).toList();
        if (privileges.isEmpty()) {
            return;
        }


        VerticalLayout layout = new VerticalLayout();
        layout.setClassName("my-cart-base shadow-s");

        String cat = getTranslation("sr.we.group.priv." + group.name().replace("_", ".").toLowerCase() + ".name");
        H2 h2 = new H2(cat);
        h2.setClassName(LumoUtility.TextColor.PRIMARY);
        permissionForm.add(h2);
        permissionForm.add(layout);
//        permissionForm.setColspan(layout,2);
        for (Privilege privilege : privileges) {
            Checkbox checkbox = new Checkbox();
            checkboxes.add(new Item(privilege,checkbox));
            String name = getTranslation("sr.we.priv." + privilege.getName().replace("_", ".").toLowerCase() + ".name");
            checkbox.setLabel(name);
            String description = getTranslation("sr.we.priv." + privilege.getName().replace("_", ".").toLowerCase() + ".description");
            Label label = new Label(description);
            label.setClassName(LumoUtility.TextColor.PRIMARY);
            layout.add(new VerticalLayout(checkbox, label));
            checkbox.addValueChangeListener(f -> {
                if (f.getValue()) {
                    list.add(privilege.getId());
                } else {
                    list.remove(privilege.getId());
                }
            });
        }
    }

    private class Item {
        private Privilege privilege;
        private Checkbox checkbox;

        public Item(Privilege privilege, Checkbox checkbox) {
            this.privilege = privilege;
            this.checkbox = checkbox;
        }

        public Privilege getPrivilege() {
            return privilege;
        }

        public Checkbox getCheckbox() {
            return checkbox;
        }
    }
}
