package sr.we.ui.views.invoice;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.InvoiceService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Invoice;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.vo.InvoiceVO;
import sr.we.shekelflowcore.security.PrivilegeModeAbstract;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.InvoicesPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.ArrowDownButton;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.NotYetChange;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * A Designer generated component for the invoice-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@BreadCrumb(titleKey = "sr.we.invoices")
@Tag("invoice-view")
@JsModule("./src/views/invoice/invoice-view.ts")
@Route(value = "invoices", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class InvoiceView extends LitTemplate implements BeforeEnterObserver, AfterNavigationObserver {

    private String business;
    @Id("invoice-grid-layout")
    private Div invoiceGridLayout;
    @Id("add-invoice-btn")
    private Button addInvoiceBtn;
    private final Grid<Invoice> grid = new Grid<>();
    @Id("filter-field")
    private TextField filterField;

    /**
     * Creates a new InvoiceView.
     */
    public InvoiceView() {
        // You can initialise any data required for the connected UI components here.
        addInvoiceBtn.addClickListener(f -> UI.getCurrent().navigate(AddInvoiceView.class, new RouteParameters(new RouteParam("business", business))));
        filterField.addValueChangeListener(new NotYetChange<>());

        Grid.Column<Invoice> statusColumn = grid.addComponentColumn(new ValueProvider<Invoice, LineAwesomeIcon>() {
            @Override
            public LineAwesomeIcon apply(Invoice invoice) {

                LineAwesomeIcon invoiceViewStatusSpan = new LineAwesomeIcon();
                invoiceViewStatusSpan.removeClassName("menu-item-icon");
                if (invoice.isFullyPayed()) {
                    invoiceViewStatusSpan.setText(invoice.getStatus().getDisplay() + " : Paid");
                    invoiceViewStatusSpan.getElement().getThemeList().add("badge success");
                } else {

                    if (invoice.getPaymentDue().isBefore(LocalDate.now())) {
                        invoiceViewStatusSpan.setText(invoice.getStatus().getDisplay() + " : Overdue");
                        invoiceViewStatusSpan.getElement().getThemeList().add("badge error");
                    } else {
                        invoiceViewStatusSpan.setText(invoice.getStatus().getDisplay() + " : Pending");
                        invoiceViewStatusSpan.getElement().getThemeList().add("badge contrast");
                    }
                }
                return invoiceViewStatusSpan;
            }
        }).setHeader("Status");
        Grid.Column<Invoice> dueColumn = grid.addColumn(Invoice::getPaymentDue).setHeader("Due");
        Grid.Column<Invoice> dateColumn = grid.addColumn(Invoice::getInvoiceDate).setHeader("Date");
        grid.addColumn(Invoice::getInvoiceNumber).setHeader("Number");
        grid.addColumn(f -> f.getCustomer() == null ? "None" : (f.getCustomer().getName() + " " + f.getCustomer().getFirstName())).setHeader("Customer");
        grid.addColumn(f -> (f.getCurrencyTo() == null ? "" : (f.getCurrencyTo().getCode() + " ")) + Constants.CURRENCY_FORMAT.format(f.getRest() == null ? BigDecimal.ZERO : f.getRest())).setHeader("Amount due");
        grid.addComponentColumn(new ValueProvider<Invoice, ArrowDownButton>() {
            @Override
            public ArrowDownButton apply(Invoice detail) {
//                if (detail.isFullyPayed()) {
//                    LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-check");
//                    lineAwesomeIcon.getElement().getThemeList().add("badge primary success");
//                    return lineAwesomeIcon;
//                }
                ArrowDownButton lineAwesomeIcon = new ArrowDownButton();
//                if (detail.getPaymentTransactions() != null && !detail.getPaymentTransactions().isEmpty()) {
//                    lineAwesomeIcon = new LineAwesomeIcon("la la-check");
//                } else {
//                    lineAwesomeIcon = new LineAwesomeIcon("la la-chevron-circle-down");
//                }
//                lineAwesomeIcon.addClickListener(f -> {
//                    BigDecimal rest = detail.getFactor().subtract(detail.getTransactionsAmount());
//                    LocalDate initDate = detail.getInitDate();
//                    Long businessId = loanRequest.getLoan().getBusiness().getId();
//                    sr.we.shekelflowcore.entity.Currency fromCurrency = loanRequest.getLoan().getCurrency();
//                    Currency selectedCurrency = loanRequest.getCurrency();
//                    Reference reference = Reference.LOAN_REQUEST_PLAN_DETAIL;
//                    Long referenceId = detail.getId();
//                    TransactionDialog transactionDialog = new TransactionDialog(rest, initDate, businessId, fromCurrency, selectedCurrency, reference, referenceId);
//                    transactionDialog.setNextReferenceId(loanRequest.getId());
//                    transactionDialog.setRefresh(refresh);
//                    transactionDialog.open();
//                });

//                lineAwesomeIcon.getElement().getThemeList().add("badge primary error");
                return lineAwesomeIcon;
            }
        }).setHeader("Actions");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addItemDoubleClickListener(get -> {
            Invoice firstSelectedItem = get.getItem();
            if (firstSelectedItem != null) {
                Invoice loanRequest = firstSelectedItem;
                List<String> strings = Arrays.asList(loanRequest.getPosHeader().getId().toString());
                Map<String, List<String>> map = new HashMap<>();
                map.put("id", strings);
                QueryParameters queryParameters = new QueryParameters(map);
                UI.getCurrent().navigate(InvoiceSummaryView.getLocation(business), queryParameters);
            }
        });

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setClassName("resonate");
        HeaderRow.HeaderCell join = grid.prependHeaderRow().join(statusColumn, dueColumn, dateColumn);
        Button close = new Button("Close");
        close.setEnabled(false);
        join.setComponent(new HorizontalLayout(close));
        invoiceGridLayout.add(grid);
        grid.setAllRowsVisible(true);


        grid.addSelectionListener(g -> {
            if (g.getAllSelectedItems() == null) {
                close.setEnabled(false);
                return;
            }
            boolean present = g.getAllSelectedItems().stream().anyMatch(f -> f.isFullyPayed() && f.getStatus().compareTo(Invoice.Status.OPEN) == 0);
            close.setEnabled(present);
        });
        close.addClickListener(f -> {
            Set<Invoice> selectedItems = grid.getSelectedItems();
            for(Invoice invoice : selectedItems){
                InvoiceService invoiceService = ContextProvider.getBean(InvoiceService.class);
                InvoiceVO invoiceVO = new InvoiceVO();
                invoiceVO.setId(invoice.getId());
                invoiceVO.setStatus(Invoice.Status.CLOSED);
                invoiceService.status(invoiceVO, AuthenticatedUser.token());
            }
            refresh();
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccessService = ContextProvider.getBean(UserAccessService.class);
        String token = AuthenticatedUser.token();
        boolean hasAccess = userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(InvoicesPrivilege.class), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        business1.ifPresent(s -> business = s);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        refresh();

    }

    private void refresh() {
        InvoiceService loanService = ContextProvider.getBean(InvoiceService.class);
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO.setBusiness(Long.valueOf(business));
        List<Invoice> list = loanService.list(AuthenticatedUser.token(), invoiceVO).getResult();
        grid.setItems(list);
    }
}
