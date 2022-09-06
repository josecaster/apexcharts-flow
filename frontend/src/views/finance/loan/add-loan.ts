import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import './loan-form';

@customElement('add-loan')
export class AddLoan extends LitElement {
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
  <h2 id="add-product-title" style="flex-grow: 1; flex-shrink: 0; align-self: center;">Add loan structure</h2>
  <vaadin-button id="save-btn" style="align-self: center;" tabindex="0" theme="primary">
    Save 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <hr style="width: 100%;">
 <h3 style="padding: 0px; margin: 0px;">Loan</h3>
 <p>Add a loan structure that your company provides</p>
 <vaadin-vertical-layout theme="spacing" style="align-self: center;">
  <loan-form style="align-self: stretch; width: 600px;" id="loan-form" colspan="2"></loan-form>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
    }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
