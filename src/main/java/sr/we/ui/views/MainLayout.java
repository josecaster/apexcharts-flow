package sr.we.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.spring.SpringVaadinSession;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.CustomErrorHandler;
import sr.we.data.controller.UserAccessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.UsersRoles;
import sr.we.shekelflowcore.entity.helper.Token;
import sr.we.shekelflowcore.security.PrivilegeModeAbstract;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.*;
import sr.we.ui.views.account.ChartOfAccountsView;
import sr.we.ui.views.business.BusinessView;
import sr.we.ui.views.customers.CustomersView;
import sr.we.ui.views.dashboard.DashboardView;
import sr.we.ui.views.finance.loanrequests.RequestsView;
import sr.we.ui.views.finance.loans.LoanView;
import sr.we.ui.views.finance.payments.PaymentsView;
import sr.we.ui.views.finance.transactions.TransactionsView;
import sr.we.ui.views.invoice.InvoiceView;
import sr.we.ui.views.login.NotActiveDialog;
import sr.we.ui.views.person.PersonView;
import sr.we.ui.views.personform.PersonFormView;
import sr.we.ui.views.pos.PosView;
import sr.we.ui.views.products.ProductView;
import sr.we.ui.views.services.ServiceView;

import javax.annotation.security.RolesAllowed;
import java.util.*;
import java.util.stream.Collectors;

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
@PreserveOnRefresh
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private static Optional<String> business;
    private final Dialog dialog;
    private final H1 viewTitle;
    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;
    boolean constructed = false;
    private final UserCompanyProfile userCompanyProfile;
    private Select<Role> roleSelect;
    private ThisUser thisUser;
    private String businessId;
    private Nav nav;
    private String token;
    private UI current;

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
        Optional<ThisUser> thisUserOptional = AuthenticatedUser.get();
        if (thisUserOptional.isPresent()) {
            thisUser = thisUserOptional.get();
            Token token = thisUser.getToken();
            SpringVaadinSession.getCurrent().setAttribute("Token", token.getToken());

            // basic details
            if (thisUser.getPerson() == null) {
                event.forwardTo(PersonView.class);
                return;
            }

            // basic details
            if (thisUser.getPerson().getDefaultForms() == null) {
                event.forwardTo(PersonFormView.class);
                return;
            }

            // inactive user
            if (thisUser.getActive() == null || !thisUser.getActive()) {
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
        boolean genMenu = true;
        if (business.isEmpty()) {
            genMenu = StringUtils.isBlank(businessId) || !businessId.equalsIgnoreCase("0");
            businessId = "0";
        } else {
            String businessId1 = business.get();
            genMenu = StringUtils.isBlank(businessId) || !businessId.equalsIgnoreCase(businessId1);
            businessId = businessId1;
        }

        if (!constructed) {
//            UI current = UI.getCurrent();
//            new Thread(() -> {
//                current.access(() -> construct());
//            }).start();
            construct();
        }


        if(genMenu) {
            nav.removeAll();
            // Wrap the links in a list; improves accessibility
            current = UI.getCurrent();
            token = AuthenticatedUser.token();
            UnorderedList list = new UnorderedList();
            list.addClassNames("navigation-list");
            for (MenuItemInfo menuItem : createMenuItems()) {
//            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
//            }
            }
            //current.access(() -> {
            nav.add(list);
//                });


            List<Role> collect = thisUser.getUsersRoles().stream().filter(f -> {

                if (businessId.isEmpty() || businessId.equalsIgnoreCase("0")) {
                    return f.getBusiness() == null;
                } else {
                    return f.getBusinessId() != null && f.getBusinessId().toString().equalsIgnoreCase(businessId);
                }
            }).map(f -> f.getRole()).collect(Collectors.toList());
            roleSelect.setItems(collect);
            Optional<UsersRoles> max = thisUser.getUsersRoles().stream().//
                    filter(f -> collect.stream().anyMatch(g -> g.getId().compareTo(f.getRole().getId()) == 0)).//
                    max(Comparator.comparingLong(f -> f.getCounter() == null ? 0L : f.getCounter()));
            if (max.isPresent()) {
                UsersRoles usersRoles = max.get();
                roleSelect.setValue(usersRoles.getRole());
            }
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

        roleSelect = new Select<>();
        roleSelect.setReadOnly(true);
        roleSelect.setItemLabelGenerator(Role::getDescription);
        roleSelect.addValueChangeListener(f -> {
//            if(f.getValue() == null){
//                return;
//            }
//            UserService userService = ContextProvider.getBean(UserService.class);
//            userService.select(AuthenticatedUser.token(), Long.valueOf(businessId), f.getValue());
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout(roleSelect);
        horizontalLayout.setPadding(false);
        horizontalLayout.setMargin(false);
        horizontalLayout.setWidthFull();
        horizontalLayout.setAlignItems(FlexComponent.Alignment.END);
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        Header header = new Header(toggle, viewTitle, horizontalLayout);
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
        nav = new Nav();
        nav.addClassNames("menu-item-container");
        nav.getElement().setAttribute("aria-labelledby", "views");


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
//            if (accessChecker.hasAccess(menuItem.getView())) {
            list.add(menuItem);
//            }
        }
        return nav;
    }

    private MenuItemInfo[] createSettingItems() {
        return new MenuItemInfo[]{ //
                new MenuItemInfo(getTranslation("sr.we.settings"), "icons/menus/icons8_settings_48px.png", BusinessView.class, false)


        };
    }

    private MenuItemInfo[] createMenuItems() {
        List<MenuItemInfo> list = new ArrayList<>();
        UserAccessService userAccessService = ContextProvider.getBean(UserAccessService.class);


        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(LoanReportPrivilege.class), Privileges.READ)) {
            //current.access(() -> {
                MenuItemInfo dashboard = new MenuItemInfo(getTranslation("sr.we.dashboard"), "icons/menus/icons8_dashboard_48px.png", DashboardView.class);
                list.add(dashboard);
//            });
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(LoanPrivilege.class), Privileges.READ)) {
            //current.access(() -> {
                MenuItemInfo loans = new MenuItemInfo(getTranslation("sr.we.loans"), "icons/menus/icons8_debt_48px.png", LoanView.class);
                list.add(loans);
//            });
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(LoanRequestPrivilege.class), Privileges.READ)) {
            //current.access(() -> {
            MenuItemInfo loans = new MenuItemInfo(getTranslation("sr.we.loan.requests"), "icons/menus/icons8_lend_48px.png", RequestsView.class);
            list.add(loans);
//            });
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(CustomerPrivilege.class), Privileges.READ)) {
            //current.access(() -> {
                MenuItemInfo customers = new MenuItemInfo(getTranslation("sr.we.customers"), "icons/menus/icons8_customer_48px.png", CustomersView.class);
                list.add(customers);
//            });
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(PaymentsPrivilege.class), Privileges.INSERT)) {
            //current.access(() -> {
                MenuItemInfo transactions = new MenuItemInfo("Payments", "icons/menus/icons8_payment_history_48px.png", PaymentsView.class);
                list.add(transactions);
//            });
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(TransactionsPrivilege.class), Privileges.READ)) {
            //current.access(() -> {
                MenuItemInfo transactions = new MenuItemInfo("Transactions", "icons/menus/icons8_transaction_48px.png", TransactionsView.class);
                list.add(transactions);
//            });
        }

        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(InvoicesPrivilege.class), Privileges.READ)) {
            //current.access(() -> {
            MenuItemInfo transactions = new MenuItemInfo("Invoices", "icons/menus/icons8_invoice_48px.png", InvoiceView.class);
            list.add(transactions);
//            });
        }

        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(POSPrivilege.class), Privileges.INSERT)) {
            //current.access(() -> {
            MenuItemInfo transactions = new MenuItemInfo("Point of sale", "icons/menus/icons8_cash_register_48px.png", PosView.class);
            list.add(transactions);
//            });
        }

        /*if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(ProductsPrivilege.class), Privileges.READ)) {
            //current.access(() -> {
                MenuItemInfo products = new MenuItemInfo("Products", "icons/menus/icons8_product_48px.png", ProductView.class);
                list.add(products);
//            });
        }*/

        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(ServicesPrivilege.class), Privileges.READ)) {
            //current.access(() -> {
                MenuItemInfo products = new MenuItemInfo("Products & Services", "icons/menus/icons8_product_48px.png", ServiceView.class);
                list.add(products);
//            });
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(AccountsPrivilege.class), Privileges.READ)) {
            //current.access(() -> {
            MenuItemInfo products = new MenuItemInfo("Chart of accounts", "icons/menus/icons8_ledger_48px.png", ChartOfAccountsView.class);
            list.add(products);
//            });
        }
        return list.toArray(new MenuItemInfo[]{});
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("footer");

        Optional<ThisUser> maybeUser = AuthenticatedUser.get();
        if (maybeUser.isPresent()) {
            ThisUser user = maybeUser.get();

            Avatar avatar = new Avatar(user.getUsername()/*, user.getProfilePictureUrl()*/);
            avatar.setColorIndex(new Random().nextInt(7 - 1 + 1) + 1);
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
//            text.addClassNames("menu-item-text");

            Image img = new Image(iconClass, "icon by Icons8");
            img.setWidth("18px");
            img.setHeight("18px");
            img.getElement().getStyle().set("margin-inline-end","var(--lumo-space-s)");
            img.getElement().getStyle().set("margin-top","calc(var(--lumo-space-xs) * 0.5)");
            link.add(iconClass.startsWith("icons/menus/") ? img : new LineAwesomeIcon(iconClass), text);
            add(link);
        }

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this(menuTitle, iconClass, view, true);
        }

        public Class<?> getView() {
            return view;
        }


    }

    public static String getLocation(String business) {
        return "n/a/" + business;
    }
}
