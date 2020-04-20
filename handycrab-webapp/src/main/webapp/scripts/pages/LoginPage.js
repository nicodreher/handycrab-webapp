import React from "react";
import {LoginForm} from "../components/LoginForm";
import {Col, Container, Row} from "react-bootstrap";

export function LoginPage(props) {
    return <Container>
        <Row>
            <Col>
                <LoginForm history={props.history}/>
                <div>Noch keinen Account erstellt? Hier gehts zur <a href="/register">Registrierung</a></div>
            </Col>
        </Row>
    </Container>;
}