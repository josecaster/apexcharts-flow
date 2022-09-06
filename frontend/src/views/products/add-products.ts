import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import './product-title';
import './product-price';
import './product-type';
import './product-inventory';

@customElement('add-products')
export class AddProducts extends LitElement {
  static get styles() {
    return css`
      :host {
          display: block;
      }
      `;
  }


  render() {
    return html`
<vaadin-vertical-layout style="background: #f6f6f7; padding: var(--lumo-space-m); width: 100%; flex-direction: column;">
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
  <vaadin-button style="align-self: center;" tabindex="0" id="back-button">
    . 
  </vaadin-button>
  <h2 id="add-product-title" style="flex-grow: 1; flex-shrink: 0; align-self: center;">Add product</h2>
  <vaadin-button id="save-btn" style="align-self: center;" tabindex="0" theme="primary">
    Save 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <hr style="width: 100%;">
 <h3 style="padding: 0px; margin: 0px;">Product</h3>
 <p>Add your business's product weather it is a physical product, a service or both</p>
 <vaadin-form-layout id="form-layout">
  <vaadin-form-layout colspan="2">
   <product-title style="align-self: stretch;" id="product-title-layout" colspan="2"></product-title>
   <product-price style="align-self: stretch;" id="product-price-layout" colspan="2"></product-price>
   <product-inventory style="align-self: stretch;" id="product-inventory-layout" colspan="2"></product-inventory>
  </vaadin-form-layout>
  <product-type id="product-type" colspan="1"></product-type>
 </vaadin-form-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
