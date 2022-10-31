package sr.we.ui.views.invoice;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.ExchangeRateService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.InterExecutable;
import sr.we.shekelflowcore.entity.helper.vo.PosHeaderDetailVO;
import sr.we.shekelflowcore.entity.helper.vo.PosHeaderVO;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.shekelflowcore.settings.util.NumberUtil;
import sr.we.ui.components.general.BusinessCurrencySelect;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.pos.Item;
import sr.we.ui.views.pos.ProductOrService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ItemGrid extends Grid<Item> {

    private final BigDecimalField discountFld;
    private final BigDecimalField exchangeRateFld;
    private final Label totalLbl;
    private final Map<String, Object> map;
    private BusinessCurrencySelect currencySelect = null;
    private List<Item> itemList = null;
    //    private List<Fee> feeList = null;
    private List<Long> removeList = null;
    private BigDecimal total;

    private boolean activateListener = true;
    private PosHeader posHeader;
    private InterExecutable<Object, List<Item>> execute;
    private Business business2;

    public ItemGrid() {
        setExecute(total());
        map = new HashMap<>();
        setSelectionMode(Grid.SelectionMode.NONE);
        setAllRowsVisible(true);
        setClassName("resonate");
        addThemeVariants(GridVariant.LUMO_NO_BORDER);
        Column<Item> title = addComponentColumn(f -> {
            TextField textField = new TextField();
            textField.setWidthFull();
            textField.setValue(f.getName());
            textField.addValueChangeListener(event -> {
                if (StringUtils.isBlank(event.getValue())) {
                    textField.setValue(f.getName());
                } else {
                    f.setName(event.getValue());
                }
            });
            return textField;
        }).setHeader("Title").setFlexGrow(1);
        Column<Item> quantity = addComponentColumn(item -> {
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
                execute.build(itemList);
                getDataProvider().refreshAll();
//                feeGrid.getDataProvider().refreshAll();//TODO
            });
            return numberField;
        }).setHeader("Quantity").setFlexGrow(0);
        Column<Item> price = addColumn(item -> {
            BigDecimal calcPrice = item.getPrice();
            return Constants.CURRENCY_FORMAT.format(calcPrice == null ? BigDecimal.ZERO : calcPrice);
        }).setHeader("Price").setFlexGrow(0);
        Column<Item> amount = addColumn(item -> {
            BigDecimal calcPrice = item.getResult();
            return Constants.CURRENCY_FORMAT.format(calcPrice == null ? BigDecimal.ZERO : calcPrice);
        }).setHeader("Amount").setFlexGrow(0);
        addComponentColumn(item -> {
            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-times");
            lineAwesomeIcon.addClickListener(clickEvent -> {
                if (item.getPosHeaderDetail() != null) {
                    removeList.add(item.getPosHeaderDetail().getId());
                }
                itemList.remove(item);
                execute.build(itemList);
                getDataProvider().refreshAll();
//                feeGrid.getDataProvider().refreshAll();TODO
            });
            return lineAwesomeIcon;
        }).setFlexGrow(0);
        addComponentColumn(d -> {
            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-exclamation-triangle");
            boolean visible = false;
            boolean containsExchangeOnly = false;
            if (d.getProductOrService().getServices().getCurrency().getId().compareTo(currencySelect.getValue().getId()) != 0) {
                visible = true;
                containsExchangeOnly = true;
            }
            if (d.getProductOrService().hasDetailedInventory()) {
                visible = true;
                containsExchangeOnly = false;
            }
            if (d.getDescMap() != null && !d.getDescMap().entrySet().isEmpty() && d.getPosHeaderDetail() == null) {
                visible = true;
                containsExchangeOnly = false;
            }
            if (visible) {
                if ((d.isValid() != null && d.isValid()) || (d.isValid() == null && containsExchangeOnly)) {
                    d.setValid(true);
                    lineAwesomeIcon.removeClassName(LumoUtility.TextColor.ERROR);
                    lineAwesomeIcon.addClassName(LumoUtility.TextColor.SUCCESS);
                } else {
                    d.setValid(false);
                    lineAwesomeIcon.addClassName(LumoUtility.TextColor.ERROR);
                    lineAwesomeIcon.removeClassName(LumoUtility.TextColor.SUCCESS);
                }
            }
            lineAwesomeIcon.setVisible(visible);
            lineAwesomeIcon.addClickListener(f -> setDetailsVisible(d, !isDetailsVisible(d)));
            return lineAwesomeIcon;
        }).setFlexGrow(0);
        setItemDetailsRenderer(new ComponentRenderer<>(this::getVariableLayout));
        setDetailsVisibleOnClick(false);
        itemList = new ArrayList<>();
        setItems(itemList);

        removeList = new ArrayList<>();
//        feeGrid.setItems(feeList);//TODO


        FooterRow footerRow1 = appendFooterRow();
        footerRow1.getCell(quantity).setText("Subtotal");

        getDataProvider().addDataProviderListener(g -> {
            BigDecimal reduce = itemList.stream().map(f -> {
                BigDecimal bigDecimal = f.getResult() == null ? f.getCalcPrice() : f.getResult();
                return bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            footerRow1.getCell(amount).setText(Constants.CURRENCY_FORMAT.format(reduce));
        });

        FooterRow footerRow = appendFooterRow();
        FooterRow.FooterCell join = footerRow.getCell(quantity);
        Label discount = new Label("Discount");
        discount.setWidthFull();
        discount.addClassNames(LumoUtility.TextAlignment.RIGHT, LumoUtility.FontWeight.BOLD);
        join.setComponent(discount);
//        join.setText("Discount");
        discountFld = new BigDecimalField();
        discountFld.setWidthFull();
        discountFld.setValue(BigDecimal.ZERO);
        discountFld.addValueChangeListener(f -> {
            if (f.getValue() == null) {
                discountFld.setValue(BigDecimal.ZERO);
            }
            execute.build(itemList);
            getDataProvider().refreshAll();
        });

        footerRow.join(price, amount).setComponent(discountFld);


        FooterRow footerRow2 = appendFooterRow();
        HorizontalLayout invoiceTableLayout = new HorizontalLayout();
        footerRow2.join(footerRow2.getCells().stream().limit(4).collect(Collectors.toList())).setComponent(invoiceTableLayout);
        HorizontalLayout totalLayout = new HorizontalLayout();
        totalLayout.setClassName(LumoUtility.Flex.GROW);
        invoiceTableLayout.add(totalLayout);

        Label totalNameLbl = new Label("Total");
        currencySelect = new BusinessCurrencySelect();
        exchangeRateFld = new BigDecimalField();
        exchangeRateFld.setVisible(false);
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

        currencySelect.addValueChangeListener(g -> {
            if (g.isFromClient()) {
                if (currencySelect.getValue() == null) {
                    exchangeRateFld.setValue(BigDecimal.ZERO);
                    return;
                }
                ExchangeRateService exchangeRateService = ContextProvider.getBean(ExchangeRateService.class);
                try {
                    BigDecimal exchange = exchangeRateService.exchange(business2.getCurrency().getCode(), g.getValue().getCode(), business2.getId(), AuthenticatedUser.token());
                    exchangeRateFld.setValue(exchange);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        exchangeRateFld.addValueChangeListener(h -> {
            if (activateListener) {
                execute.build(itemList);
                getDataProvider().refreshAll();
            }
        });

    }

    private VerticalLayout getVariableLayout(Item d) {
        VerticalLayout layout = new VerticalLayout();
        layout.setClassName("my-cart-base");
        layout.setMargin(true);
        layout.setPadding(true);
        if (d.getProductOrService().getServices().getCurrency().getId().compareTo(currencySelect.getValue().getId()) != 0) {
            BigDecimalField bigDecimalField = new BigDecimalField();
            bigDecimalField.setLabel("Exchange Rate ");
            bigDecimalField.setHelperText(d.getProductOrService().getServices().getCurrency().getCode() + " - " + currencySelect.getValue().getCode());
            bigDecimalField.setValue(d.getExchange());
            layout.add(bigDecimalField);

            bigDecimalField.addValueChangeListener(f -> {
                d.setValid(f.getValue() != null);
                if (f.getValue() == null) {
                    bigDecimalField.setValue(BigDecimal.ONE);
                    return;
                }

                d.setExchange(f.getValue());
                getDataProvider().refreshAll();
                execute.build(itemList);
            });
        }
        if (d.getProductOrService().hasDetailedInventory()) {
            ComboBox<ProductsInventoryDetail> detailCmb = new ComboBox<>();
            layout.add(detailCmb);
            detailCmb.setLabel("Product");
            detailCmb.setPlaceholder("Choose a product");
            detailCmb.setHelperText("This product requires a detailed selection");
            detailCmb.setWidthFull();
            List<ProductsInventoryDetail> collect = d.getProductOrService().getDetailedInventory().stream().filter(e -> StringUtils.isNotBlank(e.getUniqueCode()) && e.getCustomerId() == null).collect(Collectors.toList());
            if (d.getPosHeaderDetail() != null && d.getPosHeaderDetail().getProduct() != null) {
                collect.add(d.getPosHeaderDetail().getProduct());
                detailCmb.setReadOnly(true);
            }
            detailCmb.setItems(collect);
            detailCmb.setItemLabelGenerator(ProductsInventoryDetail::getUniqueCode);

            detailCmb.setValue(d.getInventoryDetail() == null ? (d.getPosHeaderDetail() == null ? null : d.getPosHeaderDetail().getProduct()) : d.getInventoryDetail());
//            d.setInventoryDetail(detailCmb.getValue());

            detailCmb.addValueChangeListener(f -> {
                d.setValid(f.getValue() != null);
                d.setInventoryDetail(f.getValue());
                getDataProvider().refreshAll();
//                variableBtn.click();
                execute.build(itemList);
            });
        }
        if (d.getDescMap() != null && !d.getDescMap().entrySet().isEmpty() && d.getPosHeaderDetail() == null) {
            d.getDescMap().entrySet().stream().filter(f -> {//
                return f.getValue() != null && f.getValue().getType() != null && (//
                        f.getValue().getType().compareTo(CalculationComponent.Type.VALUE) == 0//
                                ||//
                                f.getValue().getType().compareTo(CalculationComponent.Type.VALUE_FORMULA) == 0//
                );
            }).forEach(g -> {
                String key = g.getKey();
                CalculationComponent calculationComponent = g.getValue();
                BigDecimalField bigDecimalField = new BigDecimalField();
                bigDecimalField.setLabel(calculationComponent.getName());
                bigDecimalField.setPlaceholder(calculationComponent.getCode());
                bigDecimalField.setWidthFull();
                if(StringUtils.isNotBlank(calculationComponent.getFormula()) && NumberUtil.isNumeric(calculationComponent.getFormula())) {
                    d.getMap().put(key, BigDecimal.valueOf(Double.parseDouble(calculationComponent.getFormula())));
                }
                bigDecimalField.setValue(StringUtils.isBlank((String) d.getMap().get(key)) ? null : BigDecimal.valueOf(Double.parseDouble((String) d.getMap().get(key))));
                layout.add(bigDecimalField);
                bigDecimalField.addValueChangeListener(f -> {
                    d.setValid(f.getValue() != null);
                    d.getMap().put(key, (f.getValue() == null ? null : f.getValue().toString()));
                    d.getFeeMap().put(key, (f.getValue() == null ? null : f.getValue().toString()));
                    getDataProvider().refreshAll();
//                    feeGrid.getDataProvider().refreshAll();TODO
//                    variableBtn.click();
                    execute.build(itemList);
                });
            });
        }
        layout.getComponentCount();
        return layout;
    }

    public List<PosHeaderDetailVO> itemListVo() {
        return itemList.stream().map(f -> {
            if (f.isValid() != null && !f.isValid()) {
                throw new ValidationException("Please fix invalid items before saving");
            }
            PosHeaderDetailVO posHeaderDetailVO = new PosHeaderDetailVO();
            posHeaderDetailVO.setId(f.getPosHeaderDetail() == null ? null : f.getPosHeaderDetail().getId());
            posHeaderDetailVO.setNew(posHeaderDetailVO.getId() == null);
            posHeaderDetailVO.setName(f.getName());
            posHeaderDetailVO.setCalculationResult(f.getCalculate());
            posHeaderDetailVO.setResult(f.getResult());
            posHeaderDetailVO.setCount((long) f.getCount());
            posHeaderDetailVO.setPrice(f.getPrice());
            posHeaderDetailVO.setInventoryDetail(f.getInventoryDetail() == null ? null : f.getInventoryDetail().getId());
            posHeaderDetailVO.setCurrencyFrom(f.getCurrencyTo().getId());
            posHeaderDetailVO.setCurrencyTo(currencySelect.getValue().getId());
            posHeaderDetailVO.setConvertedAmount(f.getResult());
            posHeaderDetailVO.setExchangeRate(f.getExchange());
//            posHeaderDetailVO.setProduct(f.getProductOrService() == null ? //
//                    (f.getPosHeaderDetail() == null ? null : (f.getPosHeaderDetail().getProduct() == null ? null : f.getPosHeaderDetail().getProduct().getId())) //
//                    : (f.getProductOrService().getProduct() == null ? null : f.getProductOrService().getProduct().getId()));//
            posHeaderDetailVO.setService(f.getProductOrService() == null ? //
                    (f.getPosHeaderDetail() == null ? null : (f.getPosHeaderDetail().getServices() == null ? null : f.getPosHeaderDetail().getServices().getId())) //
                    : (f.getProductOrService().getServices() == null ? null : f.getProductOrService().getServices().getId()));//
            return posHeaderDetailVO;
        }).toList();
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public void addItem(ProductOrService productOrService) {
        Optional<Item> any = itemList.stream().filter(g -> {
            ProductOrService productOrService1 = g.getProductOrService();
            if (productOrService1 == null) {
                PosHeaderDetail posHeaderDetail = g.getPosHeaderDetail();
                if (posHeaderDetail.getServices() != null && productOrService.getServices() != null) {
                    return posHeaderDetail.getServices().getId().compareTo(productOrService.getServices().getId()) == 0;
                }
            } else {
                if (productOrService1.getServices() != null && productOrService.getServices() != null) {
                    return productOrService1.getServices().getId().compareTo(productOrService.getServices().getId()) == 0;
                }
            }
            return false;
        }).findAny();
        if (any.isPresent()) {
            Item item = any.get();
            item.addCount();
            getDataProvider().refreshAll();
            getVariableLayout(item);
            execute.build(itemList);
//                getFeeLayout(item);
        } else {
            Item item = new Item(productOrService, map, new HashMap<>());
            itemList.add(item);
            getDataProvider().refreshAll();
            getVariableLayout(item);
            execute.build(itemList);
//                getFeeLayout(item);
        }
    }

    public void setTicket(PosHeader posHeader, BigDecimal convertedAmount, BigDecimal exchangeRate, Currency currencyTo) {
        this.posHeader = posHeader;

        activateListener = false;
//        init();
//        productTitle.setText("Ticket #" + posHeader.getHeaderSeq());
        List<Item> collect = posHeader.getPosHeaderDetail().stream().filter(posHeaderDetail -> posHeaderDetail.getServices() != null || posHeaderDetail.getProduct() != null).map(posHeaderDetail -> {
            //            getVariableLayout(item);
            return new Item(posHeaderDetail, map, new HashMap<>());
        }).toList();
        itemList.clear();
        itemList.addAll(collect);
        getDataProvider().refreshAll();

//        List<Fee> collect1 = posHeader.getPosHeaderDetail().stream().filter(posHeaderDetail -> posHeaderDetail.getCalculationComponent() != null && posHeaderDetail.getCalculationComponent().getCategory().compareTo(CalculationComponent.Category.FEE) == 0).map(posHeaderDetail -> new Fee(this, posHeaderDetail, posHeaderDetail.getCalculationComponent())).toList();
//        feeList.clear();
//        feeList.addAll(collect1);
//        feeGrid.getDataProvider().refreshAll();

        BigDecimal reduce = itemList.stream().map(Item::getResult).reduce(BigDecimal.ZERO, BigDecimal::add);
//        reduce = reduce.add(feeList.stream().map(Fee::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));

        BigDecimal obj = total = convertedAmount;
        currencySelect.setValue(currencyTo);
        exchangeRateFld.setValue(exchangeRate);
        discountFld.setValue(this.posHeader.getDiscount());
        obj = obj.subtract(posHeader.getDiscount() == null ? BigDecimal.ZERO : posHeader.getDiscount());
        String text = Constants.CURRENCY_FORMAT.format(obj);
        totalLbl.setText(text);
        activateListener = true;
    }

    public PosHeaderVO getVO() {
        List<PosHeaderDetailVO> vos = new ArrayList<>();
        PosHeaderVO posHeaderVO = new PosHeaderVO();
        posHeaderVO.setId(posHeader == null ? null : posHeader.getId());
        posHeaderVO.setNew(posHeaderVO.getId() == null);
        posHeaderVO.setPosStart(null);
        posHeaderVO.setDetails(vos);


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
//        }).toList();

        vos.addAll(itemListVo());
        if (vos.isEmpty()) {
            throw new ValidationException("Please add at least one (1) item");
        }
//        vos.addAll(collectFees);

        BigDecimal reduce = getItemList().stream().map(Item::getResult).reduce(BigDecimal.ZERO, BigDecimal::add);
//        reduce = reduce.add(feeList.stream().map(Fee::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        posHeaderVO.setPrice(reduce);
        posHeaderVO.setRemoveList(getRemoveList());
        posHeaderVO.setExchangeRate(/*exchangeRateFld.getValue()*/BigDecimal.ONE);
        posHeaderVO.setConvertedAmount(posHeaderVO.getPrice().multiply(posHeaderVO.getExchangeRate()));
        posHeaderVO.setCurrencyFrom(currencySelect.getValue().getId());
        posHeaderVO.setCurrencyTo(currencySelect.getValue().getId());
        posHeaderVO.setDiscount(discountFld.getValue());
        return posHeaderVO;
    }

    public void setExecute(InterExecutable<Object, List<Item>> execute) {
        this.execute = execute;
    }

    public List<Long> getRemoveList() {
        return removeList;
    }

    private InterExecutable<Object, List<Item>> total() {
        return new InterExecutable<Object, List<Item>>() {
            @Override
            public Object build(List<Item> itemList) {
                itemList.stream().forEach(f -> f.setCurrency(currencySelect.getValue()));
                total = itemList.stream().map(Item::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
//        total = total.add(feeList.stream().map(Fee::getCalcPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
                BigDecimal value = null;//exchangeRateFld.getValue();
                if (value == null) {
                    value = BigDecimal.ONE;
                }
                BigDecimal obj = total == null ? BigDecimal.ZERO : total;
                obj = obj.subtract(discountFld.getValue());
                String text = Constants.CURRENCY_FORMAT.format(obj.multiply(value));
                totalLbl.setText(text);
                recalculateColumnWidths();
                return null;
            }
        };
    }

    public void setBusiness(Business business2) {
        this.business2 = business2;
        currencySelect.setValue(business2.getCurrency());
        exchangeRateFld.setValue(BigDecimal.ONE);
    }
}
