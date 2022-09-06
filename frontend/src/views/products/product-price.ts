import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-form-layout/vaadin-form-item.js';
import '@vaadin/email-field/src/vaadin-email-field.js';
import '@vaadin/vaadin-form-layout/vaadin-form-layout.js';
import '@vaadin/vaadin-text-field/vaadin-number-field.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/icon/src/vaadin-icon.js';

@customElement('product-price')
export class ProductPrice extends LitElement {
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
  <vaadin-number-field label="Price" type="text" id="price"></vaadin-number-field>
  <vaadin-number-field label="Compare at price" id="compare-price" type="text"></vaadin-number-field>
  <hr colspan="2">
  <vaadin-number-field colspan="2" label="Cost per item" type="text" id="cost-per-item" helper-text="Customers won’t see this"></vaadin-number-field>
 </vaadin-form-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
