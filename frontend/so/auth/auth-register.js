import { LitElement, html, property, customElement } from 'lit-element';

export class AuthReg extends LitElement {

    static get properties() {
        return {
            challenge: String,
            site: String,
            userId: String,
            userName: String,
            userDisplay: String,
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
        if(changedProps.has('run')) {
            try {
                this._register();
            } catch(err) {
                this.$server.debug("" + err);
            }
        }
    }

    async _register() {
        const publicKeyCredentialCreationOptions = {
            challenge: this.s2a(this.challenge),
            rp: {
                name: "SO Security",
                id: this.site,
            },
            user: {
                id: this.s2a(this.userId),
                name: this.userName,
                displayName: this.userDisplay,
            },
            pubKeyCredParams: [{alg: -7, type: "public-key"}],
            authenticatorSelection: {
                authenticatorAttachment: "platform",
                userVerification: "required",
            },
            timeout: 60000,
            attestation: "direct"
        };
        try {
            const c = await navigator.credentials.create({
                publicKey: publicKeyCredentialCreationOptions
            });
            if(c) {
                const r = c['response'];
                this.$server.registered(c['id'], c['type'], this.a2s(r['attestationObject']), this.a2s(r['clientDataJSON']));
                return;
            }
        } catch(err) {
            this.$server.debug("" + err);
        }
        this.$server.failed();
    }
}
customElements.define('so-auth-reg', AuthReg);