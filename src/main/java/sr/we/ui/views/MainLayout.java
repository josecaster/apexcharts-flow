package sr.we.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.History;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.spring.SpringVaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.CustomErrorHandler;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.UserService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.UsersRoles;
import sr.we.shekelflowcore.entity.helper.Token;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.MenuBuilder;
import sr.we.ui.components.NaviMenu;
import sr.we.ui.views.business.BusinessView;
import sr.we.ui.views.dashboard.NotOptimized;
import sr.we.ui.views.login.NotActiveDialog;
import sr.we.ui.views.person.PersonView;
import sr.we.ui.views.personform.PersonFormView;

import javax.annotation.security.RolesAllowed;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
public class MainLayout extends AppLayout implements BeforeEnterObserver, AfterNavigationObserver {

    private static Optional<String> business;
    private final Dialog dialog;
    private final H1 viewTitle;
    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;
    private final UserCompanyProfile userCompanyProfile;
    boolean constructed = false;
    private Select<Role> roleSelect;
    private ThisUser thisUser;
    private String businessId;
    private Nav nav;
    private String token;
    private UI current;
    private HorizontalLayout breadCrumbLayout;


    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        dialog = new Dialog();
        viewTitle = new H1();

        VaadinSession.getCurrent().setErrorHandler(new CustomErrorHandler());

        userCompanyProfile = new UserCompanyProfile(dialog);
    }

    public static String getLocation(String business) {
        return "n/a/" + business;
    }

    public static boolean isMobileDevice() {
        WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        return webBrowser.isAndroid() || webBrowser.isIPhone() || webBrowser.isWindowsPhone();
    }

//    private Component createHeaderContent() {
//        DrawerToggle toggle = new DrawerToggle();
//        toggle.addClassNames("view-toggle");
//        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
//        toggle.getElement().setAttribute("aria-label", "Menu toggle");
//
//
//        viewTitle.addClassNames("view-title");
//
//        roleSelect = new Select<>();
//        roleSelect.setReadOnly(true);
//        roleSelect.setItemLabelGenerator(Role::getDescription);
//        roleSelect.addValueChangeListener(f -> {
////            if(f.getValue() == null){
////                return;
////            }
////            UserService userService = ContextProvider.getBean(UserService.class);
////            userService.select(AuthenticatedUser.token(), Long.valueOf(businessId), f.getValue());
//        });
//        HorizontalLayout horizontalLayout = new HorizontalLayout(roleSelect);
//        horizontalLayout.setPadding(false);
//        horizontalLayout.setMargin(false);
//        horizontalLayout.setWidthFull();
//        horizontalLayout.setAlignItems(FlexComponent.Alignment.END);
//        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
//        Header header = new Header(toggle, viewTitle, horizontalLayout);
//        header.addClassNames("view-header");
//
//        return header;
//    }

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



        if (genMenu) {
            nav.removeAll();
            initNaviItems();
            // Wrap the links in a list; improves accessibility
            current = UI.getCurrent();
            token = AuthenticatedUser.token();

            UserService userService = ContextProvider.getBean(UserService.class);
            thisUser = userService.get(thisUser.getId(), AuthenticatedUser.token());
            List<Role> collect = thisUser.getUsersRoles().stream().filter(f -> {

                if (businessId.isEmpty() || businessId.equalsIgnoreCase("0")) {
                    return f.getBusiness() == null;
                } else {
                    return f.getBusinessId() != null && f.getBusinessId().toString().equalsIgnoreCase(businessId);
                }
            }).map(UsersRoles::getRole).collect(Collectors.toList());
            if (collect.isEmpty()) {
                BusinessService businessService = ContextProvider.getBean(BusinessService.class);
                businessService.unselectAll(token);
                event.forwardTo(ReRouteLayout.class);
            } else {
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
        if ((StringUtils.isBlank(businessId) || businessId.equalsIgnoreCase("0")) ) {
            if(!dialog.isOpened()) {
                userCompanyProfile.click();
            }
            dialog.setModal(true);
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);
            userCompanyProfile.cancel(false);
            return;
        }

        dialog.setModal(false);
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        userCompanyProfile.cancel(true);
    }

    @Override
    public void setContent(Component content) {
        if (isMobileDevice()) {
            if (content != null) {
                BreadCrumb annotation = content.getClass().getAnnotation(BreadCrumb.class);
                if (annotation != null && !annotation.optimizedMobile()) {
                    super.setContent(new NotOptimized());
                    return;
                }
            }
        }
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

//		viewTitle = new H1();
//		viewTitle.addClassNames("view-title");
        breadCrumbLayout = new HorizontalLayout();
//        UIUtils.setTextColor("white", breadCrumbLayout);
        breadCrumbLayout.setWidthFull();

//        initAccountMenu();

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
//        HorizontalLayout horizontalLayout = new HorizontalLayout(roleSelect);
//        horizontalLayout.setPadding(false);
//        horizontalLayout.setMargin(false);
//        horizontalLayout.setWidthFull();
//        horizontalLayout.setAlignItems(FlexComponent.Alignment.END);
//        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Header header = new Header(toggle, breadCrumbLayout/*, horizontalLayout*/);
        header.addClassNames("view-header");
        header.addClassName(LumoUtility.Padding.Right.MEDIUM);
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
        return initNaviItems();
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

//    @Override
//    protected void afterNavigation() {
//        super.afterNavigation();
//        viewTitle.setText(getCurrentPageTitle());
//        if (dialog != null && dialog.isOpened()) {
//            dialog.close();
//        }
//    }

    private Nav initNaviItems() {

        NaviMenu menu = new NaviMenu(businessId);
        nav.add(menu);
        menu.removeAll();
        new MenuBuilder(businessId).buildMenu(menu);
        return nav;
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
            userMenu.addItem("Logout", e -> AuthenticatedUser.logout());

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
    public void afterNavigation(AfterNavigationEvent event) {
        if (StringUtils.isBlank(businessId) || businessId.equalsIgnoreCase("0")) {
            return;
        }
        breadCrumbLayout.removeAll();
        breadCrumbLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        BreadCrumb breadCrumb = event.getLocationChangeEvent().getRouteTargetChain().get(0).getClass().getAnnotation(BreadCrumb.class);
        if (breadCrumb != null) {
            // get parent route and title
            Class<? extends Component> parentNavigationTarget = breadCrumb.parentNavigationTarget();
            if (parentNavigationTarget != BreadCrumb.NONE.class) {
                List<RouteData> routes = RouteConfiguration.forSessionScope().getAvailableRoutes();
                Optional<RouteData> optional = routes.stream().filter(p -> p.getNavigationTarget() == parentNavigationTarget).findFirst();
                BreadCrumb parentBreadcrumb = breadCrumb.parentNavigationTarget().getAnnotation(BreadCrumb.class);
                String titleKey = parentBreadcrumb.titleKey();
                Anchor anchor = new Anchor(optional.get().getTemplate(), getTranslation(titleKey));
//                UIUtils.setTextColor("white", anchor);
                Button back = new Button("Go Back");
                back.addClickListener(f -> {
                    History history = UI.getCurrent().getPage().getHistory();
                    history.back();
                });
                breadCrumbLayout.add(back, new Span("/"));
            }

            String titleKey = breadCrumb.titleKey();
            Span viewTitle = new Span(getTranslation(titleKey));
            viewTitle.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.LARGE);
            breadCrumbLayout.add(viewTitle);

        }

//		}
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
            img.getElement().getStyle().set("margin-inline-end", "var(--lumo-space-s)");
            img.getElement().getStyle().set("margin-top", "calc(var(--lumo-space-xs) * 0.5)");
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
}
