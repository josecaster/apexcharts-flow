import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-form-layout/vaadin-form-item.js';
import '@vaadin/email-field/src/vaadin-email-field.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/icon/src/vaadin-icon.js';
import './service-components-formula-form';

@customElement('service-components')
export class ServiceComponents extends LitElement {
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
<h5 style="margin: var(--lumo-space-xs);">Pricing components</h5>
<vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m);">
 <service-components-formula-form id="service-components-formula-form" style="width: 100%;"></service-components-formula-form>
 <vaadin-horizontal-layout theme="spacing" id="action-layout" style="width: 100%;">
  <vaadin-button id="add-component" style="margin-left:auto;" tabindex="0">
    Add component 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <div id="grid-layout" style="width: 100%;"></div>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
