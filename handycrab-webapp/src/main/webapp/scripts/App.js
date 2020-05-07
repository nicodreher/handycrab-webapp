import React from "react"
import ReactDOM from "react-dom"

import {HomePage} from "./pages/public/HomePage"
import {AboutPage} from "./pages/public/AboutPage"
import {ErrorPage} from "./pages/public/ErrorPage"
import {LoginPage} from "./pages/public/LoginPage"
import {RegisterPage} from "./pages/public/RegisterPage"
import {SearchPage} from "./pages/login/SearchPage";
import {SearchResultsPage} from "./pages/login/SearchResultsPage"

import {TitleBar} from "./components/app/TitleBar"
import {Footer} from "./components/app/Footer"
import {MainMenuItem} from "./components/app/MainMenuItem"

import {
    BrowserRouter as Router,
    Route,
    Switch
} from "react-router-dom"
import {isLoggedIn, logout} from "./util/Auth";
import {ProtectedRoute} from "./components/app/ProtectedRoute";

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
        return (
            <div id="app-flex-div">
                <TitleBar menuAction={this.toggleMenu}/>
                <div id="content-div">
                    <div id="main-menu" style={{visibility: this.state.menuOpen ? "visible" : "hidden"}}>
                        <MainMenuItem icon="images/icons/menu/home_icon.png" altText="home" title="Home" url="/" />
                        {!isLoggedIn() &&
                        <MainMenuItem icon="images/icons/menu/login_icon.png" altText="login" title="Anmeldung" url="/login" />}
                        <MainMenuItem icon="images/icons/menu/register_icon.png" altText="register" title="Registrierung" url="/register" />
                        {isLoggedIn() &&
                        <MainMenuItem icon="images/icons/menu/search_icon.png" altText="search" title="Suche" url="/search" />}
                        <MainMenuItem icon="images/icons/menu/info_icon.png" altText="about" title="Über die Anwendung" url="/about" />
                        {isLoggedIn() &&
                        <MainMenuItem icon="images/icons/menu/logout_icon.svg" altText="logout" title="Abmelden" onClick={logout} />}
                    </div>
                    <Router>
                        <Switch>
                            <Route exact path="/login" component={LoginPage}/>
                            <Route exact path="/register" component={RegisterPage}/>
                            <ProtectedRoute exact path="/" component={HomePage}/>
                            <Route exact path="/about" component={AboutPage}/>
                            <ProtectedRoute exact path="/search" component={SearchPage}/>
                            <ProtectedRoute exact path="/results" component={SearchResultsPage}/>
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