package sr.we.ui.components.finance;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import sr.we.ui.views.LineAwesomeIcon;

import java.io.Serializable;

public class FormulaParameterField extends Composite<VerticalLayout> implements Serializable {

    private final VerticalLayout content;

    private String type;
    private int parameters;

    public FormulaParameterField(VerticalLayout parentLayout, String type, int parameters, String... parameterNames) {
        content = getContent();
        LineAwesomeIcon clear = new LineAwesomeIcon("la la-trash");
        clear.addClickListener(g -> {
            parentLayout.remove(this);
        });
        VerticalLayout layout = new VerticalLayout(clear);
        content.add(layout);
        layout.getElement().getStyle().set("background", "var(--lumo-base-color)");
        layout.getElement().getStyle().set("border-radius", "var(--lumo-border-radius-m)");

        VerticalLayout parent = new VerticalLayout();
//        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        if (type.equalsIgnoreCase("BRACKET")) {
            layout.add("(");
            layout.add(parent);
            VerticalLayout child = new VerticalLayout();
            LineAwesomeIcon component = FormulaField.menuItems(parent, child, 0);
            child.add( component);
            layout.add(")");
        } else {

            layout.add(parent);


            for (int i = 0; i < parameters; i++) {
                VerticalLayout child = new VerticalLayout();
                LineAwesomeIcon component = FormulaField.menuItems(parent, child, i);
                component.add(parameterNames[i]);
                parent.add( component);
            }
        }

    }


}
