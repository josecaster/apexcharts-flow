package sr.we.ui.views.services;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.dom.Element;
import sr.we.shekelflowcore.entity.Items;
import sr.we.shekelflowcore.entity.helper.InterExecutable;
import sr.we.shekelflowcore.entity.helper.vo.ServicesVO;

import java.util.List;

/**
 * A Designer generated component for the service-type template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("service-type")
@JsModule("./src/views/services/service-type.ts")
public class ServiceType extends LitTemplate {

    @Id("service-track-inventory-chk")
    private Checkbox serviceTrackInventoryChk;
    @Id("service-track-advanced-pricing-chk")
    private Checkbox serviceTrackAdvancedPricingChk;
    @Id("service-track-tax-chk")
    private Checkbox serviceTrackTaxChk;
    @Id("service-track-active-chk")
    private Checkbox serviceTrackActiveChk;
    private ServicesVO servicesVO;
    @Id("category-group")
    private RadioButtonGroup<Items.Category> categoryGroup;

    /**
     * Creates a new ServiceType.
     */
    public ServiceType() {
        // You can initialise any data required for the connected UI components here.
        serviceTrackTaxChk.addValueChangeListener(f -> {
            servicesVO.setChargeTax(serviceTrackTaxChk.getValue());
        });
        serviceTrackActiveChk.addValueChangeListener(f -> {
            servicesVO.setActive(serviceTrackActiveChk.getValue());
        });
        serviceTrackInventoryChk.addValueChangeListener(f -> {
            servicesVO.setTrackInventory(serviceTrackInventoryChk.getValue());
        });
        serviceTrackAdvancedPricingChk.addValueChangeListener(f -> {
            servicesVO.setVariablePrice(serviceTrackAdvancedPricingChk.getValue());
        });

        categoryGroup.setItems(List.of(Items.Category.values()));
    }



    public ServicesVO getVO(){
        servicesVO.setChargeTax(serviceTrackTaxChk.getValue());
        servicesVO.setActive(serviceTrackActiveChk.getValue());
        servicesVO.setTrackInventory(serviceTrackInventoryChk.getValue());
        servicesVO.setVariablePrice(serviceTrackAdvancedPricingChk.getValue());
        return servicesVO;
    }

    public void setVO(ServicesVO servicesVO){
        this.servicesVO = servicesVO;
        serviceTrackTaxChk.setValue(servicesVO.getChargeTax() != null && servicesVO.getChargeTax());
        serviceTrackActiveChk.setValue(servicesVO.getActive() != null && servicesVO.getActive());
        serviceTrackInventoryChk.setValue(servicesVO.getTrackInventory() != null && servicesVO.getTrackInventory());
        serviceTrackAdvancedPricingChk.setValue(servicesVO.getVariablePrice() != null && servicesVO.getVariablePrice());
    }

    public void trackInventroy(InterExecutable<?, Boolean> trackInventory) {
        serviceTrackInventoryChk.addValueChangeListener(f -> {
            trackInventory.build(f.getValue());
        });
        trackInventory.build(serviceTrackInventoryChk.getValue());
    }

    public void addVancedPricing(InterExecutable<?, Boolean> advancedPrice) {
        serviceTrackAdvancedPricingChk.addValueChangeListener(f -> {
            advancedPrice.build(f.getValue());
        });
        advancedPrice.build(serviceTrackAdvancedPricingChk.getValue());
    }
}
