import { LitElement, html, css } from 'lit'
import {property, customElement} from 'lit/decorators.js';

export class FormSubmit extends LitElement {

    render() {
        return html`<form id="${this.idForm}" action="${this.site}" method="post"><slot></slot></form>`;
    }

    constructor() {
        super();
        this.idForm = null;
        this.site = "";
    }

    connectedCallback() {
        super.connectedCallback();
        this.$server.ready();
    }

    go() {
        this.shadowRoot.getElementById(this.idForm).submit();
    }
}

customElements.define('so-form-submit', FormSubmit);
