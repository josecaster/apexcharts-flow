import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/tabs/src/vaadin-tabs.js';
import '@vaadin/tabs/src/vaadin-tab.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '../../components/my-search-field';

@customElement('loan-view')
export class LoanView extends LitElement {
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
  <h2 style="flex-grow: 1;">Loans</h2>
  <vaadin-button id="export-btn" style="align-self: center;" tabindex="0" theme="tertiary">
    Export 
  </vaadin-button>
  <vaadin-button style="align-self: center;" id="import-btn" tabindex="0" theme="tertiary">
    Import 
  </vaadin-button>
  <vaadin-button style="align-self: center;" id="add-loan-btn" tabindex="0" theme="primary">
    Create loan 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); align-self: stretch; height: 100%;" class="shadow-s">
  <vaadin-tabs style="align-self: stretch;" orientation="horizontal" selected="0">
   <vaadin-tab selected>
     All 
   </vaadin-tab>
   <vaadin-tab>
     Active 
   </vaadin-tab>
   <vaadin-tab>
     Draft 
   </vaadin-tab>
   <vaadin-tab>
     Archived 
   </vaadin-tab>
  </vaadin-tabs>
  <my-search-field placeholder="Filter loans" id="filter-field" style="align-self: stretch;" type="text"></my-search-field>
  <div id="loans-grid-layout" style="align-self: stretch; height: 100%;"></div>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
