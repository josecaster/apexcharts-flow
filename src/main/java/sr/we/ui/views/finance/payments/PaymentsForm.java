package sr.we.ui.views.finance.payments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.textfield.TextField;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.helper.MappedSuperClass;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.views.finance.transactions.TransactionForm;

import java.util.Optional;

public class PaymentsForm extends VerticalLayout {

    protected final VerticalLayout layout;
    protected Long referenceId;
    protected TransactionForm transactionForm = null;
    protected LoanRequest loanRequest;
    protected Grid<LoanRequest> grid = new Grid<>();
    protected String businessStringId;
    protected Business business;
    protected Long businessId;

//    public static String getLocation(String business, String loan) {
//        return LoansView.getLocation(business, loan) + "/payments";
//    }

    public PaymentsForm() {
        SplitLayout splitLayout = new SplitLayout();
        layout = new VerticalLayout();
        layout.setVisible(false);
        splitLayout.addToSecondary(layout);
        splitLayout.addThemeVariants(SplitLayoutVariant.LUMO_MINIMAL);
        splitLayout.setSplitterPosition(70);
        TextField searchField = new TextField();
        searchField.setWidthFull();
        searchField.setPlaceholder("Search specific Loan");
        splitLayout.addToPrimary(new VerticalLayout(searchField, grid));
        splitLayout.setSizeFull();
        add(splitLayout);


//        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addColumn(MappedSuperClass::getId).setHeader("ID#");
        grid.addColumn(person -> person.getCustomer().getName()).setHeader("Name");
        grid.addColumn(LoanRequest::getNextPaymentDate).setHeader("Next payment Date");
        grid.addColumn(person -> Constants.CURRENCY_FORMAT.format(person.getNextPaymentAmount())).setHeader("Next payment amount");
        grid.addColumn(person -> Constants.CURRENCY_FORMAT.format(person.getTransactionBalance())).setHeader("Balance");
        grid.addComponentColumn(f -> {
            if (f.isOverdue()) {
                Span overdue = new Span("Overdue");
                overdue.getElement().getThemeList().add("badge error");
                overdue.getElement().getStyle().set("height", "fit-content");
                return overdue;
            }
            Span pending = new Span("Pending");
            pending.getElement().getThemeList().add("badge contrast");
            pending.getElement().getStyle().set("height", "fit-content");
            return pending;
        }).setHeader("State");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(get -> {
            transactionForm.setReferenceId(null);
            Optional<LoanRequest> firstSelectedItem = get.getFirstSelectedItem();
            layout.setVisible(false);
            if (firstSelectedItem.isPresent()) {
                loanRequest = firstSelectedItem.get();
                transactionForm.setNextReferenceId(loanRequest.getId());
                transactionForm.setCurrency(loanRequest.getCurrency());
                referenceId = loanRequest.getId();
                transactionForm.setReferenceId(referenceId);
                layout.setVisible(true);
                transactionForm.setAmount(loanRequest.getNextPaymentAmount());

            }
        });
        grid.setAllRowsVisible(true);

        Button button = new Button("Pay");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        button.getElement().getStyle().set("margin-left", "auto");
        HorizontalLayout horizontalLayout = new HorizontalLayout(button);
        horizontalLayout.setWidthFull();
        add(horizontalLayout);
        horizontalLayout.setPadding(true);
        horizontalLayout.setMargin(true);

        button.addClickListener(f -> {
            transactionForm.save();
        });

    }

}
