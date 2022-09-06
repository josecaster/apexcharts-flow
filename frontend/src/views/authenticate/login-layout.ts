import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/login/src/vaadin-login-overlay-wrapper.js';
import '@vaadin/login/src/vaadin-login-form.js';
import '@vaadin/login/src/vaadin-login-form-wrapper.js';
import '@vaadin/login/src/vaadin-login-overlay.js';

@customElement('login-layout')
export class LoginLayout extends LitElement {
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
<vaadin-login-overlay></vaadin-login-overlay>
<vaadin-login-overlay-wrapper style="width: 100%; height: 100%;">
 <vaadin-login-form theme="with-overlay">
  <vaadin-login-form-wrapper theme="with-overlay"></vaadin-login-form-wrapper>
 </vaadin-login-form>
</vaadin-login-overlay-wrapper>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
