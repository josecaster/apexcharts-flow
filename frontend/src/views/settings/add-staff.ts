import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/button/src/vaadin-button.js';

@customElement('add-staff')
export class AddStaff extends LitElement {
  static get styles() {
    return css`
      :host {
          display: block;
      }
      `;
  }

  render() {
    return html`
<vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m);">
 <h3 style="margin: 0px;" id="staff-count">Staff (0 of 2)</h3>
 <p style="margin: 0px;">Customize what your staff members can edit and access. You can add up to 2 staff members on this plan. </p>
 <vaadin-vertical-layout theme="spacing" id="staff-layout" style="align-self: stretch;"></vaadin-vertical-layout>
 <vaadin-button tabindex="0" theme="primary" id="add-staff">
   Add staff 
 </vaadin-button>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
