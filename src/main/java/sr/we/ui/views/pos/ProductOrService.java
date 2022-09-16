package sr.we.ui.views.pos;

import sr.we.shekelflowcore.entity.Product;
import sr.we.shekelflowcore.entity.ProductsInventory;
import sr.we.shekelflowcore.entity.ProductsInventoryDetail;
import sr.we.shekelflowcore.entity.Services;

import java.util.Optional;
import java.util.Set;

public class ProductOrService {

    private Product product;
    private Services services;

    public ProductOrService(Product product) {
        this.product = product;
    }

    public ProductOrService(Services services) {
        this.services = services;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Services getServices() {
        return services;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    public Set<ProductsInventoryDetail> getDetailedInventory(){
        if (product != null) {
            Set<ProductsInventory> productsInventories = product.getProductsInventories();
            if (productsInventories != null && !productsInventories.isEmpty()) {
                Optional<ProductsInventory> any1 = productsInventories.stream().filter(g -> g.getDetailedStock() != null && g.getDetailedStock()).findAny();
                if (any1.isPresent()) {
                    ProductsInventory productsInventory = any1.get();
                    return productsInventory.getProductsInventoriesDetails();
                }
            }
        }
        return null;
    }

    public boolean hasDetailedInventory(){
        Set<ProductsInventoryDetail> detailedInventory = getDetailedInventory();
        return detailedInventory != null && !detailedInventory.isEmpty();
    }
}
