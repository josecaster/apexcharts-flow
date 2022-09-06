package sr.we.ui.views.products;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.ProductService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.Product;
import sr.we.shekelflowcore.entity.helper.vo.*;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.ReRouteLayout;

import java.util.*;

import static sr.we.ContextProvider.getBean;

/**
 * A Designer generated component for the add-products template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("add-products")
@JsModule("./src/views/products/add-products.ts")
public class AddProducts extends LitTemplate {


    @Id("back-button")
    private Button backButton;

    private String businessString;
    private Business business;
    @Id("product-price-layout")
    private ProductPrice productPriceLayout;
    @Id("product-title-layout")
    private ProductTitle productTitleLayout;
    @Id("product-type")
    private ProductType productType;
    @Id("product-inventory-layout")
    private ProductInventory productInventoryLayout;

    @Id("form-layout")
    private FormLayout formLayout;
    private Product product;
    @Id("save-btn")
    private Button saveBtn;
    @Id("add-product-title")
    private H2 addProductTitle;


    /**
     * Creates a new AddProducts.
     */
    public AddProducts() {
        // You can initialise any data required for the connected UI components here.

        backButton.addClickListener(f -> {
            UI.getCurrent().navigate(ProductView.class, new RouteParameters(new RouteParam("business", businessString)));
        });
        backButton.setIcon(new LineAwesomeIcon("la la-arrow-left"));
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_ICON);


        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1), new FormLayout.ResponsiveStep("500px",3));

        saveBtn.addClickListener(f -> {
            ProductVO productVO = new ProductVO();
            productVO.setId(product == null ? null : product.getId());
            productVO.setNew(product == null ? true : false);
            productVO.setBusiness(business.getId());

            // price
            IProductPriceVO productPriceVO = productPriceLayout.getVO();
            productVO.setChargeTax(productPriceVO.getChargeTax());
            productVO.setCost(productPriceVO.getCost());
            productVO.setPrice(productPriceVO.getPrice());
            productVO.setComparePrice(productPriceVO.getComparePrice());

            // title
            IProductTitleVO productTitleVO = productTitleLayout.getVO();
            productVO.setTitle(productTitleVO.getTitle());
            productVO.setDescription(productTitleVO.getDescription());

            // type
            IProductTypeVO productTypeVO = productType.getVO();
            productVO.setType(productTypeVO.getType());
            productVO.setStatus(productTypeVO.getStatus());

            //inventory
            IProductInventoryVO productInventoryVO = productInventoryLayout.getVO();
            productVO.setSku(productInventoryVO.getSku());
            productVO.setBarcode(productInventoryVO.getBarcode());
            productVO.setTrackInventory(productInventoryVO.getTrackInventory());
            productVO.setProductsInventory(productInventoryVO.getProductsInventory());

            ProductService productService = ContextProvider.getBean(ProductService.class);
            if(productVO.isNew()){
                product = productService.create(AuthenticatedUser.token(), productVO);
            } else {
                product = productService.edit(AuthenticatedUser.token(), productVO);
            }
            List<String> strings = Arrays.asList(product.getId().toString());
            Map<String, List<String>> map = new HashMap<>();
            map.put("id", strings);
            QueryParameters queryParameters = new QueryParameters(map);
            UI.getCurrent().navigate(EditProductsView.getLocation(business.getId().toString()),queryParameters);
        });
    }


    public void setBusiness(BeforeEnterEvent event) {
        Optional<String> business1 = event.getRouteParameters().get("business");
        if (business1.isPresent()) {
            businessString = business1.get();
            BusinessService businessService = ContextProvider.getBean(BusinessService.class);
            business = businessService.get(Long.valueOf(businessString), AuthenticatedUser.token());
            if(business != null){
                Currency currency = business.getCurrency();
                productPriceLayout.setCurrency(currency);
            }
        }
    }

    public void setProduct(BeforeEnterEvent event) {
        QueryParameters queryParams = event.getLocation().getQueryParameters();
        List<String> id1 = queryParams.getParameters().get("id");
        Optional<String> id = id1.stream().findAny();
        if (id.isEmpty()) {
            event.forwardTo(ReRouteLayout.class);
            throw new ValidationException("Invalid Link");
        }
        String token = AuthenticatedUser.token();
        ProductService productService = getBean(ProductService.class);
        product = productService.get(Long.valueOf(id.get()), token);

        if (product == null) {
            event.forwardTo(ReRouteLayout.class);
            throw new ValidationException("No product found");
        }

        setProduct();
    }

    private void setProduct() {
        productTitleLayout.setProduct(product);
        productPriceLayout.setProduct(product);
        productType.setProduct(product);
        productInventoryLayout.setProduct(product);
        addProductTitle.setText("Edit product");
    }

}
