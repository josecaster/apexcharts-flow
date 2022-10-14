import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/radio-group/src/vaadin-radio-group.js';
import '@vaadin/checkbox/src/vaadin-checkbox.js';

@customElement('service-type')
export class ServiceType extends LitElement {
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
 <hr style="width: 100%;">
 <vaadin-radio-group theme="vertical" id="category-group"></vaadin-radio-group>
 <hr style="width: 100%;">
 <vaadin-checkbox id="service-track-inventory-chk" type="checkbox" value="on">
   Track inventory 
 </vaadin-checkbox>
 <vaadin-checkbox id="service-track-advanced-pricing-chk" type="checkbox" value="on">
   Advanced pricing 
 </vaadin-checkbox>
 <vaadin-checkbox id="service-track-tax-chk" type="checkbox" value="on">
   Charge Tax 
 </vaadin-checkbox>
 <vaadin-checkbox id="service-track-active-chk" type="checkbox" value="on">
   Active 
 </vaadin-checkbox>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
