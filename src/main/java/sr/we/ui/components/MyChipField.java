package sr.we.ui.components;

import com.flowingcode.vaadin.addons.chipfield.ChipField;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class  MyChipField<T> extends ChipField<T> implements HasPrefixAndSuffix {



    public MyChipField(String label, ItemLabelGenerator<T> itemLabelGenerator, T... availableItems) {
        super(label, itemLabelGenerator, availableItems);
        setThemeName(LumoUtility.BoxShadow.XSMALL);
    }

    public MyChipField(String label, T... availableItems) {
        super(label, availableItems);
        setThemeName(LumoUtility.BoxShadow.XSMALL);
    }
}
