import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/combo-box/src/vaadin-combo-box.js';
import '@vaadin/button/src/vaadin-button.js';

@customElement('pos-view')
export class PosView extends LitElement {
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
<vaadin-vertical-layout style="width: 100%; flex-direction: column; background: var(--lumo-primary-color-50pct); height: 100%;">
 <vaadin-form-layout id="main-form-layout" style="padding: var(--lumo-space-s);">
  <vaadin-horizontal-layout colspan="3" ;theme="" style="border-radius:var(--lumo-border-radius); background:white; width: 100%; margin-top: var(--lumo-space-s); margin-right: var(--lumo-space-s); margin-left: var(--lumo-space-s);" theme="spacing">
   <vaadin-combo-box id="posHeaderCmb" style="margin-left:auto; align-self: center;"></vaadin-combo-box>
   <h5 style="align-self: center; margin-top:0px;" id="total-header-lbl">Total 24.00</h5>
   <vaadin-button style="align-self: center;" tabindex="0" theme="tertiary" id="save-btn">
     Save 
   </vaadin-button>
   <vaadin-button style="align-self: center; flex-grow: 0;" tabindex="0" theme="tertiary" id="charge-btn">
     Charge 
   </vaadin-button>
  </vaadin-horizontal-layout>
  <vaadin-form-layout colspan="2">
   <vaadin-vertical-layout style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); width: 100%;" colspan="2">
    <div id="radio-layout" style="align-self: center;"></div>
    <vaadin-combo-box placeholder="Filter products" id="filter-cmb" style="align-self: stretch;" type="text">
     <iron-icon icon="lumo:search" slot="prefix"></iron-icon>
    </vaadin-combo-box>
    <div id="board-layout" style="align-self: stretch;"></div>
   </vaadin-vertical-layout>
  </vaadin-form-layout>
  <vaadin-vertical-layout style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); width: 100%;" id="checkout-layout">
   <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
    <h2 id="product-title" style="flex-grow: 1;">Ticket</h2>
    <vaadin-button theme="icon" aria-label="Add new" id="variable-btn" style="align-self: center;" tabindex="0">
     <iron-icon icon="lumo:arrow-down"></iron-icon>
    </vaadin-button>
   </vaadin-horizontal-layout>
   <hr style="width: 100%;">
   <div id="items-layout" style="align-self: stretch;"></div>
   <hr style="width: 100%;">
   <div id="fee-layout" style="width: 100%;"></div>
   <hr style="width: 100%;">
   <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
    <label style="flex-grow: 1;"><b>Total</b></label>
    <label style="flex-grow: 0;" id="total-amount-footer-lbl"><b>24.00</b></label>
   </vaadin-horizontal-layout>
  </vaadin-vertical-layout>
 </vaadin-form-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
