import {
    Button,
    Card,
    CardColumns,
    Col,
    Container,
    Image,
    Jumbotron,
    Modal,
    Row,
    Spinner
} from "react-bootstrap";
import React, {Component} from "react";
import {VoteParagraph} from "../general/VoteParagraph";
import {addSolutionUrl, deleteBarrierUrl} from "../../util/RestEndpoints";
import {FormField} from "../general/FormField";
import {errorCodeToMessage} from "../../util/errorCode";
import {getCurrentUser} from "../../util/Auth";

export class BarrierDetailView extends Component {
    constructor(props) {
        super(props);
        this.state = {showModal: false, solutionText: ''};
    }

    deleteBarrier = () => {
        fetch(deleteBarrierUrl, {
            method: 'DELETE',
            cache: 'no-cache',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            credentials: 'include',
            body: JSON.stringify({_id: this.props.barrier._id})
        }).then(response => {
            if (!response.ok) {
                console.error('Failed deletion');
                location.reload();
            } else {
                window.location.href = '/search';
            }
        }).catch(error => console.error(error));
    }

    addSolution = (id, text) => {
        fetch(addSolutionUrl, {
            method: 'POST',
            cache: 'no-cache',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            credentials: 'include',
            body: JSON.stringify({_id: id, solution: text})
        }).then(response => response.json()).then(data => {
            if (data.errorCode) {
                console.error(errorCodeToMessage(data.errorCode));
            } else {
                location.reload();
            }
        });
    }

    render() {
        const isBarrierPresent = !!this.props.barrier;
        const user = getCurrentUser();
        const barrierCreatedByCurrentUser = user ? this.props.barrier?.userId === user._id : false;
        return <><Jumbotron>
            <Container>
                <Row>
                    {isBarrierPresent ? <Col>
                        <h1>{this.props.barrier.title}</h1>
                        <div align={'center'}><Image src={this.props.barrier.picturePath} fluid/></div>
                        <div>{this.props.barrier.description}</div>
                        {this.props.barrier.vote && <>
                            <VoteParagraph downvotes={this.props.barrier.downVotes} upvotes={this.props.barrier.upVotes}
                                           vote={this.props.barrier.vote} barrier={true} _id={this.props.barrier._id}/>
                            <br/>
                            <span>Breitengrad: {this.props.barrier.latitude}</span>,&nbsp;
                            <span>Längengrad: {this.props.barrier.longitude}</span>,&nbsp;
                            <span>Postleitzahl: {this.props.barrier.postcode}</span>
                            <div>erstellt von {this.props.barrier.user}</div>
                        </>}
                    </Col> : <div><Spinner animation={"border"}/> Barriere wird geladen</div>}
                </Row>
                {isBarrierPresent && <Row>
                    <Col>
                        {this.props.barrier._id &&
                        <Button onClick={() => this.setState({showModal: true})}>Lösung hinzufügen
                            <img src="images/icons/uicomponents/add_solution.svg" style={{
                                height: "30px",
                                width: "auto",
                                marginTop: "auto",
                                marginBottom: "auto",
                                marginLeft: "5px",
                            }}/>
                        </Button>}
                        {barrierCreatedByCurrentUser && <>&nbsp;
                            <Button variant={"secondary"} onClick={this.props.switchMode} style={{margin: '1px'}}>
                                Bearbeiten
                                <img src="images/icons/uicomponents/hammer_and_wrench.svg" style={{
                                    height: "30px",
                                    width: "auto",
                                    marginTop: "auto",
                                    marginBottom: "auto",
                                    marginLeft: "5px",
                                }}/></Button>&nbsp;
                            <Button variant={"danger"} onClick={this.deleteBarrier} style={{margin: '1px'}}>
                                Löschen
                                <img src="images/icons/uicomponents/trashcan.png" style={{
                                    height: "30px",
                                    width: "auto",
                                    marginTop: "auto",
                                    marginBottom: "auto",
                                    marginLeft: "5px",
                                }}/>
                            </Button> </>}
                    </Col>
                </Row>}
            </Container>
        </Jumbotron>
            {isBarrierPresent && <CardColumns>
                {this.props.barrier.solutions.map(solution =>
                    <Card key={solution._id}>
                        <Card.Body>
                            <Card.Text>{solution.text}</Card.Text>
                        </Card.Body>
                        <Card.Footer>
                            <VoteParagraph downvotes={solution.downVotes} upvotes={solution.upVotes}
                                           vote={solution.vote} barrier={false} _id={solution._id}/>
                            von {solution.user}
                        </Card.Footer>
                    </Card>
                )}
            </CardColumns>}
            <Modal size="lg" show={this.state.showModal} onHide={() => this.setState({showModal: false})}
                   aria-labelledby="add_solution_modal">
                <Modal.Header>
                    <Modal.Title id="add_solution_modal">
                        Lösungsvorschlag hinzufügen
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <FormField id='solution_text' label='Lösungsvorschlag'
                               onChange={(event) => this.setState({solutionText: event.target.value})}
                               value={this.state.solutionText} as='textarea'/>
                    <Button onClick={() => {
                        this.addSolution(this.props.barrier._id, this.state.solutionText);
                        this.setState({showModal: false});
                    }}>Lösung hinzufügen</Button> &nbsp;
                    <Button variant={"danger"} onClick={() => this.setState({showModal: false})}> Abbrechen </Button>
                </Modal.Body>
            </Modal>
        </>
    }
}