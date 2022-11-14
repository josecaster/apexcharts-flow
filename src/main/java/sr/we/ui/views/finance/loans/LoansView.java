package sr.we.ui.views.finance.loans;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Loan;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.*;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.business.BusinessView;
import sr.we.ui.views.finance.loans.tabs.*;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

//@Route(value = "", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
@RoutePrefix("view-loan/:loan")
@ParentLayout(MainLayout.class)
//@PreserveOnRefresh
public class LoansView extends VerticalLayout implements RouterLayout, HasDynamicTitle, BeforeEnterObserver {
    private final H2 header;
    private final Span currency;
    private String business;
    private Loan loan;

    public LoansView() {
        addClassName("loans-view");
        setSizeFull();

        MenuBar menus = new MenuBar();
        menus.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        dashboard(menus);
        newRequests(menus);
        payments(menus);
        customers(menus);
        settings(menus);


        header = new H2();


        add(header);
        currency = new Span();
        currency.addClassNames("text-secondary", "text-xs");
        add(currency);
        add(menus);
    }

    public static String getLocation(String business, String loan) {
        return MainLayout.getLocation(business) + "/view-loan/" + loan;
    }

    private void settings(MenuBar menus) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanAssetsPrivilege(), Privileges.READ);
        if (!hasAccess) {
            return;
        }
        menus.addItem(getTranslation("sr.we.settings"), e -> {
//            QueryParameters queryParameters = QueryParameters.fromString("id=" + loan.getId());
//            UI.getCurrent().navigate(LoansViewTabSettings.getLocation(business), queryParameters);
            UI.getCurrent().navigate(LTabSettings.class, //
                    new RouteParameters(//
                            new RouteParam("business", business),//
                            new RouteParam("loan", loan.getId().toString())));
        });
    }

    private void customers(MenuBar menus) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new CustomerPrivilege(), Privileges.READ);
        if (!hasAccess) {
            return;
        }
        menus.addItem(getTranslation("sr.we.customers"), e -> {
//            QueryParameters queryParameters = QueryParameters.fromString("id=" + loan.getId());
//            UI.getCurrent().navigate(LoansViewTabCustomers.getLocation(business), queryParameters);
            UI.getCurrent().navigate(LTabCustomers.class, //
                    new RouteParameters(//
                            new RouteParam("business", business),//
                            new RouteParam("loan", loan.getId().toString())));
        });
    }

    private void payments(MenuBar menus) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new TransactionsPrivilege(), Privileges.READ);
        if (!hasAccess) {
            return;
        }
        menus.addItem(getTranslation("sr.we.payments"), e -> {
//            QueryParameters queryParameters = QueryParameters.fromString("id=" + loan.getId());
//            UI.getCurrent().navigate(LoansViewTabPayments.getLocation(business), queryParameters);
            UI.getCurrent().navigate(LTabPayments.class, //
                    new RouteParameters(//
                            new RouteParam("business", business),//
                            new RouteParam("loan", loan.getId().toString())));
        });
    }

    private void newRequests(MenuBar menus) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanRequestPrivilege(), Privileges.READ);
        if (!hasAccess) {
            return;
        }
        menus.addItem(getTranslation("sr.we.new.requests"), e -> {
//            QueryParameters queryParameters = QueryParameters.fromString("id=" + loan.getId());
//            UI.getCurrent().navigate(LoansViewTabRequests.getLocation(business), queryParameters);
            UI.getCurrent().navigate(LTabRequests.class, //
                    new RouteParameters(//
                            new RouteParam("business", business),//
                            new RouteParam("loan", loan.getId().toString())));
        });
    }

    private void dashboard(MenuBar menus) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanReportPrivilege(), Privileges.READ);
        if (!hasAccess) {
            return;
        }
        menus.addItem(getTranslation("sr.we.dashboard"), e -> {
//            QueryParameters queryParameters = QueryParameters.fromString("id=" + loan.getId());
//            UI.getCurrent().navigate(LoansViewTabDashboard.getLocation(business, loan.getId().toString()), queryParameters);
            UI.getCurrent().navigate(LTabDashboard.class, //
                    new RouteParameters(//
                            new RouteParam("business", business),//
                            new RouteParam("loan", loan.getId().toString())));
        });
    }


    @Override
    public String getPageTitle() {
        return getTranslation("sr.we.dashboard");
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanPrivilege(), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        RouteParameters routeParameters = event.getRouteParameters();
        Optional<String> business1 = routeParameters.get("business");
        if (business1.isPresent()) {
            business = business1.get();
        }
        Optional<String> loanId = routeParameters.get("loan");
//        QueryParameters queryParams = event.getLocation().getQueryParameters();
//        List<String> id1 = queryParams.getParameters().get("id");
//        Optional<String> id = id1.stream().findAny();
        if (loanId.isEmpty()) {
            event.forwardTo(BusinessView.class);
            throw new ValidationException("Invalid Authentication");
        }
        String token = AuthenticatedUser.token();
        LoanService loanService = ContextProvider.getBean(LoanService.class);
        loan = loanService.get(Long.valueOf(loanId.get()), token);


        header.setText(loan.getName());
        LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-pen");
        lineAwesomeIcon.addClickListener(f -> {
//            QueryParameters queryParameters = QueryParameters.fromString("id=" + loan.getId());
//            UI.getCurrent().navigate(LoansViewEdit.getLocation(business), queryParameters);
            UI.getCurrent().navigate(LoansEditView.class, //
                    new RouteParameters(//
                            new RouteParam("business", business),//
                            new RouteParam("loan", loan.getId().toString())));
        });
        header.add(lineAwesomeIcon);
        currency.setText(loan.getCurrency().getName());
    }

}
