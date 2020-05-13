import React from "react";
import "../../../styles/pages/public/about-page.css";
import {Tab, Tabs} from "react-bootstrap";

export class AboutPage extends React.Component{
    render(){
        var el = new URLSearchParams(window.location.search).get("el");
        el = ["about", "datenschutz", "impressum"].includes(el) ? el : "about";

        return(
            <div>
                <Tabs defaultActiveKey={el} id="about-tabs">
                  <Tab eventKey="about" title="About">
                    <iframe className="about-iframe" src="legal/about.html" sandbox="" />
                  </Tab>
                  <Tab eventKey="datenschutz" title="Datenschutzerklärung">
                    <iframe className="about-iframe" src="legal/datenschutzerklaerung.html" sandbox="" />
                  </Tab>
                  <Tab eventKey="impressum" title="Impressum">
                    <iframe className="about-iframe" src="legal/impressum.html" sandbox=""/>
                  </Tab>
                </Tabs>
            </div>
        )
    }
}