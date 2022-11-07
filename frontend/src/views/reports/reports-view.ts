import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';

@customElement('reports-view')
export class ReportsView extends LitElement {
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
<vaadin-vertical-layout style="width: 100%; height: 100%; align-items: center;">
 <vaadin-vertical-layout theme="spacing" style="max-width: 1000px; align-items: center; margin-top: var(--lumo-space-m);">
  <vaadin-form-layout class="my-cart" style="padding: var(--lumo-space-m);">
   <vaadin-vertical-layout>
    <h3 style="margin-bottom: 0px; margin: 0px;">Financial statements</h3>
    <p>Get a clear picture of how your business is doing. Use these core statements to better understand your financial health. </p>
   </vaadin-vertical-layout>
   <vaadin-vertical-layout theme="spacing">
    <vaadin-vertical-layout id="profit-loss" class="clickable-layout">
     <vaadin-horizontal-layout theme="spacing" style="align-self: stretch; align-items: center;">
      <h3 style="margin-bottom: 0px; margin: 0px;" class="text-primary">Profit &amp; Loss (Income Statement)</h3>
      <span style="align-self: center; margin-left:auto;font-weight:bold;">&gt;</span>
     </vaadin-horizontal-layout>
     <p>Shows your business’s net profit and summarizes your revenues and expenses in a given time period.</p>
     <hr style="align-self: stretch; width: 100%;">
    </vaadin-vertical-layout>
    <vaadin-vertical-layout id="balance-sheet" class="clickable-layout">
     <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
      <h3 style="margin-bottom: 0px; margin: 0px;" class="text-primary">Balance Sheet</h3>
      <span style="align-self: center; margin-left:auto;font-weight:bold;">&gt;</span>
     </vaadin-horizontal-layout>
     <p>A snapshot of your finances on a given day. The balance sheet calculates your business’s worth (equity) by subtracting what you owe (liabilities) from what you own (assets).</p>
     <hr style="align-self: stretch; width: 100%;">
    </vaadin-vertical-layout>
    <vaadin-vertical-layout id="cash-flow" class="clickable-layout">
     <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
      <h3 style="margin-bottom: 0px; margin: 0px;" class="text-primary">Cash Flow</h3>
      <span style="align-self: center; margin-left:auto;font-weight:bold;">&gt;</span>
     </vaadin-horizontal-layout>
     <p>Shows how much money is entering and leaving your business. The cash flow statement tells you how much cash you have on hand for a specific time period.</p>
     <hr style="align-self: stretch; width: 100%;">
    </vaadin-vertical-layout>
   </vaadin-vertical-layout>
  </vaadin-form-layout>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
