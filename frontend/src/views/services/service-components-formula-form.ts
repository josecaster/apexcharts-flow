import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/email-field/src/vaadin-email-field.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/form-layout/src/vaadin-form-item.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/checkbox/src/vaadin-checkbox.js';

@customElement('service-components-formula-form')
export class ServiceComponentsFormulaForm extends LitElement {
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
<vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:#f6f6f7; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); width: 100%;">
 <vaadin-form-layout>
  <vaadin-form-item>
   <label slot="label">Code</label>
   <vaadin-text-field type="text" id="formula-code-fld"></vaadin-text-field>
  </vaadin-form-item>
  <vaadin-form-item>
   <label slot="label">Name</label>
   <vaadin-text-field type="text" id="formula-name-fld"></vaadin-text-field>
  </vaadin-form-item>
  <vaadin-form-item>
   <label slot="label">Visible customers</label>
   <vaadin-checkbox id="formula-visible-customers-chk" type="checkbox" value="on"></vaadin-checkbox>
  </vaadin-form-item>
  <vaadin-form-item>
   <label slot="label">Active</label>
   <vaadin-checkbox type="checkbox" value="on" id="formula-active-chk"></vaadin-checkbox>
  </vaadin-form-item>
 </vaadin-form-layout>
 <label>e.g. ([product_price]/50)+[service_price]</label>
 <div id="form-formula-layout" style="align-self: stretch;"></div>
 <vaadin-horizontal-layout theme="spacing" style="width: 100%;">
  <label style="align-self: center;">Use [ctrl+shift] to include predefined and user defined components</label>
  <vaadin-button id="formula-test-btn" style="align-self: center;margin-left:auto;" tabindex="0" theme="primary contrast">
   Test
  </vaadin-button>
 </vaadin-horizontal-layout>
 <vaadin-horizontal-layout theme="spacing" style="width: 100%;">
  <vaadin-button id="discard-formula-btn" style="margin-left:auto;" tabindex="0" theme="tertiary">
    Discard 
  </vaadin-button>
  <vaadin-button id="add-formula" tabindex="0">
    Add 
  </vaadin-button>
 </vaadin-horizontal-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
