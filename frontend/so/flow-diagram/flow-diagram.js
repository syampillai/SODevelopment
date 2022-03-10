import { LitElement, html, css, property, customElement } from 'lit-element'
import nomnoml from './nomnoml'

export class FlowDiagram extends LitElement {

    render() {
        return html`<canvas id="${this.idFD}"></canvas>`;
    }

    static get properties() {
      return {
      };
    }

    constructor() {
        super();
        this.idFD = null;
    }

    connectedCallback() {
        super.connectedCallback();
        this.$server.ready();
    }

    updateDef(definitions) {
        nomnoml.draw(this.shadowRoot.getElementById(this.idFD), definitions);
    }
}

customElements.define('so-diagram', FlowDiagram);
