package sr.we.ui.views.products;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.NumberField;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.Product;
import sr.we.shekelflowcore.entity.helper.vo.IProductPriceVO;
import sr.we.shekelflowcore.entity.helper.vo.ProductVO;

import java.math.BigDecimal;

/**
 * A Designer generated component for the product-price template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("product-price")
@JsModule("./src/views/products/product-price.ts")
public class ProductPrice extends LitTemplate {

    @Id("price")
    private NumberField price;
    @Id("compare-price")
    private NumberField comparePrice;
    @Id("cost-per-item")
    private NumberField costPerItem;

    /**
     * Creates a new ProductPrice.
     */
    public ProductPrice() {
        // You can initialise any data required for the connected UI components here.
    }

    public NumberField getPrice() {
        return price;
    }

    public NumberField getComparePrice() {
        return comparePrice;
    }

    public NumberField getCostPerItem() {
        return costPerItem;
    }

    public void setCurrency(Currency currency) {
        price.setPrefixComponent(new Label(currency.getCode()));
        comparePrice.setPrefixComponent(new Label(currency.getCode()));
        costPerItem.setPrefixComponent(new Label(currency.getCode()));
    }

    public void setProduct(Product product) {
        price.setValue(product.getPrice() == null ? 0d : product.getPrice().doubleValue());
        comparePrice.setValue(product.getComparePrice()== null ? 0d : product.getComparePrice().doubleValue());
        costPerItem.setValue(product.getCost() == null ? 0d : product.getCost().doubleValue());
    }

    public IProductPriceVO getVO(){
        ProductVO productVO = new ProductVO();
        productVO.setPrice(price.getValue() == null ? BigDecimal.ZERO : BigDecimal.valueOf(price.getValue()));
        productVO.setComparePrice(comparePrice.getValue() == null ? BigDecimal.ZERO : BigDecimal.valueOf(comparePrice.getValue()));
        productVO.setCost(costPerItem.getValue() == null ? BigDecimal.ZERO : BigDecimal.valueOf(costPerItem.getValue()));
        return productVO;
    }
}
