package sr.we.ui.views.finance.loans.tabs.request.planning;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.function.ValueProvider;
import sr.we.ContextProvider;
import sr.we.CustomNotificationHandler;
import sr.we.data.controller.LoanRequestService;
import sr.we.data.controller.UserAccessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.LoanRequestPlan;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.adapter.LoanRequestSchedulePlan;
import sr.we.shekelflowcore.entity.helper.adapter.LoanRequestSchedulePlanDetail;
import sr.we.shekelflowcore.exception.PrimaryThrowable;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanRequestPlanPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.TempDatePicker;
import sr.we.ui.views.LineAwesomeIcon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class LRPGenerate extends VerticalLayout {

    private LoanRequestSchedulePlan loanRequestPlan;
    private Grid<LoanRequestSchedulePlanDetail> grid;

    public LRPGenerate(LoanRequestSchedulePlan plan, LoanRequest loanRequest, Executable executable) {
        this.loanRequestPlan = plan;
        generatePLanning(loanRequest, executable);
        if (plan != null) {
            forPlan();
            enablePayment();
        }
    }

    private void enablePayment() {
        grid.addComponentColumn(new ValueProvider<LoanRequestSchedulePlanDetail, LineAwesomeIcon>() {
            @Override
            public LineAwesomeIcon apply(LoanRequestSchedulePlanDetail loanRequestPlanning) {
                LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-pencil");
                lineAwesomeIcon.addClickListener(f -> {
                    CustomNotificationHandler.notify_(new PrimaryThrowable("Payment Recording"));
                });
                return lineAwesomeIcon;
            }
        }).setHeader("Payment").setResizable(true);
    }

    private void generatePLanning(LoanRequest loanRequest, Executable executable) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        RadioButtonGroup<LoanRequestPlan.Type> loanRequestPLanType = new RadioButtonGroup<>();

        NumberField numberField = new NumberField();
        numberField.setValue(loanRequest.getFreqVal().doubleValue());
        numberField.setReadOnly(true);
        numberField.setRequiredIndicatorVisible(true);
        numberField.setStep(1);
        numberField.setMin(1);

        DatePicker datePicker = new TempDatePicker();
        loanRequestPLanType.addValueChangeListener(f -> {
            if (f.getValue().compareTo(LoanRequestPlan.Type.EXTEND) == 0) {
                numberField.setReadOnly(false);
                numberField.setValue(null);
                datePicker.setVisible(false);
            } else if (f.getValue().compareTo(LoanRequestPlan.Type.PAYMENT) == 0) {
                numberField.setReadOnly(true);
                numberField.setValue(loanRequest.getFreqVal().doubleValue());
                datePicker.setVisible(true);
            } else if (f.getValue().compareTo(LoanRequestPlan.Type.BALANCE) == 0) {
                numberField.setReadOnly(true);
                numberField.setValue(loanRequest.getFreqVal().doubleValue());
                datePicker.setVisible(true);
            }
        });


        H4 h4 = new H4(loanRequest.getBalance() == null ? "" : loanRequest.getBalance().toString());
        add(new VerticalLayout(h4, loanRequestPLanType, numberField, horizontalLayout));
        datePicker.setValue(loanRequest.getEstimatedDate());
        datePicker.setVisible(false);
        Checkbox checkbox = new Checkbox();
        if (loanRequest.getIntrestFirst() == null) {
            loanRequestPLanType.setItems(LoanRequestPlan.Type.PAYMENT);
            loanRequestPLanType.setValue(LoanRequestPlan.Type.PAYMENT);
            checkbox.setVisible(true);
        } else {
            List<LoanRequestPlan.Type> types = new ArrayList<>();
            if (loanRequest.getBalance() != null && loanRequest.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                types.add(LoanRequestPlan.Type.BALANCE);
            }
            types.add(LoanRequestPlan.Type.EXTEND);
            loanRequestPLanType.setItems(types);
            if (loanRequest.getBalance() != null && loanRequest.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                loanRequestPLanType.setValue(LoanRequestPlan.Type.BALANCE);
            } else {
                loanRequestPLanType.setValue(LoanRequestPlan.Type.EXTEND);
            }
            checkbox.setVisible(false);
            checkbox.setValue(loanRequest.getIntrestFirst());
        }
        horizontalLayout.add(datePicker);

//        FactorField factorField = new FactorField();
//        factorField.setLabel(null);
//        factorField.setHelperText(null);
//        factorField.setWidth("250px");
        checkbox.setLabel("Intrest first, Principal Later");
        horizontalLayout.add(checkbox);
//        factorField.setValue(new FactorField.Value(Loan.FactorType.P, 100d));

        Button button = new Button("Generate planning");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanRequestPlanPrivilege(), Privileges.EXECUTE);
        button.setVisible(hasAccess);
        horizontalLayout.add(button);

        Button save = new Button("Save");
        save.setVisible(false);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
//        hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanRequestPlanPrivilege(), Privileges.INSERT);
//        save.setVisible(hasAccess);
        horizontalLayout.add(save);

        setSizeFull();

        grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        add(grid);
        grid.setAllRowsVisible(true);

        grid.addColumn(new ValueProvider<LoanRequestSchedulePlanDetail, String>() {
            @Override
            public String apply(LoanRequestSchedulePlanDetail loanRequestPlanning) {
                return loanRequestPlanning.getFreq().getCaption() + " " + loanRequestPlanning.getFreqAmount();
            }
        }).setHeader("Frequency").setResizable(true).setSortable(true);

        grid.addColumn(new ValueProvider<LoanRequestSchedulePlanDetail, LocalDate>() {
            @Override
            public LocalDate apply(LoanRequestSchedulePlanDetail loanRequestPlanning) {
                return loanRequestPlanning.getInitDate();
            }
        }).setHeader("Payment date").setResizable(true).setSortable(true);

        grid.addColumn(new ValueProvider<LoanRequestSchedulePlanDetail, String>() {
            @Override
            public String apply(LoanRequestSchedulePlanDetail loanRequestPlanning) {
                return Constants.CURRENCY_FORMAT.format(loanRequestPlanning.getFactor());
            }
        }).setHeader("Amount").setResizable(true).setSortable(true);

        grid.addColumn(new ValueProvider<LoanRequestSchedulePlanDetail, String>() {
            @Override
            public String apply(LoanRequestSchedulePlanDetail loanRequestPlanning) {
                return Constants.CURRENCY_FORMAT.format(loanRequestPlanning.getCapital());
            }
        }).setHeader("Running totals").setResizable(true).setSortable(true);

        LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
        button.addClickListener(f -> {
//            FactorField.Value value = factorField.getValue();
            Double value = numberField.getValue();
            LocalDate value1 = datePicker.getValue();
            if (value == null || value1 == null) {
                throw new ValidationException("Required fields are not filled");
            }
            loanRequestPlan = loanRequestService.generatePlanning(loanRequestPLanType.getValue(), value.longValue(), value1, checkbox.getValue(), loanRequest.getId(), AuthenticatedUser.token());
            List<LoanRequestSchedulePlanDetail> loanRequestPlannings = forPlan();
            if (loanRequestPlannings != null && !loanRequestPlannings.isEmpty()) {
                boolean hasAccess2 = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanRequestPlanPrivilege(), Privileges.INSERT);
                if (hasAccess2) {
                    save.setVisible(true);
                    button.setVisible(false);
                }
            }
        });

        save.addClickListener(f -> {
            loanRequestService.save(loanRequestPlan, AuthenticatedUser.token());
            executable.build();
//            forPlan();
//            enablePayment();
        });
    }

    private List<LoanRequestSchedulePlanDetail> forPlan() {
        List<LoanRequestSchedulePlanDetail> loanRequestPlannings = loanRequestPlan.getLoanRequestPlannings();
        grid.setItems(loanRequestPlannings == null ? new ArrayList<>() : loanRequestPlannings);
        grid.recalculateColumnWidths();
        return loanRequestPlannings;
    }
}
