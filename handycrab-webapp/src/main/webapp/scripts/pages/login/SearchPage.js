import React from "react";
import {Col, Container, Row} from "react-bootstrap";
import {SearchForm} from "../../components/search/SearchForm";

export function SearchPage(props) {
    return <Container>
        <Row>
            <Col>
                <SearchForm/>
            </Col>
        </Row>
    </Container>;
}