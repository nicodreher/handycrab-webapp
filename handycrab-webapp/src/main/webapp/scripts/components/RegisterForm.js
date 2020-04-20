import React from "react"
import Button from "react-bootstrap/Button";
import {Form} from "react-bootstrap";
import {errorCodeToMessage} from "../errorCode";
import {FormField} from "./FormField";
import {OptionalAlert} from "./OptionalAlert";

export class RegisterForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {name: "", mail: "", password: "", repeatPassword: "", error: ''};

        this.handleChangedPassword = this.handleChangedPassword.bind(this);
        this.handleChangedMail = this.handleChangedMail.bind(this);
        this.handleChangedName = this.handleChangedName.bind(this);
        this.handleChangedRepeat = this.handleChangedRepeat.bind(this);

        this.handleSubmit = this.handleSubmit.bind(this);

        this.clearError = this.clearError.bind(this);
    }

    handleChangedPassword(event) {
        this.setState({password: event.target.value});
    }

    handleChangedRepeat(event) {
        this.setState({repeatPassword: event.target.value});
    }

    clearError() {
        this.setState({error: ''});
    }

    handleChangedName(event) {
        this.setState({name: event.target.value});
    }

    handleChangedMail(event) {
        this.setState({mail: event.target.value});
    }

    validate() {
        const username_regex = /^[a-zA-Z0-9_]{4,16}$/;
        const password_regex = /^[a-zA-Z0-9"!#$%&'()*+,\-./:;<=>?@\[\]]{6,100}$/;
        const mail_regex = /^[\w!#$%&'*+/=?`{|}~^-]+(?:\.[\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,6}$/;

        if (!username_regex.test(this.state.name)) {
            this.setState({error: 'Ein Benutzername besteht aus 4 bis 16 Zahlen und/oder Buchstaben. "_" ist als Sonderzeichen erlaubt.'});
            return false;
        }
        if (!mail_regex.test(this.state.mail)) {
            this.setState({error: 'Bitte geben Sie eine gültige E-Mail-Adresse ein.'});
            return false;
        }
        if (!password_regex.test(this.state.password)) {
            this.setState({error: 'Ein Passwort hat mindestens 6 Zeichen.'});
            return false;
        }
        if (!(this.state.password === this.state.repeatPassword)) {
            this.setState({error: 'Passwörter stimmen nicht überein.'});
            return false;
        }
        return true;
    }

    handleSubmit(event) {
        //alert('Submitted [name: ' + this.state.name + ', mail: ' + this.state.mail + ', password: ' + this.state.password + ', repeatPassword: ' + this.state.repeatPassword + ']');
        event.preventDefault();
        if (!this.validate()) {
            return;
        }
        let hasErrorCode = false;
        fetch("http://handycrab.nico-dreher.de/rest/users/register", {
            method: 'POST',
            cache: 'no-cache',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            credentials: 'include',
            body: JSON.stringify(
                {
                    email: this.state.mail,
                    username: this.state.name,
                    password: this.state.password
                })
        }).then(response => {
            hasErrorCode = !response.ok;
            return response.json();
        }).then((data) => {
            if (!hasErrorCode) {
                console.log(data);
                this.props.history.push("/search");
            } else {
                console.error('Errorcode: ' + data.errorCode);
                this.setState({error: errorCodeToMessage(data.errorCode), password: '', repeatPassword: ''});
            }
        }).catch(error => {
            console.error(error);
        });
    }

    render() {
        return (
            <div>
                <OptionalAlert display={this.state.error} error={this.state.error} onClose={this.state.clearError}/>
                <div>&nbsp;</div>
                <Form id="register_form" onSubmit={this.handleSubmit}>
                    <FormField id="username" label="Benutzername" onChange={this.handleChangedName}
                               value={this.state.name}/>
                    <FormField id="mail" label="E-Mail" onChange={this.handleChangedMail}
                               value={this.state.mail}/>
                    <FormField id="password" label="Passwort" type="password" onChange={this.handleChangedPassword}
                               value={this.state.password}/>
                    <FormField id="password_repeat" label="Passwort wiederholen" type="password"
                               onChange={this.handleChangedRepeat} value={this.state.repeatPassword}/>
                    <Button type={"submit"}>
                        Registrieren
                    </Button>
                </Form>
            </div>
        );
    }
}