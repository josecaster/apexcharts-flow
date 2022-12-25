package sr.we.ui.components.finance;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasHelper;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import sr.we.ui.views.LineAwesomeIcon;

public class FormulaField extends Composite<VerticalLayout> implements HasLabel, HasHelper {

    private final VerticalLayout content;
    private Span labelFld;
    private ContextMenu helperFld;

    public FormulaField() {
//        Button formulaBtn = new Button("Formula");
//        Button placeHolderBtn = new Button(getTranslation("sr.we.placeholder"));
        content = getContent();

        VerticalLayout layout = new VerticalLayout();
        content.add(layout);
        layout.getElement().getStyle().set("background", "var(--lumo-contrast-10pct)");
        layout.getElement().getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        layout.addClassName("card");
//        content.add(new HorizontalLayout(formulaBtn, placeHolderBtn));

        VerticalLayout parent = new VerticalLayout();
        layout.add(parent);

//        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2), new FormLayout.ResponsiveStep("500", 3));
//        formula(form, 0);
        VerticalLayout child = new VerticalLayout();
        LineAwesomeIcon component = menuItems(parent, child, 0);
        parent.addComponentAtIndex(0, component);
//        placeholder(form, 0);
//        formulaBtn.addClickListener(f -> {
//            formula(form, 0);
//
//        });
//        placeHolderBtn.addClickListener(f -> {
//            placeholder(form, 0);
//        });

    }

    private static void formula(VerticalLayout parent, VerticalLayout child, int index) {
        TextField textField = new TextField();
        textField.setPlaceholder("Formula");
        textField.setWidthFull();
        parent.addComponentAtIndex(index, textField);
        LineAwesomeIcon clear = new LineAwesomeIcon("la la-trash");
        textField.setPrefixComponent(clear);
        clear.addClickListener(g -> {
            if (parent.getChildren().count() > 1) {
                parent.remove(textField);
            } else {
                textField.clear();
            }
        });

        textField.setSuffixComponent(menuItems(parent, child, index));
    }

    private static void placeholder(VerticalLayout parent, VerticalLayout child, int index) {
        NumericValSelect textField = new NumericValSelect();
        textField.setWidthFull();
        textField.getBox1().setWidth("80%");
        parent.add( textField);
        LineAwesomeIcon clear = new LineAwesomeIcon("la la-trash");
        textField.setPrefixComponent(clear);
        clear.addClickListener(g -> {
            if (parent.getChildren().count() > 1) {
                parent.remove(textField);
            } else {
                textField.clear();
            }

        });


        textField.setSuffixComponent(menuItems(parent, child, index));
    }

    public static LineAwesomeIcon menuItems(VerticalLayout parent, VerticalLayout child, int indexx) {

        parent.setPadding(false);
        parent.setMargin(false);
        child.setPadding(false);
        child.setMargin(false);

        int componentCount = parent.getComponentCount();
        parent.add(child);
//        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2), new FormLayout.ResponsiveStep("500", 3));

        int index = child.getComponentCount() - 1;


        LineAwesomeIcon addMore = new LineAwesomeIcon("la la-plus-circle");

        ContextMenu userMenu = new ContextMenu(addMore);
        userMenu.setOpenOnClick(true);
        userMenu.addItem("Formula", e -> {
            formula(child, new VerticalLayout(), index + 1);
        });
        userMenu.addItem("Placeholder", e -> {
            placeholder(child, new VerticalLayout(), index + 1);
        });
        userMenu.addItem("BRACKET (...)", e -> {
            child.add( new FormulaParameterField(child, "BRACKET", 0));
        });
//        userMenu.addItem("CONDITION", e -> {
//            form.add( new FormulaParameterField(form, "IF", 3, "IF", "THEN", "ELSE"));
//        });
        userMenu.addItem("ROUNDUP(x,2)", e -> {
            child.add( new FormulaParameterField(child, "ROUNDUP", 2, "VALUE", "FACTOR"));
        });
        userMenu.addItem("ROUNDDOWN(x,2)", e -> {
            child.add( new FormulaParameterField(child, "ROUNDDOWN", 2, "VALUE", "FACTOR"));
        });
        userMenu.addItem("MIN(x,y)", e -> {
            child.add( new FormulaParameterField(child, "MIN", 2, "COMPARE1", "COMPARE2"));
        });
        userMenu.addItem("MAX(x,y)", e -> {
            child.add( new FormulaParameterField(child, "MAX", 2, "COMPARE1", "COMPARE2"));
        });
        userMenu.addItem("MATCH(x,y,z,q)", e -> {
            child.add( new FormulaParameterField(child, "MATCH", 4, "COMPARE1", "COMPARE2", "TRUE", "FALSE"));
        });
//        userMenu.addItem("FLOOR", e -> {
//        });
//        userMenu.addItem("CEILING", e -> {
//        });
        return addMore;
    }


    @Override
    public void setLabel(String label) {
        HasLabel.super.setLabel(label);
        if (labelFld == null) {
            labelFld = new Span(label);
            content.addComponentAsFirst(labelFld);
        } else {
            labelFld.setText(label);
        }

    }

    @Override
    public void setHelperText(String helperText) {
        HasHelper.super.setHelperText(helperText);
        int componentCount = content.getComponentCount();
        if (helperFld == null) {
            LineAwesomeIcon component = new LineAwesomeIcon("la la-question-circle");
            content.addComponentAtIndex(componentCount, component);
            helperFld = new ContextMenu(component);
            helperFld.setOpenOnClick(true);
            helperFld.addItem(helperText);
        } else {
            helperFld.removeAll();
            helperFld.addItem(helperText);
        }

    }
}
