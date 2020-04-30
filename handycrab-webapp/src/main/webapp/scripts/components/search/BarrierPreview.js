import React from "react"
import "../../../styles/components/search/barrier-preview.css"

export class BarrierPreview extends React.Component{

    constructor(props){
        super(props);
        this.state = {vote: this.props.vote, upvotes: this.props.upvotes, downvotes: this.props.downvotes};
    }

    onUpvoteClick = () =>{
        if (this.state.vote == "NONE"){
            this.setState({vote: "UP", upvotes: this.state.upvotes + 1});
        }
    }

    onDownvoteClick = () =>{
        if (this.state.vote == "NONE"){
            this.setState({vote: "DOWN", downvotes: this.state.downvotes + 1});
        }
    }

    decideOnUpvoteImg(){
        if (this.state.vote == "UP"){
            return "images/icons/uicomponents/upvote_clicked.svg";
        }
        return "images/icons/uicomponents/upvote.svg";
    }

    decideOnDownvoteImg(){
            if (this.state.vote == "DOWN"){
                return "images/icons/uicomponents/downvote_clicked.svg";
            }
            return "images/icons/uicomponents/downvote.svg";
    }

    render(){
        return(
        <div className="barrier-preview">
            <p className="barrier-title">{this.props.title}</p>

            <div className="barrier-content">
                <img className="barrier-img" src={this.props.icon}/>
                <p className="barrier-short-descr">{this.props.description}</p>
            </div>

            <div>
                <span className="metric-distance">
                    <img src="images/icons/uicomponents/pin.png" />
                    <p>{this.props.distance}m Abstand</p>
                </span>
                <img className="vote-image" src={this.decideOnUpvoteImg()} onClick={this.onUpvoteClick}/>
                <p className="vote-paragraph">{this.state.upvotes}</p>
                <img className="vote-image" src={this.decideOnDownvoteImg()} onClick={this.onDownvoteClick}/>
                <p className="vote-paragraph">{this.state.downvotes}</p>
            </div>
        </div>
        );
    }
}