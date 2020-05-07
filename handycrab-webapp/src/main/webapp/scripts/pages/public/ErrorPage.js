import React from "react"
import "../../../styles/pages/public/error-page.css"

export class ErrorPage extends React.Component{
    render(){
        return(
            <div className="error-div">
                <h1 className="four-o-four-text">404</h1>
                <p className="description">Ups...hier ist wohl etwas schiefgelaufen...</p>
                <a href="/" className="back-home">« Zurück zur Homepage</a>
            </div>
        );
    }
}