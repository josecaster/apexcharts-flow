package sr.we.ui.views.invoice;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.InvoiceService;
import sr.we.data.controller.PosHeaderService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.vo.InvoiceVO;
import sr.we.shekelflowcore.entity.helper.vo.PosHeaderVO;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.NotYetClick;
import sr.we.ui.components.TempDatePicker;
import sr.we.ui.components.customer.CustomerButton;
import sr.we.ui.views.ReRouteLayout;
import sr.we.ui.views.pos.ProductOrService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sr.we.ContextProvider.getBean;

/**
 * A Designer generated component for the create-invoice-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@BreadCrumb(titleKey = "sr.we.invoices.create", parentNavigationTarget = InvoiceView.class)
@Tag("create-invoice-view")
@JsModule("./src/views/invoice/create-invoice-view.ts")
public class CreateInvoiceView extends LitTemplate {


    private final TextArea footerMessageFld;
    private final VerticalLayout customerLayout;
    private final CustomerButton customerButton;
    private final TextField invoiceNameFld;
    private final TextField invoiceSummaryFld;
    private final Label companyNameLbl;
    protected ComboBox<ProductOrService> filterCmb;
    protected String business;
    protected Invoice invoice;
    @Id("new-invoice-header-details")
    protected Details createInvoiceHeaderDetails;
    @Id("div")
    protected VerticalLayout div;
    @Id("new-invoice-footer-details")
    protected Details newInvoiceFooterDetails;
    @Id("invoice-note-terms-fld")
    protected TextArea invoiceNoteTermsFld;
    @Id("new-invoice-main-layout")
    protected VerticalLayout newInvoiceMainLayout;
    @Id("invoice-table-layout")
    protected Div invoiceTableLayout;
    @Id("add-customer-layout")
    protected VerticalLayout addCustomerLayout;
    @Id("invoice-save-continue-btn")
    protected Button invoiceSaveContinueBtn;
    @Id("invoice-number-fld")
    protected TextField invoiceNumberFld;
    @Id("poso-number-fld")
    protected TextField posoNumberFld;
    @Id("payment-date-fld")
    protected TempDatePicker paymentDateFld;
    @Id("payment-due-fld")
    protected TempDatePicker paymentDueFld;
    private ItemGrid itemsGrid;
//    private Grid<Fee> feeGrid;
//    private Map<String, Object> feeMap;

    private Business business2;
    private PosHeader posHeader;

    private boolean activateListener = true;
    @Id("preview-btn")
    private Button previewBtn;

    /**
     * Creates a new CreateInvoiceView.
     */
    public CreateInvoiceView() {
        // You can initialise any data required for the connected UI components here.
        div.setMaxWidth("1000px");

        previewBtn.addClickListener(new NotYetClick<>());

        customerButton = new CustomerButton();

        addCustomerLayout.add(customerButton);

        addCustomerLayout.setMinWidth("320px");
        addCustomerLayout.setMinHeight("160px");

        customerLayout = new VerticalLayout();
        customerLayout.setWidthFull();
        addCustomerLayout.add(customerLayout);

        H5 headerSummary = new H5("Business address and contact details, title, summary, and logo");
        headerSummary.getElement().getStyle().set("margin", "5px");
        String medium = LumoUtility.BoxShadow.SMALL;

        createInvoiceHeaderDetails.setSummary(headerSummary);
        createInvoiceHeaderDetails.addThemeVariants(DetailsVariant.REVERSE);
        createInvoiceHeaderDetails.addClassNames(medium);
        createInvoiceHeaderDetails.getElement().getStyle().set("background", "var(--lumo-primary-contrast-color)");
        createInvoiceHeaderDetails.setVisible(false);

        H5 footerSummary = new H5("Footer");
        footerSummary.getElement().getStyle().set("margin", "5px");
        newInvoiceFooterDetails.setSummary(footerSummary);
        newInvoiceFooterDetails.addThemeVariants(DetailsVariant.REVERSE);
        newInvoiceFooterDetails.addClassNames(medium);
        newInvoiceFooterDetails.getElement().getStyle().set("background", "var(--lumo-primary-contrast-color)");
        footerMessageFld = new TextArea();
        footerMessageFld.setClassName(LumoUtility.Border.NONE);
        footerMessageFld.setWidthFull();
        VerticalLayout content = new VerticalLayout(footerMessageFld);
        content.setPadding(true);
        newInvoiceFooterDetails.setContent(content);

        newInvoiceMainLayout.addClassNames(medium);

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        VerticalLayout content1 = new VerticalLayout(formLayout);
        content1.setWidthFull();
        createInvoiceHeaderDetails.setContent(content1);
        Upload component = new Upload();
        component.setWidth("320px");
        component.setHeight("160px");
        formLayout.addComponentAsFirst(component);
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        layout.setAlignItems(FlexComponent.Alignment.END);
        formLayout.add(layout);
        VerticalLayout layout1 = new VerticalLayout();
        layout1.setPadding(false);
        layout1.setMargin(false);
        layout.add(layout1);

        invoiceNameFld = new TextField();
        invoiceNameFld.setValue("Invoice");
        invoiceNameFld.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.BOLD, LumoUtility.TextAlignment.RIGHT);
        invoiceSummaryFld = new TextField();
        invoiceSummaryFld.setPlaceholder("Write a short summary");
        invoiceSummaryFld.addClassNames(LumoUtility.TextAlignment.RIGHT);
        companyNameLbl = new Label();

        invoiceNameFld.setWidthFull();
        invoiceSummaryFld.setWidthFull();

        layout1.add(invoiceNameFld);
        layout1.add(invoiceSummaryFld);
        layout1.add(companyNameLbl);


        init();


        invoiceSaveContinueBtn.addClickListener(f -> save());

        paymentDateFld.addValueChangeListener(f -> {
            if (f.isFromClient()) {
                itemsGrid.setLocalDate(paymentDateFld.getValue());
                itemsGrid.refresh();
            }
        });
    }


    private void init() {
//        final Map<String, Object> map;
//        map = new HashMap<>();
//        feeMap = new HashMap<>();

        itemsGrid = new ItemGrid();
//        itemsGrid.setExecute(total());

//        feeGrid = new Grid<>();
//        feeGrid.setAllRowsVisible(true);
//        feeGrid.setAllRowsVisible(true);
//        feeGrid.setSelectionMode(Grid.SelectionMode.NONE);
//        feeGrid.setClassName("resonate");
//        feeGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
//        feeGrid.addColumn(Fee::getTitle).setFlexGrow(1);
//        feeGrid.addColumn(f -> {
//            BigDecimal price = f.getPrice();
//            return Constants.CURRENCY_FORMAT.format(price == null ? BigDecimal.ZERO : price);
//        }).setFlexGrow(0);
//
//        itemGrid = new Grid<>();
//        itemGrid.setSelectionMode(Grid.SelectionMode.NONE);
//        itemGrid.setAllRowsVisible(true);
//        itemGrid.setClassName("resonate");
//        itemGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
//        itemGrid.addComponentColumn(f -> {
//            TextField textField = new TextField();
//            textField.setWidthFull();
//            textField.setValue(f.getName());
//            textField.addValueChangeListener(event -> {
//                if (StringUtils.isBlank(event.getValue())) {
//                    textField.setValue(f.getName());
//                } else {
//                    f.setName(event.getValue());
//                }
//            });
//            return textField;
//        }).setHeader("Title").setFlexGrow(1);
//        itemGrid.addComponentColumn(item -> {
//            NumberField numberField = new NumberField();
//            numberField.setValue((double) item.getCount());
//            numberField.setMin(1);
//            numberField.setWidth("50px");
//            numberField.addValueChangeListener(event -> {
//                if (event.getValue() != null && event.getValue().compareTo((double) 0) == 0) {
//                    itemList.remove(item);
//                } else {
//                    item.setCount(event.getValue() == null ? 1 : event.getValue().intValue());
//                }
//                total();
//                itemGrid.getDataProvider().refreshAll();
//                feeGrid.getDataProvider().refreshAll();
//            });
//            return numberField;
//        }).setHeader("Quantity").setFlexGrow(0);
//        itemGrid.addColumn(item -> {
//            BigDecimal calcPrice = item.getPrice();
//            return Constants.CURRENCY_FORMAT.format(calcPrice == null ? BigDecimal.ZERO : calcPrice);
//        }).setHeader("Price").setFlexGrow(0);
//        itemGrid.addColumn(item -> {
//            BigDecimal calcPrice = item.getResult();
//            return Constants.CURRENCY_FORMAT.format(calcPrice == null ? BigDecimal.ZERO : calcPrice);
//        }).setHeader("Amount").setFlexGrow(0);
//        itemGrid.addComponentColumn(item -> {
//            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-times");
//            lineAwesomeIcon.addClickListener(clickEvent -> {
//                if (item.getPosHeaderDetail() != null) {
//                    removeList.add(item.getPosHeaderDetail().getId());
//                }
//                itemList.remove(item);
//                total();
//                itemGrid.getDataProvider().refreshAll();
//                feeGrid.getDataProvider().refreshAll();
//            });
//            return lineAwesomeIcon;
//        }).setFlexGrow(0);
//        itemGrid.setItemDetailsRenderer(new ComponentRenderer<>(this::getVariableLayout));
//        itemGrid.setDetailsVisibleOnClick(true);
//        itemList = new ArrayList<>();
//        itemGrid.setItems(itemList);
//
//        feeList = new ArrayList<>();
//        removeList = new ArrayList<>();
//        feeGrid.setItems(feeList);

        filterCmb = new ComboBox<>();
        filterCmb.setItemLabelGenerator(l -> {

            return l.getServices().getName();

        });

        filterCmb.addValueChangeListener(f -> {
            if (f.getValue() == null) {
                return;
            }
            ProductOrService productOrService = f.getValue();
            itemsGrid.addItem(productOrService);

        });
        filterCmb.setClassName(LumoUtility.Flex.GROW);
        invoiceTableLayout.add(itemsGrid);
        Label add_item = new Label("Add item: ");
        add_item.setClassName(LumoUtility.TextColor.PRIMARY);
        HorizontalLayout horizontalLayout = new HorizontalLayout(add_item, filterCmb);
        horizontalLayout.addClassNames(LumoUtility.Flex.GROW, //
                LumoUtility.Border.ALL, //
                LumoUtility.BorderColor.PRIMARY_10,//
                LumoUtility.BorderRadius.MEDIUM,//
                LumoUtility.Margin.MEDIUM,//
                LumoUtility.Padding.MEDIUM);//
        invoiceTableLayout.add(horizontalLayout);
//        invoiceTableLayout.add(feeGrid);TODO
//        return map;
    }

//    private void getFeeLayout(Item item) {
//        if (item.getFeeDescMap() != null && !item.getFeeDescMap().entrySet().isEmpty()) {
//            item.getFeeDescMap().forEach((key, calculationComponent) -> {
//                Fee fee = new Fee(this, calculationComponent);
//
//                Optional<Fee> any = feeList.stream().filter(g -> g.getTitle().equalsIgnoreCase(fee.getTitle())).findAny();
//                if (any.isPresent()) {
//                    any.get().setPrice(fee.getCalcPrice());
//                } else {
//                    feeList.add(fee);
////                    feeGrid.getDataProvider().refreshAll();
//                }
//            });
//        }
//        total();
//        feeGrid.getDataProvider().refreshAll();
//    }


//    private InterExecutable<Object, List<Item>> total() {
//        return new InterExecutable<Object, List<Item>>() {
//            @Override
//            public Object build(List<Item> itemList) {
//                total = itemList.stream().map(Item::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
////        total = total.add(feeList.stream().map(Fee::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
//                BigDecimal value = exchangeRateFld.getValue();
//                if (value == null) {
//                    value = BigDecimal.ONE;
//                }
//                BigDecimal obj = total == null ? BigDecimal.ZERO : total;
//                String text = Constants.CURRENCY_FORMAT.format(obj.multiply(value));
//                totalLbl.setText(text);
//                return null;
//            }
//        };
//    }

//    public Map<String, Object> getFeeMap() {
//        return feeMap;
//    }
//
////    public List<Item> getItemList() {
////        return itemList;
////    }
//
//    @Override
//    public void addFeeMap(String title, String toString) {
//        if (feeMap == null) {
//            feeMap = new HashMap<>();
//        }
//        feeMap.put(title, toString);
//    }

    public void save() {
        PosHeaderVO vo = itemsGrid.getVO();
        vo.setCharged(false);


        InvoiceVO invoiceVO = new InvoiceVO();
        if (invoice != null) {
            invoiceVO.setId(invoice.getId());
            invoiceVO.setNew(false);
        } else {
            invoiceVO.setNew(true);
        }
        invoiceVO.setInvoiceTitle(invoiceNameFld.getValue());
        invoiceVO.setHeaderMessage(invoiceSummaryFld.getValue());
        invoiceVO.setPosHeader(posHeader == null ? null : posHeader.getId());
        invoiceVO.setInvoiceDate(paymentDateFld.getValue());
        invoiceVO.setInvoiceNumber(invoiceNumberFld.getValue());
//        invoiceVO.setInvoiceTitle();
        invoiceVO.setBusiness(business2.getId());
        invoiceVO.setCustomer(getCustomerId());
        vo.setCustomerId(invoiceVO.getCustomer());
        invoiceVO.setPaymentDue(paymentDueFld.getValue());
        invoiceVO.setThankMessage(invoiceNoteTermsFld.getValue());
        invoiceVO.setFooterMessage(footerMessageFld.getValue());
        invoiceVO.setPosoNumber(posoNumberFld.getValue());
        invoiceVO.setPrice(vo.getPrice());
        invoiceVO.setAmount(invoiceVO.getPrice());
        invoiceVO.setExchangeRate(vo.getExchangeRate());
        invoiceVO.setConvertedAmount(vo.getConvertedAmount());
        invoiceVO.setCurrencyFrom(vo.getCurrencyFrom());
        invoiceVO.setCurrencyTo(vo.getCurrencyTo());
        vo.setInvoiceVO(invoiceVO);
        PosHeaderService posHeaderService = ContextProvider.getBean(PosHeaderService.class);
        if (vo.getId() == null) {
            posHeader = posHeaderService.create(AuthenticatedUser.token(), vo);
        } else {
            posHeader = posHeaderService.edit(AuthenticatedUser.token(), vo);
        }

        setByPosHeaderId(null, posHeader.getId());
        List<String> strings = Arrays.asList(invoice.getPosHeader().getId().toString());
        Map<String, List<String>> map = new HashMap<>();
        map.put("id", strings);
        QueryParameters queryParameters = new QueryParameters(map);
        UI.getCurrent().navigate(InvoiceSummaryView.getLocation(business), queryParameters);
    }


    private Long getCustomerId() {
        return customerButton.getCustomerId();
    }

    public void setInvoice(Invoice invoice) {
        activateListener = false;
        this.invoice = invoice;
        this.posHeader = invoice.getPosHeader();
        paymentDateFld.setValue(invoice.getInvoiceDate());
        paymentDueFld.setValue(invoice.getPaymentDue());
        invoiceNumberFld.setValue(StringUtils.isBlank(invoice.getInvoiceNumber()) ? "" : invoice.getInvoiceNumber());
        invoiceNoteTermsFld.setValue(StringUtils.isBlank(invoice.getThankMessage()) ? "" : invoice.getThankMessage());
        posoNumberFld.setValue(StringUtils.isBlank(invoice.getPosoNumber()) ? "" : invoice.getPosoNumber());
        footerMessageFld.setValue(StringUtils.isBlank(invoice.getFooterMessage()) ? "" : invoice.getFooterMessage());
//        invoiceVO.setInvoiceTitle(invoiceNameFld.getValue());
//        invoiceVO.setHeaderMessage(invoiceSummaryFld.getValue());
        invoiceNameFld.setValue(StringUtils.isBlank(invoice.getInvoiceTitle()) ? "Invoice" : invoice.getInvoiceTitle());
        invoiceSummaryFld.setValue(StringUtils.isBlank(invoice.getHeaderMessage()) ? "" : invoice.getHeaderMessage());
        companyNameLbl.setText(this.invoice.getBusiness().getName());
        customerButton.setCustomer(invoice.getCustomer());
        itemsGrid.setTicket(posHeader, (invoice.getConvertedAmount() == null ? BigDecimal.ZERO : invoice.getConvertedAmount()), invoice.getExchangeRate(), invoice.getCurrencyTo(), invoice.getInvoiceDate());

    }


    protected void setByPosHeaderId(BeforeEnterEvent event, Long posHeaderId) {
        String token = AuthenticatedUser.token();
        InvoiceService loanRequestService = getBean(InvoiceService.class);
        invoice = loanRequestService.getByPosHeader(posHeaderId, token);
        if (invoice == null) {
            if (event != null) {
                event.forwardTo(ReRouteLayout.class);
            }
            throw new ValidationException("Invalid Link");
        }
        setInvoice(invoice);

    }

    public void setBusiness2(Business business2) {
        customerButton.setBusiness(business2);
        activateListener = false;
        this.business2 = business2;
        itemsGrid.setBusiness(business2);
        companyNameLbl.setText(this.business2.getName());
        activateListener = true;
        InvoiceService loanRequestService = getBean(InvoiceService.class);
        String token = AuthenticatedUser.token();
        InvoiceSetting settings = loanRequestService.getSettings(business2.getId(), token);
        if (settings != null) {
            invoiceNoteTermsFld.setValue(settings.getThankMessage());
            footerMessageFld.setValue(settings.getFooterMessage());
            invoiceSummaryFld.setValue(settings.getHeaderMessage());
            invoiceNumberFld.setValue(String.valueOf(settings.getCurrentInvoiceNumber() == null ? 0 : settings.getCurrentInvoiceNumber() + 1));
            paymentDateFld.setValue(LocalDate.now());
            paymentDueFld.setValue(LocalDate.now().plusDays(14));
        }
    }
}
