import {currentUserUrl, logoutUrl} from "./RestEndpoints";

/**
 * Logs the user in locally.
 * This extra step is necessary since the client cannot access or check
 * for the cookie the backend sets. Thus a special key is set in the sessionStorage.
 */
export function logIn() {
    sessionStorage.setItem("loggedIn", "true");
}

/**
 * Checks if a user is logged in.
 *
 * @returns {boolean} Whether the user is logged in
 */
export function isLoggedIn() {
    const value = sessionStorage.getItem("loggedIn");
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
                location.pathname = sessionStorage.getItem("destination") ? sessionStorage.getItem("destination") : "/search";
            } else {
                sessionStorage.setItem('loggedIn', 'false');
            }
        }).catch((error) => console.warn(error));
    }
    return "true" === sessionStorage.getItem("loggedIn");
}

export function logoutLocally() {
    sessionStorage.setItem('loggedIn', 'false');
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