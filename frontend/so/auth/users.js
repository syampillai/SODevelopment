import { LitElement, html, property, customElement } from 'lit-element';

export class Users extends LitElement {

    static get properties() {
        return {
            db: String,
        };
    }

    render() {
        html`<span></span>`;
    }

    updated(changedProps) {
        if(changedProps.has('db')) {
            this.$server.users(window.localStorage.getItem("users." + this.db));
        }
    }
}
customElements.define('so-users', Users);