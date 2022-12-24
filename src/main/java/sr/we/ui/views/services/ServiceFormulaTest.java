package sr.we.ui.views.services;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.NumberField;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.CalculationService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.helper.adapter.CalculationParam;
import sr.we.shekelflowcore.entity.helper.adapter.CalculationResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;


public class ServiceFormulaTest extends Dialog {

    private final VerticalLayout contentLayout;
    private final Label formula;
    private Map<String, Object> map;
    private String text;

    public ServiceFormulaTest() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        add(layout);
        setMaxWidth("750px");

        setHeaderTitle("Formula Tester");
        layout.add(new Hr());
        contentLayout = new VerticalLayout();
        layout.add(contentLayout);
        layout.add(new Hr());
        formula = new Label();
        layout.add(formula);

        map = new HashMap<>();
    }

    public void test(Long serviceId) {
        contentLayout.add(new ProgressBar());

        open();

        UI current = UI.getCurrent();

        String token = AuthenticatedUser.token();

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                CalculationService calculationService = ContextProvider.getBean(CalculationService.class);
                CalculationParam vo = new CalculationParam();
                vo.setServiceId(serviceId);
                vo.setMap(map);

                CalculationResult calculate = calculationService.calculate(token, vo);
                map = calculate.getMap();
                current.access(() -> {
                    contentLayout.removeAll();
                    if (calculate.getResult() != null) {
                        contentLayout.add(calculate.getResult().toString());
                    } else if (calculate.getMap() != null && !calculate.getMap().isEmpty()) {
                        calculate.getMap().forEach((k, v) -> {
                            NumberField numberField = new NumberField();
                            numberField.setWidthFull();
                            numberField.setLabel(k);
                            numberField.addValueChangeListener(f -> {
                                calculate.getMap().replace(k,f.getValue());
                            });
                            contentLayout.add(numberField);
                        });
                    }
                });
            }
        });


    }

//    public void setFormula(String formula) {
//
//        contentLayout.removeAll();
//        Matcher matcher = Pattern.compile("\\[([^\\]\\[\\r\\n]*)\\]").matcher(formula);
//        Set<String> groups = new HashSet<String>();
//        while (matcher.find()) {
//            String group = matcher.group(1);
//            groups.add(group);
//        }
//        for (String m : groups) {
//
//            NumberField numberField = new NumberField();
//            numberField.setWidthFull();
//            numberField.setLabel(m);
//            numberField.addValueChangeListener(f -> {
//                if (f.getValue() != null) {
//                    if(StringUtils.isBlank(text)){
//                        text = formula;
//                    }
//                    text = text.replace("["+m+"]", f.getValue().toString());
//                    this.formula.setText(text + "=" + get(text).toPlainString());
//                }
//            });
//            contentLayout.add(numberField);
//        }
//        this.formula.setText(formula + "=" + get(formula).toPlainString());
//    }
//
//    public BigDecimal get(String formula) {
//        BigDecimal result = BigDecimal.ZERO;
//
//        // Simple usage with an expression without variables.
//        try {
//            Expression expression = new Expression(formula);
//            expression.setPrecision(2);
//            result = expression.eval();
//        } catch (ExpressionException | NullPointerException e) {
//
//
//        }
//        return result;
//    }
}
