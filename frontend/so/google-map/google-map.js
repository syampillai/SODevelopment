import '@google-web-components/google-apis/google-maps-api.js';
import {PolymerElement} from '@polymer/polymer/polymer-element.js';
import {html} from '@polymer/polymer/lib/utils/html-tag.js';

class GoogleMap extends PolymerElement {

    static get template() {
        return html`
            <style>
                :host {
                position: relative;
                display: block;
                height: 100%;
            }
            div.gmap {
                position: absolute;
                top: 0;
                right: 0;
                bottom: 0;
                left: 0;
            }
            </style>
            <google-maps-api id="[[apiId]]" api-key="[[apiKey]]" client-id="[[clientId]]" version="weekly" signed-in="[[signedIn]]" language="[[language]]" on-api-load="_apiLoaded" maps-url="[[mapsUrl]]">
            </google-maps-api>
            <div class="gmap" id="[[mapId]]" style="[[mapSize]]"></div>`
    }

	static get properties() {
        return {
            apiKey: {
                type: String,
            },
            apiId: {
                type: String,
            },
            mapId: {
                type: String,
            },
            clientId: {
                type: String,
            },
            mapSize: {
                type: String,
            },
            signedIn: {
                type: Boolean,
                value: false,
            },
            language: {
                type: String,
            },
            mapsUrl: {
                type: String,
            },
            attached: {
                type: Boolean,
                value: false,
            },
            /**
            * A Maps API object.
            */
            map: {
                type: Object,
                notify: true,
                value: null,
            },
            /**
            * A latitude to center the map on.
            */
            latitude: {
                type: Number,
                value: 37.77493,
                notify: true,
                reflectToAttribute: true,
            },
            /**
            * A longitude to center the map on.
            */
            longitude: {
                type: Number,
                value: -122.41942,
                notify: true,
                reflectToAttribute: true,
            },
            zoom: {
                type: Number,
                value: 8,
                observer: '_zoomChanged',
                notify: true,
            },
            /**
             * When set, prevents the map from tilting (when the zoom level and viewport supports it).
             */
            noAutoTilt: {
                type: Boolean,
                value: false,
            },
            /**
             * Map type to display. One of 'roadmap', 'satellite', 'hybrid', 'terrain'.
             */
            mapType: {
                type: String,
                value: 'roadmap', // roadmap, satellite, hybrid, terrain,
                observer: '_mapTypeChanged',
                notify: true,
            },
            /**
            * If set, removes the map's default UI controls.
            */
            disableDefaultUi: {
                type: Boolean,
                value: false,
                observer: '_disableDefaultUiChanged',
            },
            /**
            * If set, removes the map's 'map type' UI controls.
            */
            disableMapTypeControl: {
                type: Boolean,
                value: false,
                observer: '_disableMapTypeControlChanged',
            },
            /**
            * If set, removes the map's 'street view' UI controls.
            */
            disableStreetViewControl: {
                type: Boolean,
                value: false,
                observer: '_disableStreetViewControlChanged',
            },
            /**
            * If set, the zoom level is set such that all markers are brought into view.
            */
            fitToMarkers: {
                type: Boolean,
                value: false,
                observer: '_fitToMarkersChanged',
            },
            /**
            * The shapes (including markers) on the map.
            */
            shapes: {
                type: Object,
                value() { return new Map(); },
            },
            /**
            * If true, prevent the user from zooming the map interactively.
            */
            disableZoom: {
                type: Boolean,
                value: false,
                observer: '_disableZoomChanged',
            },
            /**
            * If set, custom styles can be applied to the map.
            * For style documentation see https://developers.google.com/maps/documentation/javascript/reference#MapTypeStyle
            */
            styles: {
                type: Object,
                value() { return {}; },
            },
            /**
            * A maximum zoom level which will be displayed on the map.
            */
            maxZoom: {
                type: Number,
                observer: '_maxZoomChanged',
            },
            /**
            * A minimum zoom level which will be displayed on the map.
            */
            minZoom: {
                type: Number,
                observer: '_minZoomChanged',
            },
            /**
             * A kml file to load.
             */
            kml: {
                type: String,
                value: null,
                observer: '_loadKml',
            },
        }
    }

    static get is() { return 'google-map'; }

    _apiLoaded() {
        this._initGMap();
    }

    connectedCallback() {
        super.connectedCallback();
        this.attached = true;
        this._initGMap();
    }

    disconnectedCallback() {
        super.disconnectedCallback();
        this.attached = false;
    }

    _element(eid) {
        if(this.shadowRoot == null) {
          return null;
        }
        return this.shadowRoot.querySelector("#" + eid);
    }

    _api() {
        return this._element(this.apiId);
    }

    _map() {
        return this._element(this.mapId);
    }
    
    _initGMap() {
        if(this.map) {
            return; // Already initialized
        }
        let api = this._api();
        if(api == null) {
            return;
        }
        if(api.libraryLoaded !== true) {
            return; // Api not loaded
        }
        if(!this.attached) {
            return; // Not attached
        }
        let map = this._map();
        if(map == null) {
            return;
        }
        this.map = new google.maps.Map(map, this._getMapOptions());
        this._updateCenter();
        this._loadKml();
        this._addMapListeners();
        if(this.fitToMarkers) {
            this._fitToMarkersChanged();
        }
        this.dispatchEvent(new CustomEvent('google-map-ready'));
        this.$server.ready();
    }

    _getMapOptions() {
        const mapOptions = {
            zoom: this.zoom,
            tilt: this.noAutoTilt ? 0 : 45,
            mapTypeId: this.mapType,
            disableDefaultUI: this.disableDefaultUi,
            mapTypeControl: !this.disableDefaultUi && !this.disableMapTypeControl,
            streetViewControl: !this.disableDefaultUi && !this.disableStreetViewControl,
            disableDoubleClickZoom: this.disableZoom,
            scrollwheel: !this.disableZoom,
            styles: this.styles,
            maxZoom: Number(this.maxZoom),
            minZoom: Number(this.minZoom),
            gestureHandling: 'greedy',
            draggable: true,
        };
        return mapOptions;
    }

    _addMapListeners() {
        google.maps.event.addListener(this.map, 'center_changed', () => {
            const center = this.map.getCenter();
            this.latitude = center.lat();
            this.longitude = center.lng();
        });
        google.maps.event.addListener(this.map, 'zoom_changed', () => {
            this.zoom = this.map.getZoom();
        });
        google.maps.event.addListener(this.map, 'maptypeid_changed', () => {
            this.mapType = this.map.getMapTypeId();
        });
    }

    _updateCenter() {
        if(this.map && this.latitude !== undefined && this.longitude !== undefined) {
            const newCenter = new google.maps.LatLng(this.latitude, this.longitude);
            let oldCenter = this.map.getCenter();
            if (!oldCenter) {
                // If the map does not have a center, set it right away.
                this.map.setCenter(newCenter);
            } else {
                // Using google.maps.LatLng returns corrected lat/lngs.
                oldCenter = new google.maps.LatLng(oldCenter.lat(), oldCenter.lng());
                // If the map currently has a center, slowly pan to the new one.
                if(!oldCenter.equals(newCenter)) {
                    this.map.panTo(newCenter);
                }
            }
        }
    }

    _zoomChanged() {
        if(this.map && this.zoom != this.map.getZoom()) {
          this.map.setZoom(Number(this.zoom));
        }
    }

    _maxZoomChanged() {
        if(this.map) {
            this.map.setOptions({ maxZoom: Number(this.maxZoom) });
        }
    }

    _minZoomChanged() {
        if(this.map) {
            this.map.setOptions({ minZoom: Number(this.minZoom) });
        }
    }

    _mapTypeChanged() {
        if(this.map) {
            this.map.setMapTypeId(this.mapType);
        }
    }

    _disableDefaultUiChanged() {
        if(this.map) {
            this.map.setOptions({ disableDefaultUI: this.disableDefaultUi });
        }
    }

    _disableMapTypeControlChanged() {
        if(this.map) {
            this.map.setOptions({ mapTypeControl: !this.disableMapTypeControl });
        }
    }

    _disableStreetViewControlChanged() {
        if(this.map) {
            this.map.setOptions({ streetViewControl: !this.disableStreetViewControl });
        }
    }

    _disableZoomChanged() {
        if(!this.map) {
            return;
        }
        this.map.setOptions({
            disableDoubleClickZoom: this.disableZoom,
            scrollwheel: !this.disableZoom,
        });
    }

    _fitToMarkersChanged() {
        if (this.map && this.fitToMarkers && this.shapes.size > 0) {
            const latLngBounds = new google.maps.LatLngBounds();
            let count = 0;
            for(let m of this.shapes.values()) {
                if(!(m instanceof google.maps.Marker)) {
                    continue;
                }
                if(m.getMap() != null) {
                    latLngBounds.extend(m.getPosition());
                    ++count;
                }
            }
            // For one marker, don't alter zoom, just center it.
            if (count > 1) {
                this.map.fitBounds(latLngBounds);
            } else if(count > 0) {
                this.map.setCenter(latLngBounds.getCenter());
            }
        }
    }

    /**
    * Explicitly resizes the map, updating its center. This is useful if the
    * map does not show after you have unhidden it.
    */
    resize() {
        if (this.map) {
            // saves and restores latitude/longitude because resize can move the center
            const oldLatitude = this.latitude;
            const oldLongitude = this.longitude;
            google.maps.event.trigger(this.map, 'resize');
            this.latitude = oldLatitude; // restore because resize can move our center
            this.longitude = oldLongitude;
            if (this.fitToMarkers) { // we might not have a center if we are doing fit-to-markers
                this._fitToMarkersChanged();
            }
        }
    }

    _loadKml() {
        if (this.map && this.kml) {
            const kmlfile = new google.maps.KmlLayer({
            url: this.kml,
            map: this.map,
            });
        }
    }

    command(command) {
        switch(command) {
            case '':
                return;
            case 'C':
                this._updateCenter();
                break;
            case 'R':
                this.resize();
                break;
        }
        this.$server.commandDone();
    }

    addMarker(id, latitude, longitude, draggable, title, iconUrl) {
        if(!this.map) {
            return;
        }
        let m = this.shapes.get(id);
        if(m) {
            if(!(m instanceof google.maps.Marker)) {
                return;
            }
            m.setPosition(new google.maps.LatLng(latitude, longitude));
            m.setDraggable(draggable);
            m.setTitle(title);
            m.setIcon({ url: iconUrl });
            m.setMap(this.map);
        } else {
            m = new google.maps.Marker({
                map: this.map,
                draggable: draggable,
                animation: google.maps.Animation.DROP,
                position: new google.maps.LatLng(latitude, longitude),
                title: title,
                icon: {
                    url: iconUrl,
                },
            });
            this.shapes.set(id, m);
            m.addListener("click", () => {
                this.$server.markerClicked(id);
            });
            m.addListener("dragend", () => {
                let p = m.getPosition();
                this.$server.markerPositioned(id, p.lat(), p.lng());
            });
        }
        if(this.fitToMarkers) {
            this._fitToMarkersChanged();
        }
        this.$server.commandDone();
    }

    addPolygon(id, points, draggable, strokeColor, strokeOpacity, strokeWeight, fillColor, fillOpacity) {
        if(!this.map) {
            return;
        }
        let m = this.shapes.get(id);
        let options = {
            paths: points,
            draggable: draggable,
            strokeColor: strokeColor,
            strokeOpacity: strokeOpacity,
            strokeWeight: strokeWeight,
            fillColor: fillColor,
            fillOpacity: fillOpacity,
        };
        if(m) {
            if(!(m instanceof google.maps.Polygon)) {
                return;
            }
            m.setOptions(options);
        } else {
            m = new google.maps.Polygon(options);
            this.shapes.set(id, m);
            m.addListener("dragend", () => {
                let p = m.getPath().getAt(0);
                this.$server.polyPositioned(id, p.lat(), p.lng());
            });
        }
        m.setMap(this.map);
        this.$server.commandDone();
    }

    addPolyline(id, points, draggable, strokeColor, strokeOpacity, strokeWeight) {
        if(!this.map) {
            return;
        }
        let m = this.shapes.get(id);
        let options = {
            path: points,
            draggable: draggable,
            strokeColor: strokeColor,
            strokeOpacity: strokeOpacity,
            strokeWeight: strokeWeight,
        };
        if(m) {
            if(!(m instanceof google.maps.Polyline)) {
                return;
            }
            m.setOptions(options);
        } else {
            m = new google.maps.Polyline(options);
            this.shapes.set(id, m);
            m.addListener("dragend", () => {
                let p = m.getPath().getAt(0);
                this.$server.polyPositioned(id, p.lat(), p.lng());
            });
        }
        m.setMap(this.map);
        this.$server.commandDone();
    }

    addCircle(id, latitude, longitude, radius, draggable, strokeColor, strokeOpacity, strokeWeight, fillColor, fillOpacity) {
        if(!this.map) {
            return;
        }
        let m = this.shapes.get(id);
        let options = {
            center: new google.maps.LatLng(latitude, longitude),
            radius: radius,
            draggable: draggable,
            strokeColor: strokeColor,
            strokeOpacity: strokeOpacity,
            strokeWeight: strokeWeight,
            fillColor: fillColor,
            fillOpacity: fillOpacity,
        };
        if(m) {
            if(!(m instanceof google.maps.Circle)) {
                return;
            }
            m.setOptions(options);
        } else {
            m = new google.maps.Circle(options);
            this.shapes.set(id, m);
            m.addListener("dragend", () => {
                let p = m.getCenter();
                this.$server.circlePositioned(id, p.lat(), p.lng());
            });
        }
        m.setMap(this.map);
        this.$server.commandDone();
    }

    remove(id) {
        if(!this.map) {
            return;
        }
        let m = this.shapes.get(id);
        if(m) {
            m.setMap(null);
            this.shapes.remove(id);
        }
        this.$server.commandDone();
    }

    hide(id) {
        if(!this.map) {
            return;
        }
        let m = this.shapes.get(id);
        if(m) {
            m.setMap(null);
        }
        this.$server.commandDone();
    }

    show(id) {
        if(!this.map) {
            return;
        }
        let m = this.shapes.get(id);
        if(m) {
            m.setMap(this.map);
            if(this.fitToMarkers && m instanceof google.maps.Marker) {
                this._fitToMarkersChanged();
            }
        }
        this.$server.commandDone();
    }

    clear(type) {
        let ss = new Array();
        for(let s of this.shapes.entries()) {
            if(type === "m" && !(s[1] instanceof google.maps.Marker)) {
                continue;
            }
            if(type === "p" && !(s[1] instanceof google.maps.Polygon)) {
                continue;
            }
            if(type === "l" && !(s[1] instanceof google.maps.Polyline)) {
                continue;
            }
            if(type === "c" && !(s[1] instanceof google.maps.Circle)) {
                continue;
            }
            s[1].setMap(null);
            ss.push(s[0]);
        }
        for(let s of ss) {
            this.shapes.delete(s);
        }
        this.$server.commandDone();
    }
}

customElements.define(GoogleMap.is, GoogleMap);
