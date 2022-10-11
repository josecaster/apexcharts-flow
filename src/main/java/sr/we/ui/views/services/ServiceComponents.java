package sr.we.ui.views.services;

import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.dom.Element;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.vo.CalculationComponentVO;
import sr.we.shekelflowcore.entity.helper.vo.ServicesVO;

/**
 * A Designer generated component for the service-components template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("service-components")
@JsModule("./src/views/services/service-components.ts")
public class ServiceComponents extends LitTemplate {

    private final Grid<CalculationComponentVO> serviceComponentsGrid;
    @Id("add-component")
    private Button addBtn;
    @Id("grid-layout")
    private Div gridLayout;
    @Id("service-components-formula-form")
    private ServiceComponentsFormulaForm serviceComponentsFormulaForm;

    boolean edit = false;
    @Id("action-layout")
    private Element actionLayout;

    /**
     * Creates a new ServiceComponents.
     */
    public ServiceComponents() {
        // You can initialise any data required for the connected UI components here.

        serviceComponentsFormulaForm.setVisible(false);

        addBtn.addClickListener(f -> {
            serviceComponentsFormulaForm.setVO(null);
            serviceComponentsFormulaForm.setVisible(true);
            Animated.animate(serviceComponentsFormulaForm, edit ? Animated.Animation.FADE_IN_UP : Animated.Animation.FADE_IN_RIGHT, Animated.Modifier.FAST);
        });

        serviceComponentsFormulaForm.setOnSave(new Executable(){

            @Override
            public Object build() {
                CalculationComponentVO vo = serviceComponentsFormulaForm.getVO();
                if(!serviceComponentsFormulaForm.isEdit()){
                    servicesVO.getCalculationComponentVO().add(vo);
                }
                serviceComponentsGrid.getDataProvider().refreshAll();
                return null;
            }
        });


        serviceComponentsGrid = new Grid<>();
        gridLayout.add(serviceComponentsGrid);

        serviceComponentsGrid.setWidthFull();
        serviceComponentsGrid.addColumn(CalculationComponentVO::getCode).setHeader("Code");
        serviceComponentsGrid.addColumn(CalculationComponentVO::getName).setHeader("Name");
        serviceComponentsGrid.addColumn(CalculationComponentVO::getFormula).setHeader("Formula");
        serviceComponentsGrid.addColumn(CalculationComponentVO::getVisibleCustomer).setHeader("Visible customer");
        serviceComponentsGrid.addColumn(CalculationComponentVO::getActive).setHeader("Active");


        serviceComponentsGrid.addItemDoubleClickListener(f -> {
            CalculationComponentVO item = f.getItem();
            serviceComponentsFormulaForm.setVO(item);
            serviceComponentsFormulaForm.setVisible(true);
            Animated.animate(serviceComponentsFormulaForm, edit ? Animated.Animation.FADE_IN_UP : Animated.Animation.FADE_IN_RIGHT, Animated.Modifier.FAST);
        });

    }
private ServicesVO servicesVO;
    public void setVO(ServicesVO servicesVO) {
        this.servicesVO= servicesVO;
        serviceComponentsGrid.setItems(servicesVO.getCalculationComponentVO());
        serviceComponentsGrid.getDataProvider().refreshAll();
        serviceComponentsFormulaForm.setComponets(servicesVO.getCalculationComponentVO());
    }
}
