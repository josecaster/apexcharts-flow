package sr.we.ui.views.products;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import sr.we.shekelflowcore.entity.Product;

/**
 * A Designer generated component for the product-options template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("product-options")
@JsModule("./src/views/products/product-options.ts")
public class ProductOptions extends LitTemplate {

    /**
     * Creates a new ProductOptions.
     */
    public ProductOptions() {
        // You can initialise any data required for the connected UI components here.
    }

    public void setProduct(Product product) {
    }
}
