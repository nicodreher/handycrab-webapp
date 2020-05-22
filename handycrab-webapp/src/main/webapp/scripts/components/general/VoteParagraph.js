import React, {Component} from "react";
import "../../../styles/components/search/barrier-preview.css"
import {voteForBarrierUrl, voteForSolutionUrl} from "../../util/RestEndpoints";

export class VoteParagraph extends Component {
    constructor(props) {
        super(props);
        this.state = {vote: this.props.vote, upvotes: this.props.upvotes, downvotes: this.props.downvotes};
    }

    decideOnUpvoteImg() {
        if (this.state.vote === "UP") {
            return "images/icons/uicomponents/upvote_clicked.svg";
        }
        return "images/icons/uicomponents/upvote.svg";
    }

    decideOnDownvoteImg() {
        if (this.state.vote === "DOWN") {
            return "images/icons/uicomponents/downvote_clicked.svg";
        }
        return "images/icons/uicomponents/downvote.svg";
    }

    onVoteClick = (clickedVote) => {
        let upvotes;
        let downvotes;
        let vote;
        let voteResult;
        if (clickedVote === "UP") {
            // if previous state was downvote remove one, the user changed their mind
            downvotes = this.state.vote === "DOWN" ? this.state.downvotes - 1 : this.state.downvotes;
            voteResult = this.state.vote === "UP" ? "NONE" : "UP";
            // if previous state was upvote remove one, because the user clicked to remove their vote, else add one
            upvotes = this.state.vote === "UP" ? this.state.upvotes - 1 : this.state.upvotes + 1;

            // if previous state was upvote, set the current vote to none, because user removed their vote, else set it
            // to upvote
            vote = this.state.vote === "UP" ? "NONE" : "UP";
        } else {
            // if previous state was upvote remove one, the user changed their mind
            upvotes = this.state.vote === "UP" ? this.state.upvotes - 1 : this.state.upvotes;
            voteResult = this.state.vote === "DOWN" ? "NONE" : "DOWN";
            // if previous state was downvote remove one, because the user clicked to remove their vote, else add one
            downvotes = this.state.vote === "DOWN" ? this.state.downvotes - 1 : this.state.downvotes + 1;

            // if previous state was downvote, set the current vote to none, because user removed their vote, else set
            // it to downvote
            vote = this.state.vote === "DOWN" ? "NONE" : "DOWN";
        }
        const url = this.props.barrier ? voteForBarrierUrl : voteForSolutionUrl;
        fetch(url, {
            method: 'PUT',
            cache: 'no-cache',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            credentials: 'include',
            body: JSON.stringify(
                {
                    _id: this.props._id,
                    vote: voteResult,
                })
        }).catch(error => console.error(error));

        this.setState({vote: vote, upvotes: upvotes, downvotes: downvotes})
    }

    render() {
        return <span>
            <img className="vote-image" src={this.decideOnUpvoteImg()} onClick={() => this.onVoteClick("UP")}/>
            <p className="vote-paragraph">{this.state.upvotes}</p>
            <img className="vote-image" src={this.decideOnDownvoteImg()}
                 onClick={() => this.onVoteClick("DOWN")}/>
            <p className="vote-paragraph">{this.state.downvotes}</p>
        </span>
    }
}