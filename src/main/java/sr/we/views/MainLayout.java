package sr.we.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.data.SelectListDataView;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.AfterNavigationHandler;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;

import java.util.List;
import java.util.Optional;

import com.vaadin.flow.spring.SpringVaadinSession;
import com.vaadin.flow.spring.security.VaadinSavedRequestAwareAuthenticationSuccessHandler;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.helper.Token;
import sr.we.views.about.AboutView;
import sr.we.views.athenticationauthorization.AthenticationAuthorizationView;
import sr.we.views.business.CreateBusinessView;
import sr.we.views.communication.CommunicationView;
import sr.we.views.customers.CustomersView;
import sr.we.views.dashboard.DashboardView;
import sr.we.views.flow.FlowView;
import sr.we.views.loans.LoansView;
import sr.we.views.login.LoginView;
import sr.we.views.logout.LogoutView;
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

    private Dialog dialog;

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

    private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    @Override
    public void setContent(Component content) {
        super.setContent(content);
    }

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        AuthenticatedUser bean = ContextProvider.getBean(AuthenticatedUser.class);
        Optional<ThisUser> thisUser = bean.get();
        if(thisUser.isPresent()){
            ThisUser thisUser1 = thisUser.get();
            Token token = thisUser1.getToken();
            SpringVaadinSession.getCurrent().setAttribute("Token",token.getToken());
        }


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
        String token = (String) SpringVaadinSession.getCurrent().getAttribute("Token");
        if(!StringUtils.isEmpty(token)){
            dialog = new Dialog();
            com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(new UserCompanyProfile(dialog), dialog,
                    createNavigation(), createFooter());
            section.addClassNames("drawer-section");
            return section;
        }
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
                new MenuItemInfo(getTranslation("sr.we.dashboard"), "la la-chart-area", DashboardView.class), //

                new MenuItemInfo(getTranslation("sr.we.overview"), "la la-chart-area", OverviewView.class), //

                new MenuItemInfo(getTranslation("sr.we.loans"), "la la-list", LoansView.class), //

                new MenuItemInfo(getTranslation("sr.we.purchase"), "la la-list", PurchasesView.class), //

                new MenuItemInfo(getTranslation("sr.we.customers"), "la la-th-list", CustomersView.class), //

                new MenuItemInfo(getTranslation("sr.we.partners"), "la la-th-list", PartnersView.class), //

                new MenuItemInfo(getTranslation("sr.we.products"), "la la-file", ProductsView.class), //

                new MenuItemInfo(getTranslation("sr.we.products.components"), "la la-file", ProductsComponentsView.class), //

                new MenuItemInfo(getTranslation("sr.we.flow"), "la la-file", FlowView.class), //

                new MenuItemInfo(getTranslation("sr.we.authentication.authorization"), "la la-columns",
                        AthenticationAuthorizationView.class), //

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
        if(dialog != null && dialog.isOpened()){
            dialog.close();
        }
    }

    private String getCurrentPageTitle() {
        Component content = getContent();
        Class aClass = content.getClass();
        if(HasDynamicTitle.class.isAssignableFrom(aClass)){
            HasDynamicTitle hasDynamicTitle = (HasDynamicTitle) content;
            return hasDynamicTitle.getPageTitle();
        }
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
