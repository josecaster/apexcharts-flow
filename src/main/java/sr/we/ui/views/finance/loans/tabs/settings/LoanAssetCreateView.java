package sr.we.ui.views.finance.loans.tabs.settings;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
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
import sr.we.shekelflowcore.security.privileges.LoanAssetsPrivilege;
import sr.we.ui.views.finance.loans.tabs.LTabRequests;
import sr.we.ui.views.business.BusinessView;
import sr.we.ui.views.finance.loans.LoansView;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@Route(value = "loan-asset-new", layout = LoansView.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
//@PreserveOnRefresh
public class LoanAssetCreateView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver {

    private final Dialog dialog;
    private final LoanAssetCreateLayout loanAssetViewNewLayout;

    public static String getLocation(String business, String loan) {
        return LoansView.getLocation(business, loan) + "/loan-asset-new";
    }
    private String business;
    private Loan loan;


    public LoanAssetCreateView() {

        loanAssetViewNewLayout = new LoanAssetCreateLayout();
        dialog = new Dialog(loanAssetViewNewLayout);
        dialog.setHeaderTitle("Add new loan assets types");
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> {
            dialog.close();
            redirectToParent();
        });
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.setModal(true);
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        add(dialog);

    }

    private void redirectToParent() {
//        QueryParameters queryParameters = QueryParameters.fromString("id=" + loan.getId());
//        UI.getCurrent().navigate(LoansViewTabRequests.getLocation(business), queryParameters);
        UI.getCurrent().navigate(LTabRequests.class, //
                new RouteParameters(//
                        new RouteParam("business", business),//
                        new RouteParam("loan", loan.getId().toString())));
    }



    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanAssetsPrivilege(), Privileges.INSERT);
        if(!hasAccess){
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
        loanAssetViewNewLayout.setBusiness(business);
        loanAssetViewNewLayout.setLoan(loan);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        dialog.open();
    }
}
