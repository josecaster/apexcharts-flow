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
        this.productOrService = (posHeaderDetail.getProduct() == null ? new ProductOrService(posHeaderDetail.getServices()) : new ProductOrService(posHeaderDetail.getProduct()));

        this.feeMap = feeMap;
        this.feeDescMap = new HashMap<>();


        Services services = posHeaderDetail.getServices();

        init(map, feeMap, services);
        price = this.posHeaderDetail.getPrice();
        result = this.posHeaderDetail.getResult();
    }

    public Item(ProductOrService productOrService, Map<String, Object> map, Map<String, Object> feeMap) {
        this.productOrService = productOrService;
        Services services = productOrService.getServices();
        init(map, feeMap, services);
    }

    private void init(Map<String, Object> map, Map<String, Object> feeMap, Services services) {
        addCount();
        this.map = map;
        this.descMap = new HashMap<>();

        this.feeMap = feeMap;
        this.feeDescMap = new HashMap<>();

        if (services != null) {
            Boolean advancedPricing = services.getVariablePrice();
            if (!(advancedPricing == null || !advancedPricing)) {
                Set<CalculationComponent> calculationComponents = services.getCalculationComponents();
                List<CalculationComponent> variables = calculationComponents.stream().filter(f -> f.getType().compareTo(CalculationComponent.Type.VARIABLE) == 0).collect(Collectors.toList());
                List<CalculationComponent> categories = calculationComponents.stream().filter(f -> f.getCategory().compareTo(CalculationComponent.Category.FEE) == 0).collect(Collectors.toList());

                variables.forEach(f -> {
                    boolean b = map.containsKey(f.getCode());
                    descMap.put(f.getCode(), f);
                    if (!b) {
                        map.put(f.getCode(), null);
                    }
                });

                categories.forEach(f -> {
                    boolean b = feeMap.containsKey(f.getCode());
                    feeDescMap.put(f.getCode(), f);
                    if (!b) {
                        feeMap.put(f.getCode(), null);
                    }
                });
            }
        }
    }

    void addCount() {
        count++;
    }

    public String getName() {
        return posHeaderDetail == null ? (productOrService.getProduct() == null ? productOrService.getServices().getName() : productOrService.getProduct().getTitle()) : posHeaderDetail.getName();
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

    BigDecimal getCalcPrice() {
        itemCount = BigDecimal.valueOf(this.count);
        price = price();
        result = price.multiply(itemCount);
        return result;
    }

    private BigDecimal price() {
        Product product = productOrService == null ? (posHeaderDetail == null ? null : posHeaderDetail.getProduct()) : productOrService.getProduct();
        Services services = productOrService == null ? (posHeaderDetail == null ? null : posHeaderDetail.getServices()) : productOrService.getServices();
        if (product != null) {
            return (product.getPrice() == null ? BigDecimal.ZERO : product.getPrice());
        } else if (services != null) {
            Boolean advancedPricing = services.getVariablePrice();
            if (advancedPricing == null || !advancedPricing) {
                return (services.getPrice() == null ? BigDecimal.ZERO : services.getPrice());
            } else {
                CalculationService calculationService = ContextProvider.getBean(CalculationService.class);
                CalculationParam vo = new CalculationParam();
                vo.setServiceId(services.getId());
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
