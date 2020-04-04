import React from "react"
import {RegisterForm} from "../components/RegisterForm";
import {Col, Container, Row} from "react-bootstrap";

export class RegisterPage extends React.Component {
    render() {
        return (
            <Container>
                <Row>
                    <Col>
                        <RegisterForm/>
                    </Col>
                </Row>
            </Container>

        );
    }
}