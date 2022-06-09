package sr.we.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.spring.SpringVaadinSession;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.CustomErrorHandler;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.helper.Token;
import sr.we.views.about.AboutView;
import sr.we.views.athenticationauthorization.AthenticationAuthorizationView;
import sr.we.views.business.BusinessView;
import sr.we.views.communication.CommunicationView;
import sr.we.views.customers.CustomersView;
import sr.we.views.dashboard.DashboardView;
import sr.we.views.flow.FlowView;
import sr.we.views.loans.LoansView;
import sr.we.views.login.NotActiveDialog;
import sr.we.views.partners.PartnersView;
import sr.we.views.person.PersonView;
import sr.we.views.personform.PersonFormView;
import sr.we.views.products.ProductsView;
import sr.we.views.productscomponents.ProductsComponentsView;
import sr.we.views.purchases.PurchasesView;
import sr.we.views.reports.ReportsView;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
//@RoutePrefix(":business")
//@Route(":business?")
////@RouteAlias(":businessID(" + RouteParameterRegex.INTEGER + ")")
////@RouteAlias("last")
//@RoutePrefix(":business")
//@Route(value = "")
//@RoutePrefix("forum/category/:categoryID")
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
@Route("")
@RoutePrefix("n/a/:business")
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private static Optional<String> business;
    private final Dialog dialog;
    private final H1 viewTitle;
    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;
    boolean constructed = false;
    private final UserCompanyProfile userCompanyProfile;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        dialog = new Dialog();
        viewTitle = new H1();

        VaadinSession.getCurrent().setErrorHandler(new CustomErrorHandler());

        userCompanyProfile = new UserCompanyProfile(dialog);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        AuthenticatedUser bean = ContextProvider.getBean(AuthenticatedUser.class);
        Optional<ThisUser> thisUser = bean.get();
        if (thisUser.isPresent()) {
            ThisUser thisUser1 = thisUser.get();
            Token token = thisUser1.getToken();
            SpringVaadinSession.getCurrent().setAttribute("Token", token.getToken());

            // basic details
            if (thisUser1.getPerson() == null) {
                event.forwardTo(PersonView.class);
                return;
            }

            // basic details
            if (thisUser1.getPerson().getDefaultForms() == null) {
                event.forwardTo(PersonFormView.class);
                return;
            }

            // inactive user
            if (thisUser1.getActive() == null || !thisUser1.getActive()) {
                new NotActiveDialog().open();
                return;
            }
        }

//        Optional<String> business1 = event.getRouteParameters().get("business");
//        if (business1.isPresent()) {
//            business = business1.get();
//            Long aLong = Long.valueOf(business);
//            if(aLong.compareTo(userCompanyProfile.getListBox()) != 0){
//                event.forwardTo(MainLayout.class, new RouteParameters(new RouteParam("business", userCompanyProfile.getListBox().toString())));
//            }
//        } else {
//            event.forwardTo(MainLayout.class, new RouteParameters(new RouteParam("business", userCompanyProfile.getListBox().toString())));
//        }

        business = event.getRouteParameters().get("business");

        if (!constructed) {
//            UI current = UI.getCurrent();
//            new Thread(() -> {
//                current.access(() -> construct());
//            }).start();
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
        String token = AuthenticatedUser.token();
        if (!StringUtils.isEmpty(token)) {


            com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(userCompanyProfile, dialog, createNavigation(), createSettings(), createFooter());
            section.addClassNames("drawer-section");
            return section;
        }
        H2 appName = new H2("ShekelFlow");
        appName.addClassNames("app-name");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName, createNavigation(), createSettings(), createFooter());
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

    private Nav createSettings() {
        Nav nav = new Nav();
//        nav.addClassNames("menu-item-container");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("navigation-list");
        nav.add(list);

        for (MenuItemInfo menuItem : createSettingItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }

        }
        return nav;
    }

    private MenuItemInfo[] createSettingItems() {
        return new MenuItemInfo[]{ //
                new MenuItemInfo(getTranslation("sr.we.settings"), "la la-cogs", BusinessView.class, false)


        };
    }

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{ //
                new MenuItemInfo(getTranslation("sr.we.dashboard"), "la la-chart-area", DashboardView.class), //

                //new MenuItemInfo(getTranslation("sr.we.overview"), "la la-chart-area", OverviewView.class), //

                new MenuItemInfo(getTranslation("sr.we.loans"), "la la-list", LoansView.class), //

                new MenuItemInfo(getTranslation("sr.we.purchase"), "la la-list", PurchasesView.class), //

                new MenuItemInfo(getTranslation("sr.we.customers"), "la la-th-list", CustomersView.class), //

                new MenuItemInfo(getTranslation("sr.we.partners"), "la la-th-list", PartnersView.class), //

                new MenuItemInfo(getTranslation("sr.we.products"), "la la-file", ProductsView.class), //

                new MenuItemInfo(getTranslation("sr.we.products.components"), "la la-file", ProductsComponentsView.class), //

                new MenuItemInfo(getTranslation("sr.we.flow"), "la la-file", FlowView.class), //

                new MenuItemInfo(getTranslation("sr.we.authentication.authorization"), "la la-columns", AthenticationAuthorizationView.class), //

                new MenuItemInfo(getTranslation("sr.we.communication"), "la la-comments", CommunicationView.class), //

                new MenuItemInfo(getTranslation("sr.we.reports"), "la la-th-list", ReportsView.class), //

                new MenuItemInfo(getTranslation("sr.we.about"), "la la-file", AboutView.class), //


        };
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("footer");

        Optional<ThisUser> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            ThisUser user = maybeUser.get();

            Avatar avatar = new Avatar(user.getUsername()/*, user.getProfilePictureUrl()*/);
            avatar.addClassNames("me-xs");

            ContextMenu userMenu = new ContextMenu(layout);
            userMenu.setOpenOnClick(true);
            userMenu.addItem("Logout", e -> authenticatedUser.logout());

            Span name = new Span(user.getUsername());
            name.addClassNames("font-medium", "text-s", "text-secondary");

            layout.add(avatar, name);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
        if (dialog != null && dialog.isOpened()) {
            dialog.close();
        }
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

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view, boolean main) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames("menu-item-link");
            if (main) {
                link.setRoute(view, new RouteParameters(new RouteParam("business", business.isPresent() ? business.get() : "0")));
            } else {
                link.setRoute(view);
            }

            Span text = new Span(menuTitle);
            text.addClassNames("menu-item-text");

            link.add(new LineAwesomeIcon(iconClass), text);
            add(link);
        }

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this(menuTitle, iconClass, view, true);
        }

        public Class<?> getView() {
            return view;
        }


    }
}
