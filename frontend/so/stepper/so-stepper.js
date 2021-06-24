import { LitElement, html, css, property, customElement } from 'lit-element'

export class SOStepper extends LitElement {

    static get styles() {
        return css `
            :host {
                display: flex;
                flex-direction: column;
            }

            .header {
                display: flex;
                justify-content: space-between;
                border-bottom: 1px solid var(--lumo-contrast-10pct);
                margin: var(--lumo-space-s) var(--lumo-space-s) 0 var(--lumo-space-s);
                padding-bottom: var(--lumo-space-s);
                overflow-x: auto;
                flex: 0 0 auto;
            }

            .content {
                display: flex;
                margin: var(--lumo-space-s) var(--lumo-space-s);
                overflow-y: auto;
                flex: 1 1 auto;
            }

            .footer {
                display: flex;
                justify-content: space-between;
                margin: var(--lumo-space-s) var(--lumo-space-s);
                flex: 0 0 auto;
            }

            .step-header {
                display: flex;
                flex-direction: column;
                position: relative;
                width: 100%;
                align-items: center;
            }

            .step-header:after {
                height: 1px;
                background: var(--lumo-primary-color);
                content: "";
                position: absolute;
                top: 18px;
                left: 0px;
                right: 0px;
            }

            .step-header:first-child:after {
                left: 50%;
                width: 50%;
            }

            .step-header:last-child:after {
                width: 50%;
            }

            .number-wrapper {
                padding-left: 10px;
                padding-right: 10px;
                background-color: var(--lumo-tint);
                z-index: 1;
            }

            .step-number {
                width: 30px;
                height: 30px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                border: 1px solid var(--lumo-primary-color);
                border-radius: 100%;
                background: var(--lumo-tint);
                color: var(--lumo-primary-color);
                margin: var(--lumo-space-xs) 0;
            }

            .step-title {
                font-size: 14px;
                max-width: 200px;
                overflow-wrap: break-word;
            }

            .step-header.completed .step-number {
                background: var(--lumo-primary-color);
                color: var(--lumo-tint);
            }

            .step-header.active .step-number:after {
                content: "";
                position: absolute;
                height: 34px;
                width: 34px;
                border: 1px solid var(--lumo-primary-color);
                border-radius: 100%;
                animation: current-step 1s infinite;
                animation-direction: alternate;
                animation-fill-mode: both;
            }

            @-webkit-keyframes current-step {
                0% {
                    transform: scale(1);
                }
                100% {
                    transform: scale(1.1);
                }
            }

            @-moz-keyframes current-step {
                0% {
                    transform: scale(1);
                }
                100% {
                    transform: scale(1.1);
                }
            }

            @-o-keyframes current-step {
                0% {
                    transform: scale(1);
                }
                100% {
                    transform: scale(1.1);
                }
            }

            @keyframes current-step {
                0% {
                    transform: scale(1);
                }
                100% {
                    transform: scale(1.1);
                }
            }
        `;
    }

    render() {
        return html`
            <div id="header" class="header"></div>
            <div id="content" class="content"></div>
            <div id="footer" class="footer"></div>
        `;
    }

    connectedCallback() {
        super.connectedCallback();
        this.$server.ready();
    }
}

customElements.define('so-stepper', SOStepper);

