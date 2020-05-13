import React from "react"
import "../../../styles/components/app/footer.css"

export class Footer extends React.Component{
    render(){
        return(
            <div className="footer">
                <a className="footer-element" href="/about?el=datenschutz">Datenschutzerklärung</a>
                <a className="footer-element" href="/about?el=impressum">Impressum</a>
            </div>
        )
    }
}