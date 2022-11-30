package sr.we.ui.components;

import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;

public class MyDialog extends Dialog {

    private static final String SET_PROPERTY_IN_OVERLAY_JS = "this.$.overlay.$.overlay.style[$0]=$1";

    public MyDialog() {
    }

    public MyDialog(Component... components) {
        super(components);
    }

    @Override
    public void open() {
        super.open();
        Animated.animate(this, Animated.Animation.BACK_IN_UP);
    }

    @Override
    public void close() {
        super.close();
        Animated.animate(this, Animated.Animation.BACK_OUT_DOWN);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        Animated.removeAnimations(MyDialog.this);
        super.onDetach(detachEvent);
    }
}
