import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/number-field/src/vaadin-number-field.js';

@customElement('pos-charge')
export class PosCharge extends LitElement {
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
<vaadin-vertical-layout style="width: 100%; height: 100%; align-items: center;">
 <h3 style="margin-bottom:0px;">SRD 0.00,-</h3>
 <p style="margin-top: 0px;">Total amount due</p>
</vaadin-vertical-layout>
<vaadin-number-field type="number" label="Received"></vaadin-number-field>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
