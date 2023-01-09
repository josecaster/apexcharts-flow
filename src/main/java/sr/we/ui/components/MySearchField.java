package sr.we.ui.components;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import sr.we.ui.components.buttons.FilterButton;
import sr.we.ui.views.LineAwesomeIcon;

/**
 * A Designer generated component for the my-search-field template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("my-search-field")
@JsModule("./src/views/components/my-search-field.ts")
public class MySearchField extends LitTemplate {

    @Id("my-filter")
    private TextField myFilter;
    //    @Id("left")
//    private Div left;

//    private final MyChipField<String> myFilterr;
    //    @Id("middle")
//    private Div middle;
//    @Id("right")
//    private Div right;

    /**
     * Creates a new MySearchField.
     */
    public MySearchField() {
//        myFilterr = new MyChipField<>("Search");
//        middle.add(myFilterr);
        // You can initialise any data required for the connected UI components here.
//        myFilterr.setNewItemHandler(label -> label);
//        myFilterr.setClosable(true);
        myFilter.setPrefixComponent(new LineAwesomeIcon("la la-search"));
        myFilter.setSuffixComponent(new FilterButton());

    }

    public void addValueChangeListener(HasValue.ValueChangeListener valueChangeListener) {
        myFilter.addValueChangeListener(valueChangeListener);
    }
}
