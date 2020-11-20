import { ModuleDef } from "./webswing-inject";
import { appFrameProtoOut } from "./proto/proto.out";
import { appFrameProtoIn } from "./proto/proto.in";
import { serverBrowserFrameProto } from "./proto/proto.frame";
import { commonProto } from "./proto/proto.common";
import { getToken } from './webswing-util';

export const socketInjectable = {
    cfg: 'webswing.config' as const,
    processMessage: 'base.processMessage' as const,
    showDialog: 'dialog.show' as const,
    showBar: 'dialog.showBar' as const,
    hideDialog: 'dialog.hide' as const,
    currentDialog: 'dialog.current' as const,
    dialogs: 'dialog.content' as const,
    reTrySession: 'webswing.reTrySession' as const
}

export interface ISocketService {
    'socket.connect': () => void,
    'socket.send': (message: AppFrameIn) => void,
    'socket.sendHandshake': (handshake: HandshakeMessage) => void,
    'socket.sendTimestamp': (timestamp: TimestampsMessage) => void,
    'socket.sendPlaybackCommand': (command: PlaybackCommandMessage) => void,
    'socket.sendSimpleEvent': (event: SimpleEventMessage) => void,
    'socket.instanceId': () => string | undefined,
    'socket.awaitResponse': (callback: (result: JsResultMsgOrError) => void, request: AppFrameIn, correlationId: string, timeout: number) => void,
    'socket.dispose': () => void
}

export type AppFrame = AppFrameOut

type SocketFrameMessage = serverBrowserFrameProto.IServerToBrowserFrameMsgOutProto
type AppFrameOut = appFrameProtoOut.IAppFrameMsgOutProto & {
    receivedTimestamp?: number
}
type HandshakeMessage = commonProto.IConnectionHandshakeMsgInProto
type TimestampsMessage = serverBrowserFrameProto.ITimestampsMsgInProto
type PlaybackCommandMessage = serverBrowserFrameProto.IPlaybackCommandMsgInProto
type SimpleEventMessage = commonProto.ISimpleEventMsgInProto
type AppFrameIn = appFrameProtoIn.IAppFrameMsgInProto
type JsResultMsgOrError = appFrameProtoOut.IJsResultMsgOutProto & Partial<Error>

export class SocketModule extends ModuleDef<typeof socketInjectable, ISocketService> {
    
    private maxRetries = 1;
    private retryNumber = 0;

    private socket?: WebSocket;
    private instanceId?: string;
    private responseHandlers: { [K: string]: (result: JsResultMsgOrError) => void } = {};

    public provides() {
        return {
            'socket.connect': this.connect,
            'socket.send': this.send,
            'socket.sendHandshake': this.sendHandshake,
            'socket.sendTimestamp': this.sendTimestamp,
            'socket.sendPlaybackCommand': this.sendPlaybackCommand,
            'socket.sendSimpleEvent': this.sendSimpleEvent,
            'socket.instanceId': this.getInstanceId,
            'socket.awaitResponse': this.awaitResponse,
            'socket.dispose': this.dispose
        }
    }

    public connect() {
    	this.connectWebSocket();
    }

    public dispose() {
        if (this.socket) {
            this.socket.close(1000, "Disconnecting instance.");
        }
        this.socket = undefined;
        this.instanceId = undefined;
    }

    public send(message: AppFrameIn) {
        const appFrameProto = serverBrowserFrameProto.BrowserToServerFrameMsgInProto.create();
        const proto = appFrameProtoIn.AppFrameMsgInProto.fromObject(message);
        const msg = appFrameProtoIn.AppFrameMsgInProto.encode(proto).finish();
        appFrameProto.appFrameMsgIn = msg;
        this.sendAppFrame(appFrameProto);
    }
    
    public sendHandshake(handshake: HandshakeMessage) {
        const appFrameProto = serverBrowserFrameProto.BrowserToServerFrameMsgInProto.create();
        appFrameProto.handshake = handshake;
        this.sendAppFrame(appFrameProto);
    }
    
    public sendTimestamp(timestamps: TimestampsMessage) {
        const appFrameProto = serverBrowserFrameProto.BrowserToServerFrameMsgInProto.create();
        appFrameProto.timestamps = [timestamps];
        this.sendAppFrame(appFrameProto);
    }
    
    public sendSimpleEvent(event: SimpleEventMessage) {
        const appFrameProto = serverBrowserFrameProto.BrowserToServerFrameMsgInProto.create();
        appFrameProto.events = [event];
        this.sendAppFrame(appFrameProto);
    }

    public sendPlaybackCommand(command: PlaybackCommandMessage) {
        const appFrameProto = serverBrowserFrameProto.BrowserToServerFrameMsgInProto.create();
        appFrameProto.playback = command;
        this.sendAppFrame(appFrameProto);
    }

    public awaitResponse(callback: (result: JsResultMsgOrError) => void, request: AppFrameIn, correlationId: string, timeout: number) {
        this.send(request);
        this.responseHandlers[correlationId] = callback;
        setTimeout(() => {
            if (this.responseHandlers[correlationId] != null) {
                delete this.responseHandlers[correlationId];
                callback(new Error("Java call timed out after " + timeout + " ms."));
            }
        }, timeout);
    }

    public getInstanceId() {
        return this.instanceId;
    }

    private sendAppFrame(appFrameProto: serverBrowserFrameProto.BrowserToServerFrameMsgInProto) {
        if (this.socket && this.socket != null) {
            if (this.socket.readyState === this.socket.OPEN) {
                const appFrame = serverBrowserFrameProto.BrowserToServerFrameMsgInProto.encode(appFrameProto).finish();
                this.socket.send(typedArrayToBuffer(appFrame));
            } else {
                if (this.api.currentDialog() === this.api.dialogs.sessionStolenNotification
                    || this.api.currentDialog() === this.api.dialogs.disconnectedDialog) {
                    return;
                } else {
                    this.api.showDialog(this.api.dialogs.disconnectedDialog);
                }
            }
        }
    }

    private connectWebSocket() {
        const wsBaseUrl = this.api.cfg.connectionUrl.replace(/(http)(s)?\:\/\//, "ws$2://");
        let url = wsBaseUrl + 'async/swing-bin';
        if (this.api.cfg.recordingPlayback) {
            url = wsBaseUrl + 'async/swing-play';
        }
        
        url += "?X-webswing-token=" + encodeURIComponent(getToken()!);
        
        if (this.api.cfg.recordingPlayback) {
            url += "&file=" + encodeURIComponent(this.api.cfg.recordingPlayback);
        }
        if (this.api.cfg.args != null) {
        	url += "&X-webswing-args=" + encodeURIComponent(this.api.cfg.args);
        }
        if (this.api.cfg.recording != null) {
        	url += "&X-webswing-recording=" + encodeURIComponent(this.api.cfg.recording);
        }
        if (this.api.cfg.debugPort != null) {
        	url += "&X-webswing-debugPort=" + encodeURIComponent(this.api.cfg.debugPort);
        }

        this.socket = new WebSocket(url);
        this.socket.binaryType = "arraybuffer";

        this.socket.onopen = () => {
            // empty
        };
        
        this.socket.onmessage = (event) => {
            this.retryNumber = 0;
            const receivedTimestamp = new Date().getTime();
            try {
                const frame: SocketFrameMessage = this.decodeResponse(event.data);
                let data: AppFrame = {};

                if (frame.connectionInfo != null) {
                    // tslint:disable-next-line: no-console
                    console.log("Connected to server [" + frame.connectionInfo.serverId + "] and session pool [" + frame.connectionInfo.sessionPoolId + "].")
                }

                if (frame.appFrameMsgOut && frame.appFrameMsgOut != null) {
                    const appFrameProto = appFrameProtoOut.AppFrameMsgOutProto.decode(frame.appFrameMsgOut);
                    const appFrameData: AppFrameOut = appFrameProtoOut.AppFrameMsgOutProto.toObject(appFrameProto);
                    data = appFrameData;

                    if (data.instanceId != null) {
                        this.instanceId = data.instanceId;
                    }
                    // javascript2java response handling
                    if (data.javaResponse != null && data.javaResponse.correlationId != null) {
                        const correlationId = data.javaResponse.correlationId;
                        if (this.responseHandlers[correlationId] != null) {
                            const callback = this.responseHandlers[correlationId];
                            delete this.responseHandlers[correlationId];
                            callback(data.javaResponse);
                        }
                    }
                    data.receivedTimestamp = receivedTimestamp;
                }

                this.api.processMessage(data);
            } catch (e) {
                console.error(e.stack);
                return;
            }
        };

        this.socket.onclose = () => {
            if (this.api.currentDialog() === this.api.dialogs.stoppedDialog || this.api.currentDialog() === this.api.dialogs.timedoutDialog
                || this.api.currentDialog() === this.api.dialogs.sessionStolenNotification) {
                return;
            }

            const token = getToken();
            const isLoggedOut = !token || token == null || !token.length;

            if (this.retryNumber >= this.maxRetries || isLoggedOut) {
                this.api.hideDialog();
                this.api.showDialog(this.api.dialogs.disconnectedDialog);
                this.retryNumber = 0;
            } else {
                this.api.showDialog(this.api.dialogs.reconnectingDialog);
                this.api.reTrySession();
                this.retryNumber++;
            }
        };

        this.socket.onerror = () => {
            this.api.showDialog(this.api.dialogs.connectionErrorDialog);
        };
    }

    private decodeResponse(message: ArrayBuffer): SocketFrameMessage {
        if (message.byteLength === 1) {
            return {};// ignore atmosphere heartbeat
        }
        const uint8View = new Uint8Array(message);

        const proto = serverBrowserFrameProto.ServerToBrowserFrameMsgOutProto.decode(uint8View);
        const data: SocketFrameMessage = serverBrowserFrameProto.ServerToBrowserFrameMsgOutProto.toObject(proto);
        
        return data;
    }
}

function typedArrayToBuffer(array: Uint8Array) {
    return array.buffer.slice(array.byteOffset, array.byteLength + array.byteOffset)
}