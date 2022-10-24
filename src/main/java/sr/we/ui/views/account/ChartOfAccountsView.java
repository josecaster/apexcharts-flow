package sr.we.ui.views.account;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import sr.we.ContextProvider;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.enums.ChartOfAccountTypes;
import sr.we.shekelflowcore.enums.ChartOfAccounts;
import sr.we.shekelflowcore.security.PrivilegeModeAbstract;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.AccountsPrivilege;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;

/**
 * A Designer generated component for the chart-of-accounts-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@BreadCrumb(titleKey = "sr.we.chart.of.accounts")
@Tag("chart-of-accounts-view")
@JsModule("./src/views/accounts/chart-of-accounts-view.ts")
@Route(value = "chart-of-accounts", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class ChartOfAccountsView extends LitTemplate implements BeforeEnterObserver {

    private final List<ChartOfAccountTypes> list;
    @Id("add-new-accounts-btn")
    private Button addNewAccountsBtn;
    //    @Id("assets-tab")
    private final Tab assetsTab;
    //    @Id("lcc-tab")
    private final Tab lccTab;
    //    @Id("inc-tab")
    private final Tab incTab;
    //    @Id("exp-tab")
    private final Tab expTab;
    //    @Id("eq-tab")
    private final Tab eqTab;
    @Id("account-view-layout")
    private Div accountViewLayout;
    @Id("chart-tab")
    private Tabs chartTab;
    @Id("main-char-of-accounts-layout")
    private VerticalLayout mainCharOfAccountsLayout;
    private Long businessId;
    @Id("chart-of-accounts-charts-layout")
    private VerticalLayout chartOfAccountsChartsLayout;

    /**
     * Creates a new ChartOfAccountsView.
     */
    public ChartOfAccountsView() {
        // You can initialise any data required for the connected UI components here.

        chartOfAccountsChartsLayout.setMaxWidth("1000px");

        assetsTab = new Tab("Assets");
        lccTab = new Tab("Liabilities & Credit Cards");
        incTab = new Tab("Income");
        expTab = new Tab("Expenses");
        eqTab = new Tab("Equity");

        chartTab.add(assetsTab, lccTab, incTab, expTab, eqTab);

        list = List.of(ChartOfAccountTypes.values());
        chartTab.addSelectedChangeListener(f -> {
            accountViewLayout.removeAll();


            if (f.getSelectedTab().equals(assetsTab)) {
                addAccounts(ChartOfAccounts.ASSETS);
            } else if (f.getSelectedTab().equals(lccTab)) {
                addAccounts(ChartOfAccounts.LCC);
            } else if (f.getSelectedTab().equals(incTab)) {
                addAccounts(ChartOfAccounts.INC);
            } else if (f.getSelectedTab().equals(expTab)) {
                addAccounts(ChartOfAccounts.EXP);
            } else if (f.getSelectedTab().equals(eqTab)) {
                addAccounts(ChartOfAccounts.EQ);
            }
        });

        chartTab.setSelectedTab(assetsTab);

    }

    private void addAccounts(ChartOfAccounts assets) {
        List<ChartOfAccountTypes> collect = list.stream().filter(g -> {
            return g.getChartOfAccounts().compareTo(assets) == 0;
        }).toList();
        for (ChartOfAccountTypes accountCodes : collect) {
            AccountView accountView = new AccountView();
            accountViewLayout.add(accountView);
            accountView.build(accountCodes, businessId);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccessService = ContextProvider.getBean(UserAccessService.class);
        String token = AuthenticatedUser.token();
        boolean hasAccess = userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(AccountsPrivilege.class), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        if (business1.isPresent()) {
            String business = business1.get();
            businessId = Long.valueOf(business);
        }
        addAccounts(ChartOfAccounts.ASSETS);
    }
}
