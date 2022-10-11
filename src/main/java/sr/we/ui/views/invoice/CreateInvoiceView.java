package sr.we.ui.views.invoice;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.ExchangeRateService;
import sr.we.data.controller.InvoiceService;
import sr.we.data.controller.PosHeaderService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.vo.InvoiceVO;
import sr.we.shekelflowcore.entity.helper.vo.PosHeaderDetailVO;
import sr.we.shekelflowcore.entity.helper.vo.PosHeaderVO;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.TempDatePicker;
import sr.we.ui.components.general.CurrencySelect;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.ReRouteLayout;
import sr.we.ui.views.finance.loanrequests.CustomerCmb;
import sr.we.ui.views.pos.Fee;
import sr.we.ui.views.pos.IFee;
import sr.we.ui.views.pos.Item;
import sr.we.ui.views.pos.ProductOrService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static sr.we.ContextProvider.getBean;

/**
 * A Designer generated component for the create-invoice-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("create-invoice-view")
@JsModule("./src/views/invoice/create-invoice-view.ts")
public class CreateInvoiceView extends LitTemplate implements IFee {

    private final Map<String, Object> map;
    private final CurrencySelect currencySelect;
    private final BigDecimalField exchangeRateFld;
    private final Label totalLbl;
    private final TextArea footerMessageFld;
    private final VerticalLayout customerLayout;
    private final Button addCustomerLabelLayout;
    private final TextField invoiceNameFld;
    private final TextField invoiceSummaryFld;
    private final Label companyNameLbl;
    protected ComboBox<ProductOrService> filterCmb;
    protected String business;
    protected Invoice invoice;
    private TextField firstNameFld;
    private TextField lastNameFld;
    private TextField mobileNumberFld;
    private EmailField emailFld;
    private Grid<Item> itemGrid;
    private Grid<Fee> feeGrid;
    private Map<String, Object> feeMap;
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
    private List<Item> itemList = null;
    private List<Fee> feeList = null;
    @Id("invoice-table-layout")
    protected Div invoiceTableLayout;
    private Business business2;
    private PosHeader posHeader;
    @Id("add-customer-layout")
    protected VerticalLayout addCustomerLayout;
    @Id("invoice-save-continue-btn")
    protected Button invoiceSaveContinueBtn;
    private CustomerCmb existingCustomersCmb;
    @Id("invoice-number-fld")
    protected TextField invoiceNumberFld;
    @Id("poso-number-fld")
    protected TextField posoNumberFld;
    @Id("payment-date-fld")
    protected TempDatePicker paymentDateFld;
    @Id("payment-due-fld")
    protected TempDatePicker paymentDueFld;
    private BigDecimal total;
    private boolean activateListener = true;

    /**
     * Creates a new CreateInvoiceView.
     */
    public CreateInvoiceView() {
        // You can initialise any data required for the connected UI components here.
        div.setMaxWidth("1000px");

        addCustomerLabelLayout = new Button();
        addCustomerLabelLayout.setIcon(new LineAwesomeIcon("la la-plus"));
        addCustomerLabelLayout.setText("Add a customer");
        addCustomerLabelLayout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        addCustomerLabelLayout.setClassName(LumoUtility.AlignItems.CENTER);

        addCustomerLayout.add(addCustomerLabelLayout);

        addCustomerLayout.setMinWidth("320px");
        addCustomerLayout.setMinHeight("160px");

        customerLayout = new VerticalLayout();
        customerLayout.setWidthFull();
        addCustomerLayout.add(customerLayout);


        ContextMenu contextMenu = new ContextMenu(addCustomerLabelLayout);
        contextMenu.setOpenOnClick(true);
        contextMenu.addItem("Create new customer", g -> {
            customerLayout.removeAll();
            firstNameFld = new TextField();
            lastNameFld = new TextField();
            mobileNumberFld = new TextField();
            emailFld = new EmailField();

            firstNameFld.setPlaceholder("Firstname");
            lastNameFld.setPlaceholder("Name");
            mobileNumberFld.setPlaceholder("Mobile number");
            emailFld.setPlaceholder("Email-address");

            firstNameFld.setWidthFull();
            lastNameFld.setWidthFull();
            mobileNumberFld.setWidthFull();
            emailFld.setWidthFull();

            customerLayout.add(firstNameFld, lastNameFld, mobileNumberFld, emailFld);
        });
        contextMenu.addItem("Choose existing customer", g -> {
            customerLayout.removeAll();
            existingCustomersCmb = new CustomerCmb();
            existingCustomersCmb.setWidthFull();
            existingCustomersCmb.setPlaceholder("Choose existing customer");
            existingCustomersCmb.load(business2.getId());
            customerLayout.add(existingCustomersCmb);

            existingCustomersCmb.addValueChangeListener(f -> setCustomer(f.getValue()));
        });

        H5 headerSummary = new H5("Business address and contact details, title, summary, and logo");
        headerSummary.getElement().getStyle().set("margin", "5px");
        String medium = LumoUtility.BoxShadow.SMALL;

        createInvoiceHeaderDetails.setSummary(headerSummary);
        createInvoiceHeaderDetails.addThemeVariants(DetailsVariant.REVERSE);
        createInvoiceHeaderDetails.addClassNames(medium);
        createInvoiceHeaderDetails.getElement().getStyle().set("background", "var(--lumo-primary-contrast-color)");

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
        invoiceNameFld.addClassNames(LumoUtility.FontSize.LARGE,LumoUtility.FontWeight.BOLD,LumoUtility.TextAlignment.RIGHT);
        invoiceSummaryFld = new TextField();
        invoiceSummaryFld.setPlaceholder("Write a short summary");
        invoiceSummaryFld.addClassNames(LumoUtility.TextAlignment.RIGHT);
        companyNameLbl = new Label();

        invoiceNameFld.setWidthFull();
        invoiceSummaryFld.setWidthFull();

        layout1.add(invoiceNameFld);
        layout1.add(invoiceSummaryFld);
        layout1.add(companyNameLbl);



        map = init();

        HorizontalLayout totalLayout = new HorizontalLayout();
        totalLayout.setClassName(LumoUtility.Flex.GROW);
        invoiceTableLayout.add(totalLayout);

        Label totalNameLbl = new Label("Total");
        currencySelect = new CurrencySelect();
        exchangeRateFld = new BigDecimalField();
        totalLbl = new Label("0.00");

        totalNameLbl.getElement().getStyle().set("margin-left", "auto");
        totalNameLbl.getElement().getStyle().set("font-weight", "bold");
        totalLbl.getElement().getStyle().set("font-weight", "bold");
        totalNameLbl.setClassName(LumoUtility.TextAlignment.RIGHT);
        currencySelect.setLabel(null);
        currencySelect.setHelperText(null);

        totalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        totalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        totalLayout.add(totalNameLbl);
        totalLayout.add(currencySelect);
        totalLayout.add(exchangeRateFld);
        totalLayout.add(totalLbl);

        invoiceSaveContinueBtn.addClickListener(f -> save());

        currencySelect.addValueChangeListener(g -> {
            if (g.isFromClient()) {
                if (currencySelect.getValue() == null) {
                    exchangeRateFld.setValue(BigDecimal.ZERO);
                    return;
                }
                ExchangeRateService exchangeRateService = ContextProvider.getBean(ExchangeRateService.class);
                try {
                    BigDecimal exchange = exchangeRateService.exchange(CreateInvoiceView.this.business2.getCurrency().getCode(), g.getValue().getCode(), business2.getId(), AuthenticatedUser.token());
                    exchangeRateFld.setValue(exchange);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        exchangeRateFld.addValueChangeListener(h -> {
            if (activateListener) {
                BigDecimal val = total.multiply(h.getValue());
                totalLbl.setText(Constants.CURRENCY_FORMAT.format(val));
            }
        });
    }

    private void setCustomer(Customer value) {
        if (value != null) {
            customerLayout.removeAll();
            customerLayout.add(new Label(//
                    (StringUtils.isNotBlank(value.getFirstName()) ? (value.getFirstName() + " ,") : "") + //
                            (StringUtils.isNotBlank(value.getName()) ? value.getName() : "")));//
            CustomerContact primaryCustomerContacts = value.getPrimaryCustomerContacts();
            if (primaryCustomerContacts != null) {
                if (StringUtils.isNotBlank(primaryCustomerContacts.getMobile())) {
                    customerLayout.add(new Label(primaryCustomerContacts.getMobile()));
                }
                if (StringUtils.isNotBlank(primaryCustomerContacts.getEmail())) {
                    customerLayout.add(new Label(primaryCustomerContacts.getEmail()));
                }
            }
            addCustomerLabelLayout.setIcon(new LineAwesomeIcon("la la-pencil"));
            addCustomerLabelLayout.setText("Edit customer");
        } else {
            addCustomerLabelLayout.setIcon(new LineAwesomeIcon("la la-plus"));
            addCustomerLabelLayout.setText("Add a customer");
        }
    }

    private Map<String, Object> init() {
        final Map<String, Object> map;
        map = new HashMap<>();
        feeMap = new HashMap<>();


        feeGrid = new Grid<>();
        feeGrid.setAllRowsVisible(true);
        feeGrid.setAllRowsVisible(true);
        feeGrid.setSelectionMode(Grid.SelectionMode.NONE);
        feeGrid.setClassName("resonate");
        feeGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        feeGrid.addColumn(Fee::getTitle).setFlexGrow(1);
        feeGrid.addColumn(f -> {
            BigDecimal price = f.getPrice();
            return Constants.CURRENCY_FORMAT.format(price == null ? BigDecimal.ZERO : price);
        }).setFlexGrow(0);

        itemGrid = new Grid<>();
        itemGrid.setSelectionMode(Grid.SelectionMode.NONE);
        itemGrid.setAllRowsVisible(true);
        itemGrid.setClassName("resonate");
        itemGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        itemGrid.addComponentColumn(f -> {
            TextField textField = new TextField();
            textField.setWidthFull();
            textField.setValue(f.getName());
            return textField;
        }).setHeader("Title").setFlexGrow(1);
        itemGrid.addComponentColumn(item -> {
            NumberField numberField = new NumberField();
            numberField.setValue((double) item.getCount());
            numberField.setMin(1);
            numberField.setWidth("50px");
            numberField.addValueChangeListener(event -> {
                if (event.getValue() != null && event.getValue().compareTo((double) 0) == 0) {
                    itemList.remove(item);
                } else {
                    item.setCount(event.getValue() == null ? 1 : event.getValue().intValue());
                }
                total();
                itemGrid.getDataProvider().refreshAll();
                feeGrid.getDataProvider().refreshAll();
            });
            return numberField;
        }).setHeader("Quantity").setFlexGrow(0);
        itemGrid.addColumn(item -> {
            BigDecimal calcPrice = item.getPrice();
            return Constants.CURRENCY_FORMAT.format(calcPrice == null ? BigDecimal.ZERO : calcPrice);
        }).setHeader("Price").setFlexGrow(0);
        itemGrid.addColumn(item -> {
            BigDecimal calcPrice = item.getResult();
            return Constants.CURRENCY_FORMAT.format(calcPrice == null ? BigDecimal.ZERO : calcPrice);
        }).setHeader("Amount").setFlexGrow(0);
        itemGrid.addComponentColumn(item -> {
            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-times");
            lineAwesomeIcon.addClickListener(clickEvent -> {
                itemList.remove(item);
                total();
                itemGrid.getDataProvider().refreshAll();
                feeGrid.getDataProvider().refreshAll();
            });
            return lineAwesomeIcon;
        }).setFlexGrow(0);
        itemGrid.setItemDetailsRenderer(new ComponentRenderer<>(this::getVariableLayout));
        itemGrid.setDetailsVisibleOnClick(true);
        itemList = new ArrayList<>();
        itemGrid.setItems(itemList);

        feeList = new ArrayList<>();
        feeGrid.setItems(feeList);

        filterCmb = new ComboBox<>();
        filterCmb.setItemLabelGenerator(l -> {
            if (l.getProduct() != null) {
                return l.getProduct().getTitle();
            } else {
                return l.getServices().getName();
            }
        });

        filterCmb.addValueChangeListener(f -> {
            if (f.getValue() == null) {
                return;
            }
            ProductOrService productOrService = f.getValue();
            Optional<Item> any = itemList.stream().filter(g -> {
                ProductOrService productOrService1 = g.getProductOrService();
                if (productOrService1 == null) {
                    PosHeaderDetail posHeaderDetail = g.getPosHeaderDetail();
                    if (posHeaderDetail.getServices() != null && productOrService.getServices() != null) {
                        return posHeaderDetail.getServices().getId().compareTo(productOrService.getServices().getId()) == 0;
                    } else if (posHeaderDetail.getProduct() != null && productOrService.getProduct() != null) {
                        return posHeaderDetail.getProduct().getId().compareTo(productOrService.getProduct().getId()) == 0;
                    }
                } else {
                    if (productOrService1.getServices() != null && productOrService.getServices() != null) {
                        return productOrService1.getServices().getId().compareTo(productOrService.getServices().getId()) == 0;
                    } else if (productOrService1.getProduct() != null && productOrService.getProduct() != null) {
                        return productOrService1.getProduct().getId().compareTo(productOrService.getProduct().getId()) == 0;
                    }
                }
                return false;
            }).findAny();
            if (any.isPresent()) {
                Item item = any.get();
                item.addCount();
//                total();
                itemGrid.getDataProvider().refreshAll();
                getVariableLayout(item);
                getFeeLayout(item);
            } else {
                Item item = new Item(productOrService, map, feeMap);
                itemList.add(item);
                itemGrid.getDataProvider().refreshAll();
//                total();
                getVariableLayout(item);
                getFeeLayout(item);
            }
        });
        filterCmb.setClassName(LumoUtility.Flex.GROW);
        invoiceTableLayout.add(itemGrid);
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
        invoiceTableLayout.add(feeGrid);
        return map;
    }

    private void getFeeLayout(Item item) {
        if (item.getFeeDescMap() != null && !item.getFeeDescMap().entrySet().isEmpty()) {
            item.getFeeDescMap().forEach((key, calculationComponent) -> {
                Fee fee = new Fee(this, calculationComponent);

                Optional<Fee> any = feeList.stream().filter(g -> g.getTitle().equalsIgnoreCase(fee.getTitle())).findAny();
                if (any.isPresent()) {
                    any.get().setPrice(fee.getCalcPrice());
                } else {
                    feeList.add(fee);
//                    feeGrid.getDataProvider().refreshAll();
                }
            });
        }
        total();
        feeGrid.getDataProvider().refreshAll();
    }

    private VerticalLayout getVariableLayout(Item d) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        layout.setPadding(false);
        if (d.getProductOrService().hasDetailedInventory()) {
            ComboBox<ProductsInventoryDetail> detailCmb = new ComboBox<>();
            layout.add(detailCmb);
            detailCmb.setLabel("Product");
            detailCmb.setPlaceholder("Choose a product");
            detailCmb.setHelperText("This product requires a detailed selection");
            detailCmb.setWidthFull();
            detailCmb.setItems(d.getProductOrService().getDetailedInventory().stream().filter(e -> StringUtils.isNotBlank(e.getUniqueCode())).collect(Collectors.toList()));
            detailCmb.setItemLabelGenerator(ProductsInventoryDetail::getUniqueCode);
            detailCmb.setValue(d.getInventoryDetail());
            detailCmb.addValueChangeListener(f -> {
                d.setInventoryDetail(f.getValue());
                itemGrid.getDataProvider().refreshAll();
//                variableBtn.click();
                total();
            });
        }
        if (d.getDescMap() != null && !d.getDescMap().entrySet().isEmpty()) {
            d.getDescMap().forEach((key, calculationComponent) -> {
                BigDecimalField bigDecimalField = new BigDecimalField();
                bigDecimalField.setLabel(calculationComponent.getName());
                bigDecimalField.setPlaceholder(calculationComponent.getCode());
                bigDecimalField.setWidthFull();
                bigDecimalField.setValue(StringUtils.isBlank((String) d.getMap().get(key)) ? null : BigDecimal.valueOf(Double.parseDouble((String) d.getMap().get(key))));
                layout.add(bigDecimalField);
                bigDecimalField.addValueChangeListener(f -> {
                    d.getMap().put(key, (f.getValue() == null ? null : f.getValue().toString()));
                    d.getFeeMap().put(key, (f.getValue() == null ? null : f.getValue().toString()));
                    itemGrid.getDataProvider().refreshAll();
                    feeGrid.getDataProvider().refreshAll();
//                    variableBtn.click();
                    total();
                });
            });
        }
        layout.getComponentCount();
        return layout;
    }

    private void total() {
        total = itemList.stream().map(Item::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        total = total.add(feeList.stream().map(Fee::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        BigDecimal value = exchangeRateFld.getValue();
        if (value == null) {
            value = BigDecimal.ONE;
        }
        BigDecimal obj = total == null ? BigDecimal.ZERO : total;
        String text = Constants.CURRENCY_FORMAT.format(obj.multiply(value));
        totalLbl.setText(text);
    }

    public Map<String, Object> getFeeMap() {
        return feeMap;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    @Override
    public void addFeeMap(String title, String toString) {
        if (feeMap == null) {
            feeMap = new HashMap<>();
        }
        feeMap.put(title, toString);
    }

    public void save() {
        PosHeaderVO vo = getVO();
        vo.setCharged(false);


        InvoiceVO invoiceVO = new InvoiceVO();
        if (invoice != null) {
            invoiceVO.setId(invoice.getId());
            invoiceVO.setNew(false);
        } else {
            invoiceVO.setNew(true);
        }
        if (firstNameFld != null) {
            invoiceVO.setFirstName(firstNameFld.getValue());
            invoiceVO.setName(lastNameFld.getValue());
            invoiceVO.setMobileNumber(mobileNumberFld.getValue());
            invoiceVO.setEmailAddress(emailFld.getValue());
        }
        invoiceVO.setInvoiceTitle(invoiceNameFld.getValue());
        invoiceVO.setHeaderMessage(invoiceSummaryFld.getValue());
        invoiceVO.setPosHeader(posHeader == null ? null : posHeader.getId());
        invoiceVO.setInvoiceDate(paymentDateFld.getValue());
        invoiceVO.setInvoiceNumber(invoiceNumberFld.getValue());
//        invoiceVO.setInvoiceTitle();
        invoiceVO.setBusiness(business2.getId());
        invoiceVO.setCustomer(existingCustomersCmb == null ? //
                (invoice == null ? null : //
                        (invoice.getCustomer() == null ? null : invoice.getCustomer().getId())//
                ) : //
                (existingCustomersCmb.getValue() == null ? null : //
                        existingCustomersCmb.getValue().getId()));
        invoiceVO.setPaymentDue(paymentDueFld.getValue());
        invoiceVO.setThankMessage(invoiceNoteTermsFld.getValue());
        invoiceVO.setFooterMessage(footerMessageFld.getValue());
        invoiceVO.setPosoNumber(posoNumberFld.getValue());
        invoiceVO.setPrice(vo.getPrice());
        invoiceVO.setAmount(invoiceVO.getPrice());
        invoiceVO.setExchangeRate(exchangeRateFld.getValue());
        invoiceVO.setConvertedAmount(invoiceVO.getPrice().multiply(exchangeRateFld.getValue()));
        invoiceVO.setCurrencyFrom(business2.getCurrency().getId());
        invoiceVO.setCurrencyTo(currencySelect.getValue().getId());
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
        UI.getCurrent().navigate(InvoiceSummaryView.getLocation(business),queryParameters);
    }

    public PosHeaderVO getVO() {
        List<PosHeaderDetailVO> vos = new ArrayList<>();
        PosHeaderVO posHeaderVO = new PosHeaderVO();
        posHeaderVO.setId(posHeader == null ? null : posHeader.getId());
        posHeaderVO.setNew(posHeaderVO.getId() == null);
        posHeaderVO.setPosStart(null);
        posHeaderVO.setCustomerId(null);//TODO
        posHeaderVO.setDetails(vos);
        List<PosHeaderDetailVO> collectItems = itemList.stream().map(f -> {
            PosHeaderDetailVO posHeaderDetailVO = new PosHeaderDetailVO();
            posHeaderDetailVO.setId(f.getPosHeaderDetail() == null ? null : f.getPosHeaderDetail().getId());
            posHeaderDetailVO.setNew(posHeaderDetailVO.getId() == null);
            posHeaderDetailVO.setName(f.getName());
            posHeaderDetailVO.setCalculationResult(f.getCalculate());
            posHeaderDetailVO.setResult(f.getResult());
            posHeaderDetailVO.setCount((long) f.getCount());
            posHeaderDetailVO.setPrice(f.getPrice());
            posHeaderDetailVO.setInventoryDetail(f.getInventoryDetail() == null ? null : f.getInventoryDetail().getId());
            posHeaderDetailVO.setProduct(f.getProductOrService() == null ? //
                    (f.getPosHeaderDetail() == null ? null : (f.getPosHeaderDetail().getProduct() == null ? null : f.getPosHeaderDetail().getProduct().getId())) //
                    : (f.getProductOrService().getProduct() == null ? null : f.getProductOrService().getProduct().getId()));//
            posHeaderDetailVO.setService(f.getProductOrService() == null ? //
                    (f.getPosHeaderDetail() == null ? null : (f.getPosHeaderDetail().getServices() == null ? null : f.getPosHeaderDetail().getServices().getId())) //
                    : (f.getProductOrService().getServices() == null ? null : f.getProductOrService().getServices().getId()));//
            return posHeaderDetailVO;
        }).toList();

        List<PosHeaderDetailVO> collectFees = feeList.stream().map(f -> {
            PosHeaderDetailVO posHeaderDetailVO = new PosHeaderDetailVO();
            posHeaderDetailVO.setId(f.getPosHeaderDetail() == null ? null : f.getPosHeaderDetail().getId());
            posHeaderDetailVO.setNew(posHeaderDetailVO.getId() == null);
            posHeaderDetailVO.setName(f.getTitle());
            posHeaderDetailVO.setCalculationResult(f.getCalculate());
            posHeaderDetailVO.setResult(f.getPrice());
            posHeaderDetailVO.setCount(1L);
            posHeaderDetailVO.setPrice(f.getPrice());
//            posHeaderDetailVO.setService(f.getCalculationComponent().getServices() == null ? null : f.getCalculationComponent().getServices().getId());
            posHeaderDetailVO.setCalculationComponent(f.getCalculationComponent().getId());
            return posHeaderDetailVO;
        }).toList();

        vos.addAll(collectItems);
        vos.addAll(collectFees);

        BigDecimal reduce = itemList.stream().map(Item::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        reduce = reduce.add(feeList.stream().map(Fee::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        posHeaderVO.setPrice(reduce);

        return posHeaderVO;
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
        setCustomer(invoice.getCustomer());
        setTicket(posHeader);
        BigDecimal obj = invoice.getConvertedAmount() == null ? BigDecimal.ZERO : invoice.getConvertedAmount();
        total = obj;
        currencySelect.setValue(invoice.getCurrencyTo());
        exchangeRateFld.setValue(invoice.getExchangeRate());
        String text = Constants.CURRENCY_FORMAT.format(obj);
        totalLbl.setText(text);
        activateListener = true;
    }

    private void setTicket(PosHeader posHeader) {
//        init();
//        productTitle.setText("Ticket #" + posHeader.getHeaderSeq());
        List<Item> collect = posHeader.getPosHeaderDetail().stream().filter(posHeaderDetail -> posHeaderDetail.getServices() != null || posHeaderDetail.getProduct() != null).map(posHeaderDetail -> {
            //            getVariableLayout(item);
            return new Item(posHeaderDetail, map, feeMap);
        }).toList();
        itemList.clear();
        itemList.addAll(collect);
        itemGrid.getDataProvider().refreshAll();

        List<Fee> collect1 = posHeader.getPosHeaderDetail().stream().filter(posHeaderDetail -> posHeaderDetail.getCalculationComponent() != null &&
                posHeaderDetail.getCalculationComponent().getCategory().compareTo(CalculationComponent.Category.FEE) == 0).map(posHeaderDetail -> new Fee(this, posHeaderDetail, posHeaderDetail.getCalculationComponent())).toList();
        feeList.clear();
        feeList.addAll(collect1);
        feeGrid.getDataProvider().refreshAll();

        BigDecimal reduce = itemList.stream().map(Item::getResult).reduce(BigDecimal.ZERO, BigDecimal::add);
        reduce = reduce.add(feeList.stream().map(Fee::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    protected void setByPosHeaderId(BeforeEnterEvent event, Long posHeaderId) {
        String token = AuthenticatedUser.token();
        InvoiceService loanRequestService = getBean(InvoiceService.class);
        invoice = loanRequestService.get(posHeaderId, token);
        if (invoice == null) {
            if (event != null) {
                event.forwardTo(ReRouteLayout.class);
            }
            throw new ValidationException("Invalid Link");
        }
        setInvoice(invoice);
    }

    public void setBusiness2(Business business2) {
        activateListener = false;
        this.business2 = business2;
        currencySelect.setValue(business2.getCurrency());
        exchangeRateFld.setValue(BigDecimal.ONE);
        companyNameLbl.setText(this.business2.getName());
        activateListener = true;
    }
}
