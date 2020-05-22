import {currentUserUrl, logoutUrl} from "./RestEndpoints";

const loggedInKey = 'loggedIn';
const currentUserKey = 'getCurrentUser'

/**
 * Logs the user in locally.
 * This extra step is necessary since the client cannot access or check
 * for the cookie the backend sets. Thus a special key is set in the sessionStorage.
 */
export function logIn(user) {
    sessionStorage.setItem(loggedInKey, "true");
    sessionStorage.setItem(currentUserKey, JSON.stringify(user));
}

export function getCurrentUser() {
    return JSON.parse(sessionStorage.getItem(currentUserKey));
}

/**
 * Checks if a user is logged in.
 *
 * @returns {boolean} Whether the user is logged in
 */
export function isLoggedIn() {
    const value = sessionStorage.getItem(loggedInKey);
    if (value === null) {
        fetch(currentUserUrl, {
            method: 'GET',
            cache: 'no-cache',
            mode: 'cors',
            credentials: 'include',
            headers: new Headers({
                'Content-Type': 'application/json'
            })
        }).then((response) => {
            if (response.ok) {
                logIn();
                response.json().then(data => logIn(data)).catch(error => console.error(error));
                location.pathname = sessionStorage.getItem("destination") ? sessionStorage.getItem("destination") : "/search";
            } else {
                console.error(response.status + ': ' + response.statusText);
                sessionStorage.setItem(loggedInKey, 'false');
                sessionStorage.removeItem(currentUserKey);
            }
        }).catch((error) => console.warn(error));
    }
    return "true" === sessionStorage.getItem(loggedInKey);
}

export function logoutLocally() {
    sessionStorage.removeItem(loggedInKey);
    sessionStorage.removeItem(currentUserKey);
}

/**
 * Destroys the session a user has with the backend and logs the user out locally.
 */
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