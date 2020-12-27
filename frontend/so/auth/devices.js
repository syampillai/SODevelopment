import { LitElement, html, property, customElement } from 'lit-element';

export class Device extends LitElement {

    static get properties() {
        return {
            : String,
        };
    }

    render() {
        html`<span></span>`;
    }

    updated(changedProps) {
        if(changedProps.has('device')) {
            this.$server.device(window.localStorage.getItem("device.user." + this.db));
        }
    }
}
customElements.define('so-device', Device);