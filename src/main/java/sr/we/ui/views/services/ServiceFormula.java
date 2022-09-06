package sr.we.ui.views.services;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import de.f0rce.ace.AceEditor;
import de.f0rce.ace.enums.AceMode;
import de.f0rce.ace.enums.AceTheme;
import sr.we.shekelflowcore.entity.helper.vo.ServicesVO;

import java.util.Arrays;

/**
 * A Designer generated component for the service-formula template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("service-formula")
@JsModule("./src/views/services/service-formula.ts")
public class ServiceFormula extends LitTemplate {

    private final MyAceEditor myAceEditor;
    @Id("formula-layout")
    private Div formulaLayout;
    @Id("test-btn")
    private Button testBtn;

    /**
     * Creates a new ServiceFormula.
     */
    public ServiceFormula() {
        // You can initialise any data required for the connected UI components here.
        myAceEditor = new MyAceEditor();
        formulaLayout.add(myAceEditor);


        ServiceFormulaTest serviceFormulaTest = new ServiceFormulaTest();
        testBtn.addClickListener(f -> {
            serviceFormulaTest.test(servicesVO.getId());
        });
    }

    public String getFormula() {
       return myAceEditor.getValue();
    }

    private void setFormula(String formula) {
        myAceEditor.setValue(formula);
    }

    private ServicesVO servicesVO;

    public void setVO(ServicesVO servicesVO) {
        this.servicesVO = servicesVO;
        setFormula(servicesVO.getFormula());
        myAceEditor.setComponets(servicesVO.getCalculationComponentVO());
    }
}
