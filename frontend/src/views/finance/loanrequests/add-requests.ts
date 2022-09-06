import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import './requestor-form';
import './assets-form';
import './provision-form';
import './repayment-form';

@customElement('add-requests')
export class AddRequests extends LitElement {
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
<vaadin-vertical-layout style="background: #f6f6f7; padding: var(--lumo-space-m); width: 100%; flex-direction: column;">
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
  <vaadin-button style="align-self: center;" tabindex="0" id="back-button">
    . 
  </vaadin-button>
  <h2 id="add-product-title" style="flex-grow: 1; flex-shrink: 0; align-self: center;">Loan request</h2>
  <vaadin-button id="save-btn" style="align-self: center;" tabindex="0" theme="primary">
    Save 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <hr style="width: 100%;">
 <h3 style="padding: 0px; margin: 0px;">Request</h3>
 <p>Request a loan or register a requested loan which can further be provided</p>
 <div id="board-layout" style="align-self: stretch;"></div>
 <vaadin-form-layout id="form-layout">
  <requestor-form id="requestor-form" colspan="1"></requestor-form>
  <vaadin-form-layout colspan="2" style="width: 100%;">
   <assets-form id="assets-form" style="width: 100%;" colspan="1"></assets-form>
   <provision-form id="provision-form" style="width: 100%;" colspan="1"></provision-form>
   <repayment-form id="repayment-form" style="width: 100%;" colspan="2"></repayment-form>
  </vaadin-form-layout>
 </vaadin-form-layout>
</vaadin-vertical-layout>
`;
    }
    //<product-title style="align-self: stretch;" id="product-title-layout" colspan="2"></product-title>
      //   <product-price style="align-self: stretch;" id="product-price-layout" colspan="2"></product-price>
        // <product-inventory style="align-self: stretch;" id="product-inventory-layout" colspan="2"></product-inventory>

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
