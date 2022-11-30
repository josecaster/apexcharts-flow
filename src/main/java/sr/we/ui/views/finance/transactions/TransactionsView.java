package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import sr.we.ContextProvider;
import sr.we.CustomNotificationHandler;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.PaymentTransactionService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.PaymentTransaction;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.InterExecutable;
import sr.we.shekelflowcore.entity.helper.vo.PaymentTransactionVO;
import sr.we.shekelflowcore.enums.TransactionType;
import sr.we.shekelflowcore.exception.PrimaryThrowable;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.TransactionsPrivilege;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.NotYetChange;
import sr.we.ui.components.NotYetClick;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private String businessString;
    @Id("transactions-grid-layout")
    private Div transactionsGridLayout;
    @Id("add-income-btn")
    private Button addIncomeBtn;
    @Id("add-expense-btn")
    private Button addExpenseBtn;
    @Id("more-btn")
    private Button moreBtn;
    @Id("filter-field")
    private TextField filterField;
    private Business business;
    private Set<PaymentTransaction> paymentTransactions;

    /**
     * Creates a new TransactionsView.
     */
    public TransactionsView() {
        // You can initialise any data required for the connected UI components here.



        transactionGrid = new TransactionGrid();
        transactionsGridLayout.add(transactionGrid);


        moreBtn.addClickListener(f -> {
            JournalentryDialog journalentryDialog = new JournalentryDialog(Long.valueOf(businessString));
            journalentryDialog.setRefresh(() -> {
                transactionGrid.afterNavigation();
                return null;
            });
            journalentryDialog.open();
        });
        addIncomeBtn.addClickListener(f -> {
            TransactionDialog transactionDialog = new TransactionDialog(null, LocalDate.now(), Long.valueOf(businessString), business.getCurrency(), business.getCurrency(), null, null, TransactionType.DEPOSIT,null);
            transactionDialog.setRefresh(() -> {
                transactionGrid.afterNavigation();
                return null;
            });
            transactionDialog.open();
        });
        addExpenseBtn.addClickListener(f -> {
            TransactionDialog transactionDialog = new TransactionDialog(null, LocalDate.now(), Long.valueOf(businessString), business.getCurrency(), business.getCurrency(), null, null, TransactionType.WITHDRAWAL,null);
            transactionDialog.setRefresh(() -> {
                transactionGrid.afterNavigation();
                return null;
            });
            transactionDialog.open();
        });
        filterField.addValueChangeListener(new NotYetChange<>());
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
            businessString = business1.get();
            transactionGrid.setBusiness(businessString);
            BusinessService businessService = ContextProvider.getBean(BusinessService.class);
            business = businessService.get(Long.valueOf(businessString), AuthenticatedUser.token());
        }
    }

}
