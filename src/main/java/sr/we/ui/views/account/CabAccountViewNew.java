package sr.we.ui.views.account;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import sr.we.shekelflowcore.entity.AccountType;
import sr.we.shekelflowcore.entity.Loan;
import sr.we.shekelflowcore.entity.Role;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.dashboard.DashboardView;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@Route(value = "cab-account-new", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class CabAccountViewNew extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver {

    private final Dialog dialog;
    private final AccountNewLayout loanRequestStateView;

    public static String getLocation(String business) {
        return MainLayout.getLocation(business) + "/cab-account-new";
    }
    private String business;
    private Loan loan;


    public CabAccountViewNew() {

        loanRequestStateView = new AccountNewLayout();
        loanRequestStateView.setAccountType(AccountType.CAB);
        dialog = new Dialog(loanRequestStateView);
        dialog.setHeaderTitle("Add account");
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
        UI.getCurrent().navigate(DashboardView.class, new RouteParameters(new RouteParam("business", business)));
    }



    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters routeParameters = event.getRouteParameters();
        Optional<String> business1 = routeParameters.get("business");
        if (business1.isPresent()) {
            business = business1.get();
        }
        loanRequestStateView.setBusiness(business);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        dialog.open();
    }
}
