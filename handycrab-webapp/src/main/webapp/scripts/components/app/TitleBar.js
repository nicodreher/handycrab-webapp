import React from "react"
import {MenuButton} from "./MenuButton.js"
import "../../../styles/components/app/title-bar.css"

export class TitleBar extends React.Component{
    constructor(props){
        super(props);
    }

    render(){
        return(
            <div id="titlebar">
                <MenuButton menuAction={this.props.menuAction}/>
                <p id="title">Handycrab</p>
            </div>
        )
    }
}