package sr.we.ui.views.finance.loanrequests;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestService;
import sr.we.data.controller.UserAccessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.LoanRequestPlan;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanRequestPlanPrivilege;
import sr.we.ui.views.finance.loans.tabs.request.planning.LRPGenerate;
import sr.we.ui.views.finance.loans.tabs.request.planning.LRPView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A Designer generated component for the repayment-form template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("repayment-form")
@JsModule("./src/views/finance/loanrequests/repayment-form.ts")
public class RepaymentForm extends LitTemplate {

    @Id("standard-schedule-layout")
    private Div standardScheduleLayout;
    @Id("extend-schedule-layout")
    private Div extendScheduleLayout;
    @Id("balance-schedule-layout")
    private Div balanceScheduleLayout;
    @Id("generate-payment-btn")
    private Button generatePaymentBtn;
    private Dialog dialog;
    private LoanRequest loanRequest;

    /**
     * Creates a new RepaymentForm.
     */
    public RepaymentForm() {
        // You can initialise any data required for the connected UI components here.
//        Animated.animate(this, Animated.Animation.SLIDE_IN_RIGHT);

        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanRequestPlanPrivilege(), Privileges.INSERT, Privileges.EXECUTE);
        generatePaymentBtn.setVisible(hasAccess);

        generatePaymentBtn.addClickListener(f -> {
            String token = AuthenticatedUser.token();

            LRPGenerate loanRequestsPlanningView = new LRPGenerate(null, loanRequest, new Executable() {
                @Override
                public Object build() {
                    dialog.close();
                    LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
                    LoanRequest loanRequest1 = loanRequestService.get(loanRequest.getId(), token);
                    setLoanRequest(loanRequest1, refresh);
                    refresh.build();
                    return loanRequest1;
                }
            });
            dialog.removeAll();
            dialog.add(loanRequestsPlanningView);
            dialog.open();
        });
    }

    private Executable refresh;
    protected void setLoanRequest(LoanRequest loanRequest, Executable refresh) {
        this.loanRequest = loanRequest;
        this.refresh = refresh;

        UI current = UI.getCurrent();
        String token = AuthenticatedUser.token();

        new Thread(new Runnable() {
            @Override
            public void run() {

                current.access(() -> {
                    dialog = new Dialog();
                    dialog.setHeaderTitle("Generate payment plan");
                    Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> {
                        dialog.close();
//                        current.getPage().executeJs("return window.location.href").then(String.class, location -> {
//                            current.getPage().setLocation(location);
//                        });
//
                    });
                    dialog.setCloseOnOutsideClick(false);
                    dialog.setCloseOnEsc(false);
                    dialog.setModal(true);
                    dialog.setResizable(true);
                    closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                    dialog.getHeader().add(closeButton);

                    List<LoanRequestPlan> loanRequestPlans = new ArrayList<>(RepaymentForm.this.loanRequest.getLoanRequestPlans());

                    standardScheduleLayout.removeAll();
                    extendScheduleLayout.removeAll();
                    balanceScheduleLayout.removeAll();

                    loanRequestPlans = loanRequestPlans.stream().sorted(Comparator.comparingLong(LoanRequestPlan::getId)).collect(Collectors.toList());
                    for (LoanRequestPlan loanRequestPlan : loanRequestPlans) {

                        LRPView loanRequestsPlanningView = new LRPView(loanRequestPlan, loanRequest, () -> {

                            LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
                            LoanRequest loanRequest1 = loanRequestService.get(loanRequest.getId(), token);
                            setLoanRequest(loanRequest1, refresh);
                            refresh.build();
                            return loanRequest1;
                        });

                        if (loanRequestPlan.getType().compareTo(LoanRequestPlan.Type.PAYMENT) == 0) {
                            standardScheduleLayout.add(loanRequestsPlanningView);
                        } else if (loanRequestPlan.getType().compareTo(LoanRequestPlan.Type.EXTEND) == 0) {
                            extendScheduleLayout.add(loanRequestsPlanningView);
                        } else {
                            balanceScheduleLayout.add(loanRequestsPlanningView);
                        }
                    }
                });
            }
        }).start();

        generatePaymentBtn.setVisible(false);
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.APPROVED) == 0) {
            generatePaymentBtn.setVisible(true);
        }
    }

}
