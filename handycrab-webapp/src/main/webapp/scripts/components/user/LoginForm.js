import React from "react";
import {Form, Button} from "react-bootstrap";
import {errorCodeToMessage} from "../../util/errorCode";
import {FormField} from "../general/FormField";
import {OptionalAlert} from "../app/OptionalAlert";
import {loginUrl} from "../../util/RestEndpoints";
import {logIn} from "../../util/Auth";

export class LoginForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {login: '', password: '', error: '', stayLoggedIn: false};

        this.handleChangedLogin = this.handleChangedLogin.bind(this);
        this.handleChangedPassword = this.handleChangedPassword.bind(this);

        this.handleSubmit = this.handleSubmit.bind(this);

        this.clearError = this.clearError.bind(this);
    }

    clearError() {
        this.setState({error: ''});
    }

    handleChangedStayLoggedIn = (event) => {
        this.setState({stayLoggedIn: !this.state.stayLoggedIn});
    }

    handleChangedLogin(event) {
        this.setState({login: event.target.value});
    }

    handleChangedPassword(event) {
        this.setState({password: event.target.value});
    }

    handleSubmit(event) {
        event.preventDefault();
        let hasErrorCode;
        fetch(loginUrl, {
            method: 'POST',
            cache: 'no-cache',
            credentials: 'include',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify(
                {
                    login: this.state.login,
                    password: this.state.password,
                    createToken: this.state.stayLoggedIn
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
                logIn();
                const destination = sessionStorage.getItem("destination");
                this.props.history.push(destination ? destination : "/search");
            }
        }).catch(error => {
            console.error(error);
            this.setState({password: '', error: 'Ein unerwarteter Fehler ist aufgetreten'})
        });
    }

    render() {
        return (
            <div>
                <div>&nbsp;</div>
                <OptionalAlert display={this.state.error} error={this.state.error} onClose={this.clearError}/>
                <Form id="login_form" onSubmit={this.handleSubmit}>
                    <FormField id="login" value={this.state.login} onChange={this.handleChangedLogin} type='text'
                               label="Benutzername oder E-Mail"/>
                    <FormField id="password" type="password" value={this.state.password}
                               onChange={this.handleChangedPassword} label="Passwort"/>
                    <Form.Group controlId="stay logged in">
                        <Form.Check type={"checkbox"} label={"Angemeldet bleiben"} checked={this.state.stayLoggedIn}
                                    onChange={this.handleChangedStayLoggedIn}/>
                    </Form.Group>
                    <Button type="submit">
                        Anmelden
                    </Button>
                </Form>
            </div>
        );
    }
}