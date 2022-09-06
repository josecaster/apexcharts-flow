package sr.we.ui.views.finance.loans;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanService;
import sr.we.data.controller.UserAccessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Loan;
import sr.we.shekelflowcore.entity.helper.vo.LoanVO;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanPrivilege;
import sr.we.ui.components.finance.FactorField;
import sr.we.ui.components.finance.FrequencyRangeField;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.products.ProductView;

/**
 * A Designer generated component for the add-loan template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("add-loan")
@JsModule("./src/views/finance/loan/add-loan.ts")
public class AddLoan extends LitTemplate {

    @Id("back-button")
    private Button backButton;
    @Id("save-btn")
    private Button saveBtn;
    @Id("loan-form")
    private LoanForm loanForm;
    private String business;
    private Loan loan;

    /**
     * Creates a new AddLoan.
     */
    public AddLoan() {
        // You can initialise any data required for the connected UI components here.

        backButton.addClickListener(f -> {
            UI.getCurrent().navigate(LoanView.class, new RouteParameters(new RouteParam("business", business)));
        });
        backButton.setIcon(new LineAwesomeIcon("la la-arrow-left"));
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_ICON);

        saveBtn.addClickListener(f -> {
            onSave();
        });
    }


    public void setBusiness(String business) {
        this.business = business;
        loanForm.setBusiness(business);
    }

    protected void onSave() {
        LoanService loanService = ContextProvider.getBean(LoanService.class);
        String token = AuthenticatedUser.token();
        LoanVO vo = loanForm.getLoanVO();
        if(vo.isNew()) {
            loanService.create(token, vo);
        } else {
            loanService.edit(token, vo);
        }

        UI.getCurrent().navigate(LoanView.class, new RouteParameters(new RouteParam("business", business)));


    }

    public void setLoan(Loan loan) {
        this.loan = loan;
        loanForm.setLoan(loan);
    }
}
