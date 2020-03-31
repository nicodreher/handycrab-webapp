import React from "react"
import "../../styles/components/menu-button.css"

export class MenuButton extends React.Component{

    buttonClickHandler(){
        var menubutton = document.getElementById("menubutton");

        if (menubutton.classList.contains("opened")){
            menubutton.classList.remove("opened");
        } else {
            menubutton.classList.add("opened");
        }
    }

    render(){
        return(
            <div id="menubutton" onClick={this.buttonClickHandler}>
                <div className="menubutton-burger"></div>
            </div>
        )
    }
}