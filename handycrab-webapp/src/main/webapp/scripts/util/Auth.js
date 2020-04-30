import {logoutUrl} from "./RestEndpoints";

export function logIn() {
    sessionStorage.setItem("loggedIn", "true");
}

export function isLoggedIn() {
    return "true" === sessionStorage.getItem("loggedIn");
}

export function logoutLocally() {
    sessionStorage.removeItem("loggedIn");
}

export function logout() {
    logoutLocally();
    fetch(logoutUrl, {
        method: "POST",
        credentials: "include",
        cache: "no-cache",
        mode: "cors"
    }).then((response) => {
        window.location.pathname = "/login";
    }).catch((error) => {
            console.log(error);
            window.location.pathname = "/error";
        }
    );
}