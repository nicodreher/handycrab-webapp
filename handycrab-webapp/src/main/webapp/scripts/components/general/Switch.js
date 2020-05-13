import React from "react"
import ReactDOM from "react-dom"
import "../../../styles/components/general/switch.css"

export class Switch extends React.Component{

    constructor(props){
        super(props);
    }

    render(){
        var classNames = "switch "
        classNames += this.props.on ? "on" : ""

        return(
            <p className={classNames} onClick={this.props.onClick}></p>
        );
    }
}