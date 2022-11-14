package sr.we.ui.views.finance.loans.tabs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.yuri0x7c1.bslayout.BsColumn;
import org.vaadin.addons.yuri0x7c1.bslayout.BsLayout;
import org.vaadin.addons.yuri0x7c1.bslayout.BsRow;
import sr.we.ContextProvider;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanReportPrivilege;
import sr.we.ui.views.finance.loans.LoansView;

import javax.annotation.security.RolesAllowed;

import static sr.we.ui.views.dashboard.DashboardView.createHighlight;

@Route(value = "dashboard", layout = LoansView.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
//@PreserveOnRefresh
public class LTabDashboard extends VerticalLayout implements BeforeEnterObserver {

    public LTabDashboard() {
        var board = new BsLayout();
        board.withRows(new BsRow().withColumns(new BsColumn(createHighlight("Current users", "745", 33.7)).withSize(BsColumn.Size.XS), new BsColumn(createHighlight("View events", "54.6k", -112.45)).withSize(BsColumn.Size.XS), new BsColumn(createHighlight("Conversion rate", "18%", 3.9)).withSize(BsColumn.Size.XS), new BsColumn(createHighlight("Custom metric", "-123.45", 0.0)).withSize(BsColumn.Size.XS)));
//        board.addRow(DashboardView.createViewEvents());
//        board.addRow(DashboardView.createServiceHealth(), DashboardView.createResponseTimes());
        add(board);
    }

    public static String getLocation(String business, String loan) {
        return LoansView.getLocation(business, loan) + "/dashboard";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanReportPrivilege(), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
    }
}
