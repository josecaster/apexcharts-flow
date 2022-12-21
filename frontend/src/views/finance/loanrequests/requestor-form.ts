import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-form-layout/vaadin-form-item.js';
import '@vaadin/email-field/src/vaadin-email-field.js';
import '@vaadin/vaadin-form-layout/vaadin-form-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/combo-box/src/vaadin-combo-box.js';
import '@vaadin/date-picker/src/vaadin-date-picker.js';
import '@vaadin/number-field/src/vaadin-number-field.js';
import '@vaadin/avatar/src/vaadin-avatar.js';

@customElement('requestor-form')
export class RequestorForm extends LitElement {
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
<vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m);" class="shadow-s">
 <h5 style="margin: var(--lumo-space-xs);">Profile</h5>
 <vaadin-avatar id="avatar-fld"></vaadin-avatar>
 <vaadin-form-layout style="flex-shrink: 1; padding: var(--lumo-space-xs);">
  <vaadin-text-field label="Name" id="name-fld" type="text" required invalid></vaadin-text-field>
  <vaadin-text-field label="Firstname" type="text" id="firstname-fld" required></vaadin-text-field>
  <vaadin-combo-box id="customer-cmb" colspan="3"></vaadin-combo-box>
  <hr colspan="3">
  <vaadin-text-field label="Mobile number" type="text" id="mobile-fld"></vaadin-text-field>
  <vaadin-email-field id="email-fld" label="Email address" type="email"></vaadin-email-field>
  <hr colspan="3">
  <vaadin-combo-box id="loan-cmb" required label="Loan structure"></vaadin-combo-box>
  <vaadin-date-picker label="Request date" id="request-date-fld" required invalid></vaadin-date-picker>
  <vaadin-number-field id="duration-fld" type="number" required label="Duration"></vaadin-number-field>
  <vaadin-number-field type="number" id="amount-fld" required label="Loan amount" invalid></vaadin-number-field>
  <vaadin-checkbox id="intrest-first-chk" style="font-weight:bold; margin-top: var(--lumo-space-l);">
    I want to pay interest first and principle later 
  </vaadin-checkbox>
 </vaadin-form-layout>
</vaadin-vertical-layout>
`;
    }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
