import React from "react"
import {Col, Container, Row} from "react-bootstrap";
import {SearchForm} from "../components/SearchForm";

export class SearchPage extends React.Component {
    render() {
        return (
            <Container>
                <Row>
                    <Col>
                        <SearchForm/>
                    </Col>
                </Row>
            </Container>

        );
    }
}