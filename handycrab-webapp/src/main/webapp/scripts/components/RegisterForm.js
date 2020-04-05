import React from "react"
import Button from "react-bootstrap/Button";
import {Col, Form} from "react-bootstrap";

export class RegisterForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {name: "", mail: "", password: "", repeatPassword: ""};

        this.handleChangedPassword = this.handleChangedPassword.bind(this);
        this.handleChangedMail = this.handleChangedMail.bind(this);
        this.handleChangedName = this.handleChangedName.bind(this);
        this.handleChangedRepeat = this.handleChangedRepeat.bind(this);

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChangedPassword(event) {
        this.setState({password: event.target.value});
    }

    handleChangedRepeat(event) {
        this.setState({repeatPassword: event.target.value});
    }

    handleChangedName(event) {
        this.setState({name: event.target.value});
    }

    handleChangedMail(event) {
        this.setState({mail: event.target.value});
    }

    handleSubmit(event) {
        //alert('Submitted [name: ' + this.state.name + ', mail: ' + this.state.mail + ', password: ' + this.state.password + ', repeatPassword: ' + this.state.repeatPassword + ']');
        let hasErrorCode = false;
        console.log(JSON.stringify(
            {
                email: this.state.mail,
                username: this.state.name,
                password: this.state.password
            }));
        fetch("http://handycrab.nico-dreher.de/rest/users/register", {
            method: 'POST',
            cache: 'no-cache',
            mode: 'no-cors',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(
                {
                    email: this.state.mail,
                    username: this.state.name,
                    password: this.state.password
                })
        }).then(response => {
            console.log("Still ok before logging response");
            console.log(response);
            hasErrorCode = response.ok;
            return response.json();
        }).then((data) => {
            console.log("Got to second promise");
            if (!hasErrorCode) {
                //TODO handle success
                console.log(data);
            } else {
                //TODO handle errorcode
                console.error('Errorcode: ' + data.errorCode);
            }
        }).catch(error => {
            console.error(error);
        });

        event.preventDefault();
    }

    render() {
        return (
            <div>
                <Form id="register_form" onSubmit={this.handleSubmit}>
                    <Form.Group>
                        <Form.Label id="username_label" htmlFor="username" column sm="2">
                            Benutzername:
                        </Form.Label>
                        <Col sm="10">
                            <Form.Control type="text" required={true} id="username" value={this.state.name}
                                          onChange={this.handleChangedName} aria-describedby="username_label"/>
                        </Col>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label id="mail_label" htmlFor="usermail" column sm="2">
                            E-Mail:
                        </Form.Label>
                        <Col sm="10">
                            <Form.Control type="text" required={true} id="usermail" value={this.state.mail}
                                          onChange={this.handleChangedMail} aria-describedby="mail_label"/>
                        </Col>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label id="password_label" htmlFor="userpassword" column sm="2">
                            Passwort:
                        </Form.Label>
                        <Col sm="10">
                            <Form.Control type="password" id="userpassword" required={true} value={this.state.password}
                                          onChange={this.handleChangedPassword} aria-describedby="password_label"/>
                        </Col>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label id="password_repeat_label" htmlFor="repeat_password" column sm="2">
                            Passwort:
                        </Form.Label>
                        <Col sm="10">
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