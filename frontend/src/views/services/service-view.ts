import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/tabs/src/vaadin-tabs.js';
import '@vaadin/tabs/src/vaadin-tab.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/text-field/src/vaadin-text-field.js';

@customElement('service-view')
export class ServiceView extends LitElement {
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
<vaadin-vertical-layout style="width: 100%; height: 100%; padding: var(--lumo-space-s);">
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
  <h2 style="flex-grow: 1;">Services</h2>
  <vaadin-button id="export-btn" style="align-self: center;" tabindex="0" theme="tertiary">
    Export
  </vaadin-button>
  <vaadin-button style="align-self: center;" id="import-btn" tabindex="0" theme="tertiary">
    Import
  </vaadin-button>
  <vaadin-button style="align-self: center;" id="add-product-btn" tabindex="0" theme="primary">
   Add service
  </vaadin-button>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); align-self: stretch;">
  <vaadin-tabs style="align-self: stretch;" orientation="horizontal" selected="0">
   <vaadin-tab selected>
     All
   </vaadin-tab>
   <vaadin-tab>
     Active
   </vaadin-tab>
   <vaadin-tab>
    Inactive
   </vaadin-tab>
  </vaadin-tabs>
  <vaadin-text-field placeholder="Filter products" id="filter-field" style="align-self: stretch;" type="text">
   <iron-icon icon="lumo:search" slot="prefix"></iron-icon>
  </vaadin-text-field>
  <div id="product-grid-layout" style="align-self: stretch;"></div>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
