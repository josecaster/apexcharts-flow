import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/select/src/vaadin-select.js';

@customElement('toolbar-layout')
export class ToolbarLayout extends LitElement {
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
<vaadin-horizontal-layout class="content" style="width: 100%;">
 <vaadin-button theme="icon tertiary" aria-label="Add new" id="addBtn" tabindex="0">
  <iron-icon icon="lumo:plus"></iron-icon>
 </vaadin-button>
 <vaadin-button theme="icon error tertiary" aria-label="Add new" id="deleteBtn" tabindex="0">
  <iron-icon icon="lumo:cross"></iron-icon>
 </vaadin-button>
 <vaadin-button theme="icon tertiary" aria-label="Add new" id="saveBtn" tabindex="0">
  <iron-icon icon="lumo:upload"></iron-icon>
 </vaadin-button>
 <vaadin-button theme="icon tertiary success" aria-label="Add new" id="downloadBtn" tabindex="0">
  <iron-icon icon="lumo:download"></iron-icon>
 </vaadin-button>
 <vaadin-button theme="icon tertiary" aria-label="Add new" id="reloadBtn" tabindex="0">
  <iron-icon icon="lumo:reload"></iron-icon>
 </vaadin-button>
 <div style="margin-left: auto;">
  <vaadin-button theme="icon tertiary" aria-label="Add new" id="previousBtn" tabindex="0">
   <iron-icon icon="lumo:angle-left"></iron-icon>
  </vaadin-button>
  <span id="pagingSummary">50/120</span>
  <vaadin-button theme="icon tertiary" aria-label="Add new" tabindex="0" id="nextBtn">
   <iron-icon icon="lumo:angle-right"></iron-icon>
  </vaadin-button>
  <vaadin-select value="Item one" id="pagingSelect" style="width: 75px;" theme="small" placeholder="10"></vaadin-select>
 </div>
</vaadin-horizontal-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
