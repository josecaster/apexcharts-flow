package sr.we.ui.views.services;

import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;
import sr.we.shekelflowcore.entity.CalculationComponent;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.vo.CalculationComponentVO;
import sr.we.shekelflowcore.settings.util.NumberUtil;
import sr.we.ui.components.NotYetClick;

import java.math.BigDecimal;
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
    private final BigDecimalField valueFld;
    boolean edit = false;
    @Id("discard-formula-btn")
    private Button discardBtn;
    @Id("add-formula")
    private Button addBtn;
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

    private Executable onSave;
    private CalculationComponentVO calculationComponentVO;
    @Id("formula-test-btn")
    private Button formulaTestBtn;
    @Id("formula-category-cmb")
    private Select<CalculationComponent.Category> formulaCategoryCmb;
    @Id("formula-type-cmb")
    private Select<CalculationComponent.Type> formulaTypeCmb;

    /**
     * Creates a new ServiceComponentsFormulaForm.
     */
    public ServiceComponentsFormulaForm() {
        // You can initialise any data required for the connected UI components here.


        formulaCategoryCmb.setItems(List.of(CalculationComponent.Category.values()));
        formulaTypeCmb.setItems(List.of(CalculationComponent.Type.values()));

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
        valueFld = new BigDecimalField();

        formFormulaLayout.add(myAceEditor, valueFld);

        myAceEditor.setVisible(false);
        valueFld.setVisible(false);


        formulaTypeCmb.addValueChangeListener(f -> {
            if (f.getValue() == null) {
                formulaVisible(true);
                return;
            }
            switch (f.getValue()) {
                case FORMULA, VALUE_FORMULA -> formulaVisible(true);
                case VALUE -> formulaVisible(false);
            }
        });

        formulaTestBtn.addClickListener(new NotYetClick<>());
    }

    private void formulaVisible(boolean b) {
        myAceEditor.setVisible(b);
        valueFld.setVisible(!b);
    }

    public CalculationComponentVO getVO() {
        if (calculationComponentVO == null) {
            calculationComponentVO = new CalculationComponentVO();
            calculationComponentVO.setNew(true);
        }
        calculationComponentVO.setCode(formulaCodeFld.getValue());
        calculationComponentVO.setName(formulaNameFld.getValue());
        calculationComponentVO.setType(formulaTypeCmb.getValue());
        calculationComponentVO.setCategory(formulaCategoryCmb.getValue());
        calculationComponentVO.setFormula(myAceEditor.isVisible() ? myAceEditor.getValue() : (valueFld.getValue() == null ? null : valueFld.getValue().toString()));
        calculationComponentVO.setVisibleCustomer(formulaVisibleCustomersChk.getValue());
        calculationComponentVO.setActive(formulaActiveChk.getValue());
        return calculationComponentVO;
    }

    public void setVO(CalculationComponentVO vo) {
        this.calculationComponentVO = vo;
        if (vo != null) {
            edit = true;
            formulaCodeFld.setValue(vo.getCode());
            formulaNameFld.setValue(vo.getName());
            if (vo.getType() == null || vo.getType().compareTo(CalculationComponent.Type.FORMULA) == 0 || vo.getType().compareTo(CalculationComponent.Type.VALUE_FORMULA) == 0) {
                myAceEditor.setValue(vo.getFormula());
            } else {
                if (StringUtils.isNotBlank(vo.getFormula()) && NumberUtil.isNumeric(vo.getFormula())) {
                    valueFld.setValue(BigDecimal.valueOf(Double.parseDouble(vo.getFormula())));
                }
            }
            formulaCategoryCmb.setValue(vo.getCategory());
            formulaTypeCmb.setValue(vo.getType());
            formulaVisibleCustomersChk.setValue(vo.getVisibleCustomer());
            formulaActiveChk.setValue(vo.getActive() != null && vo.getActive());
        } else {
            edit = false;
            formulaCodeFld.clear();
            formulaNameFld.clear();
            myAceEditor.clear();
            formulaVisibleCustomersChk.clear();
            formulaActiveChk.clear();
            formulaCategoryCmb.clear();
            formulaTypeCmb.clear();
            valueFld.clear();
        }
    }

    public void setOnSave(Executable onSave) {
        this.onSave = onSave;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setComponents(List<CalculationComponentVO> calculationComponentVO) {
        myAceEditor.setComponets(calculationComponentVO);
    }
}
