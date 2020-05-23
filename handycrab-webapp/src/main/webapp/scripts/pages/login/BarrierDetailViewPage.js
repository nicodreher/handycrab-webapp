import React from "react";
import {Col, Container, Row} from "react-bootstrap";
import {getBarrierUrl, getUsernameUrl} from "../../util/RestEndpoints";
import {AddForm} from "../../components/add/AddForm";
import {BarrierDetailView} from "../../components/add/BarrierDetailView";
import {OptionalAlert} from "../../components/app/OptionalAlert";

export class BarrierDetailViewPage extends React.Component {
    barrierPlaceHolder = {
        title: 'Unbekannte Id',
        description: "Die angegebene Id konnte nicht gefunden werden.",
        solutions: []
    };

    constructor(props) {
        super(props);
        this.state = {
            error: '',
            barrier: undefined,
            editMode: false
        };
    }

    componentDidMount() {
        const searchParams = new URLSearchParams(window.location.search);
        if (searchParams.id === null) {
            this.setState({
                error: 'Die gewählte Barriere existiert nicht.',
                barrier: this.barrierPlaceHolder
            });
        } else
            fetch(getBarrierUrl + '?_id=' + searchParams.get("id"), {
                method: 'GET',
                cache: 'no-cache',
                credentials: 'include'
            }).then(response => {
                return response.json();
            }).then(data => {
                if (data.errorCode !== undefined) {
                    this.setState({
                        error: 'Ein unerwarteter Fehler ist aufgetreten',
                        barrier: this.barrierPlaceHolder
                    });
                } else {
                    this.setState({barrier: data});
                    return data;
                }
            }).then(barrier => {
                this.getUserName(barrier.userId).then(name => this.updateName(name)).catch(error => console.warn(error));
                return this.resolveNames(barrier.solutions);
            }).then(names => {
                const copy = this.state.barrier;
                copy.solutions.forEach((solution, index) => copy.solutions[index].user = names[index]);
                copy.solutions.sort((a, b) => {
                    const aKarma = a.upVotes - a.downVotes;
                    const bKarma = b.upVotes - b.downVotes;
                    return bKarma - aKarma;
                })
                this.setState({barrier: copy});
            }).catch(error => console.error(error));
    }

    updateName = (name) => {
        const copy = this.state.barrier;
        copy.user = name;
        this.setState({barrier: copy});
    }
    switchMode = () => {
        this.setState({editMode: !this.state.editMode});
    }

    /**
     *
     * @param solutions {[]}
     * @return {Promise<[string]>}
     */
    resolveNames = (solutions) => new Promise((resolve, reject) => {
        if (solutions === undefined || solutions === null) {
            return resolve([]);
        }
        const promises = solutions.map(solution => this.getUserName(solution.userId));
        return resolve(Promise.all(promises));
    })

    /**
     *
     * @param id {string}
     * @return {Promise<string>}
     */
    getUserName = (id) => {
        return fetch(getUsernameUrl + '?id=' + id, {
            method: 'GET',
            cache: 'no-cache',
            credentials: 'include',
        }).then(response => response.json()).then(json => {
            return json.result;
        }).catch(error => {
            console.error(error);
            return 'Anonymer Nutzer'
        });
    }

    render() {
        return <Container>
            <div>&nbsp;</div>
            <Row>
                <Col>
                    <OptionalAlert display={this.state.error} error={this.state.error}
                                   onClose={() => this.setState({error: ''})}/>
                    {!(this.state.editMode) &&
                    <BarrierDetailView barrier={this.state.barrier} switchMode={this.switchMode}/>}
                    {(this.state.editMode) && <AddForm barrier={this.state.barrier} switchMode={this.switchMode}/>}
                </Col>
            </Row>
        </Container>
    }
}