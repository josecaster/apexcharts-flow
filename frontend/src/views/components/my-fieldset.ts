import { LitElement, html, css, customElement } from 'lit-element';
import './my-legend';

@customElement('my-fieldset')
export class MyFieldset extends LitElement {
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
<fieldset style="border-width: thin; border-style: dashed; border-color: var(--lumo-shade-10pct); border-image: initial; border-radius: calc(var(--lumo-size-m) / 4); margin: 0px; padding-left: 0px; width: 100%; padding-right: 0px;">
 <div id="div"></div>
 <legend style="color: var(--lumo-secondary-text-color); font-size: var(--lumo-font-size-xs);" id="legend">Caption</legend>
</fieldset>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
