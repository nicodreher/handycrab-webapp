import React from "react"
import ReactDOM from "react-dom"

import {HomePage} from "./pages/HomePage.js"
import {AboutPage} from "./pages/AboutPage.js"
import {ErrorPage} from "./pages/ErrorPage.js"
import {LoginPage} from "./pages/LoginPage"
import {RegisterPage} from "./pages/RegisterPage"
import {SearchResultsPage} from "./pages/SearchResultsPage"

import {TitleBar} from "./components/TitleBar.js"
import {Footer} from "./components/Footer.js"
import {MainMenuItem} from "./components/MainMenuItem.js"

import {
    BrowserRouter as Router,
    Route,
    Switch
} from "react-router-dom"
import {SearchPage} from "./pages/SearchPage";
class App extends React.Component {

    constructor(props){
        super(props);
        this.state = {menuOpen: false, loggedIn: false};
    }

    toggleMenu = () => {
        if (this.state.menuOpen){
            this.setState({menuOpen: false});
        } else {
            this.setState({menuOpen: true});
        }
    }

    render() {
        return (
            <div id="app-flex-div">
                <TitleBar menuAction={this.toggleMenu}/>
                <div id="content-div">
                    <div id="main-menu" style={{visibility: this.state.menuOpen ? "visible" : "hidden"}}>
                        <MainMenuItem icon="images/icons/home_icon.png" altText="hom  e" title="Home" url="/" />
                        <MainMenuItem icon="images/icons/login_icon.png" altText="login" title="Anmeldung" url="/login" />
                        <MainMenuItem icon="images/icons/register_icon.png" altText="register" title="Registrierung" url="/register" />
                        {this.state.loggedIn &&
                        <MainMenuItem icon="images/icons/search_icon.png" altText="search" title="Suche" url="/search" />}
                        <MainMenuItem icon="images/icons/info_icon.png" altText="about" title="Über die Anwendung" url="/about" />
                    </div>
                    <Router>
                        <Switch>
                            <Route exact path="/login" component={LoginPage}/>
                            <Route exact path="/register" component={RegisterPage}/>
                            <Route exact path="/" component={HomePage}/>
                            <Route exact path="/about" component={AboutPage}/>
                            <Route exact path="/search" component={SearchPage}/>
                            <Route exact path="/results" component={SearchResultsPage}/>
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