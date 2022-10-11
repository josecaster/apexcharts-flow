import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/email-field/src/vaadin-email-field.js';
import '@vaadin/vaadin-form-layout/vaadin-form-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/form-layout/src/vaadin-form-item.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/vaadin-form-layout/vaadin-form-item.js';
import '@vaadin/combo-box/src/vaadin-combo-box.js';

@customElement('service-form')
export class ServiceForm extends LitElement {
  static get styles() {
    return css`
      :host {
          display: block;
      }
      `;
  }

  render() {
    return html`
<vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); width: 100%;">
 <vaadin-form-layout style="flex-shrink: 1; padding: var(--lumo-space-xs); align-self: stretch;">
  <vaadin-form-item>
   <label slot="label">Item code</label>
   <vaadin-text-field placeholder="fill in a service code" style="flex-shrink: 0; flex-grow: 1; width: 100%;" type="text" id="code-fld"></vaadin-text-field>
  </vaadin-form-item>
  <vaadin-form-item>
   <label slot="label">Title</label>
   <vaadin-text-field placeholder="fill in a service name" style="width: 100%;" type="text" id="title-fld"></vaadin-text-field>
  </vaadin-form-item>
  <vaadin-form-item>
   <label slot="label">Type</label>
   <vaadin-combo-box style="flex-shrink: 0; flex-grow: 1; width: 100%;" placeholder="choose a suitable service type" id="type-cmb"></vaadin-combo-box>
  </vaadin-form-item>
 </vaadin-form-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
