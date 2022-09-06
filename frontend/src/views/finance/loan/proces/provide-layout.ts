import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/select/src/vaadin-select.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/flow-frontend/vaadin-big-decimal-field.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/form-layout/src/vaadin-form-item.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/progress-bar/src/vaadin-progress-bar.js';

@customElement('provide-layout')
export class ProvideLayout extends LitElement {
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
<div style="width: 100%;">
 <h5>Provide funds</h5>
 <p>Providing funds is intended to credit de requester with the requested funds</p>
 <vaadin-form-layout id="vaadinFormLayout">
  <vaadin-form-item>
   <label slot="label">Currency</label>
   <vaadin-select value="Item one" id="currencySelect" style="width: 100%;" autofocus required></vaadin-select>
  </vaadin-form-item>
  <vaadin-form-item>
   <label slot="label" id="label">Amount</label>
   <vaadin-big-decimal-field id="amountFld" style="width: 100%;"></vaadin-big-decimal-field>
  </vaadin-form-item>
  <vaadin-form-item>
   <label slot="label">Progress</label>
   <vaadin-horizontal-layout theme="spacing">
    <vaadin-progress-bar id="loanProvideProgress"></vaadin-progress-bar>
    <label id="label1">0%25</label>
   </vaadin-horizontal-layout>
  </vaadin-form-item>
  <vaadin-button theme="primary" id="vaadinButton" tabindex="0">
    Record a payment 
  </vaadin-button>
 </vaadin-form-layout>
</div>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
