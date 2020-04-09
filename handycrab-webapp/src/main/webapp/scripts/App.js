import React from "react"
import ReactDOM from "react-dom"

import {HomePage} from "./pages/HomePage.js"
import {AboutPage} from "./pages/AboutPage.js"
import {ErrorPage} from "./pages/ErrorPage.js"
import {LoginPage} from "./pages/LoginPage"
import {RegisterPage} from "./pages/RegisterPage"

import {TitleBar} from "./components/TitleBar.js"
import {Footer} from "./components/Footer.js"
import {
    BrowserRouter as Router,
    Route,
    Switch
} from "react-router-dom"
import {SearchPage} from "./pages/SearchPage";

class App extends React.Component {
    render() {
        return (
            <div>
                <TitleBar/>
                <Router>
                    <Switch>
                        <Route exact path="/login" component={LoginPage}/>
                        <Route exact path="/register" component={RegisterPage}/>
                        <Route exact path="/" component={HomePage}/>
                        <Route exact path="/about" component={AboutPage}/>
                        <Route exact path="/search" component={SearchPage}/>
                        <Route component={ErrorPage}/>
                    </Switch>
                </Router>
                <Footer/>
            </div>
        )
    }
}

ReactDOM.render(<App/>, document.getElementById("app"));