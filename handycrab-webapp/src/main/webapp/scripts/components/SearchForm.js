import React from "react"
import Button from "react-bootstrap/Button";
import {Form} from "react-bootstrap";
import {FormField} from "./FormField";

export class SearchForm extends React.Component {
    watch_id = undefined;

    constructor(props) {
        super(props);
        this.state = {latitude: 0.0, longitude: 0.0, radius: 10, postal: 1001};

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
                    this.setState({latitude: 'Ihre Position konnte nicht erfasst werden.'});
                },
                {
                    enableHighAccuracy: true,
                    maximumAge: 30000,
                    timeout: 15000
                });
        } else {
            this.setState({latitude: 'Ihr Browser unterstützt keine Positionserkennung'});
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
                <Form id="search_form" onSubmit={this.handleSubmit}>
                    <FormField id="latitude" label="Breitengrad" disabled={true} onChange={this.handleChangedLatitude}
                               value={this.state.latitude} type='text'/>
                    <FormField id="longitude" label="Längengrad" disabled={true} onChange={this.handleChangedLongitude}
                               value={this.state.longitude} type='text'/>
                    <FormField id='radius' label='Such-Radius (in Meter)' type='number' min='5' max='25'
                               onChange={this.handleChangedRadius} value={this.state.radius}/>
                    <FormField id='postalcode' type='number' value={this.state.postal}
                               onChange={this.handleChangedPostal} min='01001' max='99999' label='Postleitzahl'/>
                    <Button type={"submit"}>
                        Suchen
                    </Button>
                </Form>
            </div>

        );
    }
}