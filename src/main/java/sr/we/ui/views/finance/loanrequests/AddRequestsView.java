package sr.we.ui.views.finance.loanrequests;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Loan;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanRequestPrivilege;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.business.BusinessView;
import sr.we.ui.views.finance.loans.LoansView;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@Route(value = "requests-new", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class AddRequestsView extends AddRequests implements BeforeEnterObserver {

    private String business;

    public AddRequestsView() {
        assetsForm.setVisible(false);
        provisionForm.setVisible(false);
        repaymentForm.setVisible(false);
        boardLayout.setVisible(false);
        formLayout.setColspan(requestorForm,3);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanRequestPrivilege(), Privileges.INSERT);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        RouteParameters routeParameters = event.getRouteParameters();
        Optional<String> business1 = routeParameters.get("business");
        business1.ifPresent(s -> business = s);
        setBusiness(business);
    }
}
