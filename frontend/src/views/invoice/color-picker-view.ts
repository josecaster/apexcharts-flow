import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/text-area/src/vaadin-text-area.js';
import '@vaadin/checkbox/src/vaadin-checkbox.js';

@customElement('color-picker-view')
export class ColorPickerView extends LitElement {
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
<vaadin-vertical-layout style="width: 100%; height: 100%;max-width:500px;">
 <vaadin-tabs id="is-tabs"></vaadin-tabs>
 <vaadin-form-layout>
  <vaadin-form-item>
   <label slot="label">Header primary color</label>
   <vaadin-text-field label="" placeholder="Color code" id="is-header-1" type="text"></vaadin-text-field>
  </vaadin-form-item>
  <vaadin-form-item>
   <label slot="label">Header secondary color</label>
   <vaadin-text-field label="" placeholder="Color code" id="is_header-2" type="text"></vaadin-text-field>
  </vaadin-form-item>
  <vaadin-form-item>
   <label slot="label">Footer primary color</label>
   <vaadin-text-field label="" placeholder="Color code" id="is-footer-1" type="text"></vaadin-text-field>
  </vaadin-form-item>
  <vaadin-form-item>
   <label slot="label">Notes/Terms</label>
   <vaadin-text-area label="" placeholder="Message" id="is-notes-terms" style="width: 100%;"></vaadin-text-area>
  </vaadin-form-item>
  <vaadin-form-item>
   <label slot="label">Footer message</label>
   <vaadin-text-area label="" placeholder="Message" id="is-footer-msg" style="width: 100%;"></vaadin-text-area>
  </vaadin-form-item>
 </vaadin-form-layout>
 <vaadin-checkbox id="is-all-chk" checked type="checkbox" value="on">
  Change for all upcomming invoices
 </vaadin-checkbox>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
