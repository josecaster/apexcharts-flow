package sr.we.ui.components.finance;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasHelper;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.component.textfield.NumberField;
import sr.we.shekelflowcore.entity.Loan;

import java.math.BigDecimal;

public class FactorField extends NumberField implements HasLabel, HasHelper {

    private final FactorTypeSelect factorTypeSelect;

    private Integer maxVal;

    public FactorField() {
        factorTypeSelect = new FactorTypeSelect();
        factorTypeSelect.addThemeVariants(SelectVariant.LUMO_SMALL);

        factorTypeSelect.setEmptySelectionAllowed(false);
        factorTypeSelect.setWidth("50%");
        
        setSuffixComponent(factorTypeSelect);

        setLabel(getTranslation("sr.we.factor.type"));
        setHelperText(getTranslation("sr.we.factor.type.info"));

        factorTypeSelect.addValueChangeListener(f -> {
           switch (f.getValue()){
               case P -> {
                   setMin(1);
                   setMax(100);
               }
               case A -> {
                   setMin(1);
                   if(maxVal != null){
                       setMax(maxVal);
                   }
               }
           }
        });
        factorTypeSelect.setValue(Loan.FactorType.P);
//        setPreventInvalidInput(true);
    }

    @Override
    protected void setReadonly(boolean readonly) {
        super.setReadonly(readonly);
        factorTypeSelect.setReadOnly(readonly);
    }

    @Override
    public void setLabel(String label) {
//        HasLabel.super.setLabel(label);
        super.setLabel(getTranslation(label));
    }

    @Override
    public void setHelperText(String helperText) {
//        HasHelper.super.setHelperText(helperText);
        super.setHelperText(getTranslation(helperText));
    }

    public void setRequiredIndicatorVisible(boolean required) {
        setRequiredIndicatorVisible(required);
    }

    public Value getValue1() {
        return new Value(factorTypeSelect.getValue(), getValue());
    }

    public void setValue(FactorField.Value value) {
        factorTypeSelect.setValue(value.getFactorType());
        setValue(value.getFactor().doubleValue());
    }

    public void setWidthFull() {
        setWidthFull();
    }

    public void setWidth(String s) {
        setWidth(s);
    }

    public static class Value {
        private final Loan.FactorType factorType;
        private final BigDecimal factor;

        public Value(Loan.FactorType factorType, Double factor) {
            this.factorType = factorType;
            this.factor = new BigDecimal(factor);
        }

        public Loan.FactorType getFactorType() {
            return factorType;
        }

        public BigDecimal getFactor() {
            return factor;
        }
    }

    public void setMaxVal(int maxVal) {
        this.maxVal = maxVal;
    }
}
