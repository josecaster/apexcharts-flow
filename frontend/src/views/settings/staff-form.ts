import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-form-layout/vaadin-form-item.js';
import '@vaadin/email-field/src/vaadin-email-field.js';
import '@vaadin/vaadin-form-layout/vaadin-form-layout.js';
import '@vaadin/vaadin-text-field/vaadin-text-field.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/icon/src/vaadin-icon.js';

@customElement('staff-form')
export class StaffForm extends LitElement {
  static get styles() {
    return css`
      :host {
          display: block;
      }
      `;
  }

  render() {
    return html`
<vaadin-vertical-layout theme="spacing" style="width: 100%; height: 100%; border-radius:var(--lumo-border-radius); background:white;">
 <vaadin-form-layout style="flex-shrink: 1; padding: var(--lumo-space-m);">
  <vaadin-text-field label="First name" type="text" id="first-name"></vaadin-text-field>
  <vaadin-text-field label="Last name" id="last-name" type="text"></vaadin-text-field>
  <p colspan="2">Enter the staff member's first and last name as they appear on their government-issued ID.</p>
  <vaadin-text-field colspan="2" label="Email" type="text" id="email"></vaadin-text-field>
 </vaadin-form-layout>
 <hr style="width: 100%;">
 <vaadin-vertical-layout theme="spacing" style="width: 100%; padding: var(--lumo-space-m);">
  <vaadin-horizontal-layout theme="spacing" style="width: 100%;">
   <p colspan="2" style="align-self: center; flex-grow: 1;" id="permission-state-txt">This staff will have no permissions in this store.</p>
   <vaadin-button style="margin-right:auto; align-self: center;" tabindex="0" id="permission-state-btn">
     Select all 
   </vaadin-button>
  </vaadin-horizontal-layout>
  <vaadin-vertical-layout id="permission-form"></vaadin-vertical-layout>
 </vaadin-vertical-layout>
 <hr style="width: 100%;">
 <vaadin-horizontal-layout theme="spacing" style="padding: var(--lumo-space-m);">
  <vaadin-icon icon="vaadin:info-circle" style="align-self: center;"></vaadin-icon>
  <p style="align-self: center;"> Learn more about permissions.</p>
 </vaadin-horizontal-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
