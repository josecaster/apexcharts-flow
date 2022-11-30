package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import sr.we.ContextProvider;
import sr.we.CustomNotificationHandler;
import sr.we.data.controller.PaymentTransactionService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.PaymentTransaction;
import sr.we.shekelflowcore.entity.helper.MappedSuperClass;
import sr.we.shekelflowcore.entity.helper.vo.PaymentTransactionVO;
import sr.we.shekelflowcore.exception.PrimaryThrowable;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.shekelflowcore.settings.util.DateUtil;
import sr.we.ui.components.UIUtil;
import sr.we.ui.components.buttons.DeleteButton;
import sr.we.ui.views.LineAwesomeIcon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


//@PreserveOnRefresh
public class TransactionGrid extends VerticalLayout {

    Grid<PaymentTransaction> grid = new Grid<>();
    private Set<PaymentTransaction> paymentTransactions;
    private String business;

    public TransactionGrid() {
//        addClassName("loans-view");
//        layout.setSizeFull();
        grid.setAllRowsVisible(true);
        grid.setClassName("resonate");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        Grid.Column<PaymentTransaction> payment_date = grid.addColumn(f -> f.getPaymentDate() == null ? null : Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(f.getPaymentDate()))).setHeader("Payment date");
        Grid.Column<PaymentTransaction> payment_method = grid.addColumn(PaymentTransaction::getMemo).setHeader("Description");
        Grid.Column<PaymentTransaction> account = grid.addColumn(f -> f.getAccount() == null ? null : f.getAccount().getName()).setHeader("Account");
        Grid.Column<PaymentTransaction> reference = grid.addColumn(f -> f.getReference().getCaption() + " #" + f.getReferenceId()).setHeader("Reference");
        grid.addColumn(f -> f.getCurrencyFrom().getCode()+" "+(f.getAmount() == null ? Constants.CURRENCY_FORMAT.format(BigDecimal.ZERO) : Constants.CURRENCY_FORMAT.format(f.getAmount()))).setHeader("Amount");
        grid.addColumn(f -> f.getCurrencyTo().getCode()+" "+(f.getConvertedAmount() == null ? Constants.CURRENCY_FORMAT.format(BigDecimal.ZERO) : Constants.CURRENCY_FORMAT.format(f.getConvertedAmount()))).setHeader("Currency Amount");
        HeaderRow.HeaderCell join = grid.prependHeaderRow().join(payment_date, payment_method, account, reference);
        Div transactionToolbar = new Div();
        transactionToolbar.setWidthFull();
        join.setComponent(transactionToolbar);

        DeleteButton img = new DeleteButton();
        transactionToolbar.add(img);

        ConfirmDialog confirmDialog = new ConfirmDialog("Delete", "Do you wish to delete the selected transactions?", "Yes", g -> {
            List<Long> longs = paymentTransactions.stream().map(MappedSuperClass::getId).toList();
            PaymentTransactionVO paymentTransactionVO = new PaymentTransactionVO();
            paymentTransactionVO.setIds(longs);
            PaymentTransactionService paymentTransactionService = ContextProvider.getBean(PaymentTransactionService.class);
            Long count = paymentTransactionService.delete(AuthenticatedUser.token(), paymentTransactionVO);
            CustomNotificationHandler.notify_(new PrimaryThrowable(count + " items deleted"));
            afterNavigation();
        });
        confirmDialog.setConfirmButtonTheme(LumoUtility.Background.ERROR);
        confirmDialog.setCancelable(true);

        img.addClickListener(f -> {
            confirmDialog.open();
        });

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
            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon();

            switch (f.getTransactionType()) {
                case DEPOSIT -> {
                    lineAwesomeIcon.icon("la la-angle-up");
                    lineAwesomeIcon.getElement().getThemeList().add(UIUtil.Badge.PILL + " success");
                }
                case WITHDRAWAL -> {
                    lineAwesomeIcon.icon("la la-angle-down");
                    lineAwesomeIcon.getElement().getThemeList().add(UIUtil.Badge.PILL + " error");
                }
                case UNKNOWN -> {
                    lineAwesomeIcon.icon("la la-circle");
                    lineAwesomeIcon.getElement().getThemeList().add(UIUtil.Badge.PILL + " primary");
                }
            }
            lineAwesomeIcon.getElement().getStyle().set("color", "white");
            lineAwesomeIcon.getElement().getStyle().set("margin", "0px");
            lineAwesomeIcon.getElement().getStyle().set("font-size", "var(--lumo-font-size-xl) !important");

            lineAwesomeIcon.addClassName(LumoUtility.FontSize.MEDIUM);
            return lineAwesomeIcon;
        }).setHeader("Transaction");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addSelectionListener(get -> {
            paymentTransactions = get.getAllSelectedItems();
            if (paymentTransactions == null || paymentTransactions.isEmpty()) {
                img.setVisible(false);
            }
            img.setVisible(true);
        });

        add(grid);
        setPadding(false);
        setSpacing(false);
    }

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
