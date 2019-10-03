import Webswing from "./webswing"
import {loadTranslations} from "./webswing-tools";
import "../css/scss/style.scss";

loadTranslations().then(
    t => Webswing(t));