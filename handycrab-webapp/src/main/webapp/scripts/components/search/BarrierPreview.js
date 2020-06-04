import React from "react"
import "../../../styles/components/search/barrier-preview.css"
import {VoteParagraph} from "../general/VoteParagraph";
import {Modal, Button} from "react-bootstrap";

export class BarrierPreview extends React.Component {

    constructor(props) {
        super(props);

        this.state = {showModal: false};
    }

    deleteBarrier = () => {

        if (this.props.deleteCommand != null){
            this.props.deleteCommand(this.props._id);
        }

        this.setState({showModal: false});
    }

    render() {
        const replacementIcon = "images/defaults/default_barrier.jpg";

        return (
            <div className="barrier-preview">
                <div className="barrier-title">
                    <a href={'/detail?id='+this.props._id} style={{flex: 1}}>
                        <p>
                            {this.props.title}
                        </p>
                    </a>
                    {this.props.deletable === true &&
                        <img src="images/icons/uicomponents/trashcan.png" style={{height: "30px", width: "auto",
                         marginTop: "auto", marginBottom:"auto", marginLeft:"5px", cursor: "pointer"}}
                         onClick={() => {this.setState({showModal: true})}}/>
                    }
                </div>

                <div className="barrier-content">
                    <img className="barrier-img"
                         src={this.props.icon !== undefined ? this.props.icon : replacementIcon}/>
                    <p className="barrier-short-descr">{this.props.description}</p>
                </div>

                <div>
                    <VoteParagraph downvotes={this.props.downvotes} upvotes={this.props.upvotes} _id={this.props._id}
                                   barrier={true} vote={this.props.vote}/>

                    {!(this.props.distance == null)  &&
                    <span className="metric-distance">
                        <img src="images/icons/uicomponents/pin.png"/>
                        <p>ca. {this.props.distance}m Abstand</p>
                    </span>
                    }
                </div>

                <Modal show={this.state.showModal} onHide={() => this.setState({showModal: false})} backdrop="static"
                 keyboard={false}>
                    <Modal.Header closeButton onHide={() => this.setState({showModal: false})}>
                        <Modal.Title>Barriere löschen?</Modal.Title>
                    </Modal.Header>

                    <Modal.Body>
                        <p>Soll die Barriere {this.props.title} unwiederruflich gelöscht werden?</p>
                    </Modal.Body>

                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => this.setState({showModal: false})}>abbrechen</Button>
                        <Button variant="primary" onClick={this.deleteBarrier}>löschen</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        );
    }
}