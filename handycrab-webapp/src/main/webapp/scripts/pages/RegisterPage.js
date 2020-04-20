import React from "react";
import {RegisterForm} from "../components/RegisterForm";
import {Col, Container, Row} from "react-bootstrap";

export function RegisterPage(props) {
    return <Container>
        <Row>
            <Col>
                <RegisterForm history={props.history}/>
            </Col>
        </Row>
    </Container>;

}