package sr.we.ui.views.finance.loans;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Loan;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.vo.LoanVO;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanPrivilege;
import sr.we.shekelflowcore.security.privileges.TransactionsPrivilege;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.StateListenerLayout;
import sr.we.ui.views.business.BusinessView;
import sr.we.ui.components.general.CurrencySelect;
import sr.we.ui.components.finance.FactorField;
import sr.we.ui.components.finance.FormulaField;
import sr.we.ui.components.finance.FrequencyRangeField;
import sr.we.ui.views.finance.loans.tabs.LTabDashboard;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@Route(value = "edit-loan", layout = LoansView.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
//@PreserveOnRefresh
public class LoansEditView extends AddLoan implements HasDynamicTitle, BeforeEnterObserver {

    //    private final TextField nameFld;
//    private final CurrencySelect currencyFld;
//
//    private final FrequencyRangeField frequencyField;
//
//    private final FactorField factorField;
//
//    private final Checkbox fixed;
//
//    private final FormulaField eligibleField, interestField;
    private Loan loan;
    private String business;

    public LoansEditView() {
//        nameFld = new TextField();
//        nameFld.setRequired(true);
//        nameFld.setRequiredIndicatorVisible(true);
//        nameFld.setWidthFull();
//
//        currencyFld = new CurrencySelect();
//        currencyFld.setRequiredIndicatorVisible(true);
//        currencyFld.setLabel(null);
//        currencyFld.setHelperText(getTranslation("sr.we.loan.currency.info"));
//        currencyFld.setWidthFull();
//
//        frequencyField = new FrequencyRangeField();
//        frequencyField.setRequiredIndicatorVisible(true);
//        frequencyField.setWidthFull();
//
//        factorField = new FactorField();
//        factorField.setRequiredIndicatorVisible(true);
//        factorField.setWidthFull();
//
//        fixed = new Checkbox();
//
//
//        eligibleField = new FormulaField();
//        eligibleField.setHelperText(getTranslation("sr.we.eligible.calculation.info"));
//
//        interestField = new FormulaField();
//        interestField.setHelperText(getTranslation("sr.we.interest.calculation.info"));
//
//        FormLayout formLayoutLeft = new FormLayout();
////        formLayoutLeft.add(companyName, businessCurrency, frequencyField, factorField, publish);
//        formLayoutLeft.addFormItem(nameFld, getTranslation("sr.we.loan.name"));
//        formLayoutLeft.addFormItem(currencyFld, getTranslation("sr.we.loan.currency"));
//        formLayoutLeft.addFormItem(frequencyField, frequencyField.getLabel());
//        frequencyField.setLabel(null);
//        formLayoutLeft.addFormItem(factorField, factorField.getLabel());
//        factorField.setLabel(null);
//        formLayoutLeft.addFormItem(fixed, getTranslation("sr.we.fixed"));
//        state(nameFld, currencyFld, frequencyField, factorField, fixed);
//        formLayoutLeft.getElement().getStyle().set("align-self", "top");
//        formLayoutLeft.setWidth("500px");
//        formLayoutLeft.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
//
//        FormLayout formLayoutRight = new FormLayout();
////        formLayoutRight.add(eligibleField, interestField);
//        formLayoutRight.addFormItem(eligibleField, getTranslation("sr.we.eligible.calculation"));
//        formLayoutRight.addFormItem(interestField, getTranslation("sr.we.interest.calculation"));
//        state(eligibleField, interestField);
//        formLayoutRight.getElement().getStyle().set("align-self", "top");
//        formLayoutRight.setWidth("500px");
//        formLayoutRight.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
//
////        FormLayout mainForm = new FormLayout(formLayoutLeft, formLayoutRight);
////        mainForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("500", 2));
//        add(formLayoutLeft);
    }

//    @Override
//    protected void onSave() {
//        LoanService loanService = ContextProvider.getBean(LoanService.class);
//        String token = AuthenticatedUser.token();
//        LoanVO vo = new LoanVO();
//        vo.setId(loan.getId());
//        vo.setBusiness(Long.valueOf(business));
//        vo.setCurrency(currencyFld.getValue().getId());
//        vo.setName(nameFld.getValue());
//        vo.setFixed(fixed.getValue());
//        FrequencyRangeField.Value frequency = frequencyField.getValue();
//        vo.setFreq(frequency.getFreq());
//        vo.setFreqMin(frequency.getMin().longValue());
//        vo.setFreqMax(frequency.getMax().longValue());
//        FactorField.Value factor = factorField.getValue1();
//        vo.setFactor(factor.getFactor());
//        vo.setFactorType(factor.getFactorType());
//        loanService.edit(token, vo);
//
//        UI.getCurrent().navigate(LTabDashboard.class, //
//                new RouteParameters(//
//                        new RouteParam("business", business),//
//                        new RouteParam("loan", loan.getId().toString())));
//
//
//    }
//
//    @Override
//    protected void onDiscard() {
//        nameFld.clear();
//        currencyFld.clear();
//        stateChanged(false, false);
//    }
//
//    @Override
//    protected boolean validate() {
//        if (nameFld.isEmpty()) {
//            return false;
//        }
//        return currencyFld.getOptionalValue().isPresent();
//    }

    public static String getLocation(String business) {
        return MainLayout.getLocation(business) + "/edit-loan";
    }

    @Override
    public String getPageTitle() {
        return getTranslation("sr.we.edit");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanPrivilege(), Privileges.UPDATE);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        RouteParameters routeParameters = event.getRouteParameters();
        Optional<String> business1 = routeParameters.get("business");
        if (business1.isPresent()) {
            business = business1.get();
            setBusiness(business);
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
        UI current = UI.getCurrent();
//        new Thread(() -> {
        LoanService loanService = ContextProvider.getBean(LoanService.class);
        loan = loanService.get(Long.valueOf(loanId.get()), token);
//            current.access(() -> {
        setLoan(loan);
//            });
//        }).start();
    }
}
