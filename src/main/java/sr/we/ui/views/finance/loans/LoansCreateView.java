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
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.vo.LoanVO;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanPrivilege;
import sr.we.shekelflowcore.security.privileges.TransactionsPrivilege;
import sr.we.ui.views.MainLayout;
import sr.we.ui.components.general.CurrencySelect;
import sr.we.ui.components.finance.FrequencyRangeField;
import sr.we.ui.views.StateListenerLayout;
import sr.we.ui.components.finance.FactorField;
import sr.we.ui.components.finance.FormulaField;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@Route(value = "create-loan", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
//@PreserveOnRefresh
public class LoansCreateView extends AddLoan implements HasDynamicTitle, BeforeEnterObserver {

//    private final TextField companyName;
//    private final CurrencySelect businessCurrency;
//
//    private final FrequencyRangeField frequencyField;
//
//    private final FactorField factorField;
//
//    private final Checkbox fixed;

//    private final FormulaField eligibleField, interestField;

    public LoansCreateView() {
//        companyName = new TextField();
//        companyName.setRequired(true);
//        companyName.setRequiredIndicatorVisible(true);
//        companyName.setWidthFull();
//
//        businessCurrency = new CurrencySelect();
//        businessCurrency.setRequiredIndicatorVisible(true);
//        businessCurrency.setLabel(null);
//        businessCurrency.setHelperText(getTranslation("sr.we.loan.currency.info"));
//        businessCurrency.setWidthFull();
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
////        eligibleField = new FormulaField();
////        eligibleField.setHelperText(getTranslation("sr.we.eligible.calculation.info"));
////
////        interestField = new FormulaField();
////        interestField.setHelperText(getTranslation("sr.we.interest.calculation.info"));
//
//        FormLayout formLayoutLeft = new FormLayout();
////        formLayoutLeft.add(companyName, businessCurrency, frequencyField, factorField, publish);
//        formLayoutLeft.addFormItem(companyName, getTranslation("sr.we.loan.name"));
//        formLayoutLeft.addFormItem(businessCurrency, getTranslation("sr.we.loan.currency"));
//        formLayoutLeft.addFormItem(frequencyField, frequencyField.getLabel());
//        frequencyField.setLabel(null);
//        formLayoutLeft.addFormItem(factorField, factorField.getLabel());
//        factorField.setLabel(null);
//        formLayoutLeft.addFormItem(fixed,getTranslation("sr.we.fixed"));
//        state(companyName, businessCurrency, frequencyField, factorField, fixed);
//        formLayoutLeft.getElement().getStyle().set("align-self", "top");
//        formLayoutLeft.setWidth("500px");
//        formLayoutLeft.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
//
//        FormLayout formLayoutRight = new FormLayout();
////        formLayoutRight.add(eligibleField, interestField);
////        formLayoutRight.addFormItem(eligibleField, getTranslation("sr.we.eligible.calculation"));
////        formLayoutRight.addFormItem(interestField, getTranslation("sr.we.interest.calculation"));
////        state(eligibleField, interestField);
//        formLayoutRight.getElement().getStyle().set("align-self", "top");
//        formLayoutRight.setWidth("500px");
//        formLayoutRight.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
//
////        FormLayout mainForm = new FormLayout(formLayoutLeft, formLayoutRight);
////        mainForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),new FormLayout.ResponsiveStep("500", 2));
//        add(formLayoutLeft);
    }


//    @Override
//    protected void onDiscard() {
//        companyName.clear();
//        businessCurrency.clear();
//        stateChanged(false, false);
//    }
//
//    @Override
//    protected boolean validate() {
//        if (companyName.isEmpty()) {
//            return false;
//        }
//        return businessCurrency.getOptionalValue().isPresent();
//    }

    @Override
    public String getPageTitle() {
        return getTranslation("sr.we.create.new.loan.structure");
    }

    private String business;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanPrivilege(), Privileges.INSERT);
        if(!hasAccess){
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        if (business1.isPresent()) {
            business = business1.get();
        }
        setBusiness(business);
    }
}
