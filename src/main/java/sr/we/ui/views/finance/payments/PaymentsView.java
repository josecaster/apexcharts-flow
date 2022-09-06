package sr.we.ui.views.finance.payments;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.LoanRequestService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.TransactionsPrivilege;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.business.BusinessView;
import sr.we.ui.views.finance.loans.LoansView;
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
        List<LoanRequest> list = loanService.list(AuthenticatedUser.token(), Long.valueOf(businessStringId), null);
        if(list != null) {
            list = list.stream().filter(f -> f.getStatus().compareTo(LoanRequest.Status.REPAYMENT) == 0).collect(Collectors.toList());
            grid.setItems(list);
        } else {

        }

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

        layout.removeAll();

        BigDecimal rest = null;
        LocalDate now = LocalDate.now();
        businessId = Long.valueOf(this.businessStringId);
        Currency fromCurrency = null;
        Currency selectedCurrency = null;
        PaymentTransaction.Reference reference = PaymentTransaction.Reference.LOAN_REQUEST_PLAN_DETAIL;

        PaymentTransaction.PlusMin plus = reference.getPlusMin();
        transactionForm = new TransactionForm(rest, now, businessId, fromCurrency, selectedCurrency, reference, null, plus);
        layout.add(transactionForm);
    }
}
