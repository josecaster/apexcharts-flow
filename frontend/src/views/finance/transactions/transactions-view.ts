import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/tabs/src/vaadin-tabs.js';
import '@vaadin/tabs/src/vaadin-tab.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/text-field/src/vaadin-text-field.js';

@customElement('transactions-view')
export class TransactionsView extends LitElement {
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
  <vaadin-button id="add-income-btn" style="margin-left:auto;align-self: center;border: solid 1px;" tabindex="0" theme="tertiary">
    Add income 
  </vaadin-button>
  <vaadin-button style="align-self: center;border: solid 1px;" id="add-expense-btn" tabindex="0" theme="tertiary">
    Add expense 
  </vaadin-button>
  <vaadin-button style="align-self: center;border: solid 1px;" id="more-btn" tabindex="0" theme="tertiary">
    More 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); align-self: stretch;" class="shadow-s">
  <vaadin-tabs style="align-self: stretch;" orientation="horizontal" selected="0" id="vaadinTabs">
   <vaadin-tab selected>
     All transactions 
   </vaadin-tab>
   <vaadin-tab disabled>
     Active 
   </vaadin-tab>
   <vaadin-tab disabled>
     Draft 
   </vaadin-tab>
   <vaadin-tab disabled>
     Archived 
   </vaadin-tab>
  </vaadin-tabs>
  <vaadin-text-field placeholder="Filter transactions" id="filter-field" style="align-self: stretch;" type="text">
   <iron-icon icon="lumo:search" slot="prefix"></iron-icon>
  </vaadin-text-field>
  <div id="transactions-grid-layout" style="align-self: stretch;"></div>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
