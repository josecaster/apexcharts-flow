package sr.we.ui.views.invoice;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.InvoiceService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Invoice;
import sr.we.shekelflowcore.entity.InvoiceSetting;
import sr.we.shekelflowcore.entity.helper.vo.InvoiceSettingVO;

/**
 * A Designer generated component for the color-picker-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("color-picker-view")
@JsModule("./src/views/invoice/color-picker-view.ts")
public class ColorPickerView extends LitTemplate {

    private final Tab invoice_settings;
    private final Tab system_settings;
    @Id("is-header-1")
    private TextField isHeader1;
    @Id("is_header-2")
    private TextField is_header2;
    @Id("is-footer-1")
    private TextField isFooter1;
    @Id("is-notes-terms")
    private TextArea isNotesTerms;
    @Id("is-footer-msg")
    private TextArea isFooterMsg;

    private InvoiceSetting systemSettings;
    private Invoice invoice;
    @Id("is-tabs")
    private Tabs isTabs;
    @Id("is-all-chk")
    private Checkbox isAllChk;

    /**
     * Creates a new ColorPickerView.
     */
    public ColorPickerView() {
        // You can initialise any data required for the connected UI components here.

        invoice_settings = new Tab("Invoice settings");
        system_settings = new Tab("System settings");
        isTabs.add(invoice_settings, system_settings);

        isTabs.addSelectedChangeListener(f -> {
            Tab selectedTab = f.getSelectedTab();
            select(selectedTab);
        });

        isFooter1.setMaxLength(64);
        isHeader1.setMaxLength(64);
        is_header2.setMaxLength(64);
        isNotesTerms.setMaxLength(512);
        isFooterMsg.setMaxLength(512);
    }

    private void select(Tab selectedTab) {
        if (selectedTab.equals(system_settings)) {
            isHeader1.setValue(StringUtils.isBlank(systemSettings.getHeaderColor1()) ? "" : systemSettings.getHeaderColor1());
            is_header2.setValue(StringUtils.isBlank(systemSettings.getHeaderColor2()) ? "" : systemSettings.getHeaderColor2());
            isFooter1.setValue(StringUtils.isBlank(systemSettings.getFooterColor1()) ? "" : systemSettings.getFooterColor1());
            isNotesTerms.setValue(StringUtils.isBlank(systemSettings.getThankMessage()) ? "" : systemSettings.getThankMessage());
            isFooterMsg.setValue(StringUtils.isBlank(systemSettings.getFooterMessage()) ? "" : systemSettings.getFooterMessage());
            isAllChk.setVisible(false);
        } else {
            isHeader1.setValue(StringUtils.isBlank(invoice.getHeaderColor1()) ? "" : invoice.getHeaderColor1());
            is_header2.setValue(StringUtils.isBlank(invoice.getHeaderColor2()) ? "" : invoice.getHeaderColor2());
            isFooter1.setValue(StringUtils.isBlank(invoice.getFooterColor1()) ? "" : invoice.getFooterColor1());
            isNotesTerms.setValue(StringUtils.isBlank(invoice.getThankMessage()) ? "" : invoice.getThankMessage());
            isFooterMsg.setValue(StringUtils.isBlank(invoice.getFooterMessage()) ? "" : invoice.getFooterMessage());
            isAllChk.setVisible(true);
        }
    }

    public void setValues(Invoice invoice, InvoiceSetting invoiceSetting) {
        this.invoice = invoice;
        this.systemSettings = invoiceSetting;
        select(isTabs.getSelectedTab());
    }

    public void save() {
        InvoiceSettingVO invoiceSettingVO = new InvoiceSettingVO();
        invoiceSettingVO.setId(this.systemSettings.getId());
        invoiceSettingVO.setFooterColor1(isFooter1.getValue());
        invoiceSettingVO.setHeaderColor1(isHeader1.getValue());
        invoiceSettingVO.setHeaderColor2(is_header2.getValue());
        invoiceSettingVO.setThankMessage(isNotesTerms.getValue());
        invoiceSettingVO.setFooterMessage(isFooterMsg.getValue());
        invoiceSettingVO.setSaveForAll(isAllChk.isVisible() && isAllChk.getValue());
        invoiceSettingVO.setInvoiceId(isTabs.getSelectedTab().equals(invoice_settings) ? invoice.getId() : null);
        InvoiceService invoiceService = ContextProvider.getBean(InvoiceService.class);
        invoiceService.setSettings(invoiceSettingVO, AuthenticatedUser.token());
    }
}
