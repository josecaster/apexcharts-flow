package sr.we.ui.views.invoice;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.HTMLtoCANVAS.HTML2CANVAS;
import sr.we.ContextProvider;
import sr.we.data.controller.InvoiceService;
import sr.we.data.report.MyReportEngine;
import sr.we.shekelflowcore.entity.Invoice;
import sr.we.shekelflowcore.entity.InvoiceSetting;
import sr.we.shekelflowcore.entity.PosHeader;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.shekelflowcore.settings.util.DateUtil;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.UIUtil;
import sr.we.ui.views.pos.Item;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A Designer generated component for the invoice-pre-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@BreadCrumb(titleKey = "sr.we.invoices.preview",parentNavigationTarget = InvoiceView.class)
@Route(value = "invoice/:token")
@AnonymousAllowed
@Tag("invoice-pre-view")
@JsModule("./src/views/invoice/invoice-pre-view.ts")
public class InvoicePreView extends LitTemplate implements BeforeEnterObserver {

    private final Grid<Item> itemGrid;
    private final List<Item> itemList;
    private final Map<String, Object> map, feeMap;
    @Id("invoice-preview-title-h2")
    private H2 invoicePreviewTitleH2;
    @Id("invoice-preview-table-layout")
    private VerticalLayout invoicePreviewTableLayout;
    @Id("invoice-preview-total-layout")
    private VerticalLayout invoicePreviewTotalLayout;
    private Invoice invoice;
//    private InvoiceSetting invoiceSetting;
    @Id("invoice-preview-download-btn-layout")
    private Div invoicePreviewDownloadBtnLayout;
    @Id("invoice-preview-header-paragraph")
    private Paragraph invoicePreviewHeaderParagraph;
    @Id("invoice-preview-amount-due-header-h1")
    private H1 invoicePreviewAmountDueHeaderH1;
    @Id("invoice-preview-header-billto-lbl")
    private Label invoicePreviewHeaderBilltoLbl;
    @Id("invoicer-preview-invnumber-lbl")
    private Label invoicerPreviewInvnumberLbl;
    @Id("invoicer-preview-poso-lbl")
    private Label invoicerPreviewPosoLbl;
    @Id("invoicer-preview-invdate-lbl")
    private Label invoicerPreviewInvdateLbl;
    @Id("invoicer-preview-paydue-lbl")
    private Label invoicerPreviewPaydueLbl;
    @Id("invoicer-preview-notes-ta")
    private TextArea invoicerPreviewNotesTa;
    @Id("invoicer-preview-footer-lbl")
    private Label invoicerPreviewFooterLbl;
    @Id("invoicer-preview-company-lbl")
    private Label invoicerPreviewCompanyLbl;
    @Id("invoice-preview-status-span")
    private Span invoicePreviewStatusSpan;
    @Id("invoice-preview-report-layout")
    private VerticalLayout invoicePreviewReportLayout;
    private String invoiceToken;

    /**
     * Creates a new InvoicePreView.
     */
    public InvoicePreView() {
        // You can initialise any data required for the connected UI components here.
        Button invoiceSummaryDownloadBtn = new Button("Download invoice");
        invoiceSummaryDownloadBtn.getElement().getStyle().set("border", "solid 1px");
        invoiceSummaryDownloadBtn.getElement().getStyle().set("align-self", "center");
        invoiceSummaryDownloadBtn.getElement().getStyle().set("border-radius", "100px");

        Anchor anchor = new Anchor("", invoiceSummaryDownloadBtn);
        anchor.setTarget(AnchorTarget.BLANK);
        UI current = UI.getCurrent();
        anchor.setHref(new StreamResource("Invoice" + UUID.randomUUID() + ".pdf", new InputStreamFactory() {

            @Override
            public InputStream createInputStream() {

//                Element e = invoicePreviewReportLayout.getElement();
//                String id = e.getAttribute("id");
//                if (StringUtils.isEmpty(id)) {
//                    e.setAttribute("id", "elementForScreenShot");
//                    id = e.getAttribute("id");
//                }
//
//                StateNode node = e.getNode();
//                e.addEventListener("blobReady", (l) -> {
//                    Util.getJavaScriptReturn(node, "blobValue.valueOf()").then((jsonValue) -> {
//                        current.access(() -> {
//                            Image image = new Image(jsonValue.asString(), "adfs");
//                            image.setWidth("1000px");
//                            Dialog dialog = new Dialog(image);
//                            dialog.setSizeFull();
//                            dialog.open();
//                        });
//                    });
//                });
//                Util.getJavaScriptInvoke(node, "let element = document.querySelector('#" + id + "');\nasync function  makeScreenshot() \n{\n  return new Promise((resolve, reject) => {  \n    resolve(html2canvas(element));\n  });\n}\n\nfunction send(canvas) {\n   blobValue = canvas.toDataURL();\n    element.dispatchEvent(new Event('blobReady'));\n}\n\nmakeScreenshot().then((canvas) =>{\n  send(canvas);\n});\n\n");

//                CompletableFuture<?> completableFuture = HTML2CANVAS.takeScreenShot(invoicePreviewReportLayout.getElement());
//                completableFuture.thenRun(() -> {
//                    try {
////                        Image image = new Image(completableFuture.get(), "adfs");
////                        add(image);
//                        String src = (String) completableFuture.get();
////                        current.access(() -> {
//
//                            Image image = new Image(src, "adfs");
//                            image.setWidth("1000px");
//                            Dialog dialog = new Dialog(image);
//                            dialog.setSizeFull();
//                            dialog.open();
////                        });
//                    } catch (InterruptedException | ExecutionException ignored) {
//                    }
//                });

//                CompletableFuture<String> completableFuture = HTML2CANVAS.takeScreenShot(invoicePreviewReportLayout.getElement());
//                completableFuture.thenRun(() -> {
//                    try {
//                        String src = completableFuture.get();
//
//                        current.access(() -> {
//                            Image image = new Image(src, "adfs");
//                            image.setWidth("1000px");
//                            Dialog dialog = new Dialog(image);
//                            dialog.setSizeFull();
//                            dialog.open();
//                        });
//                    } catch (InterruptedException | ExecutionException ignored) {
//                    }
//                });
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("INVOICE_ID_", invoice.getId());
                map.put("INVOICE_DUE", invoice.getRest());
                if(invoice != null){
                    if(StringUtils.isNotBlank(invoice.getHeaderColor1())){
                        map.put("HEADER_COLOR_1", invoice.getHeaderColor1());
                    }
                    if(StringUtils.isNotBlank(invoice.getHeaderColor2())){
                        map.put("HEADER_COLOR_2", invoice.getHeaderColor2());
                    }
                    if(StringUtils.isNotBlank(invoice.getFooterColor1())){
                        map.put("FOOTER_COLOR_1", invoice.getFooterColor1());
                    }
                }
                try {
                    byte[] exportReportMap = ((MyReportEngine) ContextProvider.getBean(MyReportEngine.class)).exportInvoice(map);
                    return new ByteArrayInputStream(exportReportMap);
                } catch (JRException l) {
                    throw new RuntimeException(l);
                }
            }

        }));
        invoicePreviewDownloadBtnLayout.add(anchor);

        itemGrid = new Grid();
        itemGrid.setSelectionMode(Grid.SelectionMode.NONE);
        itemGrid.setAllRowsVisible(true);
        itemGrid.setClassName("resonate");
        itemGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        itemGrid.addColumn(Item::getName).setHeader("Title").setFlexGrow(1).setResizable(true).setSortable(true);
        itemGrid.addColumn(Item::getCount).setHeader("Quantity").setFlexGrow(0).setResizable(true).setSortable(true);
        itemGrid.addColumn(item -> {
            BigDecimal calcPrice = item.getPrice();
            return Constants.CURRENCY_FORMAT.format(calcPrice == null ? BigDecimal.ZERO : calcPrice);
        }).setHeader("Price").setFlexGrow(0).setResizable(true).setSortable(true);
        itemGrid.addColumn(item -> {
            BigDecimal calcPrice = item.getResult();
            return Constants.CURRENCY_FORMAT.format(calcPrice == null ? BigDecimal.ZERO : calcPrice);
        }).setHeader("Amount").setFlexGrow(0).setResizable(true).setSortable(true);

        itemList = new ArrayList<>();
        itemGrid.setItems(itemList);

        map = new HashMap<>();
        feeMap = new HashMap<>();

        invoicePreviewTableLayout.add(itemGrid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> token = event.getRouteParameters().get("token");
        token.ifPresent(s -> invoiceToken = s);
        InvoiceService invoiceService = ContextProvider.getBean(InvoiceService.class);
        invoice = invoiceService.getByToken(invoiceToken);
        setInvoice(invoice);
    }


    private void setInvoice(Invoice invoice) {
        invoicePreviewTitleH2.setText("Invoice #" + invoice.getInvoiceNumber() + " from " + invoice.getBusiness().getName());
        invoicePreviewHeaderParagraph.setText(StringUtils.isBlank(invoice.getHeaderMessage()) ? "N.A." : invoice.getHeaderMessage());
        invoicePreviewAmountDueHeaderH1.setText(invoice.getCurrencyTo().getCode() + " " + (invoice.getRest() == null ? "?" : Constants.CURRENCY_FORMAT.format(invoice.getRest())));
        invoicePreviewHeaderBilltoLbl.setText(invoice.getCustomer() == null ? "None" : (invoice.getCustomer().getName() + " " + invoice.getCustomer().getFirstName()));

        invoicePreviewStatusSpan.getElement().getThemeList().remove("success");
        invoicePreviewStatusSpan.getElement().getThemeList().remove("error");
        invoicePreviewStatusSpan.getElement().getThemeList().remove("contrast");
        if (invoice.isFullyPayed()) {
            invoicePreviewStatusSpan.setText("Paid");
            invoicePreviewStatusSpan.getElement().getThemeList().add(UIUtil.Badge.PILL+" success");
        } else {

            if (invoice.getPaymentDue().isBefore(LocalDate.now())) {
                invoicePreviewStatusSpan.setText("Overdue");
                invoicePreviewStatusSpan.getElement().getThemeList().add(UIUtil.Badge.PILL+" error");
            } else {
                invoicePreviewStatusSpan.setText("Pending");
                invoicePreviewStatusSpan.getElement().getThemeList().add(UIUtil.Badge.PILL+" contrast");
            }
        }

        invoicerPreviewNotesTa.setValue(StringUtils.isBlank(invoice.getThankMessage()) ? "" : invoice.getThankMessage());
        invoicerPreviewInvnumberLbl.setText(StringUtils.isBlank(invoice.getInvoiceNumber()) ? "" : ("# " + invoice.getInvoiceNumber()));
        invoicerPreviewPosoLbl.setText(StringUtils.isBlank(invoice.getPosoNumber()) ? "" : ("# " + invoice.getPosoNumber()));
        invoicerPreviewCompanyLbl.setText(invoice.getBusiness().getName());
        invoicerPreviewFooterLbl.setText(StringUtils.isBlank(invoice.getFooterMessage()) ? "" : invoice.getFooterMessage());
        invoicerPreviewInvdateLbl.setText(invoice.getInvoiceDate() == null ? "" : Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(invoice.getInvoiceDate())));
        invoicerPreviewPaydueLbl.setText(invoice.getPaymentDue() == null ? "" : Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(invoice.getPaymentDue())));

        setTicket(invoice.getPosHeader());
        discount(invoice);
        total(invoice);
        due(invoice);

    }

    private void due(Invoice invoice) {
        Label total = new Label("Amount due " + invoice.getCurrencyTo().getCode() + " " + (invoice.getRest() == null ? "?" : Constants.CURRENCY_FORMAT.format(invoice.getRest())));
        total.getElement().getStyle().set("margin-left", "auto");
        HorizontalLayout horizontalLayout = new HorizontalLayout(total);
        horizontalLayout.setWidthFull();
        invoicePreviewTotalLayout.add(horizontalLayout);
    }

    private void discount(Invoice invoice) {
        if(invoice.getPosHeader().getDiscount() != null && invoice.getPosHeader().getDiscount().compareTo(BigDecimal.ZERO) != 0) {
            Label total = new Label("Discount " + Constants.CURRENCY_FORMAT.format(invoice.getPosHeader().getDiscount()));
            total.getElement().getStyle().set("margin-left", "auto");
            total.getElement().getStyle().set("font-weight", "bold");
            HorizontalLayout horizontalLayout = new HorizontalLayout(total);
            horizontalLayout.setWidthFull();
            invoicePreviewTotalLayout.add(horizontalLayout);
        }
    }

    private void total(Invoice invoice) {
        Label total = new Label("Total " + invoice.getCurrencyTo().getCode() + " " + (invoice.getConvertedAmount() == null ? "?" : Constants.CURRENCY_FORMAT.format(invoice.getConvertedAmount().subtract(invoice.getPosHeader().getDiscount() == null ? BigDecimal.ZERO : invoice.getPosHeader().getDiscount().multiply(invoice.getPosHeader().getExchangeRate())))));
        total.getElement().getStyle().set("margin-left", "auto");
        total.getElement().getStyle().set("font-weight", "bold");
        HorizontalLayout horizontalLayout = new HorizontalLayout(total);
        horizontalLayout.setWidthFull();
        invoicePreviewTotalLayout.add(horizontalLayout);
    }


    private void setTicket(PosHeader posHeader) {
        List<Item> collect = posHeader.getPosHeaderDetail().stream().filter(posHeaderDetail -> posHeaderDetail.getServices() != null || posHeaderDetail.getProduct() != null).map(posHeaderDetail -> {
            //            getVariableLayout(item);
            return new Item(posHeaderDetail, map, feeMap);
        }).toList();
        itemList.clear();
        itemList.addAll(collect);
        itemGrid.getDataProvider().refreshAll();
    }

}
