import { LitElement, html, css } from 'lit'
import {property, customElement} from 'lit/decorators.js';

export class SOTemplate extends LitElement {

    render() {
        return html`<span id="${this.idTemplate}"><slot></slot></span>`;
    }

    constructor() {
        super();
        this.idTemplate = null;
    }

    connectedCallback() {
        super.connectedCallback();
        this.$server.ready();
    }
}

customElements.define('so-template', SOTemplate);
