package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.PaymentTransactionService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.TransactionsPrivilege;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

/**
 * A Designer generated component for the transactions-view template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */

@BreadCrumb(titleKey = "sr.we.transactions")
@Route(value = "transaction", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
@Tag("transactions-view")
@JsModule("./src/views/finance/transactions/transactions-view.ts")
public class TransactionsView extends LitTemplate implements AfterNavigationObserver, HasDynamicTitle, BeforeEnterObserver {

    private final TransactionGrid transactionGrid;
    private String business;
    @Id("transactions-grid-layout")
    private Div transactionsGridLayout;

    /**
     * Creates a new TransactionsView.
     */
    public TransactionsView() {
        // You can initialise any data required for the connected UI components here.

        transactionGrid = new TransactionGrid();
        transactionsGridLayout.add(transactionGrid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        transactionGrid.afterNavigation();
    }


    @Override
    public String getPageTitle() {
        return "Payment Transactions";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new TransactionsPrivilege(), Privileges.READ);
        if(!hasAccess){
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        if (business1.isPresent()) {
            business = business1.get();
            transactionGrid.setBusiness(business);
        }
    }

}
