package sr.we.ui.views.finance.loans;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import sr.we.shekelflowcore.entity.Loan;
import sr.we.shekelflowcore.entity.helper.vo.LoanVO;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.ui.components.finance.FactorField;
import sr.we.ui.components.finance.FrequencyRangeField;
import sr.we.ui.components.general.CurrencySelect;

import java.math.BigDecimal;

/**
 * A Designer generated component for the loan-form template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("loan-form")
@JsModule("./src/views/finance/loan/loan-form.ts")
public class LoanForm extends LitTemplate {

    private final FrequencyRangeField frequencyRangeField;
    @Id("duration-layout")
    private Div durationLayout;
    @Id("loan-name-fld")
    private TextField loanNameFld;
    @Id("currency-cmb")
    private CurrencySelect currencyCmb;
    @Id("factor-type-fld")
    private FactorField factorTypeFld;
    @Id("fixed-loan-chk")
    private Checkbox fixedLoanChk;
    private String business;
    private Loan loan;

    /**
     * Creates a new LoanForm.
     */
    public LoanForm() {
        // You can initialise any data required for the connected UI components here.
        frequencyRangeField = new FrequencyRangeField();
        durationLayout.add(frequencyRangeField);

        currencyCmb.setLabel("Currency");
        currencyCmb.setHelperText(null);

        factorTypeFld.setLabel("Factor type");
        factorTypeFld.setHelperText(null);
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    protected boolean validate() {
        if (loanNameFld.isEmpty()) {
            return false;
        }
        return currencyCmb.getOptionalValue().isPresent();
    }


    LoanVO getLoanVO() {

        boolean validate = validate();
        if(!validate){
            throw new ValidationException("Not all required fields are filled");
        }

        LoanVO vo = new LoanVO();
        vo.setId(loan == null ? null : loan.getId());
        vo.setNew(vo.getId() == null);
        vo.setBusiness(Long.valueOf(business));
        vo.setCurrency(currencyCmb.getValue().getId());
        vo.setName(loanNameFld.getValue());
        vo.setFixed(fixedLoanChk.getValue());
        FrequencyRangeField.Value frequency = frequencyRangeField.getValue();
        vo.setFreq(frequency.getFreq());
        vo.setFreqMin(frequency.getMin().longValue());
        vo.setFreqMax(frequency.getMax().longValue());
        FactorField.Value factor = factorTypeFld.getValue1();
        vo.setFactor(factor.getFactor());
        vo.setFactorType(factor.getFactorType());
        return vo;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
        currencyCmb.setReadOnly(true);
        fixedLoanChk.setReadOnly(true);
        frequencyRangeField.setReadOnly(true);
        factorTypeFld.setReadOnly(true);

        currencyCmb.setValue(loan.getCurrency());
        loanNameFld.setValue(loan.getName());
        fixedLoanChk.setValue(loan.getFixed());

        Long freqMax = loan.getFreqMax();
        Long freqMin = loan.getFreqMin();
        Double min = freqMin == null ? null : freqMin.doubleValue();
        Double max = freqMax == null ? null : freqMax.doubleValue();
        Loan.Freq freq = loan.getFreq();
        FrequencyRangeField.Value value = new FrequencyRangeField.Value(freq, min, max);
        frequencyRangeField.setValue(value);

        Loan.FactorType factorType = loan.getFactorType();
        BigDecimal factor = loan.getFactor();
        Double factor1 = factor == null ? null : factor.doubleValue();
        FactorField.Value value1 = new FactorField.Value(factorType, factor1);
        factorTypeFld.setValue(value1);
    }
}
