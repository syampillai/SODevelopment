import { LitElement, html, css } from 'lit'
import {property, customElement} from 'lit/decorators.js';
import SignaturePad from 'signature_pad/dist/signature_pad.min'

export class SOSign extends LitElement {

    render() {
        return html`<canvas
        id="${this.idSign}"
        style="width:${this.width};height:${this.height};border:1px solid ${this.border};">
        </canvas>`;
    }

    static get properties() {
      return {
        width: { type: String },
        height: { type: String },
        person: { type: String },
        border: { type: String },
      };
    }

    constructor() {
        super();
        this.idSign = null;
        this.person = "Person";
        this.width = "600";
        this.height = "500";
        this.border = "blue";
    }

    firstUpdated() {
        this.signaturePad = new SignaturePad(this.shadowRoot.getElementById(this.idSign));
        this._person();
    }

    connectedCallback() {
        super.connectedCallback();
        this.$server.ready();
    }

    _person() {
        let canvas = this.shadowRoot.getElementById(this.idSign);
        let ctx = canvas.getContext('2d');
        ctx.font = "12px Arial";
        ctx.fillStyle = "black";
        ctx.textAlign = "center";
        ctx.fillText(this.person, canvas.width/2, canvas.height - 5);
    }

    clear() {
        this.signaturePad.clear();
        this._person();
    }

    load(signature) {
        this.signaturePad.fromDataURL(signature);
    }

    read() {
        if(this.signaturePad.isEmpty()) {
            this.$server.read("");
        } else {
            this.$server.read(this.signaturePad.toDataURL('image/png'));
        }
    }

    color(penColor, borderColor) {
        this.clear();
        this.signaturePad.penColor = penColor;
        this.border = borderColor;
    }
}

customElements.define('so-sign', SOSign);
