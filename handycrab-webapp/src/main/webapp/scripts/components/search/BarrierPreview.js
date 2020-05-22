import React from "react"
import "../../../styles/components/search/barrier-preview.css"
import {VoteParagraph} from "../general/VoteParagraph";

export class BarrierPreview extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const replacementIcon = "images/defaults/default_barrier.jpg";

        return (
            <div className="barrier-preview">
                <p className="barrier-title">{this.props.title}</p>

                <div className="barrier-content">
                    <img className="barrier-img"
                         src={this.props.icon !== undefined ? this.props.icon : replacementIcon}/>
                    <p className="barrier-short-descr">{this.props.description}</p>
                </div>

                <div>
                    <VoteParagraph downvotes={this.props.downvotes} upvotes={this.props.upvotes} _id={this.props._id}
                                   barrier={true} vote={this.props.vote}/>

                    {!this.props.distance === null || !this.props.distance === undefined &&
                    <span className="metric-distance">
                        <img src="images/icons/uicomponents/pin.png"/>
                        <p>{this.props.distance}m Abstand</p>
                    </span>
                    }
                </div>
            </div>
        );
    }
}