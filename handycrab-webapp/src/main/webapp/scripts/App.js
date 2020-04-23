import React from "react"
import ReactDOM from "react-dom"

import {HomePage} from "./pages/HomePage.js"
import {AboutPage} from "./pages/AboutPage.js"
import {ErrorPage} from "./pages/ErrorPage.js"
import {LoginPage} from "./pages/LoginPage"
import {RegisterPage} from "./pages/RegisterPage"

import {TitleBar} from "./components/TitleBar.js"
import {Footer} from "./components/Footer.js"
import {MainMenuItem} from "./components/MainMenuItem.js"

import {
    BrowserRouter as Router,
    Route,
    Switch
} from "react-router-dom"
import {SearchPage} from "./pages/SearchPage";
import {isLoggedIn, logout} from "./Auth";
import {ProtectedRoute} from "./components/ProtectedRoute";

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {menuOpen: false};
    }

    toggleMenu = () => {
        if (this.state.menuOpen) {
            this.setState({menuOpen: false});
        } else {
            this.setState({menuOpen: true});
        }
    }

    render() {
        const needToRedirectToHttps = window.location.protocol === "http:";
        if (needToRedirectToHttps) {
            if (window.location.hostname.includes("localhost") || window.location.hostname.includes("127.0.0.1")) {
                console.log("Debug environment, skipping redirect")
            } else {
                window.location.href = window.location.href.replace("http://", "https://");
            }
        }
        return (
            <div>
                <TitleBar menuAction={this.toggleMenu}/>
                <div id="content-div">
                    <div id="main-menu" style={{visibility: this.state.menuOpen ? "visible" : "hidden"}}>
                        <MainMenuItem icon="images/icons/home_icon.png" altText="home" title="Home" url="/"/>
                        <MainMenuItem icon="images/icons/login_icon.png" altText="login" title="Anmeldung"
                                      url="/login"/>
                        <MainMenuItem icon="images/icons/register_icon.png" altText="register" title="Registrierung"
                                      url="/register"/>
                        {isLoggedIn() &&
                        <MainMenuItem icon="images/icons/search_icon.png" altText="search" title="Suche"
                                      url="/search"/>}
                        <MainMenuItem icon="images/icons/info_icon.png" altText="about" title="Über die Anwendung"
                                      url="/about"/>
                        {isLoggedIn() &&
                        <MainMenuItem icon="images/icons/logout_icon.svg" altText="logout" title="Abmelden"
                                      onClick={logout}/>}
                    </div>
                    <Router>
                        <Switch>
                            <Route exact path="/login" component={LoginPage}/>
                            <Route exact path="/register" component={RegisterPage}/>
                            <ProtectedRoute exact path="/" component={HomePage}/>
                            <Route exact path="/about" component={AboutPage}/>
                            <ProtectedRoute exact path="/search" component={SearchPage}/>
                            <Route component={ErrorPage}/>
                        </Switch>
                    </Router>
                </div>
                <Footer/>
            </div>
        )
    }
}

ReactDOM.render(<App/>, document.getElementById("app"));