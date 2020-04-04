import React from "react"
import {LoginForm} from "../components/LoginForm";
import {Col, Container, Row} from "react-bootstrap";

export class LoginPage extends React.Component {
    render() {
        return (
            <Container>
                <Row>
                    <Col>
                        <LoginForm/>
                        <div>Noch keinen Account erstellt? Hier gehts zur <a href="/register">Registrierung</a></div>
                    </Col>
                </Row>
            </Container>
        );
    }
}