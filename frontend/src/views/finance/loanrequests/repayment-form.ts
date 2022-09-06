import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-form-layout/vaadin-form-item.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';

@customElement('repayment-form')
export class RepaymentForm extends LitElement {
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
<vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m);">
 <div style="align-self: stretch;">
  <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
   <h5 style="margin: var(--lumo-space-xs); flex-grow: 1; align-self: center;">Payment schedule</h5>
   <vaadin-button id="generate-payment-btn" style="align-self: center;" tabindex="0" theme="primary">
    Generate payment plan
   </vaadin-button>
  </vaadin-horizontal-layout>
  <label>Generate a payment plan for the requester and you will get the ability to record payments on top of the generated schedule</label>
  <div id="standard-schedule-layout" style="width: 100%;"></div>
  <hr style="width: 100%;">
 </div>
 <div id="extend-schedule-layout" style="width: 100%;"></div>
 <hr style="width: 100%;">
 <div id="balance-schedule-layout" style="width: 100%;"></div>
</vaadin-vertical-layout>
`;
    }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
