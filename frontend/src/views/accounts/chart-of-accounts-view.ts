import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/tabs/src/vaadin-tabs.js';

@customElement('chart-of-accounts-view')
export class ChartOfAccountsView extends LitElement {
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
<vaadin-vertical-layout style="height: 100%; padding: var(--lumo-space-s); align-items: center;" id="main-char-of-accounts-layout">
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
  <h2 style="flex-grow: 1;">Chart of accounts</h2>
  <vaadin-button style="align-self: center;" id="add-new-accounts-btn" tabindex="0" theme="primary">
    Add new accounts 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); flex-shrink: 1; flex-grow: 0; align-items: stretch; align-self: center;" id="chart-of-accounts-charts-layout">
  <vaadin-tabs style="align-self: center;" orientation="horizontal" id="chart-tab" selected="0"></vaadin-tabs>
  <div id="account-view-layout" style="align-self: stretch;"></div>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
