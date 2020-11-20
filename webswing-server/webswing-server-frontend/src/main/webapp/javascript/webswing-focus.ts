import { ModuleDef } from "./webswing-inject";
import { detectIE } from "./webswing-util";
import { appFrameProtoOut } from "./proto/proto.out";

export const focusManagerInjectable = {
    cfg: 'webswing.config' as const,
    getFocusedWindow: 'base.getFocusedWindow' as const,
    touchInputFocusGained: 'touch.inputFocusGained' as const,
    updateFocusedHtmlCanvas: 'input.updateFocusedHtmlCanvas' as const
}

export interface IFocusManagerService {
    'focusManager.manageFocusEvent': (focusEvent: appFrameProtoOut.IFocusEventMsgOutProto) => void,
    'focusManager.focusInput': (input: HTMLInputElement) => void,
    'focusManager.focusDefault': () => void
}

const EventType = appFrameProtoOut.FocusEventMsgOutProto.FocusEventTypeProto
export class FocusManagerModule extends ModuleDef<typeof focusManagerInjectable, IFocusManagerService> {

    public provides() {
        return {
            'focusManager.manageFocusEvent': this.manageFocusEvent,
            'focusManager.focusInput': this.focusInput,
            'focusManager.focusDefault': this.focusDefault
        }
    }

    public manageFocusEvent(focusEvent: appFrameProtoOut.IFocusEventMsgOutProto) {
        this.manageFocusEventRetry(focusEvent, 0);
    }

    public focusInput(input: HTMLInputElement) {
        // legacy function from webswing-input
        // this should not be needed and focus should always be correctly handled by focusDefault()

        if (this.api.cfg.touchMode) {
            return;
        }

        if (detectIE() && detectIE() <= 11) {
            input.blur(); // fix issue when compositionend causes focus to be lost in IE
        }

        if (this.isFocusManaged()) {
            // ignore, do not focus the hidden input
            // focus is handled by accessibility
        } else {
            // scrollX , scrollY attributes on IE gives undefined, so changed to compatible pageXOffset,pageYOffset
            // let sx = window.pageXOffset, sy = window.pageYOffset;
            // TODO ??? -> In order to ensure that the browser will fire clipboard events, we always need to have something selected
            input.value = ' ';
            // set the style attributes as the focus/select cannot work well in IE
            // input.style.top = sy +'px';
            // input.style.left = sx +'px';
            focusWithPreventScroll(input, true);
            input.select();
            // window.scrollTo(sx,sy);
        }
    }

    public focusDefault() {
        const doc = this.api.getFocusedWindow().document;
        const focusedElement = $((doc as any).activeElement);

        if (focusedElement.length && focusedElement.is(".catch-focus") || focusedElement.parents(".catch-focus").length) {
            // focus managed by a dialog or other element which catches the focus
            return;
        }

        const ariaEl = ($(doc).find(".webswing-element-content .aria-element.focusable") as unknown) as JQuery<HTMLElement>;
        if (ariaEl.length) {
            // focus aria element
            ariaEl[0].focus({ preventScroll: true });
        } else {
            // focus input
            const input = (($(doc).find(".ws-input-hidden") as unknown) as JQuery<HTMLInputElement>)[0];

            this.api.updateFocusedHtmlCanvas(input);
            focusWithPreventScroll(input);
        }
    }

    public isFocusManaged() {
        // if focus is managed, we should not try to change the focus externally

        const doc = this.api.getFocusedWindow().document;
        const focusedElement = $((doc as any).activeElement);
        const aria = $(doc).find(".webswing-element-content .aria-element.focusable");

        return aria.length > 0 || (focusedElement.length && focusedElement.is(".catch-focus") || focusedElement.parents(".catch-focus").length);
    }

    private manageFocusEventRetry(focusEvent: appFrameProtoOut.IFocusEventMsgOutProto, retryCount: number) {
    	const doc = $(this.api.getFocusedWindow().document);
        const input = ((doc.find(".ws-input-hidden") as unknown) as JQuery<HTMLInputElement>)[0];

        if (input) {
        	this.doManageFocusEvent(focusEvent);
        	return;
        }
        
        // in some cases there is a race condition on input being available right after window is undocked
        if (retryCount < 4) {
        	setTimeout(() => {
        		this.manageFocusEventRetry(focusEvent, retryCount + 1);
        	}, 50);
        }
    }
    
    private doManageFocusEvent(focusEvent: appFrameProtoOut.IFocusEventMsgOutProto) {
    	const doc = $(this.api.getFocusedWindow().document);
        const input = ((doc.find(".ws-input-hidden") as unknown) as JQuery<HTMLInputElement>)[0];
        
        input.classList.remove('editable');
        input.classList.remove('focused-with-caret');

        if ((focusEvent.type === EventType.focusWithCarretGained || focusEvent.type === EventType.focusPasswordGained) && (focusEvent.x! + focusEvent.w! > 0) && (focusEvent.y! + focusEvent.h! > 0)) {
            if (focusEvent.type === EventType.focusPasswordGained) {
                input.type = 'password';
            } else {
                input.type = 'text';
            }

            updateInputPosition(input, focusEvent);
            input.classList.add('focused-with-caret');
            if (focusEvent.editable) {
                input.classList.add('editable');
            }
            this.api.touchInputFocusGained();
        } else {
            updateInputPosition(input);
            input.value = '';
        }

        this.focusDefault();
    }

}
function focusWithPreventScroll(focusElement: HTMLInputElement, selectInput?: boolean) {
    if (focusElement) {
        const temppos = focusElement.style.position;
        const templeft = focusElement.style.left;
        const temptop = focusElement.style.top;
        if (detectIE() && detectIE() <= 11) {// prevent scroll does not work in IE
            focusElement.style.position = 'fixed';
            focusElement.style.left = '0px';
            focusElement.style.top = '0px'
        }
        focusElement.focus({ preventScroll: true });
        if (selectInput) {
            focusElement.select();
        }
        if (detectIE() && detectIE() <= 11) {
            focusElement.style.position = temppos;
            focusElement.style.left = templeft;
            focusElement.style.top = temptop;
        }
    }
}

function updateInputPosition(el: HTMLElement, focusEvent?: appFrameProtoOut.IFocusEventMsgOutProto) {
    if (focusEvent == null) {
        el.style.top = '';
        el.style.left = '';
        el.style.height = '';
    } else {
        const maxX = focusEvent.x! + focusEvent.w!;
        const maxY = focusEvent.y! + focusEvent.h!;
        el.style.top = Math.min(Math.max((focusEvent.y! + focusEvent.caretY!), focusEvent.y!), maxY) + 'px';
        el.style.left = Math.min(Math.max((focusEvent.x! + focusEvent.caretX!), focusEvent.x!), maxX) + 'px';
        el.style.height = focusEvent.caretH + 'px';
    }
}