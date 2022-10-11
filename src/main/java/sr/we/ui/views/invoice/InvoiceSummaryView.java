package sr.we.ui.views.invoice;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.addon.stefan.clipboard.ClientsideClipboard;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.InvoiceService;
import sr.we.data.controller.UserAccessService;
import sr.we.data.report.MyReportEngine;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.Invoice;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.POSPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.ConfirmationDialog;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.ReRouteLayout;
import sr.we.ui.views.finance.transactions.TransactionDialog;

import javax.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

import static sr.we.ContextProvider.getBean;

/**
 * A Designer generated component for the invoice-summary-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Route(value = "invoice-view", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
@Tag("invoice-summary-view")
@JsModule("./src/views/invoice/invoice-summary-view.ts")
public class InvoiceSummaryView extends LitTemplate implements BeforeEnterObserver {

    private final Button invoiceSummaryDownloadBtn;
    @Id("invoice-view-main-layout")
    private VerticalLayout invoiceViewMainLayout;
    @Id("invoice-view-title-h2")
    private H2 invoiceViewTitleH2;
    @Id("invoice-view-status-span")
    private Span invoiceViewStatusSpan;
    @Id("invoice-view-customer-span")
    private Span invoiceViewCustomerSpan;
    @Id("invoice-view-amount-due-span")
    private Span invoiceViewAmountDueSpan;
    @Id("invoice-view-due-on-span")
    private Span invoiceViewDueOnSpan;
    @Id("invoice-view-create-layout")
    private VerticalLayout invoiceViewCreateLayout;
    @Id("invoice-view-send-layout")
    private VerticalLayout invoiceViewSendLayout;
    @Id("invoice-view-get-paid-layout")
    private VerticalLayout invoiceViewGetPaidLayout;
    @Id("invoice-view-get-paid-content")

    private VerticalLayout invoiceViewGetPaidContent;
    @Id("invoice-view-send-content")
    private VerticalLayout invoiceViewSendContent;
    @Id("invoice-view-create-content")
    private VerticalLayout invoiceViewCreateContent;
    @Id("invoice-view-create-icon-span")
    private LineAwesomeIcon invoiceViewCreateIconSpan;
    @Id("invoice-view-send-icon-span")
    private LineAwesomeIcon invoiceViewSendIconSpan;
    @Id("invoice-view-get-paid-icon-span")
    private LineAwesomeIcon invoiceViewGetPaidIconSpan;
    @Id("invoice-view-edit-invoice-btn")
    private Button invoiceViewEditInvoiceBtn;
    @Id("invoice-summary-payment-btn")
    private Button invoiceSummaryPaymentBtn;
    private String business;
    private Business business2;
    private Invoice invoice;
    private Long posHeaderId;
    @Id("invoice-summary-share-link-btn")
    private Button invoiceSummaryShareLinkBtn;
    @Id("invoice-summary-resend-invoice-btn")
    private Button invoiceSummaryResendInvoiceBtn;
    @Id("invoice-summary-dashboard")
    private FormLayout invoiceSummaryDashboard;
    @Id("invoice-summary-download-btn-layout")
    private Div invoiceSummaryBtnLayout;

    /**
     * Creates a new InvoiceSummaryView.
     */
    public InvoiceSummaryView() {
        // You can initialise any data required for the connected UI components here.

        invoiceViewCreateIconSpan.icon("la la-plus-circle");
        invoiceViewSendIconSpan.icon("la la-paper-plane");
        invoiceViewGetPaidIconSpan.icon("la la-credit-card");

        invoiceViewCreateIconSpan.addClassName(LumoUtility.TextColor.PRIMARY);
        invoiceViewSendIconSpan.addClassName(LumoUtility.TextColor.PRIMARY);
        invoiceViewGetPaidIconSpan.addClassName(LumoUtility.TextColor.PRIMARY);

        invoiceViewEditInvoiceBtn.addClickListener(f -> {
            List<String> strings = Arrays.asList(invoice.getPosHeader().getId().toString());
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

            TransactionDialog transactionDialog = new TransactionDialog(invoice.getRest(), LocalDate.now(), business2.getId(), fromCurrency, selectedCurrency, reference, referenceId);
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
            TextField contentComponent = new TextField();
            contentComponent.setWidthFull();
            contentComponent.setValue(invoice.getCustomer() == null ? "" : //
                    (invoice.getCustomer().getPrimaryCustomerContacts() == null ? "" : //
                            (StringUtils.isBlank(invoice.getCustomer().getPrimaryCustomerContacts().getEmail()) ? "" : //
                                    invoice.getCustomer().getPrimaryCustomerContacts().getEmail())));
            ConfirmationDialog confirmationDialog = new ConfirmationDialog("Send email", "Confirm e-mailadres", contentComponent);
            confirmationDialog.open();
            confirmationDialog.getContinueBtn().addClickListener(c -> {
                String token = AuthenticatedUser.token();
                InvoiceService invoiceService = ContextProvider.getBean(InvoiceService.class);
                UI current = UI.getCurrent();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean emailSend = invoiceService.sendEmail(invoice.getId(), contentComponent.getValue(), token);

                        current.access(() -> {
                            if (emailSend) {
                                Notification notification = new Notification();
                                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                notification.setText(getTranslation("sr.we.success"));
                                notification.setDuration(5000);
                                notification.setPosition(Notification.Position.MIDDLE);
                                notification.open();
                            }
                        });
                    }
                }).start();
            });
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
                Notification.show("Sharable link copied");
            });
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidthFull();
            horizontalLayout.add(new Anchor(sharableLink,sharableLink,AnchorTarget.BLANK),component1);
            invoiceViewSendContent.add(horizontalLayout);
        });

        Anchor anchor = new Anchor("", invoiceSummaryDownloadBtn);
        anchor.setTarget(AnchorTarget.BLANK);
        anchor.setHref(new StreamResource("Invoice" + UUID.randomUUID() + ".pdf", new InputStreamFactory() {

            @Override
            public InputStream createInputStream() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("INVOICE_ID_", invoice.getId());
                try {
                    byte[] exportReportMap = ((MyReportEngine) ContextProvider.getBean(MyReportEngine.class)).exportInvoice(map);
                    return new ByteArrayInputStream(exportReportMap);
                } catch (JRException e) {
                    throw new RuntimeException(e);
                }
            }

        }));
        invoiceSummaryBtnLayout.add(anchor);
        invoiceViewCreateLayout.setMaxWidth("1000px");
        invoiceViewSendLayout.setMaxWidth("1000px");
        invoiceViewGetPaidLayout.setMaxWidth("1000px");
//        invoiceSummaryDashboard.setMaxWidth("1000px");
    }

    public static String getLocation(String business) {
        return MainLayout.getLocation(business) + "/invoice-view";
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

    private void setByPosHeaderId(BeforeEnterEvent event, Long posHeaderId) {
        String token = AuthenticatedUser.token();
        InvoiceService loanRequestService = getBean(InvoiceService.class);
        invoice = loanRequestService.get(posHeaderId, token);
        if (invoice == null) {
            if (event != null) {
                event.forwardTo(ReRouteLayout.class);
            }
            throw new ValidationException("Invalid Link");
        }
        setInvoice(invoice);
    }

    private void setInvoice(Invoice invoice) {

        invoiceViewTitleH2.setText("Invoice # " + invoice.getInvoiceNumber());

        invoiceViewStatusSpan.getElement().getThemeList().remove("success");
        invoiceViewStatusSpan.getElement().getThemeList().remove("error");
        invoiceViewStatusSpan.getElement().getThemeList().remove("contrast");
        if (invoice.isFullyPayed()) {
            invoiceViewStatusSpan.setText("Paid");
            invoiceViewStatusSpan.getElement().getThemeList().add("badge success");
            invoiceSummaryPaymentBtn.setVisible(false);
        } else {

            if (invoice.getPaymentDue().isBefore(LocalDate.now())) {
                invoiceViewStatusSpan.setText("Overdue");
                invoiceViewStatusSpan.getElement().getThemeList().add("badge error");
            } else {
                invoiceViewStatusSpan.setText("Pending");
                invoiceViewStatusSpan.getElement().getThemeList().add("badge contrast");
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
    }

    private void setBusiness2(Business business) {
        business2 = business;
    }
}
