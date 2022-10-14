package sr.we.ui.views.pos;

import sr.we.shekelflowcore.entity.Items;
import sr.we.shekelflowcore.entity.Product;
import sr.we.shekelflowcore.entity.ProductsInventory;
import sr.we.shekelflowcore.entity.ProductsInventoryDetail;

import java.util.Optional;
import java.util.Set;

public class ProductOrService {

//    private Product product;
    private Items items;

//    public ProductOrService(Product product) {
//        this.product = product;
//    }

    public ProductOrService(Items items) {
        this.items = items;
    }

//    public Product getProduct() {
//        return product;
//    }

//    public void setProduct(Product product) {
//        this.product = product;
//    }

    public Items getServices() {
        return items;
    }

    public void setServices(Items items) {
        this.items = items;
    }

    public Set<ProductsInventoryDetail> getDetailedInventory(){
        if (items != null) {
            Set<ProductsInventory> productsInventories = items.getProductsInventories();
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
