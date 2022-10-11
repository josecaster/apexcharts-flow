import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/vaadin-text-field/vaadin-number-field.js';
import '@vaadin/checkbox/src/vaadin-checkbox.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';

@customElement('product-inventory')
export class ProductInventory extends LitElement {
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
<vaadin-vertical-layout style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m);">
 <vaadin-form-layout style="flex-grow: 0; flex-shrink: 1; align-self: stretch;">
  <vaadin-text-field label="SKU" placeholder="Stock keeping unit" type="text" colspan="1" id="sku"></vaadin-text-field>
  <vaadin-text-field label="Barcode" placeholder="Barcode" type="text" colspan="1" id="barcode"></vaadin-text-field>
 </vaadin-form-layout>
 <vaadin-form-layout style="flex-grow: 0; flex-shrink: 1; align-self: stretch;">
  <vaadin-vertical-layout id="inventroy-grid-layout" colspan="1"></vaadin-vertical-layout>
  <vaadin-vertical-layout colspan="1" style="align-self: flex-start;">
   <vaadin-vertical-layout id="inventory-form-layout">
    <vaadin-form-layout style="flex-grow: 0; flex-shrink: 0;">
     <vaadin-number-field label="Quantity" placeholder="Amount of items" colspan="2" id="quantity-fld"></vaadin-number-field>
     <vaadin-checkbox id="continue-selling-chk" type="checkbox" value="on" checked>
       Continue selling when out of stock 
     </vaadin-checkbox>
     <vaadin-checkbox id="detailed-stock-chk" type="checkbox" value="on">
       Detailed stock 
     </vaadin-checkbox>
     <vaadin-horizontal-layout style="width: 100%;" colspan="2">
      <vaadin-button id="done-btn" style="margin-left:auto;" tabindex="0" theme="contrast">
        Done 
      </vaadin-button>
     </vaadin-horizontal-layout>
    </vaadin-form-layout>
   </vaadin-vertical-layout>
   <vaadin-vertical-layout id="inventory-detail-layout"></vaadin-vertical-layout>
  </vaadin-vertical-layout>
 </vaadin-form-layout>
 <vaadin-horizontal-layout class="footer" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 0; background-color:">
  <h5 id="total-amt-lbl" style="margin-left:auto;">Total amount of items (0)</h5>
 </vaadin-horizontal-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
