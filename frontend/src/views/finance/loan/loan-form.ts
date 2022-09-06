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
import '@vaadin/form-layout/src/vaadin-form-item.js';
import '@vaadin/vaadin-text-field/vaadin-number-field.js';
import '@vaadin/checkbox/src/vaadin-checkbox.js';
import '@vaadin/select/src/vaadin-select.js';

@customElement('loan-form')
export class LoanForm extends LitElement {
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
<vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m);">
 <h5 style="margin: var(--lumo-space-xs);">Pricing</h5>
 <vaadin-form-layout style="flex-shrink: 1; padding: var(--lumo-space-xs);">
  <vaadin-text-field error-message="Please enter a value" required invalid="" label="Loan name" id="loan-name-fld" type="text"></vaadin-text-field>
  <vaadin-select value="Item one" id="currency-cmb"></vaadin-select>
  <vaadin-form-item colspan="2">
   <label slot="label">Duration</label>
   <div id="duration-layout"></div>
  </vaadin-form-item>
  <vaadin-number-field label="Factor type" type="text" id="factor-type-fld" required error-message="Please enter a value"></vaadin-number-field>
  <vaadin-checkbox id="fixed-loan-chk" type="checkbox" value="on">
   This is a fixed loan
  </vaadin-checkbox>
  <hr colspan="2">
  <label colspan="2">Choose your options wise because you cannot change the options ones already being used.</label>
 </vaadin-form-layout>
</vaadin-vertical-layout>
`;
    }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
