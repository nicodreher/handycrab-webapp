import React from "react"
import "../../../styles/components/app/menu-button.css"

export class MenuButton extends React.Component{

    constructor(props){
        super(props);
    }

    buttonClickHandler = () => {
        this.props.menuAction();

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