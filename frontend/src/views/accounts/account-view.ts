import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/icon';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';
import '@vaadin/icons';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/button/src/vaadin-button.js';
import '@polymer/iron-icon/iron-icon.js';

@customElement('account-view')
export class AccountView extends LitElement {
  static get styles() {
    return css`
      :host {
          display: block;
      }
      `;
  }

  render() {
    return html`
<vaadin-vertical-layout style="width: 100%; padding: var(--lumo-space-xs);margin-bottom:15px;" class="my-cart-base">
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch; background:var(--lumo-base-color);">
  <h3 id="account-type" style="align-self: center;">Heading 3</h3>
  <vaadin-icon style="align-self: center; width: 18px; height: 18px;" icon="vaadin:question-circle-o" id="account-type-desc" size="16"></vaadin-icon>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout theme="spacing" id="records-layout" style="align-self: stretch;border-top: solid 1px;"></vaadin-vertical-layout>
 <vaadin-vertical-layout theme="spacing" style="align-self: stretch;border-top: solid 1px;">
  <vaadin-button id="add-account-btn" tabindex="0" theme="tertiary">
   <iron-icon icon="lumo:plus" slot="prefix"></iron-icon>Add a new account 
  </vaadin-button>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
