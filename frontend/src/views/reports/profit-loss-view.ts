import { LitElement, html, css, customElement } from 'lit-element';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/select/src/vaadin-select.js';
import '@vaadin/date-picker/src/vaadin-date-picker.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/tabs/src/vaadin-tabs.js';
import '@vaadin/tabs/src/vaadin-tab.js';

@customElement('profit-loss-view')
export class ProfitLossView extends LitElement {
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
<vaadin-vertical-layout theme="spacing" style="width: 100%; height: 100%; align-items: center;">
 <vaadin-vertical-layout theme="spacing-xl" style="max-width:1000px; align-items: center; align-self: center;">
  <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
   <vaadin-button id="export-btn" style="margin-left:auto; align-self: center; border: solid 1px; border-radius:100px; width: 125px;" tabindex="0" theme="tertiary">
    <iron-icon icon="lumo:arrow-down" slot="suffix" style="margin: 0px; padding: 0px;"></iron-icon>Export 
   </vaadin-button>
  </vaadin-horizontal-layout>
  <vaadin-vertical-layout theme="spacing" class="my-cart-base" style="align-self: stretch; padding: var(--lumo-space-m);">
   <vaadin-form-layout>
    <vaadin-horizontal-layout theme="spacing" style="align-items: flex-end;">
     <vaadin-select value="Item one" label="As of" id="as-of-date-select"></vaadin-select>
     <vaadin-date-picker id="as-of-date-picker"></vaadin-date-picker>
    </vaadin-horizontal-layout>
    <vaadin-horizontal-layout theme="spacing" style="align-items: flex-end;">
     <vaadin-select value="Item one" label="Report Type" id="report-type-select"></vaadin-select>
     <vaadin-button theme="primary" style="margin-left:auto; border: solid 1px; border-radius:100px;" tabindex="0" id="update-report-btn">
       Update Report 
     </vaadin-button>
    </vaadin-horizontal-layout>
   </vaadin-form-layout>
  </vaadin-vertical-layout>
  <vaadin-horizontal-layout theme="spacing" style="align-self: center; align-items: flex-end; justify-content: center;">
   <vaadin-vertical-layout style="align-self: flex-end; align-items: center; justify-content: center; font-weight:bold;">
    <span>Income</span>
    <h2 style="margin: 0px;" id="income-lbl">$171,393.55</h2>
   </vaadin-vertical-layout>
   <h2 style="font-weight:bold; color:var(--lumo-base-color); align-self: flex-end; margin: 0px;">-</h2>
   <vaadin-vertical-layout style="align-self: flex-end; align-items: center; justify-content: center; font-weight:bold;">
    <span>Cost of Goods Sold </span>
    <h2 style="margin: 0px;" id="cgs-lbl">$267,715.40</h2>
   </vaadin-vertical-layout>
   <h2 style="font-weight:bold; color:var(--lumo-base-color); align-self: flex-end; margin: 0px;">-</h2>
   <vaadin-vertical-layout style="align-self: flex-end; align-items: center; justify-content: center; font-weight:bold;">
    <span>Operating Expenses</span>
    <h2 style="margin: 0px;" id="oe-lbl">$0.00</h2>
   </vaadin-vertical-layout>
   <h2 style="font-weight:bold; color:var(--lumo-base-color); align-self: flex-end; margin: 0px;">=</h2>
   <vaadin-vertical-layout style="align-self: flex-end; align-items: center; justify-content: center; font-weight:bold;">
    <span>Net Profit</span>
    <h2 style="margin: 0px;color:var(--lumo-success-color);" id="total">$439,108.95</h2>
   </vaadin-vertical-layout>
  </vaadin-horizontal-layout>
  <vaadin-horizontal-layout theme="spacing">
   <vaadin-tabs id="report-tab" orientation="horizontal" selected="0">
    <vaadin-tab selected>
      Summary 
    </vaadin-tab>
    <vaadin-tab>
      Details 
    </vaadin-tab>
   </vaadin-tabs>
   <vaadin-tabs id="currency-tabs" orientation="horizontal" selected="0">
    <vaadin-tab selected>
      USD 
    </vaadin-tab>
    <vaadin-tab selected>
      SRD 
    </vaadin-tab>
    <vaadin-tab selected>
      Euro 
    </vaadin-tab>
   </vaadin-tabs>
  </vaadin-horizontal-layout>
  <div id="table-layout" style="width: 100%;"></div>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
   }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
