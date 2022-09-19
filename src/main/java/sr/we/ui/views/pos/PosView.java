package sr.we.ui.views.pos;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.PosHeaderService;
import sr.we.data.controller.PosStartService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.vo.PosHeaderDetailVO;
import sr.we.shekelflowcore.entity.helper.vo.PosHeaderVO;
import sr.we.shekelflowcore.entity.helper.vo.PosStartVO;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.POSPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.finance.transactions.TransactionDialog;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A Designer generated component for the pos-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("pos-view")
@JsModule("./src/views/pos/pos-view.ts")
@Route(value = "pos", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
@PageTitle("Point of sale")
public class PosView extends LitTemplate implements BeforeEnterObserver {

    private final Dialog dialog;
    private final Grid<PosHeader> ticketsGrid;
    @Id("board-layout")
    private Div boardLayout;
    @Id("main-form-layout")
    private FormLayout mainFormLayout;
    @Id("filter-cmb")
    private ComboBox<ProductOrService> filterCmb;
    @Id("radio-layout")
    private Div radioLayout;
    @Id("items-layout")
    private Div itemsLayout;
    @Id("total-header-lbl")
    private H5 totalHeaderLbl;
    @Id("total-amount-footer-lbl")
    private Label totalAmountFooterLbl;
    @Id("variable-btn")
    private Button variableBtn;
    @Id("fee-layout")
    private Div feeLayout;
    @Id("posHeaderCmb")
    private ComboBox<PosHeader> posHeaderCmb;
    @Id("product-title")
    private H2 productTitle;
    @Id("charge-btn")
    private Button chargeBtn;
    @Id("save-btn")
    private Button saveBtn;
    private PosStart posStart;
    private Grid<ProductOrServiceGrid> grid;
    private Grid<Item> itemGrid;
    private Grid<Fee> feeGrid;
    private Map<String, Object> map, feeMap;
    private List<Item> itemList = null;
    private List<Fee> feeList = null;
    private LocalDateTime targetDate;
    private String business;
    private Business business2;
    @Id("tickets-layout")
    private Div ticketsLayout;
    private final List<PosHeader> ticketsList;
    @Id("top-bar-layout")
    private Element topBarLayout;

    /**
     * Creates a new PosView.
     */
    public PosView() {
        mainFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1), new FormLayout.ResponsiveStep("1500px", 3));

        topBarLayout.setVisible(false);

        posHeaderCmb.setItemLabelGenerator(f -> {
            return "Ticket #" + f.getHeaderSeq();
        });
        posHeaderCmb.setClearButtonVisible(true);
        posHeaderCmb.addValueChangeListener(f -> {
            if (f.getValue() == null) {
                startNewTicket();
            } else {
                setTicket(f.getValue());
            }
        });


        saveBtn.addClickListener(f -> {
            PosHeaderVO vo = getVO();
            vo.setCharged(false);
            PosHeaderService posHeaderService = ContextProvider.getBean(PosHeaderService.class);
            PosHeader posHeader = null;
            if (vo.getId() == null) {
                posHeader = posHeaderService.create(AuthenticatedUser.token(), vo);
            } else {
                posHeader = posHeaderService.edit(AuthenticatedUser.token(), vo);
            }
        });

        chargeBtn.addClickListener(f -> {
            PosHeaderVO vo = getVO();
            vo.setCharged(false);
            PosHeaderService posHeaderService = ContextProvider.getBean(PosHeaderService.class);
            PosHeader posHeader = null;
            if (vo.getId() == null) {
                posHeader = posHeaderService.create(AuthenticatedUser.token(), vo);
            } else {
                posHeader = posHeaderService.edit(AuthenticatedUser.token(), vo);
            }


            TransactionDialog transactionDialog = new TransactionDialog(posHeader.getRest(), LocalDate.now(), Long.valueOf(business), business2.getCurrency(), business2.getCurrency(), PaymentTransaction.Reference.POS, posHeader.getId());
            transactionDialog.disableAmount();
            transactionDialog.setOnSave(() -> {
                return null;
            });
            transactionDialog.setRefresh(() -> {

                startNewTicket();
                return null;
            });
            transactionDialog.open();
        });


        dialog = new Dialog();

        ticketsLayout.removeAll();
        ticketsGrid = new Grid<>();
        ticketsLayout.add(ticketsGrid);
        ticketsList = new ArrayList<>();
        ticketsGrid.setItems(ticketsList);
        ticketsGrid.addColumn(f -> {
            return "Ticket #" + f.getHeaderSeq();
        }).setHeader("ID");
        ticketsGrid.addColumn(PosHeader::getNote).setHeader("Note");
        ticketsGrid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getPrice())).setHeader("Price");
        ticketsGrid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getRest())).setHeader("Rest");
        ticketsGrid.addComponentColumn(new ValueProvider<PosHeader, LineAwesomeIcon>() {
            @Override
            public LineAwesomeIcon apply(PosHeader posHeader) {
                if (posHeader.getRest().compareTo(BigDecimal.ZERO) == 0) {
                    LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-check");
                    lineAwesomeIcon.getElement().getThemeList().add("badge primary success");
                    return lineAwesomeIcon;
                }
                LineAwesomeIcon lineAwesomeIcon = null;
                if (posHeader.getPaymentTransactions() != null && !posHeader.getPaymentTransactions().isEmpty()) {
                    lineAwesomeIcon = new LineAwesomeIcon("la la-check");
                } else {
                    lineAwesomeIcon = new LineAwesomeIcon("la la-chevron-circle-down");
                }
                lineAwesomeIcon.addClickListener(f -> {
                    TransactionDialog transactionDialog = new TransactionDialog(posHeader.getRest(), LocalDate.now(), Long.valueOf(business), business2.getCurrency(), business2.getCurrency(), PaymentTransaction.Reference.POS, posHeader.getId());
//                    transactionDialog.disableAmount();
                    transactionDialog.setOnSave(() -> {
                        return null;
                    });
                    transactionDialog.setRefresh(() -> {
//                        String placeholder = ticketNumber();
//                        posHeaderCmb.setPlaceholder(placeholder);
//                        productTitle.setText("New " + placeholder);
                        startNewTicket();
                        return null;
                    });
                    transactionDialog.open();
//                    BigDecimal rest = detail.getFactor().subtract(detail.getTransactionsAmount());
//                    LocalDate initDate = detail.getInitDate();
//                    Long businessId = loanRequest.getLoan().getBusiness().getId();
////                    LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
////                    LoanRequest loanRequest1 = loanRequestService.get(loanRequestPlan.getLoanRequestId(), AuthenticatedUser.token());
//                    Currency fromCurrency = loanRequest.getLoan().getCurrency();
//                    Currency selectedCurrency = loanRequest.getCurrency();
//                    PaymentTransaction.Reference reference = PaymentTransaction.Reference.LOAN_REQUEST_PLAN_DETAIL;
//                    Long referenceId = detail.getId();
//                    PaymentTransaction.PlusMin plusMin = PaymentTransaction.PlusMin.PLUS;
//                    TransactionDialog transactionDialog = new TransactionDialog(rest, initDate, businessId, fromCurrency, selectedCurrency, reference, referenceId, plusMin);
//                    transactionDialog.setNextReferenceId(loanRequest.getId());
//                    transactionDialog.setRefresh(refresh);
//                    transactionDialog.open();
                });

                lineAwesomeIcon.getElement().getThemeList().add("badge primary error");
                return lineAwesomeIcon;
            }
        }).setHeader("Record Payment");

        variableBtn.addClickListener(f-> {
            PosHeaderVO vo = getVO();
            vo.setCharged(false);
            PosHeaderService posHeaderService = ContextProvider.getBean(PosHeaderService.class);
            PosHeader posHeader = null;
            if (vo.getId() == null) {
                posHeader = posHeaderService.create(AuthenticatedUser.token(), vo);
            } else {
                posHeader = posHeaderService.edit(AuthenticatedUser.token(), vo);
            }
            startNewTicket();
            refreshTickets();
            ticketsGrid.scrollIntoView();
        });
    }

    private void init() {
        // You can initialise any data required for the connected UI components here.
        RadioButtonGroup<Radio> productservicesRadio = new RadioButtonGroup<Radio>();
        radioLayout.removeAll();
        radioLayout.add(productservicesRadio);
        productservicesRadio.setItems(EnumSet.allOf(Radio.class));

        grid = new Grid();
        map = new HashMap<>();
        feeMap = new HashMap<>();
        grid.addClassName("dashboard-view");
        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        boardLayout.addClassName("dashboard-view");
        boardLayout.removeAll();
        boardLayout.add(grid);
        grid.addComponentColumn(f -> rowBoard(f)).setFrozen(true);

        filterCmb.setItemLabelGenerator(l -> {
            if (l.getProduct() != null) {
                return l.getProduct().getTitle();
            } else {
                return l.getServices().getName();
            }
        });

        productservicesRadio.addValueChangeListener(f -> {
            CallbackDataProvider<ProductOrService, String> dataProvider = null;
            DataProvider<ProductOrServiceGrid, Void> dataProviderGrid = null;
            if (f.getValue().compareTo(Radio.PRODUCTS) == 0) {
                filterCmb.setPlaceholder("Filter products");


                dataProvider = DataProviders.getProducts(business);
                dataProviderGrid = DataProviders.getProductsGrid(business);
            } else {
                filterCmb.setPlaceholder("Filter services");


                dataProvider = DataProviders.getServices(business);
                dataProviderGrid = DataProviders.getServicesGrid(business);
            }

            filterCmb.setItems(dataProvider);
            grid.setItems(dataProviderGrid);
        });

        productservicesRadio.setValue(Radio.SERVICES);

        feeGrid = new Grid<>();
        feeGrid.setHeight("60px");
        feeLayout.removeAll();
        feeLayout.add(feeGrid);
        feeGrid.setAllRowsVisible(true);
        feeGrid.setSelectionMode(Grid.SelectionMode.NONE);
        feeGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_COMPACT);
        feeGrid.addColumn(Fee::getTitle).setFlexGrow(1);
        feeGrid.addColumn(f -> {
            BigDecimal price = f.getPrice();
            return Constants.CURRENCY_FORMAT.format(price == null ? BigDecimal.ZERO : price);
        }).setFlexGrow(0);

        itemGrid = new Grid<>();
        itemsLayout.removeAll();
        itemsLayout.add(itemGrid);
        itemGrid.setSelectionMode(Grid.SelectionMode.NONE);
        itemGrid.setHeight("300px");
        itemGrid.addThemeVariants( GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_COMPACT);
        itemGrid.addComponentColumn(item -> {
            NumberField numberField = new NumberField();
            numberField.setValue((double) item.getCount());
            numberField.setMin(1);
            numberField.setWidth("50px");
            numberField.addValueChangeListener(event -> {
                if (event.getValue() != null && event.getValue().compareTo((double) 0) == 0) {
                    itemList.remove(item);
                    total();
                    itemGrid.getDataProvider().refreshAll();
                    feeGrid.getDataProvider().refreshAll();
                } else {
                    item.setCount(event.getValue() == null ? 1 : event.getValue().intValue());
                    total();
                    itemGrid.getDataProvider().refreshAll();
                    feeGrid.getDataProvider().refreshAll();
                }
            });
            return numberField;
        }).setFlexGrow(0);
        itemGrid.addColumn(Item::getName).setHeader("Title").setFlexGrow(1);
        itemGrid.addColumn(item -> {
            BigDecimal calcPrice = item.getResult();
            return Constants.CURRENCY_FORMAT.format(calcPrice == null ? BigDecimal.ZERO : calcPrice);
        }).setHeader("Price").setFlexGrow(0);
        itemGrid.addComponentColumn(item -> {
            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-times");
            lineAwesomeIcon.addClickListener(clickEvent -> {
                itemList.remove(item);
                total();
                itemGrid.getDataProvider().refreshAll();
                feeGrid.getDataProvider().refreshAll();
            });
            return lineAwesomeIcon;
        }).setAutoWidth(false).setWidth("25px");
        itemGrid.setItemDetailsRenderer(new ComponentRenderer<VerticalLayout, Item>(data -> {
            return getVariableLayout(data);
        }));
        itemGrid.setDetailsVisibleOnClick(true);
        itemList = new ArrayList<>();
        itemGrid.setItems(itemList);

        feeList = new ArrayList<>();
        feeGrid.setItems(feeList);


    }


    private void getFeeLayout(Item item) {
        if (item.getFeeDescMap() != null && !item.getFeeDescMap().entrySet().isEmpty()) {
            item.getFeeDescMap().entrySet().forEach(e -> {
                CalculationComponent calculationComponent = e.getValue();
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
            d.getDescMap().entrySet().forEach(e -> {
                BigDecimalField bigDecimalField = new BigDecimalField();
                CalculationComponent calculationComponent = e.getValue();
                bigDecimalField.setLabel(calculationComponent.getName());
                bigDecimalField.setPlaceholder(calculationComponent.getCode());
                bigDecimalField.setWidthFull();
                bigDecimalField.setValue(StringUtils.isBlank((String) d.getMap().get(e.getKey())) ? null : BigDecimal.valueOf(Double.parseDouble((String) d.getMap().get(e.getKey()))));
                layout.add(bigDecimalField);
                bigDecimalField.addValueChangeListener(f -> {
                    d.getMap().put(e.getKey(), (f.getValue() == null ? null : f.getValue().toString()));
                    d.getFeeMap().put(e.getKey(), (f.getValue() == null ? null : f.getValue().toString()));
                    itemGrid.getDataProvider().refreshAll();
                    feeGrid.getDataProvider().refreshAll();
//                    variableBtn.click();
                    total();
                });
            });
        }
        if (layout.getComponentCount() > 0) {
//            variableMenu.add(layout);
//            variableMenu.add(new Hr());
//            Animated.animate(variableBtn, Animated.Animation.BOUNCE);
        }
        return layout;
    }

    public PosHeaderVO getVO() {
        List<PosHeaderDetailVO> vos = new ArrayList<>();
        PosHeaderVO posHeaderVO = new PosHeaderVO();
        posHeaderVO.setId(posHeaderCmb.getValue() == null ? null : posHeaderCmb.getValue().getId());
        posHeaderVO.setNew(posHeaderVO.getId() == null);
        posHeaderVO.setPosStart(posStart == null ? null : posStart.getId());
        posHeaderVO.setCustomerId(null);//TODO
        posHeaderVO.setDetails(vos);
        List<PosHeaderDetailVO> collectItems = itemList.stream().map(f -> {
            PosHeaderDetailVO posHeaderDetailVO = new PosHeaderDetailVO();
            posHeaderDetailVO.setId(f.getPosHeaderDetail() == null ? null : f.getPosHeaderDetail().getId());
            posHeaderDetailVO.setNew(posHeaderDetailVO.getId() == null);
            posHeaderDetailVO.setName(f.getName());
            posHeaderDetailVO.setCalculationResult(f.getCalculate());
            posHeaderDetailVO.setResult(f.getResult());
            posHeaderDetailVO.setCount(Long.valueOf(f.getCount()));
            posHeaderDetailVO.setPrice(f.getPrice());
            posHeaderDetailVO.setInventoryDetail(f.getInventoryDetail() == null ? null : f.getInventoryDetail().getId());
            posHeaderDetailVO.setProduct(f.getProductOrService() == null ? //
                    (f.getPosHeaderDetail() == null ? null : (f.getPosHeaderDetail().getProduct() == null ? null : f.getPosHeaderDetail().getProduct().getId())) //
                    : (f.getProductOrService().getProduct() == null ? null : f.getProductOrService().getProduct().getId()));//
            posHeaderDetailVO.setService(f.getProductOrService() == null ? //
                    (f.getPosHeaderDetail() == null ? null : (f.getPosHeaderDetail().getServices() == null ? null : f.getPosHeaderDetail().getServices().getId())) //
                    : (f.getProductOrService().getServices() == null ? null : f.getProductOrService().getServices().getId()));//
            return posHeaderDetailVO;
        }).collect(Collectors.toList());

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
        }).collect(Collectors.toList());

        vos.addAll(collectItems);
        vos.addAll(collectFees);

        BigDecimal reduce = itemList.stream().map(Item::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        reduce = reduce.add(feeList.stream().map(Fee::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        posHeaderVO.setPrice(reduce);

        return posHeaderVO;
    }

    private void total() {
        BigDecimal reduce = itemList.stream().map(Item::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        reduce = reduce.add(feeList.stream().map(Fee::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        String text = Constants.CURRENCY_FORMAT.format(reduce == null ? BigDecimal.ZERO : reduce);
        totalHeaderLbl.setText("Total " + text);
        totalAmountFooterLbl.setText(text);
    }

    private Component rowBoard(ProductOrServiceGrid productOrServiceGrid) {
        Board board = new Board();
        Row row = board.addRow();
        if (productOrServiceGrid.getOne() != null) {
            row.add(itemCard(productOrServiceGrid.getOne()));
        }
        if (productOrServiceGrid.getTwo() != null) {
            row.add(itemCard(productOrServiceGrid.getTwo()));
        }
        if (productOrServiceGrid.getThree() != null) {
            row.add(itemCard(productOrServiceGrid.getThree()));
        }
        if (productOrServiceGrid.getFour() != null) {
            row.add(itemCard(productOrServiceGrid.getFour()));
        }
        return board;
    }

    public Component itemCard(ProductOrService productOrService) {
        VerticalLayout layout = new VerticalLayout();
        UI current = UI.getCurrent();
        layout.addClickListener(f -> {
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
        if (productOrService == null) {
            return layout;
        }

        Product product = productOrService.getProduct();
        Services services = productOrService.getServices();
        String value = product != null ? product.getId().toString() : services.getId().toString();
        String title = product != null ? product.getTitle() : services.getName();
        double procent = product != null ? (product.getPrice() == null ? 0D : product.getPrice().doubleValue()) : //
                (services.getPrice() == null ? 0D : services.getPrice().doubleValue());


        H2 h2 = new H2(title);
        h2.addClassNames("font-normal", "m-0", "text-secondary", "text-xs");

        Span valueSpan = new Span();
        valueSpan.addClassNames("font-semibold", "text-3xl");


        layout.add(h2, valueSpan);


        String build = value;
        valueSpan.setText(build);


        layout.addClassName("p-l");
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        return layout;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new POSPrivilege(), Privileges.INSERT);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        business1.ifPresent(s -> business = s);
        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
        business2 = businessService.get(Long.valueOf(business), AuthenticatedUser.token());
        PosStartService posStartService = ContextProvider.getBean(PosStartService.class);
        targetDate = LocalDateTime.now();
        List<PosStart> list = posStartService.list(Long.valueOf(business), targetDate.toLocalDate(), AuthenticatedUser.token());
        if (list == null || list.isEmpty()) {

            dialog.setCloseOnOutsideClick(false);
            dialog.setCloseOnEsc(false);
            dialog.removeAll();
            dialog.setHeaderTitle("Start a new shift");
            FormLayout formLayout = new FormLayout();
            formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1));
            DateTimePicker dateTimePicker = new DateTimePicker();
            dateTimePicker.setValue(targetDate);
            formLayout.addFormItem(dateTimePicker, "Start time");
            BigDecimalField cashFld = new BigDecimalField();
            formLayout.addFormItem(cashFld, "Starting cash");
            dialog.add(formLayout);
            Button startBtn = new Button("Start");
            startBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            startBtn.getElement().getStyle().set("margin-left", "auto");
            dialog.getFooter().add(startBtn);
            dialog.open();
            startBtn.addClickListener(f -> {
                PosStartVO posStartVO = new PosStartVO();
                posStartVO.setBusiness(Long.valueOf(business));
                posStartVO.setTargetDate(dateTimePicker.getValue());
                posStartVO.setStartCash(cashFld.getValue());
                posStartVO.setNew(true);

                posStartService.create(AuthenticatedUser.token(), posStartVO);

                UI.getCurrent().getPage().reload();
            });
        } else {
            if (dialog != null && dialog.isOpened()) {
                dialog.close();
            }
            posStart = list.get(0);
            refreshTickets();
//            posHeaderCmb.setItems(collect);
//            Optional<PosHeader> max = collect.stream().max(Comparator.comparingLong(PosHeader::getHeaderSeq));
//            if (max.isPresent()) {
//                PosHeader posHeader = max.get();
//                posHeaderCmb.setValue(posHeader);
//                productTitle.setText("Ticket #" + posHeader.getHeaderSeq());
//            } else {
            // start new ticket
//            String placeholder = ticketNumber();
//            posHeaderCmb.setPlaceholder(placeholder);
//            productTitle.setText("New " + placeholder);
            startNewTicket();
//            }
        }
    }

    private void refreshTickets() {
        ticketsList.clear();
        ticketsList.addAll(posStart.getPosHeader().stream().filter(f -> f.getCharged() == null || !f.getCharged()).collect(Collectors.toList()));
        ticketsGrid.getDataProvider().refreshAll();
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public Map<String, Object> getFeeMap() {
        return feeMap;
    }

    public void addFeeMap(String key, Object value) {
        if (feeMap == null) {
            feeMap = new HashMap<>();
        }
        feeMap.put(key, value);
    }

    public List<Item> getItemList() {
        return itemList;
    }

    private void startNewTicket() {
        init();
        PosStartService posStartService = ContextProvider.getBean(PosStartService.class);
        List<PosStart> list = posStartService.list(Long.valueOf(business), targetDate.toLocalDate(), AuthenticatedUser.token());
        if (list != null && !list.isEmpty()) {
            posStart = list.get(0);
        }
        String placeholder = ticketNumber();
        posHeaderCmb.setPlaceholder(placeholder);
        productTitle.setText("New " + placeholder);
        String text = "0.00";
        totalHeaderLbl.setText("Total " + text);
        totalAmountFooterLbl.setText(text);
    }

    private String ticketNumber() {
        String placeholder = "Ticket #" + (posStart.getPosHeader().size() + 1);
        return placeholder;
    }

    private void setTicket(PosHeader posHeader) {
        init();
        productTitle.setText("Ticket #" + posHeader.getHeaderSeq());
        List<Item> collect = posHeader.getPosHeaderDetail().stream().filter(posHeaderDetail -> posHeaderDetail.getServices() != null || posHeaderDetail.getProduct() != null).map(posHeaderDetail -> {
            Item item = new Item(posHeaderDetail, map, feeMap);
//            getVariableLayout(item);
            return item;
        }).collect(Collectors.toList());
        itemList.clear();
        itemList.addAll(collect);
        itemGrid.getDataProvider().refreshAll();

        List<Fee> collect1 = posHeader.getPosHeaderDetail().stream().filter(posHeaderDetail -> posHeaderDetail.getCalculationComponent() != null).map(posHeaderDetail -> {
            return new Fee(PosView.this, posHeaderDetail, posHeaderDetail.getCalculationComponent());
        }).collect(Collectors.toList());
        feeList.clear();
        feeList.addAll(collect1);
        feeGrid.getDataProvider().refreshAll();

        BigDecimal reduce = itemList.stream().map(Item::getResult).reduce(BigDecimal.ZERO, BigDecimal::add);
        reduce = reduce.add(feeList.stream().map(Fee::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        String text = Constants.CURRENCY_FORMAT.format(reduce == null ? BigDecimal.ZERO : reduce);
        totalHeaderLbl.setText("Total " + text);
        totalAmountFooterLbl.setText(text);
    }

}
