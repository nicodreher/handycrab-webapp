import React from "react";
import Button from "react-bootstrap/Button";
import {Alert, Col, Form} from "react-bootstrap";
import {errorCodeToMessage} from "../errorCode";

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
        let hasErrorCode;
        console.log(JSON.stringify(
            {
                login: this.state.login,
                password: this.state.password
            }));
        fetch("http://handycrab.nico-dreher.de/rest/users/login", {
            method: 'POST',
            cache: 'no-cache',
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
                this.setState({error: errorCodeToMessage(data.errorCode)});
            } else {
                //TODO success
                console.log(data);
            }
        }).catch(error => {
            console.error(error)
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
                <Form id="login_form" onSubmit={this.handleSubmit}>
                    <Form.Group>
                        <Form.Label id="username_label" htmlFor="username_or_mail">
                            Benutzername oder E-Mail:
                        </Form.Label>
                        <Col>
                            <Form.Control type="text" required={true} id="username_or_mail" value={this.state.login}
                                          onChange={this.handleChangedLogin} aria-describedby="username_label"/>
                        </Col>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label id="password_label" htmlFor="userpassword">
                            Passwort:
                        </Form.Label>
                        <Col>
                            <Form.Control type="password" id="userpassword" required={true} value={this.state.password}
                                          onChange={this.handleChangedPassword} aria-describedby="password_label"/>
                        </Col>
                    </Form.Group>
                    <Button type="submit">
                        Anmelden
                    </Button>
                </Form>
            </div>
        )
            ;
    }
}