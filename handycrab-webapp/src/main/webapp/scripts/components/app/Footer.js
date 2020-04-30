import React from "react"
import "../../../styles/components/app/footer.css"

export class Footer extends React.Component{
    render(){
        return(
            <div className="footer">
                <a className="footer-element" href="/about">Datenschutzerklärung</a>
                <a className="footer-element" href="/about">Impressum</a>
            </div>
        )
    }
}