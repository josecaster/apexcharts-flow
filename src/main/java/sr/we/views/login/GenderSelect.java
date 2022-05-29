package sr.we.views.login;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.select.Select;
import sr.we.shekelflowcore.entity.helper.Gender;

public class GenderSelect extends Select<Gender> {

    public GenderSelect() {
        setItems(Gender.M,Gender.F,Gender.O);
        setItemLabelGenerator(new ItemLabelGenerator<Gender>() {
            @Override
            public String apply(Gender item) {
                return item.toString();
            }
        });
        setLabel(getTranslation("sr.we.gender"));
//        setHelperText(getTranslation("sr.we.gender.info"));
    }
}
