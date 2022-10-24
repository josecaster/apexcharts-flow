import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/email-field/src/vaadin-email-field.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/form-layout/src/vaadin-form-item.js';

@customElement('service-formula')
export class ServiceFormula extends LitElement {
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
<vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); width: 100%;" class="shadow-s">
 <label>e.g. ([product_price]/50)+[service_price]</label>
 <div id="formula-layout" style="align-self: stretch;"></div>
 <vaadin-horizontal-layout theme="spacing" style="width: 100%;">
  <label style="align-self: center;">Use [ctrl+shift] to include predefined and user defined components</label>
  <vaadin-button id="test-btn" style="align-self: center;margin-left:auto;" tabindex="0" theme="primary contrast">
    Test 
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
