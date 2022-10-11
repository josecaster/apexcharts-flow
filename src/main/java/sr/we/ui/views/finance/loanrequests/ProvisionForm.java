package sr.we.ui.views.finance.loanrequests;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.ui.views.finance.loans.tabs.request.proces.ProvideLayout;

/**
 * A Designer generated component for the provision-form template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("provision-form")
@JsModule("./src/views/finance/loanrequests/provision-form.ts")
public class ProvisionForm extends LitTemplate {
    @Id("provide-layout")
    private ProvideLayout provideLayout;

    //    @Id("progress-layout")
//    private Div progressLayout;
//    @Id("provide-payment-btn")
//    private Button providePaymentBtn;
//    @Id("amount_fld")
//    private Element amount_fld;
//    @Id("currency-provision-cmb")
//    private ComboBox<String> currencyProvisionCmb;
    private LoanRequest loanRequest;
    private Executable refresh1;

    /**
     * Creates a new ProvisionForm.
     */
    public ProvisionForm() {
        // You can initialise any data required for the connected UI components here.

    }

    protected void setLoanRequest(LoanRequest loanRequest, final Executable refresh) {
        this.loanRequest = loanRequest;
        refresh1 = new Executable() {
            @Override
            public Object build() {
                refresh.build();
                LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
                LoanRequest loanRequest1 = loanRequestService.get(loanRequest.getId(), AuthenticatedUser.token());
                provideLayout.setLoanRequest(loanRequest1, refresh1);
                return null;
            }
        };
        provideLayout.setLoanRequest(loanRequest, refresh1);

    }

}
