import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import './provision-form';
import './repayment-form';
import './requestor-form';
import './assets-form';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';

@customElement('add-requests')
export class AddRequests extends LitElement {
  static get styles() {
    return css`
      :host {
          display: block;
      }
      `;
  }

  render() {
      return html`
<vaadin-vertical-layout style="background: #f6f6f7; width: 100%; flex-direction: column; padding-right: var(--lumo-space-m); padding-left: var(--lumo-space-m);">
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
  <vaadin-button style="align-self: center;" tabindex="0" id="back-button">
    . 
  </vaadin-button>
  <h2 id="add-product-title" style="flex-grow: 1; flex-shrink: 0; align-self: center;margin:0px;">Loan request</h2>
  <span id="loan-request-status-span">Span</span>
  <vaadin-button id="save-btn" style="align-self: center;" tabindex="0" theme="primary">
    Save 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <hr style="width: 100%;margin:0px;">
 <h3 style="padding: 0px; margin: 0px;" id="new-request-lbl">Request</h3>
 <p id="new-request-paragraph">Request a loan or register a requested loan which can further be provided</p>
 <div id="board-layout" style="align-self: stretch;"></div>
 <vaadin-form-layout id="form-layout">
  <div colspan="1">
   <requestor-form id="requestor-form" colspan="1" style="margin-bottom: var(--lumo-space-m);"></requestor-form>
   <assets-form id="assets-form" style="width: 100%; height: 100%; margin-top: var(--lumo-space-l);" colspan="1"></assets-form>
  </div>
  <vaadin-form-layout colspan="2" style="width: 100%;" rowspan="3">
   <repayment-form id="repayment-form" style="width: 100%;" colspan="2"></repayment-form>
   <provision-form id="provision-form" style="width: 100%;" colspan="2"></provision-form>
  </vaadin-form-layout>
 </vaadin-form-layout>
 <div id="contract-layout" style="width: 100%; align-self: stretch;"></div>
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
  <vaadin-button id="generate-contract-btn" tabindex="0" style="flex-grow: 0; flex-shrink: 1; margin-left:auto;" theme="primary">
    Generate contract 
  </vaadin-button>
  <vaadin-button id="loan-request-cancel-btn" style="flex-grow: 0; flex-shrink: 1;" tabindex="0" theme="tertiary">
    Cancel 
  </vaadin-button>
  <vaadin-button style="flex-grow: 0; flex-shrink: 1;" tabindex="0" id="loan-request-archive-btn">
    Archive 
  </vaadin-button>
  <vaadin-button style="flex-grow: 0; flex-shrink: 1;" tabindex="0" id="loan-request-approve-btn" theme="success">
    Approve 
  </vaadin-button>
  <vaadin-button style="flex-grow: 0; flex-shrink: 1;" tabindex="0" id="loan-request-done-btn" theme="primary success">
    Done 
  </vaadin-button>
 </vaadin-horizontal-layout>
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
