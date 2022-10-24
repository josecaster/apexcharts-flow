import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/combo-box/src/vaadin-combo-box.js';
import '@vaadin/button/src/vaadin-button.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';

@customElement('pos-view')
export class PosView extends LitElement {
  static get styles() {
    return css`
      :host {
          display: block;
          background: var(--lumo-primary-color-50pct);
      }
      `;
  }

  render() {
    return html`
<vaadin-vertical-layout style="width: 100%; flex-direction: column; height: 100%;">
 <vaadin-form-layout id="main-form-layout" style="padding: var(--lumo-space-s);">
  <vaadin-horizontal-layout colspan="3" style="border-radius:var(--lumo-border-radius); background:white; width: 100%; margin-top: var(--lumo-space-s); margin-right: var(--lumo-space-s); margin-left: var(--lumo-space-s);" theme="spacing" id="top-bar-layout" class="shadow-s">
   <vaadin-combo-box id="posHeaderCmb" style="margin-left:auto; align-self: center;"></vaadin-combo-box>
   <h5 style="align-self: center; margin-top:0px;" id="total-header-lbl">Total 0.00</h5>
   <vaadin-button style="align-self: center;" tabindex="0" theme="tertiary" id="save-btn">
     Save 
   </vaadin-button>
   <vaadin-button style="align-self: center; flex-grow: 0;" tabindex="0" theme="tertiary" id="charge-btn">
     Charge 
   </vaadin-button>
  </vaadin-horizontal-layout>
  <vaadin-form-layout colspan="1">
   <vaadin-vertical-layout style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); width: 100%; height: 514px;" colspan="2" class="shadow-s">
    <div id="radio-layout" style="align-self: flex-start;">
     <h2>Items</h2>
    </div>
    <vaadin-combo-box placeholder="Filter products" id="filter-cmb" style="align-self: stretch;" type="text">
     <iron-icon icon="lumo:search" slot="prefix"></iron-icon>
    </vaadin-combo-box>
    <div id="board-layout" style="align-self: stretch;"></div>
   </vaadin-vertical-layout>
  </vaadin-form-layout>
  <vaadin-vertical-layout style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); width: 100%; height: 514px;" id="checkout-layout" class="shadow-s">
   <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
    <h2 id="product-title" style="flex-grow: 1;">Ticket</h2>
    <vaadin-button id="customer-btn" style="align-self: center;" tabindex="0" theme="tertiary">
      + Add Customer 
    </vaadin-button>
    <vaadin-button theme="primary success" aria-label="Add new" id="variable-btn" style="align-self: center;" tabindex="0">
      Save 
    </vaadin-button>
   </vaadin-horizontal-layout>
   <div id="items-layout" style="align-self: stretch;"></div>
   <div id="fee-layout" style="width: 100%;"></div>
  </vaadin-vertical-layout>
  <div style="align-self: stretch;" id="tickets-layout" colspan="3"></div>
 </vaadin-form-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
