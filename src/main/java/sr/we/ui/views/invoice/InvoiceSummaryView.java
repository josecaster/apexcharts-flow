package sr.we.ui.views.invoice;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.redhogs.cronparser.CronExpressionDescriptor;
import net.redhogs.cronparser.DescriptionTypeEnum;
import net.redhogs.cronparser.Options;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.addon.stefan.clipboard.ClientsideClipboard;
import sr.we.ContextProvider;
import sr.we.CustomNotificationHandler;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.InvoiceService;
import sr.we.data.controller.UserAccessService;
import sr.we.data.report.MyReportEngine;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.vo.InvoiceVO;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.exception.PrimaryThrowable;
import sr.we.shekelflowcore.exception.SuccessThrowable;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.POSPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.shekelflowcore.settings.util.DateUtil;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.MailDialog;
import sr.we.ui.components.MyDialog;
import sr.we.ui.components.UIUtil;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.ReRouteLayout;
import sr.we.ui.views.finance.transactions.TransactionDialog;
import sr.we.ui.views.finance.transactions.TransactionsCmb;

import javax.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.Executors;

import static sr.we.ContextProvider.getBean;

/**
 * A Designer generated component for the invoice-summary-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@BreadCrumb(titleKey = "sr.we.invoices.view", parentNavigationTarget = InvoiceView.class)
@Route(value = "invoice-view", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
@Tag("invoice-summary-view")
@JsModule("./src/views/invoice/invoice-summary-view.ts")
public class InvoiceSummaryView extends LitTemplate implements BeforeEnterObserver {

    protected final Button invoiceSummaryDownloadBtn;
    protected final FormLayout expressionBuilder;
    @Id("invoice-view-main-layout")
    protected VerticalLayout invoiceViewMainLayout;
    @Id("invoice-view-title-h2")
    protected H2 invoiceViewTitleH2;
    @Id("invoice-view-status-span")
    protected Span invoiceViewStatusSpan;
    @Id("invoice-view-customer-span")
    protected Span invoiceViewCustomerSpan;
    @Id("invoice-view-amount-due-span")
    protected Span invoiceViewAmountDueSpan;
    @Id("invoice-view-due-on-span")
    protected Span invoiceViewDueOnSpan;
    @Id("invoice-view-create-layout")
    protected VerticalLayout invoiceViewCreateLayout;
    @Id("invoice-view-send-layout")
    protected VerticalLayout invoiceViewSendLayout;
    @Id("invoice-view-get-paid-layout")
    protected VerticalLayout invoiceViewGetPaidLayout;
    @Id("invoice-view-get-paid-content")

    protected VerticalLayout invoiceViewGetPaidContent;
    @Id("invoice-view-send-content")
    protected VerticalLayout invoiceViewSendContent;
    @Id("invoice-view-create-content")
    protected VerticalLayout invoiceViewCreateContent;
    @Id("invoice-view-create-icon-span")
    protected LineAwesomeIcon invoiceViewCreateIconSpan;
    @Id("invoice-view-send-icon-span")
    protected LineAwesomeIcon invoiceViewSendIconSpan;
    @Id("invoice-view-get-paid-icon-span")
    protected LineAwesomeIcon invoiceViewGetPaidIconSpan;
    @Id("invoice-view-edit-invoice-btn")
    protected Button invoiceViewEditInvoiceBtn;
    @Id("invoice-summary-payment-btn")
    protected Button invoiceSummaryPaymentBtn;
    protected String business;
    protected Business business2;
    protected Invoice invoice;
    protected InvoiceSetting invoiceSetting;
    protected Long posHeaderId;
    @Id("invoice-summary-share-link-btn")
    protected Button invoiceSummaryShareLinkBtn;
    @Id("invoice-summary-resend-invoice-btn")
    protected Button invoiceSummaryResendInvoiceBtn;
    @Id("invoice-summary-dashboard")
    protected FormLayout invoiceSummaryDashboard;
    @Id("invoice-summary-download-btn-layout")
    protected Div invoiceSummaryBtnLayout;
    @Id("invoice-view-schedule-content")
    protected VerticalLayout invoiceViewScheduleContent;
    @Id("invoice-view-schedule-layout")
    protected VerticalLayout invoiceViewScheduleLayout;
    @Id("invoice-view-start-schedule-btn")
    protected Button invoiceViewStartScheduleBtn;
    @Id("invoice-view-enable-schedule-chk")
    protected Checkbox invoiceViewEnableScheduleChk;
    @Id("invoice-view-schedule-icon-span")
    protected LineAwesomeIcon invoiceViewScheduleIconSpan;
    protected Select<ApplicationScheduledTask.CronExpressionBuilder> cronExpressionBuilderSelect;
    @Id("invoice-view-edit-schedule-btn")
    protected Button invoiceViewEditScheduleBtn;
    @Id("invoice-view-schedule-end")
    protected VerticalLayout invoiceViewScheduleEnd;
    @Id("invoice-view-end-radio-group")
    protected RadioButtonGroup<String> invoiceViewEndRadioGroup;
    @Id("invoice-view-end-form-item")
    protected FormItem invoiceViewEndFormItem;
    @Id("invoice-view-end-span")
    protected Span invoiceViewEndSpan;
    private Dialog dialog;
    private IntegerField integerField;
    private DatePicker datePicker;
    @Id("invoice-status-btn")
    private Button invoiceStatusBtn;
    @Id("settings-btn")
    private Button settingsBtn;
    @Id("is-transactions-layout")
    private TransactionsCmb isTransactionsLayout;

    /**
     * Creates a new InvoiceSummaryView.
     */
    public InvoiceSummaryView() {
        // You can initialise any data required for the connected UI components here.

        invoiceViewCreateIconSpan.icon("la la-plus-circle");
        invoiceViewSendIconSpan.icon("la la-paper-plane");
        invoiceViewGetPaidIconSpan.icon("la la-credit-card");
        invoiceViewScheduleIconSpan.icon("la la-calendar");

        invoiceViewCreateIconSpan.addClassName(LumoUtility.TextColor.PRIMARY);
        invoiceViewSendIconSpan.addClassName(LumoUtility.TextColor.PRIMARY);
        invoiceViewGetPaidIconSpan.addClassName(LumoUtility.TextColor.PRIMARY);
        invoiceViewScheduleIconSpan.addClassName(LumoUtility.TextColor.PRIMARY);

        invoiceViewEditInvoiceBtn.addClickListener(f -> {
            List<String> strings = List.of(invoice.getPosHeader().getId().toString());
            Map<String, List<String>> map = new HashMap<>();
            map.put("id", strings);
            QueryParameters queryParameters = new QueryParameters(map);
            UI.getCurrent().navigate(EditInvoiceView.getLocation(business), queryParameters);
        });

        invoiceSummaryPaymentBtn.addClickListener(f -> {
            Currency fromCurrency = invoice.getCurrencyTo();
            Currency selectedCurrency = invoice.getCurrencyTo();
            Reference reference = Reference.INVOICE;
            Long referenceId = invoice.getId();

            TransactionDialog transactionDialog = new TransactionDialog(invoice.getRest(), LocalDate.now(), business2.getId(), fromCurrency, selectedCurrency, reference, referenceId,(invoice.getCustomer() == null ? null : invoice.getCustomer().getId()));
            transactionDialog.setNextReferenceId(invoice.getId());
            transactionDialog.setRefresh(() -> {
                setByPosHeaderId(null, posHeaderId);
                return null;
            });
            transactionDialog.open();
        });

        invoiceSummaryDownloadBtn = new Button("Download invoice");
        invoiceSummaryDownloadBtn.getElement().getStyle().set("border", "solid 1px");
        invoiceSummaryDownloadBtn.getElement().getStyle().set("align-self", "center");
        invoiceSummaryDownloadBtn.getElement().getStyle().set("border-radius", "100px");

        invoiceSummaryResendInvoiceBtn.addClickListener(f -> {

            MailDialog mailDialog = new MailDialog("Mail invoice", (vo) -> {
                String token = AuthenticatedUser.token();
                InvoiceService invoiceService = ContextProvider.getBean(InvoiceService.class);
                UI current = UI.getCurrent();
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        vo.setReferenceId(invoice.getId());
                        boolean emailSend = invoiceService.sendEmail(vo, token);

                        current.access(() -> {
                            if (emailSend) {
                                CustomNotificationHandler.notify_(new SuccessThrowable());
                            }
                        });
                    }
                });
                return null;
            });
            String subj = "Invoice #" + invoice.getInvoiceNumber() + " From " + invoice.getBusiness().getName();
            String message = subj + " <br> " + invoice.getCurrencyTo().getCode() + " " + Constants.CURRENCY_FORMAT.format(invoice.getConvertedAmount());
            String emailTo = invoice.getCustomer() == null ? "" : //
                    (invoice.getCustomer().getPrimaryCustomerContacts() == null ? "" : //
                            (StringUtils.isBlank(invoice.getCustomer().getPrimaryCustomerContacts().getEmail()) ? "" : //
                                    invoice.getCustomer().getPrimaryCustomerContacts().getEmail()));
            mailDialog.setValues(emailTo, subj, message);
            mailDialog.open();


//            TextField contentComponent = new TextField();
//            contentComponent.setWidthFull();
//            contentComponent.setValue(invoice.getCustomer() == null ? "" : //
//                    (invoice.getCustomer().getPrimaryCustomerContacts() == null ? "" : //
//                            (StringUtils.isBlank(invoice.getCustomer().getPrimaryCustomerContacts().getEmail()) ? "" : //
//                                    invoice.getCustomer().getPrimaryCustomerContacts().getEmail())));
//            ConfirmationDialog confirmationDialog = new ConfirmationDialog("Send email", "Confirm e-mailadres", contentComponent);
//            confirmationDialog.open();
//            confirmationDialog.getContinueBtn().addClickListener(c -> {
//                String token = AuthenticatedUser.token();
//                InvoiceService invoiceService = ContextProvider.getBean(InvoiceService.class);
//                UI current = UI.getCurrent();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        boolean emailSend = invoiceService.sendEmail(invoice.getId(), contentComponent.getValue(), token);
//
//                        current.access(() -> {
//                            if (emailSend) {
//                                CustomNotificationHandler.notify_(new SuccessThrowable());
//                            }
//                        });
//                    }
//                }).start();
//            });
        });

        invoiceSummaryShareLinkBtn.addClickListener(f -> {
            InvoiceService invoiceService = ContextProvider.getBean(InvoiceService.class);
            String sharableLink = invoiceService.getSharableLink(invoice.getId(), AuthenticatedUser.token());
            invoiceViewSendContent.removeAll();
            LineAwesomeIcon component = new LineAwesomeIcon("la la-copy");
            Button component1 = new Button();
            component1.setIcon(component);
            component1.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            component1.addClickListener(c -> {
                ClientsideClipboard.writeToClipboard(sharableLink);
                CustomNotificationHandler.notify_(new PrimaryThrowable("Sharable link copied!"));
            });
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidthFull();
            horizontalLayout.add(new Anchor(sharableLink, sharableLink, AnchorTarget.BLANK), component1);
            invoiceViewSendContent.add(horizontalLayout);
        });

        Anchor anchor = new Anchor("", invoiceSummaryDownloadBtn);
        anchor.setTarget(AnchorTarget.BLANK);
        anchor.setHref(new StreamResource("Invoice" + UUID.randomUUID() + ".pdf", new InputStreamFactory() {

            @Override
            public InputStream createInputStream() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("INVOICE_ID_", invoice.getId());
                map.put("INVOICE_DUE", invoice.getRest());
                if (StringUtils.isNotBlank(invoice.getHeaderColor1())) {
                    map.put("HEADER_COLOR_1", invoice.getHeaderColor1());
                }
                if (StringUtils.isNotBlank(invoice.getHeaderColor2())) {
                    map.put("HEADER_COLOR_2", invoice.getHeaderColor2());
                }
                if (StringUtils.isNotBlank(invoice.getFooterColor1())) {
                    map.put("FOOTER_COLOR_1", invoice.getFooterColor1());
                }
                try {
                    byte[] exportReportMap = ((MyReportEngine) ContextProvider.getBean(MyReportEngine.class)).exportInvoice(map);
                    return new ByteArrayInputStream(exportReportMap);
                } catch (JRException e) {
                    throw new RuntimeException(e);
                }
            }

        }));


        expressionBuilder = getExpressionBuilder();
        invoiceViewScheduleContent.add(expressionBuilder);
        invoiceViewStartScheduleBtn.setEnabled(false);
        expressionBuilder.setVisible(false);
        invoiceViewEditScheduleBtn.setVisible(false);
        invoiceViewEditScheduleBtn.setEnabled(false);
        invoiceViewScheduleEnd.setVisible(false);
        invoiceViewEnableScheduleChk.addValueChangeListener(f -> {
            Boolean value2 = f.getValue();
            invoiceViewStartScheduleBtn.setEnabled(value2);
            invoiceViewEditScheduleBtn.setEnabled(value2);
            expressionBuilder.setVisible(value2);
            invoiceViewScheduleEnd.setVisible(value2);
            if (!value2 && f.isFromClient()) {
//                ApplicationScheduledTask.CronExpressionBuilder value = cronExpressionBuilderSelect.getValue();
                Boolean value1 = invoiceViewEnableScheduleChk.getValue();
                Long id = invoice.getId();
                InvoiceService invoiceService = ContextProvider.getBean(InvoiceService.class);
                InvoiceVO invoiceVO = new InvoiceVO();
                invoiceVO.setId(id);
                invoiceVO.setRecurringInvoice(value1);
//                invoiceVO.setCronBuilder(value);
//                invoiceVO.setHour(value.getHour());
//                invoiceVO.setMonth(value.getMonth());
//                invoiceVO.setMonthDay(value.getMonthDay());
//                invoiceVO.setDayOfWeek(value.getDayOfWeek());
                ApplicationScheduledTask applicationScheduledTask = invoiceService.scheduleInvoice(invoiceVO, AuthenticatedUser.token());
                setApplicationScheduledTask(applicationScheduledTask);
            }
        });

        invoiceViewEditScheduleBtn.addClickListener(f -> {
            invoiceViewScheduleContent.removeAll();
            invoiceViewEndSpan.setVisible(false);
            expressionBuilder.setVisible(true);
            invoiceViewScheduleEnd.setVisible(true);
            invoiceViewEditScheduleBtn.setVisible(false);
            invoiceViewStartScheduleBtn.setVisible(true);
            invoiceViewScheduleContent.add(expressionBuilder);
            invoiceViewEndRadioGroup.setVisible(true);
        });

        invoiceViewEndRadioGroup.setItems("A", "B", "C");
        invoiceViewEndRadioGroup.setRenderer(new ComponentRenderer<>(f -> {

            if (f.equalsIgnoreCase("A")) {
                datePicker = new DatePicker();
                datePicker.setLabel("End by date");
                return datePicker;
            } else if (f.equalsIgnoreCase("B")) {
                integerField = new IntegerField();
                integerField.setLabel("End by amount of recurring invoices");
                integerField.setHasControls(true);
                return integerField;
            }
            return new Label("Never");
        }));

        invoiceViewStartScheduleBtn.addClickListener(f -> {
            ApplicationScheduledTask.CronExpressionBuilder value = cronExpressionBuilderSelect.getValue();
            Boolean value1 = invoiceViewEnableScheduleChk.getValue();
            Long id = invoice.getId();
            InvoiceService invoiceService = ContextProvider.getBean(InvoiceService.class);
            InvoiceVO invoiceVO = new InvoiceVO();
            invoiceVO.setId(id);
            invoiceVO.setRecurringInvoice(value1);
            invoiceVO.setCronBuilder(value);
            invoiceVO.setHour(value.getHour());
            invoiceVO.setMonth(value.getMonth());
            invoiceVO.setMonthDay(value.getMonthDay());
            invoiceVO.setDayOfWeek(value.getDayOfWeek());
            String value2 = invoiceViewEndRadioGroup.getValue();
            if (StringUtils.isNotBlank(value2)) {
                if (value2.equalsIgnoreCase("A")) {
                    if (datePicker.getValue() == null) {
                        throw new ValidationException("Recurring end date is not provided");
                    }
                    invoiceVO.setRecurringEndDate(datePicker.getValue());
                } else if (value2.equalsIgnoreCase("B")) {
                    Integer value3 = integerField.getValue();
                    if (value3 == null) {
                        throw new ValidationException("Recurring amount is not provided");
                    }
                    invoiceVO.setRecurringAmount(value3);
                }
            }
            ApplicationScheduledTask applicationScheduledTask = invoiceService.scheduleInvoice(invoiceVO, AuthenticatedUser.token());
            setApplicationScheduledTask(applicationScheduledTask);
        });


        invoiceStatusBtn.addClickListener(f -> {
            boolean paid = invoice.getRest().compareTo(BigDecimal.ZERO) == 0;
            if (paid) {
                // close invoice
                InvoiceService invoiceService = ContextProvider.getBean(InvoiceService.class);
                InvoiceVO invoiceVO = new InvoiceVO();
                invoiceVO.setId(invoice.getId());
                invoiceVO.setStatus(Invoice.Status.CLOSED);
                invoice = invoiceService.status(invoiceVO, AuthenticatedUser.token());
                setInvoice(invoice);
                return;
            }

            boolean cancel = invoice.getPaymentTransactions() == null || invoice.getPaymentTransactions().isEmpty();
            if (cancel) {
                // close invoice
                InvoiceService invoiceService = ContextProvider.getBean(InvoiceService.class);
                InvoiceVO invoiceVO = new InvoiceVO();
                invoiceVO.setId(invoice.getId());
                invoiceVO.setStatus(Invoice.Status.CANCELED);
                invoice = invoiceService.status(invoiceVO, AuthenticatedUser.token());
                setInvoice(invoice);
                return;
            }
        });


        settingsBtn.setIcon(new LineAwesomeIcon("la la-cog"));
        settingsBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        settingsBtn.addClickListener(f -> {
            if (dialog == null) {
                dialog = new MyDialog();
                ColorPickerView colorPickerView = new ColorPickerView();
                colorPickerView.setValues(invoice, invoiceSetting);
                dialog.add(colorPickerView);
                Button cancel = new Button("Cancel", (e) -> dialog.close());
                Button save = new Button("Save", (e) -> {
                    colorPickerView.save();
                    dialog.close();
                    setByPosHeaderId(null, posHeaderId);
                });


                dialog.getFooter().add(cancel);
                dialog.getFooter().add(save);
            }
            dialog.open();


        });

        invoiceSummaryBtnLayout.add(anchor);
        invoiceViewCreateLayout.setMaxWidth("1000px");
        invoiceViewSendLayout.setMaxWidth("1000px");
        invoiceViewGetPaidLayout.setMaxWidth("1000px");
        invoiceViewScheduleContent.setMaxWidth("1000px");
    }

    public static String getLocation(String business) {
        return MainLayout.getLocation(business) + "/invoice-view";
    }

    protected void setApplicationScheduledTask(ApplicationScheduledTask applicationScheduledTask) {
        invoiceViewEditScheduleBtn.setVisible(true);
        invoiceViewStartScheduleBtn.setVisible(false);
        invoiceViewScheduleEnd.setVisible(true);
        invoiceViewScheduleContent.removeAll();
//        invoiceViewScheduleEnd.removeAll();
        invoiceViewEndRadioGroup.setVisible(false);
        invoiceViewEndSpan.setVisible(true);
        invoiceViewEnableScheduleChk.setValue(applicationScheduledTask.getActive());
        if (applicationScheduledTask.getEndDate() == null && applicationScheduledTask.getMaxTask() == null) {
            invoiceViewEndSpan.setText("Never");
        } else if (applicationScheduledTask.getEndDate() != null) {
            invoiceViewEndSpan.setText(Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(applicationScheduledTask.getEndDate())));
        } else {
            invoiceViewEndSpan.setText("After created a total of " + applicationScheduledTask.getMaxTask() + " invoice(s)");
        }
        try {
            Options options = new Options();
            options.setVerbose(true);
            invoiceViewScheduleContent.add(new Span(!applicationScheduledTask.getActive() ? "Recurring invoice disabled" : CronExpressionDescriptor.getDescription(DescriptionTypeEnum.FULL, applicationScheduledTask.getCronExpression(), options)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    protected FormLayout getExpressionBuilder() {

        FormLayout formLayout = new FormLayout();
        cronExpressionBuilderSelect = new Select<>();
        cronExpressionBuilderSelect.setPlaceholder("Choose a frequency");
        cronExpressionBuilderSelect.setItems(List.of(ApplicationScheduledTask.CronExpressionBuilder.values()));
        formLayout.add(cronExpressionBuilderSelect);
        FormLayout formLayout1 = new FormLayout();
        formLayout.add(formLayout1);

        cronExpressionBuilderSelect.addValueChangeListener(f -> {
            formLayout1.removeAll();
            if (f.getValue() == null) {
                return;
            }

            switch (f.getValue()) {
                case DAILY -> {
                    Select<ApplicationScheduledTask.Hour> numberField = new Select<ApplicationScheduledTask.Hour>();
                    numberField.setItems(List.of(ApplicationScheduledTask.Hour.values()));
                    numberField.setItemLabelGenerator(ApplicationScheduledTask.Hour::getCaption);
                    numberField.setHelperText("Choose an hour of the day");
                    formLayout1.add(numberField);

                    numberField.addValueChangeListener(l -> {
                        ApplicationScheduledTask.Hour value = l.getValue();
                        if (value == null) {
                            cronExpressionBuilderSelect.setHelperText("No valid schedule");
                            return;
                        }
                        try {
                            ApplicationScheduledTask.CronExpressionBuilder daily = f.getValue();
                            daily.setHour(value.getHour());
                            cronExpressionBuilderSelect.setHelperText(daily.getDescription());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                case WEEKLY -> {
                    Select<DayOfWeek> numberField = new Select<DayOfWeek>();
                    numberField.setItems(List.of(DayOfWeek.values()));
                    numberField.setItemLabelGenerator(g -> g.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
                    numberField.setHelperText("Choose a day of the week");
                    formLayout1.add(numberField);

                    numberField.addValueChangeListener(l -> {
                        DayOfWeek value = l.getValue();
                        if (value == null) {
                            cronExpressionBuilderSelect.setHelperText("No valid schedule");
                            return;
                        }
                        try {
                            ApplicationScheduledTask.CronExpressionBuilder daily = f.getValue();
                            daily.setDayOfWeek(value);
                            cronExpressionBuilderSelect.setHelperText(daily.getDescription());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                case MONTHLY -> {

                    Select<ApplicationScheduledTask.MyMonthDay> numberField1 = new Select<ApplicationScheduledTask.MyMonthDay>();
                    numberField1.setItems(List.of(ApplicationScheduledTask.MyMonthDay.values()));
                    numberField1.setItemLabelGenerator(g -> g.getCaption());
                    numberField1.setHelperText("Choose a day of the month");
                    formLayout1.add(numberField1);


                    numberField1.addValueChangeListener(l -> {
                        ApplicationScheduledTask.MyMonthDay value = l.getValue();
                        if (value == null) {
                            cronExpressionBuilderSelect.setHelperText("No valid schedule");
                            return;
                        }
                        try {
                            ApplicationScheduledTask.CronExpressionBuilder daily = f.getValue();
                            daily.setMonthDay(value.getDayOfMonth());
                            cronExpressionBuilderSelect.setHelperText(daily.getDescription());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                case YEARLY -> {
                    Select<Month> numberField = new Select<Month>();
                    numberField.setItems(List.of(Month.values()));
                    numberField.setItemLabelGenerator(g -> g.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
                    numberField.setHelperText("Choose a month of the year");
                    formLayout1.add(numberField);

                    Select<ApplicationScheduledTask.MyMonthDay> numberField1 = new Select<ApplicationScheduledTask.MyMonthDay>();
                    numberField1.setItems(List.of(ApplicationScheduledTask.MyMonthDay.values()));
                    numberField1.setItemLabelGenerator(g -> g.getCaption());
                    numberField1.setHelperText("Choose a day of the month");
                    formLayout1.add(numberField1);


                    numberField1.addValueChangeListener(l -> {
                        ApplicationScheduledTask.MyMonthDay value = l.getValue();
                        if (value == null || numberField.getValue() == null) {
                            cronExpressionBuilderSelect.setHelperText("No valid schedule");
                            return;
                        }
                        try {
                            ApplicationScheduledTask.CronExpressionBuilder daily = f.getValue();
                            daily.setMonthDay(value.getDayOfMonth());
                            cronExpressionBuilderSelect.setHelperText(daily.getDescription());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    });


                    numberField.addValueChangeListener(l -> {
                        Month value = l.getValue();
                        if (value == null || numberField1.getValue() == null) {
                            cronExpressionBuilderSelect.setHelperText("No valid schedule");
                            return;
                        }
                        try {
                            ApplicationScheduledTask.CronExpressionBuilder daily = f.getValue();
                            daily.setMonth(value);
                            cronExpressionBuilderSelect.setHelperText(daily.getDescription());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });


        return formLayout;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new POSPrivilege(), Privileges.INSERT);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        business1.ifPresent(s -> business = s);
        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
        setBusiness2(businessService.get(Long.valueOf(business), AuthenticatedUser.token()));

        QueryParameters queryParams = event.getLocation().getQueryParameters();
        List<String> id1 = queryParams.getParameters().get("id");
        Optional<String> id = id1.stream().findAny();
        if (id.isEmpty()) {
            event.forwardTo(ReRouteLayout.class);
            throw new ValidationException("Invalid Link");
        }
        posHeaderId = Long.valueOf(id.get());
        setByPosHeaderId(event, posHeaderId);

    }

    protected void setByPosHeaderId(BeforeEnterEvent event, Long posHeaderId) {
        String token = AuthenticatedUser.token();
        InvoiceService loanRequestService = getBean(InvoiceService.class);
        invoice = loanRequestService.getByPosHeader(posHeaderId, token);
        if (invoice == null) {
            if (event != null) {
                event.forwardTo(ReRouteLayout.class);
            }
            throw new ValidationException("Invalid Link");
        }
        setInvoice(invoice);
    }

    protected void setInvoice(Invoice invoice) {
        InvoiceService loanRequestService = getBean(InvoiceService.class);
        invoiceSetting = loanRequestService.getSettings(business2.getId(), AuthenticatedUser.token());
        if (invoice.getRest().compareTo(BigDecimal.ZERO) == 0) {
            invoiceStatusBtn.setText("Close invoice");
            invoiceStatusBtn.setThemeName("tertiary success");
        } else if (invoice.getPaymentTransactions() != null && !invoice.getPaymentTransactions().isEmpty()) {
            invoiceStatusBtn.setVisible(false);
        }


        if (invoice.getMainInvoiceId_() != null) {
            invoiceViewScheduleLayout.setVisible(false);
            invoiceViewTitleH2.setText("Recurring Invoice # " + invoice.getInvoiceNumber());
        } else {
            invoiceViewTitleH2.setText("Invoice # " + invoice.getInvoiceNumber());
        }


        invoiceViewStatusSpan.getElement().getThemeList().remove("success");
        invoiceViewStatusSpan.getElement().getThemeList().remove("error");
        invoiceViewStatusSpan.getElement().getThemeList().remove("contrast");
        if (invoice.isFullyPayed()) {
            invoiceViewStatusSpan.setText(invoice.getStatus().getDisplay() + " : Paid");
            invoiceViewStatusSpan.getElement().getThemeList().add(UIUtil.Badge.PILL+" success");
            invoiceSummaryPaymentBtn.setVisible(false);
        } else {

            if (invoice.getPaymentDue().isBefore(LocalDate.now())) {
                invoiceViewStatusSpan.setText(invoice.getStatus().getDisplay() + " : Overdue");
                invoiceViewStatusSpan.getElement().getThemeList().add(UIUtil.Badge.PILL+" error");
            } else {
                invoiceViewStatusSpan.setText(invoice.getStatus().getDisplay() + " : Pending");
                invoiceViewStatusSpan.getElement().getThemeList().add(UIUtil.Badge.PILL+" contrast");
            }
            invoiceSummaryPaymentBtn.setVisible(true);
        }

        invoiceViewCustomerSpan.setText(invoice.getCustomer() == null ? "None" : (invoice.getCustomer().getName() + " " + invoice.getCustomer().getFirstName()));
        invoiceViewCustomerSpan.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.BOLD);

//        invoiceViewDueOnSpan.setText(invoice.getPaymentDue() == null ? "" : Constants.SIMPLE_DATE_FORMAT.format(invoice.getPaymentDue()));
        invoiceViewDueOnSpan.setText(invoice.getPaymentDue() + "");
        invoiceViewDueOnSpan.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.BOLD);

        invoiceViewAmountDueSpan.setText(invoice.getCurrencyTo().getCode() + " " + (invoice.getRest() == null ? "0.00" : Constants.CURRENCY_FORMAT.format(invoice.getRest())));
        invoiceViewAmountDueSpan.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.BOLD);


        if (invoice.getScheduledTasks() != null && !invoice.getScheduledTasks().isEmpty()) {
            setApplicationScheduledTask(invoice.getScheduledTasks().stream().findFirst().get());
        } else {
            invoiceViewEnableScheduleChk.setValue(false);
        }

        if (invoice.getStatus().compareTo(Invoice.Status.CANCELED) == 0 || invoice.getStatus().compareTo(Invoice.Status.CLOSED) == 0) {
            invoiceStatusBtn.setVisible(false);
            invoiceViewEditScheduleBtn.setVisible(false);
            invoiceViewStartScheduleBtn.setVisible(false);
            invoiceSummaryPaymentBtn.setVisible(false);
            invoiceViewEditInvoiceBtn.setVisible(false);
            settingsBtn.setVisible(false);
            invoiceViewEnableScheduleChk.setVisible(false);
        }

        isTransactionsLayout.loadCmb(invoice.getPaymentTransactions());

    }

    protected void setBusiness2(Business business) {
        business2 = business;
    }

}
