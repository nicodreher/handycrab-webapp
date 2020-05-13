import React from "react"
import { Map as LeafletMap, TileLayer, Marker, Popup } from 'react-leaflet';

export class MapComponent extends React.Component {
    render() {

        const pinIcon = new L.Icon({
            iconUrl: "images/icons/uicomponents/pin.svg",
            iconRetinaUrl: "images/icons/uicomponents/pin.svg",
            iconAnchor: [23, 46],
            popupAnchor: [0, -46],
            shadowUrl: null,
            shadowSize: null,
            shadowAnchor: null,
            iconSize: new L.Point(46, 46),
            className: 'me-pin-icon'
        });

        return (
            <div className="leafletmap-div-container">
                <LeafletMap center={[this.props.latitude, this.props.longitude]} zoom={13} minZoom={1} maxZoom={19}
                 attributionControl={true} zoomControl={true} doubleClickZoom={true} scrollWheelZoom={true} dragging={true}
                 animate={true} easeLinearity={0.35}>
                    <TileLayer url='http://{s}.tile.osm.org/{z}/{x}/{y}.png'/>

                    {this.props.results.map(result =>
                        <Marker key={result._id} position={[result.latitude, result.longitude]} icon={pinIcon}>
                          <Popup>
                            <p><b>{result.title}</b></p>
                          </Popup>
                        </Marker>
                    )};

                    <Marker key="me" position={[this.props.latitude, this.props.longitude]}>
                      <Popup>
                        <p><b>Eigener (Such-)Standort</b></p>
                      </Popup>
                    </Marker>
                </LeafletMap>
            </div>
        );
    }
}
