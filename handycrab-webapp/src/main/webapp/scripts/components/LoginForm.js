import React from "react";
import Button from "react-bootstrap/Button";
import {Form} from "react-bootstrap";
import {errorCodeToMessage} from "../errorCode";
import {FormField} from "./FormField";
import {OptionalAlert} from "./OptionalAlert";

export class LoginForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {login: '', password: '', error: ''};

        this.handleChangedLogin = this.handleChangedLogin.bind(this);
        this.handleChangedPassword = this.handleChangedPassword.bind(this);

        this.handleSubmit = this.handleSubmit.bind(this);

        this.clearError = this.clearError.bind(this);
    }

    clearError() {
        this.setState({error: ''});
    }

    handleChangedLogin(event) {
        this.setState({login: event.target.value});
    }

    handleChangedPassword(event) {
        this.setState({password: event.target.value});
    }

    handleSubmit(event) {
        //alert('Submitted [login: ' + this.state.login + ', password: ' + this.state.password + ']');
        event.preventDefault();
        let hasErrorCode;
        fetch("http://handycrab.nico-dreher.de/rest/users/login", {
            method: 'POST',
            cache: 'no-cache',
            credentials: 'include',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify(
                {
                    login: this.state.login,
                    password: this.state.password
                })
        }).then(response => {
            hasErrorCode = !response.ok;
            return response.json();
        }).then((data) => {
            if (hasErrorCode) {
                console.error("Errorcode: " + data.errorCode);
                this.setState({error: errorCodeToMessage(data.errorCode), password: ''});
            } else {
                console.log(data);
                this.props.history.push("/search");
            }
        }).catch(error => {
            console.error(error);
            this.setState({password: '', error: 'Ein unerwarteter Fehler ist aufgetreten'})
        });
    }

    render() {
        return (
            <div>
                <OptionalAlert display={this.state.error} error={this.state.error} onClose={this.clearError}/>
                <div>&nbsp;</div>
                <Form id="login_form" onSubmit={this.handleSubmit}>
                    <FormField id="login" value={this.state.login} onChange={this.handleChangedLogin}
                               label="Benutzername oder E-Mail"/>
                    <FormField id="password" type="password" value={this.state.password}
                               onChange={this.handleChangedPassword} label="Passwort"/>
                    <Button type="submit">
                        Anmelden
                    </Button>
                </Form>
            </div>
        );
    }
}