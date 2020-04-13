import React from "react"
import Button from "react-bootstrap/Button";
import {Row, Col, Form} from "react-bootstrap";
import {FormField} from "./FormField";

export class SearchForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {latitude: 0.0, longitude: 0.0, radius: 10, watch_id: undefined, postal: 1001};

        this.handleChangedLatitude = this.handleChangedLatitude.bind(this);
        this.handleChangedLongitude = this.handleChangedLongitude.bind(this);
        this.handleChangedRadius = this.handleChangedRadius.bind(this);
        this.handleChangedPostal = this.handleChangedPostal.bind(this);

        this.handleSubmit = this.handleSubmit.bind(this);

    }

    componentDidMount() {
        if (navigator.geolocation) {
            this.setState({
                watch_id: navigator.geolocation.watchPosition(
                    (position) => {
                        this.setState({latitude: position.coords.latitude, longitude: position.coords.longitude});
                    },
                    () => {
                        this.setState({latitude: 'Ihre Position konnte nicht erfasst werden.'});
                    },
                    {
                        enableHighAccuracy: true,
                        maximumAge: 30000,
                        timeout: 15000
                    })
            });
        } else {
            this.setState({latitude: 'Ihr Browser unterstützt keine Positionserkennung'});
        }
    }

    componentWillUnmount() {
        if (this.state.watch_id !== undefined) {
            navigator.geolocation.clearWatch(this.state.watch_id);
        }
    }


    handleChangedLongitude(event) {
        this.setState({longitude: event.target.value});
    }

    handleChangedRadius(event) {
        this.setState({radius: event.target.value});
    }

    handleChangedLatitude(event) {
        this.setState({latitude: event.target.value});
    }

    handleChangedPostal(event) {
        this.setState({postal: event.target.value});
    }

    handleSubmit(event) {
        //alert('Submitted [name: ' + this.state.name + ', mail: ' + this.state.mail + ', password: ' + this.state.password + ', repeatPassword: ' + this.state.repeatPassword + ']');
        event.preventDefault();
        console.log("Submitted the search form");
    }

    render() {
        return (
            <div>
                <div>&nbsp;</div>
                <Form id="search_form" onSubmit={this.handleSubmit}>
                    <FormField id="latitude" label="Breitengrad" disabled={true} onChange={this.handleChangedLatitude}
                               value={this.state.latitude}/>
                    <FormField id="longitude" label="Längengrad" disabled={true} onChange={this.handleChangedLongitude}
                               value={this.state.longitude}/>
                    <Form.Group as={Row}>
                        <Form.Label id="radius_label" htmlFor="radius" column sm="2">
                            Such-Radius (in Meter):
                        </Form.Label>
                        <Col sm="10">
                            <Form.Control type="number" id="radius" value={this.state.radius}
                                          onChange={this.handleChangedRadius} aria-describedby="radius_label" min="5"
                                          max="25"/>
                        </Col>
                    </Form.Group>
                    <Form.Group as={Row}>
                        <Form.Label id="postal_label" htmlFor="postal" column sm="2">
                            Postleitzahl:
                        </Form.Label>
                        <Col sm="10">
                            <Form.Control type="number" value={this.state.postal} onChange={this.handleChangedPostal}
                                          aria-describedby="postal_label" min="01001" max="99999"/>
                        </Col>
                    </Form.Group>
                    <Button type={"submit"}>
                        Suchen
                    </Button>
                </Form>
            </div>

        );
    }
}