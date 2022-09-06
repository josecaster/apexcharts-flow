package sr.we.ui.components;

import com.vaadin.flow.component.datepicker.DatePicker;

import java.util.Locale;

public class TempDatePicker extends DatePicker {

    public TempDatePicker() {
        init();
    }

    private void init() {
        Locale nl = new Locale("nl");
        setLocale(nl);
    }

    public TempDatePicker(String label) {
        super(label);
        init();
    }
}
