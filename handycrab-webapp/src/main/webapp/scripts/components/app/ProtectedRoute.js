import React from "react";
import {Redirect, Route} from "react-router-dom";
import {isLoggedIn} from "../../util/Auth";

export function ProtectedRoute(props) {
    const loggedIn = isLoggedIn();
    if (!loggedIn){
        sessionStorage.setItem("destination", window.location.pathname);
    }
    return loggedIn ? <Route {...props}/> : <Redirect to={'/login'}/>
}