import React from "react"
import {MenuButton} from "./MenuButton.js"
import "../../styles/components/title-bar.css"

export class TitleBar extends React.Component{
    render(){
        return(
            <div id="titlebar">
                <MenuButton />
                <p id="title">Handycrab</p>
            </div>
        )
    }
}