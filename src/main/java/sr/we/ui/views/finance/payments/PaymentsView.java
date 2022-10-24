package sr.we.ui.views.finance.payments;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.LoanRequestService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.PaymentsPrivilege;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.finance.transactions.TransactionForm;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Route(value = "payments", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
//@PreserveOnRefresh
public class PaymentsView extends PaymentsForm implements BeforeEnterObserver, AfterNavigationObserver {


    public PaymentsView() {
        addClassName("loans-view");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        LoanRequestService loanService = ContextProvider.getBean(LoanRequestService.class);
        List<LoanRequest> list = loanService.list(AuthenticatedUser.token(), Long.valueOf(businessStringId), null).getResult();
        if (list != null) {
            list = list.stream().filter(f -> f.getStatus().compareTo(LoanRequest.Status.APPROVED) == 0).collect(Collectors.toList());
            grid.setItems(list);
        } else {

        }

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new PaymentsPrivilege(), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        if (business1.isPresent()) {
            businessStringId = business1.get();
        }
        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
        business = businessService.get(Long.valueOf(businessStringId), AuthenticatedUser.token());

        layout.removeAll();

        BigDecimal rest = null;
        LocalDate now = LocalDate.now();
        businessId = Long.valueOf(this.businessStringId);
        Currency fromCurrency = null;
        Currency selectedCurrency = null;
        Reference reference = Reference.LOAN_REQUEST_PLAN_DETAIL;
        transactionForm = new TransactionForm(rest, now, businessId, fromCurrency, selectedCurrency, reference, null);
        layout.add(transactionForm);
    }
}
