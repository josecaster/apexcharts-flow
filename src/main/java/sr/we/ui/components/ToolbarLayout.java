package sr.we.ui.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.template.Id;
import sr.we.ui.views.LineAwesomeIcon;

/**
 * A Designer generated component for the toolbar-layout template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("toolbar-layout")
@JsModule("./src/views/components/toolbar-layout.ts")
public class ToolbarLayout extends LitTemplate {

    @Id("addBtn")
    private Button addBtn;
    @Id("deleteBtn")
    private Button deleteBtn;
    @Id("saveBtn")
    private Button saveBtn;
    @Id("downloadBtn")
    private Button downloadBtn;
    @Id("reloadBtn")
    private Button reloadBtn;
    @Id("previousBtn")
    private Button previousBtn;
    @Id("pagingSummary")
    private Span pagingSummary;
    @Id("nextBtn")
    private Button nextBtn;
    @Id("pagingSelect")
    private Select pagingSelect;

    /**
     * Creates a new ToolbarLayout.
     */
    public ToolbarLayout() {
        addBtn.getElement().removeAllChildren();
        deleteBtn.getElement().removeAllChildren();
        saveBtn.getElement().removeAllChildren();
        downloadBtn.getElement().removeAllChildren();
        reloadBtn.getElement().removeAllChildren();
        previousBtn.getElement().removeAllChildren();
        nextBtn.getElement().removeAllChildren();
        // You can initialise any data required for the connected UI components here.
        addBtn.setIcon(new LineAwesomeIcon("la la-plus"));
        deleteBtn.setIcon(new LineAwesomeIcon("la la-trash"));
        saveBtn.setIcon(new LineAwesomeIcon("la la-save"));
        downloadBtn.setIcon(new LineAwesomeIcon("la la-download"));
        reloadBtn.setIcon(new LineAwesomeIcon("la la-redo-alt"));
        previousBtn.setIcon(new LineAwesomeIcon("la la-angle-left"));
        nextBtn.setIcon(new LineAwesomeIcon("la la-angle-right"));
    }

}
