package sr.we.ui.views.pos;

import sr.we.ContextProvider;
import sr.we.data.controller.CalculationService;
import sr.we.data.controller.ExchangeRateService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.adapter.CalculationParam;
import sr.we.shekelflowcore.entity.helper.adapter.CalculationResult;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Item {

    private final ProductOrService productOrService;
    private Boolean valid;
    private Map<String, Object> map;
    private Map<String, Object> feeMap;
    private Map<String, CalculationComponent> descMap;
    private Map<String, CalculationComponent> feeDescMap;
    private int count;
    private ProductsInventoryDetail inventoryDetail;
    private CalculationResult calculate;
    private BigDecimal result, itemCount;
    private BigDecimal price;
    private PosHeaderDetail posHeaderDetail;
    private BigDecimal exchange;
    private Currency currencyTo;
    private boolean recalcRate = true;
    private LocalDate localDate;

    public Item(PosHeaderDetail posHeaderDetail, Map<String, Object> map, Map<String, Object> feeMap) {
        addCount();
        this.map = map;
        this.posHeaderDetail = posHeaderDetail;
        this.descMap = new HashMap<>();
        this.productOrService = new ProductOrService(posHeaderDetail.getServices());
        count = this.posHeaderDetail.getCount().intValue();
        this.feeMap = feeMap;
        this.feeDescMap = new HashMap<>();


        Items items = posHeaderDetail.getServices();

        init(map, feeMap, items);
        price = this.posHeaderDetail.getPrice();
        result = this.posHeaderDetail.getResult();
        exchange = this.posHeaderDetail.getExchangeRate();
        currencyTo = this.posHeaderDetail.getCurrencyTo();
    }

    public Item(ProductOrService productOrService, Map<String, Object> map, Map<String, Object> feeMap) {
        this.productOrService = productOrService;
        Items items = productOrService.getServices();
        setCurrency(items.getBusiness().getCurrency());
        init(map, feeMap, items);
    }

    private void init(Map<String, Object> map, Map<String, Object> feeMap, Items items) {
        if (count == 0) {
            addCount();
        }
        this.map = map;
        this.descMap = new HashMap<>();

        this.feeMap = feeMap;
        this.feeDescMap = new HashMap<>();

        if (items != null) {
            Boolean advancedPricing = items.getVariablePrice();
            if (!(advancedPricing == null || !advancedPricing)) {
                Set<CalculationComponent> calculationComponents = items.getCalculationComponents();
                List<CalculationComponent> variables = calculationComponents.stream().filter(f -> f.getType() == null //
                        || f.getType().compareTo(CalculationComponent.Type.VALUE_FORMULA) == 0 //
                        || f.getType().compareTo(CalculationComponent.Type.VALUE) == 0).toList();//
//                List<CalculationComponent> categories = calculationComponents.stream().filter(f -> f.getCategory().compareTo(CalculationComponent.Category.FEE) == 0).collect(Collectors.toList());

                variables.forEach(f -> {
                    boolean b = map.containsKey(f.getCode());
                    descMap.put(f.getCode(), f);
                    if (!b) {
                        map.put(f.getCode(), null);
                    }
                });

//                categories.forEach(f -> {
//                    boolean b = feeMap.containsKey(f.getCode());
//                    feeDescMap.put(f.getCode(), f);
//                    if (!b) {
//                        feeMap.put(f.getCode(), null);
//                    }
//                });
            }
        }
    }

    public void addCount() {
        count++;
    }

    public String getName() {
        return posHeaderDetail == null ? productOrService.getServices().getName() : posHeaderDetail.getName();
    }

    public void setName(String name) {
        if (posHeaderDetail == null) {
            productOrService.getServices().setName(name);
        } else {
            posHeaderDetail.setName(name);
        }
    }

    public BigDecimal getResult() {
        return result;
    }

    public BigDecimal getItemCount() {
        return itemCount;
    }

    public CalculationResult getCalculate() {
        return calculate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getCalcPrice() {
        itemCount = BigDecimal.valueOf(this.count);
        price = price();
        result = price.multiply(itemCount);

        if (currencyTo != null) {
            if (currencyTo.getId().compareTo(productOrService.getServices().getCurrency().getId()) != 0) {
                try {
                    if (recalcRate || exchange == null) {
                        ExchangeRateService exchangeRateService = ContextProvider.getBean(ExchangeRateService.class);
                        exchange = exchangeRateService.exchange(currencyTo.getCode(), productOrService.getServices().getCurrency().getCode(), productOrService.getServices().getBusiness().getId(), localDate, AuthenticatedUser.token());
                    }
                    if (exchange == null) {
                        exchange = BigDecimal.ONE;
                    }
                    result = result.multiply(exchange);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    private BigDecimal price() {
        if(overridePrice && price != null){
            return price;
        }
        Items items = productOrService == null ? (posHeaderDetail == null ? null : posHeaderDetail.getServices()) : productOrService.getServices();
        if (posHeaderDetail != null && posHeaderDetail.getPrice() != null) {
            return posHeaderDetail.getPrice();
        }
        if (items != null) {
            Boolean advancedPricing = items.getVariablePrice();
            if (advancedPricing == null || !advancedPricing) {
                return (items.getPrice() == null ? BigDecimal.ZERO : items.getPrice());
            } else {
                CalculationService calculationService = ContextProvider.getBean(CalculationService.class);
                CalculationParam vo = new CalculationParam();
                vo.setServiceId(items.getId());
                vo.setMap(map);
                calculate = calculationService.calculate(AuthenticatedUser.token(), vo);
                return (calculate.getResult() == null ? BigDecimal.ZERO : calculate.getResult());
            }
        }
        return BigDecimal.ZERO;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int intValue) {
        count = intValue;
    }

    public ProductOrService getProductOrService() {
        return productOrService;
    }

    public ProductsInventoryDetail getInventoryDetail() {
        return inventoryDetail;
    }

    public void setInventoryDetail(ProductsInventoryDetail inventoryDetail) {
        this.inventoryDetail = inventoryDetail;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public Map<String, Object> getFeeMap() {
        return feeMap;
    }

    public Map<String, CalculationComponent> getDescMap() {
        return descMap;
    }

    public Map<String, CalculationComponent> getFeeDescMap() {
        return feeDescMap;
    }

    public PosHeaderDetail getPosHeaderDetail() {
        return posHeaderDetail;
    }

    public void setCurrency(Currency currencyTo) {
        this.currencyTo = currencyTo;
    }

    public Currency getCurrencyTo() {
        return currencyTo;
    }

    public BigDecimal getExchange() {
        return exchange;
    }

    public void setExchange(BigDecimal value) {
        exchange = value;
        recalcRate = false;
    }

    public Boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }
private boolean overridePrice = false;
    public void setPrice(BigDecimal price) {
        this.price = price;
        overridePrice = true;
    }
}
