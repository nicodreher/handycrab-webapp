import React from "react"
import "../../../styles/pages/public/error-page.css"

export class ErrorPage extends React.Component{
    render(){
        return(
            <div>
                <img id="error-cat" src="images/error/error_cat.jpg" alt="Fehler-Katze"/>
                <p id="error-text">Ups 😔, da ist wohl etwas schiefgelaufen... <a href="/">Hier gehts heim</a></p>
            </div>
        );
    }
}