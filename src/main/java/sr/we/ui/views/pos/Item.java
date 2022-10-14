package sr.we.ui.views.pos;

import sr.we.ContextProvider;
import sr.we.data.controller.CalculationService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.adapter.CalculationParam;
import sr.we.shekelflowcore.entity.helper.adapter.CalculationResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Item {
    private final ProductOrService productOrService;
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
    }

    public Item(ProductOrService productOrService, Map<String, Object> map, Map<String, Object> feeMap) {
        this.productOrService = productOrService;
        Items items = productOrService.getServices();
        init(map, feeMap, items);
    }

    private void init(Map<String, Object> map, Map<String, Object> feeMap, Items items) {
        if(count == 0) {
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
                List<CalculationComponent> variables = calculationComponents.stream().filter(f -> f.getType() == null || f.getType().compareTo(CalculationComponent.Type.VARIABLE) == 0).collect(Collectors.toList());
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
        return posHeaderDetail == null ? productOrService.getServices().getName()  : posHeaderDetail.getName();
    }

    public void setName(String name){
        if(posHeaderDetail == null){
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
        return result;
    }

    private BigDecimal price() {
        Items items = productOrService == null ? (posHeaderDetail == null ? null : posHeaderDetail.getServices()) : productOrService.getServices();
        if(posHeaderDetail != null && posHeaderDetail.getPrice() != null){
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
}
