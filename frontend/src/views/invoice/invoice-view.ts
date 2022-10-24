import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/tabs/src/vaadin-tabs.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/vaadin-lumo-styles/utility';
import '@vaadin/tabs/src/vaadin-tab.js';

@customElement('invoice-view')
export class InvoiceView extends LitElement {
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
<vaadin-vertical-layout style="width: 100%; height: 100%; padding: var(--lumo-space-s);">
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
  <vaadin-button style="align-self: center;margin-left:auto;" id="add-invoice-btn" tabindex="0" theme="primary">
    Create an invoice 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); align-self: stretch;" class="shadow-s">
  <vaadin-text-field placeholder="Filter requests" id="filter-field" style="align-self: stretch;" type="text">
   <iron-icon icon="lumo:search" slot="prefix"></iron-icon>
  </vaadin-text-field>
  <vaadin-tabs style="align-self: center;" orientation="horizontal" selected="0">
   <vaadin-tab>
     All invoices 
   </vaadin-tab>
   <vaadin-tab selected disabled>
     Unpaid 
   </vaadin-tab>
   <vaadin-tab disabled>
     Paid 
   </vaadin-tab>
  </vaadin-tabs>
  <div id="invoice-grid-layout" style="align-self: stretch;"></div>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
