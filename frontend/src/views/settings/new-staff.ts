import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import './staff-form';

@customElement('new-staff')
export class NewStaff extends LitElement {
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
<vaadin-vertical-layout style="background: #f6f6f7; padding: var(--lumo-space-m); width: 100%;">
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
  <vaadin-button style="align-self: center;" tabindex="0" id="back-button">
    . 
  </vaadin-button>
  <h2 id="company-name" style="flex-grow: 1; flex-shrink: 0; align-self: center;">Add staff</h2>
  <vaadin-button id="send-invite" style="align-self: center;" tabindex="0">
    Send invite 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <hr style="width: 100%;">
 <h3 style="padding: 0px; margin: 0px;">Staff</h3>
 <p>Give staff access to your store by sending them an invitation. If you’re working with a designer, developer, or marketer, find out how to give collaborator access to your store.</p>
 <staff-form id="staff-form" style="align-self: center;max-width:1000px;"></staff-form>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
