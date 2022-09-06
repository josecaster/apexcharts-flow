package sr.we.ui.views.finance.loanrequests;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Customer;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanRequestPrivilege;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.ReRouteLayout;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static sr.we.ContextProvider.getBean;

@Route(value = "edit-requests", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class EditRequestsView extends AddRequests implements BeforeEnterObserver {

    private String business;
    private LoanRequest loanRequest;

    public static String getLocation(String business) {
        return MainLayout.getLocation(business) + "/edit-requests";
    }

    public EditRequestsView() {
//        assetsForm.setVisible(false);
//        provisionForm.setVisible(false);
//        repaymentForm.setVisible(false);
//        formLayout.setColspan(requestorForm,3);
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

        QueryParameters queryParams = event.getLocation().getQueryParameters();
        List<String> id1 = queryParams.getParameters().get("id");
        Optional<String> id = id1.stream().findAny();
        if (id.isEmpty()) {
            event.forwardTo(ReRouteLayout.class);
            throw new ValidationException("Invalid Link");
        }
        String token = AuthenticatedUser.token();
        LoanRequestService loanRequestService = getBean(LoanRequestService.class);
        loanRequest = loanRequestService.get(Long.valueOf(id.get()), token);
        if (loanRequest == null) {
            event.forwardTo(ReRouteLayout.class);
            throw new ValidationException("Invalid Link");
        }
        setLoanRequest(loanRequest);
    }


}
