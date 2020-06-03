import React from "react"
import ReactDOM from "react-dom"

import {AboutPage} from "./pages/public/AboutPage"
import {ErrorPage} from "./pages/public/ErrorPage"
import {LoginPage} from "./pages/public/LoginPage"
import {RegisterPage} from "./pages/public/RegisterPage"
import {SearchPage} from "./pages/login/SearchPage";
import {SearchResultsPage} from "./pages/login/SearchResultsPage"
import {PersonalBarriersPage} from "./pages/login/PersonalBarriersPage"

import {TitleBar} from "./components/app/TitleBar"
import {Footer} from "./components/app/Footer"
import {MainMenuItem} from "./components/app/MainMenuItem"
import {Button} from "react-bootstrap";

import {
    BrowserRouter as Router,
    Route,
    Switch
} from "react-router-dom"
import {isLoggedIn, logout, updateCurrentUser} from "./util/Auth";
import {ProtectedRoute} from "./components/app/ProtectedRoute";
import {BarrierAddPage} from "./pages/login/BarrierAddPage";
import {BarrierDetailViewPage} from "./pages/login/BarrierDetailViewPage";

const duration_5_min = 5 * 1000 * 60;

class App extends React.Component {

    constructor(props) {
        super(props);

        var showCookieNotice = sessionStorage.getItem("cookieNotice") == null ? true :
            sessionStorage.getItem("cookieNotice") == "true";

        this.state = {menuOpen: false, showCookieNotice: showCookieNotice};
    }

    componentDidMount() {
        this.intervalID = setInterval(() => updateCurrentUser(), duration_5_min);
    }

    componentWillUnmount() {
        clearInterval(this.intervalID);
    }

    toggleMenu = () => {
        if (this.state.menuOpen) {
            this.setState({menuOpen: false});
        } else {
            this.setState({menuOpen: true});
        }
    }

    acceptCookies = () => {
        sessionStorage.setItem("cookieNotice", false);
        this.setState({showCookieNotice: false});
    }

    render() {
        const loggedIn = isLoggedIn();
        return (
            <div id="app-flex-div">
                <TitleBar menuAction={this.toggleMenu}/>
                <div id="content-div">
                    <div id="main-menu" style={{visibility: this.state.menuOpen ? "visible" : "hidden"}}>
                        {!loggedIn &&
                        <MainMenuItem icon="images/icons/menu/login_icon.png" altText="login" title="Anmeldung"
                                      url="/login"/>}
                        {!loggedIn &&
                        <MainMenuItem icon="images/icons/menu/register_icon.png" altText="register"
                                      title="Registrierung" url="/register"/>}
                        {loggedIn &&
                        <MainMenuItem icon="images/icons/menu/search_icon.png" altText="search" title="Suche"
                                      url="/"/>}
                        {loggedIn &&
                        <MainMenuItem icon="images/icons/menu/add_icon.svg" altText="add" title="Barriere hinzufügen"
                                      url="/add"/>}
                        {loggedIn &&
                        <MainMenuItem icon="images/icons/menu/my_barriers.png" altText="my barriers"
                                      title="Meine Barrieren" url="/my-barriers"/>}
                        <MainMenuItem icon="images/icons/menu/info_icon.png" altText="about" title="Über die Anwendung"
                                      url="/about"/>
                        {loggedIn &&
                        <MainMenuItem icon="images/icons/menu/logout_icon.svg" altText="logout" title="Abmelden"
                                      onClick={logout}/>}
                    </div>
                    <Router>
                        <Switch>
                            <Route exact path="/login" component={LoginPage}/>
                            <Route exact path="/register" component={RegisterPage}/>
                            <ProtectedRoute exact path="/" component={SearchPage}/>
                            <Route exact path="/about" component={AboutPage}/>
                            <ProtectedRoute exact path="/results" component={SearchResultsPage}/>
                            <ProtectedRoute exact path="/add" component={BarrierAddPage}/>
                            <ProtectedRoute exact path="/detail" component={BarrierDetailViewPage}/>
                            <ProtectedRoute exact path="/my-barriers" component={PersonalBarriersPage}/>
                            <Route component={ErrorPage}/>
                        </Switch>
                    </Router>
                </div>

                {this.state.showCookieNotice &&
                    <div style={{backgroundColor: "#ffd863", display: "flex", justifyContent: "space-between",
                                 width: "100%", padding: "5px", position: "absolute", bottom: "32px"}}>
                        <p style={{margin: 0}}>Diese Website verwendet Cookies. Mehr dazu in der {" "}
                            <a href="/about?el=datenschutz">Datenschutzerklärung.</a>
                        </p>
                        <Button style={{height: "25px", paddingTop: 0, marginTop: "auto", marginLeft: "5px",
                        marginBottom: "auto"}} onClick={this.acceptCookies}>ok</Button>
                    </div>}

                <Footer/>
            </div>
        )
    }
}

ReactDOM.render(<App/>, document.getElementById("app"));