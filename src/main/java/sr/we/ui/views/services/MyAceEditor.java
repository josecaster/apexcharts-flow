package sr.we.ui.views.services;

import de.f0rce.ace.AceEditor;
import de.f0rce.ace.enums.AceMode;
import de.f0rce.ace.enums.AceTheme;
import sr.we.shekelflowcore.entity.helper.vo.CalculationComponentVO;
import sr.we.shekelflowcore.settings.util.Rubric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyAceEditor extends AceEditor {

    public MyAceEditor() {
        setTheme(AceTheme.terminal);
        List<String> collect = new ArrayList<>();
        setAutoComplete(true);
        setMode(AceMode.io);
        setWidthFull();
    }

    public void setComponets(List<CalculationComponentVO> calculationComponentVO) {
        if(calculationComponentVO != null && !calculationComponentVO.isEmpty()){
            List<String> collect = calculationComponentVO.stream().map(f -> "(" + f.getCode() + ")").collect(Collectors.toList());
            collect.add("("+ Rubric.SERVICE_PRICE.getTitle() +")");
            collect.add("("+ Rubric.SERVICE_COST.getTitle() +")");
            collect.add("("+ Rubric.SERVICE_COMPARE_PRICE.getTitle() +")");
            setCustomAutocompletion(collect,"");
        }
    }
}
