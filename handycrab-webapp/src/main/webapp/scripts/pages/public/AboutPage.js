import React from "react"
import "../../../styles/pages/public/about-page.css"

export class AboutPage extends React.Component{
    render(){
        return(
            <div>
                <a href="/">&#60; Homepage</a>
                <div id="about" className="about-div">
                    <h1>Über diese Anwendung</h1>
                    <iframe className="about-iframe" src="legal/about.html" sandbox="" />
                </div>
                <div id="datenschutz"  className="about-div" name="datenschutzerklaerung">
                    <h1>Datenschutzerklärung</h1>
                    <iframe className="about-iframe" src="legal/datenschutzerklaerung.html" sandbox="" />
                </div>
                <div id="impressum" className="about-div" name="impressum">
                    <h1>Impressum</h1>
                    <iframe className="about-iframe" src="legal/impressum.html" sandbox=""/>
                </div>
            </div>
        )
    }
}