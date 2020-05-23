import {currentUserUrl, logoutUrl} from "./RestEndpoints";
import {errorCodeToMessage} from "./errorCode";

const loggedInKey = 'loggedIn';
const currentUserKey = 'currentUser'

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
            return response.json();
        }).then(data => {
            if (!data.errorCode) {
                logIn(data);
                location.href = sessionStorage.getItem("destination") ? sessionStorage.getItem("destination") : "/search";
            } else {
                console.error(errorCodeToMessage(data.errorCode));
                logoutLocally();
            }
        }).catch((error) => console.warn(error));
    }
    return "true" === sessionStorage.getItem(loggedInKey);
}

export function logoutLocally() {
    sessionStorage.removeItem(loggedInKey);
    sessionStorage.removeItem(currentUserKey);
}

export function updateCurrentUser() {
    fetch(currentUserUrl, {
        method: 'GET',
        cache: 'no-cache',
        mode: 'cors',
        credentials: 'include',
        headers: new Headers({
            'Content-Type': 'application/json'
        })
    }).then(response => {
        return response.json();
    }).then(data => sessionStorage.setItem(currentUserKey, JSON.stringify(data))).catch(error => console.error(error));
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
            console.error(error);
            window.location.pathname = "/error";
        }
    );
}