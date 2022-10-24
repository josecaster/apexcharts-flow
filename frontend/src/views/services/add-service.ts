import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import './service-type';
import './service-form';
import '../products/product-inventory';
import './service-price';
import './service-components';
import './service-formula';

@customElement('add-service')
export class AddService extends LitElement {
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
<vaadin-vertical-layout style="background: #f6f6f7; padding: var(--lumo-space-m); width: 100%; flex-direction: column;">
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch; margin:0px; padding: 0px;">
  <vaadin-button id="save-btn" style=" align-self: center; padding: 0px;  margin-left:auto;" tabindex="0" theme="primary">
    Save 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <hr style="width: 100%;">
 <vaadin-form-layout id="main-form-layout">
  <vaadin-form-layout colspan="2">
   <vaadin-form-item colspan="2">
    <service-form style="width: 100%;" id="service-form" colspan="2" class="shadow-s "></service-form>
    <label slot="label">Main</label>
   </vaadin-form-item>
   <vaadin-form-item colspan="2">
    <product-inventory id="service-inventory" style="width: 100%;" colspan="2" class="shadow-s"></product-inventory>
    <label slot="label">Inventory</label>
   </vaadin-form-item>
   <vaadin-form-item colspan="2">
    <service-price id="service-price" style="width: 100%;" colspan="2" class="shadow-s"></service-price>
    <label slot="label">Price</label>
   </vaadin-form-item>
   <vaadin-form-item id="ser-comp-layout" colspan="2">
    <service-components id="service-components" style="width: 100%;" colspan="2" class="shadow-s"></service-components>
    <label slot="label">Price components</label>
   </vaadin-form-item>
   <vaadin-form-item id="serv-formula-layout" colspan="2">
    <service-formula id="service-formula" style="width: 100%;" colspan="2" class="shadow-s"></service-formula>
    <label slot="label">Advanced price</label>
   </vaadin-form-item>
  </vaadin-form-layout>
  <service-type colspan="1" id="service-type"></service-type>
 </vaadin-form-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
