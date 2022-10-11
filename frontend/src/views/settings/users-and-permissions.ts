import { LitElement, html, css, customElement } from 'lit-element';
import './store-owner';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import './add-staff';

@customElement('users-and-permissions')
export class UsersAndPermissions extends LitElement {
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
<vaadin-vertical-layout style="width: 100%; height: 100%; padding: var(--lumo-space-s); background: #f6f6f7;">
 <h2 id="company-name">Users and permissions</h2>
 <hr style="width: 100%;">
 <h3 style="padding: 0px; margin: 0px;" id="example-h3-bryan">Permissions</h3>
 <p>Manage what users can see or do in your business</p>
 <store-owner style="flex-shrink: 1; width: 100%; flex-grow: 0; " id="store-owner"></store-owner>
 <add-staff style="width: 100%;" id="add-staff"></add-staff>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
