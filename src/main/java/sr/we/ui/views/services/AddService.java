package sr.we.ui.views.services;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.ItemsService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.CalculationComponent;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.Items;
import sr.we.shekelflowcore.entity.helper.InterExecutable;
import sr.we.shekelflowcore.entity.helper.vo.CalculationComponentVO;
import sr.we.shekelflowcore.entity.helper.vo.IProductInventoryVO;
import sr.we.shekelflowcore.entity.helper.vo.ServicesVO;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.ReRouteLayout;
import sr.we.ui.views.products.ProductInventory;

import java.util.*;

import static sr.we.ContextProvider.getBean;

/**
 * A Designer generated component for the add-service template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("add-service")
@JsModule("./src/views/services/add-service.ts")
public class AddService extends LitTemplate {

    @Id("save-btn")
    private Button saveBtn;
    @Id("service-form")
    private ServiceForm serviceForm;

    private String businessString;
    private Business business;
    private Items items;
    private Currency currency;
    @Id("service-price")
    private ServicePrice servicePrice;
    @Id("service-formula")
    private ServiceFormula serviceFormula;
    @Id("service-components")
    private ServiceComponents serviceComponents;
    @Id("main-form-layout")
    private FormLayout mainFormLayout;
    private ServicesVO servicesVO;
    @Id("service-inventory")
    private ProductInventory serviceInventory;
    @Id("service-type")
    private ServiceType serviceType;
    @Id("ser-comp-layout")
    private FormItem serCompLayout;
    @Id("serv-formula-layout")
    private FormItem servFormulaLayout;

    /**
     * Creates a new AddService.
     */
    public AddService() {
        // You can initialise any data required for the connected UI components here.

        mainFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1), new FormLayout.ResponsiveStep("500px", 3));

        saveBtn.addClickListener(f -> {
//            ServicesVO servicesVO = serviceForm.getVO();
//            servicesVO.setId(services == null ? null : services.getId());
//            servicesVO.setNew(services == null);
            servicesVO.setBusiness(business.getId());
//
//            List<CalculationComponentVO> vo = serviceComponents.getVO();
//            servicesVO.setCalculationComponentVO(vo);
            // Refresh formula since there is no valuechangelistener for the ace editor
            String formula = serviceFormula.getFormula();
            servicesVO.setFormula(formula);
//
//            ServicesVO vo1 = servicePrice.getVO();
//            servicesVO.setCost(vo1.getCost());
//            servicesVO.setComparePrice(vo1.getComparePrice());
//            servicesVO.setPrice(vo1.getPrice());
//            servicesVO.setVariablePrice(vo1.getVariablePrice());
//            ServicesVO vo = serviceType.getVO();
//            servicesVO.setChargeTax(vo.getChargeTax());
//            servicesVO.setActive(vo.getActive());
//            servicesVO.setTrackInventory(vo.getTrackInventory());
//            servicesVO.setVariablePrice(vo.getVariablePrice());

            //inventory
            IProductInventoryVO productInventoryVO = serviceInventory.getVO();
            servicesVO.setSku(productInventoryVO.getSku());
            servicesVO.setBarcode(productInventoryVO.getBarcode());
//            servicesVO.setTrackInventory(productInventoryVO.getTrackInventory());
            servicesVO.setProductsInventory(productInventoryVO.getProductsInventory());


            ItemsService productService = ContextProvider.getBean(ItemsService.class);
            if (servicesVO.isNew()) {
                items = productService.create(AuthenticatedUser.token(), servicesVO);
            } else {
                items = productService.edit(AuthenticatedUser.token(), servicesVO);
            }
            List<String> strings = Arrays.asList(items.getId().toString());
            Map<String, List<String>> map = new HashMap<>();
            map.put("id", strings);
            QueryParameters queryParameters = new QueryParameters(map);
            UI.getCurrent().navigate(EditServiceView.getLocation(business.getId().toString()), queryParameters);
        });


    }

    private void setBusiness(BeforeEnterEvent event) {
        Optional<String> business1 = event.getRouteParameters().get("business");
        if (business1.isPresent()) {
            businessString = business1.get();
            BusinessService businessService = ContextProvider.getBean(BusinessService.class);
            business = businessService.get(Long.valueOf(businessString), AuthenticatedUser.token());
            if (business != null && (this.items == null || this.items.getCurrency() == null)) {
                currency = business.getCurrency();
                servicePrice.setCurrency(currency);
            }
        }
    }


    protected void setService(BeforeEnterEvent event) {
        QueryParameters queryParams = event.getLocation().getQueryParameters();
        List<String> id1 = queryParams.getParameters().get("id");
        Optional<String> id = id1.stream().findAny();
        if (id.isEmpty()) {
            event.forwardTo(ReRouteLayout.class);
            throw new ValidationException("Invalid Link");
        }
        String token = AuthenticatedUser.token();
        ItemsService productService = getBean(ItemsService.class);
        items = productService.get(Long.valueOf(id.get()), token);

        if (items == null) {
            event.forwardTo(ReRouteLayout.class);
            throw new ValidationException("No services found");
        }

        setServices(event);
    }

    protected void setServices(BeforeEnterEvent event) {
        servicesVO = new ServicesVO();
        servicesVO.setNew(true);
        List<CalculationComponentVO> items = new ArrayList<>();
        if (this.items != null) {
            servicesVO.setNew(false);
            servicesVO.setId(this.items.getId());
            servicesVO.setCode(this.items.getCode());
            servicesVO.setName(this.items.getName());
            servicesVO.setPrice(this.items.getPrice());
            servicesVO.setFormula(this.items.getFormula());
            servicesVO.setType(this.items.getType());
            servicesVO.setVariablePrice(this.items.getVariablePrice());
            servicesVO.setComparePrice(this.items.getComparePrice());
            servicesVO.setCost(this.items.getCost());
            servicesVO.setActive(this.items.getActive());
            servicesVO.setBusiness(this.items.getBusiness().getId());
            for (CalculationComponent calculationComponent : this.items.getCalculationComponents()) {
                CalculationComponentVO calculationComponentVO = new CalculationComponentVO();
                calculationComponentVO.setNew(false);
                calculationComponentVO.setId(calculationComponent.getId());
                calculationComponentVO.setCode(calculationComponent.getCode());
                calculationComponentVO.setName(calculationComponent.getName());
                calculationComponentVO.setFormula(calculationComponent.getFormula());
                calculationComponentVO.setVisibleCustomer(calculationComponent.getVisibleCustomer());
                calculationComponentVO.setActive(calculationComponent.getActive());
                items.add(calculationComponentVO);
            }

            servicesVO.setSku(this.items.getSku());
            servicesVO.setBarcode(this.items.getBarcode());
            servicesVO.setTrackInventory(this.items.getTrackInventory());
            servicesVO.setProductsInventory(this.items.getProductsInventoriesVO());
            servicesVO.setCategory(this.items.getCategory());
            servicesVO.setCurrency(this.items.getCurrency() == null ? null : this.items.getCurrency().getId());
        }

        servicesVO.setCalculationComponentVO(items);
        serviceType.setVO(servicesVO);
        serviceForm.setVO(servicesVO);
        serviceComponents.setVO(servicesVO);
        serviceFormula.setVO(servicesVO);
        servicePrice.setVO(servicesVO);
        setBusiness(event);


        serviceInventory.setProduct(this.servicesVO);

        serviceType.trackInventroy(serviceInventory.getTrackInventory());
        InterExecutable<?, Boolean> advancedPrice = (f) -> {
            serCompLayout.setVisible(f);
            servFormulaLayout.setVisible(f);
            servicePrice.getAdvancedPrice().build(f);
            return null;
        };
        serviceType.addVancedPricing(advancedPrice);
    }

}
