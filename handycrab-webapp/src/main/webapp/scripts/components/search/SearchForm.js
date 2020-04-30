import React from "react"
import Button from "react-bootstrap/Button";
import {Form, Row} from "react-bootstrap";
import {FormField} from "../general/FormField";
import FormCheck from "react-bootstrap/FormCheck";
import {OptionalAlert} from "../app/OptionalAlert";
import Col from "react-bootstrap/Col";

export class SearchForm extends React.Component {
    watch_id = undefined;

    constructor(props) {
        super(props);
        this.state = {
            latitude: parseFloat('0.0'),
            longitude: parseFloat('0.0'),
            radius: 10,
            postal: 1001,
            searchByPosition: true,
            error: ''
        };

        this.handleChangedLatitude = this.handleChangedLatitude.bind(this);
        this.handleChangedLongitude = this.handleChangedLongitude.bind(this);
        this.handleChangedRadius = this.handleChangedRadius.bind(this);
        this.handleChangedPostal = this.handleChangedPostal.bind(this);

        this.handleSubmit = this.handleSubmit.bind(this);

    }

    componentDidMount() {
        if (navigator.geolocation) {
            this.watch_id = navigator.geolocation.watchPosition(
                (position) => {
                    this.setState({
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude,
                    });
                },
                () => {
                    this.setState({error: 'Ihre Position konnte nicht erfasst werden.', searchByPosition: false});
                },
                {
                    enableHighAccuracy: true,
                    maximumAge: 30000,
                    timeout: 15000
                });
        } else {
            this.setState({error: 'Ihr Browser unterstützt keine Positionserkennung', searchByPosition: false});
        }
    }

    componentWillUnmount() {
        if (this.watch_id !== undefined) {
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
                <OptionalAlert display={this.state.error} error={this.state.error} onClose={() => {
                    this.setState({error: ''});
                }}/>
                <Form id="search_form" onSubmit={this.handleSubmit}>
                    <FormField id="latitude" label="Breitengrad" disabled={true} onChange={this.handleChangedLatitude}
                               value={this.state.latitude.toFixed(3)} type='text'/>
                    <FormField id="longitude" label="Längengrad" disabled={true} onChange={this.handleChangedLongitude}
                               value={this.state.longitude.toFixed(3)} type='text'/>
                    <FormField id='radius' label='Such-Radius (in Meter)' type='number' min='5' max='25'
                               onChange={this.handleChangedRadius} value={this.state.radius}
                               disabled={!this.state.searchByPosition}/>
                    <FormField id='postalcode' type='number' value={this.state.postal}
                               onChange={this.handleChangedPostal} min='01001' max='99999' label='Postleitzahl'
                               disabled={this.state.searchByPosition}/>
                    <Form.Group as={Row}>
                        <Form.Label as="legend" column sm={2}>
                            Suchen über
                        </Form.Label>
                        <Col sm={10}>
                            <FormCheck type={'radio'} checked={this.state.searchByPosition}
                                       label={'Position'}
                                       onChange={() => {
                                           this.setState({searchByPosition: true})
                                       }}/>
                            <FormCheck type={'radio'} checked={!this.state.searchByPosition}
                                       label={'Postleitzahl'}
                                       onChange={() => {
                                           this.setState({searchByPosition: false})
                                       }}/>
                        </Col>
                    </Form.Group>
                    <Button type={"submit"}>
                        Suchen
                    </Button>
                </Form>
            </div>);
    }
}