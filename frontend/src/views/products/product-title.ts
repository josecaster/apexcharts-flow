import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/text-area/src/vaadin-text-area.js';

@customElement('product-title')
export class ProductTitle extends LitElement {
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
<vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m);">
 <label>Title</label>
 <vaadin-text-field placeholder="fill in your product name" style="align-self: stretch;" type="text" id="title"></vaadin-text-field>
 <label>Description</label>
 <vaadin-text-area placeholder="Add detailed explanation" id="description" style="align-self: stretch;" maxlength="4000"></vaadin-text-area>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
