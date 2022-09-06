package sr.we.ui.views.products;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.template.Id;
import sr.we.shekelflowcore.entity.Product;
import sr.we.shekelflowcore.entity.helper.vo.IProductTypeVO;
import sr.we.shekelflowcore.entity.helper.vo.ProductVO;

/**
 * A Designer generated component for the product-type template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("product-type")
@JsModule("./src/views/products/product-type.ts")
public class ProductType extends LitTemplate {

    @Id("category-group")
    private RadioButtonGroup<Product.Type> categoryGroup;
    @Id("status-cmb")
    private ComboBox<Product.Status> statusCmb;

    /**
     * Creates a new ProductType.
     */
    public ProductType() {
        // You can initialise any data required for the connected UI components here.
        categoryGroup.removeAll();
        categoryGroup.setItems(Product.Type.Physical, Product.Type.Digital);
        statusCmb.clear();
        statusCmb.setItems(Product.Status.Active, Product.Status.Draft, Product.Status.Archived);

        categoryGroup.setValue(Product.Type.Physical);
        statusCmb.setValue(Product.Status.Draft);
    }

    public void setProduct(Product product) {
        categoryGroup.setValue(product.getType());
        statusCmb.setValue(product.getStatus());
    }

    public IProductTypeVO getVO() {
        ProductVO productVO = new ProductVO();
        productVO.setType(categoryGroup.getValue());
        productVO.setStatus(statusCmb.getValue());
        return productVO;
    }
}
