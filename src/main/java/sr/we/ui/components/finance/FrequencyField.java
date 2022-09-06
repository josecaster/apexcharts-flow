package sr.we.ui.components.finance;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.textfield.NumberField;
import sr.we.shekelflowcore.entity.Loan;

import java.math.BigDecimal;

public class FrequencyField extends NumberField {

//    private final NumberField content;
    private final FreqSelect freqSelect;

    public FrequencyField() {
        freqSelect = new FreqSelect();
        setPrefixComponent(freqSelect);

        setLabel(getTranslation("sr.we.freq"));
        setHelperText(getTranslation("sr.we.freq.info"));
    }

//    public void setRequiredIndicatorVisible(boolean required) {
//        content.setRequiredIndicatorVisible(required);
//    }

    public void setValue(FrequencyField.Value value) {
        freqSelect.setValue(value.getFreq());
        super.setValue(value.getFactor().doubleValue());
    }

    public FrequencyField.Value getValue1() {
        return new FrequencyField.Value(freqSelect.getValue(), super.getValue());
    }

    public void clear() {
        freqSelect.clear();
        super.clear();
    }

//    public void setWidthFull() {
//        content.setWidthFull();
//    }

//    public void setLabel(String label) {
//        content.setLabel(label);
//    }

//    public void setHelperText(String helperText) {
//        content.setHelperText(helperText);
//    }

    public void setReadOnly(boolean b) {
        freqSelect.setReadOnly(b);
        super.setReadOnly(b);
    }

    public static class Value {
        private final Loan.Freq freq;
        private final BigDecimal factor;

        public Value(Loan.Freq freq, Double factor) {
            this.freq = freq;
            this.factor = (factor == null ? null : new BigDecimal(factor));
        }

        public Loan.Freq getFreq() {
            return freq;
        }

        public BigDecimal getFactor() {
            return factor;
        }
    }
}
