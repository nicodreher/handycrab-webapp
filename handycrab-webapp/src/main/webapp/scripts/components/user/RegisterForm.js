import React from "react"
import Button from "react-bootstrap/Button";
import {Form} from "react-bootstrap";
import {errorCodeToMessage} from "../../util/errorCode";
import {FormField} from "../general/FormField";
import {OptionalAlert} from "../app/OptionalAlert";
import {registerUrl} from "../../util/RestEndpoints";
import {logIn} from "../../util/Auth";

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

    isValidUsername(username) {
        return /^[a-zA-Z0-9_]{4,16}$/.test(username)
    }

    isValidMail(mail) {
        return /^[\w!#$%&'*+/=?`{|}~^-]+(?:\.[\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,6}$/.test(mail);
    }

    isValidPassword(password) {
        return /^[a-zA-Z0-9"!#$%&'()*+,\-./:;<=>?@\[\]]{6,100}$/.test(password);
    }

    isValidRepeat(password, repeat) {
        return password === repeat && this.isValidPassword(repeat);
    }

    validate() {
        if (!this.isValidUsername(this.state.name)) {
            this.setState({error: 'Ein Benutzername besteht aus 4 bis 16 Zahlen und/oder Buchstaben. "_" ist als Sonderzeichen erlaubt.'});
            return false;
        }
        if (!this.isValidMail(this.state.mail)) {
            this.setState({error: 'Bitte geben Sie eine gültige E-Mail-Adresse ein.'});
            return false;
        }
        if (!this.isValidPassword(this.state.password)) {
            this.setState({error: 'Ein Passwort hat mindestens 6 Zeichen.'});
            return false;
        }
        if (!this.isValidRepeat(this.state.password, this.state.repeatPassword)) {
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
        fetch(registerUrl, {
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
                logIn();
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
        const validPassword = this.isValidPassword(this.state.password);
        const validMail = this.isValidMail(this.state.mail);
        const validRepeat = this.isValidRepeat(this.state.password, this.state.repeatPassword);
        const validUsername = this.isValidUsername(this.state.name);
        return (
            <div>
                <div>&nbsp;</div>
                <OptionalAlert display={this.state.error} error={this.state.error} onClose={this.clearError}/>
                <Form id="register_form" onSubmit={this.handleSubmit}>
                    <FormField id="username" label="Benutzername" onChange={this.handleChangedName} type='text'
                               value={this.state.name} isValid={validUsername}/>
                    <FormField id="mail" label="E-Mail" onChange={this.handleChangedMail} type='text'
                               value={this.state.mail} isValid={validMail}/>
                    <FormField id="password" label="Passwort" type="password" onChange={this.handleChangedPassword}
                               value={this.state.password} isValid={validPassword}/>
                    <FormField id="password_repeat" label="Passwort wiederholen" type="password"
                               onChange={this.handleChangedRepeat} value={this.state.repeatPassword}
                               isValid={validRepeat}/>
                    <Button type={"submit"}>
                        Registrieren
                    </Button>
                </Form>
            </div>
        );
    }
}