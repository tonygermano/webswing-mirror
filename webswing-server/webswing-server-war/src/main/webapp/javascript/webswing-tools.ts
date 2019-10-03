import 'promise-polyfill/src/polyfill';
import "core-js/stable";
import "regenerator-runtime/runtime";

export async function loadTranslations(baseUrl?:string) {
    baseUrl = baseUrl?baseUrl:'';
    const langs = await loadAvailableLanguages(baseUrl);
    const msg = await loadLocalizedMsgs(baseUrl);

    return {langs, msg}
}


export function getParam(name:string) {
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var results = new RegExp("[\\?&]" + name + "=([^&#]*)").exec(location.href);
    return results == null ? null : decodeURIComponent(results[1]);
}

async function loadAvailableLanguages(baseUrl:string) {
    let lang = getSelectedLang();
    try {
        const translatedLangs = await load(baseUrl+"javascript/nls/" + lang + "/select.json");
        return translatedLangs;
    } catch (e) {
        try {
            const defaultLangs = await load(baseUrl+"javascript/nls/select.json");
            return defaultLangs;
        } catch (e1) {
            return null;
        }
    }
}

async function loadLocalizedMsgs(baseUrl:string) {
    let lang = getSelectedLang();
    let langParts =lang.split("-");
    while(langParts.length>0){
        try {
            const messages = await load(baseUrl+"javascript/nls/" + langParts.join("-")+ "/msg.json");
            return messages;
        } catch (e) {
            langParts.pop();
        }
    }
    return null;
}

async function load(path: string) {
    const res = await fetch(path);
    try {
        const definedValue = await res.json();
        if (definedValue !== null) {
            return definedValue.root ? definedValue.root : definedValue;
        }
    } catch (e) {
        if(res.ok){
            console.error("Failed to parse translations from "+path, e);
        }else{
            throw e;
        }
    }
    return null
}

function getSelectedLang() {
    const browserLang = ((navigator as any).browserLanguage || navigator.language);
    let storedLang;
    try {
        storedLang = localStorage.getItem("webswingLang");
    } catch (e) {
        console.error(e);
    }
    return storedLang || browserLang;
}