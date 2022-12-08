package sr.we.ui.views.products;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import org.apache.commons.lang3.StringUtils;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.InterExecutable;
import sr.we.shekelflowcore.entity.helper.vo.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Designer generated component for the product-inventory template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("product-inventory")
@JsModule("./src/views/products/product-inventory.ts")
public class ProductInventory extends LitTemplate {

    private final Grid<ProductsInventoryVO> grid;
    private DataProvider<ProductsInventoryDetailVO, ?> dataProviderDetail;
    private ProductsInventoryVO productsInventory;
    private List<ProductsInventoryVO> items;
    @Id("inventory-detail-layout")
    private VerticalLayout inventoryDetailLayout;
    @Id("inventory-form-layout")
    private VerticalLayout inventoryFormLayout;
    @Id("inventroy-grid-layout")
    private VerticalLayout inventroyGridLayout;
//    @Id("track-inventory-chk")
//    private Checkbox trackInventoryChk;
    @Id("total-amt-lbl")
    private H5 totalAmtLbl;
    @Id("done-btn")
    private Button doneBtn;
    @Id("quantity-fld")
    private NumberField quantityFld;
    @Id("continue-selling-chk")
    private Checkbox continueSellingChk;
    @Id("detailed-stock-chk")
    private Checkbox detailedStockChk;
    private Executable executable;
    private DataProvider<ProductsInventoryVO, ?> dataProvider;
    private List<ProductsInventoryDetailVO> productsInventoryDetails;
    @Id("sku")
    private TextField sku;
    @Id("barcode")
    private TextField barcode;

    /**
     * Creates a new ProductInventory.
     */
    public ProductInventory() {
        // You can initialise any data required for the connected UI components here.
//        trackInventoryChk.setValue(true);
        inventoryDetailLayout.setVisible(false);
        Grid<ProductsInventoryDetailVO> gridDetail = new Grid<>();
        gridDetail.addComponentColumn(f -> {
            TextField textField = new TextField();
            if (f.getCustomerId() != null) {
                textField.setReadOnly(true);
            }
            textField.addValueChangeListener(g -> {
                f.setUniqueCode(g.getValue());
            });
            if (StringUtils.isNotBlank(f.getUniqueCode())) {
                textField.setValue(f.getUniqueCode());
            }
            return textField;
        }).setHeader("Unique code").setResizable(true);
        gridDetail.addColumn(ProductsInventoryDetailVO::getCustomerId).setHeader("Customer").setResizable(true).setSortable(true);
        inventoryDetailLayout.add(gridDetail);
        grid = new Grid<>();
        inventroyGridLayout.add(grid);
        grid.addColumn(ProductsInventoryVO::getQuantity).setHeader("Quantity").setResizable(true).setSortable(true);
        grid.addColumn(ProductsInventoryVO::getContinueSellingOutStock).setHeader("Continue").setResizable(true).setSortable(true);
        grid.addComponentColumn(f -> {
            if (f.getDetailedStock()) {
                Button detail = new Button("Detail");
                detail.addClickListener(g -> {
                    inventoryDetailLayout.setVisible(true);
                    inventoryFormLayout.setVisible(false);
                    grid.deselectAll();

                    productsInventoryDetails = f.getProductsInventoryDetails();
                    if (productsInventoryDetails == null) {
                        productsInventoryDetails = new ArrayList<>();
                        f.setProductsInventoryDetails(productsInventoryDetails);
                    }
                    gridDetail.setItems(productsInventoryDetails);
                    dataProviderDetail = gridDetail.getDataProvider();
                    int i = f.getQuantity().intValue();
                    if (i > productsInventoryDetails.size()) {
                        i = i - productsInventoryDetails.size();

                        for (int l = 0; l < i; l++) {
                            ProductsInventoryDetailVO productsInventoryDetailVO = new ProductsInventoryDetailVO();
                            productsInventoryDetailVO.setNew(true);
                            productsInventoryDetails.add(productsInventoryDetailVO);
                            productsInventoryDetailVO.setProductsInventoryVO(f);
                        }
                        dataProviderDetail.refreshAll();
                    }
                });
                return detail;

            }
            return new Label("-");
        }).setHeader("Detail").setResizable(true);
        gridDetail.addComponentColumn(d -> {
            if (d.getCustomerId() == null) {
                Button remove = new Button("Remove");
                remove.addClickListener(r -> {
                    d.getProductsInventoryVO().setQuantity(d.getProductsInventoryVO().getQuantity() - 1L);
                    dataProvider.refreshAll();
                    sumation();

                    productsInventoryDetails.remove(d);
                    dataProviderDetail.refreshAll();
                });
                return remove;
            }
            return new Label("-");
        }).setResizable(true);

        quantityFld.addValueChangeListener(d -> {
            if (executable != null) {
                executable.build();
            }
        });
        grid.addSelectionListener(f -> {
            Optional<ProductsInventoryVO> firstSelectedItem = f.getFirstSelectedItem();
            if (firstSelectedItem.isPresent()) {
                productsInventory = firstSelectedItem.get();
                quantityFld.setValue(Double.valueOf(productsInventory.getQuantity()));
                quantityFld.setMin(quantityFld.getValue());
                executable = new Executable() {
                    @Override
                    public Object build() {
                        if (productsInventory.getDetailedStock()) {
                            if (quantityFld.getValue().compareTo(productsInventory.getQuantity().doubleValue()) < 0) {
                                quantityFld.setValue(productsInventory.getQuantity().doubleValue());
                            }
                        }
                        return null;
                    }
                };

                continueSellingChk.setValue(productsInventory.getContinueSellingOutStock());
                detailedStockChk.setValue(productsInventory.getDetailedStock());

                inventoryFormLayout.setVisible(true);
                inventoryDetailLayout.setVisible(false);
            }
        });


        doneBtn.addClickListener(f -> {
            if (productsInventory == null) {
                productsInventory = new ProductsInventoryVO();
                productsInventory.setNew(true);
            }
            productsInventory.setQuantity(quantityFld.getValue().longValue());
            productsInventory.setContinueSellingOutStock(continueSellingChk.getValue());
            productsInventory.setDetailedStock(detailedStockChk.getValue());

            refresh(true);
            inventoryFormLayout.setVisible(false);
            inventoryDetailLayout.setVisible(false);


        });

        trackInventory = (f) -> {
            trackInventory(f);
            return null;
        };
    }

    private InterExecutable<?,Boolean> trackInventory;

    public InterExecutable<?, Boolean> getTrackInventory() {
        return trackInventory;
    }

    private void refresh(boolean check) {
        if (items == null) {
            items = new ArrayList<>();
        }
        grid.setItems(items);
        dataProvider = grid.getDataProvider();
        if (check) {
            if (productsInventory.getId() == null) {
                items.add(productsInventory);
                dataProvider.refreshAll();
            } else {
                dataProvider.refreshItem(productsInventory);
            }
        } else {
            dataProvider.refreshAll();
        }
        sumation();
    }

    private void sumation() {
        BigDecimal reduce = items.stream().map(g -> BigDecimal.valueOf(g.getQuantity())).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalAmtLbl.setText("Total amount of items (" + reduce.longValue() + ")");
    }

    private void trackInventory(boolean val) {
        totalAmtLbl.setVisible(val);
        inventroyGridLayout.setVisible(val);
        inventoryFormLayout.setVisible(val);
    }

    public void setProduct(ServicesVO product) {
//        trackInventoryChk.setValue(product.getTrackInventory() != null && product.getTrackInventory());
        if (StringUtils.isNotBlank(product.getSku())) {
            sku.setValue(product.getSku());
        }
        if (StringUtils.isNotBlank(product.getBarcode())) {
            barcode.setValue(product.getBarcode());
        }

        items = product.getProductsInventory();

        inventoryFormLayout.setVisible(items == null || items.isEmpty());

        refresh(false);
    }

    public IProductInventoryVO getVO() {
        ProductVO productVO = new ProductVO();
//        productVO.setTrackInventory(trackInventoryChk.getValue());
        productVO.setSku(sku.getValue());
        productVO.setBarcode(barcode.getValue());
        productVO.setProductsInventory(items);
        return productVO;
    }
}
