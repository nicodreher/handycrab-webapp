import React from "react"
import Button from "react-bootstrap/Button";
import {Alert, Col, Form} from "react-bootstrap";
import {errorCodeToMessage} from "../errorCode";

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
        if (!this.validate()) {
            event.preventDefault();
            return;
        }

        let hasErrorCode = false;
        fetch("http://handycrab.nico-dreher.de/rest/users/register", {
            method: 'POST',
            cache: 'no-cache',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
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
                //TODO handle success
                console.log(data);
            } else {
                console.error('Errorcode: ' + data.errorCode);
                this.setState({error: errorCodeToMessage(data.errorCode)});
            }
        }).catch(error => {
            console.error(error);
        });

        event.preventDefault();
    }

    render() {
        let alert;
        if (this.state.error) {
            alert =
                <Alert variant={"danger"} dismissible={true} onClose={this.clearError}> {this.state.error}  </Alert>;
        } else {
            alert = <span/>;
        }
        return (
            <div>
                {alert}
                <Form id="register_form" onSubmit={this.handleSubmit}>
                    <Form.Group>
                        <Form.Label id="username_label" htmlFor="username" column>
                            Benutzername:
                        </Form.Label>
                        <Col>
                            <Form.Control type="text" required={true} id="username" value={this.state.name}
                                          onChange={this.handleChangedName} aria-describedby="username_label"/>
                        </Col>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label id="mail_label" htmlFor="usermail" column>
                            E-Mail:
                        </Form.Label>
                        <Col>
                            <Form.Control type="text" required={true} id="usermail" value={this.state.mail}
                                          onChange={this.handleChangedMail} aria-describedby="mail_label"/>
                        </Col>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label id="password_label" htmlFor="userpassword" column>
                            Passwort:
                        </Form.Label>
                        <Col>
                            <Form.Control type="password" id="userpassword" required={true} value={this.state.password}
                                          onChange={this.handleChangedPassword} aria-describedby="password_label"/>
                        </Col>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label id="password_repeat_label" htmlFor="repeat_password" column>
                            Passwort:
                        </Form.Label>
                        <Col>
                            <Form.Control type="password" id="repeat_password" required={true}
                                          aria-describedby="password_repeat_label"
                                          value={this.state.repeatPassword} onChange={this.handleChangedRepeat}/>
                        </Col>
                    </Form.Group>
                    <Button type={"submit"}>
                        Registrieren
                    </Button>
                </Form>
            </div>

        );
    }
}