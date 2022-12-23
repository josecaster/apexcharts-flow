import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-form-layout/vaadin-form-item.js';
import '@vaadin/vaadin-form-layout/vaadin-form-layout.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/combo-box/src/vaadin-combo-box.js';
import '@vaadin/details/src/vaadin-details.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/upload/src/vaadin-upload.js';
import '@vaadin/text-area/src/vaadin-text-area.js';
import '@vaadin/number-field/src/vaadin-number-field.js';
import '@vaadin/select/src/vaadin-select.js';

@customElement('assets-form')
export class AssetsForm extends LitElement {
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
<vaadin-vertical-layout theme="spacing" style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m); height: 100%;" class="shadow-s">
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
  <h5 style="margin: var(--lumo-space-xs); flex-grow: 1; align-self: center;">Assets</h5>
  <vaadin-button id="approve-assets-btn" style="align-self: center;" tabindex="0" hidden>
    Approve assets 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <vaadin-form-layout style="flex-shrink: 1; padding: var(--lumo-space-xs);">
  <h3 colspan="3" id="total-asset-lbl">Total asset value 0.00 SRD</h3>
  <vaadin-details id="add-assets-detail" style="width: 100%;" tabindex="0" opened>
   <vaadin-form-layout style="border-radius:var(--lumo-border-radius); background:#f6f6f7; padding: var(--lumo-space-s);" colspan="3">
    <vaadin-upload id="upload-btn" style="width: 100%;" colspan="3"></vaadin-upload>
    <vaadin-text-area id="asset-memo-fld" colspan="3" placeholder="Write a memo"></vaadin-text-area>
    <vaadin-number-field id="asset-value-fld1" type="number" placeholder="Asset value"></vaadin-number-field>
    <vaadin-select id="asset-currency-cmb1"></vaadin-select>
    <vaadin-select id="asset-type-cmb" placeholder="Choose a Asset Type"></vaadin-select>
    <vaadin-horizontal-layout theme="spacing" colspan="3">
     <vaadin-button style="margin-left:auto;" tabindex="0" theme="tertiary" id="discard-assets-btn">
       discard 
     </vaadin-button>
     <vaadin-button tabindex="0" theme="primary success" id="add-assets-btn">
       add assets 
     </vaadin-button>
    </vaadin-horizontal-layout>
   </vaadin-form-layout>
  </vaadin-details>
 </vaadin-form-layout>
 <vaadin-details id="view-assets-detail" style="width: 100%;" tabindex="0">
  <div id="assets-grid-layout" style="width: 100%;"></div>
 </vaadin-details>
</vaadin-vertical-layout>
`;
      }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
