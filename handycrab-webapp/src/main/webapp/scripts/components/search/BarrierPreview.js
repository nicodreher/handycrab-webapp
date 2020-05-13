import React from "react"
import "../../../styles/components/search/barrier-preview.css"

export class BarrierPreview extends React.Component{

    constructor(props){
        super(props);
        this.state = {vote: this.props.vote, upvotes: this.props.upvotes, downvotes: this.props.downvotes};
    }

    onVoteClick = (clickedVote) =>
    {
        var upvotes;
        var downvotes;
        var vote;

        if (clickedVote === "UP")
        {
            // if previous state was downvote remove one, the user changed their mind
            downvotes = this.state.vote === "DOWN" ? this.state.downvotes - 1 : this.state.downvotes;

            // if previous state was upvote remove one, because the user clicked to remove their vote, else add one
            upvotes = this.state.vote === "UP" ? this.state.upvotes - 1 : this.state.upvotes + 1;

            // if previous state was upvote, set the current vote to none, because user removed their vote, else set it
            // to upvote
            vote = this.state.vote === "UP" ? "NONE" : "UP";
        }
        else
        {
            // if previous state was upvote remove one, the user changed their mind
            upvotes = this.state.vote === "UP" ? this.state.upvotes - 1 : this.state.upvotes;

            // if previous state was downvote remove one, because the user clicked to remove their vote, else add one
            downvotes = this.state.vote === "DOWN" ? this.state.downvotes -1 : this.state.downvotes + 1;

            // if previous state was downvote, set the current vote to none, because user removed their vote, else set
            // it to downvote
            vote = this.state.vote === "DOWN" ? "NONE" : "DOWN";
        }

        this.setState({vote: vote, upvotes: upvotes, downvotes: downvotes})
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
        var replacementIcon = "images/defaults/default_barrier.jpg";

        return(
        <div className="barrier-preview">
            <p className="barrier-title">{this.props.title}</p>

            <div className="barrier-content">
                <img className="barrier-img" src={this.props.icon !== undefined ? this.props.icon : replacementIcon}/>
                <p className="barrier-short-descr">{this.props.description}</p>
            </div>

            <div>
                <img className="vote-image" src={this.decideOnUpvoteImg()} onClick={() => this.onVoteClick("UP")}/>
                <p className="vote-paragraph">{this.state.upvotes}</p>
                <img className="vote-image" src={this.decideOnDownvoteImg()} onClick={() => this.onVoteClick("DOWN")}/>
                <p className="vote-paragraph">{this.state.downvotes}</p>

                {!this.props.distance == null || !this.props.distance == undefined &&
                    <span className="metric-distance">
                        <img src="images/icons/uicomponents/pin.png" />
                        <p>{this.props.distance}m Abstand</p>
                    </span>
                }
            </div>
        </div>
        );
    }
}