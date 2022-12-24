package sr.we.ui.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import sr.we.shekelflowcore.entity.helper.Executable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Highlight extends VerticalLayout {

    private Executable<String> value;
    private Executable<Double> percentage;

    public Highlight(String title, Executable<String> value, Executable<Double> procent) {



        H2 h2 = new H2(title);
        h2.addClassNames("font-normal", "m-0", "text-secondary", "text-xs");

        Span valueSpan = new Span();
        valueSpan.addClassNames("font-semibold", "text-3xl");


        ProgressBar progressBar = new ProgressBar();
        progressBar.setWidthFull();
        progressBar.setIndeterminate(true);
        add(h2, valueSpan, progressBar);


        UI current = UI.getCurrent();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                String build = value.build();
                current.access(() -> {
                    valueSpan.setText(build);
                });
            }
        });

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                Double percentage = procent.build();
                current.access(() -> {
                    VaadinIcon icon = VaadinIcon.ARROW_UP;
                    Icon i = icon.create();
                    i.addClassNames("box-border", "p-xs");
                    String prefix = "";
                    String theme = UIUtil.Badge.PILL;
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
                        Span badge = new Span();
                        if(percentage == 0){
                            badge.add(percentageSpan);
                        } else {
                            badge.add(i, percentageSpan);
                        }
                        badge.getElement().getThemeList().add(theme);

                        add(badge);
                    }
                    progressBar.setVisible(false);
                });
            }
        });


        addClassName("p-l");
        setPadding(false);
        setSpacing(false);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }
}
