/**
 * @param {number} code The errorCode
 * @return {String} The error message
 */
export function errorCodeToMessage(code) {
    switch (code) {
        case 1:
            return 'Unvollständige Anfrage';
        case 2:
            return 'Unauthorized';
        case 3:
            return 'E-Mail bereits verwendet';
        case 4:
            return 'Benutzername bereits verwendet';
        case 5:
            return 'E-Mail ungültig';
        case 6:
            return 'Login ungültig';
        case 7:
            return 'Benutzer nicht gefunden';
        case 8:
            return 'Invalide Koordinaten';
        case 9:
            return 'Barriere nicht gefunden';
        case 10:
            return 'Benutzer nicht gefunden';
        case 11:
            return 'Lösung nicht gefunden';
        case 12:
            return 'Nutzername ungültig. Ein Benutzername besteht aus 4 bis 16 Zeichen. Erlaubt sind dabei nur Buchstaben und Ziffern. Keine Sonderzeichen wie "$"';
        case 13:
            return 'Ungültiges Passwort. Ein Passwort besteht aus mindestens 6 und höchstens 100 Zeichen!';
        case 14:
            return 'Das hochgeladene Bild ist zu groß'
        case 15:
            return 'Das Format des Bildes wird nicht unterstützt. Bitte nutzen Sie eine .jpg- oder .png-Datei'
        case 16:
            return 'Das Bild für diese Barriere konnte nicht gefunden werden'
        case 17:
            return 'Invalide Anfrageparameter'
        case 18:
            return 'Invalide Barriere'
        default:
            return 'Ein unerwarteter Fehler ist aufgetreten';

    }

}