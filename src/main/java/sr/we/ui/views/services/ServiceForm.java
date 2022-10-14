package sr.we.ui.views.services;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;
import sr.we.shekelflowcore.entity.Items;
import sr.we.shekelflowcore.entity.helper.vo.ServicesVO;

/**
 * A Designer generated component for the service-form template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("service-form")
@JsModule("./src/views/services/service-form.ts")
public class ServiceForm extends LitTemplate {

    @Id("code-fld")
    private TextField codeFld;
    @Id("title-fld")
    private TextField titleFld;

    /**
     * Creates a new ServiceForm.
     */
    public ServiceForm() {
        // You can initialise any data required for the connected UI components here.
//        validCkh.setValue(true);
//        typeCmb.setItems(Items.Type.ONE_TIME, Items.Type.PERIODICALLY);
//
////        validCkh.addValueChangeListener(f -> {
////            this.servicesVO.setActive(f.getValue());
////        });
//        typeCmb.addValueChangeListener(f -> {
//            this.servicesVO.setType(f.getValue());
//        });
        codeFld.addValueChangeListener(f -> {
            this.servicesVO.setCode(f.getValue());
        });
        titleFld.addValueChangeListener(f -> {
            this.servicesVO.setName(f.getValue());
        });
    }

    private void setServices(ServicesVO services) {
//        validCkh.setValue(services.getActive() != null && services.getActive());
//        typeCmb.setValue(services.getType());
        codeFld.setValue(StringUtils.isBlank(services.getCode()) ? "" : services.getCode());
        titleFld.setValue(StringUtils.isBlank(services.getName()) ? "" : services.getName());
    }

    private ServicesVO servicesVO;

    public void setVO(ServicesVO servicesVO) {
        this.servicesVO = servicesVO;
        setServices(servicesVO);
    }
}
