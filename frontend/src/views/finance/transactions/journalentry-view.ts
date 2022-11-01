import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/date-picker/src/vaadin-date-picker.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/button/src/vaadin-button.js';

@customElement('journalentry-view')
export class JournalentryView extends LitElement {
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
<vaadin-vertical-layout style="width: 100%; height: 100%; padding: var(--lumo-space-m);">
 <vaadin-horizontal-layout theme="spacing">
  <vaadin-date-picker label="Date" style="align-self: flex-end;" id="transaction-date-picker"></vaadin-date-picker>
  <vaadin-text-field label="Description" id="transaction-description" type="text"></vaadin-text-field>
 </vaadin-horizontal-layout>
 <div id="journal-table-layout" style="align-self: stretch;"></div>
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
  <vaadin-button id="add-journal-entry-btn" style="align-self: center;border: solid 1px;border-radius:100px;" tabindex="0" theme="tertiary">
   Add line
  </vaadin-button>
  <span id="journal-entry-summary-span" style="margin-left:auto;"></span>
 </vaadin-horizontal-layout>
 <vaadin-horizontal-layout theme="spacing" style="align-self: stretch;">
  <vaadin-button id="delete-btn" style="align-self: center;border: solid 1px;border-radius:100px;" tabindex="0" theme="tertiary error">
   Delete
  </vaadin-button>
  <vaadin-button style="align-self: center;border: solid 1px;border-radius:100px;" tabindex="0" theme="tertiary" id="copy-btn">
   Copy
  </vaadin-button>
  <p id="last-updated-paragraph" style="margin-left:auto;">Journal entry last modified November 1st, 2022 </p>
 </vaadin-horizontal-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
