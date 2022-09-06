import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '../../../components/toolbar-layout';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/form-layout/src/vaadin-form-item.js';
import '@vaadin/checkbox/src/vaadin-checkbox.js';
import '@vaadin/grid/src/vaadin-grid.js';
import '@vaadin/flow-frontend/vaadin-big-decimal-field.js';
import '@vaadin/button/src/vaadin-button.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/text-area/src/vaadin-text-area.js';

@customElement('input-layout')
export class InputLayout extends LitElement {
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
<vaadin-vertical-layout style="width: 100%; height: 100%;" id="vaadinVerticalLayout">
 <vaadin-button theme="icon" aria-label="Add new" id="saveBtn" tabindex="0">
  <iron-icon icon="lumo:plus"></iron-icon>Save
 </vaadin-button>
 <vaadin-horizontal-layout theme="spacing" style="flex-grow: 0; align-self: stretch;">
  <vaadin-form-layout style="flex-grow: 1; flex-shrink: 0;">
   <vaadin-form-item>
    <label slot="label" id="valueLbl">Value</label>
    <vaadin-big-decimal-field id="valueAmountFld" style="width: 100%;"></vaadin-big-decimal-field>
   </vaadin-form-item>
   <vaadin-form-item>
    <label slot="label" id="memoLbl">Memo</label>
    <vaadin-text-area placeholder="Add detailed explanation" id="memoFld" style="width: 100%;"></vaadin-text-area>
   </vaadin-form-item>
   <vaadin-checkbox id="assetValidChk" type="checkbox" value="on">
     Valid 
   </vaadin-checkbox>
  </vaadin-form-layout>
 </vaadin-horizontal-layout>
 <toolbar-layout style="width: 100%; flex-shrink: 1; flex-grow: 0; height: 50px;" id="toolbarLayout"></toolbar-layout>
 <vaadin-grid items="[[items]]" id="fileGrid" style="flex-grow: 0; flex-shrink: 0;" is-attached all-rows-visible></vaadin-grid>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
