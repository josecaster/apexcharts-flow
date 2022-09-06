package sr.we.ui.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import sr.we.shekelflowcore.entity.helper.Build;

public class Highlight extends VerticalLayout {

    private Build<String> value;
    private Build<Double> percentage;

    public Highlight(String title, Build<String> value, Build<Double> procent) {



        H2 h2 = new H2(title);
        h2.addClassNames("font-normal", "m-0", "text-secondary", "text-xs");

        Span valueSpan = new Span();
        valueSpan.addClassNames("font-semibold", "text-3xl");


        add(h2, valueSpan);


        UI current = UI.getCurrent();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String build = value.build();
                current.access(() -> {
                    valueSpan.setText(build);
                });
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {

                Double percentage = procent.build();
                current.access(() -> {
                    VaadinIcon icon = VaadinIcon.ARROW_UP;
                    Icon i = icon.create();
                    i.addClassNames("box-border", "p-xs");
                    String prefix = "";
                    String theme = "badge";
                    if (percentage != null) {
                        if (percentage == 0) {
                            prefix = "±";
                        } else if (percentage > 0) {
                            prefix = "+";
                            theme += " success";
                        } else if (percentage < 0) {
                            icon = VaadinIcon.ARROW_DOWN;
                            theme += " error";
                        }

                        String percentageSpanValue = prefix + percentage;
                        Span percentageSpan = new Span(percentageSpanValue);
                        Span badge = new Span(i, percentageSpan);
                        badge.getElement().getThemeList().add(theme);

                        add(badge);
                    }
                });
            }
        }).start();


        addClassName("p-l");
        setPadding(false);
        setSpacing(false);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }
}
