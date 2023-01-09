package sr.we.ui.views.finance.loans.tabs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.LoanRequestService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.TransactionsPrivilege;
import sr.we.ui.views.business.BusinessView;
import sr.we.ui.views.finance.loans.LoansView;
import sr.we.ui.views.finance.payments.PaymentsForm;
import sr.we.ui.views.finance.transactions.TransactionForm;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Route(value = "payments", layout = LoansView.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
//@PreserveOnRefresh
public class LTabPayments extends PaymentsForm implements BeforeEnterObserver, AfterNavigationObserver {
    private String loanId;



    public static String getLocation(String business, String loan) {
        return LoansView.getLocation(business, loan) + "/payments";
    }



    @Override
    public void afterNavigation(AfterNavigationEvent event) {
//        LoanRequestService loanService = ContextProvider.getBean(LoanRequestService.class);TODO
//        List<LoanRequest> list = loanService.list(AuthenticatedUser.token(), Long.valueOf(businessStringId), Long.valueOf(loanId)).getResult();
//        if(list != null) {
//            list = list.stream().filter(f -> f.getStatus().compareTo(LoanRequest.Status.APPROVED) == 0).collect(Collectors.toList());
//            grid.setItems(list);
//        } else {
//
//        }

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
            businessStringId = business1.get();
        }
        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
        business = businessService.get(Long.valueOf(businessStringId), AuthenticatedUser.token());
        Optional<String> loan = event.getRouteParameters().get("loan");
//        QueryParameters queryParams = event.getLocation().getQueryParameters();
//        List<String> id1 = queryParams.getParameters().get("id");
//        Optional<String> id = id1.stream().findAny();
        if (loan.isEmpty()) {
            event.forwardTo(BusinessView.class);
            throw new ValidationException("Invalid Authentication");
        } else {
            loanId = loan.get();
        }

        layout.removeAll();

        BigDecimal rest = null;
        LocalDate now = LocalDate.now();
        businessId = Long.valueOf(this.businessStringId);
        Currency fromCurrency = business.getCurrency();
        Currency selectedCurrency = business.getCurrency();
        Reference reference = Reference.LOAN_REQUEST_PLAN_DETAIL;
        transactionForm = new TransactionForm(rest, now, businessId, fromCurrency, selectedCurrency, reference, null, null);

        layout.add(transactionForm);
    }
}
