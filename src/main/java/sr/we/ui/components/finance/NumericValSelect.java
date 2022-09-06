package sr.we.ui.components.finance;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.NumericVal;

import java.util.List;

public class NumericValSelect extends CustomField<NumericVal> implements HasPrefixAndSuffix {


    private final ComboBox<NumericVal> box1;
    private final Div div;

    public NumericValSelect() {
        box1 = new ComboBox();

        PojoService pojoService = ContextProvider.getBean(PojoService.class);
        String token = AuthenticatedUser.token();

        List<NumericVal> businessTypes = pojoService.listNumericVal(token, null);
        box1.setItems(businessTypes);

        box1.setItemLabelGenerator((f) -> f.getCode());

        box1.setPlaceholder(getTranslation("sr.we.placeholder"));
        div = new Div(box1);
        add(div );
    }

    @Override
    protected NumericVal generateModelValue() {

        return box1.getValue();
    }


    @Override
    protected void setPresentationValue(NumericVal numericValSelectComboBox) {
        this.box1.setValue(numericValSelectComboBox);
    }

    public void setPrefixComponent(Component comp) {
        div.addComponentAsFirst(comp);
    }

    public void setSuffixComponent(Component comp) {
        int componentCount = div.getComponentCount();
        div.addComponentAtIndex(componentCount, comp);
    }



    public void clear() {
        box1.clear();
    }

    public ComboBox<NumericVal> getBox1() {
        return box1;
    }
}
