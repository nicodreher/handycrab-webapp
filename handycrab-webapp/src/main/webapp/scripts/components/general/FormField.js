import {Col, Form, Row} from "react-bootstrap";
import React from "react";

export function FormField(props) {
    const invalid = props.isValid !== undefined ? (!props.isValid) : undefined;
    return <Form.Group as={Row}>
        <Form.Label id={props.id + '_label'} htmlFor={props.id} column sm="2">
            {props.label}
        </Form.Label>
        <Col sm="10">
            <Form.Control isInvalid={invalid} {...props} aria-describedby={props.id + '_label'}/>
        </Col>
    </Form.Group>
}