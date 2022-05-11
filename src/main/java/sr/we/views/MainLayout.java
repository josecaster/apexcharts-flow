package sr.we.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import java.util.Optional;

import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.views.about.AboutView;
import sr.we.views.athenticationauthorization.AthenticationAuthorizationView;
import sr.we.views.communication.CommunicationView;
import sr.we.views.customers.CustomersView;
import sr.we.views.dashboard.DashboardView;
import sr.we.views.flow.FlowView;
import sr.we.views.loans.LoansView;
import sr.we.views.overview.OverviewView;
import sr.we.views.partners.PartnersView;
import sr.we.views.products.ProductsView;
import sr.we.views.productscomponents.ProductsComponentsView;
import sr.we.views.purchases.PurchasesView;
import sr.we.views.reports.ReportsView;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

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

        /**
         * Simple wrapper to create icons using LineAwesome iconset. See
         * https://icons8.com/line-awesome
         */
        @NpmPackage(value = "line-awesome", version = "1.3.0")
        public static class LineAwesomeIcon extends Span {
            public LineAwesomeIcon(String lineawesomeClassnames) {
                addClassNames("menu-item-icon");
                if (!lineawesomeClassnames.isEmpty()) {
                    addClassNames(lineawesomeClassnames);
                }
            }
        }

    }

    private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("view-title");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("view-header");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("ShekelFlow");
        appName.addClassNames("app-name");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation(), createFooter());
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
                new MenuItemInfo("Dashboard", "la la-chart-area", DashboardView.class), //

                new MenuItemInfo("Overview", "la la-chart-area", OverviewView.class), //

                new MenuItemInfo("Loans", "la la-list", LoansView.class), //

                new MenuItemInfo("Purchases", "la la-list", PurchasesView.class), //

                new MenuItemInfo("Customers", "la la-th-list", CustomersView.class), //

                new MenuItemInfo("Partners", "la la-th-list", PartnersView.class), //

                new MenuItemInfo("Products", "la la-file", ProductsView.class), //

                new MenuItemInfo("Products Components", "la la-file", ProductsComponentsView.class), //

                new MenuItemInfo("Flow", "la la-file", FlowView.class), //

                new MenuItemInfo("Athentication & Authorization", "la la-columns",
                        AthenticationAuthorizationView.class), //

                new MenuItemInfo("Communication", "la la-comments", CommunicationView.class), //

                new MenuItemInfo("Reports", "la la-th-list", ReportsView.class), //

                new MenuItemInfo("About", "la la-file", AboutView.class), //

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

            ContextMenu userMenu = new ContextMenu(avatar);
            userMenu.setOpenOnClick(true);
            userMenu.addItem("Logout", e -> {
                authenticatedUser.logout();
            });

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
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
