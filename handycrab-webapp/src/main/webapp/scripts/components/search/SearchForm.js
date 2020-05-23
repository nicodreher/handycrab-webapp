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
            postal: '',
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
        event.preventDefault();
        let search;
        if (this.state.searchByPosition) {
            search = "lo=" + this.state.longitude + "&la=" + this.state.latitude + "&ra=" + this.state.radius;
        } else {
            if (/^\s*$/.test(this.state.postal)) {
                this.setState({error: 'Bitte geben Sie eine Postleitzahl ein'})
                return;
            } else {
                search = "pc=" + this.state.postal;
            }
        }
        window.location.replace(window.location.origin + "/results?" + search);
    }

    render() {
        const radii = [10, 25, 50, 100];
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
                    <Form.Group as={Row} controlId={"radius_selection"}>
                        <Form.Label as="legend" column sm={2}>
                            Such-Radius (in Meter)
                        </Form.Label>
                        <Col sm={10}>
                            {radii.map(r => <FormCheck key={r} type={'radio'} checked={this.state.radius === r}
                                                       disabled={!this.state.searchByPosition} inline label={r}
                                                       onChange={() => {
                                                           this.setState({radius: r})
                                                       }}/>)}
                        </Col>
                    </Form.Group>
                    <FormField id='postalcode' type='text' value={this.state.postal}
                               onChange={this.handleChangedPostal} label={'Postleitzahl'}
                               disabled={this.state.searchByPosition}
                               isInvalid={/^\s*$/.test(this.state.postal) && !this.state.searchByPosition}/>
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