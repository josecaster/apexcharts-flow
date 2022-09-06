package sr.we.ui.components.finance;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasHelper;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import sr.we.shekelflowcore.entity.Loan;

import java.math.BigDecimal;

public class FrequencyRangeField extends Composite<HorizontalLayout> implements HasHelper, HasLabel {

    private final NumberField min, max;
    private final FreqSelect freqSelect;

    public FrequencyRangeField() {
        HorizontalLayout content = getContent();
        content.setSpacing(true);
        content.setMargin(false);
        freqSelect = new FreqSelect();
        min = new NumberField();
        max = new NumberField();
        min.setPlaceholder("Min");
        max.setPlaceholder("Max");
        HorizontalLayout div = new HorizontalLayout(min, max);
        div.setSpacing(true);
        div.setMargin(false);
        content.add(freqSelect, div);
        content.getFlexGrow(freqSelect);
        setLabel(getTranslation("sr.we.freq"));
        setHelperText(getTranslation("sr.we.freq.info"));

        freqSelect.setWidthFull();
        min.setWidth("87.5px");
        max.setWidth("87.5px");

//        freqSelect.setWidth("50%");
//        div.setWidth("50%");

    }


    public void setRequiredIndicatorVisible(boolean required) {
        freqSelect.setRequiredIndicatorVisible(required);
        min.setRequiredIndicatorVisible(required);
        max.setRequiredIndicatorVisible(required);

    }

    public FrequencyRangeField.Value getValue() {
        return new FrequencyRangeField.Value(freqSelect.getValue(), min.getValue(), max.getValue());
    }

    public void setValue(FrequencyRangeField.Value value) {
        freqSelect.setValue(value.getFreq());
        min.setValue(value.getMin().doubleValue());
        max.setValue(value.getMax().doubleValue());
    }

    public void setWidthFull() {
        getContent().setWidthFull();
    }

    public void setReadOnly(boolean b) {
        freqSelect.setReadOnly(b);
        min.setReadOnly(b);
        max.setReadOnly(b);
    }

    public static class Value {
        private final Loan.Freq freq;
        private final BigDecimal min, max;

        public Value(Loan.Freq freq, Double min, Double max) {
            this.freq = freq;
            this.min = new BigDecimal(min);
            this.max = new BigDecimal(max);
        }

        public Loan.Freq getFreq() {
            return freq;
        }

        public BigDecimal getMin() {
            return min;
        }

        public BigDecimal getMax() {
            return max;
        }
    }
}
