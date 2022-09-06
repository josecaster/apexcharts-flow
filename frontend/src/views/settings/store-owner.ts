import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/avatar/src/vaadin-avatar.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';

@customElement('store-owner')
export class StoreOwner extends LitElement {
  static get styles() {
    return css`
      :host {
          display: block;
      }
      `;
  }

  render() {
    return html`
<div style="margin: var(--lumo-space-s); margin-top: var(--lumo-space-m);">
 <vaadin-vertical-layout style="width: 100%; background:white; border-radius:var(--lumo-border-radius); margin: 0px;">
  <vaadin-horizontal-layout class="header" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 0; padding-left: var(--lumo-space-m);">
   <h3 style="flex-grow: 1; flex-shrink: 0;">Store owner</h3>
   <a href="" style="align-self: center; margin-right:auto; flex-shrink: 0; flex-grow: 0; padding-right: var(--lumo-space-s);" id="transfer-ownership">Transfer ownership</a>
  </vaadin-horizontal-layout>
  <vaadin-vertical-layout class="content" style="width: 100%; flex-grow: 1; flex-shrink: 1; flex-basis: auto; padding: var(--lumo-space-m);">
   <vaadin-horizontal-layout theme="spacing" style="width: 100%;">
    <vaadin-avatar style="align-self: center;" id="avatar"></vaadin-avatar>
    <vaadin-vertical-layout theme="spacing" style="width: 100%; align-self: center;">
     <a href="https://vaadin.com" id="store-owner-link">Jose Caster</a>
     <p id="last-login">Last login was Monday, 25 July 2022 19:36 GMT-3</p>
    </vaadin-vertical-layout>
   </vaadin-horizontal-layout>
  </vaadin-vertical-layout>
  <vaadin-horizontal-layout class="footer" style="width: 100%; flex-basis: var(--lumo-size-l); flex-shrink: 1; background: var(--lumo-contrast-10pct); border-bottom-left-radius:var(--lumo-border-radius);border-bottom-right-radius:var(--lumo-border-radius);">
   <p style="padding-left: var(--lumo-space-s);">Store owners have some permissions that can't be assigned to staff. Learn more about</p>
   <a href="" style="align-self: center; padding-left: var(--lumo-space-s);" id="store-owner-permissions">store owner permissions.</a>
  </vaadin-horizontal-layout>
 </vaadin-vertical-layout>
</div>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
