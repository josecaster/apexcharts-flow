package sr.we.views.settings;

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
import sr.we.CustomErrorHandler;
import sr.we.security.AuthenticatedUser;
import sr.we.views.LineAwesomeIcon;
import sr.we.views.business.BusinessView;
import sr.we.views.dashboard.DashboardView;
import sr.we.views.person.GeneralView;
import sr.we.views.personform.InfoFormView;

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
//        AuthenticatedUser bean = ContextProvider.getBean(AuthenticatedUser.class);
//        Optional<ThisUser> thisUser = bean.get();
//        if (thisUser.isPresent()) {
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
//        }
        if (!constructed) {
            construct();
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
        home.addClickListener(f -> UI.getCurrent().navigate(DashboardView.class));

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

        for (MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }

        }
        return nav;
    }

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{ //
                new MenuItemInfo(getTranslation("sr.we.business"), "las la-feather", BusinessView.class),

                new MenuItemInfo(getTranslation("sr.we.general"), "la la-user", GeneralView.class), //

                new MenuItemInfo(getTranslation("sr.we.info"), "la la-info-circle", InfoFormView.class)


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

    /**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames("menu-item-link");
            link.setRoute(view);

            Span text = new Span(menuTitle);
            text.addClassNames("menu-item-text");

            link.add(new LineAwesomeIcon(iconClass), text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }


    }
}
