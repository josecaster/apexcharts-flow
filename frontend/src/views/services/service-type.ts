import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/combo-box/src/vaadin-combo-box.js';
import '@vaadin/radio-group/src/vaadin-radio-group.js';
import '@vaadin/radio-group/src/vaadin-radio-button.js';

@customElement('service-type')
export class ServiceType extends LitElement {
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
    <vaadin-vertical-layout style="padding-left: var(--lumo-space-m); border-radius:var(--lumo-border-radius); background:white; margin: var(--lumo-space-s); margin-top: var(--lumo-space-m); margin-right: var(--lumo-space-s); margin-bottom: var(--lumo-space-s); margin-left: var(--lumo-space-s); padding: var(--lumo-space-m);">
     <vaadin-combo-box id="status-cmb" style="align-self: stretch;"></vaadin-combo-box>
     <vaadin-radio-group theme="vertical" id="category-group">
      <vaadin-radio-button name="vaadin-radio-group-0" type="radio" value="on">
       <b>Physical product</b>
      </vaadin-radio-button>
      <vaadin-radio-button>
       <b>Physical product + service</b>
      </vaadin-radio-button>
      <vaadin-radio-button name="vaadin-radio-group-0" type="radio" value="on">
       <b>Dynamic product</b>
      </vaadin-radio-button>
      <vaadin-radio-button name="vaadin-radio-group-0" type="radio" value="on">
       <b>Dynamic product + service</b>
      </vaadin-radio-button>
     </vaadin-radio-group>
    </vaadin-vertical-layout>

    `;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
