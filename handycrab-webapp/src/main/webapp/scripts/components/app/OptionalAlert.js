import Alert from "react-bootstrap/Alert";
import React from "react";

export function OptionalAlert(props) {
    return props.display ? <Alert dismissible={true} onClose={props.onClose} variant="danger">{props.error}</Alert> :
        <span/>;


}