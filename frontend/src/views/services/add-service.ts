import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import './service-form';
import './service-price';
import './service-components';
import './service-formula';
import './service-type';

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
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
  <vaadin-button style="align-self: center;" tabindex="0" id="back-button">
    . 
  </vaadin-button>
  <h2 id="add-product-title" style="flex-grow: 1; flex-shrink: 0; align-self: center;">Add service</h2>
  <vaadin-button id="save-btn" style="align-self: center;" tabindex="0" theme="primary">
    Save 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <hr style="width: 100%;">
 <h3 style="padding: 0px; margin: 0px;">Service</h3>
 <p>Register a service that your business provides whether it's recurring or one time</p>
 <vaadin-form-layout id="main-form-layout">
  <vaadin-form-layout colspan="2">
   <service-form style="width: 100%;" id="service-form" colspan="2"></service-form>
   <service-price id="service-price" style="width: 100%;" colspan="2"></service-price>
   <service-components id="service-components" style="width: 100%;" colspan="2"></service-components>
   <service-formula id="service-formula" style="width: 100%;" colspan="2"></service-formula>
  </vaadin-form-layout>
  <service-type colspan="1"></service-type>
 </vaadin-form-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
