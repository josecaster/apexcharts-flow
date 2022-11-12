package sr.we.ui.views.pos;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import sr.we.ContextProvider;
import sr.we.data.controller.*;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.InterExecutable;
import sr.we.shekelflowcore.entity.helper.adapter.CustomerBody;
import sr.we.shekelflowcore.entity.helper.vo.*;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.POSPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.MyDialog;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.finance.loanrequests.CustomerCmb;
import sr.we.ui.views.finance.transactions.TransactionDialog;
import sr.we.ui.views.invoice.ItemGrid;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A Designer generated component for the pos-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@BreadCrumb(titleKey = "sr.we.point.of.sale")
@Tag("pos-view")
@JsModule("./src/views/pos/pos-view.ts")
@Route(value = "pos", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
@PageTitle("Point of sale")
public class PosView extends LitTemplate implements BeforeEnterObserver {

    private final Dialog dialog;
    private final TicketsView ticketsGrid;
    private final List<PosHeader> ticketsList;
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
    private ItemGrid itemsGrid;
    //    private Grid<Fee> feeGrid;
//    private Map<String, Object> map, feeMap;
//    private List<Item> itemList = null;
//    private List<Fee> feeList = null;
    private LocalDateTime targetDate;
    private String business;
    private Business business2;
    @Id("tickets-layout")
    private Div ticketsLayout;
    @Id("top-bar-layout")
    private Element topBarLayout;
    @Id("customer-btn")
    private Button customerBtn;
    private Customer customer;

    /**
     * Creates a new PosView.
     */
    public PosView() {
//        mainFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1), new FormLayout.ResponsiveStep("1500px", 3));

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
            vo.setPosStart(posStart == null ? null : posStart.getId());
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


            TransactionDialog transactionDialog = new TransactionDialog(posHeader.getRest(), LocalDate.now(), Long.valueOf(business), business2.getCurrency(), business2.getCurrency(), Reference.POS, posHeader.getId());
            transactionDialog.disableAmount();
            transactionDialog.setOnSave(() -> {
                PosStartService posStartService = ContextProvider.getBean(PosStartService.class);
                List<PosStart> list = posStartService.list(Long.valueOf(business), targetDate.toLocalDate(), AuthenticatedUser.token()).getResult();
                posStart = list.get(0);
                refreshTickets();
                return null;
            });
            transactionDialog.setRefresh(() -> {
                PosStartService posStartService = ContextProvider.getBean(PosStartService.class);
                List<PosStart> list = posStartService.list(Long.valueOf(business), targetDate.toLocalDate(), AuthenticatedUser.token()).getResult();
                posStart = list.get(0);
                refreshTickets();
                startNewTicket();
                return null;
            });
            transactionDialog.open();
        });


        dialog = new MyDialog();

//        ticketsLayout.removeAll();
        ticketsGrid = new TicketsView();
        ticketsGrid.marginpadding("0px");
        ticketsGrid.addClassName(LumoUtility.Margin.NONE);
        ticketsLayout.add(ticketsGrid);
        ticketsList = new ArrayList<>();
//        ticketsGrid.setItems(ticketsList);
//        ticketsGrid.addColumn(f -> {
//            return "Ticket #" + f.getHeaderSeq();
//        }).setHeader("ID");
//        ticketsGrid.addColumn(PosHeader::getNote).setHeader("Note");
//        ticketsGrid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getPrice())).setHeader("Price");
//        ticketsGrid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getRest())).setHeader("Rest");
//        ticketsGrid.addComponentColumn(new ValueProvider<PosHeader, LineAwesomeIcon>() {
//            @Override
//            public LineAwesomeIcon apply(PosHeader posHeader) {
//                if (posHeader.getRest().compareTo(BigDecimal.ZERO) == 0) {
//                    LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-check");
//                    lineAwesomeIcon.getElement().getThemeList().add("badge primary success");
//                    return lineAwesomeIcon;
//                }
//                LineAwesomeIcon lineAwesomeIcon = null;
//                if (posHeader.getPaymentTransactions() != null && !posHeader.getPaymentTransactions().isEmpty()) {
//                    lineAwesomeIcon = new LineAwesomeIcon("la la-check");
//                } else {
//                    lineAwesomeIcon = new LineAwesomeIcon("la la-chevron-circle-down");
//                }
//                lineAwesomeIcon.addClickListener(f -> {
//                    TransactionDialog transactionDialog = new TransactionDialog(posHeader.getRest(), LocalDate.now(), Long.valueOf(business), business2.getCurrency(), business2.getCurrency(), Reference.POS, posHeader.getId());
////                    transactionDialog.disableAmount();
//                    transactionDialog.setOnSave(() -> {
//                        PosStartService posStartService = ContextProvider.getBean(PosStartService.class);
//                        List<PosStart> list = posStartService.list(Long.valueOf(business), targetDate.toLocalDate(), AuthenticatedUser.token());
//                        posStart = list.get(0);
//                        refreshTickets();
//                        return null;
//                    });
//                    transactionDialog.setRefresh(() -> {
////                        String placeholder = ticketNumber();
////                        posHeaderCmb.setPlaceholder(placeholder);
////                        productTitle.setText("New " + placeholder);
//                        PosStartService posStartService = ContextProvider.getBean(PosStartService.class);
//                        List<PosStart> list = posStartService.list(Long.valueOf(business), targetDate.toLocalDate(), AuthenticatedUser.token());
//                        posStart = list.get(0);
//                        refreshTickets();
//                        startNewTicket();
//                        return null;
//                    });
//                    transactionDialog.open();
////                    BigDecimal rest = detail.getFactor().subtract(detail.getTransactionsAmount());
////                    LocalDate initDate = detail.getInitDate();
////                    Long businessId = loanRequest.getLoan().getBusiness().getId();
//////                    LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
//////                    LoanRequest loanRequest1 = loanRequestService.get(loanRequestPlan.getLoanRequestId(), AuthenticatedUser.token());
////                    Currency fromCurrency = loanRequest.getLoan().getCurrency();
////                    Currency selectedCurrency = loanRequest.getCurrency();
////                    PaymentTransaction.Reference reference = PaymentTransaction.Reference.LOAN_REQUEST_PLAN_DETAIL;
////                    Long referenceId = detail.getId();
////                    PaymentTransaction.PlusMin plusMin = PaymentTransaction.PlusMin.PLUS;
////                    TransactionDialog transactionDialog = new TransactionDialog(rest, initDate, businessId, fromCurrency, selectedCurrency, reference, referenceId, plusMin);
////                    transactionDialog.setNextReferenceId(loanRequest.getId());
////                    transactionDialog.setRefresh(refresh);
////                    transactionDialog.open();
//                });
//
//                lineAwesomeIcon.getElement().getThemeList().add("badge primary error");
//                return lineAwesomeIcon;
//            }
//        }).setHeader("Record Payment");

        variableBtn.addClickListener(f -> {
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


        ContextMenu contextMenu = new ContextMenu(customerBtn);
        contextMenu.setOpenOnClick(true);
        contextMenu.addItem("Create new customer", g -> {
            VerticalLayout customerLayout = new VerticalLayout();
            TextField firstNameFld = new TextField();
            TextField lastNameFld = new TextField();
            TextField mobileNumberFld = new TextField();
            EmailField emailFld = new EmailField();

            firstNameFld.setPlaceholder("Firstname");
            lastNameFld.setPlaceholder("Name");
            mobileNumberFld.setPlaceholder("Mobile number");
            emailFld.setPlaceholder("Email-address");

            firstNameFld.setWidthFull();
            lastNameFld.setWidthFull();
            mobileNumberFld.setWidthFull();
            emailFld.setWidthFull();

            customerLayout.add(firstNameFld, lastNameFld, mobileNumberFld, emailFld);
            Dialog dialog1 = new MyDialog();
            dialog1.setHeaderTitle("Add new customer");
            dialog1.add(customerLayout);
            Button cancel = new Button("Cancel", (e) -> dialog1.close());
            cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog1.getFooter().add(cancel);
            Button save = new Button("Save");
            save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            dialog1.getFooter().add(save);
            dialog1.open();


            save.addClickListener(f -> {
                CustomerVO customerVO = new CustomerVO();
                customerVO.setNew(true);
                customerVO.setFirstName(firstNameFld.getValue());
                customerVO.setName(lastNameFld.getValue());
                customerVO.setBusiness(business2.getId());

                CustomerBody customerBody = new CustomerBody();
                customerBody.setNew(true);
                customerBody.setCustomerVO(customerVO);
                CustomerContactVO customerContactVO = new CustomerContactVO();
                customerContactVO.setMobile(mobileNumberFld.getValue());
                customerContactVO.setEmail(emailFld.getValue());

                customerBody.setCustomerContactVO(customerContactVO);
                CustomerService customerService = ContextProvider.getBean(CustomerService.class);

                customerBody.setCustomerBillingVO(new CustomerBillingVO());
                customerBody.setCustomerShippingVO(new CustomerShippingVO());
                customerBody.setShippingAddressVO(new CustomerAddressVO());
                customerBody.setBillingAddressVO(new CustomerAddressVO());
                Customer customer1 = customerService.create(AuthenticatedUser.token(), customerBody);
                setCustomer(customer1);
                dialog1.close();
            });

        });
        contextMenu.addItem("Choose existing customer", g -> {
            CustomerCmb existingCustomersCmb = new CustomerCmb();
            existingCustomersCmb.setWidthFull();
            existingCustomersCmb.setPlaceholder("Choose existing customer");
            existingCustomersCmb.load(business2.getId());
            Dialog dialog1 = new MyDialog();
            dialog1.setHeaderTitle("Select customer");
            dialog1.add(existingCustomersCmb);
            Button cancel = new Button("Cancel", (e) -> dialog1.close());
            cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog1.getFooter().add(cancel);
            Button save = new Button("Save");
            save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            dialog1.getFooter().add(save);
            dialog1.open();

            existingCustomersCmb.addValueChangeListener(f -> setCustomer(f.getValue()));
        });
    }

    private void setCustomer(Customer customer) {
        this.customer = customer;
        customerBtn.setText(customer.getName() + ", " + customer.getFirstName());
    }

    private void init() {
        // You can initialise any data required for the connected UI components here.
//        RadioButtonGroup<Radio> productservicesRadio = new RadioButtonGroup<Radio>();
//        radioLayout.setVisible(false);
//        radioLayout.add(productservicesRadio);
//        productservicesRadio.setItems(EnumSet.allOf(Radio.class));

        grid = new Grid();
//        map = new HashMap<>();
//        feeMap = new HashMap<>();
        grid.addClassName("resonate");
        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        boardLayout.addClassName("dashboard-view");
        boardLayout.removeAll();
        boardLayout.add(grid);
        grid.addComponentColumn(f -> rowBoard(f)).setFrozen(true).setHeader("Select an item");
        filterCmb.setVisible(false);
        filterCmb.setItemLabelGenerator(l -> {
//            if (l.getProduct() != null) {
//                return l.getProduct().getTitle();
//            } else {
            return l.getServices().getName();
//            }
        });

//        productservicesRadio.addValueChangeListener(f -> {
//        CallbackDataProvider<ProductOrService, String> dataProvider = null;
//        DataProvider<ProductOrServiceGrid, Void> dataProviderGrid = null;
//            if (f.getValue().compareTo(Radio.PRODUCTS) == 0) {
//                filterCmb.setPlaceholder("Filter products");
//
//
//                dataProvider = DataProviders.getProducts(business);
//                dataProviderGrid = DataProviders.getProductsGrid(business);
//            } else {
        filterCmb.setPlaceholder("Filter services");


//        dataProvider = DataProviders.getServices(business);
//        dataProviderGrid = DataProviders.getServicesGrid(business);
//            }
//
//        filterCmb.setItems(dataProvider);
//        grid.setItems(dataProviderGrid);
        ServicesVO vo = new ServicesVO();
        vo.setBusiness(Long.valueOf(business));
        ItemsService productService = ContextProvider.getBean(ItemsService.class);
        List<Items> list1 = productService.list(AuthenticatedUser.token(), vo).getResult();
        List<ProductOrService> list = list1.stream().map(ProductOrService::new).collect(Collectors.toList());

        List<ProductOrServiceGrid> list2 = DataProviders.toGrid(list);
        grid.setItems(list2);
//        });

//        productservicesRadio.setValue(Radio.SERVICES);

//        feeGrid = new Grid<>();
//        feeGrid.setHeight("60px");
//        feeLayout.removeAll();
//        feeLayout.add(feeGrid);
//        feeGrid.setAllRowsVisible(true);
//        feeGrid.setSelectionMode(Grid.SelectionMode.NONE);
//        feeGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_COMPACT);
//        feeGrid.addColumn(Fee::getTitle).setFlexGrow(1);
//        feeGrid.addColumn(f -> {
//            BigDecimal price = f.getPrice();
//            return Constants.CURRENCY_FORMAT.format(price == null ? BigDecimal.ZERO : price);
//        }).setFlexGrow(0);

        itemsGrid = new ItemGrid();
//        itemsGrid.setExecute(total());
        itemsGrid.setBusiness(business2);
        itemsLayout.removeAll();
        itemsLayout.add(itemsGrid);
        itemsLayout.setHeightFull();
        itemsGrid.setAllRowsVisible(false);
        itemsGrid.setHeightFull();
        itemsGrid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_COMPACT);
        itemsGrid.removeThemeVariants(GridVariant.LUMO_ROW_STRIPES);
//        itemGrid.setSelectionMode(Grid.SelectionMode.NONE);
//        itemGrid.setHeight("300px");
//        itemGrid.addThemeVariants( GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_COMPACT);
//        itemGrid.addComponentColumn(item -> {
//            NumberField numberField = new NumberField();
//            numberField.setValue((double) item.getCount());
//            numberField.setMin(1);
//            numberField.setWidth("50px");
//            numberField.addValueChangeListener(event -> {
//                if (event.getValue() != null && event.getValue().compareTo((double) 0) == 0) {
//                    itemList.remove(item);
//                    total();
//                    itemGrid.getDataProvider().refreshAll();
//                    feeGrid.getDataProvider().refreshAll();
//                } else {
//                    item.setCount(event.getValue() == null ? 1 : event.getValue().intValue());
//                    total();
//                    itemGrid.getDataProvider().refreshAll();
//                    feeGrid.getDataProvider().refreshAll();
//                }
//            });
//            return numberField;
//        }).setFlexGrow(0);
//        itemGrid.addColumn(Item::getName).setHeader("Title").setFlexGrow(1);
//        itemGrid.addColumn(item -> {
//            BigDecimal calcPrice = item.getResult();
//            return Constants.CURRENCY_FORMAT.format(calcPrice == null ? BigDecimal.ZERO : calcPrice);
//        }).setHeader("Price").setFlexGrow(0);
//        itemGrid.addComponentColumn(item -> {
//            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-times");
//            lineAwesomeIcon.addClickListener(clickEvent -> {
//                itemList.remove(item);
//                total();
//                itemGrid.getDataProvider().refreshAll();
//                feeGrid.getDataProvider().refreshAll();
//            });
//            return lineAwesomeIcon;
//        }).setAutoWidth(false).setWidth("25px");
//        itemGrid.setItemDetailsRenderer(new ComponentRenderer<VerticalLayout, Item>(data -> {
//            return getVariableLayout(data);
//        }));
//        itemGrid.setDetailsVisibleOnClick(true);
//        itemList = new ArrayList<>();
//        itemGrid.setItems(itemList);
//
//        feeList = new ArrayList<>();
//        feeGrid.setItems(feeList);


    }


//    private void getFeeLayout(Item item) {
//        if (item.getFeeDescMap() != null && !item.getFeeDescMap().entrySet().isEmpty()) {
//            item.getFeeDescMap().entrySet().forEach(e -> {
//                CalculationComponent calculationComponent = e.getValue();
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

//    private VerticalLayout getVariableLayout(Item d) {
//        VerticalLayout layout = new VerticalLayout();
//        layout.setMargin(false);
//        layout.setPadding(false);
//        if (d.getProductOrService().hasDetailedInventory()) {
//            ComboBox<ProductsInventoryDetail> detailCmb = new ComboBox<>();
//            layout.add(detailCmb);
//            detailCmb.setLabel("Product");
//            detailCmb.setPlaceholder("Choose a product");
//            detailCmb.setHelperText("This product requires a detailed selection");
//            detailCmb.setWidthFull();
//            detailCmb.setItems(d.getProductOrService().getDetailedInventory().stream().filter(e -> StringUtils.isNotBlank(e.getUniqueCode())).collect(Collectors.toList()));
//            detailCmb.setItemLabelGenerator(ProductsInventoryDetail::getUniqueCode);
//            detailCmb.setValue(d.getInventoryDetail());
//            detailCmb.addValueChangeListener(f -> {
//                d.setInventoryDetail(f.getValue());
//                itemGrid.getDataProvider().refreshAll();
////                variableBtn.click();
//                total();
//            });
//        }
//        if (d.getDescMap() != null && !d.getDescMap().entrySet().isEmpty()) {
//            d.getDescMap().entrySet().forEach(e -> {
//                BigDecimalField bigDecimalField = new BigDecimalField();
//                CalculationComponent calculationComponent = e.getValue();
//                bigDecimalField.setLabel(calculationComponent.getName());
//                bigDecimalField.setPlaceholder(calculationComponent.getCode());
//                bigDecimalField.setWidthFull();
//                bigDecimalField.setValue(StringUtils.isBlank((String) d.getMap().get(e.getKey())) ? null : BigDecimal.valueOf(Double.parseDouble((String) d.getMap().get(e.getKey()))));
//                layout.add(bigDecimalField);
//                bigDecimalField.addValueChangeListener(f -> {
//                    d.getMap().put(e.getKey(), (f.getValue() == null ? null : f.getValue().toString()));
//                    d.getFeeMap().put(e.getKey(), (f.getValue() == null ? null : f.getValue().toString()));
//                    itemGrid.getDataProvider().refreshAll();
//                    feeGrid.getDataProvider().refreshAll();
////                    variableBtn.click();
//                    total();
//                });
//            });
//        }
//        if (layout.getComponentCount() > 0) {
////            variableMenu.add(layout);
////            variableMenu.add(new Hr());
////            Animated.animate(variableBtn, Animated.Animation.BOUNCE);
//        }
//        return layout;
//    }

    public PosHeaderVO getVO() {
//        List<PosHeaderDetailVO> vos = new ArrayList<>();
        /*PosHeaderVO posHeaderVO = new PosHeaderVO();
        posHeaderVO.setId(posHeaderCmb.getValue() == null ? null : posHeaderCmb.getValue().getId());
        posHeaderVO.setNew(posHeaderVO.getId() == null);
        posHeaderVO.setPosStart(posStart == null ? null : posStart.getId());
        posHeaderVO.setDetails(vos);*/


        PosHeaderVO vo = itemsGrid.getVO();
        vo.setCustomerId(customer == null ? null : customer.getId());
        vo.setPosStart(posStart == null ? null : posStart.getId());
        vo.setCharged(false);
//        List<PosHeaderDetailVO> collectItems = itemList.stream().map(f -> {
//            PosHeaderDetailVO posHeaderDetailVO = new PosHeaderDetailVO();
//            posHeaderDetailVO.setId(f.getPosHeaderDetail() == null ? null : f.getPosHeaderDetail().getId());
//            posHeaderDetailVO.setNew(posHeaderDetailVO.getId() == null);
//            posHeaderDetailVO.setName(f.getName());
//            posHeaderDetailVO.setCalculationResult(f.getCalculate());
//            posHeaderDetailVO.setResult(f.getResult());
//            posHeaderDetailVO.setCount(Long.valueOf(f.getCount()));
//            posHeaderDetailVO.setPrice(f.getPrice());
//            posHeaderDetailVO.setInventoryDetail(f.getInventoryDetail() == null ? null : f.getInventoryDetail().getId());
////            posHeaderDetailVO.setProduct(f.getProductOrService() == null ? //
////                    (f.getPosHeaderDetail() == null ? null : (f.getPosHeaderDetail().getProduct() == null ? null : f.getPosHeaderDetail().getProduct().getId())) //
////                    : (f.getProductOrService().getProduct() == null ? null : f.getProductOrService().getProduct().getId()));//
//            posHeaderDetailVO.setService(f.getProductOrService() == null ? //
//                    (f.getPosHeaderDetail() == null ? null : (f.getPosHeaderDetail().getServices() == null ? null : f.getPosHeaderDetail().getServices().getId())) //
//                    : (f.getProductOrService().getServices() == null ? null : f.getProductOrService().getServices().getId()));//
//            return posHeaderDetailVO;
//        }).collect(Collectors.toList());
//
//        List<PosHeaderDetailVO> collectFees = feeList.stream().map(f -> {
//            PosHeaderDetailVO posHeaderDetailVO = new PosHeaderDetailVO();
//            posHeaderDetailVO.setId(f.getPosHeaderDetail() == null ? null : f.getPosHeaderDetail().getId());
//            posHeaderDetailVO.setNew(posHeaderDetailVO.getId() == null);
//            posHeaderDetailVO.setName(f.getTitle());
//            posHeaderDetailVO.setCalculationResult(f.getCalculate());
//            posHeaderDetailVO.setResult(f.getPrice());
//            posHeaderDetailVO.setCount(1L);
//            posHeaderDetailVO.setPrice(f.getPrice());
////            posHeaderDetailVO.setService(f.getCalculationComponent().getServices() == null ? null : f.getCalculationComponent().getServices().getId());
//            posHeaderDetailVO.setCalculationComponent(f.getCalculationComponent().getId());
//            return posHeaderDetailVO;
//        }).collect(Collectors.toList());

//        vos.addAll(itemsGrid.itemListVo());
//        vos.addAll(collectFees);

        /*BigDecimal reduce = itemsGrid.getItemList().stream().map(Item::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
//        reduce = reduce.add(feeList.stream().map(Fee::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        posHeaderVO.setPrice(reduce);
        posHeaderVO.setRemoveList(itemsGrid.getRemoveList());*/

        return vo;
    }

    private InterExecutable<Object, List<Item>> total() {
        return new InterExecutable<Object, List<Item>>() {

            @Override
            public Object build(List<Item> items) {
                BigDecimal reduce = items.stream().map(Item::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
//        reduce = reduce.add(feeList.stream().map(Fee::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
                String text = Constants.CURRENCY_FORMAT.format(reduce == null ? BigDecimal.ZERO : reduce);
                totalHeaderLbl.setText("Total " + text);
                return null;
            }
        };

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
        layout.setClassName("my-cart-base");
        UI current = UI.getCurrent();
        layout.addClickListener(f -> {
            itemsGrid.addItem(productOrService);
        });
        if (productOrService == null) {
            return layout;
        }

//        Product product = productOrService.getProduct();
        Items items = productOrService.getServices();
        String value = items.getId().toString();
        String title = items.getName();
        double procent =
                (items.getPrice() == null ? 0D : items.getPrice().doubleValue());


        H5 h2 = new H5(title);
        h2.addClassNames(LumoUtility.Margin.NONE);

        Span valueSpan = new Span();
        valueSpan.addClassNames("font-semibold", "text-3xl");


        layout.add(h2/*, valueSpan*/);


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
        List<PosStart> list = posStartService.list(Long.valueOf(business), targetDate.toLocalDate(), AuthenticatedUser.token()).getResult();
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
        ticketsGrid.setTickets(ticketsList, business2, () -> {
            PosStartService posStartService = ContextProvider.getBean(PosStartService.class);
            List<PosStart> list = posStartService.list(business2.getId(), targetDate.toLocalDate(), AuthenticatedUser.token()).getResult();
            posStart = list.get(0);
            refreshTickets();
            return null;
        }, () -> {
            PosStartService posStartService = ContextProvider.getBean(PosStartService.class);
            List<PosStart> list = posStartService.list(business2.getId(), targetDate.toLocalDate(), AuthenticatedUser.token()).getResult();
            posStart = list.get(0);
            refreshTickets();
            startNewTicket();
            return null;
        });
    }

    private void startNewTicket() {
        init();
        PosStartService posStartService = ContextProvider.getBean(PosStartService.class);
        List<PosStart> list = posStartService.list(Long.valueOf(business), targetDate.toLocalDate(), AuthenticatedUser.token()).getResult();
        if (list != null && !list.isEmpty()) {
            posStart = list.get(0);
        }
        String placeholder = ticketNumber();
        posHeaderCmb.setPlaceholder(placeholder);
        productTitle.setText("New " + placeholder);
        String text = "0.00";
        totalHeaderLbl.setText("Total " + text);
    }

    private String ticketNumber() {
        String placeholder = "Ticket #" + (posStart.getPosHeader().size() + 1);
        return placeholder;
    }

    private void setTicket(PosHeader posHeader) {
        init();
        productTitle.setText("Ticket #" + posHeader.getHeaderSeq());
        itemsGrid.setTicket(posHeader, posHeader.getConvertedAmount(), posHeader.getExchangeRate(), posHeader.getCurrencyTo(), posHeader.getPosStart().getTargetDate().toLocalDate());

        BigDecimal reduce = itemsGrid.getItemList().stream().map(Item::getResult).reduce(BigDecimal.ZERO, BigDecimal::add);
//        reduce = reduce.add(feeList.stream().map(Fee::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        String text = Constants.CURRENCY_FORMAT.format(reduce == null ? BigDecimal.ZERO : reduce);
        totalHeaderLbl.setText("Total " + text);
    }

}
