package sr.we.ui.views.products;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;
import sr.we.shekelflowcore.entity.Product;
import sr.we.shekelflowcore.entity.helper.vo.IProductTitleVO;
import sr.we.shekelflowcore.entity.helper.vo.ProductVO;

/**
 * A Designer generated component for the product-title template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("product-title")
@JsModule("./src/views/products/product-title.ts")
public class ProductTitle extends LitTemplate {

    @Id("title")
    private TextField title;
    @Id("description")
    private TextArea description;

    /**
     * Creates a new ProductTitle.
     */
    public ProductTitle() {
        // You can initialise any data required for the connected UI components here.
    }

    public void setProduct(Product product) {
        title.setValue(StringUtils.isBlank(product.getTitle()) ? "" : product.getTitle());
        description.setValue(StringUtils.isBlank(product.getDescription())? "" : product.getDescription());
    }

    public IProductTitleVO getVO(){
        ProductVO productVO = new ProductVO();
        productVO.setTitle(title.getValue());
        productVO.setDescription(description.getValue());
        return productVO;
    }
}
