package sr.we.ui.views.settings;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import sr.we.ContextProvider;
import sr.we.CustomErrorHandler;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.person.GeneralView;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.ReRouteLayout;
import sr.we.ui.views.business.BusinessView;
import sr.we.ui.views.personform.InfoFormView;
import sr.we.ui.views.settings.users.UsersAndPermissions;

import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
public class SettingsLayout extends AppLayout implements BeforeEnterObserver {

    boolean constructed = false;
    //    private Dialog dialog;
    private final H1 viewTitle;
    private final AccessAnnotationChecker accessChecker;

    public SettingsLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.accessChecker = accessChecker;

//        dialog = new Dialog();
        viewTitle = new H1();

        VaadinSession.getCurrent().setErrorHandler(new CustomErrorHandler());


    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        AuthenticatedUser bean = ContextProvider.getBean(AuthenticatedUser.class);
        Optional<ThisUser> thisUser = bean.get();
        if (thisUser.isPresent()) {
//            ThisUser thisUser1 = thisUser.get();
//            Token token = thisUser1.getToken();
//            SpringVaadinSession.getCurrent().setAttribute("Token", token.getToken());
//
//            // basic details
//            if (thisUser1.getPerson() == null) {
//                event.forwardTo(MainInfoView.class);
//                return;
//            }
//
//            // basic details
//            if (thisUser1.getPerson().getDefaultForms() == null) {
//                event.forwardTo(DetailInfoView.class);
//                return;
//            }
//
//            // inactive user
//            if (thisUser1.getActive() == null || !thisUser1.getActive()) {
//                new NotActiveDialog().open();
//                return;
//            }
            if (!constructed) {
                construct();
            }
        } else {
            UI.getCurrent().navigate(ReRouteLayout.class);
        }
    }

    @Override
    public void setContent(Component content) {
        super.setContent(content);
    }

    public void construct() throws RuntimeException {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
        constructed = true;
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");


        viewTitle.addClassNames("view-title");


        Header header = new Header(toggle, viewTitle);
        header.addClassNames("view-header");
        return header;
    }

    private Component createDrawerContent() {

        H2 appName = new H2("Settings");
        appName.addClassNames("app-name");

        Button home = new Button(getTranslation("sr.we.home"));
        home.setIcon(new LineAwesomeIcon("la la-home"));
        home.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_CONTRAST);
        home.addClickListener(f -> UI.getCurrent().navigate(ReRouteLayout.class));

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(home,appName, createNavigation());
        section.addClassNames("drawer-section");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("menu-item-container");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("navigation-list");
        nav.add(list);

        for (MainLayout.MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }

        }
        return nav;
    }

    private MainLayout.MenuItemInfo[] createMenuItems() {
        return new MainLayout.MenuItemInfo[]{ //
                new MainLayout.MenuItemInfo(getTranslation("sr.we.business"), "icons/menus/icons8_business_48px.png", BusinessView.class, false),

                new MainLayout.MenuItemInfo(getTranslation("sr.we.general"), "icons/menus/icons8_contact_48px.png", GeneralView.class, false), //

                new MainLayout.MenuItemInfo(getTranslation("sr.we.info"), "icons/menus/icons8_contact_details_48px.png", InfoFormView.class, false),
                new MainLayout.MenuItemInfo("Users and permissions", "icons/menus/icons8_users_48px.png", UsersAndPermissions.class, false)


        };
    }

//    private Footer createFooter() {
//        Footer layout = new Footer();
//        layout.addClassNames("footer");
//
//        Optional<ThisUser> maybeUser = authenticatedUser.get();
//        if (maybeUser.isPresent()) {
//            ThisUser user = maybeUser.get();
//
//            Avatar avatar = new Avatar(user.getUsername()/*, user.getProfilePictureUrl()*/);
//            avatar.addClassNames("me-xs");
//
//            ContextMenu userMenu = new ContextMenu(avatar);
//            userMenu.setOpenOnClick(true);
//            userMenu.addItem("Logout", e -> {
//                authenticatedUser.logout();
//            });
//
//            Span name = new Span(user.getUsername());
//            name.addClassNames("font-medium", "text-s", "text-secondary");
//
//            layout.add(avatar, name);
//        } else {
//            Anchor loginLink = new Anchor("login", "Sign in");
//            layout.add(loginLink);
//        }
//
//        return layout;
//    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        Component content = getContent();
        Class<? extends Component> aClass = content.getClass();
        if (HasDynamicTitle.class.isAssignableFrom(aClass)) {
            HasDynamicTitle hasDynamicTitle = (HasDynamicTitle) content;
            return hasDynamicTitle.getPageTitle();
        }
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
