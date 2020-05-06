import React from "react";
import {Col, Container, Row} from "react-bootstrap";
import {AddForm} from "../../components/add/AddForm";

export function BarrierAddPage(props) {
    return <Container>
        <Row>
            <Col>
                <AddForm/>
            </Col>
        </Row>
    </Container>;
}