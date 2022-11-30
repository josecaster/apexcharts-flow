package sr.we.ui.views.reports;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import sr.we.ContextProvider;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.AccountsPrivilege;
import sr.we.shekelflowcore.security.privileges.ServicesPrivilege;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.NotYetClick;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

/**
 * A Designer generated component for the reports-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@BreadCrumb(titleKey = "sr.we.reports")
@Tag("reports-view")
@JsModule("./src/views/reports/reports-view.ts")
@Route(value = "reports", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class ReportsView extends LitTemplate implements BeforeEnterObserver {

    @Id("profit-loss")
    private VerticalLayout profitLoss;
    @Id("balance-sheet")
    private VerticalLayout balanceSheet;
    @Id("cash-flow")
    private VerticalLayout cashFlow;
    private String business;

    /**
     * Creates a new ReportsView.
     */
    public ReportsView() {
        // You can initialise any data required for the connected UI components here.
        profitLoss.addClickListener(f -> {
            UI.getCurrent().navigate(ProfitLossView.getLocation(Long.valueOf(business).toString()));
        });
        cashFlow.addClickListener(new NotYetClick<>());
        balanceSheet.addClickListener(f -> {
            UI.getCurrent().navigate(BalanceSheetView.getLocation(Long.valueOf(business).toString()));
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new AccountsPrivilege(), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        business1.ifPresent(s -> business = s);
    }

}
