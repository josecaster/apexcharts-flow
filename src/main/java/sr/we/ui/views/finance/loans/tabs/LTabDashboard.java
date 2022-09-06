package sr.we.ui.views.finance.loans.tabs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import sr.we.ContextProvider;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanReportPrivilege;
import sr.we.ui.views.finance.loans.LoansView;
import sr.we.ui.views.dashboard.DashboardView;

import javax.annotation.security.RolesAllowed;

import static sr.we.ui.views.dashboard.DashboardView.createHighlight;

@Route(value = "dashboard", layout = LoansView.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
//@PreserveOnRefresh
public class LTabDashboard extends VerticalLayout implements BeforeEnterObserver {

    public static String getLocation(String business, String loan) {
        return LoansView.getLocation(business,loan)+"/dashboard";
    }

    public LTabDashboard() {
        var board = new Board();
        board.addRow(createHighlight("Current users", "745", 33.7), createHighlight("View events", "54.6k", -112.45),
                createHighlight("Conversion rate", "18%", 3.9), createHighlight("Custom metric", "-123.45", 0.0));
//        board.addRow(DashboardView.createViewEvents());
//        board.addRow(DashboardView.createServiceHealth(), DashboardView.createResponseTimes());
        add(board);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanReportPrivilege(), Privileges.READ);
        if(!hasAccess){
            UI.getCurrent().navigate(AboutView.class);
        }
    }
}
