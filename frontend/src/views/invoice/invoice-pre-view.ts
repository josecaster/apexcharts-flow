import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/text-area/src/vaadin-text-area.js';

@customElement('invoice-pre-view')
export class InvoicePreView extends LitElement {
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
<vaadin-vertical-layout style="width: 100%; height: 100%; align-items: center;">
 <vaadin-vertical-layout theme="spacing" style="max-width:1000px; align-self: center; width: 100%;">
  <h2 id="invoice-preview-title-h2" style="align-self: center;">Title</h2>
  <vaadin-horizontal-layout theme="spacing" style="align-self: stretch; padding: var(--lumo-space-m);">
   <div id="invoice-preview-download-btn-layout" style="align-self: center;"></div>
   <span style="margin-left:auto;" id="invoice-preview-status-span">Span</span>
  </vaadin-horizontal-layout>
  <vaadin-vertical-layout theme="spacing" class="my-cart-white" style="align-self: stretch; padding: var(--lumo-space-m);" id="invoice-preview-report-layout">
   <vaadin-horizontal-layout theme="spacing" style="align-self: stretch; background: var(--lumo-primary-color-10pct); border-radius:10px; padding: var(--lumo-space-s);">
    <vaadin-vertical-layout>
     <h1 style="margin-bottom:0px;">Invoice</h1>
     <p id="invoice-preview-header-paragraph">Paragraph</p>
    </vaadin-vertical-layout>
    <vaadin-vertical-layout style="margin-left:auto; padding: var(--lumo-space-s);background: var(--lumo-primary-color-50pct);  color:white;">
     <p>Amount due</p>
     <h1 style="margin-bottom:0px;margin-top:0px;color:white;" id="invoice-preview-amount-due-header-h1">SRD 0.00</h1>
    </vaadin-vertical-layout>
   </vaadin-horizontal-layout>
   <vaadin-form-layout style="width: 100%;">
    <vaadin-form-layout>
     <label style="font-weight: bold;" colspan="2">Bill TO</label>
     <label id="invoice-preview-header-billto-lbl" colspan="2">Bill TO</label>
    </vaadin-form-layout>
    <vaadin-form-layout>
     <vaadin-form-item>
      <label style="font-weight:bold;">Invoice number:</label>
      <label id="invoicer-preview-invnumber-lbl">#</label>
     </vaadin-form-item>
     <vaadin-form-item>
      <label style="font-weight:bold;">P.O/S.O number:</label>
      <label id="invoicer-preview-poso-lbl">#</label>
     </vaadin-form-item>
     <vaadin-form-item>
      <label style="font-weight:bold;">Invoice date:</label>
      <label id="invoicer-preview-invdate-lbl">#</label>
     </vaadin-form-item>
     <vaadin-form-item>
      <label style="font-weight:bold;">Payment due:</label>
      <label id="invoicer-preview-paydue-lbl">#</label>
     </vaadin-form-item>
    </vaadin-form-layout>
   </vaadin-form-layout>
   <vaadin-vertical-layout theme="spacing" id="invoice-preview-table-layout" style="align-self: stretch;"></vaadin-vertical-layout>
   <vaadin-vertical-layout theme="spacing" id="invoice-preview-total-layout" style="align-self: stretch;"></vaadin-vertical-layout>
   <vaadin-text-area label="Notes/Terms" id="invoicer-preview-notes-ta" style="align-self: stretch;" readonly></vaadin-text-area>
   <label id="invoicer-preview-footer-lbl" style="align-self: center;">Footer</label>
   <hr style="width: 100%;">
   <label style="align-self: flex-start; font-weight:bold;" id="invoicer-preview-company-lbl">Footer</label>
  </vaadin-vertical-layout>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
