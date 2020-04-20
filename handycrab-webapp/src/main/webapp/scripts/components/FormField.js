import {Col, Form, Row} from "react-bootstrap";
import React from "react";

export function FormField(props) {
    const required = props.required !== undefined ? props.required : true;
    const disabled = props.disabled !== undefined ? props.disabled : false;
    const type = props.type !== undefined ? props.type : "text";
    return <Form.Group as={Row}>
        <Form.Label id={props.id + '_label'} htmlFor={props.id} column sm="2">
            {props.label}
        </Form.Label>
        <Col sm="10">
            <Form.Control type={type} required={required} id={props.id} value={props.value}
                          onChange={props.onChange} aria-describedby={props.id + '_label'} disabled={disabled}/>
        </Col>
    </Form.Group>
}