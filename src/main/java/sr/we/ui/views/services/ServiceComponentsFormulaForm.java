package sr.we.ui.views.services;

import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import sr.we.shekelflowcore.entity.helper.Build;
import sr.we.shekelflowcore.entity.helper.vo.CalculationComponentVO;

import java.util.List;

/**
 * A Designer generated component for the service-components-formula-form template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("service-components-formula-form")
@JsModule("./src/views/services/service-components-formula-form.ts")
public class ServiceComponentsFormulaForm extends LitTemplate {

    private final MyAceEditor myAceEditor;
    @Id("discard-formula-btn")
    private Button discardBtn;
    @Id("add-formula")
    private Button addBtn;

    boolean edit = false;
    @Id("form-formula-layout")
    private Div formFormulaLayout;
    @Id("formula-code-fld")
    private TextField formulaCodeFld;
    @Id("formula-name-fld")
    private TextField formulaNameFld;
    @Id("formula-visible-customers-chk")
    private Checkbox formulaVisibleCustomersChk;
    @Id("formula-active-chk")
    private Checkbox formulaActiveChk;

    private Build onSave;
    private CalculationComponentVO calculationComponentVO;
    @Id("formula-test-btn")
    private Button formulaTestBtn;

    /**
     * Creates a new ServiceComponentsFormulaForm.
     */
    public ServiceComponentsFormulaForm() {
        // You can initialise any data required for the connected UI components here.

        discardBtn.addClickListener(f -> {
            setVisible(false);
            Animated.animate(this, edit ? Animated.Animation.FADE_OUT_UP : Animated.Animation.FADE_OUT_RIGHT, Animated.Modifier.FASTER);
        });

        addBtn.addClickListener(f -> {
            if (onSave != null) {
                onSave.build();
            }
            // first add
            setVisible(false);
            Animated.animate(this, edit ? Animated.Animation.FADE_OUT_UP : Animated.Animation.FADE_OUT_RIGHT, Animated.Modifier.FASTER);
        });

        myAceEditor = new MyAceEditor();
        formFormulaLayout.add(myAceEditor);

        formulaTestBtn.addClickListener(f -> {
//            ServiceFormulaTest serviceFormulaTest = new ServiceFormulaTest();
//            serviceFormulaTest.test();
            Notification.show("Not yet implemented");
        });
    }

    public CalculationComponentVO getVO() {
        if (calculationComponentVO == null) {
            calculationComponentVO = new CalculationComponentVO();
            calculationComponentVO.setNew(true);
        }
        calculationComponentVO.setCode(formulaCodeFld.getValue());
        calculationComponentVO.setName(formulaNameFld.getValue());
        calculationComponentVO.setFormula(myAceEditor.getValue());
        calculationComponentVO.setVisibleCustomer(formulaVisibleCustomersChk.getValue());
        calculationComponentVO.setActive(formulaActiveChk.getValue());
        return calculationComponentVO;
    }

    public void setVO(CalculationComponentVO vo) {
        this.calculationComponentVO = vo;
        if(vo != null) {
            edit = true;
            formulaCodeFld.setValue(vo.getCode());
            formulaNameFld.setValue(vo.getName());
            myAceEditor.setValue(vo.getFormula());
            formulaVisibleCustomersChk.setValue(vo.getVisibleCustomer());
            formulaActiveChk.setValue(vo.getActive() == null ? false : vo.getActive());
        } else {
            edit = false;
            formulaCodeFld.clear();
            formulaNameFld.clear();
            myAceEditor.clear();
            formulaVisibleCustomersChk.clear();
            formulaActiveChk.clear();
        }
    }

    public void setOnSave(Build onSave) {
        this.onSave = onSave;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setComponets(List<CalculationComponentVO> calculationComponentVO) {
        myAceEditor.setComponets(calculationComponentVO);
    }
}
