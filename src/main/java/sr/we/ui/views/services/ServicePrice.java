package sr.we.ui.views.services;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.NumberField;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.InterExecutable;
import sr.we.shekelflowcore.entity.helper.vo.ServicesVO;

import java.math.BigDecimal;

/**
 * A Designer generated component for the service-price template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("service-price")
@JsModule("./src/views/services/service-price.ts")
public class ServicePrice extends LitTemplate {

//    @Id("price-variable-chk")
//    private Checkbox priceVariableChk;
    @Id("price")
    private NumberField price;
    @Id("compare-price")
    private NumberField comparePrice;
    @Id("cost-per-item")
    private NumberField costPerItem;
    private InterExecutable<?, Boolean> advancedPrice;

    /**
     * Creates a new ServicePrice.
     */
    public ServicePrice() {
        // You can initialise any data required for the connected UI components here.
        advancedPrice = (f) -> {
            Boolean value = f;
            price.setReadOnly(value);
            comparePrice.setReadOnly(value);
            costPerItem.setReadOnly(value);
            this.servicesVO.setVariablePrice(value);
            if(value) {
                price.setValue(0d);
                comparePrice.setValue(0d);
                costPerItem.setValue(0d);
            }
            return null;
        };
        price.addValueChangeListener(f -> {
            this.servicesVO.setPrice(f.getValue() == null ? null : BigDecimal.valueOf(f.getValue()));
        });
        comparePrice.addValueChangeListener(f -> {
            this.servicesVO.setComparePrice(f.getValue() == null ? null : BigDecimal.valueOf(f.getValue()));
        });
        costPerItem.addValueChangeListener(f -> {
            this.servicesVO.setCost(f.getValue() == null ? null : BigDecimal.valueOf(f.getValue()));
        });
    }

    public void setCurrency(Currency currency) {
        price.setPrefixComponent(new Label(currency.getCode()));
        comparePrice.setPrefixComponent(new Label(currency.getCode()));
        costPerItem.setPrefixComponent(new Label(currency.getCode()));
    }

    private void setServices(ServicesVO services) {
//        priceVariableChk.setValue(services.getVariablePrice() != null && services.getVariablePrice());
        price.setValue(services.getPrice() == null ? null : services.getPrice().doubleValue());
        comparePrice.setValue(services.getComparePrice() == null ? null : services.getComparePrice().doubleValue());
        costPerItem.setValue(services.getCost() == null ? null : services.getCost().doubleValue());
    }

    public InterExecutable<?,Boolean> getAdvancedPrice() {
        return advancedPrice;
    }

    private ServicesVO servicesVO;
    public void setVO(ServicesVO servicesVO) {
        this.servicesVO = servicesVO;
        setServices(servicesVO);
    }
}
