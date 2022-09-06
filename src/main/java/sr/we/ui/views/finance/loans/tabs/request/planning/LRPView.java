package sr.we.ui.views.finance.loans.tabs.request.planning;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.function.ValueProvider;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.Build;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.ToolbarLayout;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.finance.transactions.TransactionDialog;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class LRPView extends VerticalLayout {

    private final LoanRequestPlan loanRequestPlan;
    private final Build refresh;
    private Grid<LoanRequestPlanDetail> grid;

    private final LoanRequest loanRequest;

    public LRPView(LoanRequestPlan plan, LoanRequest loanRequest, Build build) {
        this.loanRequestPlan = plan;
        this.loanRequest = loanRequest;
        refresh = build;
        generatePLanning(refresh);
        forPlan();
//        if (loanRequest.getStatus().ordinal() == LoanRequest.Status.REPAYMENT.ordinal()) {
            enablePayment();
//        }
        setMargin(false);
        setPadding(false);

//        HeaderRow headerRow = grid.prependHeaderRow();
//        HeaderRow.HeaderCell join = headerRow.join(grid.getHeaderRows().get(0).getCells());
//        join.setComponent(new ToolbarLayout());
    }

    private void enablePayment() {
        grid.addComponentColumn(new ValueProvider<LoanRequestPlanDetail, LineAwesomeIcon>() {
            @Override
            public LineAwesomeIcon apply(LoanRequestPlanDetail detail) {
                if (detail.isFullyPayed()) {
                    LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-check");
                    lineAwesomeIcon.getElement().getThemeList().add("badge primary success");
                    return lineAwesomeIcon;
                }
                LineAwesomeIcon lineAwesomeIcon = null;
                if (detail.getPaymentTransactions() != null && !detail.getPaymentTransactions().isEmpty()) {
                    lineAwesomeIcon = new LineAwesomeIcon("la la-check");
                } else {
                    lineAwesomeIcon = new LineAwesomeIcon("la la-chevron-circle-down");
                }
                lineAwesomeIcon.addClickListener(f -> {
                    BigDecimal rest = detail.getFactor().subtract(detail.getTransactionsAmount());
                    LocalDate initDate = detail.getInitDate();
                    Long businessId = loanRequest.getLoan().getBusiness().getId();
//                    LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
//                    LoanRequest loanRequest1 = loanRequestService.get(loanRequestPlan.getLoanRequestId(), AuthenticatedUser.token());
                    Currency fromCurrency = loanRequest.getLoan().getCurrency();
                    Currency selectedCurrency = loanRequest.getCurrency();
                    PaymentTransaction.Reference reference = PaymentTransaction.Reference.LOAN_REQUEST_PLAN_DETAIL;
                    Long referenceId = detail.getId();
                    PaymentTransaction.PlusMin plusMin = PaymentTransaction.PlusMin.PLUS;
                    TransactionDialog transactionDialog = new TransactionDialog(rest, initDate, businessId, fromCurrency, selectedCurrency, reference, referenceId, plusMin);
                    transactionDialog.setNextReferenceId(loanRequest.getId());
                    transactionDialog.setRefresh(refresh);
                    transactionDialog.open();
                });

                lineAwesomeIcon.getElement().getThemeList().add("badge primary error");
                return lineAwesomeIcon;
            }
        }).setHeader("Record Payment");
    }

    private void generatePLanning(Build build) {
        Div horizontalLayout = new Div();
        add(horizontalLayout);


//        Button save = new Button("Save");
//        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        horizontalLayout.add(new H4(loanRequestPlan.getType().toString()));

        setSizeFull();

        grid = new Grid<>();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        add(grid);
        grid.setAllRowsVisible(true);

        grid.addComponentColumn(new ValueProvider<LoanRequestPlanDetail, Span>() {
            @Override
            public Span apply(LoanRequestPlanDetail loanRequestPlanning) {


                Span span = null;

                if (loanRequestPlanning.isFullyPayed()) {
                    Span paid = new Span("Paid");
                    paid.getElement().getThemeList().add("badge success");
                    span = paid;
                } else {

                    if (loanRequestPlanning.getInitDate().isBefore(LocalDate.now())) {
                        Span pending = new Span("Overdue");
                        pending.getElement().getThemeList().add("badge error");
                        span = pending;
                    } else {
                        Span confirmed = new Span("Pending");
                        confirmed.getElement().getThemeList().add("badge contrast");
                        span = confirmed;
                    }
                }

//                ProgressBar progressBar = new ProgressBar();
//                progressBar.setMax(loanRequestPlanning.getFactor().doubleValue());
//                BigDecimal transactionsAmount = loanRequestPlanning.getTransactionsAmount();
//                progressBar.setValue(transactionsAmount.doubleValue());
                return span;
            }
        }).setHeader("Status");

        grid.addComponentColumn(new ValueProvider<LoanRequestPlanDetail, Div>() {
            @Override
            public Div apply(LoanRequestPlanDetail loanRequestPlanning) {

                ProgressBar progressBar = new ProgressBar();
                progressBar.setMax(loanRequestPlanning.getFactor().doubleValue());
                BigDecimal transactionsAmount = loanRequestPlanning.getTransactionsAmount();
                progressBar.setValue(transactionsAmount.doubleValue());
                Div div = new Div(progressBar);
                div.setHeightFull();
                return div;
            }
        }).setHeader("Progress");

        grid.addColumn(new ValueProvider<LoanRequestPlanDetail, LocalDate>() {
            @Override
            public LocalDate apply(LoanRequestPlanDetail loanRequestPlanning) {
                return loanRequestPlanning.getInitDate();
            }
        }).setHeader("Payment date");

        grid.addColumn(new ValueProvider<LoanRequestPlanDetail, String>() {
            @Override
            public String apply(LoanRequestPlanDetail loanRequestPlanning) {
                return Constants.CURRENCY_FORMAT.format(loanRequestPlanning.getFactor());
            }
        }).setHeader("Amount");

        grid.addColumn(new ValueProvider<LoanRequestPlanDetail, String>() {
            @Override
            public String apply(LoanRequestPlanDetail loanRequestPlanning) {
                return Constants.CURRENCY_FORMAT.format(loanRequestPlanning.getCapital());
            }
        }).setHeader("Running totals");


//        save.addClickListener(f -> {
//            LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
//            loanRequestService.save(loanRequestPlan, AuthenticatedUser.token());
//            build.build();
////            forPlan();
////            enablePayment();
//        });
    }

    private Set<LoanRequestPlanDetail> forPlan() {
        Set<LoanRequestPlanDetail> loanRequestPlannings = loanRequestPlan.getLoanRequestPlanDetails();
        grid.setItems(loanRequestPlannings == null ? new ArrayList<>() : loanRequestPlannings.stream().sorted(Comparator.comparing(LoanRequestPlanDetail::getInitDate)).collect(Collectors.toList()));
        return loanRequestPlannings;
    }
}
