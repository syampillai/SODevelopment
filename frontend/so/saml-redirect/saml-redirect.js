import { LitElement, html, css } from 'lit'
import {property, customElement} from 'lit/decorators.js';

export class RedirectSAML extends LitElement {

    render() {
        return html`<form id="${this.idSAML}" action="${this.site}" method="post">
        <input type="hidden" name="RelayState" value="${this.relayState}">
        <input type="hidden" name="SAMLResponse" value="${this.samlResponse}">
        </form>`;
    }

    constructor() {
        super();
        this.idSAML = null;
        this.relayState = "SO";
        this.samlResponse = "";
        this.site = "";
    }

    connectedCallback() {
        super.connectedCallback();
        this.$server.ready();
    }

    go() {
        this.shadowRoot.getElementById(this.idSAML).submit();
    }
}

customElements.define('so-redirect-saml', RedirectSAML);
