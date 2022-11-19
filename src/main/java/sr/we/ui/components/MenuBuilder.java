package sr.we.ui.components;

import com.vaadin.flow.component.UI;
import sr.we.ContextProvider;
import sr.we.data.controller.UserAccessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.security.PrivilegeModeAbstract;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.*;
import sr.we.ui.views.account.ChartOfAccountsView;
import sr.we.ui.views.currencyexchange.CurrencyExchangeView;
import sr.we.ui.views.customers.CustomerView;
import sr.we.ui.views.dashboard.DashboardView;
import sr.we.ui.views.dashboard.MainDashboardView;
import sr.we.ui.views.finance.loanrequests.RequestsView;
import sr.we.ui.views.finance.loans.LoanView;
import sr.we.ui.views.finance.transactions.TransactionsView;
import sr.we.ui.views.invoice.InvoiceView;
import sr.we.ui.views.pos.PosView;
import sr.we.ui.views.pos.TicketsGridView;
import sr.we.ui.views.reports.ReportsView;
import sr.we.ui.views.services.ServiceView;

import java.util.List;
import java.util.Locale;

public class MenuBuilder {

    private final Locale locale;
    private final String businessId;

    public MenuBuilder(String businessId) {
        super();
        locale = UI.getCurrent().getLocale();
        this.businessId = businessId;
    }

    public NaviMenu buildMenu(NaviMenu menu) {
        menu.removeAll();

        UserAccessService userAccessService = ContextProvider.getBean(UserAccessService.class);

        String token = AuthenticatedUser.token();

        UI current = UI.getCurrent();
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(LoanReportPrivilege.class), Privileges.READ)) {
            menu.addNaviItem(current.getTranslation("sr.we.dashboard"), "icons/menus/icons8_dashboard_48px.png", DashboardView.class);
        } else if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(TransactionsPrivilege.class), Privileges.READ)){
            menu.addNaviItem(current.getTranslation("sr.we.dashboard"), "icons/menus/icons8_dashboard_48px.png", MainDashboardView.class);
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(LoanPrivilege.class), Privileges.READ)) {
            menu.addNaviItem(current.getTranslation("sr.we.loans"), "icons/menus/icons8_debt_48px.png", LoanView.class);
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(LoanRequestPrivilege.class), Privileges.READ)) {
            menu.addNaviItem(current.getTranslation("sr.we.loan.requests"), "icons/menus/icons8_lend_48px.png", RequestsView.class);
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(CurrencyExchangePrivilege.class), Privileges.READ)) {
            menu.addNaviItem(current.getTranslation("sr.we.currency.exchange"), "icons/menus/icons8_currency_exchange_48px.png", CurrencyExchangeView.class);
        }

        NaviItem incomeParent = menu.addNaviItem("Income", "icons/menus/icons8_budget_48px.png", null);
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(CustomerPrivilege.class), Privileges.READ)) {
            menu.addNaviItem(incomeParent, current.getTranslation("sr.we.customers"), "icons/menus/icons8_customer_48px.png", CustomerView.class);
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(InvoicesPrivilege.class), Privileges.READ)) {
            menu.addNaviItem(incomeParent, "Invoices", "icons/menus/icons8_invoice_48px.png", InvoiceView.class);
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(POSPrivilege.class), Privileges.INSERT)) {
            menu.addNaviItem(incomeParent, "Point of sale", "icons/menus/icons8_cash_register_48px.png", PosView.class);
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(ServicesPrivilege.class), Privileges.READ)) {
            menu.addNaviItem(incomeParent, "Products & Services", "icons/menus/icons8_product_48px.png", ServiceView.class);
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(POSPrivilege.class), Privileges.READ)) {
            menu.addNaviItem(incomeParent,"Tickets", "icons/menus/icons8_receipt_48px.png", TicketsGridView.class);
        }

        NaviItem journalsParent = menu.addNaviItem("Transactions", "icons/menus/icons8_transaction_48px_2.png", null);


        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(TransactionsPrivilege.class), Privileges.READ)) {
            menu.addNaviItem(journalsParent, "Records", "icons/menus/icons8_transaction_48px.png", TransactionsView.class);
        }


        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(AccountsPrivilege.class), Privileges.READ)) {
            menu.addNaviItem(journalsParent, "Chart of accounts", "icons/menus/icons8_ledger_48px.png", ChartOfAccountsView.class);
        }
        if (userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(AccountsPrivilege.class), Privileges.READ)) {
            menu.addNaviItem("Reports", "icons/menus/icons8_chart_48px.png", ReportsView.class);
        }


        List<NaviItem> naviItems = menu.getNaviItems();
//        naviItems.stream().forEach(p -> checkAccess(menu, p));

        return menu;
    }

//    private Object checkAccess(NaviMenu naviMenu, NaviItem naviItem) {
//        Class<? extends Component> navigationTarget = naviItem.getNavigationTarget();
//        if (navigationTarget != null) {
//            AllowedRoles allowedRoles = navigationTarget.getAnnotation(AllowedRoles.class);
//            if (allowedRoles != null) {
//                List<RoleCode> roleCodes = Arrays.asList(allowedRoles.value());
//                boolean match = roleCodes.stream().anyMatch(p -> p.equals(user.getRole().getCode()));
//                if (!match) {
//                    if (naviItem.getSuperItem() != null) {
//                        naviItem.getSuperItem().getSubItems().remove(naviItem);
//                    }
//                    naviMenu.remove(naviItem);
//                }
//            }
//        }
//        List<NaviItem> subItems = naviItem.getSubItems();
//        ArrayList<NaviItem> list = new ArrayList<>(subItems);
//        if (subItems != null) {
//            for (NaviItem subItem : list) {
//                checkAccess(naviMenu, subItem);
//            }
//        }
//        subItems = naviItem.getSubItems();
//        if (naviItem.getNavigationTarget() == null && (subItems == null || subItems.isEmpty())) {
//            naviMenu.remove(naviItem);
//        }
//
//        return null;
//    }
//
//	private void setMenuVisibility(View view) {
//		NaviItem naviItem = menus.get(view);
//		if (naviItem != null) {
//			naviItem.setVisible(true);
//			setSuperItemVisibility(naviItem);
//		}
//	}

    private void setSuperItemVisibility(NaviItem naviItem) {
        NaviItem superItem = naviItem.getSuperItem();
        if (superItem != null) {
            superItem.setVisible(true);
            setSuperItemVisibility(superItem);
        }
    }
}