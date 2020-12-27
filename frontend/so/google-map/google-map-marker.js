import {PolymerElement} from '@polymer/polymer/polymer-element.js';
import {html} from '@polymer/polymer/lib/utils/html-tag.js';

class GoogleMapMarker extends PolymerElement {

    static get template() {
        return html'
            <style>
                :host {
                display: none;
            }
            </style>
            <slot></slot>`
    }

    static get properties() {
        /**
        * A Google Maps marker object.
        *
        * @type google.maps.Marker
        */
        marker: {
            type: Object,
            notify: true,
        },

        /**
        * The Google map object.
        *
        * @type google.maps.Map
        */
        map: {
            type: Object,
            observer: '_mapChanged',
        },

        /**
        * A Google Map Infowindow object.
        *
        * @type {?Object}
        */
        info: {
            type: Object,
            value: null,
        },

        /**
        * When true, marker *click events are automatically registered.
        */
        clickEvents: {
            type: Boolean,
            value: false,
            observer: '_clickEventsChanged',
        },

        /**
        * When true, marker drag* events are automatically registered.
        */
        dragEvents: {
            type: Boolean,
            value: false,
            observer: '_dragEventsChanged',
        },

        /**
        * Image URL for the marker icon.
        *
        * @type string|google.maps.Icon|google.maps.Symbol
        */
        icon: {
            type: Object,
            value: null,
            observer: '_iconChanged',
        },

        /**
        * When true, marker mouse* events are automatically registered.
        */
        mouseEvents: {
            type: Boolean,
            value: false,
            observer: '_mouseEventsChanged',
        },

        /**
        * Z-index for the marker icon.
        */
        zIndex: {
            type: Number,
            value: 0,
            observer: '_zIndexChanged',
        },

        /**
        * The marker's longitude coordinate.
        */
        longitude: {
            type: Number,
            value: null,
            notify: true,
        },

        /**
        * The marker's latitude coordinate.
        */
        latitude: {
            type: Number,
            value: null,
            notify: true,
        },

        /**
        * The marker's label.
        */
        label: {
            type: String,
            value: null,
            observer: '_labelChanged',
        },

        /**
        * A animation for the marker. "DROP" or "BOUNCE". See
        * https://developers.google.com/maps/documentation/javascript/examples/marker-animations.
        */
        animation: {
            type: String,
            value: null,
            observer: '_animationChanged',
        },

        /**
        * Specifies whether the InfoWindow is open or not
        */
        open: {
            type: Boolean,
            value: false,
            observer: '_openChanged',
        }
    }
}