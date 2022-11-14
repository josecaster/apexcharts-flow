import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/text-area/src/vaadin-text-area.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';

@customElement('create-invoice-view')
export class CreateInvoiceView extends LitElement {
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
<vaadin-vertical-layout style="background: #f6f6f7; width: 100%; flex-direction: column; padding-right: var(--lumo-space-m); padding-left: var(--lumo-space-m);">
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
  <vaadin-button id="preview-btn" style="align-self: center;border: solid 1px; margin-left:auto;" tabindex="0" theme="tertiary">
    Preview 
  </vaadin-button>
  <vaadin-button id="invoice-save-continue-btn" style="align-self: center;" tabindex="0" theme="primary">
    Save and continue 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout id="div" style="align-self: center;">
  <vaadin-details id="new-invoice-header-details" style="align-self: stretch;border-radius:var(--lumo-border-radius); "></vaadin-details>
  <vaadin-vertical-layout id="new-invoice-main-layout" style="align-self: stretch;" class="my-cart-white">
   <vaadin-form-layout style="width: 100%;">
    <vaadin-vertical-layout theme="spacing" style="padding: var(--lumo-space-m);" colspan="1">
     <vaadin-vertical-layout theme="spacing" style="border: solid 1px; border-radius:var(--lumo-border-radius); border-color:var(--lumo-base-color); justify-content: center;" id="add-customer-layout"></vaadin-vertical-layout>
    </vaadin-vertical-layout>
    <vaadin-vertical-layout theme="spacing" style="align-self: flex-end; padding: var(--lumo-space-m);">
     <vaadin-horizontal-layout theme="spacing" style="width: 400px;">
      <label style="align-self: center; flex-grow: 1; flex-shrink: 0;text-align: end;">Invoice number</label>
      <vaadin-text-field id="invoice-number-fld" style="align-self: flex-end; "></vaadin-text-field>
     </vaadin-horizontal-layout>
     <vaadin-horizontal-layout theme="spacing" style="width: 400px;">
      <label style="align-self: center; flex-shrink: 0; flex-grow: 1;text-align: end;">P.O./S.O. number</label>
      <vaadin-text-field id="poso-number-fld" style="align-self: flex-end; "></vaadin-text-field>
     </vaadin-horizontal-layout>
     <vaadin-horizontal-layout theme="spacing" style="width: 400px;">
      <label style="align-self: center; flex-shrink: 0; flex-grow: 1;text-align: end;">Invoice date</label>
      <vaadin-date-picker id="payment-date-fld" style="align-self: flex-end; "></vaadin-date-picker>
     </vaadin-horizontal-layout>
     <vaadin-horizontal-layout theme="spacing" style="width: 400px;">
      <label style="align-self: center; flex-grow: 1; flex-shrink: 0;text-align: end;">Payment due</label>
      <vaadin-date-picker id="payment-due-fld" style="align-self: flex-end; "></vaadin-date-picker>
     </vaadin-horizontal-layout>
    </vaadin-vertical-layout>
   </vaadin-form-layout>
   <div id="invoice-table-layout" style="align-self: stretch;"></div>
   <vaadin-text-area label="Notes \ Terms" id="invoice-note-terms-fld" style="align-self: stretch; padding: var(--lumo-space-m);"></vaadin-text-area>
  </vaadin-vertical-layout>
  <vaadin-details id="new-invoice-footer-details" style="align-self: stretch; border-radius:var(--lumo-border-radius); "></vaadin-details>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
