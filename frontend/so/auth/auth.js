import { LitElement, html, css } from 'lit'
import {property, customElement} from 'lit/decorators.js';

export class Auth extends LitElement {

    static get properties() {
        return {
            findDevice: String,
            challenge: String,
            credentialId: String,
            site: String,
            run: Boolean,
        };
    }

    render() {
        html`<span></span>`;
    }

    s2a(s) {
        return Int8Array.from(atob(s), c => c.charCodeAt(0));
    }

    a2s(a) {
        if(a == null) {
            return '';
        }
        if(a instanceof ArrayBuffer) {
            a = new Int8Array(a);
        }
        return JSON.stringify(a);
    }

    firstUpdated() {
        PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable()
            .then(b => this.$server.biometric(b))
            .catch(err => this.$server.biometric(false));
    }

    updated(changedProps) {
        if(changedProps.has('findDevice')) {
            this.$server.device(window.localStorage.getItem(this.findDevice));
            return;
        }
        if(changedProps.has('run')) {
            try {
                this._doAuth();
            } catch(err) {
                this.$server.debug("" + err);
            }
        }
    }

    async _doAuth() {
        const publicKeyCredentialRequestOptions = {
            challenge: this.s2a(this.challenge),
            rpId: this.site,
            allowCredentials: [{
                id: this.s2a(this.credentialId),
                type: 'public-key',
                transports: ['internal'],
            }],
            userVerification: "required",
            timeout: 60000,
        };
        try {
            const c = await navigator.credentials.get({
                publicKey: publicKeyCredentialRequestOptions
            });
            if(c) {
                const r = c['response'];
                this.$server.authenticated(c['id'], c['type'], this.a2s(r['authenticatorData']), this.a2s(r['clientDataJSON']), this.a2s(r['signature']), this.a2s(r['userHandle']));
                return;
            }
        } catch(err) {
            this.$server.debug("" + err);
        }
        this.$server.failed();
    }
}
customElements.define('so-auth', Auth);