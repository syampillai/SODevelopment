import { LitElement, html, css } from 'lit'
import {property, customElement} from 'lit/decorators.js';

export class BrowserMessage extends LitElement {

    render() {
        return html`
            <span id="span"></span>
        `;
    }

    connectedCallback() {
        super.connectedCallback();
        this.$server.ready();
        window.addEventListener('message', this._message);
    }

    disconnectedCallback() {
        window.removeEventListener('message', this._message);
        super.disconnectedCallback();
    }

    _message = (event) => {
        this.$server.message(event.origin, JSON.stringify(event.data));
    }

}

customElements.define('so-browser-message', BrowserMessage);
