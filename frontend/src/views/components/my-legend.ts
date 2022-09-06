import { LitElement, html, css, customElement } from 'lit-element';

@customElement('my-legend')
export class MyLegend extends LitElement {
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

    <legend style="color: var(--lumo-secondary-text-color); font-size: var(--lumo-font-size-xs);" id="legend">Caption</legend>
    `;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
//   createRenderRoot() {
//     return this;
//   }
}
