import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/checkbox/src/vaadin-checkbox.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/radio-group/src/vaadin-radio-group.js';

@customElement('invoice-summary-view')
export class InvoiceSummaryView extends LitElement {
  static get styles() {
    return css`
      :host {
          display: block;
          height: 100%;
      }
      `;
  }

  render() {
    return html`
<vaadin-vertical-layout style="width: 100%; height: 100%; font-weight:bold !important;" id="invoice-view-main-layout">
 <vaadin-horizontal-layout theme="spacing" style="align-items: center; justify-content: center;">
  <h2 id="invoice-view-title-h2">Invoice #</h2>
  <vaadin-button id="invoice-status-btn" style="align-self: center;border: solid 1px;border-radius:100px;" tabindex="0" theme="tertiary error">
   Cancel invoice
  </vaadin-button>
 </vaadin-horizontal-layout>
 <hr style="align-self: stretch; width: 100%;">
 <vaadin-form-layout id="invoice-summary-dashboard" style="align-self: center; width: 100%;">
  <vaadin-form-layout>
   <vaadin-vertical-layout theme="spacing">
    <label>Status</label>
    <span id="invoice-view-status-span">Span</span>
   </vaadin-vertical-layout>
   <vaadin-vertical-layout theme="spacing">
    <label>Customer</label>
    <span id="invoice-view-customer-span">Span</span>
   </vaadin-vertical-layout>
  </vaadin-form-layout>
  <vaadin-form-layout>
   <vaadin-vertical-layout theme="spacing">
    <label>Amount due</label>
    <span id="invoice-view-amount-due-span">Span</span>
   </vaadin-vertical-layout>
   <vaadin-vertical-layout theme="spacing">
    <label>Due on</label>
    <span id="invoice-view-due-on-span">Span</span>
   </vaadin-vertical-layout>
  </vaadin-form-layout>
 </vaadin-form-layout>
 <vaadin-vertical-layout theme="spacing" class="my-cart-white" style="padding: var(--lumo-space-m); align-self: center; width: 100%;" id="invoice-view-create-layout">
  <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
   <span id="invoice-view-create-icon-span" style="align-self: center;"></span>
   <label style="align-self: center;">Create</label>
   <vaadin-horizontal-layout theme="spacing" id="invoice-view-create-action-layout" style="flex-grow: 0; flex-shrink: 1; width: 100%; justify-content: flex-end; align-self: center;">
    <vaadin-button style="align-self: center;border: solid 1px;border-radius:100px;" theme="tertiary" id="invoice-view-edit-invoice-btn" tabindex="0">
      Edit invoice 
    </vaadin-button>
   </vaadin-horizontal-layout>
  </vaadin-horizontal-layout>
  <vaadin-vertical-layout theme="spacing" style="align-self: stretch;" id="invoice-view-create-content"></vaadin-vertical-layout>
 </vaadin-vertical-layout>
 <vaadin-vertical-layout theme="spacing" id="invoice-view-schedule-layout" style="align-self: center; width: 100%; padding: var(--lumo-space-m);max-width:1000px;" class="my-cart-white">
  <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
   <span style="align-self: center;" id="invoice-view-schedule-icon-span"></span>
   <label style="align-self: center;">Schedule</label>
   <vaadin-horizontal-layout theme="spacing" style="align-self: center; width: 100%; justify-content: flex-end;">
    <vaadin-checkbox style="align-self: center;" id="invoice-view-enable-schedule-chk" type="checkbox" value="on">
      Recurring invoice 
    </vaadin-checkbox>
    <vaadin-button id="invoice-view-edit-schedule-btn" style="align-self: center;border: solid 1px;border-radius:100px;" tabindex="0" theme="tertiary contrast">
      Edit schedule 
    </vaadin-button>
    <vaadin-button style="align-self: center;border: solid 1px;border-radius:100px;" tabindex="0" theme="tertiary contrast" id="invoice-view-start-schedule-btn">
      Save schedule 
    </vaadin-button>
   </vaadin-horizontal-layout>
  </vaadin-horizontal-layout>
  <vaadin-form-layout>
   <vaadin-form-item colspan="2">
    <label slot="label">Start recurring invoices</label>
    <vaadin-vertical-layout theme="spacing" style="align-self: stretch;" id="invoice-view-schedule-content"></vaadin-vertical-layout>
   </vaadin-form-item>
   <vaadin-form-item id="invoice-view-end-form-item">
    <label slot="label">End recurring invoices</label>
    <vaadin-vertical-layout theme="spacing" id="invoice-view-schedule-end" style="align-self: stretch;">
     <vaadin-radio-group id="invoice-view-end-radio-group"></vaadin-radio-group>
     <span id="invoice-view-end-span">Span</span>
    </vaadin-vertical-layout>
   </vaadin-form-item>
  </vaadin-form-layout>
 </vaadin-vertical-layout>
 <vaadin-vertical-layout theme="spacing" class="my-cart-white" style="align-self: center; padding: var(--lumo-space-m); width: 100%;" id="invoice-view-send-layout">
  <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
   <span id="invoice-view-send-icon-span" style="align-self: center;"></span>
   <label style="align-self: center;">Send</label>
   <vaadin-horizontal-layout theme="spacing" id="invoice-view-send-action-layout" style="flex-grow: 0; flex-shrink: 1; width: 100%; justify-content: flex-end; align-self: center;">
    <vaadin-button style="align-self: center;border: solid 1px;border-radius:100px;" theme="tertiary" tabindex="0" id="invoice-summary-resend-invoice-btn">
      Send invoice 
    </vaadin-button>
    <vaadin-button style="align-self: center;border: solid 1px;border-radius:100px;" theme="tertiary" tabindex="0" id="invoice-summary-share-link-btn">
      Get share link 
    </vaadin-button>
    <div id="invoice-summary-download-btn-layout"></div>
   </vaadin-horizontal-layout>
  </vaadin-horizontal-layout>
  <vaadin-vertical-layout theme="spacing" style="align-self: stretch;" id="invoice-view-send-content"></vaadin-vertical-layout>
 </vaadin-vertical-layout>
 <vaadin-vertical-layout theme="spacing" class="my-cart-white" style="align-self: center; padding: var(--lumo-space-m); width: 100%;" id="invoice-view-get-paid-layout">
  <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
   <span id="invoice-view-get-paid-icon-span" style="align-self: center;"></span>
   <label style="align-self: center; flex-grow: 1; flex-shrink: 0;">Get paid</label>
   <vaadin-horizontal-layout theme="spacing" id="invoice-view-create-action-layout" style="flex-grow: 0; flex-shrink: 1; width: 100%; justify-content: flex-end; align-self: center;">
    <vaadin-button style="align-self: center;border: solid 1px;border-radius:100px;" theme="primary" tabindex="0" id="invoice-summary-payment-btn">
      Record a payment 
    </vaadin-button>
   </vaadin-horizontal-layout>
  </vaadin-horizontal-layout>
  <vaadin-vertical-layout theme="spacing" style="align-self: stretch;" id="invoice-view-get-paid-content"></vaadin-vertical-layout>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
