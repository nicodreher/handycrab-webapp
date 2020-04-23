import React from "react";
import {Redirect, Route} from "react-router-dom";
import {isLoggedIn} from "../Auth";

export function ProtectedRoute(props) {
    return isLoggedIn() ? <Route {...props}/> : <Redirect to={'/login'}/>
}