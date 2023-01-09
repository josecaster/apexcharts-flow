import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/text-field/src/vaadin-text-field.js';

@customElement('my-search-field')
export class MySearchField extends LitElement {
  static get styles() {
    return css`
      :host {
          display: block;
      }
      `;
  }

  render() {
    return html`
<vaadin-text-field label="" placeholder="" id="my-filter" style="width: 100%;" type="text"></vaadin-text-field>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
