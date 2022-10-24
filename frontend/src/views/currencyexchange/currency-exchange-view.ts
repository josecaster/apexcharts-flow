import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/tabs/src/vaadin-tabs.js';
import '@vaadin/form-layout/src/vaadin-form-item.js';
import '@vaadin/select/src/vaadin-select.js';
import '@vaadin/flow-frontend/vaadin-big-decimal-field.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';

@customElement('currency-exchange-view')
export class CurrencyExchangeView extends LitElement {
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
 <vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); max-width:1000px; align-self: center;" class="shadow-s">
  <vaadin-form-layout style="width: 100%;" id="vaadinFormLayout">
   <vaadin-form-item>
    <vaadin-select id="currency-from-select" style="width: 100%;" required></vaadin-select>
    <label slot="label">One (1)</label>
   </vaadin-form-item>
   <vaadin-form-item>
    <vaadin-select id="currency-to-select" style="width: 100%;" required></vaadin-select>
    <label slot="label">Equals</label>
   </vaadin-form-item>
   <vaadin-form-item>
    <label slot="label">fx-rate</label>
    <vaadin-big-decimal-field id="from-amount-fld" style="width: 100%;"></vaadin-big-decimal-field>
   </vaadin-form-item>
   <vaadin-form-item>
    <label slot="label">One (1)</label>
    <vaadin-select value="Item one" id="currency-to--replica-select" style="width: 100%;" readonly></vaadin-select>
   </vaadin-form-item>
   <vaadin-form-item>
    <label slot="label">Equals</label>
    <vaadin-select value="Item one" id="currency-from--replica-select" style="width: 100%;" readonly></vaadin-select>
   </vaadin-form-item>
   <vaadin-form-item>
    <label slot="label">fx-rate</label>
    <vaadin-big-decimal-field id="to-amount-fld" style="width: 100%;"></vaadin-big-decimal-field>
   </vaadin-form-item>
  </vaadin-form-layout>
  <vaadin-horizontal-layout theme="spacing" style="width: 100%;" colspan="2">
   <vaadin-button style="align-self: center;margin-left:auto;" id="add-rate-btn" tabindex="0" theme="primary">
     Add rate 
   </vaadin-button>
  </vaadin-horizontal-layout>
  <vaadin-tabs style="align-self: stretch;" orientation="horizontal" selected="0" id="vaadinTabs"></vaadin-tabs>
  <div id="rate-grid-layout" style="align-self: stretch;"></div>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
