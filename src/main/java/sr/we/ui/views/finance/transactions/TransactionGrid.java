package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import sr.we.ContextProvider;
import sr.we.data.controller.PaymentTransactionService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.PaymentTransaction;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.views.LineAwesomeIcon;


//@PreserveOnRefresh
public class TransactionGrid extends VerticalLayout  {

    Grid<PaymentTransaction> grid = new Grid<>();
    private String business;

    public TransactionGrid() {
//        addClassName("loans-view");
//        layout.setSizeFull();
        grid.setAllRowsVisible(true);
        grid.setClassName("resonate");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addColumn(PaymentTransaction::getPaymentDate).setHeader("Payment date");
        grid.addColumn(f -> f.getPaymentMethod().getDescription()).setHeader("Payment method");
        grid.addColumn(f -> f.getAccount().getName()).setHeader("Account");
        grid.addColumn(f -> f.getReference().getCaption() + " #" + f.getReferenceId()).setHeader("Reference");
        grid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getConvertedAmount())).setHeader("Amount");
        grid.setClassNameGenerator(f -> {
            switch (f.getTransactionType()) {
                case DEPOSIT -> {
                    return "success";
                }
                case WITHDRAWAL -> {
                    return "error";
                }
            }
            return null;
        });
        grid.addComponentColumn(f -> {
            Span lineAwesomeIcon = new Span();

            switch (f.getTransactionType()) {
                case DEPOSIT -> {
                    lineAwesomeIcon.add(new LineAwesomeIcon("la la-arrow-up"));
                    lineAwesomeIcon.getElement().getThemeList().add("badge success");
                }
                case WITHDRAWAL -> {
                    lineAwesomeIcon.add(new LineAwesomeIcon("la la-arrow-down"));
                    lineAwesomeIcon.getElement().getThemeList().add("badge error");
                }
            }
            return lineAwesomeIcon;
        }).setHeader("Transaction");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(get -> {
            /*Optional<PaymentTransaction> firstSelectedItem = get.getFirstSelectedItem();
            if (firstSelectedItem.isPresent()) {
                PaymentTransaction loan = firstSelectedItem.get();
//                QueryParameters queryParameters = QueryParameters.fromString("id=" + loan.getId());
//                UI.getCurrent().navigate(LoansViewTabDashboard.getLocation(business, loan.getId().toString()), queryParameters);
                UI.getCurrent().navigate(LTabDashboard.class, //
                        new RouteParameters(//
                                new RouteParam("business", business),//
                                new RouteParam("loan", loan.getId().toString())));
            }*/
        });
        add(grid);
    }

//    @Override
//    protected void onCreateClick() {
//        UI.getCurrent().navigate(LoansCreateView.class, new RouteParameters(new RouteParam("business", business)));
//    }

//    public static HorizontalLayout createCard(PaymentTransaction loan, String business, boolean showContext) {
//        UI current = UI.getCurrent();
//
//        HorizontalLayout card = new HorizontalLayout();
//        card.addClassName("card");
//        card.setSpacing(false);
////        card.getThemeList().add("spacing-s");
//        card.setPadding(false);
//
//        VerticalLayout header = new VerticalLayout();
//        header.addClassName("header");
//        header.setSpacing(false);
////        header.getThemeList().add("spacing-s");
//        header.setPadding(false);
//
//        Span name = new Span(loan.getName());
//        name.addClassName("name");
//        Span date = new Span(loan.getCurrency().getName());
//        date.addClassName("date");
//        header.add(name, date);
//
//        card.add(header);
//        Span pending = new Span("Balanced");
//        pending.getElement().getStyle().set("height","fit-content");
//        pending.getElement().getThemeList().add("badge contrast");
//
//        Span confirmed = new Span("Fixed");
//        confirmed.getElement().getStyle().set("height","fit-content");
//        confirmed.getElement().getThemeList().add("badge success");
//        if (loan.getFixed()) {
//            card.add(confirmed);
//        } else {
//            card.add(pending);
//        }
//        return card;
//    }

    public void afterNavigation() {
        PaymentTransactionService loanService = ContextProvider.getBean(PaymentTransactionService.class);
        grid.setItems(loanService.list(AuthenticatedUser.token(), Long.valueOf(business)).getResult());

    }


    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }
}
