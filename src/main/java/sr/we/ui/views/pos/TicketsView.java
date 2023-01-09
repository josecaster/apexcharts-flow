package sr.we.ui.views.pos;

import com.flowingcode.vaadin.addons.gridexporter.GridExporter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.ValueProvider;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Items;
import sr.we.shekelflowcore.entity.PosHeader;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.vo.PosHeaderVO;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.shekelflowcore.settings.util.DateUtil;
import sr.we.ui.components.*;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.finance.transactions.TransactionDialog;
import sr.we.ui.views.finance.transactions.TransactionsCmb;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collection;

/**
 * A Designer generated component for the tickets-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("tickets-view")
@JsModule("./src/views/pos/tickets-view.ts")
public class TicketsView extends LitTemplate {

    protected final Grid<PosHeader> grid;
    protected Executable onSave, onRefresh;
    @Id("tickets-grid-layout")
    private Div ticketsGridLayout;
    private Business business;
    @Id("tv-main")
    private VerticalLayout tvMain;
    @Id("tv-sub")
    private VerticalLayout tvSub;
    @Id("filter-field")
    private MySearchField filterField;


    /**
     * Creates a new TicketsView.
     */
    public TicketsView() {
        // You can initialise any data required for the connected UI components here.

        filterField.addValueChangeListener(new NotYetChange<>());

        grid = new Grid<>();

        ticketsGridLayout.add(grid);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(PosHeader::getId).setHeader("ID").setSortable(true).setId("ph.id");
//        grid.addColumn(f -> f.getPosStart() != null ? Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(f.getPosStart().getTargetDate())) : null).setHeader("Date");
        grid.addColumn(f -> f.getHeaderSeq() != null ? ("Ticket # " + f.getHeaderSeq()) : null).setHeader("Sequence").setResizable(true).setSortable(true).setId("ph.headerSeq");
        grid.addColumn(f -> (f.getCurrencyFrom() != null ? f.getCurrencyFrom().getCode() : "") + (f.getPrice() != null ? Constants.CURRENCY_FORMAT.format(f.getPrice()) : "")).setHeader("Amount").setResizable(true).setSortable(true).setId("ph.price");
        grid.addColumn(f -> (f.getCurrencyTo() != null ? f.getCurrencyTo().getCode() : "") + (f.getConvertedAmount() != null ? Constants.CURRENCY_FORMAT.format(f.getConvertedAmount()) : "")).setHeader("Converted Amount").setResizable(true).setSortable(true).setId("ph.convertedAmount");
        grid.addColumn(f -> f.getTransactionsAmount() != null ? Constants.CURRENCY_FORMAT.format(f.getTransactionsAmount()) : null).setHeader("Paid amount").setResizable(true);
        grid.addColumn(f -> f.getRest() != null ? Constants.CURRENCY_FORMAT.format(f.getRest()) : null).setHeader("Amount due").setResizable(true);
        grid.setDetailsVisibleOnClick(true);
        grid.setItemDetailsRenderer(new ComponentRenderer<>(this::getDetailLayout));
        Grid.Column<PosHeader> record_payment = grid.addComponentColumn(new ValueProvider<PosHeader, LineAwesomeIcon>() {
            @Override
            public LineAwesomeIcon apply(PosHeader posHeader) {
                if (posHeader.getRest().compareTo(BigDecimal.ZERO) == 0) {
                    LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-check");
                    lineAwesomeIcon.getElement().getThemeList().add(UIUtil.Badge.PILL + " primary success");
                    return lineAwesomeIcon;
                }
                LineAwesomeIcon lineAwesomeIcon = null;
                if (posHeader.getPaymentTransactions() != null && !posHeader.getPaymentTransactions().isEmpty()) {
                    lineAwesomeIcon = new LineAwesomeIcon("la la-check");
                } else {
                    lineAwesomeIcon = new LineAwesomeIcon("la la-chevron-circle-down");
                }
                lineAwesomeIcon.addClickListener(f -> {
                    TransactionDialog transactionDialog = new TransactionDialog(posHeader.getRest(), LocalDate.now(), business.getId(), business.getCurrency(), business.getCurrency(), Reference.POS, posHeader.getId(), posHeader.getCustomerId());
                    transactionDialog.setOnSave(onSave);
                    transactionDialog.setRefresh(onRefresh);
                    transactionDialog.open();
                });

                lineAwesomeIcon.getElement().getThemeList().add(UIUtil.Badge.PILL + " primary error");
                return lineAwesomeIcon;
            }
        }).setHeader("Record Payment").setResizable(true);
        GridExporter<PosHeader> exporter = GridExporter.createFor(grid);
        GridUtil.exportButtons(exporter, grid);
        exporter.setExportColumn(record_payment,false);
        exporter.setTitle("Tickets");
        exporter.setFileName("Tickets_" + new SimpleDateFormat("yyyyddMM").format(Calendar.getInstance().getTime()));

    }

    private VerticalLayout getDetailLayout(PosHeader f) {
        VerticalLayout layout = new VerticalLayout();
        layout.setClassName("my-cart-base");
        layout.setMargin(true);
        layout.setPadding(true);

        TransactionsCmb transactionsCmb = new TransactionsCmb();
        transactionsCmb.loadCmb(f.getPaymentTransactions());
        layout.add(transactionsCmb);

        Button aVoid = new Button("Void");
        aVoid.addThemeVariants(ButtonVariant.LUMO_ERROR);
        aVoid.addClickListener(new NotYetClick<>());
        layout.add(aVoid);
        return layout;
    }

    public void setTickets(Collection<PosHeader> collection, Business business, Executable onSave, Executable onRefresh) {
        this.onRefresh = onRefresh;
        this.onSave = onSave;
        this.business = business;
        if(collection != null) {
            grid.setItems(collection);
        }
        grid.getDataProvider().refreshAll();
    }

    public void marginpadding(String s) {
        tvMain.getElement().getStyle().set("margin", s);
        tvMain.getElement().getStyle().set("padding", s);
        tvSub.getElement().getStyle().set("margin", s);
        tvSub.getElement().getStyle().set("padding", s);
    }
}
