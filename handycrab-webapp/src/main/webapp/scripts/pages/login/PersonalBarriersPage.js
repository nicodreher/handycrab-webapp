import React from "react";
import {BarrierPreview} from "../../components/search/BarrierPreview"
import {getBarrierUrl} from "../../util/RestEndpoints";
import {deleteBarrierUrl} from "../../util/RestEndpoints";
import {OptionalAlert} from "../../components/app/OptionalAlert";

export class PersonalBarriersPage extends React.Component
{
    constructor(props)
    {
        super(props);

        this.state = {barriers: []}
    }

    componentDidMount(){
        this.getData().then(data =>{
            this.setState({barriers: data});
        });
    }

    getData = () =>{
        var hasErrorCode;

        return fetch(getBarrierUrl, {
                method: 'GET',
                cache: 'no-cache',
                credentials: 'include'
            }).then(response => {
                hasErrorCode = !response.ok;
                return response.json();
            }).then(data => {
                if (hasErrorCode) {
                    console.error("Errorcode: " + data.errorCode);
                    this.setState({error: errorCodeToMessage(data.errorCode)});
                    return [];
                } else {
                    return data;
                }
            })
            .catch(error => {
                console.error(error);
                this.setState({error: 'Ein unerwarteter Fehler ist aufgetreten'})
            });
    }

    deleteBarrier = (id) => {
        var hasErrorCode;

        fetch(deleteBarrierUrl, {
                method: 'DELETE',
                cache: 'no-cache',
                credentials: 'include',
                headers: new Headers({'Content-Type': 'application/json'}),
                body: JSON.stringify({_id: id})
            })
            .then(response => {
                hasErrorCode = !response.ok;
                return response.json();
            })
            .then(data => {
                if (hasErrorCode){
                    console.error("Errorcode: " + data.errorCode);
                    this.setState({error: errorCodeToMessage(data.errorCode)});
                }
            })
            .then(data => {
                this.getData().then(data =>{
                    this.setState({barriers: data});
                });
            })
            .catch(error => {
                console.error(error);
                this.setState({error: 'Ein unerwarteter Fehler ist aufgetreten'})
            });
    }

    clearError = () => {
        this.setState({error: null});
    }

    render(){
        return(
            <div>
                <OptionalAlert display={this.state.error} error={this.state.error} onClose={this.clearError} />

                <div className="results-content">
                    {this.state.barriers.map(barrier => <BarrierPreview key={barrier._id} title={barrier.title}
                     icon={barrier.picturePath} description={barrier.description} distance={barrier.distance}
                     upvotes={barrier.upVotes} downvotes={barrier.downVotes} vote={barrier.vote} _id={barrier._id}
                     deletable={true} deleteCommand={this.deleteBarrier}/>
                    )}
                 </div>
            </div>
        );
    }
}