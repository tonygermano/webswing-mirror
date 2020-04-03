/*eslint-disable block-scoped-var, id-length, no-control-regex, no-magic-numbers, no-prototype-builtins, no-redeclare, no-shadow, no-var, sort-vars*/
"use strict";

var $protobuf = require("protobufjs/minimal");

// Common aliases
var $Reader = $protobuf.Reader, $Writer = $protobuf.Writer, $util = $protobuf.util;

// Exported root namespace
var $root = $protobuf.roots["default"] || ($protobuf.roots["default"] = {});

$root.org = (function() {

    /**
     * Namespace org.
     * @exports org
     * @namespace
     */
    var org = {};

    org.webswing = (function() {

        /**
         * Namespace webswing.
         * @memberof org
         * @namespace
         */
        var webswing = {};

        webswing.server = (function() {

            /**
             * Namespace server.
             * @memberof org.webswing
             * @namespace
             */
            var server = {};

            server.model = (function() {

                /**
                 * Namespace model.
                 * @memberof org.webswing.server
                 * @namespace
                 */
                var model = {};

                model.proto = (function() {

                    /**
                     * Namespace proto.
                     * @memberof org.webswing.server.model
                     * @namespace
                     */
                    var proto = {};

                    proto.AppFrameMsgOutProto = (function() {

                        /**
                         * Properties of an AppFrameMsgOutProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IAppFrameMsgOutProto
                         * @property {Array.<org.webswing.server.model.proto.IApplicationInfoMsgProto>|null} [applications] AppFrameMsgOutProto applications
                         * @property {org.webswing.server.model.proto.ILinkActionMsgProto|null} [linkAction] AppFrameMsgOutProto linkAction
                         * @property {org.webswing.server.model.proto.IWindowMoveActionMsgProto|null} [moveAction] AppFrameMsgOutProto moveAction
                         * @property {org.webswing.server.model.proto.ICursorChangeEventMsgProto|null} [cursorChange] AppFrameMsgOutProto cursorChange
                         * @property {org.webswing.server.model.proto.ICopyEventMsgProto|null} [copyEvent] AppFrameMsgOutProto copyEvent
                         * @property {org.webswing.server.model.proto.IPasteRequestMsgProto|null} [pasteRequest] AppFrameMsgOutProto pasteRequest
                         * @property {org.webswing.server.model.proto.IFileDialogEventMsgProto|null} [fileDialogEvent] AppFrameMsgOutProto fileDialogEvent
                         * @property {Array.<org.webswing.server.model.proto.IWindowMsgProto>|null} [windows] AppFrameMsgOutProto windows
                         * @property {org.webswing.server.model.proto.IWindowMsgProto|null} [closedWindow] AppFrameMsgOutProto closedWindow
                         * @property {org.webswing.server.model.proto.SimpleEventMsgOutProto|null} [event] AppFrameMsgOutProto event
                         * @property {org.webswing.server.model.proto.IJsEvalRequestMsgOutProto|null} [jsRequest] AppFrameMsgOutProto jsRequest
                         * @property {org.webswing.server.model.proto.IJsResultMsgProto|null} [javaResponse] AppFrameMsgOutProto javaResponse
                         * @property {org.webswing.server.model.proto.IPixelsAreaRequestMsgOutProto|null} [pixelsRequest] AppFrameMsgOutProto pixelsRequest
                         * @property {org.webswing.server.model.proto.IPlaybackInfoMsgProto|null} [playback] AppFrameMsgOutProto playback
                         * @property {string|null} [sessionId] AppFrameMsgOutProto sessionId
                         * @property {string|null} [startTimestamp] AppFrameMsgOutProto startTimestamp
                         * @property {string|null} [sendTimestamp] AppFrameMsgOutProto sendTimestamp
                         * @property {org.webswing.server.model.proto.IFocusEventMsgProto|null} [focusEvent] AppFrameMsgOutProto focusEvent
                         * @property {Array.<org.webswing.server.model.proto.IComponentTreeMsgProto>|null} [componentTree] AppFrameMsgOutProto componentTree
                         * @property {boolean|null} [directDraw] AppFrameMsgOutProto directDraw
                         * @property {org.webswing.server.model.proto.IActionEventMsgOutProto|null} [actionEvent] AppFrameMsgOutProto actionEvent
                         * @property {boolean|null} [compositingWM] AppFrameMsgOutProto compositingWM
                         * @property {org.webswing.server.model.proto.IAudioEventMsgOutProto|null} [audioEvent] AppFrameMsgOutProto audioEvent
                         * @property {org.webswing.server.model.proto.IWindowDockMsgProto|null} [dockAction] AppFrameMsgOutProto dockAction
                         * @property {org.webswing.server.model.proto.IAccessibilityMsgProto|null} [accessible] AppFrameMsgOutProto accessible
                         * @property {Array.<org.webswing.server.model.proto.IWindowSwitchMsgProto>|null} [windowSwitchList] AppFrameMsgOutProto windowSwitchList
                         */

                        /**
                         * Constructs a new AppFrameMsgOutProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents an AppFrameMsgOutProto.
                         * @implements IAppFrameMsgOutProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IAppFrameMsgOutProto=} [properties] Properties to set
                         */
                        function AppFrameMsgOutProto(properties) {
                            this.applications = [];
                            this.windows = [];
                            this.componentTree = [];
                            this.windowSwitchList = [];
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * AppFrameMsgOutProto applications.
                         * @member {Array.<org.webswing.server.model.proto.IApplicationInfoMsgProto>} applications
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.applications = $util.emptyArray;

                        /**
                         * AppFrameMsgOutProto linkAction.
                         * @member {org.webswing.server.model.proto.ILinkActionMsgProto|null|undefined} linkAction
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.linkAction = null;

                        /**
                         * AppFrameMsgOutProto moveAction.
                         * @member {org.webswing.server.model.proto.IWindowMoveActionMsgProto|null|undefined} moveAction
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.moveAction = null;

                        /**
                         * AppFrameMsgOutProto cursorChange.
                         * @member {org.webswing.server.model.proto.ICursorChangeEventMsgProto|null|undefined} cursorChange
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.cursorChange = null;

                        /**
                         * AppFrameMsgOutProto copyEvent.
                         * @member {org.webswing.server.model.proto.ICopyEventMsgProto|null|undefined} copyEvent
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.copyEvent = null;

                        /**
                         * AppFrameMsgOutProto pasteRequest.
                         * @member {org.webswing.server.model.proto.IPasteRequestMsgProto|null|undefined} pasteRequest
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.pasteRequest = null;

                        /**
                         * AppFrameMsgOutProto fileDialogEvent.
                         * @member {org.webswing.server.model.proto.IFileDialogEventMsgProto|null|undefined} fileDialogEvent
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.fileDialogEvent = null;

                        /**
                         * AppFrameMsgOutProto windows.
                         * @member {Array.<org.webswing.server.model.proto.IWindowMsgProto>} windows
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.windows = $util.emptyArray;

                        /**
                         * AppFrameMsgOutProto closedWindow.
                         * @member {org.webswing.server.model.proto.IWindowMsgProto|null|undefined} closedWindow
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.closedWindow = null;

                        /**
                         * AppFrameMsgOutProto event.
                         * @member {org.webswing.server.model.proto.SimpleEventMsgOutProto} event
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.event = 0;

                        /**
                         * AppFrameMsgOutProto jsRequest.
                         * @member {org.webswing.server.model.proto.IJsEvalRequestMsgOutProto|null|undefined} jsRequest
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.jsRequest = null;

                        /**
                         * AppFrameMsgOutProto javaResponse.
                         * @member {org.webswing.server.model.proto.IJsResultMsgProto|null|undefined} javaResponse
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.javaResponse = null;

                        /**
                         * AppFrameMsgOutProto pixelsRequest.
                         * @member {org.webswing.server.model.proto.IPixelsAreaRequestMsgOutProto|null|undefined} pixelsRequest
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.pixelsRequest = null;

                        /**
                         * AppFrameMsgOutProto playback.
                         * @member {org.webswing.server.model.proto.IPlaybackInfoMsgProto|null|undefined} playback
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.playback = null;

                        /**
                         * AppFrameMsgOutProto sessionId.
                         * @member {string} sessionId
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.sessionId = "";

                        /**
                         * AppFrameMsgOutProto startTimestamp.
                         * @member {string} startTimestamp
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.startTimestamp = "";

                        /**
                         * AppFrameMsgOutProto sendTimestamp.
                         * @member {string} sendTimestamp
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.sendTimestamp = "";

                        /**
                         * AppFrameMsgOutProto focusEvent.
                         * @member {org.webswing.server.model.proto.IFocusEventMsgProto|null|undefined} focusEvent
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.focusEvent = null;

                        /**
                         * AppFrameMsgOutProto componentTree.
                         * @member {Array.<org.webswing.server.model.proto.IComponentTreeMsgProto>} componentTree
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.componentTree = $util.emptyArray;

                        /**
                         * AppFrameMsgOutProto directDraw.
                         * @member {boolean} directDraw
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.directDraw = false;

                        /**
                         * AppFrameMsgOutProto actionEvent.
                         * @member {org.webswing.server.model.proto.IActionEventMsgOutProto|null|undefined} actionEvent
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.actionEvent = null;

                        /**
                         * AppFrameMsgOutProto compositingWM.
                         * @member {boolean} compositingWM
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.compositingWM = false;

                        /**
                         * AppFrameMsgOutProto audioEvent.
                         * @member {org.webswing.server.model.proto.IAudioEventMsgOutProto|null|undefined} audioEvent
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.audioEvent = null;

                        /**
                         * AppFrameMsgOutProto dockAction.
                         * @member {org.webswing.server.model.proto.IWindowDockMsgProto|null|undefined} dockAction
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.dockAction = null;

                        /**
                         * AppFrameMsgOutProto accessible.
                         * @member {org.webswing.server.model.proto.IAccessibilityMsgProto|null|undefined} accessible
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.accessible = null;

                        /**
                         * AppFrameMsgOutProto windowSwitchList.
                         * @member {Array.<org.webswing.server.model.proto.IWindowSwitchMsgProto>} windowSwitchList
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         */
                        AppFrameMsgOutProto.prototype.windowSwitchList = $util.emptyArray;

                        /**
                         * Creates a new AppFrameMsgOutProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.IAppFrameMsgOutProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.AppFrameMsgOutProto} AppFrameMsgOutProto instance
                         */
                        AppFrameMsgOutProto.create = function create(properties) {
                            return new AppFrameMsgOutProto(properties);
                        };

                        /**
                         * Encodes the specified AppFrameMsgOutProto message. Does not implicitly {@link org.webswing.server.model.proto.AppFrameMsgOutProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.IAppFrameMsgOutProto} message AppFrameMsgOutProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        AppFrameMsgOutProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.applications != null && message.applications.length)
                                for (var i = 0; i < message.applications.length; ++i)
                                    $root.org.webswing.server.model.proto.ApplicationInfoMsgProto.encode(message.applications[i], writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
                            if (message.linkAction != null && message.hasOwnProperty("linkAction"))
                                $root.org.webswing.server.model.proto.LinkActionMsgProto.encode(message.linkAction, writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
                            if (message.moveAction != null && message.hasOwnProperty("moveAction"))
                                $root.org.webswing.server.model.proto.WindowMoveActionMsgProto.encode(message.moveAction, writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
                            if (message.cursorChange != null && message.hasOwnProperty("cursorChange"))
                                $root.org.webswing.server.model.proto.CursorChangeEventMsgProto.encode(message.cursorChange, writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
                            if (message.copyEvent != null && message.hasOwnProperty("copyEvent"))
                                $root.org.webswing.server.model.proto.CopyEventMsgProto.encode(message.copyEvent, writer.uint32(/* id 5, wireType 2 =*/42).fork()).ldelim();
                            if (message.pasteRequest != null && message.hasOwnProperty("pasteRequest"))
                                $root.org.webswing.server.model.proto.PasteRequestMsgProto.encode(message.pasteRequest, writer.uint32(/* id 6, wireType 2 =*/50).fork()).ldelim();
                            if (message.fileDialogEvent != null && message.hasOwnProperty("fileDialogEvent"))
                                $root.org.webswing.server.model.proto.FileDialogEventMsgProto.encode(message.fileDialogEvent, writer.uint32(/* id 7, wireType 2 =*/58).fork()).ldelim();
                            if (message.windows != null && message.windows.length)
                                for (var i = 0; i < message.windows.length; ++i)
                                    $root.org.webswing.server.model.proto.WindowMsgProto.encode(message.windows[i], writer.uint32(/* id 8, wireType 2 =*/66).fork()).ldelim();
                            if (message.closedWindow != null && message.hasOwnProperty("closedWindow"))
                                $root.org.webswing.server.model.proto.WindowMsgProto.encode(message.closedWindow, writer.uint32(/* id 9, wireType 2 =*/74).fork()).ldelim();
                            if (message.event != null && message.hasOwnProperty("event"))
                                writer.uint32(/* id 10, wireType 0 =*/80).int32(message.event);
                            if (message.jsRequest != null && message.hasOwnProperty("jsRequest"))
                                $root.org.webswing.server.model.proto.JsEvalRequestMsgOutProto.encode(message.jsRequest, writer.uint32(/* id 11, wireType 2 =*/90).fork()).ldelim();
                            if (message.javaResponse != null && message.hasOwnProperty("javaResponse"))
                                $root.org.webswing.server.model.proto.JsResultMsgProto.encode(message.javaResponse, writer.uint32(/* id 12, wireType 2 =*/98).fork()).ldelim();
                            if (message.pixelsRequest != null && message.hasOwnProperty("pixelsRequest"))
                                $root.org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto.encode(message.pixelsRequest, writer.uint32(/* id 13, wireType 2 =*/106).fork()).ldelim();
                            if (message.playback != null && message.hasOwnProperty("playback"))
                                $root.org.webswing.server.model.proto.PlaybackInfoMsgProto.encode(message.playback, writer.uint32(/* id 14, wireType 2 =*/114).fork()).ldelim();
                            if (message.sessionId != null && message.hasOwnProperty("sessionId"))
                                writer.uint32(/* id 15, wireType 2 =*/122).string(message.sessionId);
                            if (message.startTimestamp != null && message.hasOwnProperty("startTimestamp"))
                                writer.uint32(/* id 16, wireType 2 =*/130).string(message.startTimestamp);
                            if (message.sendTimestamp != null && message.hasOwnProperty("sendTimestamp"))
                                writer.uint32(/* id 17, wireType 2 =*/138).string(message.sendTimestamp);
                            if (message.focusEvent != null && message.hasOwnProperty("focusEvent"))
                                $root.org.webswing.server.model.proto.FocusEventMsgProto.encode(message.focusEvent, writer.uint32(/* id 18, wireType 2 =*/146).fork()).ldelim();
                            if (message.componentTree != null && message.componentTree.length)
                                for (var i = 0; i < message.componentTree.length; ++i)
                                    $root.org.webswing.server.model.proto.ComponentTreeMsgProto.encode(message.componentTree[i], writer.uint32(/* id 19, wireType 2 =*/154).fork()).ldelim();
                            if (message.directDraw != null && message.hasOwnProperty("directDraw"))
                                writer.uint32(/* id 20, wireType 0 =*/160).bool(message.directDraw);
                            if (message.actionEvent != null && message.hasOwnProperty("actionEvent"))
                                $root.org.webswing.server.model.proto.ActionEventMsgOutProto.encode(message.actionEvent, writer.uint32(/* id 21, wireType 2 =*/170).fork()).ldelim();
                            if (message.compositingWM != null && message.hasOwnProperty("compositingWM"))
                                writer.uint32(/* id 22, wireType 0 =*/176).bool(message.compositingWM);
                            if (message.audioEvent != null && message.hasOwnProperty("audioEvent"))
                                $root.org.webswing.server.model.proto.AudioEventMsgOutProto.encode(message.audioEvent, writer.uint32(/* id 23, wireType 2 =*/186).fork()).ldelim();
                            if (message.dockAction != null && message.hasOwnProperty("dockAction"))
                                $root.org.webswing.server.model.proto.WindowDockMsgProto.encode(message.dockAction, writer.uint32(/* id 24, wireType 2 =*/194).fork()).ldelim();
                            if (message.accessible != null && message.hasOwnProperty("accessible"))
                                $root.org.webswing.server.model.proto.AccessibilityMsgProto.encode(message.accessible, writer.uint32(/* id 25, wireType 2 =*/202).fork()).ldelim();
                            if (message.windowSwitchList != null && message.windowSwitchList.length)
                                for (var i = 0; i < message.windowSwitchList.length; ++i)
                                    $root.org.webswing.server.model.proto.WindowSwitchMsgProto.encode(message.windowSwitchList[i], writer.uint32(/* id 26, wireType 2 =*/210).fork()).ldelim();
                            return writer;
                        };

                        /**
                         * Decodes an AppFrameMsgOutProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.AppFrameMsgOutProto} AppFrameMsgOutProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        AppFrameMsgOutProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.AppFrameMsgOutProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    if (!(message.applications && message.applications.length))
                                        message.applications = [];
                                    message.applications.push($root.org.webswing.server.model.proto.ApplicationInfoMsgProto.decode(reader, reader.uint32()));
                                    break;
                                case 2:
                                    message.linkAction = $root.org.webswing.server.model.proto.LinkActionMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 3:
                                    message.moveAction = $root.org.webswing.server.model.proto.WindowMoveActionMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 4:
                                    message.cursorChange = $root.org.webswing.server.model.proto.CursorChangeEventMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 5:
                                    message.copyEvent = $root.org.webswing.server.model.proto.CopyEventMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 6:
                                    message.pasteRequest = $root.org.webswing.server.model.proto.PasteRequestMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 7:
                                    message.fileDialogEvent = $root.org.webswing.server.model.proto.FileDialogEventMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 8:
                                    if (!(message.windows && message.windows.length))
                                        message.windows = [];
                                    message.windows.push($root.org.webswing.server.model.proto.WindowMsgProto.decode(reader, reader.uint32()));
                                    break;
                                case 9:
                                    message.closedWindow = $root.org.webswing.server.model.proto.WindowMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 10:
                                    message.event = reader.int32();
                                    break;
                                case 11:
                                    message.jsRequest = $root.org.webswing.server.model.proto.JsEvalRequestMsgOutProto.decode(reader, reader.uint32());
                                    break;
                                case 12:
                                    message.javaResponse = $root.org.webswing.server.model.proto.JsResultMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 13:
                                    message.pixelsRequest = $root.org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto.decode(reader, reader.uint32());
                                    break;
                                case 14:
                                    message.playback = $root.org.webswing.server.model.proto.PlaybackInfoMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 15:
                                    message.sessionId = reader.string();
                                    break;
                                case 16:
                                    message.startTimestamp = reader.string();
                                    break;
                                case 17:
                                    message.sendTimestamp = reader.string();
                                    break;
                                case 18:
                                    message.focusEvent = $root.org.webswing.server.model.proto.FocusEventMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 19:
                                    if (!(message.componentTree && message.componentTree.length))
                                        message.componentTree = [];
                                    message.componentTree.push($root.org.webswing.server.model.proto.ComponentTreeMsgProto.decode(reader, reader.uint32()));
                                    break;
                                case 20:
                                    message.directDraw = reader.bool();
                                    break;
                                case 21:
                                    message.actionEvent = $root.org.webswing.server.model.proto.ActionEventMsgOutProto.decode(reader, reader.uint32());
                                    break;
                                case 22:
                                    message.compositingWM = reader.bool();
                                    break;
                                case 23:
                                    message.audioEvent = $root.org.webswing.server.model.proto.AudioEventMsgOutProto.decode(reader, reader.uint32());
                                    break;
                                case 24:
                                    message.dockAction = $root.org.webswing.server.model.proto.WindowDockMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 25:
                                    message.accessible = $root.org.webswing.server.model.proto.AccessibilityMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 26:
                                    if (!(message.windowSwitchList && message.windowSwitchList.length))
                                        message.windowSwitchList = [];
                                    message.windowSwitchList.push($root.org.webswing.server.model.proto.WindowSwitchMsgProto.decode(reader, reader.uint32()));
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates an AppFrameMsgOutProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.AppFrameMsgOutProto} AppFrameMsgOutProto
                         */
                        AppFrameMsgOutProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.AppFrameMsgOutProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.AppFrameMsgOutProto();
                            if (object.applications) {
                                if (!Array.isArray(object.applications))
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.applications: array expected");
                                message.applications = [];
                                for (var i = 0; i < object.applications.length; ++i) {
                                    if (typeof object.applications[i] !== "object")
                                        throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.applications: object expected");
                                    message.applications[i] = $root.org.webswing.server.model.proto.ApplicationInfoMsgProto.fromObject(object.applications[i]);
                                }
                            }
                            if (object.linkAction != null) {
                                if (typeof object.linkAction !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.linkAction: object expected");
                                message.linkAction = $root.org.webswing.server.model.proto.LinkActionMsgProto.fromObject(object.linkAction);
                            }
                            if (object.moveAction != null) {
                                if (typeof object.moveAction !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.moveAction: object expected");
                                message.moveAction = $root.org.webswing.server.model.proto.WindowMoveActionMsgProto.fromObject(object.moveAction);
                            }
                            if (object.cursorChange != null) {
                                if (typeof object.cursorChange !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.cursorChange: object expected");
                                message.cursorChange = $root.org.webswing.server.model.proto.CursorChangeEventMsgProto.fromObject(object.cursorChange);
                            }
                            if (object.copyEvent != null) {
                                if (typeof object.copyEvent !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.copyEvent: object expected");
                                message.copyEvent = $root.org.webswing.server.model.proto.CopyEventMsgProto.fromObject(object.copyEvent);
                            }
                            if (object.pasteRequest != null) {
                                if (typeof object.pasteRequest !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.pasteRequest: object expected");
                                message.pasteRequest = $root.org.webswing.server.model.proto.PasteRequestMsgProto.fromObject(object.pasteRequest);
                            }
                            if (object.fileDialogEvent != null) {
                                if (typeof object.fileDialogEvent !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.fileDialogEvent: object expected");
                                message.fileDialogEvent = $root.org.webswing.server.model.proto.FileDialogEventMsgProto.fromObject(object.fileDialogEvent);
                            }
                            if (object.windows) {
                                if (!Array.isArray(object.windows))
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.windows: array expected");
                                message.windows = [];
                                for (var i = 0; i < object.windows.length; ++i) {
                                    if (typeof object.windows[i] !== "object")
                                        throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.windows: object expected");
                                    message.windows[i] = $root.org.webswing.server.model.proto.WindowMsgProto.fromObject(object.windows[i]);
                                }
                            }
                            if (object.closedWindow != null) {
                                if (typeof object.closedWindow !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.closedWindow: object expected");
                                message.closedWindow = $root.org.webswing.server.model.proto.WindowMsgProto.fromObject(object.closedWindow);
                            }
                            switch (object.event) {
                            case "applicationAlreadyRunning":
                            case 0:
                                message.event = 0;
                                break;
                            case "shutDownNotification":
                            case 1:
                                message.event = 1;
                                break;
                            case "tooManyClientsNotification":
                            case 2:
                                message.event = 2;
                                break;
                            case "continueOldSession":
                            case 3:
                                message.event = 3;
                                break;
                            case "configurationError":
                            case 4:
                                message.event = 4;
                                break;
                            case "sessionStolenNotification":
                            case 5:
                                message.event = 5;
                                break;
                            case "unauthorizedAccess":
                            case 6:
                                message.event = 6;
                                break;
                            case "shutDownAutoLogoutNotification":
                            case 7:
                                message.event = 7;
                                break;
                            case "sessionTimeoutWarning":
                            case 8:
                                message.event = 8;
                                break;
                            case "sessionTimedOutNotification":
                            case 9:
                                message.event = 9;
                                break;
                            case "applicationBusy":
                            case 10:
                                message.event = 10;
                                break;
                            }
                            if (object.jsRequest != null) {
                                if (typeof object.jsRequest !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.jsRequest: object expected");
                                message.jsRequest = $root.org.webswing.server.model.proto.JsEvalRequestMsgOutProto.fromObject(object.jsRequest);
                            }
                            if (object.javaResponse != null) {
                                if (typeof object.javaResponse !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.javaResponse: object expected");
                                message.javaResponse = $root.org.webswing.server.model.proto.JsResultMsgProto.fromObject(object.javaResponse);
                            }
                            if (object.pixelsRequest != null) {
                                if (typeof object.pixelsRequest !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.pixelsRequest: object expected");
                                message.pixelsRequest = $root.org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto.fromObject(object.pixelsRequest);
                            }
                            if (object.playback != null) {
                                if (typeof object.playback !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.playback: object expected");
                                message.playback = $root.org.webswing.server.model.proto.PlaybackInfoMsgProto.fromObject(object.playback);
                            }
                            if (object.sessionId != null)
                                message.sessionId = String(object.sessionId);
                            if (object.startTimestamp != null)
                                message.startTimestamp = String(object.startTimestamp);
                            if (object.sendTimestamp != null)
                                message.sendTimestamp = String(object.sendTimestamp);
                            if (object.focusEvent != null) {
                                if (typeof object.focusEvent !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.focusEvent: object expected");
                                message.focusEvent = $root.org.webswing.server.model.proto.FocusEventMsgProto.fromObject(object.focusEvent);
                            }
                            if (object.componentTree) {
                                if (!Array.isArray(object.componentTree))
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.componentTree: array expected");
                                message.componentTree = [];
                                for (var i = 0; i < object.componentTree.length; ++i) {
                                    if (typeof object.componentTree[i] !== "object")
                                        throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.componentTree: object expected");
                                    message.componentTree[i] = $root.org.webswing.server.model.proto.ComponentTreeMsgProto.fromObject(object.componentTree[i]);
                                }
                            }
                            if (object.directDraw != null)
                                message.directDraw = Boolean(object.directDraw);
                            if (object.actionEvent != null) {
                                if (typeof object.actionEvent !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.actionEvent: object expected");
                                message.actionEvent = $root.org.webswing.server.model.proto.ActionEventMsgOutProto.fromObject(object.actionEvent);
                            }
                            if (object.compositingWM != null)
                                message.compositingWM = Boolean(object.compositingWM);
                            if (object.audioEvent != null) {
                                if (typeof object.audioEvent !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.audioEvent: object expected");
                                message.audioEvent = $root.org.webswing.server.model.proto.AudioEventMsgOutProto.fromObject(object.audioEvent);
                            }
                            if (object.dockAction != null) {
                                if (typeof object.dockAction !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.dockAction: object expected");
                                message.dockAction = $root.org.webswing.server.model.proto.WindowDockMsgProto.fromObject(object.dockAction);
                            }
                            if (object.accessible != null) {
                                if (typeof object.accessible !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.accessible: object expected");
                                message.accessible = $root.org.webswing.server.model.proto.AccessibilityMsgProto.fromObject(object.accessible);
                            }
                            if (object.windowSwitchList) {
                                if (!Array.isArray(object.windowSwitchList))
                                    throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.windowSwitchList: array expected");
                                message.windowSwitchList = [];
                                for (var i = 0; i < object.windowSwitchList.length; ++i) {
                                    if (typeof object.windowSwitchList[i] !== "object")
                                        throw TypeError(".org.webswing.server.model.proto.AppFrameMsgOutProto.windowSwitchList: object expected");
                                    message.windowSwitchList[i] = $root.org.webswing.server.model.proto.WindowSwitchMsgProto.fromObject(object.windowSwitchList[i]);
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from an AppFrameMsgOutProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.AppFrameMsgOutProto} message AppFrameMsgOutProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        AppFrameMsgOutProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.arrays || options.defaults) {
                                object.applications = [];
                                object.windows = [];
                                object.componentTree = [];
                                object.windowSwitchList = [];
                            }
                            if (options.defaults) {
                                object.linkAction = null;
                                object.moveAction = null;
                                object.cursorChange = null;
                                object.copyEvent = null;
                                object.pasteRequest = null;
                                object.fileDialogEvent = null;
                                object.closedWindow = null;
                                object.event = options.enums === String ? "applicationAlreadyRunning" : 0;
                                object.jsRequest = null;
                                object.javaResponse = null;
                                object.pixelsRequest = null;
                                object.playback = null;
                                object.sessionId = "";
                                object.startTimestamp = "";
                                object.sendTimestamp = "";
                                object.focusEvent = null;
                                object.directDraw = false;
                                object.actionEvent = null;
                                object.compositingWM = false;
                                object.audioEvent = null;
                                object.dockAction = null;
                                object.accessible = null;
                            }
                            if (message.applications && message.applications.length) {
                                object.applications = [];
                                for (var j = 0; j < message.applications.length; ++j)
                                    object.applications[j] = $root.org.webswing.server.model.proto.ApplicationInfoMsgProto.toObject(message.applications[j], options);
                            }
                            if (message.linkAction != null && message.hasOwnProperty("linkAction"))
                                object.linkAction = $root.org.webswing.server.model.proto.LinkActionMsgProto.toObject(message.linkAction, options);
                            if (message.moveAction != null && message.hasOwnProperty("moveAction"))
                                object.moveAction = $root.org.webswing.server.model.proto.WindowMoveActionMsgProto.toObject(message.moveAction, options);
                            if (message.cursorChange != null && message.hasOwnProperty("cursorChange"))
                                object.cursorChange = $root.org.webswing.server.model.proto.CursorChangeEventMsgProto.toObject(message.cursorChange, options);
                            if (message.copyEvent != null && message.hasOwnProperty("copyEvent"))
                                object.copyEvent = $root.org.webswing.server.model.proto.CopyEventMsgProto.toObject(message.copyEvent, options);
                            if (message.pasteRequest != null && message.hasOwnProperty("pasteRequest"))
                                object.pasteRequest = $root.org.webswing.server.model.proto.PasteRequestMsgProto.toObject(message.pasteRequest, options);
                            if (message.fileDialogEvent != null && message.hasOwnProperty("fileDialogEvent"))
                                object.fileDialogEvent = $root.org.webswing.server.model.proto.FileDialogEventMsgProto.toObject(message.fileDialogEvent, options);
                            if (message.windows && message.windows.length) {
                                object.windows = [];
                                for (var j = 0; j < message.windows.length; ++j)
                                    object.windows[j] = $root.org.webswing.server.model.proto.WindowMsgProto.toObject(message.windows[j], options);
                            }
                            if (message.closedWindow != null && message.hasOwnProperty("closedWindow"))
                                object.closedWindow = $root.org.webswing.server.model.proto.WindowMsgProto.toObject(message.closedWindow, options);
                            if (message.event != null && message.hasOwnProperty("event"))
                                object.event = options.enums === String ? $root.org.webswing.server.model.proto.SimpleEventMsgOutProto[message.event] : message.event;
                            if (message.jsRequest != null && message.hasOwnProperty("jsRequest"))
                                object.jsRequest = $root.org.webswing.server.model.proto.JsEvalRequestMsgOutProto.toObject(message.jsRequest, options);
                            if (message.javaResponse != null && message.hasOwnProperty("javaResponse"))
                                object.javaResponse = $root.org.webswing.server.model.proto.JsResultMsgProto.toObject(message.javaResponse, options);
                            if (message.pixelsRequest != null && message.hasOwnProperty("pixelsRequest"))
                                object.pixelsRequest = $root.org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto.toObject(message.pixelsRequest, options);
                            if (message.playback != null && message.hasOwnProperty("playback"))
                                object.playback = $root.org.webswing.server.model.proto.PlaybackInfoMsgProto.toObject(message.playback, options);
                            if (message.sessionId != null && message.hasOwnProperty("sessionId"))
                                object.sessionId = message.sessionId;
                            if (message.startTimestamp != null && message.hasOwnProperty("startTimestamp"))
                                object.startTimestamp = message.startTimestamp;
                            if (message.sendTimestamp != null && message.hasOwnProperty("sendTimestamp"))
                                object.sendTimestamp = message.sendTimestamp;
                            if (message.focusEvent != null && message.hasOwnProperty("focusEvent"))
                                object.focusEvent = $root.org.webswing.server.model.proto.FocusEventMsgProto.toObject(message.focusEvent, options);
                            if (message.componentTree && message.componentTree.length) {
                                object.componentTree = [];
                                for (var j = 0; j < message.componentTree.length; ++j)
                                    object.componentTree[j] = $root.org.webswing.server.model.proto.ComponentTreeMsgProto.toObject(message.componentTree[j], options);
                            }
                            if (message.directDraw != null && message.hasOwnProperty("directDraw"))
                                object.directDraw = message.directDraw;
                            if (message.actionEvent != null && message.hasOwnProperty("actionEvent"))
                                object.actionEvent = $root.org.webswing.server.model.proto.ActionEventMsgOutProto.toObject(message.actionEvent, options);
                            if (message.compositingWM != null && message.hasOwnProperty("compositingWM"))
                                object.compositingWM = message.compositingWM;
                            if (message.audioEvent != null && message.hasOwnProperty("audioEvent"))
                                object.audioEvent = $root.org.webswing.server.model.proto.AudioEventMsgOutProto.toObject(message.audioEvent, options);
                            if (message.dockAction != null && message.hasOwnProperty("dockAction"))
                                object.dockAction = $root.org.webswing.server.model.proto.WindowDockMsgProto.toObject(message.dockAction, options);
                            if (message.accessible != null && message.hasOwnProperty("accessible"))
                                object.accessible = $root.org.webswing.server.model.proto.AccessibilityMsgProto.toObject(message.accessible, options);
                            if (message.windowSwitchList && message.windowSwitchList.length) {
                                object.windowSwitchList = [];
                                for (var j = 0; j < message.windowSwitchList.length; ++j)
                                    object.windowSwitchList[j] = $root.org.webswing.server.model.proto.WindowSwitchMsgProto.toObject(message.windowSwitchList[j], options);
                            }
                            return object;
                        };

                        /**
                         * Converts this AppFrameMsgOutProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.AppFrameMsgOutProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        AppFrameMsgOutProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return AppFrameMsgOutProto;
                    })();

                    proto.AccessibilityMsgProto = (function() {

                        /**
                         * Properties of an AccessibilityMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IAccessibilityMsgProto
                         * @property {string|null} [id] AccessibilityMsgProto id
                         * @property {string|null} [role] AccessibilityMsgProto role
                         * @property {string|null} [text] AccessibilityMsgProto text
                         * @property {string|null} [tooltip] AccessibilityMsgProto tooltip
                         * @property {string|null} [value] AccessibilityMsgProto value
                         * @property {string|null} [description] AccessibilityMsgProto description
                         * @property {string|null} [columnheader] AccessibilityMsgProto columnheader
                         * @property {boolean|null} [password] AccessibilityMsgProto password
                         * @property {boolean|null} [toggle] AccessibilityMsgProto toggle
                         * @property {number|null} [selstart] AccessibilityMsgProto selstart
                         * @property {number|null} [selend] AccessibilityMsgProto selend
                         * @property {number|null} [rowheight] AccessibilityMsgProto rowheight
                         * @property {number|null} [rows] AccessibilityMsgProto rows
                         * @property {number|null} [size] AccessibilityMsgProto size
                         * @property {number|null} [position] AccessibilityMsgProto position
                         * @property {number|null} [level] AccessibilityMsgProto level
                         * @property {number|null} [colindex] AccessibilityMsgProto colindex
                         * @property {number|null} [rowindex] AccessibilityMsgProto rowindex
                         * @property {number|null} [colcount] AccessibilityMsgProto colcount
                         * @property {number|null} [rowcount] AccessibilityMsgProto rowcount
                         * @property {Array.<string>|null} [states] AccessibilityMsgProto states
                         * @property {number|null} [min] AccessibilityMsgProto min
                         * @property {number|null} [max] AccessibilityMsgProto max
                         * @property {number|null} [val] AccessibilityMsgProto val
                         * @property {number|null} [screenX] AccessibilityMsgProto screenX
                         * @property {number|null} [screenY] AccessibilityMsgProto screenY
                         * @property {number|null} [width] AccessibilityMsgProto width
                         * @property {number|null} [height] AccessibilityMsgProto height
                         * @property {Array.<org.webswing.server.model.proto.IAccessibilityHierarchyMsgProto>|null} [hierarchy] AccessibilityMsgProto hierarchy
                         */

                        /**
                         * Constructs a new AccessibilityMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents an AccessibilityMsgProto.
                         * @implements IAccessibilityMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IAccessibilityMsgProto=} [properties] Properties to set
                         */
                        function AccessibilityMsgProto(properties) {
                            this.states = [];
                            this.hierarchy = [];
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * AccessibilityMsgProto id.
                         * @member {string} id
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.id = "";

                        /**
                         * AccessibilityMsgProto role.
                         * @member {string} role
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.role = "";

                        /**
                         * AccessibilityMsgProto text.
                         * @member {string} text
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.text = "";

                        /**
                         * AccessibilityMsgProto tooltip.
                         * @member {string} tooltip
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.tooltip = "";

                        /**
                         * AccessibilityMsgProto value.
                         * @member {string} value
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.value = "";

                        /**
                         * AccessibilityMsgProto description.
                         * @member {string} description
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.description = "";

                        /**
                         * AccessibilityMsgProto columnheader.
                         * @member {string} columnheader
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.columnheader = "";

                        /**
                         * AccessibilityMsgProto password.
                         * @member {boolean} password
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.password = false;

                        /**
                         * AccessibilityMsgProto toggle.
                         * @member {boolean} toggle
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.toggle = false;

                        /**
                         * AccessibilityMsgProto selstart.
                         * @member {number} selstart
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.selstart = 0;

                        /**
                         * AccessibilityMsgProto selend.
                         * @member {number} selend
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.selend = 0;

                        /**
                         * AccessibilityMsgProto rowheight.
                         * @member {number} rowheight
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.rowheight = 0;

                        /**
                         * AccessibilityMsgProto rows.
                         * @member {number} rows
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.rows = 0;

                        /**
                         * AccessibilityMsgProto size.
                         * @member {number} size
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.size = 0;

                        /**
                         * AccessibilityMsgProto position.
                         * @member {number} position
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.position = 0;

                        /**
                         * AccessibilityMsgProto level.
                         * @member {number} level
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.level = 0;

                        /**
                         * AccessibilityMsgProto colindex.
                         * @member {number} colindex
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.colindex = 0;

                        /**
                         * AccessibilityMsgProto rowindex.
                         * @member {number} rowindex
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.rowindex = 0;

                        /**
                         * AccessibilityMsgProto colcount.
                         * @member {number} colcount
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.colcount = 0;

                        /**
                         * AccessibilityMsgProto rowcount.
                         * @member {number} rowcount
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.rowcount = 0;

                        /**
                         * AccessibilityMsgProto states.
                         * @member {Array.<string>} states
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.states = $util.emptyArray;

                        /**
                         * AccessibilityMsgProto min.
                         * @member {number} min
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.min = 0;

                        /**
                         * AccessibilityMsgProto max.
                         * @member {number} max
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.max = 0;

                        /**
                         * AccessibilityMsgProto val.
                         * @member {number} val
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.val = 0;

                        /**
                         * AccessibilityMsgProto screenX.
                         * @member {number} screenX
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.screenX = 0;

                        /**
                         * AccessibilityMsgProto screenY.
                         * @member {number} screenY
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.screenY = 0;

                        /**
                         * AccessibilityMsgProto width.
                         * @member {number} width
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.width = 0;

                        /**
                         * AccessibilityMsgProto height.
                         * @member {number} height
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.height = 0;

                        /**
                         * AccessibilityMsgProto hierarchy.
                         * @member {Array.<org.webswing.server.model.proto.IAccessibilityHierarchyMsgProto>} hierarchy
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         */
                        AccessibilityMsgProto.prototype.hierarchy = $util.emptyArray;

                        /**
                         * Creates a new AccessibilityMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IAccessibilityMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.AccessibilityMsgProto} AccessibilityMsgProto instance
                         */
                        AccessibilityMsgProto.create = function create(properties) {
                            return new AccessibilityMsgProto(properties);
                        };

                        /**
                         * Encodes the specified AccessibilityMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.AccessibilityMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IAccessibilityMsgProto} message AccessibilityMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        AccessibilityMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.id != null && message.hasOwnProperty("id"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
                            if (message.role != null && message.hasOwnProperty("role"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.role);
                            if (message.text != null && message.hasOwnProperty("text"))
                                writer.uint32(/* id 3, wireType 2 =*/26).string(message.text);
                            if (message.tooltip != null && message.hasOwnProperty("tooltip"))
                                writer.uint32(/* id 4, wireType 2 =*/34).string(message.tooltip);
                            if (message.value != null && message.hasOwnProperty("value"))
                                writer.uint32(/* id 5, wireType 2 =*/42).string(message.value);
                            if (message.description != null && message.hasOwnProperty("description"))
                                writer.uint32(/* id 6, wireType 2 =*/50).string(message.description);
                            if (message.columnheader != null && message.hasOwnProperty("columnheader"))
                                writer.uint32(/* id 7, wireType 2 =*/58).string(message.columnheader);
                            if (message.password != null && message.hasOwnProperty("password"))
                                writer.uint32(/* id 8, wireType 0 =*/64).bool(message.password);
                            if (message.toggle != null && message.hasOwnProperty("toggle"))
                                writer.uint32(/* id 9, wireType 0 =*/72).bool(message.toggle);
                            if (message.selstart != null && message.hasOwnProperty("selstart"))
                                writer.uint32(/* id 10, wireType 0 =*/80).sint32(message.selstart);
                            if (message.selend != null && message.hasOwnProperty("selend"))
                                writer.uint32(/* id 11, wireType 0 =*/88).sint32(message.selend);
                            if (message.rowheight != null && message.hasOwnProperty("rowheight"))
                                writer.uint32(/* id 12, wireType 0 =*/96).sint32(message.rowheight);
                            if (message.rows != null && message.hasOwnProperty("rows"))
                                writer.uint32(/* id 13, wireType 0 =*/104).sint32(message.rows);
                            if (message.size != null && message.hasOwnProperty("size"))
                                writer.uint32(/* id 14, wireType 0 =*/112).sint32(message.size);
                            if (message.position != null && message.hasOwnProperty("position"))
                                writer.uint32(/* id 15, wireType 0 =*/120).sint32(message.position);
                            if (message.level != null && message.hasOwnProperty("level"))
                                writer.uint32(/* id 16, wireType 0 =*/128).sint32(message.level);
                            if (message.colindex != null && message.hasOwnProperty("colindex"))
                                writer.uint32(/* id 17, wireType 0 =*/136).sint32(message.colindex);
                            if (message.rowindex != null && message.hasOwnProperty("rowindex"))
                                writer.uint32(/* id 18, wireType 0 =*/144).sint32(message.rowindex);
                            if (message.colcount != null && message.hasOwnProperty("colcount"))
                                writer.uint32(/* id 19, wireType 0 =*/152).sint32(message.colcount);
                            if (message.rowcount != null && message.hasOwnProperty("rowcount"))
                                writer.uint32(/* id 20, wireType 0 =*/160).sint32(message.rowcount);
                            if (message.states != null && message.states.length)
                                for (var i = 0; i < message.states.length; ++i)
                                    writer.uint32(/* id 21, wireType 2 =*/170).string(message.states[i]);
                            if (message.min != null && message.hasOwnProperty("min"))
                                writer.uint32(/* id 22, wireType 0 =*/176).sint32(message.min);
                            if (message.max != null && message.hasOwnProperty("max"))
                                writer.uint32(/* id 23, wireType 0 =*/184).sint32(message.max);
                            if (message.val != null && message.hasOwnProperty("val"))
                                writer.uint32(/* id 24, wireType 0 =*/192).sint32(message.val);
                            if (message.screenX != null && message.hasOwnProperty("screenX"))
                                writer.uint32(/* id 25, wireType 0 =*/200).sint32(message.screenX);
                            if (message.screenY != null && message.hasOwnProperty("screenY"))
                                writer.uint32(/* id 26, wireType 0 =*/208).sint32(message.screenY);
                            if (message.width != null && message.hasOwnProperty("width"))
                                writer.uint32(/* id 27, wireType 0 =*/216).sint32(message.width);
                            if (message.height != null && message.hasOwnProperty("height"))
                                writer.uint32(/* id 28, wireType 0 =*/224).sint32(message.height);
                            if (message.hierarchy != null && message.hierarchy.length)
                                for (var i = 0; i < message.hierarchy.length; ++i)
                                    $root.org.webswing.server.model.proto.AccessibilityHierarchyMsgProto.encode(message.hierarchy[i], writer.uint32(/* id 29, wireType 2 =*/234).fork()).ldelim();
                            return writer;
                        };

                        /**
                         * Decodes an AccessibilityMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.AccessibilityMsgProto} AccessibilityMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        AccessibilityMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.AccessibilityMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.id = reader.string();
                                    break;
                                case 2:
                                    message.role = reader.string();
                                    break;
                                case 3:
                                    message.text = reader.string();
                                    break;
                                case 4:
                                    message.tooltip = reader.string();
                                    break;
                                case 5:
                                    message.value = reader.string();
                                    break;
                                case 6:
                                    message.description = reader.string();
                                    break;
                                case 7:
                                    message.columnheader = reader.string();
                                    break;
                                case 8:
                                    message.password = reader.bool();
                                    break;
                                case 9:
                                    message.toggle = reader.bool();
                                    break;
                                case 10:
                                    message.selstart = reader.sint32();
                                    break;
                                case 11:
                                    message.selend = reader.sint32();
                                    break;
                                case 12:
                                    message.rowheight = reader.sint32();
                                    break;
                                case 13:
                                    message.rows = reader.sint32();
                                    break;
                                case 14:
                                    message.size = reader.sint32();
                                    break;
                                case 15:
                                    message.position = reader.sint32();
                                    break;
                                case 16:
                                    message.level = reader.sint32();
                                    break;
                                case 17:
                                    message.colindex = reader.sint32();
                                    break;
                                case 18:
                                    message.rowindex = reader.sint32();
                                    break;
                                case 19:
                                    message.colcount = reader.sint32();
                                    break;
                                case 20:
                                    message.rowcount = reader.sint32();
                                    break;
                                case 21:
                                    if (!(message.states && message.states.length))
                                        message.states = [];
                                    message.states.push(reader.string());
                                    break;
                                case 22:
                                    message.min = reader.sint32();
                                    break;
                                case 23:
                                    message.max = reader.sint32();
                                    break;
                                case 24:
                                    message.val = reader.sint32();
                                    break;
                                case 25:
                                    message.screenX = reader.sint32();
                                    break;
                                case 26:
                                    message.screenY = reader.sint32();
                                    break;
                                case 27:
                                    message.width = reader.sint32();
                                    break;
                                case 28:
                                    message.height = reader.sint32();
                                    break;
                                case 29:
                                    if (!(message.hierarchy && message.hierarchy.length))
                                        message.hierarchy = [];
                                    message.hierarchy.push($root.org.webswing.server.model.proto.AccessibilityHierarchyMsgProto.decode(reader, reader.uint32()));
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates an AccessibilityMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.AccessibilityMsgProto} AccessibilityMsgProto
                         */
                        AccessibilityMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.AccessibilityMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.AccessibilityMsgProto();
                            if (object.id != null)
                                message.id = String(object.id);
                            if (object.role != null)
                                message.role = String(object.role);
                            if (object.text != null)
                                message.text = String(object.text);
                            if (object.tooltip != null)
                                message.tooltip = String(object.tooltip);
                            if (object.value != null)
                                message.value = String(object.value);
                            if (object.description != null)
                                message.description = String(object.description);
                            if (object.columnheader != null)
                                message.columnheader = String(object.columnheader);
                            if (object.password != null)
                                message.password = Boolean(object.password);
                            if (object.toggle != null)
                                message.toggle = Boolean(object.toggle);
                            if (object.selstart != null)
                                message.selstart = object.selstart | 0;
                            if (object.selend != null)
                                message.selend = object.selend | 0;
                            if (object.rowheight != null)
                                message.rowheight = object.rowheight | 0;
                            if (object.rows != null)
                                message.rows = object.rows | 0;
                            if (object.size != null)
                                message.size = object.size | 0;
                            if (object.position != null)
                                message.position = object.position | 0;
                            if (object.level != null)
                                message.level = object.level | 0;
                            if (object.colindex != null)
                                message.colindex = object.colindex | 0;
                            if (object.rowindex != null)
                                message.rowindex = object.rowindex | 0;
                            if (object.colcount != null)
                                message.colcount = object.colcount | 0;
                            if (object.rowcount != null)
                                message.rowcount = object.rowcount | 0;
                            if (object.states) {
                                if (!Array.isArray(object.states))
                                    throw TypeError(".org.webswing.server.model.proto.AccessibilityMsgProto.states: array expected");
                                message.states = [];
                                for (var i = 0; i < object.states.length; ++i)
                                    message.states[i] = String(object.states[i]);
                            }
                            if (object.min != null)
                                message.min = object.min | 0;
                            if (object.max != null)
                                message.max = object.max | 0;
                            if (object.val != null)
                                message.val = object.val | 0;
                            if (object.screenX != null)
                                message.screenX = object.screenX | 0;
                            if (object.screenY != null)
                                message.screenY = object.screenY | 0;
                            if (object.width != null)
                                message.width = object.width | 0;
                            if (object.height != null)
                                message.height = object.height | 0;
                            if (object.hierarchy) {
                                if (!Array.isArray(object.hierarchy))
                                    throw TypeError(".org.webswing.server.model.proto.AccessibilityMsgProto.hierarchy: array expected");
                                message.hierarchy = [];
                                for (var i = 0; i < object.hierarchy.length; ++i) {
                                    if (typeof object.hierarchy[i] !== "object")
                                        throw TypeError(".org.webswing.server.model.proto.AccessibilityMsgProto.hierarchy: object expected");
                                    message.hierarchy[i] = $root.org.webswing.server.model.proto.AccessibilityHierarchyMsgProto.fromObject(object.hierarchy[i]);
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from an AccessibilityMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.AccessibilityMsgProto} message AccessibilityMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        AccessibilityMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.arrays || options.defaults) {
                                object.states = [];
                                object.hierarchy = [];
                            }
                            if (options.defaults) {
                                object.id = "";
                                object.role = "";
                                object.text = "";
                                object.tooltip = "";
                                object.value = "";
                                object.description = "";
                                object.columnheader = "";
                                object.password = false;
                                object.toggle = false;
                                object.selstart = 0;
                                object.selend = 0;
                                object.rowheight = 0;
                                object.rows = 0;
                                object.size = 0;
                                object.position = 0;
                                object.level = 0;
                                object.colindex = 0;
                                object.rowindex = 0;
                                object.colcount = 0;
                                object.rowcount = 0;
                                object.min = 0;
                                object.max = 0;
                                object.val = 0;
                                object.screenX = 0;
                                object.screenY = 0;
                                object.width = 0;
                                object.height = 0;
                            }
                            if (message.id != null && message.hasOwnProperty("id"))
                                object.id = message.id;
                            if (message.role != null && message.hasOwnProperty("role"))
                                object.role = message.role;
                            if (message.text != null && message.hasOwnProperty("text"))
                                object.text = message.text;
                            if (message.tooltip != null && message.hasOwnProperty("tooltip"))
                                object.tooltip = message.tooltip;
                            if (message.value != null && message.hasOwnProperty("value"))
                                object.value = message.value;
                            if (message.description != null && message.hasOwnProperty("description"))
                                object.description = message.description;
                            if (message.columnheader != null && message.hasOwnProperty("columnheader"))
                                object.columnheader = message.columnheader;
                            if (message.password != null && message.hasOwnProperty("password"))
                                object.password = message.password;
                            if (message.toggle != null && message.hasOwnProperty("toggle"))
                                object.toggle = message.toggle;
                            if (message.selstart != null && message.hasOwnProperty("selstart"))
                                object.selstart = message.selstart;
                            if (message.selend != null && message.hasOwnProperty("selend"))
                                object.selend = message.selend;
                            if (message.rowheight != null && message.hasOwnProperty("rowheight"))
                                object.rowheight = message.rowheight;
                            if (message.rows != null && message.hasOwnProperty("rows"))
                                object.rows = message.rows;
                            if (message.size != null && message.hasOwnProperty("size"))
                                object.size = message.size;
                            if (message.position != null && message.hasOwnProperty("position"))
                                object.position = message.position;
                            if (message.level != null && message.hasOwnProperty("level"))
                                object.level = message.level;
                            if (message.colindex != null && message.hasOwnProperty("colindex"))
                                object.colindex = message.colindex;
                            if (message.rowindex != null && message.hasOwnProperty("rowindex"))
                                object.rowindex = message.rowindex;
                            if (message.colcount != null && message.hasOwnProperty("colcount"))
                                object.colcount = message.colcount;
                            if (message.rowcount != null && message.hasOwnProperty("rowcount"))
                                object.rowcount = message.rowcount;
                            if (message.states && message.states.length) {
                                object.states = [];
                                for (var j = 0; j < message.states.length; ++j)
                                    object.states[j] = message.states[j];
                            }
                            if (message.min != null && message.hasOwnProperty("min"))
                                object.min = message.min;
                            if (message.max != null && message.hasOwnProperty("max"))
                                object.max = message.max;
                            if (message.val != null && message.hasOwnProperty("val"))
                                object.val = message.val;
                            if (message.screenX != null && message.hasOwnProperty("screenX"))
                                object.screenX = message.screenX;
                            if (message.screenY != null && message.hasOwnProperty("screenY"))
                                object.screenY = message.screenY;
                            if (message.width != null && message.hasOwnProperty("width"))
                                object.width = message.width;
                            if (message.height != null && message.hasOwnProperty("height"))
                                object.height = message.height;
                            if (message.hierarchy && message.hierarchy.length) {
                                object.hierarchy = [];
                                for (var j = 0; j < message.hierarchy.length; ++j)
                                    object.hierarchy[j] = $root.org.webswing.server.model.proto.AccessibilityHierarchyMsgProto.toObject(message.hierarchy[j], options);
                            }
                            return object;
                        };

                        /**
                         * Converts this AccessibilityMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.AccessibilityMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        AccessibilityMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return AccessibilityMsgProto;
                    })();

                    proto.AccessibilityHierarchyMsgProto = (function() {

                        /**
                         * Properties of an AccessibilityHierarchyMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IAccessibilityHierarchyMsgProto
                         * @property {string|null} [id] AccessibilityHierarchyMsgProto id
                         * @property {string|null} [role] AccessibilityHierarchyMsgProto role
                         * @property {string|null} [text] AccessibilityHierarchyMsgProto text
                         * @property {number|null} [position] AccessibilityHierarchyMsgProto position
                         * @property {number|null} [size] AccessibilityHierarchyMsgProto size
                         */

                        /**
                         * Constructs a new AccessibilityHierarchyMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents an AccessibilityHierarchyMsgProto.
                         * @implements IAccessibilityHierarchyMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IAccessibilityHierarchyMsgProto=} [properties] Properties to set
                         */
                        function AccessibilityHierarchyMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * AccessibilityHierarchyMsgProto id.
                         * @member {string} id
                         * @memberof org.webswing.server.model.proto.AccessibilityHierarchyMsgProto
                         * @instance
                         */
                        AccessibilityHierarchyMsgProto.prototype.id = "";

                        /**
                         * AccessibilityHierarchyMsgProto role.
                         * @member {string} role
                         * @memberof org.webswing.server.model.proto.AccessibilityHierarchyMsgProto
                         * @instance
                         */
                        AccessibilityHierarchyMsgProto.prototype.role = "";

                        /**
                         * AccessibilityHierarchyMsgProto text.
                         * @member {string} text
                         * @memberof org.webswing.server.model.proto.AccessibilityHierarchyMsgProto
                         * @instance
                         */
                        AccessibilityHierarchyMsgProto.prototype.text = "";

                        /**
                         * AccessibilityHierarchyMsgProto position.
                         * @member {number} position
                         * @memberof org.webswing.server.model.proto.AccessibilityHierarchyMsgProto
                         * @instance
                         */
                        AccessibilityHierarchyMsgProto.prototype.position = 0;

                        /**
                         * AccessibilityHierarchyMsgProto size.
                         * @member {number} size
                         * @memberof org.webswing.server.model.proto.AccessibilityHierarchyMsgProto
                         * @instance
                         */
                        AccessibilityHierarchyMsgProto.prototype.size = 0;

                        /**
                         * Creates a new AccessibilityHierarchyMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.AccessibilityHierarchyMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IAccessibilityHierarchyMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.AccessibilityHierarchyMsgProto} AccessibilityHierarchyMsgProto instance
                         */
                        AccessibilityHierarchyMsgProto.create = function create(properties) {
                            return new AccessibilityHierarchyMsgProto(properties);
                        };

                        /**
                         * Encodes the specified AccessibilityHierarchyMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.AccessibilityHierarchyMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.AccessibilityHierarchyMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IAccessibilityHierarchyMsgProto} message AccessibilityHierarchyMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        AccessibilityHierarchyMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.id != null && message.hasOwnProperty("id"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
                            if (message.role != null && message.hasOwnProperty("role"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.role);
                            if (message.text != null && message.hasOwnProperty("text"))
                                writer.uint32(/* id 3, wireType 2 =*/26).string(message.text);
                            if (message.position != null && message.hasOwnProperty("position"))
                                writer.uint32(/* id 4, wireType 0 =*/32).sint32(message.position);
                            if (message.size != null && message.hasOwnProperty("size"))
                                writer.uint32(/* id 5, wireType 0 =*/40).sint32(message.size);
                            return writer;
                        };

                        /**
                         * Decodes an AccessibilityHierarchyMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.AccessibilityHierarchyMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.AccessibilityHierarchyMsgProto} AccessibilityHierarchyMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        AccessibilityHierarchyMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.AccessibilityHierarchyMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.id = reader.string();
                                    break;
                                case 2:
                                    message.role = reader.string();
                                    break;
                                case 3:
                                    message.text = reader.string();
                                    break;
                                case 4:
                                    message.position = reader.sint32();
                                    break;
                                case 5:
                                    message.size = reader.sint32();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates an AccessibilityHierarchyMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.AccessibilityHierarchyMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.AccessibilityHierarchyMsgProto} AccessibilityHierarchyMsgProto
                         */
                        AccessibilityHierarchyMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.AccessibilityHierarchyMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.AccessibilityHierarchyMsgProto();
                            if (object.id != null)
                                message.id = String(object.id);
                            if (object.role != null)
                                message.role = String(object.role);
                            if (object.text != null)
                                message.text = String(object.text);
                            if (object.position != null)
                                message.position = object.position | 0;
                            if (object.size != null)
                                message.size = object.size | 0;
                            return message;
                        };

                        /**
                         * Creates a plain object from an AccessibilityHierarchyMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.AccessibilityHierarchyMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.AccessibilityHierarchyMsgProto} message AccessibilityHierarchyMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        AccessibilityHierarchyMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.id = "";
                                object.role = "";
                                object.text = "";
                                object.position = 0;
                                object.size = 0;
                            }
                            if (message.id != null && message.hasOwnProperty("id"))
                                object.id = message.id;
                            if (message.role != null && message.hasOwnProperty("role"))
                                object.role = message.role;
                            if (message.text != null && message.hasOwnProperty("text"))
                                object.text = message.text;
                            if (message.position != null && message.hasOwnProperty("position"))
                                object.position = message.position;
                            if (message.size != null && message.hasOwnProperty("size"))
                                object.size = message.size;
                            return object;
                        };

                        /**
                         * Converts this AccessibilityHierarchyMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.AccessibilityHierarchyMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        AccessibilityHierarchyMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return AccessibilityHierarchyMsgProto;
                    })();

                    proto.FocusEventMsgProto = (function() {

                        /**
                         * Properties of a FocusEventMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IFocusEventMsgProto
                         * @property {org.webswing.server.model.proto.FocusEventMsgProto.FocusEventTypeProto} type FocusEventMsgProto type
                         * @property {number|null} [x] FocusEventMsgProto x
                         * @property {number|null} [y] FocusEventMsgProto y
                         * @property {number|null} [w] FocusEventMsgProto w
                         * @property {number|null} [h] FocusEventMsgProto h
                         * @property {number|null} [caretX] FocusEventMsgProto caretX
                         * @property {number|null} [caretY] FocusEventMsgProto caretY
                         * @property {number|null} [caretH] FocusEventMsgProto caretH
                         * @property {boolean|null} [editable] FocusEventMsgProto editable
                         */

                        /**
                         * Constructs a new FocusEventMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a FocusEventMsgProto.
                         * @implements IFocusEventMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IFocusEventMsgProto=} [properties] Properties to set
                         */
                        function FocusEventMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * FocusEventMsgProto type.
                         * @member {org.webswing.server.model.proto.FocusEventMsgProto.FocusEventTypeProto} type
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @instance
                         */
                        FocusEventMsgProto.prototype.type = 1;

                        /**
                         * FocusEventMsgProto x.
                         * @member {number} x
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @instance
                         */
                        FocusEventMsgProto.prototype.x = 0;

                        /**
                         * FocusEventMsgProto y.
                         * @member {number} y
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @instance
                         */
                        FocusEventMsgProto.prototype.y = 0;

                        /**
                         * FocusEventMsgProto w.
                         * @member {number} w
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @instance
                         */
                        FocusEventMsgProto.prototype.w = 0;

                        /**
                         * FocusEventMsgProto h.
                         * @member {number} h
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @instance
                         */
                        FocusEventMsgProto.prototype.h = 0;

                        /**
                         * FocusEventMsgProto caretX.
                         * @member {number} caretX
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @instance
                         */
                        FocusEventMsgProto.prototype.caretX = 0;

                        /**
                         * FocusEventMsgProto caretY.
                         * @member {number} caretY
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @instance
                         */
                        FocusEventMsgProto.prototype.caretY = 0;

                        /**
                         * FocusEventMsgProto caretH.
                         * @member {number} caretH
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @instance
                         */
                        FocusEventMsgProto.prototype.caretH = 0;

                        /**
                         * FocusEventMsgProto editable.
                         * @member {boolean} editable
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @instance
                         */
                        FocusEventMsgProto.prototype.editable = false;

                        /**
                         * Creates a new FocusEventMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IFocusEventMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.FocusEventMsgProto} FocusEventMsgProto instance
                         */
                        FocusEventMsgProto.create = function create(properties) {
                            return new FocusEventMsgProto(properties);
                        };

                        /**
                         * Encodes the specified FocusEventMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.FocusEventMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IFocusEventMsgProto} message FocusEventMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        FocusEventMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            writer.uint32(/* id 1, wireType 0 =*/8).int32(message.type);
                            if (message.x != null && message.hasOwnProperty("x"))
                                writer.uint32(/* id 2, wireType 0 =*/16).sint32(message.x);
                            if (message.y != null && message.hasOwnProperty("y"))
                                writer.uint32(/* id 3, wireType 0 =*/24).sint32(message.y);
                            if (message.w != null && message.hasOwnProperty("w"))
                                writer.uint32(/* id 4, wireType 0 =*/32).uint32(message.w);
                            if (message.h != null && message.hasOwnProperty("h"))
                                writer.uint32(/* id 5, wireType 0 =*/40).uint32(message.h);
                            if (message.caretX != null && message.hasOwnProperty("caretX"))
                                writer.uint32(/* id 6, wireType 0 =*/48).sint32(message.caretX);
                            if (message.caretY != null && message.hasOwnProperty("caretY"))
                                writer.uint32(/* id 7, wireType 0 =*/56).sint32(message.caretY);
                            if (message.caretH != null && message.hasOwnProperty("caretH"))
                                writer.uint32(/* id 8, wireType 0 =*/64).sint32(message.caretH);
                            if (message.editable != null && message.hasOwnProperty("editable"))
                                writer.uint32(/* id 9, wireType 0 =*/72).bool(message.editable);
                            return writer;
                        };

                        /**
                         * Decodes a FocusEventMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.FocusEventMsgProto} FocusEventMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        FocusEventMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.FocusEventMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.type = reader.int32();
                                    break;
                                case 2:
                                    message.x = reader.sint32();
                                    break;
                                case 3:
                                    message.y = reader.sint32();
                                    break;
                                case 4:
                                    message.w = reader.uint32();
                                    break;
                                case 5:
                                    message.h = reader.uint32();
                                    break;
                                case 6:
                                    message.caretX = reader.sint32();
                                    break;
                                case 7:
                                    message.caretY = reader.sint32();
                                    break;
                                case 8:
                                    message.caretH = reader.sint32();
                                    break;
                                case 9:
                                    message.editable = reader.bool();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            if (!message.hasOwnProperty("type"))
                                throw $util.ProtocolError("missing required 'type'", { instance: message });
                            return message;
                        };

                        /**
                         * Creates a FocusEventMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.FocusEventMsgProto} FocusEventMsgProto
                         */
                        FocusEventMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.FocusEventMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.FocusEventMsgProto();
                            switch (object.type) {
                            case "focusLost":
                            case 1:
                                message.type = 1;
                                break;
                            case "focusGained":
                            case 2:
                                message.type = 2;
                                break;
                            case "focusWithCarretGained":
                            case 3:
                                message.type = 3;
                                break;
                            case "focusPasswordGained":
                            case 4:
                                message.type = 4;
                                break;
                            }
                            if (object.x != null)
                                message.x = object.x | 0;
                            if (object.y != null)
                                message.y = object.y | 0;
                            if (object.w != null)
                                message.w = object.w >>> 0;
                            if (object.h != null)
                                message.h = object.h >>> 0;
                            if (object.caretX != null)
                                message.caretX = object.caretX | 0;
                            if (object.caretY != null)
                                message.caretY = object.caretY | 0;
                            if (object.caretH != null)
                                message.caretH = object.caretH | 0;
                            if (object.editable != null)
                                message.editable = Boolean(object.editable);
                            return message;
                        };

                        /**
                         * Creates a plain object from a FocusEventMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.FocusEventMsgProto} message FocusEventMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        FocusEventMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.type = options.enums === String ? "focusLost" : 1;
                                object.x = 0;
                                object.y = 0;
                                object.w = 0;
                                object.h = 0;
                                object.caretX = 0;
                                object.caretY = 0;
                                object.caretH = 0;
                                object.editable = false;
                            }
                            if (message.type != null && message.hasOwnProperty("type"))
                                object.type = options.enums === String ? $root.org.webswing.server.model.proto.FocusEventMsgProto.FocusEventTypeProto[message.type] : message.type;
                            if (message.x != null && message.hasOwnProperty("x"))
                                object.x = message.x;
                            if (message.y != null && message.hasOwnProperty("y"))
                                object.y = message.y;
                            if (message.w != null && message.hasOwnProperty("w"))
                                object.w = message.w;
                            if (message.h != null && message.hasOwnProperty("h"))
                                object.h = message.h;
                            if (message.caretX != null && message.hasOwnProperty("caretX"))
                                object.caretX = message.caretX;
                            if (message.caretY != null && message.hasOwnProperty("caretY"))
                                object.caretY = message.caretY;
                            if (message.caretH != null && message.hasOwnProperty("caretH"))
                                object.caretH = message.caretH;
                            if (message.editable != null && message.hasOwnProperty("editable"))
                                object.editable = message.editable;
                            return object;
                        };

                        /**
                         * Converts this FocusEventMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.FocusEventMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        FocusEventMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        /**
                         * FocusEventTypeProto enum.
                         * @name org.webswing.server.model.proto.FocusEventMsgProto.FocusEventTypeProto
                         * @enum {string}
                         * @property {number} focusLost=1 focusLost value
                         * @property {number} focusGained=2 focusGained value
                         * @property {number} focusWithCarretGained=3 focusWithCarretGained value
                         * @property {number} focusPasswordGained=4 focusPasswordGained value
                         */
                        FocusEventMsgProto.FocusEventTypeProto = (function() {
                            var valuesById = {}, values = Object.create(valuesById);
                            values[valuesById[1] = "focusLost"] = 1;
                            values[valuesById[2] = "focusGained"] = 2;
                            values[valuesById[3] = "focusWithCarretGained"] = 3;
                            values[valuesById[4] = "focusPasswordGained"] = 4;
                            return values;
                        })();

                        return FocusEventMsgProto;
                    })();

                    proto.ApplicationInfoMsgProto = (function() {

                        /**
                         * Properties of an ApplicationInfoMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IApplicationInfoMsgProto
                         * @property {string} name ApplicationInfoMsgProto name
                         * @property {Uint8Array|null} [base64Icon] ApplicationInfoMsgProto base64Icon
                         * @property {string|null} [url] ApplicationInfoMsgProto url
                         */

                        /**
                         * Constructs a new ApplicationInfoMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents an ApplicationInfoMsgProto.
                         * @implements IApplicationInfoMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IApplicationInfoMsgProto=} [properties] Properties to set
                         */
                        function ApplicationInfoMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * ApplicationInfoMsgProto name.
                         * @member {string} name
                         * @memberof org.webswing.server.model.proto.ApplicationInfoMsgProto
                         * @instance
                         */
                        ApplicationInfoMsgProto.prototype.name = "";

                        /**
                         * ApplicationInfoMsgProto base64Icon.
                         * @member {Uint8Array} base64Icon
                         * @memberof org.webswing.server.model.proto.ApplicationInfoMsgProto
                         * @instance
                         */
                        ApplicationInfoMsgProto.prototype.base64Icon = $util.newBuffer([]);

                        /**
                         * ApplicationInfoMsgProto url.
                         * @member {string} url
                         * @memberof org.webswing.server.model.proto.ApplicationInfoMsgProto
                         * @instance
                         */
                        ApplicationInfoMsgProto.prototype.url = "";

                        /**
                         * Creates a new ApplicationInfoMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.ApplicationInfoMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IApplicationInfoMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.ApplicationInfoMsgProto} ApplicationInfoMsgProto instance
                         */
                        ApplicationInfoMsgProto.create = function create(properties) {
                            return new ApplicationInfoMsgProto(properties);
                        };

                        /**
                         * Encodes the specified ApplicationInfoMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.ApplicationInfoMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.ApplicationInfoMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IApplicationInfoMsgProto} message ApplicationInfoMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        ApplicationInfoMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            writer.uint32(/* id 1, wireType 2 =*/10).string(message.name);
                            if (message.base64Icon != null && message.hasOwnProperty("base64Icon"))
                                writer.uint32(/* id 2, wireType 2 =*/18).bytes(message.base64Icon);
                            if (message.url != null && message.hasOwnProperty("url"))
                                writer.uint32(/* id 3, wireType 2 =*/26).string(message.url);
                            return writer;
                        };

                        /**
                         * Decodes an ApplicationInfoMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.ApplicationInfoMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.ApplicationInfoMsgProto} ApplicationInfoMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        ApplicationInfoMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.ApplicationInfoMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.name = reader.string();
                                    break;
                                case 2:
                                    message.base64Icon = reader.bytes();
                                    break;
                                case 3:
                                    message.url = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            if (!message.hasOwnProperty("name"))
                                throw $util.ProtocolError("missing required 'name'", { instance: message });
                            return message;
                        };

                        /**
                         * Creates an ApplicationInfoMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.ApplicationInfoMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.ApplicationInfoMsgProto} ApplicationInfoMsgProto
                         */
                        ApplicationInfoMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.ApplicationInfoMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.ApplicationInfoMsgProto();
                            if (object.name != null)
                                message.name = String(object.name);
                            if (object.base64Icon != null)
                                if (typeof object.base64Icon === "string")
                                    $util.base64.decode(object.base64Icon, message.base64Icon = $util.newBuffer($util.base64.length(object.base64Icon)), 0);
                                else if (object.base64Icon.length)
                                    message.base64Icon = object.base64Icon;
                            if (object.url != null)
                                message.url = String(object.url);
                            return message;
                        };

                        /**
                         * Creates a plain object from an ApplicationInfoMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.ApplicationInfoMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.ApplicationInfoMsgProto} message ApplicationInfoMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        ApplicationInfoMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.name = "";
                                if (options.bytes === String)
                                    object.base64Icon = "";
                                else {
                                    object.base64Icon = [];
                                    if (options.bytes !== Array)
                                        object.base64Icon = $util.newBuffer(object.base64Icon);
                                }
                                object.url = "";
                            }
                            if (message.name != null && message.hasOwnProperty("name"))
                                object.name = message.name;
                            if (message.base64Icon != null && message.hasOwnProperty("base64Icon"))
                                object.base64Icon = options.bytes === String ? $util.base64.encode(message.base64Icon, 0, message.base64Icon.length) : options.bytes === Array ? Array.prototype.slice.call(message.base64Icon) : message.base64Icon;
                            if (message.url != null && message.hasOwnProperty("url"))
                                object.url = message.url;
                            return object;
                        };

                        /**
                         * Converts this ApplicationInfoMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.ApplicationInfoMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        ApplicationInfoMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return ApplicationInfoMsgProto;
                    })();

                    proto.LinkActionMsgProto = (function() {

                        /**
                         * Properties of a LinkActionMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface ILinkActionMsgProto
                         * @property {org.webswing.server.model.proto.LinkActionMsgProto.LinkActionTypeProto} action LinkActionMsgProto action
                         * @property {string} src LinkActionMsgProto src
                         */

                        /**
                         * Constructs a new LinkActionMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a LinkActionMsgProto.
                         * @implements ILinkActionMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.ILinkActionMsgProto=} [properties] Properties to set
                         */
                        function LinkActionMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * LinkActionMsgProto action.
                         * @member {org.webswing.server.model.proto.LinkActionMsgProto.LinkActionTypeProto} action
                         * @memberof org.webswing.server.model.proto.LinkActionMsgProto
                         * @instance
                         */
                        LinkActionMsgProto.prototype.action = 0;

                        /**
                         * LinkActionMsgProto src.
                         * @member {string} src
                         * @memberof org.webswing.server.model.proto.LinkActionMsgProto
                         * @instance
                         */
                        LinkActionMsgProto.prototype.src = "";

                        /**
                         * Creates a new LinkActionMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.LinkActionMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.ILinkActionMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.LinkActionMsgProto} LinkActionMsgProto instance
                         */
                        LinkActionMsgProto.create = function create(properties) {
                            return new LinkActionMsgProto(properties);
                        };

                        /**
                         * Encodes the specified LinkActionMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.LinkActionMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.LinkActionMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.ILinkActionMsgProto} message LinkActionMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        LinkActionMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            writer.uint32(/* id 1, wireType 0 =*/8).int32(message.action);
                            writer.uint32(/* id 2, wireType 2 =*/18).string(message.src);
                            return writer;
                        };

                        /**
                         * Decodes a LinkActionMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.LinkActionMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.LinkActionMsgProto} LinkActionMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        LinkActionMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.LinkActionMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.action = reader.int32();
                                    break;
                                case 2:
                                    message.src = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            if (!message.hasOwnProperty("action"))
                                throw $util.ProtocolError("missing required 'action'", { instance: message });
                            if (!message.hasOwnProperty("src"))
                                throw $util.ProtocolError("missing required 'src'", { instance: message });
                            return message;
                        };

                        /**
                         * Creates a LinkActionMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.LinkActionMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.LinkActionMsgProto} LinkActionMsgProto
                         */
                        LinkActionMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.LinkActionMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.LinkActionMsgProto();
                            switch (object.action) {
                            case "file":
                            case 0:
                                message.action = 0;
                                break;
                            case "url":
                            case 1:
                                message.action = 1;
                                break;
                            case "print":
                            case 2:
                                message.action = 2;
                                break;
                            case "redirect":
                            case 3:
                                message.action = 3;
                                break;
                            }
                            if (object.src != null)
                                message.src = String(object.src);
                            return message;
                        };

                        /**
                         * Creates a plain object from a LinkActionMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.LinkActionMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.LinkActionMsgProto} message LinkActionMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        LinkActionMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.action = options.enums === String ? "file" : 0;
                                object.src = "";
                            }
                            if (message.action != null && message.hasOwnProperty("action"))
                                object.action = options.enums === String ? $root.org.webswing.server.model.proto.LinkActionMsgProto.LinkActionTypeProto[message.action] : message.action;
                            if (message.src != null && message.hasOwnProperty("src"))
                                object.src = message.src;
                            return object;
                        };

                        /**
                         * Converts this LinkActionMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.LinkActionMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        LinkActionMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        /**
                         * LinkActionTypeProto enum.
                         * @name org.webswing.server.model.proto.LinkActionMsgProto.LinkActionTypeProto
                         * @enum {string}
                         * @property {number} file=0 file value
                         * @property {number} url=1 url value
                         * @property {number} print=2 print value
                         * @property {number} redirect=3 redirect value
                         */
                        LinkActionMsgProto.LinkActionTypeProto = (function() {
                            var valuesById = {}, values = Object.create(valuesById);
                            values[valuesById[0] = "file"] = 0;
                            values[valuesById[1] = "url"] = 1;
                            values[valuesById[2] = "print"] = 2;
                            values[valuesById[3] = "redirect"] = 3;
                            return values;
                        })();

                        return LinkActionMsgProto;
                    })();

                    proto.WindowMoveActionMsgProto = (function() {

                        /**
                         * Properties of a WindowMoveActionMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IWindowMoveActionMsgProto
                         * @property {number|null} [sx] WindowMoveActionMsgProto sx
                         * @property {number|null} [sy] WindowMoveActionMsgProto sy
                         * @property {number|null} [dx] WindowMoveActionMsgProto dx
                         * @property {number|null} [dy] WindowMoveActionMsgProto dy
                         * @property {number|null} [width] WindowMoveActionMsgProto width
                         * @property {number|null} [height] WindowMoveActionMsgProto height
                         */

                        /**
                         * Constructs a new WindowMoveActionMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a WindowMoveActionMsgProto.
                         * @implements IWindowMoveActionMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IWindowMoveActionMsgProto=} [properties] Properties to set
                         */
                        function WindowMoveActionMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * WindowMoveActionMsgProto sx.
                         * @member {number} sx
                         * @memberof org.webswing.server.model.proto.WindowMoveActionMsgProto
                         * @instance
                         */
                        WindowMoveActionMsgProto.prototype.sx = 0;

                        /**
                         * WindowMoveActionMsgProto sy.
                         * @member {number} sy
                         * @memberof org.webswing.server.model.proto.WindowMoveActionMsgProto
                         * @instance
                         */
                        WindowMoveActionMsgProto.prototype.sy = 0;

                        /**
                         * WindowMoveActionMsgProto dx.
                         * @member {number} dx
                         * @memberof org.webswing.server.model.proto.WindowMoveActionMsgProto
                         * @instance
                         */
                        WindowMoveActionMsgProto.prototype.dx = 0;

                        /**
                         * WindowMoveActionMsgProto dy.
                         * @member {number} dy
                         * @memberof org.webswing.server.model.proto.WindowMoveActionMsgProto
                         * @instance
                         */
                        WindowMoveActionMsgProto.prototype.dy = 0;

                        /**
                         * WindowMoveActionMsgProto width.
                         * @member {number} width
                         * @memberof org.webswing.server.model.proto.WindowMoveActionMsgProto
                         * @instance
                         */
                        WindowMoveActionMsgProto.prototype.width = 0;

                        /**
                         * WindowMoveActionMsgProto height.
                         * @member {number} height
                         * @memberof org.webswing.server.model.proto.WindowMoveActionMsgProto
                         * @instance
                         */
                        WindowMoveActionMsgProto.prototype.height = 0;

                        /**
                         * Creates a new WindowMoveActionMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.WindowMoveActionMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowMoveActionMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.WindowMoveActionMsgProto} WindowMoveActionMsgProto instance
                         */
                        WindowMoveActionMsgProto.create = function create(properties) {
                            return new WindowMoveActionMsgProto(properties);
                        };

                        /**
                         * Encodes the specified WindowMoveActionMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowMoveActionMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.WindowMoveActionMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowMoveActionMsgProto} message WindowMoveActionMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        WindowMoveActionMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.sx != null && message.hasOwnProperty("sx"))
                                writer.uint32(/* id 1, wireType 0 =*/8).sint32(message.sx);
                            if (message.sy != null && message.hasOwnProperty("sy"))
                                writer.uint32(/* id 2, wireType 0 =*/16).sint32(message.sy);
                            if (message.dx != null && message.hasOwnProperty("dx"))
                                writer.uint32(/* id 3, wireType 0 =*/24).sint32(message.dx);
                            if (message.dy != null && message.hasOwnProperty("dy"))
                                writer.uint32(/* id 4, wireType 0 =*/32).sint32(message.dy);
                            if (message.width != null && message.hasOwnProperty("width"))
                                writer.uint32(/* id 5, wireType 0 =*/40).uint32(message.width);
                            if (message.height != null && message.hasOwnProperty("height"))
                                writer.uint32(/* id 6, wireType 0 =*/48).uint32(message.height);
                            return writer;
                        };

                        /**
                         * Decodes a WindowMoveActionMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.WindowMoveActionMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.WindowMoveActionMsgProto} WindowMoveActionMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        WindowMoveActionMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.WindowMoveActionMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.sx = reader.sint32();
                                    break;
                                case 2:
                                    message.sy = reader.sint32();
                                    break;
                                case 3:
                                    message.dx = reader.sint32();
                                    break;
                                case 4:
                                    message.dy = reader.sint32();
                                    break;
                                case 5:
                                    message.width = reader.uint32();
                                    break;
                                case 6:
                                    message.height = reader.uint32();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a WindowMoveActionMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.WindowMoveActionMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.WindowMoveActionMsgProto} WindowMoveActionMsgProto
                         */
                        WindowMoveActionMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.WindowMoveActionMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.WindowMoveActionMsgProto();
                            if (object.sx != null)
                                message.sx = object.sx | 0;
                            if (object.sy != null)
                                message.sy = object.sy | 0;
                            if (object.dx != null)
                                message.dx = object.dx | 0;
                            if (object.dy != null)
                                message.dy = object.dy | 0;
                            if (object.width != null)
                                message.width = object.width >>> 0;
                            if (object.height != null)
                                message.height = object.height >>> 0;
                            return message;
                        };

                        /**
                         * Creates a plain object from a WindowMoveActionMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.WindowMoveActionMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.WindowMoveActionMsgProto} message WindowMoveActionMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        WindowMoveActionMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.sx = 0;
                                object.sy = 0;
                                object.dx = 0;
                                object.dy = 0;
                                object.width = 0;
                                object.height = 0;
                            }
                            if (message.sx != null && message.hasOwnProperty("sx"))
                                object.sx = message.sx;
                            if (message.sy != null && message.hasOwnProperty("sy"))
                                object.sy = message.sy;
                            if (message.dx != null && message.hasOwnProperty("dx"))
                                object.dx = message.dx;
                            if (message.dy != null && message.hasOwnProperty("dy"))
                                object.dy = message.dy;
                            if (message.width != null && message.hasOwnProperty("width"))
                                object.width = message.width;
                            if (message.height != null && message.hasOwnProperty("height"))
                                object.height = message.height;
                            return object;
                        };

                        /**
                         * Converts this WindowMoveActionMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.WindowMoveActionMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        WindowMoveActionMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return WindowMoveActionMsgProto;
                    })();

                    proto.CursorChangeEventMsgProto = (function() {

                        /**
                         * Properties of a CursorChangeEventMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface ICursorChangeEventMsgProto
                         * @property {string} cursor CursorChangeEventMsgProto cursor
                         * @property {Uint8Array|null} [b64img] CursorChangeEventMsgProto b64img
                         * @property {number|null} [x] CursorChangeEventMsgProto x
                         * @property {number|null} [y] CursorChangeEventMsgProto y
                         * @property {string|null} [curFile] CursorChangeEventMsgProto curFile
                         * @property {string|null} [winId] CursorChangeEventMsgProto winId
                         */

                        /**
                         * Constructs a new CursorChangeEventMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a CursorChangeEventMsgProto.
                         * @implements ICursorChangeEventMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.ICursorChangeEventMsgProto=} [properties] Properties to set
                         */
                        function CursorChangeEventMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * CursorChangeEventMsgProto cursor.
                         * @member {string} cursor
                         * @memberof org.webswing.server.model.proto.CursorChangeEventMsgProto
                         * @instance
                         */
                        CursorChangeEventMsgProto.prototype.cursor = "";

                        /**
                         * CursorChangeEventMsgProto b64img.
                         * @member {Uint8Array} b64img
                         * @memberof org.webswing.server.model.proto.CursorChangeEventMsgProto
                         * @instance
                         */
                        CursorChangeEventMsgProto.prototype.b64img = $util.newBuffer([]);

                        /**
                         * CursorChangeEventMsgProto x.
                         * @member {number} x
                         * @memberof org.webswing.server.model.proto.CursorChangeEventMsgProto
                         * @instance
                         */
                        CursorChangeEventMsgProto.prototype.x = 0;

                        /**
                         * CursorChangeEventMsgProto y.
                         * @member {number} y
                         * @memberof org.webswing.server.model.proto.CursorChangeEventMsgProto
                         * @instance
                         */
                        CursorChangeEventMsgProto.prototype.y = 0;

                        /**
                         * CursorChangeEventMsgProto curFile.
                         * @member {string} curFile
                         * @memberof org.webswing.server.model.proto.CursorChangeEventMsgProto
                         * @instance
                         */
                        CursorChangeEventMsgProto.prototype.curFile = "";

                        /**
                         * CursorChangeEventMsgProto winId.
                         * @member {string} winId
                         * @memberof org.webswing.server.model.proto.CursorChangeEventMsgProto
                         * @instance
                         */
                        CursorChangeEventMsgProto.prototype.winId = "";

                        /**
                         * Creates a new CursorChangeEventMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.CursorChangeEventMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.ICursorChangeEventMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.CursorChangeEventMsgProto} CursorChangeEventMsgProto instance
                         */
                        CursorChangeEventMsgProto.create = function create(properties) {
                            return new CursorChangeEventMsgProto(properties);
                        };

                        /**
                         * Encodes the specified CursorChangeEventMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.CursorChangeEventMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.CursorChangeEventMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.ICursorChangeEventMsgProto} message CursorChangeEventMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        CursorChangeEventMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            writer.uint32(/* id 1, wireType 2 =*/10).string(message.cursor);
                            if (message.b64img != null && message.hasOwnProperty("b64img"))
                                writer.uint32(/* id 2, wireType 2 =*/18).bytes(message.b64img);
                            if (message.x != null && message.hasOwnProperty("x"))
                                writer.uint32(/* id 3, wireType 0 =*/24).sint32(message.x);
                            if (message.y != null && message.hasOwnProperty("y"))
                                writer.uint32(/* id 4, wireType 0 =*/32).sint32(message.y);
                            if (message.curFile != null && message.hasOwnProperty("curFile"))
                                writer.uint32(/* id 5, wireType 2 =*/42).string(message.curFile);
                            if (message.winId != null && message.hasOwnProperty("winId"))
                                writer.uint32(/* id 6, wireType 2 =*/50).string(message.winId);
                            return writer;
                        };

                        /**
                         * Decodes a CursorChangeEventMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.CursorChangeEventMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.CursorChangeEventMsgProto} CursorChangeEventMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        CursorChangeEventMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.CursorChangeEventMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.cursor = reader.string();
                                    break;
                                case 2:
                                    message.b64img = reader.bytes();
                                    break;
                                case 3:
                                    message.x = reader.sint32();
                                    break;
                                case 4:
                                    message.y = reader.sint32();
                                    break;
                                case 5:
                                    message.curFile = reader.string();
                                    break;
                                case 6:
                                    message.winId = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            if (!message.hasOwnProperty("cursor"))
                                throw $util.ProtocolError("missing required 'cursor'", { instance: message });
                            return message;
                        };

                        /**
                         * Creates a CursorChangeEventMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.CursorChangeEventMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.CursorChangeEventMsgProto} CursorChangeEventMsgProto
                         */
                        CursorChangeEventMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.CursorChangeEventMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.CursorChangeEventMsgProto();
                            if (object.cursor != null)
                                message.cursor = String(object.cursor);
                            if (object.b64img != null)
                                if (typeof object.b64img === "string")
                                    $util.base64.decode(object.b64img, message.b64img = $util.newBuffer($util.base64.length(object.b64img)), 0);
                                else if (object.b64img.length)
                                    message.b64img = object.b64img;
                            if (object.x != null)
                                message.x = object.x | 0;
                            if (object.y != null)
                                message.y = object.y | 0;
                            if (object.curFile != null)
                                message.curFile = String(object.curFile);
                            if (object.winId != null)
                                message.winId = String(object.winId);
                            return message;
                        };

                        /**
                         * Creates a plain object from a CursorChangeEventMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.CursorChangeEventMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.CursorChangeEventMsgProto} message CursorChangeEventMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        CursorChangeEventMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.cursor = "";
                                if (options.bytes === String)
                                    object.b64img = "";
                                else {
                                    object.b64img = [];
                                    if (options.bytes !== Array)
                                        object.b64img = $util.newBuffer(object.b64img);
                                }
                                object.x = 0;
                                object.y = 0;
                                object.curFile = "";
                                object.winId = "";
                            }
                            if (message.cursor != null && message.hasOwnProperty("cursor"))
                                object.cursor = message.cursor;
                            if (message.b64img != null && message.hasOwnProperty("b64img"))
                                object.b64img = options.bytes === String ? $util.base64.encode(message.b64img, 0, message.b64img.length) : options.bytes === Array ? Array.prototype.slice.call(message.b64img) : message.b64img;
                            if (message.x != null && message.hasOwnProperty("x"))
                                object.x = message.x;
                            if (message.y != null && message.hasOwnProperty("y"))
                                object.y = message.y;
                            if (message.curFile != null && message.hasOwnProperty("curFile"))
                                object.curFile = message.curFile;
                            if (message.winId != null && message.hasOwnProperty("winId"))
                                object.winId = message.winId;
                            return object;
                        };

                        /**
                         * Converts this CursorChangeEventMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.CursorChangeEventMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        CursorChangeEventMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return CursorChangeEventMsgProto;
                    })();

                    proto.CopyEventMsgProto = (function() {

                        /**
                         * Properties of a CopyEventMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface ICopyEventMsgProto
                         * @property {string|null} [text] CopyEventMsgProto text
                         * @property {string|null} [html] CopyEventMsgProto html
                         * @property {Uint8Array|null} [img] CopyEventMsgProto img
                         * @property {Array.<string>|null} [files] CopyEventMsgProto files
                         * @property {boolean|null} [other] CopyEventMsgProto other
                         */

                        /**
                         * Constructs a new CopyEventMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a CopyEventMsgProto.
                         * @implements ICopyEventMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.ICopyEventMsgProto=} [properties] Properties to set
                         */
                        function CopyEventMsgProto(properties) {
                            this.files = [];
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * CopyEventMsgProto text.
                         * @member {string} text
                         * @memberof org.webswing.server.model.proto.CopyEventMsgProto
                         * @instance
                         */
                        CopyEventMsgProto.prototype.text = "";

                        /**
                         * CopyEventMsgProto html.
                         * @member {string} html
                         * @memberof org.webswing.server.model.proto.CopyEventMsgProto
                         * @instance
                         */
                        CopyEventMsgProto.prototype.html = "";

                        /**
                         * CopyEventMsgProto img.
                         * @member {Uint8Array} img
                         * @memberof org.webswing.server.model.proto.CopyEventMsgProto
                         * @instance
                         */
                        CopyEventMsgProto.prototype.img = $util.newBuffer([]);

                        /**
                         * CopyEventMsgProto files.
                         * @member {Array.<string>} files
                         * @memberof org.webswing.server.model.proto.CopyEventMsgProto
                         * @instance
                         */
                        CopyEventMsgProto.prototype.files = $util.emptyArray;

                        /**
                         * CopyEventMsgProto other.
                         * @member {boolean} other
                         * @memberof org.webswing.server.model.proto.CopyEventMsgProto
                         * @instance
                         */
                        CopyEventMsgProto.prototype.other = false;

                        /**
                         * Creates a new CopyEventMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.CopyEventMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.ICopyEventMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.CopyEventMsgProto} CopyEventMsgProto instance
                         */
                        CopyEventMsgProto.create = function create(properties) {
                            return new CopyEventMsgProto(properties);
                        };

                        /**
                         * Encodes the specified CopyEventMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.CopyEventMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.CopyEventMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.ICopyEventMsgProto} message CopyEventMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        CopyEventMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.text != null && message.hasOwnProperty("text"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.text);
                            if (message.html != null && message.hasOwnProperty("html"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.html);
                            if (message.img != null && message.hasOwnProperty("img"))
                                writer.uint32(/* id 3, wireType 2 =*/26).bytes(message.img);
                            if (message.files != null && message.files.length)
                                for (var i = 0; i < message.files.length; ++i)
                                    writer.uint32(/* id 4, wireType 2 =*/34).string(message.files[i]);
                            if (message.other != null && message.hasOwnProperty("other"))
                                writer.uint32(/* id 5, wireType 0 =*/40).bool(message.other);
                            return writer;
                        };

                        /**
                         * Decodes a CopyEventMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.CopyEventMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.CopyEventMsgProto} CopyEventMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        CopyEventMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.CopyEventMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.text = reader.string();
                                    break;
                                case 2:
                                    message.html = reader.string();
                                    break;
                                case 3:
                                    message.img = reader.bytes();
                                    break;
                                case 4:
                                    if (!(message.files && message.files.length))
                                        message.files = [];
                                    message.files.push(reader.string());
                                    break;
                                case 5:
                                    message.other = reader.bool();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a CopyEventMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.CopyEventMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.CopyEventMsgProto} CopyEventMsgProto
                         */
                        CopyEventMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.CopyEventMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.CopyEventMsgProto();
                            if (object.text != null)
                                message.text = String(object.text);
                            if (object.html != null)
                                message.html = String(object.html);
                            if (object.img != null)
                                if (typeof object.img === "string")
                                    $util.base64.decode(object.img, message.img = $util.newBuffer($util.base64.length(object.img)), 0);
                                else if (object.img.length)
                                    message.img = object.img;
                            if (object.files) {
                                if (!Array.isArray(object.files))
                                    throw TypeError(".org.webswing.server.model.proto.CopyEventMsgProto.files: array expected");
                                message.files = [];
                                for (var i = 0; i < object.files.length; ++i)
                                    message.files[i] = String(object.files[i]);
                            }
                            if (object.other != null)
                                message.other = Boolean(object.other);
                            return message;
                        };

                        /**
                         * Creates a plain object from a CopyEventMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.CopyEventMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.CopyEventMsgProto} message CopyEventMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        CopyEventMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.arrays || options.defaults)
                                object.files = [];
                            if (options.defaults) {
                                object.text = "";
                                object.html = "";
                                if (options.bytes === String)
                                    object.img = "";
                                else {
                                    object.img = [];
                                    if (options.bytes !== Array)
                                        object.img = $util.newBuffer(object.img);
                                }
                                object.other = false;
                            }
                            if (message.text != null && message.hasOwnProperty("text"))
                                object.text = message.text;
                            if (message.html != null && message.hasOwnProperty("html"))
                                object.html = message.html;
                            if (message.img != null && message.hasOwnProperty("img"))
                                object.img = options.bytes === String ? $util.base64.encode(message.img, 0, message.img.length) : options.bytes === Array ? Array.prototype.slice.call(message.img) : message.img;
                            if (message.files && message.files.length) {
                                object.files = [];
                                for (var j = 0; j < message.files.length; ++j)
                                    object.files[j] = message.files[j];
                            }
                            if (message.other != null && message.hasOwnProperty("other"))
                                object.other = message.other;
                            return object;
                        };

                        /**
                         * Converts this CopyEventMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.CopyEventMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        CopyEventMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return CopyEventMsgProto;
                    })();

                    proto.PasteRequestMsgProto = (function() {

                        /**
                         * Properties of a PasteRequestMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IPasteRequestMsgProto
                         * @property {string|null} [title] PasteRequestMsgProto title
                         * @property {string|null} [message] PasteRequestMsgProto message
                         */

                        /**
                         * Constructs a new PasteRequestMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a PasteRequestMsgProto.
                         * @implements IPasteRequestMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IPasteRequestMsgProto=} [properties] Properties to set
                         */
                        function PasteRequestMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * PasteRequestMsgProto title.
                         * @member {string} title
                         * @memberof org.webswing.server.model.proto.PasteRequestMsgProto
                         * @instance
                         */
                        PasteRequestMsgProto.prototype.title = "";

                        /**
                         * PasteRequestMsgProto message.
                         * @member {string} message
                         * @memberof org.webswing.server.model.proto.PasteRequestMsgProto
                         * @instance
                         */
                        PasteRequestMsgProto.prototype.message = "";

                        /**
                         * Creates a new PasteRequestMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.PasteRequestMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IPasteRequestMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.PasteRequestMsgProto} PasteRequestMsgProto instance
                         */
                        PasteRequestMsgProto.create = function create(properties) {
                            return new PasteRequestMsgProto(properties);
                        };

                        /**
                         * Encodes the specified PasteRequestMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.PasteRequestMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.PasteRequestMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IPasteRequestMsgProto} message PasteRequestMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        PasteRequestMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.title != null && message.hasOwnProperty("title"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.title);
                            if (message.message != null && message.hasOwnProperty("message"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.message);
                            return writer;
                        };

                        /**
                         * Decodes a PasteRequestMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.PasteRequestMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.PasteRequestMsgProto} PasteRequestMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        PasteRequestMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.PasteRequestMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.title = reader.string();
                                    break;
                                case 2:
                                    message.message = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a PasteRequestMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.PasteRequestMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.PasteRequestMsgProto} PasteRequestMsgProto
                         */
                        PasteRequestMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.PasteRequestMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.PasteRequestMsgProto();
                            if (object.title != null)
                                message.title = String(object.title);
                            if (object.message != null)
                                message.message = String(object.message);
                            return message;
                        };

                        /**
                         * Creates a plain object from a PasteRequestMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.PasteRequestMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.PasteRequestMsgProto} message PasteRequestMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        PasteRequestMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.title = "";
                                object.message = "";
                            }
                            if (message.title != null && message.hasOwnProperty("title"))
                                object.title = message.title;
                            if (message.message != null && message.hasOwnProperty("message"))
                                object.message = message.message;
                            return object;
                        };

                        /**
                         * Converts this PasteRequestMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.PasteRequestMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        PasteRequestMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return PasteRequestMsgProto;
                    })();

                    proto.FileDialogEventMsgProto = (function() {

                        /**
                         * Properties of a FileDialogEventMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IFileDialogEventMsgProto
                         * @property {org.webswing.server.model.proto.FileDialogEventMsgProto.FileDialogEventTypeProto} eventType FileDialogEventMsgProto eventType
                         * @property {boolean|null} [allowDownload] FileDialogEventMsgProto allowDownload
                         * @property {boolean|null} [allowUpload] FileDialogEventMsgProto allowUpload
                         * @property {boolean|null} [allowDelete] FileDialogEventMsgProto allowDelete
                         * @property {string|null} [filter] FileDialogEventMsgProto filter
                         * @property {boolean|null} [isMultiSelection] FileDialogEventMsgProto isMultiSelection
                         * @property {string|null} [selection] FileDialogEventMsgProto selection
                         */

                        /**
                         * Constructs a new FileDialogEventMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a FileDialogEventMsgProto.
                         * @implements IFileDialogEventMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IFileDialogEventMsgProto=} [properties] Properties to set
                         */
                        function FileDialogEventMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * FileDialogEventMsgProto eventType.
                         * @member {org.webswing.server.model.proto.FileDialogEventMsgProto.FileDialogEventTypeProto} eventType
                         * @memberof org.webswing.server.model.proto.FileDialogEventMsgProto
                         * @instance
                         */
                        FileDialogEventMsgProto.prototype.eventType = 0;

                        /**
                         * FileDialogEventMsgProto allowDownload.
                         * @member {boolean} allowDownload
                         * @memberof org.webswing.server.model.proto.FileDialogEventMsgProto
                         * @instance
                         */
                        FileDialogEventMsgProto.prototype.allowDownload = false;

                        /**
                         * FileDialogEventMsgProto allowUpload.
                         * @member {boolean} allowUpload
                         * @memberof org.webswing.server.model.proto.FileDialogEventMsgProto
                         * @instance
                         */
                        FileDialogEventMsgProto.prototype.allowUpload = false;

                        /**
                         * FileDialogEventMsgProto allowDelete.
                         * @member {boolean} allowDelete
                         * @memberof org.webswing.server.model.proto.FileDialogEventMsgProto
                         * @instance
                         */
                        FileDialogEventMsgProto.prototype.allowDelete = false;

                        /**
                         * FileDialogEventMsgProto filter.
                         * @member {string} filter
                         * @memberof org.webswing.server.model.proto.FileDialogEventMsgProto
                         * @instance
                         */
                        FileDialogEventMsgProto.prototype.filter = "";

                        /**
                         * FileDialogEventMsgProto isMultiSelection.
                         * @member {boolean} isMultiSelection
                         * @memberof org.webswing.server.model.proto.FileDialogEventMsgProto
                         * @instance
                         */
                        FileDialogEventMsgProto.prototype.isMultiSelection = false;

                        /**
                         * FileDialogEventMsgProto selection.
                         * @member {string} selection
                         * @memberof org.webswing.server.model.proto.FileDialogEventMsgProto
                         * @instance
                         */
                        FileDialogEventMsgProto.prototype.selection = "";

                        /**
                         * Creates a new FileDialogEventMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.FileDialogEventMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IFileDialogEventMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.FileDialogEventMsgProto} FileDialogEventMsgProto instance
                         */
                        FileDialogEventMsgProto.create = function create(properties) {
                            return new FileDialogEventMsgProto(properties);
                        };

                        /**
                         * Encodes the specified FileDialogEventMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.FileDialogEventMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.FileDialogEventMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IFileDialogEventMsgProto} message FileDialogEventMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        FileDialogEventMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            writer.uint32(/* id 1, wireType 0 =*/8).int32(message.eventType);
                            if (message.allowDownload != null && message.hasOwnProperty("allowDownload"))
                                writer.uint32(/* id 2, wireType 0 =*/16).bool(message.allowDownload);
                            if (message.allowUpload != null && message.hasOwnProperty("allowUpload"))
                                writer.uint32(/* id 3, wireType 0 =*/24).bool(message.allowUpload);
                            if (message.allowDelete != null && message.hasOwnProperty("allowDelete"))
                                writer.uint32(/* id 4, wireType 0 =*/32).bool(message.allowDelete);
                            if (message.filter != null && message.hasOwnProperty("filter"))
                                writer.uint32(/* id 5, wireType 2 =*/42).string(message.filter);
                            if (message.isMultiSelection != null && message.hasOwnProperty("isMultiSelection"))
                                writer.uint32(/* id 6, wireType 0 =*/48).bool(message.isMultiSelection);
                            if (message.selection != null && message.hasOwnProperty("selection"))
                                writer.uint32(/* id 7, wireType 2 =*/58).string(message.selection);
                            return writer;
                        };

                        /**
                         * Decodes a FileDialogEventMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.FileDialogEventMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.FileDialogEventMsgProto} FileDialogEventMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        FileDialogEventMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.FileDialogEventMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.eventType = reader.int32();
                                    break;
                                case 2:
                                    message.allowDownload = reader.bool();
                                    break;
                                case 3:
                                    message.allowUpload = reader.bool();
                                    break;
                                case 4:
                                    message.allowDelete = reader.bool();
                                    break;
                                case 5:
                                    message.filter = reader.string();
                                    break;
                                case 6:
                                    message.isMultiSelection = reader.bool();
                                    break;
                                case 7:
                                    message.selection = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            if (!message.hasOwnProperty("eventType"))
                                throw $util.ProtocolError("missing required 'eventType'", { instance: message });
                            return message;
                        };

                        /**
                         * Creates a FileDialogEventMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.FileDialogEventMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.FileDialogEventMsgProto} FileDialogEventMsgProto
                         */
                        FileDialogEventMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.FileDialogEventMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.FileDialogEventMsgProto();
                            switch (object.eventType) {
                            case "Open":
                            case 0:
                                message.eventType = 0;
                                break;
                            case "Close":
                            case 1:
                                message.eventType = 1;
                                break;
                            case "AutoUpload":
                            case 2:
                                message.eventType = 2;
                                break;
                            case "AutoSave":
                            case 3:
                                message.eventType = 3;
                                break;
                            }
                            if (object.allowDownload != null)
                                message.allowDownload = Boolean(object.allowDownload);
                            if (object.allowUpload != null)
                                message.allowUpload = Boolean(object.allowUpload);
                            if (object.allowDelete != null)
                                message.allowDelete = Boolean(object.allowDelete);
                            if (object.filter != null)
                                message.filter = String(object.filter);
                            if (object.isMultiSelection != null)
                                message.isMultiSelection = Boolean(object.isMultiSelection);
                            if (object.selection != null)
                                message.selection = String(object.selection);
                            return message;
                        };

                        /**
                         * Creates a plain object from a FileDialogEventMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.FileDialogEventMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.FileDialogEventMsgProto} message FileDialogEventMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        FileDialogEventMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.eventType = options.enums === String ? "Open" : 0;
                                object.allowDownload = false;
                                object.allowUpload = false;
                                object.allowDelete = false;
                                object.filter = "";
                                object.isMultiSelection = false;
                                object.selection = "";
                            }
                            if (message.eventType != null && message.hasOwnProperty("eventType"))
                                object.eventType = options.enums === String ? $root.org.webswing.server.model.proto.FileDialogEventMsgProto.FileDialogEventTypeProto[message.eventType] : message.eventType;
                            if (message.allowDownload != null && message.hasOwnProperty("allowDownload"))
                                object.allowDownload = message.allowDownload;
                            if (message.allowUpload != null && message.hasOwnProperty("allowUpload"))
                                object.allowUpload = message.allowUpload;
                            if (message.allowDelete != null && message.hasOwnProperty("allowDelete"))
                                object.allowDelete = message.allowDelete;
                            if (message.filter != null && message.hasOwnProperty("filter"))
                                object.filter = message.filter;
                            if (message.isMultiSelection != null && message.hasOwnProperty("isMultiSelection"))
                                object.isMultiSelection = message.isMultiSelection;
                            if (message.selection != null && message.hasOwnProperty("selection"))
                                object.selection = message.selection;
                            return object;
                        };

                        /**
                         * Converts this FileDialogEventMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.FileDialogEventMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        FileDialogEventMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        /**
                         * FileDialogEventTypeProto enum.
                         * @name org.webswing.server.model.proto.FileDialogEventMsgProto.FileDialogEventTypeProto
                         * @enum {string}
                         * @property {number} Open=0 Open value
                         * @property {number} Close=1 Close value
                         * @property {number} AutoUpload=2 AutoUpload value
                         * @property {number} AutoSave=3 AutoSave value
                         */
                        FileDialogEventMsgProto.FileDialogEventTypeProto = (function() {
                            var valuesById = {}, values = Object.create(valuesById);
                            values[valuesById[0] = "Open"] = 0;
                            values[valuesById[1] = "Close"] = 1;
                            values[valuesById[2] = "AutoUpload"] = 2;
                            values[valuesById[3] = "AutoSave"] = 3;
                            return values;
                        })();

                        return FileDialogEventMsgProto;
                    })();

                    proto.WindowSwitchMsg = (function() {

                        /**
                         * Properties of a WindowSwitchMsg.
                         * @memberof org.webswing.server.model.proto
                         * @interface IWindowSwitchMsg
                         * @property {string} id WindowSwitchMsg id
                         * @property {string|null} [title] WindowSwitchMsg title
                         * @property {boolean|null} [modalBlocked] WindowSwitchMsg modalBlocked
                         */

                        /**
                         * Constructs a new WindowSwitchMsg.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a WindowSwitchMsg.
                         * @implements IWindowSwitchMsg
                         * @constructor
                         * @param {org.webswing.server.model.proto.IWindowSwitchMsg=} [properties] Properties to set
                         */
                        function WindowSwitchMsg(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * WindowSwitchMsg id.
                         * @member {string} id
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsg
                         * @instance
                         */
                        WindowSwitchMsg.prototype.id = "";

                        /**
                         * WindowSwitchMsg title.
                         * @member {string} title
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsg
                         * @instance
                         */
                        WindowSwitchMsg.prototype.title = "";

                        /**
                         * WindowSwitchMsg modalBlocked.
                         * @member {boolean} modalBlocked
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsg
                         * @instance
                         */
                        WindowSwitchMsg.prototype.modalBlocked = false;

                        /**
                         * Creates a new WindowSwitchMsg instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsg
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowSwitchMsg=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.WindowSwitchMsg} WindowSwitchMsg instance
                         */
                        WindowSwitchMsg.create = function create(properties) {
                            return new WindowSwitchMsg(properties);
                        };

                        /**
                         * Encodes the specified WindowSwitchMsg message. Does not implicitly {@link org.webswing.server.model.proto.WindowSwitchMsg.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsg
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowSwitchMsg} message WindowSwitchMsg message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        WindowSwitchMsg.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
                            if (message.title != null && message.hasOwnProperty("title"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.title);
                            if (message.modalBlocked != null && message.hasOwnProperty("modalBlocked"))
                                writer.uint32(/* id 3, wireType 0 =*/24).bool(message.modalBlocked);
                            return writer;
                        };

                        /**
                         * Decodes a WindowSwitchMsg message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsg
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.WindowSwitchMsg} WindowSwitchMsg
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        WindowSwitchMsg.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.WindowSwitchMsg();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.id = reader.string();
                                    break;
                                case 2:
                                    message.title = reader.string();
                                    break;
                                case 3:
                                    message.modalBlocked = reader.bool();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            if (!message.hasOwnProperty("id"))
                                throw $util.ProtocolError("missing required 'id'", { instance: message });
                            return message;
                        };

                        /**
                         * Creates a WindowSwitchMsg message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsg
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.WindowSwitchMsg} WindowSwitchMsg
                         */
                        WindowSwitchMsg.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.WindowSwitchMsg)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.WindowSwitchMsg();
                            if (object.id != null)
                                message.id = String(object.id);
                            if (object.title != null)
                                message.title = String(object.title);
                            if (object.modalBlocked != null)
                                message.modalBlocked = Boolean(object.modalBlocked);
                            return message;
                        };

                        /**
                         * Creates a plain object from a WindowSwitchMsg message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsg
                         * @static
                         * @param {org.webswing.server.model.proto.WindowSwitchMsg} message WindowSwitchMsg
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        WindowSwitchMsg.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.id = "";
                                object.title = "";
                                object.modalBlocked = false;
                            }
                            if (message.id != null && message.hasOwnProperty("id"))
                                object.id = message.id;
                            if (message.title != null && message.hasOwnProperty("title"))
                                object.title = message.title;
                            if (message.modalBlocked != null && message.hasOwnProperty("modalBlocked"))
                                object.modalBlocked = message.modalBlocked;
                            return object;
                        };

                        /**
                         * Converts this WindowSwitchMsg to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsg
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        WindowSwitchMsg.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return WindowSwitchMsg;
                    })();

                    proto.WindowMsgProto = (function() {

                        /**
                         * Properties of a WindowMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IWindowMsgProto
                         * @property {string} id WindowMsgProto id
                         * @property {Array.<org.webswing.server.model.proto.IWindowPartialContentMsgProto>|null} [content] WindowMsgProto content
                         * @property {Uint8Array|null} [directDraw] WindowMsgProto directDraw
                         * @property {string|null} [title] WindowMsgProto title
                         * @property {number|null} [posX] WindowMsgProto posX
                         * @property {number|null} [posY] WindowMsgProto posY
                         * @property {number|null} [width] WindowMsgProto width
                         * @property {number|null} [height] WindowMsgProto height
                         * @property {string|null} [name] WindowMsgProto name
                         * @property {org.webswing.server.model.proto.WindowMsgProto.WindowTypeProto|null} [type] WindowMsgProto type
                         * @property {boolean|null} [modalBlocked] WindowMsgProto modalBlocked
                         * @property {string|null} [ownerId] WindowMsgProto ownerId
                         * @property {number|null} [state] WindowMsgProto state
                         * @property {Array.<org.webswing.server.model.proto.IWindowMsgProto>|null} [internalWindows] WindowMsgProto internalWindows
                         * @property {org.webswing.server.model.proto.WindowMsgProto.DockModeProto|null} [dockMode] WindowMsgProto dockMode
                         */

                        /**
                         * Constructs a new WindowMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a WindowMsgProto.
                         * @implements IWindowMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IWindowMsgProto=} [properties] Properties to set
                         */
                        function WindowMsgProto(properties) {
                            this.content = [];
                            this.internalWindows = [];
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * WindowMsgProto id.
                         * @member {string} id
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.id = "";

                        /**
                         * WindowMsgProto content.
                         * @member {Array.<org.webswing.server.model.proto.IWindowPartialContentMsgProto>} content
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.content = $util.emptyArray;

                        /**
                         * WindowMsgProto directDraw.
                         * @member {Uint8Array} directDraw
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.directDraw = $util.newBuffer([]);

                        /**
                         * WindowMsgProto title.
                         * @member {string} title
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.title = "";

                        /**
                         * WindowMsgProto posX.
                         * @member {number} posX
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.posX = 0;

                        /**
                         * WindowMsgProto posY.
                         * @member {number} posY
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.posY = 0;

                        /**
                         * WindowMsgProto width.
                         * @member {number} width
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.width = 0;

                        /**
                         * WindowMsgProto height.
                         * @member {number} height
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.height = 0;

                        /**
                         * WindowMsgProto name.
                         * @member {string} name
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.name = "";

                        /**
                         * WindowMsgProto type.
                         * @member {org.webswing.server.model.proto.WindowMsgProto.WindowTypeProto} type
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.type = 1;

                        /**
                         * WindowMsgProto modalBlocked.
                         * @member {boolean} modalBlocked
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.modalBlocked = false;

                        /**
                         * WindowMsgProto ownerId.
                         * @member {string} ownerId
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.ownerId = "";

                        /**
                         * WindowMsgProto state.
                         * @member {number} state
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.state = 0;

                        /**
                         * WindowMsgProto internalWindows.
                         * @member {Array.<org.webswing.server.model.proto.IWindowMsgProto>} internalWindows
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.internalWindows = $util.emptyArray;

                        /**
                         * WindowMsgProto dockMode.
                         * @member {org.webswing.server.model.proto.WindowMsgProto.DockModeProto} dockMode
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         */
                        WindowMsgProto.prototype.dockMode = 1;

                        /**
                         * Creates a new WindowMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.WindowMsgProto} WindowMsgProto instance
                         */
                        WindowMsgProto.create = function create(properties) {
                            return new WindowMsgProto(properties);
                        };

                        /**
                         * Encodes the specified WindowMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowMsgProto} message WindowMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        WindowMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
                            if (message.content != null && message.content.length)
                                for (var i = 0; i < message.content.length; ++i)
                                    $root.org.webswing.server.model.proto.WindowPartialContentMsgProto.encode(message.content[i], writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
                            if (message.directDraw != null && message.hasOwnProperty("directDraw"))
                                writer.uint32(/* id 3, wireType 2 =*/26).bytes(message.directDraw);
                            if (message.title != null && message.hasOwnProperty("title"))
                                writer.uint32(/* id 4, wireType 2 =*/34).string(message.title);
                            if (message.posX != null && message.hasOwnProperty("posX"))
                                writer.uint32(/* id 5, wireType 0 =*/40).sint32(message.posX);
                            if (message.posY != null && message.hasOwnProperty("posY"))
                                writer.uint32(/* id 6, wireType 0 =*/48).sint32(message.posY);
                            if (message.width != null && message.hasOwnProperty("width"))
                                writer.uint32(/* id 7, wireType 0 =*/56).uint32(message.width);
                            if (message.height != null && message.hasOwnProperty("height"))
                                writer.uint32(/* id 8, wireType 0 =*/64).uint32(message.height);
                            if (message.name != null && message.hasOwnProperty("name"))
                                writer.uint32(/* id 9, wireType 2 =*/74).string(message.name);
                            if (message.type != null && message.hasOwnProperty("type"))
                                writer.uint32(/* id 10, wireType 0 =*/80).int32(message.type);
                            if (message.modalBlocked != null && message.hasOwnProperty("modalBlocked"))
                                writer.uint32(/* id 11, wireType 0 =*/88).bool(message.modalBlocked);
                            if (message.ownerId != null && message.hasOwnProperty("ownerId"))
                                writer.uint32(/* id 12, wireType 2 =*/98).string(message.ownerId);
                            if (message.state != null && message.hasOwnProperty("state"))
                                writer.uint32(/* id 13, wireType 0 =*/104).uint32(message.state);
                            if (message.internalWindows != null && message.internalWindows.length)
                                for (var i = 0; i < message.internalWindows.length; ++i)
                                    $root.org.webswing.server.model.proto.WindowMsgProto.encode(message.internalWindows[i], writer.uint32(/* id 14, wireType 2 =*/114).fork()).ldelim();
                            if (message.dockMode != null && message.hasOwnProperty("dockMode"))
                                writer.uint32(/* id 15, wireType 0 =*/120).int32(message.dockMode);
                            return writer;
                        };

                        /**
                         * Decodes a WindowMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.WindowMsgProto} WindowMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        WindowMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.WindowMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.id = reader.string();
                                    break;
                                case 2:
                                    if (!(message.content && message.content.length))
                                        message.content = [];
                                    message.content.push($root.org.webswing.server.model.proto.WindowPartialContentMsgProto.decode(reader, reader.uint32()));
                                    break;
                                case 3:
                                    message.directDraw = reader.bytes();
                                    break;
                                case 4:
                                    message.title = reader.string();
                                    break;
                                case 5:
                                    message.posX = reader.sint32();
                                    break;
                                case 6:
                                    message.posY = reader.sint32();
                                    break;
                                case 7:
                                    message.width = reader.uint32();
                                    break;
                                case 8:
                                    message.height = reader.uint32();
                                    break;
                                case 9:
                                    message.name = reader.string();
                                    break;
                                case 10:
                                    message.type = reader.int32();
                                    break;
                                case 11:
                                    message.modalBlocked = reader.bool();
                                    break;
                                case 12:
                                    message.ownerId = reader.string();
                                    break;
                                case 13:
                                    message.state = reader.uint32();
                                    break;
                                case 14:
                                    if (!(message.internalWindows && message.internalWindows.length))
                                        message.internalWindows = [];
                                    message.internalWindows.push($root.org.webswing.server.model.proto.WindowMsgProto.decode(reader, reader.uint32()));
                                    break;
                                case 15:
                                    message.dockMode = reader.int32();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            if (!message.hasOwnProperty("id"))
                                throw $util.ProtocolError("missing required 'id'", { instance: message });
                            return message;
                        };

                        /**
                         * Creates a WindowMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.WindowMsgProto} WindowMsgProto
                         */
                        WindowMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.WindowMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.WindowMsgProto();
                            if (object.id != null)
                                message.id = String(object.id);
                            if (object.content) {
                                if (!Array.isArray(object.content))
                                    throw TypeError(".org.webswing.server.model.proto.WindowMsgProto.content: array expected");
                                message.content = [];
                                for (var i = 0; i < object.content.length; ++i) {
                                    if (typeof object.content[i] !== "object")
                                        throw TypeError(".org.webswing.server.model.proto.WindowMsgProto.content: object expected");
                                    message.content[i] = $root.org.webswing.server.model.proto.WindowPartialContentMsgProto.fromObject(object.content[i]);
                                }
                            }
                            if (object.directDraw != null)
                                if (typeof object.directDraw === "string")
                                    $util.base64.decode(object.directDraw, message.directDraw = $util.newBuffer($util.base64.length(object.directDraw)), 0);
                                else if (object.directDraw.length)
                                    message.directDraw = object.directDraw;
                            if (object.title != null)
                                message.title = String(object.title);
                            if (object.posX != null)
                                message.posX = object.posX | 0;
                            if (object.posY != null)
                                message.posY = object.posY | 0;
                            if (object.width != null)
                                message.width = object.width >>> 0;
                            if (object.height != null)
                                message.height = object.height >>> 0;
                            if (object.name != null)
                                message.name = String(object.name);
                            switch (object.type) {
                            case "basic":
                            case 1:
                                message.type = 1;
                                break;
                            case "html":
                            case 2:
                                message.type = 2;
                                break;
                            case "internal":
                            case 3:
                                message.type = 3;
                                break;
                            case "internalHtml":
                            case 4:
                                message.type = 4;
                                break;
                            case "internalWrapper":
                            case 5:
                                message.type = 5;
                                break;
                            }
                            if (object.modalBlocked != null)
                                message.modalBlocked = Boolean(object.modalBlocked);
                            if (object.ownerId != null)
                                message.ownerId = String(object.ownerId);
                            if (object.state != null)
                                message.state = object.state >>> 0;
                            if (object.internalWindows) {
                                if (!Array.isArray(object.internalWindows))
                                    throw TypeError(".org.webswing.server.model.proto.WindowMsgProto.internalWindows: array expected");
                                message.internalWindows = [];
                                for (var i = 0; i < object.internalWindows.length; ++i) {
                                    if (typeof object.internalWindows[i] !== "object")
                                        throw TypeError(".org.webswing.server.model.proto.WindowMsgProto.internalWindows: object expected");
                                    message.internalWindows[i] = $root.org.webswing.server.model.proto.WindowMsgProto.fromObject(object.internalWindows[i]);
                                }
                            }
                            switch (object.dockMode) {
                            case "none":
                            case 1:
                                message.dockMode = 1;
                                break;
                            case "dockable":
                            case 2:
                                message.dockMode = 2;
                                break;
                            case "autoUndock":
                            case 3:
                                message.dockMode = 3;
                                break;
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from a WindowMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.WindowMsgProto} message WindowMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        WindowMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.arrays || options.defaults) {
                                object.content = [];
                                object.internalWindows = [];
                            }
                            if (options.defaults) {
                                object.id = "";
                                if (options.bytes === String)
                                    object.directDraw = "";
                                else {
                                    object.directDraw = [];
                                    if (options.bytes !== Array)
                                        object.directDraw = $util.newBuffer(object.directDraw);
                                }
                                object.title = "";
                                object.posX = 0;
                                object.posY = 0;
                                object.width = 0;
                                object.height = 0;
                                object.name = "";
                                object.type = options.enums === String ? "basic" : 1;
                                object.modalBlocked = false;
                                object.ownerId = "";
                                object.state = 0;
                                object.dockMode = options.enums === String ? "none" : 1;
                            }
                            if (message.id != null && message.hasOwnProperty("id"))
                                object.id = message.id;
                            if (message.content && message.content.length) {
                                object.content = [];
                                for (var j = 0; j < message.content.length; ++j)
                                    object.content[j] = $root.org.webswing.server.model.proto.WindowPartialContentMsgProto.toObject(message.content[j], options);
                            }
                            if (message.directDraw != null && message.hasOwnProperty("directDraw"))
                                object.directDraw = options.bytes === String ? $util.base64.encode(message.directDraw, 0, message.directDraw.length) : options.bytes === Array ? Array.prototype.slice.call(message.directDraw) : message.directDraw;
                            if (message.title != null && message.hasOwnProperty("title"))
                                object.title = message.title;
                            if (message.posX != null && message.hasOwnProperty("posX"))
                                object.posX = message.posX;
                            if (message.posY != null && message.hasOwnProperty("posY"))
                                object.posY = message.posY;
                            if (message.width != null && message.hasOwnProperty("width"))
                                object.width = message.width;
                            if (message.height != null && message.hasOwnProperty("height"))
                                object.height = message.height;
                            if (message.name != null && message.hasOwnProperty("name"))
                                object.name = message.name;
                            if (message.type != null && message.hasOwnProperty("type"))
                                object.type = options.enums === String ? $root.org.webswing.server.model.proto.WindowMsgProto.WindowTypeProto[message.type] : message.type;
                            if (message.modalBlocked != null && message.hasOwnProperty("modalBlocked"))
                                object.modalBlocked = message.modalBlocked;
                            if (message.ownerId != null && message.hasOwnProperty("ownerId"))
                                object.ownerId = message.ownerId;
                            if (message.state != null && message.hasOwnProperty("state"))
                                object.state = message.state;
                            if (message.internalWindows && message.internalWindows.length) {
                                object.internalWindows = [];
                                for (var j = 0; j < message.internalWindows.length; ++j)
                                    object.internalWindows[j] = $root.org.webswing.server.model.proto.WindowMsgProto.toObject(message.internalWindows[j], options);
                            }
                            if (message.dockMode != null && message.hasOwnProperty("dockMode"))
                                object.dockMode = options.enums === String ? $root.org.webswing.server.model.proto.WindowMsgProto.DockModeProto[message.dockMode] : message.dockMode;
                            return object;
                        };

                        /**
                         * Converts this WindowMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.WindowMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        WindowMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        /**
                         * WindowTypeProto enum.
                         * @name org.webswing.server.model.proto.WindowMsgProto.WindowTypeProto
                         * @enum {string}
                         * @property {number} basic=1 basic value
                         * @property {number} html=2 html value
                         * @property {number} internal=3 internal value
                         * @property {number} internalHtml=4 internalHtml value
                         * @property {number} internalWrapper=5 internalWrapper value
                         */
                        WindowMsgProto.WindowTypeProto = (function() {
                            var valuesById = {}, values = Object.create(valuesById);
                            values[valuesById[1] = "basic"] = 1;
                            values[valuesById[2] = "html"] = 2;
                            values[valuesById[3] = "internal"] = 3;
                            values[valuesById[4] = "internalHtml"] = 4;
                            values[valuesById[5] = "internalWrapper"] = 5;
                            return values;
                        })();

                        /**
                         * DockModeProto enum.
                         * @name org.webswing.server.model.proto.WindowMsgProto.DockModeProto
                         * @enum {string}
                         * @property {number} none=1 none value
                         * @property {number} dockable=2 dockable value
                         * @property {number} autoUndock=3 autoUndock value
                         */
                        WindowMsgProto.DockModeProto = (function() {
                            var valuesById = {}, values = Object.create(valuesById);
                            values[valuesById[1] = "none"] = 1;
                            values[valuesById[2] = "dockable"] = 2;
                            values[valuesById[3] = "autoUndock"] = 3;
                            return values;
                        })();

                        return WindowMsgProto;
                    })();

                    proto.WindowSwitchMsgProto = (function() {

                        /**
                         * Properties of a WindowSwitchMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IWindowSwitchMsgProto
                         * @property {string} id WindowSwitchMsgProto id
                         * @property {string|null} [title] WindowSwitchMsgProto title
                         * @property {boolean|null} [modalBlocked] WindowSwitchMsgProto modalBlocked
                         */

                        /**
                         * Constructs a new WindowSwitchMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a WindowSwitchMsgProto.
                         * @implements IWindowSwitchMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IWindowSwitchMsgProto=} [properties] Properties to set
                         */
                        function WindowSwitchMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * WindowSwitchMsgProto id.
                         * @member {string} id
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsgProto
                         * @instance
                         */
                        WindowSwitchMsgProto.prototype.id = "";

                        /**
                         * WindowSwitchMsgProto title.
                         * @member {string} title
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsgProto
                         * @instance
                         */
                        WindowSwitchMsgProto.prototype.title = "";

                        /**
                         * WindowSwitchMsgProto modalBlocked.
                         * @member {boolean} modalBlocked
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsgProto
                         * @instance
                         */
                        WindowSwitchMsgProto.prototype.modalBlocked = false;

                        /**
                         * Creates a new WindowSwitchMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowSwitchMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.WindowSwitchMsgProto} WindowSwitchMsgProto instance
                         */
                        WindowSwitchMsgProto.create = function create(properties) {
                            return new WindowSwitchMsgProto(properties);
                        };

                        /**
                         * Encodes the specified WindowSwitchMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowSwitchMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowSwitchMsgProto} message WindowSwitchMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        WindowSwitchMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
                            if (message.title != null && message.hasOwnProperty("title"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.title);
                            if (message.modalBlocked != null && message.hasOwnProperty("modalBlocked"))
                                writer.uint32(/* id 3, wireType 0 =*/24).bool(message.modalBlocked);
                            return writer;
                        };

                        /**
                         * Decodes a WindowSwitchMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.WindowSwitchMsgProto} WindowSwitchMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        WindowSwitchMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.WindowSwitchMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.id = reader.string();
                                    break;
                                case 2:
                                    message.title = reader.string();
                                    break;
                                case 3:
                                    message.modalBlocked = reader.bool();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            if (!message.hasOwnProperty("id"))
                                throw $util.ProtocolError("missing required 'id'", { instance: message });
                            return message;
                        };

                        /**
                         * Creates a WindowSwitchMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.WindowSwitchMsgProto} WindowSwitchMsgProto
                         */
                        WindowSwitchMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.WindowSwitchMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.WindowSwitchMsgProto();
                            if (object.id != null)
                                message.id = String(object.id);
                            if (object.title != null)
                                message.title = String(object.title);
                            if (object.modalBlocked != null)
                                message.modalBlocked = Boolean(object.modalBlocked);
                            return message;
                        };

                        /**
                         * Creates a plain object from a WindowSwitchMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.WindowSwitchMsgProto} message WindowSwitchMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        WindowSwitchMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.id = "";
                                object.title = "";
                                object.modalBlocked = false;
                            }
                            if (message.id != null && message.hasOwnProperty("id"))
                                object.id = message.id;
                            if (message.title != null && message.hasOwnProperty("title"))
                                object.title = message.title;
                            if (message.modalBlocked != null && message.hasOwnProperty("modalBlocked"))
                                object.modalBlocked = message.modalBlocked;
                            return object;
                        };

                        /**
                         * Converts this WindowSwitchMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.WindowSwitchMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        WindowSwitchMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return WindowSwitchMsgProto;
                    })();

                    proto.WindowPartialContentMsgProto = (function() {

                        /**
                         * Properties of a WindowPartialContentMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IWindowPartialContentMsgProto
                         * @property {number|null} [positionX] WindowPartialContentMsgProto positionX
                         * @property {number|null} [positionY] WindowPartialContentMsgProto positionY
                         * @property {number|null} [width] WindowPartialContentMsgProto width
                         * @property {number|null} [height] WindowPartialContentMsgProto height
                         * @property {Uint8Array|null} [base64Content] WindowPartialContentMsgProto base64Content
                         */

                        /**
                         * Constructs a new WindowPartialContentMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a WindowPartialContentMsgProto.
                         * @implements IWindowPartialContentMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IWindowPartialContentMsgProto=} [properties] Properties to set
                         */
                        function WindowPartialContentMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * WindowPartialContentMsgProto positionX.
                         * @member {number} positionX
                         * @memberof org.webswing.server.model.proto.WindowPartialContentMsgProto
                         * @instance
                         */
                        WindowPartialContentMsgProto.prototype.positionX = 0;

                        /**
                         * WindowPartialContentMsgProto positionY.
                         * @member {number} positionY
                         * @memberof org.webswing.server.model.proto.WindowPartialContentMsgProto
                         * @instance
                         */
                        WindowPartialContentMsgProto.prototype.positionY = 0;

                        /**
                         * WindowPartialContentMsgProto width.
                         * @member {number} width
                         * @memberof org.webswing.server.model.proto.WindowPartialContentMsgProto
                         * @instance
                         */
                        WindowPartialContentMsgProto.prototype.width = 0;

                        /**
                         * WindowPartialContentMsgProto height.
                         * @member {number} height
                         * @memberof org.webswing.server.model.proto.WindowPartialContentMsgProto
                         * @instance
                         */
                        WindowPartialContentMsgProto.prototype.height = 0;

                        /**
                         * WindowPartialContentMsgProto base64Content.
                         * @member {Uint8Array} base64Content
                         * @memberof org.webswing.server.model.proto.WindowPartialContentMsgProto
                         * @instance
                         */
                        WindowPartialContentMsgProto.prototype.base64Content = $util.newBuffer([]);

                        /**
                         * Creates a new WindowPartialContentMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.WindowPartialContentMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowPartialContentMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.WindowPartialContentMsgProto} WindowPartialContentMsgProto instance
                         */
                        WindowPartialContentMsgProto.create = function create(properties) {
                            return new WindowPartialContentMsgProto(properties);
                        };

                        /**
                         * Encodes the specified WindowPartialContentMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowPartialContentMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.WindowPartialContentMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowPartialContentMsgProto} message WindowPartialContentMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        WindowPartialContentMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.positionX != null && message.hasOwnProperty("positionX"))
                                writer.uint32(/* id 1, wireType 0 =*/8).sint32(message.positionX);
                            if (message.positionY != null && message.hasOwnProperty("positionY"))
                                writer.uint32(/* id 2, wireType 0 =*/16).sint32(message.positionY);
                            if (message.width != null && message.hasOwnProperty("width"))
                                writer.uint32(/* id 3, wireType 0 =*/24).uint32(message.width);
                            if (message.height != null && message.hasOwnProperty("height"))
                                writer.uint32(/* id 4, wireType 0 =*/32).uint32(message.height);
                            if (message.base64Content != null && message.hasOwnProperty("base64Content"))
                                writer.uint32(/* id 5, wireType 2 =*/42).bytes(message.base64Content);
                            return writer;
                        };

                        /**
                         * Decodes a WindowPartialContentMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.WindowPartialContentMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.WindowPartialContentMsgProto} WindowPartialContentMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        WindowPartialContentMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.WindowPartialContentMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.positionX = reader.sint32();
                                    break;
                                case 2:
                                    message.positionY = reader.sint32();
                                    break;
                                case 3:
                                    message.width = reader.uint32();
                                    break;
                                case 4:
                                    message.height = reader.uint32();
                                    break;
                                case 5:
                                    message.base64Content = reader.bytes();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a WindowPartialContentMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.WindowPartialContentMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.WindowPartialContentMsgProto} WindowPartialContentMsgProto
                         */
                        WindowPartialContentMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.WindowPartialContentMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.WindowPartialContentMsgProto();
                            if (object.positionX != null)
                                message.positionX = object.positionX | 0;
                            if (object.positionY != null)
                                message.positionY = object.positionY | 0;
                            if (object.width != null)
                                message.width = object.width >>> 0;
                            if (object.height != null)
                                message.height = object.height >>> 0;
                            if (object.base64Content != null)
                                if (typeof object.base64Content === "string")
                                    $util.base64.decode(object.base64Content, message.base64Content = $util.newBuffer($util.base64.length(object.base64Content)), 0);
                                else if (object.base64Content.length)
                                    message.base64Content = object.base64Content;
                            return message;
                        };

                        /**
                         * Creates a plain object from a WindowPartialContentMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.WindowPartialContentMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.WindowPartialContentMsgProto} message WindowPartialContentMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        WindowPartialContentMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.positionX = 0;
                                object.positionY = 0;
                                object.width = 0;
                                object.height = 0;
                                if (options.bytes === String)
                                    object.base64Content = "";
                                else {
                                    object.base64Content = [];
                                    if (options.bytes !== Array)
                                        object.base64Content = $util.newBuffer(object.base64Content);
                                }
                            }
                            if (message.positionX != null && message.hasOwnProperty("positionX"))
                                object.positionX = message.positionX;
                            if (message.positionY != null && message.hasOwnProperty("positionY"))
                                object.positionY = message.positionY;
                            if (message.width != null && message.hasOwnProperty("width"))
                                object.width = message.width;
                            if (message.height != null && message.hasOwnProperty("height"))
                                object.height = message.height;
                            if (message.base64Content != null && message.hasOwnProperty("base64Content"))
                                object.base64Content = options.bytes === String ? $util.base64.encode(message.base64Content, 0, message.base64Content.length) : options.bytes === Array ? Array.prototype.slice.call(message.base64Content) : message.base64Content;
                            return object;
                        };

                        /**
                         * Converts this WindowPartialContentMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.WindowPartialContentMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        WindowPartialContentMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return WindowPartialContentMsgProto;
                    })();

                    /**
                     * SimpleEventMsgOutProto enum.
                     * @name org.webswing.server.model.proto.SimpleEventMsgOutProto
                     * @enum {string}
                     * @property {number} applicationAlreadyRunning=0 applicationAlreadyRunning value
                     * @property {number} shutDownNotification=1 shutDownNotification value
                     * @property {number} tooManyClientsNotification=2 tooManyClientsNotification value
                     * @property {number} continueOldSession=3 continueOldSession value
                     * @property {number} configurationError=4 configurationError value
                     * @property {number} sessionStolenNotification=5 sessionStolenNotification value
                     * @property {number} unauthorizedAccess=6 unauthorizedAccess value
                     * @property {number} shutDownAutoLogoutNotification=7 shutDownAutoLogoutNotification value
                     * @property {number} sessionTimeoutWarning=8 sessionTimeoutWarning value
                     * @property {number} sessionTimedOutNotification=9 sessionTimedOutNotification value
                     * @property {number} applicationBusy=10 applicationBusy value
                     */
                    proto.SimpleEventMsgOutProto = (function() {
                        var valuesById = {}, values = Object.create(valuesById);
                        values[valuesById[0] = "applicationAlreadyRunning"] = 0;
                        values[valuesById[1] = "shutDownNotification"] = 1;
                        values[valuesById[2] = "tooManyClientsNotification"] = 2;
                        values[valuesById[3] = "continueOldSession"] = 3;
                        values[valuesById[4] = "configurationError"] = 4;
                        values[valuesById[5] = "sessionStolenNotification"] = 5;
                        values[valuesById[6] = "unauthorizedAccess"] = 6;
                        values[valuesById[7] = "shutDownAutoLogoutNotification"] = 7;
                        values[valuesById[8] = "sessionTimeoutWarning"] = 8;
                        values[valuesById[9] = "sessionTimedOutNotification"] = 9;
                        values[valuesById[10] = "applicationBusy"] = 10;
                        return values;
                    })();

                    proto.JsEvalRequestMsgOutProto = (function() {

                        /**
                         * Properties of a JsEvalRequestMsgOutProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IJsEvalRequestMsgOutProto
                         * @property {string|null} [correlationId] JsEvalRequestMsgOutProto correlationId
                         * @property {string|null} [thisObjectId] JsEvalRequestMsgOutProto thisObjectId
                         * @property {org.webswing.server.model.proto.JsEvalRequestMsgOutProto.JsEvalRequestTypeProto|null} [type] JsEvalRequestMsgOutProto type
                         * @property {string|null} [evalString] JsEvalRequestMsgOutProto evalString
                         * @property {Array.<org.webswing.server.model.proto.IJsParamMsgProto>|null} [params] JsEvalRequestMsgOutProto params
                         * @property {Array.<string>|null} [garbageIds] JsEvalRequestMsgOutProto garbageIds
                         */

                        /**
                         * Constructs a new JsEvalRequestMsgOutProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a JsEvalRequestMsgOutProto.
                         * @implements IJsEvalRequestMsgOutProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IJsEvalRequestMsgOutProto=} [properties] Properties to set
                         */
                        function JsEvalRequestMsgOutProto(properties) {
                            this.params = [];
                            this.garbageIds = [];
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * JsEvalRequestMsgOutProto correlationId.
                         * @member {string} correlationId
                         * @memberof org.webswing.server.model.proto.JsEvalRequestMsgOutProto
                         * @instance
                         */
                        JsEvalRequestMsgOutProto.prototype.correlationId = "";

                        /**
                         * JsEvalRequestMsgOutProto thisObjectId.
                         * @member {string} thisObjectId
                         * @memberof org.webswing.server.model.proto.JsEvalRequestMsgOutProto
                         * @instance
                         */
                        JsEvalRequestMsgOutProto.prototype.thisObjectId = "";

                        /**
                         * JsEvalRequestMsgOutProto type.
                         * @member {org.webswing.server.model.proto.JsEvalRequestMsgOutProto.JsEvalRequestTypeProto} type
                         * @memberof org.webswing.server.model.proto.JsEvalRequestMsgOutProto
                         * @instance
                         */
                        JsEvalRequestMsgOutProto.prototype.type = 0;

                        /**
                         * JsEvalRequestMsgOutProto evalString.
                         * @member {string} evalString
                         * @memberof org.webswing.server.model.proto.JsEvalRequestMsgOutProto
                         * @instance
                         */
                        JsEvalRequestMsgOutProto.prototype.evalString = "";

                        /**
                         * JsEvalRequestMsgOutProto params.
                         * @member {Array.<org.webswing.server.model.proto.IJsParamMsgProto>} params
                         * @memberof org.webswing.server.model.proto.JsEvalRequestMsgOutProto
                         * @instance
                         */
                        JsEvalRequestMsgOutProto.prototype.params = $util.emptyArray;

                        /**
                         * JsEvalRequestMsgOutProto garbageIds.
                         * @member {Array.<string>} garbageIds
                         * @memberof org.webswing.server.model.proto.JsEvalRequestMsgOutProto
                         * @instance
                         */
                        JsEvalRequestMsgOutProto.prototype.garbageIds = $util.emptyArray;

                        /**
                         * Creates a new JsEvalRequestMsgOutProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.JsEvalRequestMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.IJsEvalRequestMsgOutProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.JsEvalRequestMsgOutProto} JsEvalRequestMsgOutProto instance
                         */
                        JsEvalRequestMsgOutProto.create = function create(properties) {
                            return new JsEvalRequestMsgOutProto(properties);
                        };

                        /**
                         * Encodes the specified JsEvalRequestMsgOutProto message. Does not implicitly {@link org.webswing.server.model.proto.JsEvalRequestMsgOutProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.JsEvalRequestMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.IJsEvalRequestMsgOutProto} message JsEvalRequestMsgOutProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        JsEvalRequestMsgOutProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.correlationId != null && message.hasOwnProperty("correlationId"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.correlationId);
                            if (message.thisObjectId != null && message.hasOwnProperty("thisObjectId"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.thisObjectId);
                            if (message.type != null && message.hasOwnProperty("type"))
                                writer.uint32(/* id 3, wireType 0 =*/24).int32(message.type);
                            if (message.evalString != null && message.hasOwnProperty("evalString"))
                                writer.uint32(/* id 4, wireType 2 =*/34).string(message.evalString);
                            if (message.params != null && message.params.length)
                                for (var i = 0; i < message.params.length; ++i)
                                    $root.org.webswing.server.model.proto.JsParamMsgProto.encode(message.params[i], writer.uint32(/* id 5, wireType 2 =*/42).fork()).ldelim();
                            if (message.garbageIds != null && message.garbageIds.length)
                                for (var i = 0; i < message.garbageIds.length; ++i)
                                    writer.uint32(/* id 6, wireType 2 =*/50).string(message.garbageIds[i]);
                            return writer;
                        };

                        /**
                         * Decodes a JsEvalRequestMsgOutProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.JsEvalRequestMsgOutProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.JsEvalRequestMsgOutProto} JsEvalRequestMsgOutProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        JsEvalRequestMsgOutProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.JsEvalRequestMsgOutProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.correlationId = reader.string();
                                    break;
                                case 2:
                                    message.thisObjectId = reader.string();
                                    break;
                                case 3:
                                    message.type = reader.int32();
                                    break;
                                case 4:
                                    message.evalString = reader.string();
                                    break;
                                case 5:
                                    if (!(message.params && message.params.length))
                                        message.params = [];
                                    message.params.push($root.org.webswing.server.model.proto.JsParamMsgProto.decode(reader, reader.uint32()));
                                    break;
                                case 6:
                                    if (!(message.garbageIds && message.garbageIds.length))
                                        message.garbageIds = [];
                                    message.garbageIds.push(reader.string());
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a JsEvalRequestMsgOutProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.JsEvalRequestMsgOutProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.JsEvalRequestMsgOutProto} JsEvalRequestMsgOutProto
                         */
                        JsEvalRequestMsgOutProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.JsEvalRequestMsgOutProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.JsEvalRequestMsgOutProto();
                            if (object.correlationId != null)
                                message.correlationId = String(object.correlationId);
                            if (object.thisObjectId != null)
                                message.thisObjectId = String(object.thisObjectId);
                            switch (object.type) {
                            case "eval":
                            case 0:
                                message.type = 0;
                                break;
                            case "call":
                            case 1:
                                message.type = 1;
                                break;
                            case "setMember":
                            case 2:
                                message.type = 2;
                                break;
                            case "getMember":
                            case 3:
                                message.type = 3;
                                break;
                            case "deleteMember":
                            case 4:
                                message.type = 4;
                                break;
                            case "setSlot":
                            case 5:
                                message.type = 5;
                                break;
                            case "getSlot":
                            case 6:
                                message.type = 6;
                                break;
                            }
                            if (object.evalString != null)
                                message.evalString = String(object.evalString);
                            if (object.params) {
                                if (!Array.isArray(object.params))
                                    throw TypeError(".org.webswing.server.model.proto.JsEvalRequestMsgOutProto.params: array expected");
                                message.params = [];
                                for (var i = 0; i < object.params.length; ++i) {
                                    if (typeof object.params[i] !== "object")
                                        throw TypeError(".org.webswing.server.model.proto.JsEvalRequestMsgOutProto.params: object expected");
                                    message.params[i] = $root.org.webswing.server.model.proto.JsParamMsgProto.fromObject(object.params[i]);
                                }
                            }
                            if (object.garbageIds) {
                                if (!Array.isArray(object.garbageIds))
                                    throw TypeError(".org.webswing.server.model.proto.JsEvalRequestMsgOutProto.garbageIds: array expected");
                                message.garbageIds = [];
                                for (var i = 0; i < object.garbageIds.length; ++i)
                                    message.garbageIds[i] = String(object.garbageIds[i]);
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from a JsEvalRequestMsgOutProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.JsEvalRequestMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.JsEvalRequestMsgOutProto} message JsEvalRequestMsgOutProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        JsEvalRequestMsgOutProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.arrays || options.defaults) {
                                object.params = [];
                                object.garbageIds = [];
                            }
                            if (options.defaults) {
                                object.correlationId = "";
                                object.thisObjectId = "";
                                object.type = options.enums === String ? "eval" : 0;
                                object.evalString = "";
                            }
                            if (message.correlationId != null && message.hasOwnProperty("correlationId"))
                                object.correlationId = message.correlationId;
                            if (message.thisObjectId != null && message.hasOwnProperty("thisObjectId"))
                                object.thisObjectId = message.thisObjectId;
                            if (message.type != null && message.hasOwnProperty("type"))
                                object.type = options.enums === String ? $root.org.webswing.server.model.proto.JsEvalRequestMsgOutProto.JsEvalRequestTypeProto[message.type] : message.type;
                            if (message.evalString != null && message.hasOwnProperty("evalString"))
                                object.evalString = message.evalString;
                            if (message.params && message.params.length) {
                                object.params = [];
                                for (var j = 0; j < message.params.length; ++j)
                                    object.params[j] = $root.org.webswing.server.model.proto.JsParamMsgProto.toObject(message.params[j], options);
                            }
                            if (message.garbageIds && message.garbageIds.length) {
                                object.garbageIds = [];
                                for (var j = 0; j < message.garbageIds.length; ++j)
                                    object.garbageIds[j] = message.garbageIds[j];
                            }
                            return object;
                        };

                        /**
                         * Converts this JsEvalRequestMsgOutProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.JsEvalRequestMsgOutProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        JsEvalRequestMsgOutProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        /**
                         * JsEvalRequestTypeProto enum.
                         * @name org.webswing.server.model.proto.JsEvalRequestMsgOutProto.JsEvalRequestTypeProto
                         * @enum {string}
                         * @property {number} eval=0 eval value
                         * @property {number} call=1 call value
                         * @property {number} setMember=2 setMember value
                         * @property {number} getMember=3 getMember value
                         * @property {number} deleteMember=4 deleteMember value
                         * @property {number} setSlot=5 setSlot value
                         * @property {number} getSlot=6 getSlot value
                         */
                        JsEvalRequestMsgOutProto.JsEvalRequestTypeProto = (function() {
                            var valuesById = {}, values = Object.create(valuesById);
                            values[valuesById[0] = "eval"] = 0;
                            values[valuesById[1] = "call"] = 1;
                            values[valuesById[2] = "setMember"] = 2;
                            values[valuesById[3] = "getMember"] = 3;
                            values[valuesById[4] = "deleteMember"] = 4;
                            values[valuesById[5] = "setSlot"] = 5;
                            values[valuesById[6] = "getSlot"] = 6;
                            return values;
                        })();

                        return JsEvalRequestMsgOutProto;
                    })();

                    proto.JsParamMsgProto = (function() {

                        /**
                         * Properties of a JsParamMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IJsParamMsgProto
                         * @property {string|null} [primitive] JsParamMsgProto primitive
                         * @property {org.webswing.server.model.proto.IJSObjectMsgProto|null} [jsObject] JsParamMsgProto jsObject
                         * @property {org.webswing.server.model.proto.IJavaObjectRefMsgProto|null} [javaObject] JsParamMsgProto javaObject
                         * @property {Array.<org.webswing.server.model.proto.IJsParamMsgProto>|null} [array] JsParamMsgProto array
                         */

                        /**
                         * Constructs a new JsParamMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a JsParamMsgProto.
                         * @implements IJsParamMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IJsParamMsgProto=} [properties] Properties to set
                         */
                        function JsParamMsgProto(properties) {
                            this.array = [];
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * JsParamMsgProto primitive.
                         * @member {string} primitive
                         * @memberof org.webswing.server.model.proto.JsParamMsgProto
                         * @instance
                         */
                        JsParamMsgProto.prototype.primitive = "";

                        /**
                         * JsParamMsgProto jsObject.
                         * @member {org.webswing.server.model.proto.IJSObjectMsgProto|null|undefined} jsObject
                         * @memberof org.webswing.server.model.proto.JsParamMsgProto
                         * @instance
                         */
                        JsParamMsgProto.prototype.jsObject = null;

                        /**
                         * JsParamMsgProto javaObject.
                         * @member {org.webswing.server.model.proto.IJavaObjectRefMsgProto|null|undefined} javaObject
                         * @memberof org.webswing.server.model.proto.JsParamMsgProto
                         * @instance
                         */
                        JsParamMsgProto.prototype.javaObject = null;

                        /**
                         * JsParamMsgProto array.
                         * @member {Array.<org.webswing.server.model.proto.IJsParamMsgProto>} array
                         * @memberof org.webswing.server.model.proto.JsParamMsgProto
                         * @instance
                         */
                        JsParamMsgProto.prototype.array = $util.emptyArray;

                        /**
                         * Creates a new JsParamMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.JsParamMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IJsParamMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.JsParamMsgProto} JsParamMsgProto instance
                         */
                        JsParamMsgProto.create = function create(properties) {
                            return new JsParamMsgProto(properties);
                        };

                        /**
                         * Encodes the specified JsParamMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.JsParamMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.JsParamMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IJsParamMsgProto} message JsParamMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        JsParamMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.primitive != null && message.hasOwnProperty("primitive"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.primitive);
                            if (message.jsObject != null && message.hasOwnProperty("jsObject"))
                                $root.org.webswing.server.model.proto.JSObjectMsgProto.encode(message.jsObject, writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
                            if (message.javaObject != null && message.hasOwnProperty("javaObject"))
                                $root.org.webswing.server.model.proto.JavaObjectRefMsgProto.encode(message.javaObject, writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
                            if (message.array != null && message.array.length)
                                for (var i = 0; i < message.array.length; ++i)
                                    $root.org.webswing.server.model.proto.JsParamMsgProto.encode(message.array[i], writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
                            return writer;
                        };

                        /**
                         * Decodes a JsParamMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.JsParamMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.JsParamMsgProto} JsParamMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        JsParamMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.JsParamMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.primitive = reader.string();
                                    break;
                                case 2:
                                    message.jsObject = $root.org.webswing.server.model.proto.JSObjectMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 3:
                                    message.javaObject = $root.org.webswing.server.model.proto.JavaObjectRefMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 4:
                                    if (!(message.array && message.array.length))
                                        message.array = [];
                                    message.array.push($root.org.webswing.server.model.proto.JsParamMsgProto.decode(reader, reader.uint32()));
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a JsParamMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.JsParamMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.JsParamMsgProto} JsParamMsgProto
                         */
                        JsParamMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.JsParamMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.JsParamMsgProto();
                            if (object.primitive != null)
                                message.primitive = String(object.primitive);
                            if (object.jsObject != null) {
                                if (typeof object.jsObject !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.JsParamMsgProto.jsObject: object expected");
                                message.jsObject = $root.org.webswing.server.model.proto.JSObjectMsgProto.fromObject(object.jsObject);
                            }
                            if (object.javaObject != null) {
                                if (typeof object.javaObject !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.JsParamMsgProto.javaObject: object expected");
                                message.javaObject = $root.org.webswing.server.model.proto.JavaObjectRefMsgProto.fromObject(object.javaObject);
                            }
                            if (object.array) {
                                if (!Array.isArray(object.array))
                                    throw TypeError(".org.webswing.server.model.proto.JsParamMsgProto.array: array expected");
                                message.array = [];
                                for (var i = 0; i < object.array.length; ++i) {
                                    if (typeof object.array[i] !== "object")
                                        throw TypeError(".org.webswing.server.model.proto.JsParamMsgProto.array: object expected");
                                    message.array[i] = $root.org.webswing.server.model.proto.JsParamMsgProto.fromObject(object.array[i]);
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from a JsParamMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.JsParamMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.JsParamMsgProto} message JsParamMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        JsParamMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.arrays || options.defaults)
                                object.array = [];
                            if (options.defaults) {
                                object.primitive = "";
                                object.jsObject = null;
                                object.javaObject = null;
                            }
                            if (message.primitive != null && message.hasOwnProperty("primitive"))
                                object.primitive = message.primitive;
                            if (message.jsObject != null && message.hasOwnProperty("jsObject"))
                                object.jsObject = $root.org.webswing.server.model.proto.JSObjectMsgProto.toObject(message.jsObject, options);
                            if (message.javaObject != null && message.hasOwnProperty("javaObject"))
                                object.javaObject = $root.org.webswing.server.model.proto.JavaObjectRefMsgProto.toObject(message.javaObject, options);
                            if (message.array && message.array.length) {
                                object.array = [];
                                for (var j = 0; j < message.array.length; ++j)
                                    object.array[j] = $root.org.webswing.server.model.proto.JsParamMsgProto.toObject(message.array[j], options);
                            }
                            return object;
                        };

                        /**
                         * Converts this JsParamMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.JsParamMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        JsParamMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return JsParamMsgProto;
                    })();

                    proto.JSObjectMsgProto = (function() {

                        /**
                         * Properties of a JSObjectMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IJSObjectMsgProto
                         * @property {string|null} [id] JSObjectMsgProto id
                         */

                        /**
                         * Constructs a new JSObjectMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a JSObjectMsgProto.
                         * @implements IJSObjectMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IJSObjectMsgProto=} [properties] Properties to set
                         */
                        function JSObjectMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * JSObjectMsgProto id.
                         * @member {string} id
                         * @memberof org.webswing.server.model.proto.JSObjectMsgProto
                         * @instance
                         */
                        JSObjectMsgProto.prototype.id = "";

                        /**
                         * Creates a new JSObjectMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.JSObjectMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IJSObjectMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.JSObjectMsgProto} JSObjectMsgProto instance
                         */
                        JSObjectMsgProto.create = function create(properties) {
                            return new JSObjectMsgProto(properties);
                        };

                        /**
                         * Encodes the specified JSObjectMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.JSObjectMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.JSObjectMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IJSObjectMsgProto} message JSObjectMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        JSObjectMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.id != null && message.hasOwnProperty("id"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
                            return writer;
                        };

                        /**
                         * Decodes a JSObjectMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.JSObjectMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.JSObjectMsgProto} JSObjectMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        JSObjectMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.JSObjectMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.id = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a JSObjectMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.JSObjectMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.JSObjectMsgProto} JSObjectMsgProto
                         */
                        JSObjectMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.JSObjectMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.JSObjectMsgProto();
                            if (object.id != null)
                                message.id = String(object.id);
                            return message;
                        };

                        /**
                         * Creates a plain object from a JSObjectMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.JSObjectMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.JSObjectMsgProto} message JSObjectMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        JSObjectMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults)
                                object.id = "";
                            if (message.id != null && message.hasOwnProperty("id"))
                                object.id = message.id;
                            return object;
                        };

                        /**
                         * Converts this JSObjectMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.JSObjectMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        JSObjectMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return JSObjectMsgProto;
                    })();

                    proto.JavaObjectRefMsgProto = (function() {

                        /**
                         * Properties of a JavaObjectRefMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IJavaObjectRefMsgProto
                         * @property {string|null} [id] JavaObjectRefMsgProto id
                         * @property {Array.<string>|null} [methods] JavaObjectRefMsgProto methods
                         */

                        /**
                         * Constructs a new JavaObjectRefMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a JavaObjectRefMsgProto.
                         * @implements IJavaObjectRefMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IJavaObjectRefMsgProto=} [properties] Properties to set
                         */
                        function JavaObjectRefMsgProto(properties) {
                            this.methods = [];
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * JavaObjectRefMsgProto id.
                         * @member {string} id
                         * @memberof org.webswing.server.model.proto.JavaObjectRefMsgProto
                         * @instance
                         */
                        JavaObjectRefMsgProto.prototype.id = "";

                        /**
                         * JavaObjectRefMsgProto methods.
                         * @member {Array.<string>} methods
                         * @memberof org.webswing.server.model.proto.JavaObjectRefMsgProto
                         * @instance
                         */
                        JavaObjectRefMsgProto.prototype.methods = $util.emptyArray;

                        /**
                         * Creates a new JavaObjectRefMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.JavaObjectRefMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IJavaObjectRefMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.JavaObjectRefMsgProto} JavaObjectRefMsgProto instance
                         */
                        JavaObjectRefMsgProto.create = function create(properties) {
                            return new JavaObjectRefMsgProto(properties);
                        };

                        /**
                         * Encodes the specified JavaObjectRefMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.JavaObjectRefMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.JavaObjectRefMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IJavaObjectRefMsgProto} message JavaObjectRefMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        JavaObjectRefMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.id != null && message.hasOwnProperty("id"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
                            if (message.methods != null && message.methods.length)
                                for (var i = 0; i < message.methods.length; ++i)
                                    writer.uint32(/* id 2, wireType 2 =*/18).string(message.methods[i]);
                            return writer;
                        };

                        /**
                         * Decodes a JavaObjectRefMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.JavaObjectRefMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.JavaObjectRefMsgProto} JavaObjectRefMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        JavaObjectRefMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.JavaObjectRefMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.id = reader.string();
                                    break;
                                case 2:
                                    if (!(message.methods && message.methods.length))
                                        message.methods = [];
                                    message.methods.push(reader.string());
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a JavaObjectRefMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.JavaObjectRefMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.JavaObjectRefMsgProto} JavaObjectRefMsgProto
                         */
                        JavaObjectRefMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.JavaObjectRefMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.JavaObjectRefMsgProto();
                            if (object.id != null)
                                message.id = String(object.id);
                            if (object.methods) {
                                if (!Array.isArray(object.methods))
                                    throw TypeError(".org.webswing.server.model.proto.JavaObjectRefMsgProto.methods: array expected");
                                message.methods = [];
                                for (var i = 0; i < object.methods.length; ++i)
                                    message.methods[i] = String(object.methods[i]);
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from a JavaObjectRefMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.JavaObjectRefMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.JavaObjectRefMsgProto} message JavaObjectRefMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        JavaObjectRefMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.arrays || options.defaults)
                                object.methods = [];
                            if (options.defaults)
                                object.id = "";
                            if (message.id != null && message.hasOwnProperty("id"))
                                object.id = message.id;
                            if (message.methods && message.methods.length) {
                                object.methods = [];
                                for (var j = 0; j < message.methods.length; ++j)
                                    object.methods[j] = message.methods[j];
                            }
                            return object;
                        };

                        /**
                         * Converts this JavaObjectRefMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.JavaObjectRefMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        JavaObjectRefMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return JavaObjectRefMsgProto;
                    })();

                    proto.JsResultMsgProto = (function() {

                        /**
                         * Properties of a JsResultMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IJsResultMsgProto
                         * @property {string|null} [correlationId] JsResultMsgProto correlationId
                         * @property {string|null} [error] JsResultMsgProto error
                         * @property {org.webswing.server.model.proto.IJsParamMsgProto|null} [value] JsResultMsgProto value
                         */

                        /**
                         * Constructs a new JsResultMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a JsResultMsgProto.
                         * @implements IJsResultMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IJsResultMsgProto=} [properties] Properties to set
                         */
                        function JsResultMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * JsResultMsgProto correlationId.
                         * @member {string} correlationId
                         * @memberof org.webswing.server.model.proto.JsResultMsgProto
                         * @instance
                         */
                        JsResultMsgProto.prototype.correlationId = "";

                        /**
                         * JsResultMsgProto error.
                         * @member {string} error
                         * @memberof org.webswing.server.model.proto.JsResultMsgProto
                         * @instance
                         */
                        JsResultMsgProto.prototype.error = "";

                        /**
                         * JsResultMsgProto value.
                         * @member {org.webswing.server.model.proto.IJsParamMsgProto|null|undefined} value
                         * @memberof org.webswing.server.model.proto.JsResultMsgProto
                         * @instance
                         */
                        JsResultMsgProto.prototype.value = null;

                        /**
                         * Creates a new JsResultMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.JsResultMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IJsResultMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.JsResultMsgProto} JsResultMsgProto instance
                         */
                        JsResultMsgProto.create = function create(properties) {
                            return new JsResultMsgProto(properties);
                        };

                        /**
                         * Encodes the specified JsResultMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.JsResultMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.JsResultMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IJsResultMsgProto} message JsResultMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        JsResultMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.correlationId != null && message.hasOwnProperty("correlationId"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.correlationId);
                            if (message.error != null && message.hasOwnProperty("error"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.error);
                            if (message.value != null && message.hasOwnProperty("value"))
                                $root.org.webswing.server.model.proto.JsParamMsgProto.encode(message.value, writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
                            return writer;
                        };

                        /**
                         * Decodes a JsResultMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.JsResultMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.JsResultMsgProto} JsResultMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        JsResultMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.JsResultMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.correlationId = reader.string();
                                    break;
                                case 2:
                                    message.error = reader.string();
                                    break;
                                case 3:
                                    message.value = $root.org.webswing.server.model.proto.JsParamMsgProto.decode(reader, reader.uint32());
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a JsResultMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.JsResultMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.JsResultMsgProto} JsResultMsgProto
                         */
                        JsResultMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.JsResultMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.JsResultMsgProto();
                            if (object.correlationId != null)
                                message.correlationId = String(object.correlationId);
                            if (object.error != null)
                                message.error = String(object.error);
                            if (object.value != null) {
                                if (typeof object.value !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.JsResultMsgProto.value: object expected");
                                message.value = $root.org.webswing.server.model.proto.JsParamMsgProto.fromObject(object.value);
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from a JsResultMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.JsResultMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.JsResultMsgProto} message JsResultMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        JsResultMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.correlationId = "";
                                object.error = "";
                                object.value = null;
                            }
                            if (message.correlationId != null && message.hasOwnProperty("correlationId"))
                                object.correlationId = message.correlationId;
                            if (message.error != null && message.hasOwnProperty("error"))
                                object.error = message.error;
                            if (message.value != null && message.hasOwnProperty("value"))
                                object.value = $root.org.webswing.server.model.proto.JsParamMsgProto.toObject(message.value, options);
                            return object;
                        };

                        /**
                         * Converts this JsResultMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.JsResultMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        JsResultMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return JsResultMsgProto;
                    })();

                    proto.PlaybackInfoMsgProto = (function() {

                        /**
                         * Properties of a PlaybackInfoMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IPlaybackInfoMsgProto
                         * @property {number|null} [current] PlaybackInfoMsgProto current
                         * @property {number|null} [total] PlaybackInfoMsgProto total
                         */

                        /**
                         * Constructs a new PlaybackInfoMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a PlaybackInfoMsgProto.
                         * @implements IPlaybackInfoMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IPlaybackInfoMsgProto=} [properties] Properties to set
                         */
                        function PlaybackInfoMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * PlaybackInfoMsgProto current.
                         * @member {number} current
                         * @memberof org.webswing.server.model.proto.PlaybackInfoMsgProto
                         * @instance
                         */
                        PlaybackInfoMsgProto.prototype.current = 0;

                        /**
                         * PlaybackInfoMsgProto total.
                         * @member {number} total
                         * @memberof org.webswing.server.model.proto.PlaybackInfoMsgProto
                         * @instance
                         */
                        PlaybackInfoMsgProto.prototype.total = 0;

                        /**
                         * Creates a new PlaybackInfoMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.PlaybackInfoMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IPlaybackInfoMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.PlaybackInfoMsgProto} PlaybackInfoMsgProto instance
                         */
                        PlaybackInfoMsgProto.create = function create(properties) {
                            return new PlaybackInfoMsgProto(properties);
                        };

                        /**
                         * Encodes the specified PlaybackInfoMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.PlaybackInfoMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.PlaybackInfoMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IPlaybackInfoMsgProto} message PlaybackInfoMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        PlaybackInfoMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.current != null && message.hasOwnProperty("current"))
                                writer.uint32(/* id 1, wireType 0 =*/8).uint32(message.current);
                            if (message.total != null && message.hasOwnProperty("total"))
                                writer.uint32(/* id 2, wireType 0 =*/16).uint32(message.total);
                            return writer;
                        };

                        /**
                         * Decodes a PlaybackInfoMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.PlaybackInfoMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.PlaybackInfoMsgProto} PlaybackInfoMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        PlaybackInfoMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.PlaybackInfoMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.current = reader.uint32();
                                    break;
                                case 2:
                                    message.total = reader.uint32();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a PlaybackInfoMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.PlaybackInfoMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.PlaybackInfoMsgProto} PlaybackInfoMsgProto
                         */
                        PlaybackInfoMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.PlaybackInfoMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.PlaybackInfoMsgProto();
                            if (object.current != null)
                                message.current = object.current >>> 0;
                            if (object.total != null)
                                message.total = object.total >>> 0;
                            return message;
                        };

                        /**
                         * Creates a plain object from a PlaybackInfoMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.PlaybackInfoMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.PlaybackInfoMsgProto} message PlaybackInfoMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        PlaybackInfoMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.current = 0;
                                object.total = 0;
                            }
                            if (message.current != null && message.hasOwnProperty("current"))
                                object.current = message.current;
                            if (message.total != null && message.hasOwnProperty("total"))
                                object.total = message.total;
                            return object;
                        };

                        /**
                         * Converts this PlaybackInfoMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.PlaybackInfoMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        PlaybackInfoMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return PlaybackInfoMsgProto;
                    })();

                    proto.PixelsAreaRequestMsgOutProto = (function() {

                        /**
                         * Properties of a PixelsAreaRequestMsgOutProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IPixelsAreaRequestMsgOutProto
                         * @property {string|null} [correlationId] PixelsAreaRequestMsgOutProto correlationId
                         * @property {number|null} [x] PixelsAreaRequestMsgOutProto x
                         * @property {number|null} [y] PixelsAreaRequestMsgOutProto y
                         * @property {number|null} [w] PixelsAreaRequestMsgOutProto w
                         * @property {number|null} [h] PixelsAreaRequestMsgOutProto h
                         */

                        /**
                         * Constructs a new PixelsAreaRequestMsgOutProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a PixelsAreaRequestMsgOutProto.
                         * @implements IPixelsAreaRequestMsgOutProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IPixelsAreaRequestMsgOutProto=} [properties] Properties to set
                         */
                        function PixelsAreaRequestMsgOutProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * PixelsAreaRequestMsgOutProto correlationId.
                         * @member {string} correlationId
                         * @memberof org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto
                         * @instance
                         */
                        PixelsAreaRequestMsgOutProto.prototype.correlationId = "";

                        /**
                         * PixelsAreaRequestMsgOutProto x.
                         * @member {number} x
                         * @memberof org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto
                         * @instance
                         */
                        PixelsAreaRequestMsgOutProto.prototype.x = 0;

                        /**
                         * PixelsAreaRequestMsgOutProto y.
                         * @member {number} y
                         * @memberof org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto
                         * @instance
                         */
                        PixelsAreaRequestMsgOutProto.prototype.y = 0;

                        /**
                         * PixelsAreaRequestMsgOutProto w.
                         * @member {number} w
                         * @memberof org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto
                         * @instance
                         */
                        PixelsAreaRequestMsgOutProto.prototype.w = 0;

                        /**
                         * PixelsAreaRequestMsgOutProto h.
                         * @member {number} h
                         * @memberof org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto
                         * @instance
                         */
                        PixelsAreaRequestMsgOutProto.prototype.h = 0;

                        /**
                         * Creates a new PixelsAreaRequestMsgOutProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.IPixelsAreaRequestMsgOutProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto} PixelsAreaRequestMsgOutProto instance
                         */
                        PixelsAreaRequestMsgOutProto.create = function create(properties) {
                            return new PixelsAreaRequestMsgOutProto(properties);
                        };

                        /**
                         * Encodes the specified PixelsAreaRequestMsgOutProto message. Does not implicitly {@link org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.IPixelsAreaRequestMsgOutProto} message PixelsAreaRequestMsgOutProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        PixelsAreaRequestMsgOutProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.correlationId != null && message.hasOwnProperty("correlationId"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.correlationId);
                            if (message.x != null && message.hasOwnProperty("x"))
                                writer.uint32(/* id 2, wireType 0 =*/16).uint32(message.x);
                            if (message.y != null && message.hasOwnProperty("y"))
                                writer.uint32(/* id 3, wireType 0 =*/24).uint32(message.y);
                            if (message.w != null && message.hasOwnProperty("w"))
                                writer.uint32(/* id 4, wireType 0 =*/32).uint32(message.w);
                            if (message.h != null && message.hasOwnProperty("h"))
                                writer.uint32(/* id 5, wireType 0 =*/40).uint32(message.h);
                            return writer;
                        };

                        /**
                         * Decodes a PixelsAreaRequestMsgOutProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto} PixelsAreaRequestMsgOutProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        PixelsAreaRequestMsgOutProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.correlationId = reader.string();
                                    break;
                                case 2:
                                    message.x = reader.uint32();
                                    break;
                                case 3:
                                    message.y = reader.uint32();
                                    break;
                                case 4:
                                    message.w = reader.uint32();
                                    break;
                                case 5:
                                    message.h = reader.uint32();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a PixelsAreaRequestMsgOutProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto} PixelsAreaRequestMsgOutProto
                         */
                        PixelsAreaRequestMsgOutProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto();
                            if (object.correlationId != null)
                                message.correlationId = String(object.correlationId);
                            if (object.x != null)
                                message.x = object.x >>> 0;
                            if (object.y != null)
                                message.y = object.y >>> 0;
                            if (object.w != null)
                                message.w = object.w >>> 0;
                            if (object.h != null)
                                message.h = object.h >>> 0;
                            return message;
                        };

                        /**
                         * Creates a plain object from a PixelsAreaRequestMsgOutProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto} message PixelsAreaRequestMsgOutProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        PixelsAreaRequestMsgOutProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.correlationId = "";
                                object.x = 0;
                                object.y = 0;
                                object.w = 0;
                                object.h = 0;
                            }
                            if (message.correlationId != null && message.hasOwnProperty("correlationId"))
                                object.correlationId = message.correlationId;
                            if (message.x != null && message.hasOwnProperty("x"))
                                object.x = message.x;
                            if (message.y != null && message.hasOwnProperty("y"))
                                object.y = message.y;
                            if (message.w != null && message.hasOwnProperty("w"))
                                object.w = message.w;
                            if (message.h != null && message.hasOwnProperty("h"))
                                object.h = message.h;
                            return object;
                        };

                        /**
                         * Converts this PixelsAreaRequestMsgOutProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        PixelsAreaRequestMsgOutProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return PixelsAreaRequestMsgOutProto;
                    })();

                    proto.InputEventsFrameMsgInProto = (function() {

                        /**
                         * Properties of an InputEventsFrameMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IInputEventsFrameMsgInProto
                         * @property {Array.<org.webswing.server.model.proto.IInputEventMsgInProto>|null} [events] InputEventsFrameMsgInProto events
                         * @property {org.webswing.server.model.proto.IPasteEventMsgInProto|null} [paste] InputEventsFrameMsgInProto paste
                         * @property {org.webswing.server.model.proto.ICopyEventMsgInProto|null} [copy] InputEventsFrameMsgInProto copy
                         * @property {org.webswing.server.model.proto.IUploadEventMsgInProto|null} [upload] InputEventsFrameMsgInProto upload
                         * @property {org.webswing.server.model.proto.IFilesSelectedEventMsgInProto|null} [selected] InputEventsFrameMsgInProto selected
                         * @property {org.webswing.server.model.proto.IJsResultMsgProto|null} [jsResponse] InputEventsFrameMsgInProto jsResponse
                         * @property {org.webswing.server.model.proto.IJavaEvalRequestMsgInProto|null} [javaRequest] InputEventsFrameMsgInProto javaRequest
                         * @property {org.webswing.server.model.proto.IPlaybackCommandMsgInProto|null} [playback] InputEventsFrameMsgInProto playback
                         * @property {org.webswing.server.model.proto.IPixelsAreaResponseMsgInProto|null} [pixelsResponse] InputEventsFrameMsgInProto pixelsResponse
                         * @property {org.webswing.server.model.proto.IWindowEventMsgInProto|null} [window] InputEventsFrameMsgInProto window
                         * @property {org.webswing.server.model.proto.IActionEventMsgInProto|null} [action] InputEventsFrameMsgInProto action
                         */

                        /**
                         * Constructs a new InputEventsFrameMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents an InputEventsFrameMsgInProto.
                         * @implements IInputEventsFrameMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IInputEventsFrameMsgInProto=} [properties] Properties to set
                         */
                        function InputEventsFrameMsgInProto(properties) {
                            this.events = [];
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * InputEventsFrameMsgInProto events.
                         * @member {Array.<org.webswing.server.model.proto.IInputEventMsgInProto>} events
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @instance
                         */
                        InputEventsFrameMsgInProto.prototype.events = $util.emptyArray;

                        /**
                         * InputEventsFrameMsgInProto paste.
                         * @member {org.webswing.server.model.proto.IPasteEventMsgInProto|null|undefined} paste
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @instance
                         */
                        InputEventsFrameMsgInProto.prototype.paste = null;

                        /**
                         * InputEventsFrameMsgInProto copy.
                         * @member {org.webswing.server.model.proto.ICopyEventMsgInProto|null|undefined} copy
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @instance
                         */
                        InputEventsFrameMsgInProto.prototype.copy = null;

                        /**
                         * InputEventsFrameMsgInProto upload.
                         * @member {org.webswing.server.model.proto.IUploadEventMsgInProto|null|undefined} upload
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @instance
                         */
                        InputEventsFrameMsgInProto.prototype.upload = null;

                        /**
                         * InputEventsFrameMsgInProto selected.
                         * @member {org.webswing.server.model.proto.IFilesSelectedEventMsgInProto|null|undefined} selected
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @instance
                         */
                        InputEventsFrameMsgInProto.prototype.selected = null;

                        /**
                         * InputEventsFrameMsgInProto jsResponse.
                         * @member {org.webswing.server.model.proto.IJsResultMsgProto|null|undefined} jsResponse
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @instance
                         */
                        InputEventsFrameMsgInProto.prototype.jsResponse = null;

                        /**
                         * InputEventsFrameMsgInProto javaRequest.
                         * @member {org.webswing.server.model.proto.IJavaEvalRequestMsgInProto|null|undefined} javaRequest
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @instance
                         */
                        InputEventsFrameMsgInProto.prototype.javaRequest = null;

                        /**
                         * InputEventsFrameMsgInProto playback.
                         * @member {org.webswing.server.model.proto.IPlaybackCommandMsgInProto|null|undefined} playback
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @instance
                         */
                        InputEventsFrameMsgInProto.prototype.playback = null;

                        /**
                         * InputEventsFrameMsgInProto pixelsResponse.
                         * @member {org.webswing.server.model.proto.IPixelsAreaResponseMsgInProto|null|undefined} pixelsResponse
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @instance
                         */
                        InputEventsFrameMsgInProto.prototype.pixelsResponse = null;

                        /**
                         * InputEventsFrameMsgInProto window.
                         * @member {org.webswing.server.model.proto.IWindowEventMsgInProto|null|undefined} window
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @instance
                         */
                        InputEventsFrameMsgInProto.prototype.window = null;

                        /**
                         * InputEventsFrameMsgInProto action.
                         * @member {org.webswing.server.model.proto.IActionEventMsgInProto|null|undefined} action
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @instance
                         */
                        InputEventsFrameMsgInProto.prototype.action = null;

                        /**
                         * Creates a new InputEventsFrameMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IInputEventsFrameMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.InputEventsFrameMsgInProto} InputEventsFrameMsgInProto instance
                         */
                        InputEventsFrameMsgInProto.create = function create(properties) {
                            return new InputEventsFrameMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified InputEventsFrameMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.InputEventsFrameMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IInputEventsFrameMsgInProto} message InputEventsFrameMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        InputEventsFrameMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.events != null && message.events.length)
                                for (var i = 0; i < message.events.length; ++i)
                                    $root.org.webswing.server.model.proto.InputEventMsgInProto.encode(message.events[i], writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
                            if (message.paste != null && message.hasOwnProperty("paste"))
                                $root.org.webswing.server.model.proto.PasteEventMsgInProto.encode(message.paste, writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
                            if (message.copy != null && message.hasOwnProperty("copy"))
                                $root.org.webswing.server.model.proto.CopyEventMsgInProto.encode(message.copy, writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
                            if (message.upload != null && message.hasOwnProperty("upload"))
                                $root.org.webswing.server.model.proto.UploadEventMsgInProto.encode(message.upload, writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
                            if (message.selected != null && message.hasOwnProperty("selected"))
                                $root.org.webswing.server.model.proto.FilesSelectedEventMsgInProto.encode(message.selected, writer.uint32(/* id 5, wireType 2 =*/42).fork()).ldelim();
                            if (message.jsResponse != null && message.hasOwnProperty("jsResponse"))
                                $root.org.webswing.server.model.proto.JsResultMsgProto.encode(message.jsResponse, writer.uint32(/* id 6, wireType 2 =*/50).fork()).ldelim();
                            if (message.javaRequest != null && message.hasOwnProperty("javaRequest"))
                                $root.org.webswing.server.model.proto.JavaEvalRequestMsgInProto.encode(message.javaRequest, writer.uint32(/* id 7, wireType 2 =*/58).fork()).ldelim();
                            if (message.playback != null && message.hasOwnProperty("playback"))
                                $root.org.webswing.server.model.proto.PlaybackCommandMsgInProto.encode(message.playback, writer.uint32(/* id 8, wireType 2 =*/66).fork()).ldelim();
                            if (message.pixelsResponse != null && message.hasOwnProperty("pixelsResponse"))
                                $root.org.webswing.server.model.proto.PixelsAreaResponseMsgInProto.encode(message.pixelsResponse, writer.uint32(/* id 9, wireType 2 =*/74).fork()).ldelim();
                            if (message.window != null && message.hasOwnProperty("window"))
                                $root.org.webswing.server.model.proto.WindowEventMsgInProto.encode(message.window, writer.uint32(/* id 10, wireType 2 =*/82).fork()).ldelim();
                            if (message.action != null && message.hasOwnProperty("action"))
                                $root.org.webswing.server.model.proto.ActionEventMsgInProto.encode(message.action, writer.uint32(/* id 11, wireType 2 =*/90).fork()).ldelim();
                            return writer;
                        };

                        /**
                         * Decodes an InputEventsFrameMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.InputEventsFrameMsgInProto} InputEventsFrameMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        InputEventsFrameMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.InputEventsFrameMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    if (!(message.events && message.events.length))
                                        message.events = [];
                                    message.events.push($root.org.webswing.server.model.proto.InputEventMsgInProto.decode(reader, reader.uint32()));
                                    break;
                                case 2:
                                    message.paste = $root.org.webswing.server.model.proto.PasteEventMsgInProto.decode(reader, reader.uint32());
                                    break;
                                case 3:
                                    message.copy = $root.org.webswing.server.model.proto.CopyEventMsgInProto.decode(reader, reader.uint32());
                                    break;
                                case 4:
                                    message.upload = $root.org.webswing.server.model.proto.UploadEventMsgInProto.decode(reader, reader.uint32());
                                    break;
                                case 5:
                                    message.selected = $root.org.webswing.server.model.proto.FilesSelectedEventMsgInProto.decode(reader, reader.uint32());
                                    break;
                                case 6:
                                    message.jsResponse = $root.org.webswing.server.model.proto.JsResultMsgProto.decode(reader, reader.uint32());
                                    break;
                                case 7:
                                    message.javaRequest = $root.org.webswing.server.model.proto.JavaEvalRequestMsgInProto.decode(reader, reader.uint32());
                                    break;
                                case 8:
                                    message.playback = $root.org.webswing.server.model.proto.PlaybackCommandMsgInProto.decode(reader, reader.uint32());
                                    break;
                                case 9:
                                    message.pixelsResponse = $root.org.webswing.server.model.proto.PixelsAreaResponseMsgInProto.decode(reader, reader.uint32());
                                    break;
                                case 10:
                                    message.window = $root.org.webswing.server.model.proto.WindowEventMsgInProto.decode(reader, reader.uint32());
                                    break;
                                case 11:
                                    message.action = $root.org.webswing.server.model.proto.ActionEventMsgInProto.decode(reader, reader.uint32());
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates an InputEventsFrameMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.InputEventsFrameMsgInProto} InputEventsFrameMsgInProto
                         */
                        InputEventsFrameMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.InputEventsFrameMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.InputEventsFrameMsgInProto();
                            if (object.events) {
                                if (!Array.isArray(object.events))
                                    throw TypeError(".org.webswing.server.model.proto.InputEventsFrameMsgInProto.events: array expected");
                                message.events = [];
                                for (var i = 0; i < object.events.length; ++i) {
                                    if (typeof object.events[i] !== "object")
                                        throw TypeError(".org.webswing.server.model.proto.InputEventsFrameMsgInProto.events: object expected");
                                    message.events[i] = $root.org.webswing.server.model.proto.InputEventMsgInProto.fromObject(object.events[i]);
                                }
                            }
                            if (object.paste != null) {
                                if (typeof object.paste !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventsFrameMsgInProto.paste: object expected");
                                message.paste = $root.org.webswing.server.model.proto.PasteEventMsgInProto.fromObject(object.paste);
                            }
                            if (object.copy != null) {
                                if (typeof object.copy !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventsFrameMsgInProto.copy: object expected");
                                message.copy = $root.org.webswing.server.model.proto.CopyEventMsgInProto.fromObject(object.copy);
                            }
                            if (object.upload != null) {
                                if (typeof object.upload !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventsFrameMsgInProto.upload: object expected");
                                message.upload = $root.org.webswing.server.model.proto.UploadEventMsgInProto.fromObject(object.upload);
                            }
                            if (object.selected != null) {
                                if (typeof object.selected !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventsFrameMsgInProto.selected: object expected");
                                message.selected = $root.org.webswing.server.model.proto.FilesSelectedEventMsgInProto.fromObject(object.selected);
                            }
                            if (object.jsResponse != null) {
                                if (typeof object.jsResponse !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventsFrameMsgInProto.jsResponse: object expected");
                                message.jsResponse = $root.org.webswing.server.model.proto.JsResultMsgProto.fromObject(object.jsResponse);
                            }
                            if (object.javaRequest != null) {
                                if (typeof object.javaRequest !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventsFrameMsgInProto.javaRequest: object expected");
                                message.javaRequest = $root.org.webswing.server.model.proto.JavaEvalRequestMsgInProto.fromObject(object.javaRequest);
                            }
                            if (object.playback != null) {
                                if (typeof object.playback !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventsFrameMsgInProto.playback: object expected");
                                message.playback = $root.org.webswing.server.model.proto.PlaybackCommandMsgInProto.fromObject(object.playback);
                            }
                            if (object.pixelsResponse != null) {
                                if (typeof object.pixelsResponse !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventsFrameMsgInProto.pixelsResponse: object expected");
                                message.pixelsResponse = $root.org.webswing.server.model.proto.PixelsAreaResponseMsgInProto.fromObject(object.pixelsResponse);
                            }
                            if (object.window != null) {
                                if (typeof object.window !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventsFrameMsgInProto.window: object expected");
                                message.window = $root.org.webswing.server.model.proto.WindowEventMsgInProto.fromObject(object.window);
                            }
                            if (object.action != null) {
                                if (typeof object.action !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventsFrameMsgInProto.action: object expected");
                                message.action = $root.org.webswing.server.model.proto.ActionEventMsgInProto.fromObject(object.action);
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from an InputEventsFrameMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.InputEventsFrameMsgInProto} message InputEventsFrameMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        InputEventsFrameMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.arrays || options.defaults)
                                object.events = [];
                            if (options.defaults) {
                                object.paste = null;
                                object.copy = null;
                                object.upload = null;
                                object.selected = null;
                                object.jsResponse = null;
                                object.javaRequest = null;
                                object.playback = null;
                                object.pixelsResponse = null;
                                object.window = null;
                                object.action = null;
                            }
                            if (message.events && message.events.length) {
                                object.events = [];
                                for (var j = 0; j < message.events.length; ++j)
                                    object.events[j] = $root.org.webswing.server.model.proto.InputEventMsgInProto.toObject(message.events[j], options);
                            }
                            if (message.paste != null && message.hasOwnProperty("paste"))
                                object.paste = $root.org.webswing.server.model.proto.PasteEventMsgInProto.toObject(message.paste, options);
                            if (message.copy != null && message.hasOwnProperty("copy"))
                                object.copy = $root.org.webswing.server.model.proto.CopyEventMsgInProto.toObject(message.copy, options);
                            if (message.upload != null && message.hasOwnProperty("upload"))
                                object.upload = $root.org.webswing.server.model.proto.UploadEventMsgInProto.toObject(message.upload, options);
                            if (message.selected != null && message.hasOwnProperty("selected"))
                                object.selected = $root.org.webswing.server.model.proto.FilesSelectedEventMsgInProto.toObject(message.selected, options);
                            if (message.jsResponse != null && message.hasOwnProperty("jsResponse"))
                                object.jsResponse = $root.org.webswing.server.model.proto.JsResultMsgProto.toObject(message.jsResponse, options);
                            if (message.javaRequest != null && message.hasOwnProperty("javaRequest"))
                                object.javaRequest = $root.org.webswing.server.model.proto.JavaEvalRequestMsgInProto.toObject(message.javaRequest, options);
                            if (message.playback != null && message.hasOwnProperty("playback"))
                                object.playback = $root.org.webswing.server.model.proto.PlaybackCommandMsgInProto.toObject(message.playback, options);
                            if (message.pixelsResponse != null && message.hasOwnProperty("pixelsResponse"))
                                object.pixelsResponse = $root.org.webswing.server.model.proto.PixelsAreaResponseMsgInProto.toObject(message.pixelsResponse, options);
                            if (message.window != null && message.hasOwnProperty("window"))
                                object.window = $root.org.webswing.server.model.proto.WindowEventMsgInProto.toObject(message.window, options);
                            if (message.action != null && message.hasOwnProperty("action"))
                                object.action = $root.org.webswing.server.model.proto.ActionEventMsgInProto.toObject(message.action, options);
                            return object;
                        };

                        /**
                         * Converts this InputEventsFrameMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.InputEventsFrameMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        InputEventsFrameMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return InputEventsFrameMsgInProto;
                    })();

                    proto.InputEventMsgInProto = (function() {

                        /**
                         * Properties of an InputEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IInputEventMsgInProto
                         * @property {org.webswing.server.model.proto.IConnectionHandshakeMsgInProto|null} [handshake] InputEventMsgInProto handshake
                         * @property {org.webswing.server.model.proto.IKeyboardEventMsgInProto|null} [key] InputEventMsgInProto key
                         * @property {org.webswing.server.model.proto.IMouseEventMsgInProto|null} [mouse] InputEventMsgInProto mouse
                         * @property {org.webswing.server.model.proto.ISimpleEventMsgInProto|null} [event] InputEventMsgInProto event
                         * @property {org.webswing.server.model.proto.ITimestampsMsgInProto|null} [timestamps] InputEventMsgInProto timestamps
                         * @property {org.webswing.server.model.proto.IWindowFocusMsgInProto|null} [focus] InputEventMsgInProto focus
                         */

                        /**
                         * Constructs a new InputEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents an InputEventMsgInProto.
                         * @implements IInputEventMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IInputEventMsgInProto=} [properties] Properties to set
                         */
                        function InputEventMsgInProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * InputEventMsgInProto handshake.
                         * @member {org.webswing.server.model.proto.IConnectionHandshakeMsgInProto|null|undefined} handshake
                         * @memberof org.webswing.server.model.proto.InputEventMsgInProto
                         * @instance
                         */
                        InputEventMsgInProto.prototype.handshake = null;

                        /**
                         * InputEventMsgInProto key.
                         * @member {org.webswing.server.model.proto.IKeyboardEventMsgInProto|null|undefined} key
                         * @memberof org.webswing.server.model.proto.InputEventMsgInProto
                         * @instance
                         */
                        InputEventMsgInProto.prototype.key = null;

                        /**
                         * InputEventMsgInProto mouse.
                         * @member {org.webswing.server.model.proto.IMouseEventMsgInProto|null|undefined} mouse
                         * @memberof org.webswing.server.model.proto.InputEventMsgInProto
                         * @instance
                         */
                        InputEventMsgInProto.prototype.mouse = null;

                        /**
                         * InputEventMsgInProto event.
                         * @member {org.webswing.server.model.proto.ISimpleEventMsgInProto|null|undefined} event
                         * @memberof org.webswing.server.model.proto.InputEventMsgInProto
                         * @instance
                         */
                        InputEventMsgInProto.prototype.event = null;

                        /**
                         * InputEventMsgInProto timestamps.
                         * @member {org.webswing.server.model.proto.ITimestampsMsgInProto|null|undefined} timestamps
                         * @memberof org.webswing.server.model.proto.InputEventMsgInProto
                         * @instance
                         */
                        InputEventMsgInProto.prototype.timestamps = null;

                        /**
                         * InputEventMsgInProto focus.
                         * @member {org.webswing.server.model.proto.IWindowFocusMsgInProto|null|undefined} focus
                         * @memberof org.webswing.server.model.proto.InputEventMsgInProto
                         * @instance
                         */
                        InputEventMsgInProto.prototype.focus = null;

                        /**
                         * Creates a new InputEventMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.InputEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IInputEventMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.InputEventMsgInProto} InputEventMsgInProto instance
                         */
                        InputEventMsgInProto.create = function create(properties) {
                            return new InputEventMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified InputEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.InputEventMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.InputEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IInputEventMsgInProto} message InputEventMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        InputEventMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.handshake != null && message.hasOwnProperty("handshake"))
                                $root.org.webswing.server.model.proto.ConnectionHandshakeMsgInProto.encode(message.handshake, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
                            if (message.key != null && message.hasOwnProperty("key"))
                                $root.org.webswing.server.model.proto.KeyboardEventMsgInProto.encode(message.key, writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
                            if (message.mouse != null && message.hasOwnProperty("mouse"))
                                $root.org.webswing.server.model.proto.MouseEventMsgInProto.encode(message.mouse, writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
                            if (message.event != null && message.hasOwnProperty("event"))
                                $root.org.webswing.server.model.proto.SimpleEventMsgInProto.encode(message.event, writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
                            if (message.timestamps != null && message.hasOwnProperty("timestamps"))
                                $root.org.webswing.server.model.proto.TimestampsMsgInProto.encode(message.timestamps, writer.uint32(/* id 5, wireType 2 =*/42).fork()).ldelim();
                            if (message.focus != null && message.hasOwnProperty("focus"))
                                $root.org.webswing.server.model.proto.WindowFocusMsgInProto.encode(message.focus, writer.uint32(/* id 6, wireType 2 =*/50).fork()).ldelim();
                            return writer;
                        };

                        /**
                         * Decodes an InputEventMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.InputEventMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.InputEventMsgInProto} InputEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        InputEventMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.InputEventMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.handshake = $root.org.webswing.server.model.proto.ConnectionHandshakeMsgInProto.decode(reader, reader.uint32());
                                    break;
                                case 2:
                                    message.key = $root.org.webswing.server.model.proto.KeyboardEventMsgInProto.decode(reader, reader.uint32());
                                    break;
                                case 3:
                                    message.mouse = $root.org.webswing.server.model.proto.MouseEventMsgInProto.decode(reader, reader.uint32());
                                    break;
                                case 4:
                                    message.event = $root.org.webswing.server.model.proto.SimpleEventMsgInProto.decode(reader, reader.uint32());
                                    break;
                                case 5:
                                    message.timestamps = $root.org.webswing.server.model.proto.TimestampsMsgInProto.decode(reader, reader.uint32());
                                    break;
                                case 6:
                                    message.focus = $root.org.webswing.server.model.proto.WindowFocusMsgInProto.decode(reader, reader.uint32());
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates an InputEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.InputEventMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.InputEventMsgInProto} InputEventMsgInProto
                         */
                        InputEventMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.InputEventMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.InputEventMsgInProto();
                            if (object.handshake != null) {
                                if (typeof object.handshake !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventMsgInProto.handshake: object expected");
                                message.handshake = $root.org.webswing.server.model.proto.ConnectionHandshakeMsgInProto.fromObject(object.handshake);
                            }
                            if (object.key != null) {
                                if (typeof object.key !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventMsgInProto.key: object expected");
                                message.key = $root.org.webswing.server.model.proto.KeyboardEventMsgInProto.fromObject(object.key);
                            }
                            if (object.mouse != null) {
                                if (typeof object.mouse !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventMsgInProto.mouse: object expected");
                                message.mouse = $root.org.webswing.server.model.proto.MouseEventMsgInProto.fromObject(object.mouse);
                            }
                            if (object.event != null) {
                                if (typeof object.event !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventMsgInProto.event: object expected");
                                message.event = $root.org.webswing.server.model.proto.SimpleEventMsgInProto.fromObject(object.event);
                            }
                            if (object.timestamps != null) {
                                if (typeof object.timestamps !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventMsgInProto.timestamps: object expected");
                                message.timestamps = $root.org.webswing.server.model.proto.TimestampsMsgInProto.fromObject(object.timestamps);
                            }
                            if (object.focus != null) {
                                if (typeof object.focus !== "object")
                                    throw TypeError(".org.webswing.server.model.proto.InputEventMsgInProto.focus: object expected");
                                message.focus = $root.org.webswing.server.model.proto.WindowFocusMsgInProto.fromObject(object.focus);
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from an InputEventMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.InputEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.InputEventMsgInProto} message InputEventMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        InputEventMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.handshake = null;
                                object.key = null;
                                object.mouse = null;
                                object.event = null;
                                object.timestamps = null;
                                object.focus = null;
                            }
                            if (message.handshake != null && message.hasOwnProperty("handshake"))
                                object.handshake = $root.org.webswing.server.model.proto.ConnectionHandshakeMsgInProto.toObject(message.handshake, options);
                            if (message.key != null && message.hasOwnProperty("key"))
                                object.key = $root.org.webswing.server.model.proto.KeyboardEventMsgInProto.toObject(message.key, options);
                            if (message.mouse != null && message.hasOwnProperty("mouse"))
                                object.mouse = $root.org.webswing.server.model.proto.MouseEventMsgInProto.toObject(message.mouse, options);
                            if (message.event != null && message.hasOwnProperty("event"))
                                object.event = $root.org.webswing.server.model.proto.SimpleEventMsgInProto.toObject(message.event, options);
                            if (message.timestamps != null && message.hasOwnProperty("timestamps"))
                                object.timestamps = $root.org.webswing.server.model.proto.TimestampsMsgInProto.toObject(message.timestamps, options);
                            if (message.focus != null && message.hasOwnProperty("focus"))
                                object.focus = $root.org.webswing.server.model.proto.WindowFocusMsgInProto.toObject(message.focus, options);
                            return object;
                        };

                        /**
                         * Converts this InputEventMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.InputEventMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        InputEventMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return InputEventMsgInProto;
                    })();

                    proto.WindowFocusMsgInProto = (function() {

                        /**
                         * Properties of a WindowFocusMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IWindowFocusMsgInProto
                         * @property {string|null} [windowId] WindowFocusMsgInProto windowId
                         * @property {string|null} [htmlPanelId] WindowFocusMsgInProto htmlPanelId
                         */

                        /**
                         * Constructs a new WindowFocusMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a WindowFocusMsgInProto.
                         * @implements IWindowFocusMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IWindowFocusMsgInProto=} [properties] Properties to set
                         */
                        function WindowFocusMsgInProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * WindowFocusMsgInProto windowId.
                         * @member {string} windowId
                         * @memberof org.webswing.server.model.proto.WindowFocusMsgInProto
                         * @instance
                         */
                        WindowFocusMsgInProto.prototype.windowId = "";

                        /**
                         * WindowFocusMsgInProto htmlPanelId.
                         * @member {string} htmlPanelId
                         * @memberof org.webswing.server.model.proto.WindowFocusMsgInProto
                         * @instance
                         */
                        WindowFocusMsgInProto.prototype.htmlPanelId = "";

                        /**
                         * Creates a new WindowFocusMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.WindowFocusMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowFocusMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.WindowFocusMsgInProto} WindowFocusMsgInProto instance
                         */
                        WindowFocusMsgInProto.create = function create(properties) {
                            return new WindowFocusMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified WindowFocusMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowFocusMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.WindowFocusMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowFocusMsgInProto} message WindowFocusMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        WindowFocusMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.windowId != null && message.hasOwnProperty("windowId"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.windowId);
                            if (message.htmlPanelId != null && message.hasOwnProperty("htmlPanelId"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.htmlPanelId);
                            return writer;
                        };

                        /**
                         * Decodes a WindowFocusMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.WindowFocusMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.WindowFocusMsgInProto} WindowFocusMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        WindowFocusMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.WindowFocusMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.windowId = reader.string();
                                    break;
                                case 2:
                                    message.htmlPanelId = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a WindowFocusMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.WindowFocusMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.WindowFocusMsgInProto} WindowFocusMsgInProto
                         */
                        WindowFocusMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.WindowFocusMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.WindowFocusMsgInProto();
                            if (object.windowId != null)
                                message.windowId = String(object.windowId);
                            if (object.htmlPanelId != null)
                                message.htmlPanelId = String(object.htmlPanelId);
                            return message;
                        };

                        /**
                         * Creates a plain object from a WindowFocusMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.WindowFocusMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.WindowFocusMsgInProto} message WindowFocusMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        WindowFocusMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.windowId = "";
                                object.htmlPanelId = "";
                            }
                            if (message.windowId != null && message.hasOwnProperty("windowId"))
                                object.windowId = message.windowId;
                            if (message.htmlPanelId != null && message.hasOwnProperty("htmlPanelId"))
                                object.htmlPanelId = message.htmlPanelId;
                            return object;
                        };

                        /**
                         * Converts this WindowFocusMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.WindowFocusMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        WindowFocusMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return WindowFocusMsgInProto;
                    })();

                    proto.TimestampsMsgInProto = (function() {

                        /**
                         * Properties of a TimestampsMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface ITimestampsMsgInProto
                         * @property {string|null} [startTimestamp] TimestampsMsgInProto startTimestamp
                         * @property {string|null} [sendTimestamp] TimestampsMsgInProto sendTimestamp
                         * @property {string|null} [renderingTime] TimestampsMsgInProto renderingTime
                         * @property {number|null} [ping] TimestampsMsgInProto ping
                         */

                        /**
                         * Constructs a new TimestampsMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a TimestampsMsgInProto.
                         * @implements ITimestampsMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.ITimestampsMsgInProto=} [properties] Properties to set
                         */
                        function TimestampsMsgInProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * TimestampsMsgInProto startTimestamp.
                         * @member {string} startTimestamp
                         * @memberof org.webswing.server.model.proto.TimestampsMsgInProto
                         * @instance
                         */
                        TimestampsMsgInProto.prototype.startTimestamp = "";

                        /**
                         * TimestampsMsgInProto sendTimestamp.
                         * @member {string} sendTimestamp
                         * @memberof org.webswing.server.model.proto.TimestampsMsgInProto
                         * @instance
                         */
                        TimestampsMsgInProto.prototype.sendTimestamp = "";

                        /**
                         * TimestampsMsgInProto renderingTime.
                         * @member {string} renderingTime
                         * @memberof org.webswing.server.model.proto.TimestampsMsgInProto
                         * @instance
                         */
                        TimestampsMsgInProto.prototype.renderingTime = "";

                        /**
                         * TimestampsMsgInProto ping.
                         * @member {number} ping
                         * @memberof org.webswing.server.model.proto.TimestampsMsgInProto
                         * @instance
                         */
                        TimestampsMsgInProto.prototype.ping = 0;

                        /**
                         * Creates a new TimestampsMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.TimestampsMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.ITimestampsMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.TimestampsMsgInProto} TimestampsMsgInProto instance
                         */
                        TimestampsMsgInProto.create = function create(properties) {
                            return new TimestampsMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified TimestampsMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.TimestampsMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.TimestampsMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.ITimestampsMsgInProto} message TimestampsMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        TimestampsMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.startTimestamp != null && message.hasOwnProperty("startTimestamp"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.startTimestamp);
                            if (message.sendTimestamp != null && message.hasOwnProperty("sendTimestamp"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.sendTimestamp);
                            if (message.renderingTime != null && message.hasOwnProperty("renderingTime"))
                                writer.uint32(/* id 3, wireType 2 =*/26).string(message.renderingTime);
                            if (message.ping != null && message.hasOwnProperty("ping"))
                                writer.uint32(/* id 4, wireType 0 =*/32).uint32(message.ping);
                            return writer;
                        };

                        /**
                         * Decodes a TimestampsMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.TimestampsMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.TimestampsMsgInProto} TimestampsMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        TimestampsMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.TimestampsMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.startTimestamp = reader.string();
                                    break;
                                case 2:
                                    message.sendTimestamp = reader.string();
                                    break;
                                case 3:
                                    message.renderingTime = reader.string();
                                    break;
                                case 4:
                                    message.ping = reader.uint32();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a TimestampsMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.TimestampsMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.TimestampsMsgInProto} TimestampsMsgInProto
                         */
                        TimestampsMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.TimestampsMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.TimestampsMsgInProto();
                            if (object.startTimestamp != null)
                                message.startTimestamp = String(object.startTimestamp);
                            if (object.sendTimestamp != null)
                                message.sendTimestamp = String(object.sendTimestamp);
                            if (object.renderingTime != null)
                                message.renderingTime = String(object.renderingTime);
                            if (object.ping != null)
                                message.ping = object.ping >>> 0;
                            return message;
                        };

                        /**
                         * Creates a plain object from a TimestampsMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.TimestampsMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.TimestampsMsgInProto} message TimestampsMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        TimestampsMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.startTimestamp = "";
                                object.sendTimestamp = "";
                                object.renderingTime = "";
                                object.ping = 0;
                            }
                            if (message.startTimestamp != null && message.hasOwnProperty("startTimestamp"))
                                object.startTimestamp = message.startTimestamp;
                            if (message.sendTimestamp != null && message.hasOwnProperty("sendTimestamp"))
                                object.sendTimestamp = message.sendTimestamp;
                            if (message.renderingTime != null && message.hasOwnProperty("renderingTime"))
                                object.renderingTime = message.renderingTime;
                            if (message.ping != null && message.hasOwnProperty("ping"))
                                object.ping = message.ping;
                            return object;
                        };

                        /**
                         * Converts this TimestampsMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.TimestampsMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        TimestampsMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return TimestampsMsgInProto;
                    })();

                    proto.ConnectionHandshakeMsgInProto = (function() {

                        /**
                         * Properties of a ConnectionHandshakeMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IConnectionHandshakeMsgInProto
                         * @property {string|null} [clientId] ConnectionHandshakeMsgInProto clientId
                         * @property {string|null} [connectionId] ConnectionHandshakeMsgInProto connectionId
                         * @property {string|null} [viewId] ConnectionHandshakeMsgInProto viewId
                         * @property {string|null} [browserId] ConnectionHandshakeMsgInProto browserId
                         * @property {number|null} [desktopWidth] ConnectionHandshakeMsgInProto desktopWidth
                         * @property {number|null} [desktopHeight] ConnectionHandshakeMsgInProto desktopHeight
                         * @property {string|null} [applicationName] ConnectionHandshakeMsgInProto applicationName
                         * @property {boolean|null} [mirrored] ConnectionHandshakeMsgInProto mirrored
                         * @property {boolean|null} [directDrawSupported] ConnectionHandshakeMsgInProto directDrawSupported
                         * @property {string|null} [documentBase] ConnectionHandshakeMsgInProto documentBase
                         * @property {Array.<org.webswing.server.model.proto.IParamMsgProto>|null} [params] ConnectionHandshakeMsgInProto params
                         * @property {string|null} [locale] ConnectionHandshakeMsgInProto locale
                         * @property {string|null} [url] ConnectionHandshakeMsgInProto url
                         * @property {string|null} [timeZone] ConnectionHandshakeMsgInProto timeZone
                         * @property {boolean|null} [dockingSupported] ConnectionHandshakeMsgInProto dockingSupported
                         * @property {boolean|null} [touchMode] ConnectionHandshakeMsgInProto touchMode
                         * @property {boolean|null} [accessiblityEnabled] ConnectionHandshakeMsgInProto accessiblityEnabled
                         */

                        /**
                         * Constructs a new ConnectionHandshakeMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a ConnectionHandshakeMsgInProto.
                         * @implements IConnectionHandshakeMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IConnectionHandshakeMsgInProto=} [properties] Properties to set
                         */
                        function ConnectionHandshakeMsgInProto(properties) {
                            this.params = [];
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * ConnectionHandshakeMsgInProto clientId.
                         * @member {string} clientId
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.clientId = "";

                        /**
                         * ConnectionHandshakeMsgInProto connectionId.
                         * @member {string} connectionId
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.connectionId = "";

                        /**
                         * ConnectionHandshakeMsgInProto viewId.
                         * @member {string} viewId
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.viewId = "";

                        /**
                         * ConnectionHandshakeMsgInProto browserId.
                         * @member {string} browserId
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.browserId = "";

                        /**
                         * ConnectionHandshakeMsgInProto desktopWidth.
                         * @member {number} desktopWidth
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.desktopWidth = 0;

                        /**
                         * ConnectionHandshakeMsgInProto desktopHeight.
                         * @member {number} desktopHeight
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.desktopHeight = 0;

                        /**
                         * ConnectionHandshakeMsgInProto applicationName.
                         * @member {string} applicationName
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.applicationName = "";

                        /**
                         * ConnectionHandshakeMsgInProto mirrored.
                         * @member {boolean} mirrored
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.mirrored = false;

                        /**
                         * ConnectionHandshakeMsgInProto directDrawSupported.
                         * @member {boolean} directDrawSupported
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.directDrawSupported = false;

                        /**
                         * ConnectionHandshakeMsgInProto documentBase.
                         * @member {string} documentBase
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.documentBase = "";

                        /**
                         * ConnectionHandshakeMsgInProto params.
                         * @member {Array.<org.webswing.server.model.proto.IParamMsgProto>} params
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.params = $util.emptyArray;

                        /**
                         * ConnectionHandshakeMsgInProto locale.
                         * @member {string} locale
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.locale = "";

                        /**
                         * ConnectionHandshakeMsgInProto url.
                         * @member {string} url
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.url = "";

                        /**
                         * ConnectionHandshakeMsgInProto timeZone.
                         * @member {string} timeZone
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.timeZone = "";

                        /**
                         * ConnectionHandshakeMsgInProto dockingSupported.
                         * @member {boolean} dockingSupported
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.dockingSupported = false;

                        /**
                         * ConnectionHandshakeMsgInProto touchMode.
                         * @member {boolean} touchMode
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.touchMode = false;

                        /**
                         * ConnectionHandshakeMsgInProto accessiblityEnabled.
                         * @member {boolean} accessiblityEnabled
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         */
                        ConnectionHandshakeMsgInProto.prototype.accessiblityEnabled = false;

                        /**
                         * Creates a new ConnectionHandshakeMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IConnectionHandshakeMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.ConnectionHandshakeMsgInProto} ConnectionHandshakeMsgInProto instance
                         */
                        ConnectionHandshakeMsgInProto.create = function create(properties) {
                            return new ConnectionHandshakeMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified ConnectionHandshakeMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.ConnectionHandshakeMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IConnectionHandshakeMsgInProto} message ConnectionHandshakeMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        ConnectionHandshakeMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.clientId != null && message.hasOwnProperty("clientId"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.clientId);
                            if (message.connectionId != null && message.hasOwnProperty("connectionId"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.connectionId);
                            if (message.viewId != null && message.hasOwnProperty("viewId"))
                                writer.uint32(/* id 3, wireType 2 =*/26).string(message.viewId);
                            if (message.desktopWidth != null && message.hasOwnProperty("desktopWidth"))
                                writer.uint32(/* id 4, wireType 0 =*/32).uint32(message.desktopWidth);
                            if (message.desktopHeight != null && message.hasOwnProperty("desktopHeight"))
                                writer.uint32(/* id 5, wireType 0 =*/40).uint32(message.desktopHeight);
                            if (message.applicationName != null && message.hasOwnProperty("applicationName"))
                                writer.uint32(/* id 6, wireType 2 =*/50).string(message.applicationName);
                            if (message.mirrored != null && message.hasOwnProperty("mirrored"))
                                writer.uint32(/* id 7, wireType 0 =*/56).bool(message.mirrored);
                            if (message.directDrawSupported != null && message.hasOwnProperty("directDrawSupported"))
                                writer.uint32(/* id 8, wireType 0 =*/64).bool(message.directDrawSupported);
                            if (message.documentBase != null && message.hasOwnProperty("documentBase"))
                                writer.uint32(/* id 9, wireType 2 =*/74).string(message.documentBase);
                            if (message.params != null && message.params.length)
                                for (var i = 0; i < message.params.length; ++i)
                                    $root.org.webswing.server.model.proto.ParamMsgProto.encode(message.params[i], writer.uint32(/* id 10, wireType 2 =*/82).fork()).ldelim();
                            if (message.locale != null && message.hasOwnProperty("locale"))
                                writer.uint32(/* id 11, wireType 2 =*/90).string(message.locale);
                            if (message.url != null && message.hasOwnProperty("url"))
                                writer.uint32(/* id 12, wireType 2 =*/98).string(message.url);
                            if (message.browserId != null && message.hasOwnProperty("browserId"))
                                writer.uint32(/* id 13, wireType 2 =*/106).string(message.browserId);
                            if (message.timeZone != null && message.hasOwnProperty("timeZone"))
                                writer.uint32(/* id 14, wireType 2 =*/114).string(message.timeZone);
                            if (message.dockingSupported != null && message.hasOwnProperty("dockingSupported"))
                                writer.uint32(/* id 15, wireType 0 =*/120).bool(message.dockingSupported);
                            if (message.touchMode != null && message.hasOwnProperty("touchMode"))
                                writer.uint32(/* id 16, wireType 0 =*/128).bool(message.touchMode);
                            if (message.accessiblityEnabled != null && message.hasOwnProperty("accessiblityEnabled"))
                                writer.uint32(/* id 17, wireType 0 =*/136).bool(message.accessiblityEnabled);
                            return writer;
                        };

                        /**
                         * Decodes a ConnectionHandshakeMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.ConnectionHandshakeMsgInProto} ConnectionHandshakeMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        ConnectionHandshakeMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.ConnectionHandshakeMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.clientId = reader.string();
                                    break;
                                case 2:
                                    message.connectionId = reader.string();
                                    break;
                                case 3:
                                    message.viewId = reader.string();
                                    break;
                                case 13:
                                    message.browserId = reader.string();
                                    break;
                                case 4:
                                    message.desktopWidth = reader.uint32();
                                    break;
                                case 5:
                                    message.desktopHeight = reader.uint32();
                                    break;
                                case 6:
                                    message.applicationName = reader.string();
                                    break;
                                case 7:
                                    message.mirrored = reader.bool();
                                    break;
                                case 8:
                                    message.directDrawSupported = reader.bool();
                                    break;
                                case 9:
                                    message.documentBase = reader.string();
                                    break;
                                case 10:
                                    if (!(message.params && message.params.length))
                                        message.params = [];
                                    message.params.push($root.org.webswing.server.model.proto.ParamMsgProto.decode(reader, reader.uint32()));
                                    break;
                                case 11:
                                    message.locale = reader.string();
                                    break;
                                case 12:
                                    message.url = reader.string();
                                    break;
                                case 14:
                                    message.timeZone = reader.string();
                                    break;
                                case 15:
                                    message.dockingSupported = reader.bool();
                                    break;
                                case 16:
                                    message.touchMode = reader.bool();
                                    break;
                                case 17:
                                    message.accessiblityEnabled = reader.bool();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a ConnectionHandshakeMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.ConnectionHandshakeMsgInProto} ConnectionHandshakeMsgInProto
                         */
                        ConnectionHandshakeMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.ConnectionHandshakeMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.ConnectionHandshakeMsgInProto();
                            if (object.clientId != null)
                                message.clientId = String(object.clientId);
                            if (object.connectionId != null)
                                message.connectionId = String(object.connectionId);
                            if (object.viewId != null)
                                message.viewId = String(object.viewId);
                            if (object.browserId != null)
                                message.browserId = String(object.browserId);
                            if (object.desktopWidth != null)
                                message.desktopWidth = object.desktopWidth >>> 0;
                            if (object.desktopHeight != null)
                                message.desktopHeight = object.desktopHeight >>> 0;
                            if (object.applicationName != null)
                                message.applicationName = String(object.applicationName);
                            if (object.mirrored != null)
                                message.mirrored = Boolean(object.mirrored);
                            if (object.directDrawSupported != null)
                                message.directDrawSupported = Boolean(object.directDrawSupported);
                            if (object.documentBase != null)
                                message.documentBase = String(object.documentBase);
                            if (object.params) {
                                if (!Array.isArray(object.params))
                                    throw TypeError(".org.webswing.server.model.proto.ConnectionHandshakeMsgInProto.params: array expected");
                                message.params = [];
                                for (var i = 0; i < object.params.length; ++i) {
                                    if (typeof object.params[i] !== "object")
                                        throw TypeError(".org.webswing.server.model.proto.ConnectionHandshakeMsgInProto.params: object expected");
                                    message.params[i] = $root.org.webswing.server.model.proto.ParamMsgProto.fromObject(object.params[i]);
                                }
                            }
                            if (object.locale != null)
                                message.locale = String(object.locale);
                            if (object.url != null)
                                message.url = String(object.url);
                            if (object.timeZone != null)
                                message.timeZone = String(object.timeZone);
                            if (object.dockingSupported != null)
                                message.dockingSupported = Boolean(object.dockingSupported);
                            if (object.touchMode != null)
                                message.touchMode = Boolean(object.touchMode);
                            if (object.accessiblityEnabled != null)
                                message.accessiblityEnabled = Boolean(object.accessiblityEnabled);
                            return message;
                        };

                        /**
                         * Creates a plain object from a ConnectionHandshakeMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.ConnectionHandshakeMsgInProto} message ConnectionHandshakeMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        ConnectionHandshakeMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.arrays || options.defaults)
                                object.params = [];
                            if (options.defaults) {
                                object.clientId = "";
                                object.connectionId = "";
                                object.viewId = "";
                                object.desktopWidth = 0;
                                object.desktopHeight = 0;
                                object.applicationName = "";
                                object.mirrored = false;
                                object.directDrawSupported = false;
                                object.documentBase = "";
                                object.locale = "";
                                object.url = "";
                                object.browserId = "";
                                object.timeZone = "";
                                object.dockingSupported = false;
                                object.touchMode = false;
                                object.accessiblityEnabled = false;
                            }
                            if (message.clientId != null && message.hasOwnProperty("clientId"))
                                object.clientId = message.clientId;
                            if (message.connectionId != null && message.hasOwnProperty("connectionId"))
                                object.connectionId = message.connectionId;
                            if (message.viewId != null && message.hasOwnProperty("viewId"))
                                object.viewId = message.viewId;
                            if (message.desktopWidth != null && message.hasOwnProperty("desktopWidth"))
                                object.desktopWidth = message.desktopWidth;
                            if (message.desktopHeight != null && message.hasOwnProperty("desktopHeight"))
                                object.desktopHeight = message.desktopHeight;
                            if (message.applicationName != null && message.hasOwnProperty("applicationName"))
                                object.applicationName = message.applicationName;
                            if (message.mirrored != null && message.hasOwnProperty("mirrored"))
                                object.mirrored = message.mirrored;
                            if (message.directDrawSupported != null && message.hasOwnProperty("directDrawSupported"))
                                object.directDrawSupported = message.directDrawSupported;
                            if (message.documentBase != null && message.hasOwnProperty("documentBase"))
                                object.documentBase = message.documentBase;
                            if (message.params && message.params.length) {
                                object.params = [];
                                for (var j = 0; j < message.params.length; ++j)
                                    object.params[j] = $root.org.webswing.server.model.proto.ParamMsgProto.toObject(message.params[j], options);
                            }
                            if (message.locale != null && message.hasOwnProperty("locale"))
                                object.locale = message.locale;
                            if (message.url != null && message.hasOwnProperty("url"))
                                object.url = message.url;
                            if (message.browserId != null && message.hasOwnProperty("browserId"))
                                object.browserId = message.browserId;
                            if (message.timeZone != null && message.hasOwnProperty("timeZone"))
                                object.timeZone = message.timeZone;
                            if (message.dockingSupported != null && message.hasOwnProperty("dockingSupported"))
                                object.dockingSupported = message.dockingSupported;
                            if (message.touchMode != null && message.hasOwnProperty("touchMode"))
                                object.touchMode = message.touchMode;
                            if (message.accessiblityEnabled != null && message.hasOwnProperty("accessiblityEnabled"))
                                object.accessiblityEnabled = message.accessiblityEnabled;
                            return object;
                        };

                        /**
                         * Converts this ConnectionHandshakeMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.ConnectionHandshakeMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        ConnectionHandshakeMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return ConnectionHandshakeMsgInProto;
                    })();

                    proto.ParamMsgProto = (function() {

                        /**
                         * Properties of a ParamMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IParamMsgProto
                         * @property {string|null} [name] ParamMsgProto name
                         * @property {string|null} [value] ParamMsgProto value
                         */

                        /**
                         * Constructs a new ParamMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a ParamMsgProto.
                         * @implements IParamMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IParamMsgProto=} [properties] Properties to set
                         */
                        function ParamMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * ParamMsgProto name.
                         * @member {string} name
                         * @memberof org.webswing.server.model.proto.ParamMsgProto
                         * @instance
                         */
                        ParamMsgProto.prototype.name = "";

                        /**
                         * ParamMsgProto value.
                         * @member {string} value
                         * @memberof org.webswing.server.model.proto.ParamMsgProto
                         * @instance
                         */
                        ParamMsgProto.prototype.value = "";

                        /**
                         * Creates a new ParamMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.ParamMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IParamMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.ParamMsgProto} ParamMsgProto instance
                         */
                        ParamMsgProto.create = function create(properties) {
                            return new ParamMsgProto(properties);
                        };

                        /**
                         * Encodes the specified ParamMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.ParamMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.ParamMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IParamMsgProto} message ParamMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        ParamMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.name != null && message.hasOwnProperty("name"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.name);
                            if (message.value != null && message.hasOwnProperty("value"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.value);
                            return writer;
                        };

                        /**
                         * Decodes a ParamMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.ParamMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.ParamMsgProto} ParamMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        ParamMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.ParamMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.name = reader.string();
                                    break;
                                case 2:
                                    message.value = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a ParamMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.ParamMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.ParamMsgProto} ParamMsgProto
                         */
                        ParamMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.ParamMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.ParamMsgProto();
                            if (object.name != null)
                                message.name = String(object.name);
                            if (object.value != null)
                                message.value = String(object.value);
                            return message;
                        };

                        /**
                         * Creates a plain object from a ParamMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.ParamMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.ParamMsgProto} message ParamMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        ParamMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.name = "";
                                object.value = "";
                            }
                            if (message.name != null && message.hasOwnProperty("name"))
                                object.name = message.name;
                            if (message.value != null && message.hasOwnProperty("value"))
                                object.value = message.value;
                            return object;
                        };

                        /**
                         * Converts this ParamMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.ParamMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        ParamMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return ParamMsgProto;
                    })();

                    proto.KeyboardEventMsgInProto = (function() {

                        /**
                         * Properties of a KeyboardEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IKeyboardEventMsgInProto
                         * @property {org.webswing.server.model.proto.KeyboardEventMsgInProto.KeyEventTypeProto|null} [type] KeyboardEventMsgInProto type
                         * @property {number|null} [character] KeyboardEventMsgInProto character
                         * @property {number|null} [keycode] KeyboardEventMsgInProto keycode
                         * @property {boolean|null} [alt] KeyboardEventMsgInProto alt
                         * @property {boolean|null} [ctrl] KeyboardEventMsgInProto ctrl
                         * @property {boolean|null} [shift] KeyboardEventMsgInProto shift
                         * @property {boolean|null} [meta] KeyboardEventMsgInProto meta
                         */

                        /**
                         * Constructs a new KeyboardEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a KeyboardEventMsgInProto.
                         * @implements IKeyboardEventMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IKeyboardEventMsgInProto=} [properties] Properties to set
                         */
                        function KeyboardEventMsgInProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * KeyboardEventMsgInProto type.
                         * @member {org.webswing.server.model.proto.KeyboardEventMsgInProto.KeyEventTypeProto} type
                         * @memberof org.webswing.server.model.proto.KeyboardEventMsgInProto
                         * @instance
                         */
                        KeyboardEventMsgInProto.prototype.type = 0;

                        /**
                         * KeyboardEventMsgInProto character.
                         * @member {number} character
                         * @memberof org.webswing.server.model.proto.KeyboardEventMsgInProto
                         * @instance
                         */
                        KeyboardEventMsgInProto.prototype.character = 0;

                        /**
                         * KeyboardEventMsgInProto keycode.
                         * @member {number} keycode
                         * @memberof org.webswing.server.model.proto.KeyboardEventMsgInProto
                         * @instance
                         */
                        KeyboardEventMsgInProto.prototype.keycode = 0;

                        /**
                         * KeyboardEventMsgInProto alt.
                         * @member {boolean} alt
                         * @memberof org.webswing.server.model.proto.KeyboardEventMsgInProto
                         * @instance
                         */
                        KeyboardEventMsgInProto.prototype.alt = false;

                        /**
                         * KeyboardEventMsgInProto ctrl.
                         * @member {boolean} ctrl
                         * @memberof org.webswing.server.model.proto.KeyboardEventMsgInProto
                         * @instance
                         */
                        KeyboardEventMsgInProto.prototype.ctrl = false;

                        /**
                         * KeyboardEventMsgInProto shift.
                         * @member {boolean} shift
                         * @memberof org.webswing.server.model.proto.KeyboardEventMsgInProto
                         * @instance
                         */
                        KeyboardEventMsgInProto.prototype.shift = false;

                        /**
                         * KeyboardEventMsgInProto meta.
                         * @member {boolean} meta
                         * @memberof org.webswing.server.model.proto.KeyboardEventMsgInProto
                         * @instance
                         */
                        KeyboardEventMsgInProto.prototype.meta = false;

                        /**
                         * Creates a new KeyboardEventMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.KeyboardEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IKeyboardEventMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.KeyboardEventMsgInProto} KeyboardEventMsgInProto instance
                         */
                        KeyboardEventMsgInProto.create = function create(properties) {
                            return new KeyboardEventMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified KeyboardEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.KeyboardEventMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.KeyboardEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IKeyboardEventMsgInProto} message KeyboardEventMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        KeyboardEventMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.type != null && message.hasOwnProperty("type"))
                                writer.uint32(/* id 1, wireType 0 =*/8).int32(message.type);
                            if (message.character != null && message.hasOwnProperty("character"))
                                writer.uint32(/* id 2, wireType 0 =*/16).sint32(message.character);
                            if (message.keycode != null && message.hasOwnProperty("keycode"))
                                writer.uint32(/* id 3, wireType 0 =*/24).sint32(message.keycode);
                            if (message.alt != null && message.hasOwnProperty("alt"))
                                writer.uint32(/* id 4, wireType 0 =*/32).bool(message.alt);
                            if (message.ctrl != null && message.hasOwnProperty("ctrl"))
                                writer.uint32(/* id 5, wireType 0 =*/40).bool(message.ctrl);
                            if (message.shift != null && message.hasOwnProperty("shift"))
                                writer.uint32(/* id 6, wireType 0 =*/48).bool(message.shift);
                            if (message.meta != null && message.hasOwnProperty("meta"))
                                writer.uint32(/* id 7, wireType 0 =*/56).bool(message.meta);
                            return writer;
                        };

                        /**
                         * Decodes a KeyboardEventMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.KeyboardEventMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.KeyboardEventMsgInProto} KeyboardEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        KeyboardEventMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.KeyboardEventMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.type = reader.int32();
                                    break;
                                case 2:
                                    message.character = reader.sint32();
                                    break;
                                case 3:
                                    message.keycode = reader.sint32();
                                    break;
                                case 4:
                                    message.alt = reader.bool();
                                    break;
                                case 5:
                                    message.ctrl = reader.bool();
                                    break;
                                case 6:
                                    message.shift = reader.bool();
                                    break;
                                case 7:
                                    message.meta = reader.bool();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a KeyboardEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.KeyboardEventMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.KeyboardEventMsgInProto} KeyboardEventMsgInProto
                         */
                        KeyboardEventMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.KeyboardEventMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.KeyboardEventMsgInProto();
                            switch (object.type) {
                            case "keypress":
                            case 0:
                                message.type = 0;
                                break;
                            case "keydown":
                            case 1:
                                message.type = 1;
                                break;
                            case "keyup":
                            case 2:
                                message.type = 2;
                                break;
                            }
                            if (object.character != null)
                                message.character = object.character | 0;
                            if (object.keycode != null)
                                message.keycode = object.keycode | 0;
                            if (object.alt != null)
                                message.alt = Boolean(object.alt);
                            if (object.ctrl != null)
                                message.ctrl = Boolean(object.ctrl);
                            if (object.shift != null)
                                message.shift = Boolean(object.shift);
                            if (object.meta != null)
                                message.meta = Boolean(object.meta);
                            return message;
                        };

                        /**
                         * Creates a plain object from a KeyboardEventMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.KeyboardEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.KeyboardEventMsgInProto} message KeyboardEventMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        KeyboardEventMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.type = options.enums === String ? "keypress" : 0;
                                object.character = 0;
                                object.keycode = 0;
                                object.alt = false;
                                object.ctrl = false;
                                object.shift = false;
                                object.meta = false;
                            }
                            if (message.type != null && message.hasOwnProperty("type"))
                                object.type = options.enums === String ? $root.org.webswing.server.model.proto.KeyboardEventMsgInProto.KeyEventTypeProto[message.type] : message.type;
                            if (message.character != null && message.hasOwnProperty("character"))
                                object.character = message.character;
                            if (message.keycode != null && message.hasOwnProperty("keycode"))
                                object.keycode = message.keycode;
                            if (message.alt != null && message.hasOwnProperty("alt"))
                                object.alt = message.alt;
                            if (message.ctrl != null && message.hasOwnProperty("ctrl"))
                                object.ctrl = message.ctrl;
                            if (message.shift != null && message.hasOwnProperty("shift"))
                                object.shift = message.shift;
                            if (message.meta != null && message.hasOwnProperty("meta"))
                                object.meta = message.meta;
                            return object;
                        };

                        /**
                         * Converts this KeyboardEventMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.KeyboardEventMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        KeyboardEventMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        /**
                         * KeyEventTypeProto enum.
                         * @name org.webswing.server.model.proto.KeyboardEventMsgInProto.KeyEventTypeProto
                         * @enum {string}
                         * @property {number} keypress=0 keypress value
                         * @property {number} keydown=1 keydown value
                         * @property {number} keyup=2 keyup value
                         */
                        KeyboardEventMsgInProto.KeyEventTypeProto = (function() {
                            var valuesById = {}, values = Object.create(valuesById);
                            values[valuesById[0] = "keypress"] = 0;
                            values[valuesById[1] = "keydown"] = 1;
                            values[valuesById[2] = "keyup"] = 2;
                            return values;
                        })();

                        return KeyboardEventMsgInProto;
                    })();

                    proto.MouseEventMsgInProto = (function() {

                        /**
                         * Properties of a MouseEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IMouseEventMsgInProto
                         * @property {org.webswing.server.model.proto.MouseEventMsgInProto.MouseEventTypeProto|null} [type] MouseEventMsgInProto type
                         * @property {number|null} [x] MouseEventMsgInProto x
                         * @property {number|null} [y] MouseEventMsgInProto y
                         * @property {number|null} [wheelDelta] MouseEventMsgInProto wheelDelta
                         * @property {number|null} [button] MouseEventMsgInProto button
                         * @property {boolean|null} [ctrl] MouseEventMsgInProto ctrl
                         * @property {boolean|null} [alt] MouseEventMsgInProto alt
                         * @property {boolean|null} [shift] MouseEventMsgInProto shift
                         * @property {boolean|null} [meta] MouseEventMsgInProto meta
                         * @property {number|null} [buttons] MouseEventMsgInProto buttons
                         * @property {number|null} [timeMilis] MouseEventMsgInProto timeMilis
                         * @property {string|null} [winId] MouseEventMsgInProto winId
                         */

                        /**
                         * Constructs a new MouseEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a MouseEventMsgInProto.
                         * @implements IMouseEventMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IMouseEventMsgInProto=} [properties] Properties to set
                         */
                        function MouseEventMsgInProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * MouseEventMsgInProto type.
                         * @member {org.webswing.server.model.proto.MouseEventMsgInProto.MouseEventTypeProto} type
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @instance
                         */
                        MouseEventMsgInProto.prototype.type = 0;

                        /**
                         * MouseEventMsgInProto x.
                         * @member {number} x
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @instance
                         */
                        MouseEventMsgInProto.prototype.x = 0;

                        /**
                         * MouseEventMsgInProto y.
                         * @member {number} y
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @instance
                         */
                        MouseEventMsgInProto.prototype.y = 0;

                        /**
                         * MouseEventMsgInProto wheelDelta.
                         * @member {number} wheelDelta
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @instance
                         */
                        MouseEventMsgInProto.prototype.wheelDelta = 0;

                        /**
                         * MouseEventMsgInProto button.
                         * @member {number} button
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @instance
                         */
                        MouseEventMsgInProto.prototype.button = 0;

                        /**
                         * MouseEventMsgInProto ctrl.
                         * @member {boolean} ctrl
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @instance
                         */
                        MouseEventMsgInProto.prototype.ctrl = false;

                        /**
                         * MouseEventMsgInProto alt.
                         * @member {boolean} alt
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @instance
                         */
                        MouseEventMsgInProto.prototype.alt = false;

                        /**
                         * MouseEventMsgInProto shift.
                         * @member {boolean} shift
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @instance
                         */
                        MouseEventMsgInProto.prototype.shift = false;

                        /**
                         * MouseEventMsgInProto meta.
                         * @member {boolean} meta
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @instance
                         */
                        MouseEventMsgInProto.prototype.meta = false;

                        /**
                         * MouseEventMsgInProto buttons.
                         * @member {number} buttons
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @instance
                         */
                        MouseEventMsgInProto.prototype.buttons = 0;

                        /**
                         * MouseEventMsgInProto timeMilis.
                         * @member {number} timeMilis
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @instance
                         */
                        MouseEventMsgInProto.prototype.timeMilis = 0;

                        /**
                         * MouseEventMsgInProto winId.
                         * @member {string} winId
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @instance
                         */
                        MouseEventMsgInProto.prototype.winId = "";

                        /**
                         * Creates a new MouseEventMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IMouseEventMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.MouseEventMsgInProto} MouseEventMsgInProto instance
                         */
                        MouseEventMsgInProto.create = function create(properties) {
                            return new MouseEventMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified MouseEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.MouseEventMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IMouseEventMsgInProto} message MouseEventMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        MouseEventMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.type != null && message.hasOwnProperty("type"))
                                writer.uint32(/* id 1, wireType 0 =*/8).int32(message.type);
                            if (message.x != null && message.hasOwnProperty("x"))
                                writer.uint32(/* id 2, wireType 0 =*/16).sint32(message.x);
                            if (message.y != null && message.hasOwnProperty("y"))
                                writer.uint32(/* id 3, wireType 0 =*/24).sint32(message.y);
                            if (message.wheelDelta != null && message.hasOwnProperty("wheelDelta"))
                                writer.uint32(/* id 4, wireType 0 =*/32).sint32(message.wheelDelta);
                            if (message.button != null && message.hasOwnProperty("button"))
                                writer.uint32(/* id 5, wireType 0 =*/40).sint32(message.button);
                            if (message.ctrl != null && message.hasOwnProperty("ctrl"))
                                writer.uint32(/* id 6, wireType 0 =*/48).bool(message.ctrl);
                            if (message.alt != null && message.hasOwnProperty("alt"))
                                writer.uint32(/* id 7, wireType 0 =*/56).bool(message.alt);
                            if (message.shift != null && message.hasOwnProperty("shift"))
                                writer.uint32(/* id 8, wireType 0 =*/64).bool(message.shift);
                            if (message.meta != null && message.hasOwnProperty("meta"))
                                writer.uint32(/* id 9, wireType 0 =*/72).bool(message.meta);
                            if (message.buttons != null && message.hasOwnProperty("buttons"))
                                writer.uint32(/* id 10, wireType 0 =*/80).sint32(message.buttons);
                            if (message.timeMilis != null && message.hasOwnProperty("timeMilis"))
                                writer.uint32(/* id 11, wireType 0 =*/88).sint32(message.timeMilis);
                            if (message.winId != null && message.hasOwnProperty("winId"))
                                writer.uint32(/* id 12, wireType 2 =*/98).string(message.winId);
                            return writer;
                        };

                        /**
                         * Decodes a MouseEventMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.MouseEventMsgInProto} MouseEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        MouseEventMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.MouseEventMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.type = reader.int32();
                                    break;
                                case 2:
                                    message.x = reader.sint32();
                                    break;
                                case 3:
                                    message.y = reader.sint32();
                                    break;
                                case 4:
                                    message.wheelDelta = reader.sint32();
                                    break;
                                case 5:
                                    message.button = reader.sint32();
                                    break;
                                case 6:
                                    message.ctrl = reader.bool();
                                    break;
                                case 7:
                                    message.alt = reader.bool();
                                    break;
                                case 8:
                                    message.shift = reader.bool();
                                    break;
                                case 9:
                                    message.meta = reader.bool();
                                    break;
                                case 10:
                                    message.buttons = reader.sint32();
                                    break;
                                case 11:
                                    message.timeMilis = reader.sint32();
                                    break;
                                case 12:
                                    message.winId = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a MouseEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.MouseEventMsgInProto} MouseEventMsgInProto
                         */
                        MouseEventMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.MouseEventMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.MouseEventMsgInProto();
                            switch (object.type) {
                            case "mousemove":
                            case 0:
                                message.type = 0;
                                break;
                            case "mousedown":
                            case 1:
                                message.type = 1;
                                break;
                            case "mouseup":
                            case 2:
                                message.type = 2;
                                break;
                            case "mousewheel":
                            case 3:
                                message.type = 3;
                                break;
                            case "dblclick":
                            case 4:
                                message.type = 4;
                                break;
                            }
                            if (object.x != null)
                                message.x = object.x | 0;
                            if (object.y != null)
                                message.y = object.y | 0;
                            if (object.wheelDelta != null)
                                message.wheelDelta = object.wheelDelta | 0;
                            if (object.button != null)
                                message.button = object.button | 0;
                            if (object.ctrl != null)
                                message.ctrl = Boolean(object.ctrl);
                            if (object.alt != null)
                                message.alt = Boolean(object.alt);
                            if (object.shift != null)
                                message.shift = Boolean(object.shift);
                            if (object.meta != null)
                                message.meta = Boolean(object.meta);
                            if (object.buttons != null)
                                message.buttons = object.buttons | 0;
                            if (object.timeMilis != null)
                                message.timeMilis = object.timeMilis | 0;
                            if (object.winId != null)
                                message.winId = String(object.winId);
                            return message;
                        };

                        /**
                         * Creates a plain object from a MouseEventMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.MouseEventMsgInProto} message MouseEventMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        MouseEventMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.type = options.enums === String ? "mousemove" : 0;
                                object.x = 0;
                                object.y = 0;
                                object.wheelDelta = 0;
                                object.button = 0;
                                object.ctrl = false;
                                object.alt = false;
                                object.shift = false;
                                object.meta = false;
                                object.buttons = 0;
                                object.timeMilis = 0;
                                object.winId = "";
                            }
                            if (message.type != null && message.hasOwnProperty("type"))
                                object.type = options.enums === String ? $root.org.webswing.server.model.proto.MouseEventMsgInProto.MouseEventTypeProto[message.type] : message.type;
                            if (message.x != null && message.hasOwnProperty("x"))
                                object.x = message.x;
                            if (message.y != null && message.hasOwnProperty("y"))
                                object.y = message.y;
                            if (message.wheelDelta != null && message.hasOwnProperty("wheelDelta"))
                                object.wheelDelta = message.wheelDelta;
                            if (message.button != null && message.hasOwnProperty("button"))
                                object.button = message.button;
                            if (message.ctrl != null && message.hasOwnProperty("ctrl"))
                                object.ctrl = message.ctrl;
                            if (message.alt != null && message.hasOwnProperty("alt"))
                                object.alt = message.alt;
                            if (message.shift != null && message.hasOwnProperty("shift"))
                                object.shift = message.shift;
                            if (message.meta != null && message.hasOwnProperty("meta"))
                                object.meta = message.meta;
                            if (message.buttons != null && message.hasOwnProperty("buttons"))
                                object.buttons = message.buttons;
                            if (message.timeMilis != null && message.hasOwnProperty("timeMilis"))
                                object.timeMilis = message.timeMilis;
                            if (message.winId != null && message.hasOwnProperty("winId"))
                                object.winId = message.winId;
                            return object;
                        };

                        /**
                         * Converts this MouseEventMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.MouseEventMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        MouseEventMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        /**
                         * MouseEventTypeProto enum.
                         * @name org.webswing.server.model.proto.MouseEventMsgInProto.MouseEventTypeProto
                         * @enum {string}
                         * @property {number} mousemove=0 mousemove value
                         * @property {number} mousedown=1 mousedown value
                         * @property {number} mouseup=2 mouseup value
                         * @property {number} mousewheel=3 mousewheel value
                         * @property {number} dblclick=4 dblclick value
                         */
                        MouseEventMsgInProto.MouseEventTypeProto = (function() {
                            var valuesById = {}, values = Object.create(valuesById);
                            values[valuesById[0] = "mousemove"] = 0;
                            values[valuesById[1] = "mousedown"] = 1;
                            values[valuesById[2] = "mouseup"] = 2;
                            values[valuesById[3] = "mousewheel"] = 3;
                            values[valuesById[4] = "dblclick"] = 4;
                            return values;
                        })();

                        return MouseEventMsgInProto;
                    })();

                    proto.CopyEventMsgInProto = (function() {

                        /**
                         * Properties of a CopyEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface ICopyEventMsgInProto
                         * @property {org.webswing.server.model.proto.CopyEventMsgInProto.CopyEventMsgTypeProto|null} [type] CopyEventMsgInProto type
                         * @property {string|null} [file] CopyEventMsgInProto file
                         */

                        /**
                         * Constructs a new CopyEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a CopyEventMsgInProto.
                         * @implements ICopyEventMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.ICopyEventMsgInProto=} [properties] Properties to set
                         */
                        function CopyEventMsgInProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * CopyEventMsgInProto type.
                         * @member {org.webswing.server.model.proto.CopyEventMsgInProto.CopyEventMsgTypeProto} type
                         * @memberof org.webswing.server.model.proto.CopyEventMsgInProto
                         * @instance
                         */
                        CopyEventMsgInProto.prototype.type = 0;

                        /**
                         * CopyEventMsgInProto file.
                         * @member {string} file
                         * @memberof org.webswing.server.model.proto.CopyEventMsgInProto
                         * @instance
                         */
                        CopyEventMsgInProto.prototype.file = "";

                        /**
                         * Creates a new CopyEventMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.CopyEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.ICopyEventMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.CopyEventMsgInProto} CopyEventMsgInProto instance
                         */
                        CopyEventMsgInProto.create = function create(properties) {
                            return new CopyEventMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified CopyEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.CopyEventMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.CopyEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.ICopyEventMsgInProto} message CopyEventMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        CopyEventMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.type != null && message.hasOwnProperty("type"))
                                writer.uint32(/* id 1, wireType 0 =*/8).int32(message.type);
                            if (message.file != null && message.hasOwnProperty("file"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.file);
                            return writer;
                        };

                        /**
                         * Decodes a CopyEventMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.CopyEventMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.CopyEventMsgInProto} CopyEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        CopyEventMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.CopyEventMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.type = reader.int32();
                                    break;
                                case 2:
                                    message.file = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a CopyEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.CopyEventMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.CopyEventMsgInProto} CopyEventMsgInProto
                         */
                        CopyEventMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.CopyEventMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.CopyEventMsgInProto();
                            switch (object.type) {
                            case "copy":
                            case 0:
                                message.type = 0;
                                break;
                            case "cut":
                            case 1:
                                message.type = 1;
                                break;
                            case "getFileFromClipboard":
                            case 2:
                                message.type = 2;
                                break;
                            }
                            if (object.file != null)
                                message.file = String(object.file);
                            return message;
                        };

                        /**
                         * Creates a plain object from a CopyEventMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.CopyEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.CopyEventMsgInProto} message CopyEventMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        CopyEventMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.type = options.enums === String ? "copy" : 0;
                                object.file = "";
                            }
                            if (message.type != null && message.hasOwnProperty("type"))
                                object.type = options.enums === String ? $root.org.webswing.server.model.proto.CopyEventMsgInProto.CopyEventMsgTypeProto[message.type] : message.type;
                            if (message.file != null && message.hasOwnProperty("file"))
                                object.file = message.file;
                            return object;
                        };

                        /**
                         * Converts this CopyEventMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.CopyEventMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        CopyEventMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        /**
                         * CopyEventMsgTypeProto enum.
                         * @name org.webswing.server.model.proto.CopyEventMsgInProto.CopyEventMsgTypeProto
                         * @enum {string}
                         * @property {number} copy=0 copy value
                         * @property {number} cut=1 cut value
                         * @property {number} getFileFromClipboard=2 getFileFromClipboard value
                         */
                        CopyEventMsgInProto.CopyEventMsgTypeProto = (function() {
                            var valuesById = {}, values = Object.create(valuesById);
                            values[valuesById[0] = "copy"] = 0;
                            values[valuesById[1] = "cut"] = 1;
                            values[valuesById[2] = "getFileFromClipboard"] = 2;
                            return values;
                        })();

                        return CopyEventMsgInProto;
                    })();

                    proto.PasteEventMsgInProto = (function() {

                        /**
                         * Properties of a PasteEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IPasteEventMsgInProto
                         * @property {string|null} [text] PasteEventMsgInProto text
                         * @property {string|null} [html] PasteEventMsgInProto html
                         * @property {string|null} [img] PasteEventMsgInProto img
                         * @property {boolean|null} [special] PasteEventMsgInProto special
                         */

                        /**
                         * Constructs a new PasteEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a PasteEventMsgInProto.
                         * @implements IPasteEventMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IPasteEventMsgInProto=} [properties] Properties to set
                         */
                        function PasteEventMsgInProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * PasteEventMsgInProto text.
                         * @member {string} text
                         * @memberof org.webswing.server.model.proto.PasteEventMsgInProto
                         * @instance
                         */
                        PasteEventMsgInProto.prototype.text = "";

                        /**
                         * PasteEventMsgInProto html.
                         * @member {string} html
                         * @memberof org.webswing.server.model.proto.PasteEventMsgInProto
                         * @instance
                         */
                        PasteEventMsgInProto.prototype.html = "";

                        /**
                         * PasteEventMsgInProto img.
                         * @member {string} img
                         * @memberof org.webswing.server.model.proto.PasteEventMsgInProto
                         * @instance
                         */
                        PasteEventMsgInProto.prototype.img = "";

                        /**
                         * PasteEventMsgInProto special.
                         * @member {boolean} special
                         * @memberof org.webswing.server.model.proto.PasteEventMsgInProto
                         * @instance
                         */
                        PasteEventMsgInProto.prototype.special = false;

                        /**
                         * Creates a new PasteEventMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.PasteEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IPasteEventMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.PasteEventMsgInProto} PasteEventMsgInProto instance
                         */
                        PasteEventMsgInProto.create = function create(properties) {
                            return new PasteEventMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified PasteEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.PasteEventMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.PasteEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IPasteEventMsgInProto} message PasteEventMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        PasteEventMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.text != null && message.hasOwnProperty("text"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.text);
                            if (message.html != null && message.hasOwnProperty("html"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.html);
                            if (message.img != null && message.hasOwnProperty("img"))
                                writer.uint32(/* id 3, wireType 2 =*/26).string(message.img);
                            if (message.special != null && message.hasOwnProperty("special"))
                                writer.uint32(/* id 4, wireType 0 =*/32).bool(message.special);
                            return writer;
                        };

                        /**
                         * Decodes a PasteEventMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.PasteEventMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.PasteEventMsgInProto} PasteEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        PasteEventMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.PasteEventMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.text = reader.string();
                                    break;
                                case 2:
                                    message.html = reader.string();
                                    break;
                                case 3:
                                    message.img = reader.string();
                                    break;
                                case 4:
                                    message.special = reader.bool();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a PasteEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.PasteEventMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.PasteEventMsgInProto} PasteEventMsgInProto
                         */
                        PasteEventMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.PasteEventMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.PasteEventMsgInProto();
                            if (object.text != null)
                                message.text = String(object.text);
                            if (object.html != null)
                                message.html = String(object.html);
                            if (object.img != null)
                                message.img = String(object.img);
                            if (object.special != null)
                                message.special = Boolean(object.special);
                            return message;
                        };

                        /**
                         * Creates a plain object from a PasteEventMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.PasteEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.PasteEventMsgInProto} message PasteEventMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        PasteEventMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.text = "";
                                object.html = "";
                                object.img = "";
                                object.special = false;
                            }
                            if (message.text != null && message.hasOwnProperty("text"))
                                object.text = message.text;
                            if (message.html != null && message.hasOwnProperty("html"))
                                object.html = message.html;
                            if (message.img != null && message.hasOwnProperty("img"))
                                object.img = message.img;
                            if (message.special != null && message.hasOwnProperty("special"))
                                object.special = message.special;
                            return object;
                        };

                        /**
                         * Converts this PasteEventMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.PasteEventMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        PasteEventMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return PasteEventMsgInProto;
                    })();

                    proto.SimpleEventMsgInProto = (function() {

                        /**
                         * Properties of a SimpleEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface ISimpleEventMsgInProto
                         * @property {org.webswing.server.model.proto.SimpleEventMsgInProto.SimpleEventTypeProto|null} [type] SimpleEventMsgInProto type
                         */

                        /**
                         * Constructs a new SimpleEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a SimpleEventMsgInProto.
                         * @implements ISimpleEventMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.ISimpleEventMsgInProto=} [properties] Properties to set
                         */
                        function SimpleEventMsgInProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * SimpleEventMsgInProto type.
                         * @member {org.webswing.server.model.proto.SimpleEventMsgInProto.SimpleEventTypeProto} type
                         * @memberof org.webswing.server.model.proto.SimpleEventMsgInProto
                         * @instance
                         */
                        SimpleEventMsgInProto.prototype.type = 0;

                        /**
                         * Creates a new SimpleEventMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.SimpleEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.ISimpleEventMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.SimpleEventMsgInProto} SimpleEventMsgInProto instance
                         */
                        SimpleEventMsgInProto.create = function create(properties) {
                            return new SimpleEventMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified SimpleEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.SimpleEventMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.SimpleEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.ISimpleEventMsgInProto} message SimpleEventMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        SimpleEventMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.type != null && message.hasOwnProperty("type"))
                                writer.uint32(/* id 1, wireType 0 =*/8).int32(message.type);
                            return writer;
                        };

                        /**
                         * Decodes a SimpleEventMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.SimpleEventMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.SimpleEventMsgInProto} SimpleEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        SimpleEventMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.SimpleEventMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.type = reader.int32();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a SimpleEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.SimpleEventMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.SimpleEventMsgInProto} SimpleEventMsgInProto
                         */
                        SimpleEventMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.SimpleEventMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.SimpleEventMsgInProto();
                            switch (object.type) {
                            case "unload":
                            case 0:
                                message.type = 0;
                                break;
                            case "killSwing":
                            case 1:
                                message.type = 1;
                                break;
                            case "paintAck":
                            case 2:
                                message.type = 2;
                                break;
                            case "repaint":
                            case 3:
                                message.type = 3;
                                break;
                            case "downloadFile":
                            case 4:
                                message.type = 4;
                                break;
                            case "deleteFile":
                            case 5:
                                message.type = 5;
                                break;
                            case "hb":
                            case 6:
                                message.type = 6;
                                break;
                            case "cancelFileSelection":
                            case 7:
                                message.type = 7;
                                break;
                            case "requestComponentTree":
                            case 8:
                                message.type = 8;
                                break;
                            case "requestWindowSwitchList":
                            case 9:
                                message.type = 9;
                                break;
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from a SimpleEventMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.SimpleEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.SimpleEventMsgInProto} message SimpleEventMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        SimpleEventMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults)
                                object.type = options.enums === String ? "unload" : 0;
                            if (message.type != null && message.hasOwnProperty("type"))
                                object.type = options.enums === String ? $root.org.webswing.server.model.proto.SimpleEventMsgInProto.SimpleEventTypeProto[message.type] : message.type;
                            return object;
                        };

                        /**
                         * Converts this SimpleEventMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.SimpleEventMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        SimpleEventMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        /**
                         * SimpleEventTypeProto enum.
                         * @name org.webswing.server.model.proto.SimpleEventMsgInProto.SimpleEventTypeProto
                         * @enum {string}
                         * @property {number} unload=0 unload value
                         * @property {number} killSwing=1 killSwing value
                         * @property {number} paintAck=2 paintAck value
                         * @property {number} repaint=3 repaint value
                         * @property {number} downloadFile=4 downloadFile value
                         * @property {number} deleteFile=5 deleteFile value
                         * @property {number} hb=6 hb value
                         * @property {number} cancelFileSelection=7 cancelFileSelection value
                         * @property {number} requestComponentTree=8 requestComponentTree value
                         * @property {number} requestWindowSwitchList=9 requestWindowSwitchList value
                         */
                        SimpleEventMsgInProto.SimpleEventTypeProto = (function() {
                            var valuesById = {}, values = Object.create(valuesById);
                            values[valuesById[0] = "unload"] = 0;
                            values[valuesById[1] = "killSwing"] = 1;
                            values[valuesById[2] = "paintAck"] = 2;
                            values[valuesById[3] = "repaint"] = 3;
                            values[valuesById[4] = "downloadFile"] = 4;
                            values[valuesById[5] = "deleteFile"] = 5;
                            values[valuesById[6] = "hb"] = 6;
                            values[valuesById[7] = "cancelFileSelection"] = 7;
                            values[valuesById[8] = "requestComponentTree"] = 8;
                            values[valuesById[9] = "requestWindowSwitchList"] = 9;
                            return values;
                        })();

                        return SimpleEventMsgInProto;
                    })();

                    proto.FilesSelectedEventMsgInProto = (function() {

                        /**
                         * Properties of a FilesSelectedEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IFilesSelectedEventMsgInProto
                         * @property {Array.<string>|null} [files] FilesSelectedEventMsgInProto files
                         */

                        /**
                         * Constructs a new FilesSelectedEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a FilesSelectedEventMsgInProto.
                         * @implements IFilesSelectedEventMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IFilesSelectedEventMsgInProto=} [properties] Properties to set
                         */
                        function FilesSelectedEventMsgInProto(properties) {
                            this.files = [];
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * FilesSelectedEventMsgInProto files.
                         * @member {Array.<string>} files
                         * @memberof org.webswing.server.model.proto.FilesSelectedEventMsgInProto
                         * @instance
                         */
                        FilesSelectedEventMsgInProto.prototype.files = $util.emptyArray;

                        /**
                         * Creates a new FilesSelectedEventMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.FilesSelectedEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IFilesSelectedEventMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.FilesSelectedEventMsgInProto} FilesSelectedEventMsgInProto instance
                         */
                        FilesSelectedEventMsgInProto.create = function create(properties) {
                            return new FilesSelectedEventMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified FilesSelectedEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.FilesSelectedEventMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.FilesSelectedEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IFilesSelectedEventMsgInProto} message FilesSelectedEventMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        FilesSelectedEventMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.files != null && message.files.length)
                                for (var i = 0; i < message.files.length; ++i)
                                    writer.uint32(/* id 1, wireType 2 =*/10).string(message.files[i]);
                            return writer;
                        };

                        /**
                         * Decodes a FilesSelectedEventMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.FilesSelectedEventMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.FilesSelectedEventMsgInProto} FilesSelectedEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        FilesSelectedEventMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.FilesSelectedEventMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    if (!(message.files && message.files.length))
                                        message.files = [];
                                    message.files.push(reader.string());
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a FilesSelectedEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.FilesSelectedEventMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.FilesSelectedEventMsgInProto} FilesSelectedEventMsgInProto
                         */
                        FilesSelectedEventMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.FilesSelectedEventMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.FilesSelectedEventMsgInProto();
                            if (object.files) {
                                if (!Array.isArray(object.files))
                                    throw TypeError(".org.webswing.server.model.proto.FilesSelectedEventMsgInProto.files: array expected");
                                message.files = [];
                                for (var i = 0; i < object.files.length; ++i)
                                    message.files[i] = String(object.files[i]);
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from a FilesSelectedEventMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.FilesSelectedEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.FilesSelectedEventMsgInProto} message FilesSelectedEventMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        FilesSelectedEventMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.arrays || options.defaults)
                                object.files = [];
                            if (message.files && message.files.length) {
                                object.files = [];
                                for (var j = 0; j < message.files.length; ++j)
                                    object.files[j] = message.files[j];
                            }
                            return object;
                        };

                        /**
                         * Converts this FilesSelectedEventMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.FilesSelectedEventMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        FilesSelectedEventMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return FilesSelectedEventMsgInProto;
                    })();

                    proto.UploadEventMsgInProto = (function() {

                        /**
                         * Properties of an UploadEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IUploadEventMsgInProto
                         * @property {string|null} [fileName] UploadEventMsgInProto fileName
                         * @property {string|null} [tempFileLocation] UploadEventMsgInProto tempFileLocation
                         */

                        /**
                         * Constructs a new UploadEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents an UploadEventMsgInProto.
                         * @implements IUploadEventMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IUploadEventMsgInProto=} [properties] Properties to set
                         */
                        function UploadEventMsgInProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * UploadEventMsgInProto fileName.
                         * @member {string} fileName
                         * @memberof org.webswing.server.model.proto.UploadEventMsgInProto
                         * @instance
                         */
                        UploadEventMsgInProto.prototype.fileName = "";

                        /**
                         * UploadEventMsgInProto tempFileLocation.
                         * @member {string} tempFileLocation
                         * @memberof org.webswing.server.model.proto.UploadEventMsgInProto
                         * @instance
                         */
                        UploadEventMsgInProto.prototype.tempFileLocation = "";

                        /**
                         * Creates a new UploadEventMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.UploadEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IUploadEventMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.UploadEventMsgInProto} UploadEventMsgInProto instance
                         */
                        UploadEventMsgInProto.create = function create(properties) {
                            return new UploadEventMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified UploadEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.UploadEventMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.UploadEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IUploadEventMsgInProto} message UploadEventMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        UploadEventMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.fileName != null && message.hasOwnProperty("fileName"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.fileName);
                            if (message.tempFileLocation != null && message.hasOwnProperty("tempFileLocation"))
                                writer.uint32(/* id 3, wireType 2 =*/26).string(message.tempFileLocation);
                            return writer;
                        };

                        /**
                         * Decodes an UploadEventMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.UploadEventMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.UploadEventMsgInProto} UploadEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        UploadEventMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.UploadEventMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 2:
                                    message.fileName = reader.string();
                                    break;
                                case 3:
                                    message.tempFileLocation = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates an UploadEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.UploadEventMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.UploadEventMsgInProto} UploadEventMsgInProto
                         */
                        UploadEventMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.UploadEventMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.UploadEventMsgInProto();
                            if (object.fileName != null)
                                message.fileName = String(object.fileName);
                            if (object.tempFileLocation != null)
                                message.tempFileLocation = String(object.tempFileLocation);
                            return message;
                        };

                        /**
                         * Creates a plain object from an UploadEventMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.UploadEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.UploadEventMsgInProto} message UploadEventMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        UploadEventMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.fileName = "";
                                object.tempFileLocation = "";
                            }
                            if (message.fileName != null && message.hasOwnProperty("fileName"))
                                object.fileName = message.fileName;
                            if (message.tempFileLocation != null && message.hasOwnProperty("tempFileLocation"))
                                object.tempFileLocation = message.tempFileLocation;
                            return object;
                        };

                        /**
                         * Converts this UploadEventMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.UploadEventMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        UploadEventMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return UploadEventMsgInProto;
                    })();

                    proto.JavaEvalRequestMsgInProto = (function() {

                        /**
                         * Properties of a JavaEvalRequestMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IJavaEvalRequestMsgInProto
                         * @property {string|null} [correlationId] JavaEvalRequestMsgInProto correlationId
                         * @property {string|null} [objectId] JavaEvalRequestMsgInProto objectId
                         * @property {string|null} [method] JavaEvalRequestMsgInProto method
                         * @property {Array.<org.webswing.server.model.proto.IJsParamMsgProto>|null} [params] JavaEvalRequestMsgInProto params
                         */

                        /**
                         * Constructs a new JavaEvalRequestMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a JavaEvalRequestMsgInProto.
                         * @implements IJavaEvalRequestMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IJavaEvalRequestMsgInProto=} [properties] Properties to set
                         */
                        function JavaEvalRequestMsgInProto(properties) {
                            this.params = [];
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * JavaEvalRequestMsgInProto correlationId.
                         * @member {string} correlationId
                         * @memberof org.webswing.server.model.proto.JavaEvalRequestMsgInProto
                         * @instance
                         */
                        JavaEvalRequestMsgInProto.prototype.correlationId = "";

                        /**
                         * JavaEvalRequestMsgInProto objectId.
                         * @member {string} objectId
                         * @memberof org.webswing.server.model.proto.JavaEvalRequestMsgInProto
                         * @instance
                         */
                        JavaEvalRequestMsgInProto.prototype.objectId = "";

                        /**
                         * JavaEvalRequestMsgInProto method.
                         * @member {string} method
                         * @memberof org.webswing.server.model.proto.JavaEvalRequestMsgInProto
                         * @instance
                         */
                        JavaEvalRequestMsgInProto.prototype.method = "";

                        /**
                         * JavaEvalRequestMsgInProto params.
                         * @member {Array.<org.webswing.server.model.proto.IJsParamMsgProto>} params
                         * @memberof org.webswing.server.model.proto.JavaEvalRequestMsgInProto
                         * @instance
                         */
                        JavaEvalRequestMsgInProto.prototype.params = $util.emptyArray;

                        /**
                         * Creates a new JavaEvalRequestMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.JavaEvalRequestMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IJavaEvalRequestMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.JavaEvalRequestMsgInProto} JavaEvalRequestMsgInProto instance
                         */
                        JavaEvalRequestMsgInProto.create = function create(properties) {
                            return new JavaEvalRequestMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified JavaEvalRequestMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.JavaEvalRequestMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.JavaEvalRequestMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IJavaEvalRequestMsgInProto} message JavaEvalRequestMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        JavaEvalRequestMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.correlationId != null && message.hasOwnProperty("correlationId"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.correlationId);
                            if (message.objectId != null && message.hasOwnProperty("objectId"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.objectId);
                            if (message.method != null && message.hasOwnProperty("method"))
                                writer.uint32(/* id 3, wireType 2 =*/26).string(message.method);
                            if (message.params != null && message.params.length)
                                for (var i = 0; i < message.params.length; ++i)
                                    $root.org.webswing.server.model.proto.JsParamMsgProto.encode(message.params[i], writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
                            return writer;
                        };

                        /**
                         * Decodes a JavaEvalRequestMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.JavaEvalRequestMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.JavaEvalRequestMsgInProto} JavaEvalRequestMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        JavaEvalRequestMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.JavaEvalRequestMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.correlationId = reader.string();
                                    break;
                                case 2:
                                    message.objectId = reader.string();
                                    break;
                                case 3:
                                    message.method = reader.string();
                                    break;
                                case 4:
                                    if (!(message.params && message.params.length))
                                        message.params = [];
                                    message.params.push($root.org.webswing.server.model.proto.JsParamMsgProto.decode(reader, reader.uint32()));
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a JavaEvalRequestMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.JavaEvalRequestMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.JavaEvalRequestMsgInProto} JavaEvalRequestMsgInProto
                         */
                        JavaEvalRequestMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.JavaEvalRequestMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.JavaEvalRequestMsgInProto();
                            if (object.correlationId != null)
                                message.correlationId = String(object.correlationId);
                            if (object.objectId != null)
                                message.objectId = String(object.objectId);
                            if (object.method != null)
                                message.method = String(object.method);
                            if (object.params) {
                                if (!Array.isArray(object.params))
                                    throw TypeError(".org.webswing.server.model.proto.JavaEvalRequestMsgInProto.params: array expected");
                                message.params = [];
                                for (var i = 0; i < object.params.length; ++i) {
                                    if (typeof object.params[i] !== "object")
                                        throw TypeError(".org.webswing.server.model.proto.JavaEvalRequestMsgInProto.params: object expected");
                                    message.params[i] = $root.org.webswing.server.model.proto.JsParamMsgProto.fromObject(object.params[i]);
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from a JavaEvalRequestMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.JavaEvalRequestMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.JavaEvalRequestMsgInProto} message JavaEvalRequestMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        JavaEvalRequestMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.arrays || options.defaults)
                                object.params = [];
                            if (options.defaults) {
                                object.correlationId = "";
                                object.objectId = "";
                                object.method = "";
                            }
                            if (message.correlationId != null && message.hasOwnProperty("correlationId"))
                                object.correlationId = message.correlationId;
                            if (message.objectId != null && message.hasOwnProperty("objectId"))
                                object.objectId = message.objectId;
                            if (message.method != null && message.hasOwnProperty("method"))
                                object.method = message.method;
                            if (message.params && message.params.length) {
                                object.params = [];
                                for (var j = 0; j < message.params.length; ++j)
                                    object.params[j] = $root.org.webswing.server.model.proto.JsParamMsgProto.toObject(message.params[j], options);
                            }
                            return object;
                        };

                        /**
                         * Converts this JavaEvalRequestMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.JavaEvalRequestMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        JavaEvalRequestMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return JavaEvalRequestMsgInProto;
                    })();

                    proto.PlaybackCommandMsgInProto = (function() {

                        /**
                         * Properties of a PlaybackCommandMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IPlaybackCommandMsgInProto
                         * @property {org.webswing.server.model.proto.PlaybackCommandMsgInProto.PlaybackCommandProto|null} [command] PlaybackCommandMsgInProto command
                         */

                        /**
                         * Constructs a new PlaybackCommandMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a PlaybackCommandMsgInProto.
                         * @implements IPlaybackCommandMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IPlaybackCommandMsgInProto=} [properties] Properties to set
                         */
                        function PlaybackCommandMsgInProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * PlaybackCommandMsgInProto command.
                         * @member {org.webswing.server.model.proto.PlaybackCommandMsgInProto.PlaybackCommandProto} command
                         * @memberof org.webswing.server.model.proto.PlaybackCommandMsgInProto
                         * @instance
                         */
                        PlaybackCommandMsgInProto.prototype.command = 0;

                        /**
                         * Creates a new PlaybackCommandMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.PlaybackCommandMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IPlaybackCommandMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.PlaybackCommandMsgInProto} PlaybackCommandMsgInProto instance
                         */
                        PlaybackCommandMsgInProto.create = function create(properties) {
                            return new PlaybackCommandMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified PlaybackCommandMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.PlaybackCommandMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.PlaybackCommandMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IPlaybackCommandMsgInProto} message PlaybackCommandMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        PlaybackCommandMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.command != null && message.hasOwnProperty("command"))
                                writer.uint32(/* id 1, wireType 0 =*/8).int32(message.command);
                            return writer;
                        };

                        /**
                         * Decodes a PlaybackCommandMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.PlaybackCommandMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.PlaybackCommandMsgInProto} PlaybackCommandMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        PlaybackCommandMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.PlaybackCommandMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.command = reader.int32();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a PlaybackCommandMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.PlaybackCommandMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.PlaybackCommandMsgInProto} PlaybackCommandMsgInProto
                         */
                        PlaybackCommandMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.PlaybackCommandMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.PlaybackCommandMsgInProto();
                            switch (object.command) {
                            case "reset":
                            case 0:
                                message.command = 0;
                                break;
                            case "play":
                            case 1:
                                message.command = 1;
                                break;
                            case "stop":
                            case 2:
                                message.command = 2;
                                break;
                            case "step":
                            case 3:
                                message.command = 3;
                                break;
                            case "step10":
                            case 4:
                                message.command = 4;
                                break;
                            case "step100":
                            case 5:
                                message.command = 5;
                                break;
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from a PlaybackCommandMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.PlaybackCommandMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.PlaybackCommandMsgInProto} message PlaybackCommandMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        PlaybackCommandMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults)
                                object.command = options.enums === String ? "reset" : 0;
                            if (message.command != null && message.hasOwnProperty("command"))
                                object.command = options.enums === String ? $root.org.webswing.server.model.proto.PlaybackCommandMsgInProto.PlaybackCommandProto[message.command] : message.command;
                            return object;
                        };

                        /**
                         * Converts this PlaybackCommandMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.PlaybackCommandMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        PlaybackCommandMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        /**
                         * PlaybackCommandProto enum.
                         * @name org.webswing.server.model.proto.PlaybackCommandMsgInProto.PlaybackCommandProto
                         * @enum {string}
                         * @property {number} reset=0 reset value
                         * @property {number} play=1 play value
                         * @property {number} stop=2 stop value
                         * @property {number} step=3 step value
                         * @property {number} step10=4 step10 value
                         * @property {number} step100=5 step100 value
                         */
                        PlaybackCommandMsgInProto.PlaybackCommandProto = (function() {
                            var valuesById = {}, values = Object.create(valuesById);
                            values[valuesById[0] = "reset"] = 0;
                            values[valuesById[1] = "play"] = 1;
                            values[valuesById[2] = "stop"] = 2;
                            values[valuesById[3] = "step"] = 3;
                            values[valuesById[4] = "step10"] = 4;
                            values[valuesById[5] = "step100"] = 5;
                            return values;
                        })();

                        return PlaybackCommandMsgInProto;
                    })();

                    proto.PixelsAreaResponseMsgInProto = (function() {

                        /**
                         * Properties of a PixelsAreaResponseMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IPixelsAreaResponseMsgInProto
                         * @property {string|null} [correlationId] PixelsAreaResponseMsgInProto correlationId
                         * @property {string|null} [pixels] PixelsAreaResponseMsgInProto pixels
                         */

                        /**
                         * Constructs a new PixelsAreaResponseMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a PixelsAreaResponseMsgInProto.
                         * @implements IPixelsAreaResponseMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IPixelsAreaResponseMsgInProto=} [properties] Properties to set
                         */
                        function PixelsAreaResponseMsgInProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * PixelsAreaResponseMsgInProto correlationId.
                         * @member {string} correlationId
                         * @memberof org.webswing.server.model.proto.PixelsAreaResponseMsgInProto
                         * @instance
                         */
                        PixelsAreaResponseMsgInProto.prototype.correlationId = "";

                        /**
                         * PixelsAreaResponseMsgInProto pixels.
                         * @member {string} pixels
                         * @memberof org.webswing.server.model.proto.PixelsAreaResponseMsgInProto
                         * @instance
                         */
                        PixelsAreaResponseMsgInProto.prototype.pixels = "";

                        /**
                         * Creates a new PixelsAreaResponseMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.PixelsAreaResponseMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IPixelsAreaResponseMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.PixelsAreaResponseMsgInProto} PixelsAreaResponseMsgInProto instance
                         */
                        PixelsAreaResponseMsgInProto.create = function create(properties) {
                            return new PixelsAreaResponseMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified PixelsAreaResponseMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.PixelsAreaResponseMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.PixelsAreaResponseMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IPixelsAreaResponseMsgInProto} message PixelsAreaResponseMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        PixelsAreaResponseMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.correlationId != null && message.hasOwnProperty("correlationId"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.correlationId);
                            if (message.pixels != null && message.hasOwnProperty("pixels"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.pixels);
                            return writer;
                        };

                        /**
                         * Decodes a PixelsAreaResponseMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.PixelsAreaResponseMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.PixelsAreaResponseMsgInProto} PixelsAreaResponseMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        PixelsAreaResponseMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.PixelsAreaResponseMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.correlationId = reader.string();
                                    break;
                                case 2:
                                    message.pixels = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a PixelsAreaResponseMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.PixelsAreaResponseMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.PixelsAreaResponseMsgInProto} PixelsAreaResponseMsgInProto
                         */
                        PixelsAreaResponseMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.PixelsAreaResponseMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.PixelsAreaResponseMsgInProto();
                            if (object.correlationId != null)
                                message.correlationId = String(object.correlationId);
                            if (object.pixels != null)
                                message.pixels = String(object.pixels);
                            return message;
                        };

                        /**
                         * Creates a plain object from a PixelsAreaResponseMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.PixelsAreaResponseMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.PixelsAreaResponseMsgInProto} message PixelsAreaResponseMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        PixelsAreaResponseMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.correlationId = "";
                                object.pixels = "";
                            }
                            if (message.correlationId != null && message.hasOwnProperty("correlationId"))
                                object.correlationId = message.correlationId;
                            if (message.pixels != null && message.hasOwnProperty("pixels"))
                                object.pixels = message.pixels;
                            return object;
                        };

                        /**
                         * Converts this PixelsAreaResponseMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.PixelsAreaResponseMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        PixelsAreaResponseMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return PixelsAreaResponseMsgInProto;
                    })();

                    proto.ComponentTreeMsgProto = (function() {

                        /**
                         * Properties of a ComponentTreeMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IComponentTreeMsgProto
                         * @property {string|null} [componentType] ComponentTreeMsgProto componentType
                         * @property {string|null} [name] ComponentTreeMsgProto name
                         * @property {string|null} [value] ComponentTreeMsgProto value
                         * @property {number|null} [screenX] ComponentTreeMsgProto screenX
                         * @property {number|null} [screenY] ComponentTreeMsgProto screenY
                         * @property {number|null} [width] ComponentTreeMsgProto width
                         * @property {number|null} [height] ComponentTreeMsgProto height
                         * @property {boolean|null} [enabled] ComponentTreeMsgProto enabled
                         * @property {boolean|null} [visible] ComponentTreeMsgProto visible
                         * @property {Array.<org.webswing.server.model.proto.IComponentTreeMsgProto>|null} [components] ComponentTreeMsgProto components
                         * @property {boolean|null} [hidden] ComponentTreeMsgProto hidden
                         * @property {boolean|null} [selected] ComponentTreeMsgProto selected
                         */

                        /**
                         * Constructs a new ComponentTreeMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a ComponentTreeMsgProto.
                         * @implements IComponentTreeMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IComponentTreeMsgProto=} [properties] Properties to set
                         */
                        function ComponentTreeMsgProto(properties) {
                            this.components = [];
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * ComponentTreeMsgProto componentType.
                         * @member {string} componentType
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @instance
                         */
                        ComponentTreeMsgProto.prototype.componentType = "";

                        /**
                         * ComponentTreeMsgProto name.
                         * @member {string} name
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @instance
                         */
                        ComponentTreeMsgProto.prototype.name = "";

                        /**
                         * ComponentTreeMsgProto value.
                         * @member {string} value
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @instance
                         */
                        ComponentTreeMsgProto.prototype.value = "";

                        /**
                         * ComponentTreeMsgProto screenX.
                         * @member {number} screenX
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @instance
                         */
                        ComponentTreeMsgProto.prototype.screenX = 0;

                        /**
                         * ComponentTreeMsgProto screenY.
                         * @member {number} screenY
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @instance
                         */
                        ComponentTreeMsgProto.prototype.screenY = 0;

                        /**
                         * ComponentTreeMsgProto width.
                         * @member {number} width
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @instance
                         */
                        ComponentTreeMsgProto.prototype.width = 0;

                        /**
                         * ComponentTreeMsgProto height.
                         * @member {number} height
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @instance
                         */
                        ComponentTreeMsgProto.prototype.height = 0;

                        /**
                         * ComponentTreeMsgProto enabled.
                         * @member {boolean} enabled
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @instance
                         */
                        ComponentTreeMsgProto.prototype.enabled = false;

                        /**
                         * ComponentTreeMsgProto visible.
                         * @member {boolean} visible
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @instance
                         */
                        ComponentTreeMsgProto.prototype.visible = false;

                        /**
                         * ComponentTreeMsgProto components.
                         * @member {Array.<org.webswing.server.model.proto.IComponentTreeMsgProto>} components
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @instance
                         */
                        ComponentTreeMsgProto.prototype.components = $util.emptyArray;

                        /**
                         * ComponentTreeMsgProto hidden.
                         * @member {boolean} hidden
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @instance
                         */
                        ComponentTreeMsgProto.prototype.hidden = false;

                        /**
                         * ComponentTreeMsgProto selected.
                         * @member {boolean} selected
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @instance
                         */
                        ComponentTreeMsgProto.prototype.selected = false;

                        /**
                         * Creates a new ComponentTreeMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IComponentTreeMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.ComponentTreeMsgProto} ComponentTreeMsgProto instance
                         */
                        ComponentTreeMsgProto.create = function create(properties) {
                            return new ComponentTreeMsgProto(properties);
                        };

                        /**
                         * Encodes the specified ComponentTreeMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.ComponentTreeMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IComponentTreeMsgProto} message ComponentTreeMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        ComponentTreeMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.componentType != null && message.hasOwnProperty("componentType"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.componentType);
                            if (message.name != null && message.hasOwnProperty("name"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.name);
                            if (message.value != null && message.hasOwnProperty("value"))
                                writer.uint32(/* id 3, wireType 2 =*/26).string(message.value);
                            if (message.screenX != null && message.hasOwnProperty("screenX"))
                                writer.uint32(/* id 4, wireType 0 =*/32).sint32(message.screenX);
                            if (message.screenY != null && message.hasOwnProperty("screenY"))
                                writer.uint32(/* id 5, wireType 0 =*/40).sint32(message.screenY);
                            if (message.width != null && message.hasOwnProperty("width"))
                                writer.uint32(/* id 6, wireType 0 =*/48).sint32(message.width);
                            if (message.height != null && message.hasOwnProperty("height"))
                                writer.uint32(/* id 7, wireType 0 =*/56).sint32(message.height);
                            if (message.enabled != null && message.hasOwnProperty("enabled"))
                                writer.uint32(/* id 8, wireType 0 =*/64).bool(message.enabled);
                            if (message.visible != null && message.hasOwnProperty("visible"))
                                writer.uint32(/* id 9, wireType 0 =*/72).bool(message.visible);
                            if (message.components != null && message.components.length)
                                for (var i = 0; i < message.components.length; ++i)
                                    $root.org.webswing.server.model.proto.ComponentTreeMsgProto.encode(message.components[i], writer.uint32(/* id 10, wireType 2 =*/82).fork()).ldelim();
                            if (message.hidden != null && message.hasOwnProperty("hidden"))
                                writer.uint32(/* id 11, wireType 0 =*/88).bool(message.hidden);
                            if (message.selected != null && message.hasOwnProperty("selected"))
                                writer.uint32(/* id 12, wireType 0 =*/96).bool(message.selected);
                            return writer;
                        };

                        /**
                         * Decodes a ComponentTreeMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.ComponentTreeMsgProto} ComponentTreeMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        ComponentTreeMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.ComponentTreeMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.componentType = reader.string();
                                    break;
                                case 2:
                                    message.name = reader.string();
                                    break;
                                case 3:
                                    message.value = reader.string();
                                    break;
                                case 4:
                                    message.screenX = reader.sint32();
                                    break;
                                case 5:
                                    message.screenY = reader.sint32();
                                    break;
                                case 6:
                                    message.width = reader.sint32();
                                    break;
                                case 7:
                                    message.height = reader.sint32();
                                    break;
                                case 8:
                                    message.enabled = reader.bool();
                                    break;
                                case 9:
                                    message.visible = reader.bool();
                                    break;
                                case 10:
                                    if (!(message.components && message.components.length))
                                        message.components = [];
                                    message.components.push($root.org.webswing.server.model.proto.ComponentTreeMsgProto.decode(reader, reader.uint32()));
                                    break;
                                case 11:
                                    message.hidden = reader.bool();
                                    break;
                                case 12:
                                    message.selected = reader.bool();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a ComponentTreeMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.ComponentTreeMsgProto} ComponentTreeMsgProto
                         */
                        ComponentTreeMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.ComponentTreeMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.ComponentTreeMsgProto();
                            if (object.componentType != null)
                                message.componentType = String(object.componentType);
                            if (object.name != null)
                                message.name = String(object.name);
                            if (object.value != null)
                                message.value = String(object.value);
                            if (object.screenX != null)
                                message.screenX = object.screenX | 0;
                            if (object.screenY != null)
                                message.screenY = object.screenY | 0;
                            if (object.width != null)
                                message.width = object.width | 0;
                            if (object.height != null)
                                message.height = object.height | 0;
                            if (object.enabled != null)
                                message.enabled = Boolean(object.enabled);
                            if (object.visible != null)
                                message.visible = Boolean(object.visible);
                            if (object.components) {
                                if (!Array.isArray(object.components))
                                    throw TypeError(".org.webswing.server.model.proto.ComponentTreeMsgProto.components: array expected");
                                message.components = [];
                                for (var i = 0; i < object.components.length; ++i) {
                                    if (typeof object.components[i] !== "object")
                                        throw TypeError(".org.webswing.server.model.proto.ComponentTreeMsgProto.components: object expected");
                                    message.components[i] = $root.org.webswing.server.model.proto.ComponentTreeMsgProto.fromObject(object.components[i]);
                                }
                            }
                            if (object.hidden != null)
                                message.hidden = Boolean(object.hidden);
                            if (object.selected != null)
                                message.selected = Boolean(object.selected);
                            return message;
                        };

                        /**
                         * Creates a plain object from a ComponentTreeMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.ComponentTreeMsgProto} message ComponentTreeMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        ComponentTreeMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.arrays || options.defaults)
                                object.components = [];
                            if (options.defaults) {
                                object.componentType = "";
                                object.name = "";
                                object.value = "";
                                object.screenX = 0;
                                object.screenY = 0;
                                object.width = 0;
                                object.height = 0;
                                object.enabled = false;
                                object.visible = false;
                                object.hidden = false;
                                object.selected = false;
                            }
                            if (message.componentType != null && message.hasOwnProperty("componentType"))
                                object.componentType = message.componentType;
                            if (message.name != null && message.hasOwnProperty("name"))
                                object.name = message.name;
                            if (message.value != null && message.hasOwnProperty("value"))
                                object.value = message.value;
                            if (message.screenX != null && message.hasOwnProperty("screenX"))
                                object.screenX = message.screenX;
                            if (message.screenY != null && message.hasOwnProperty("screenY"))
                                object.screenY = message.screenY;
                            if (message.width != null && message.hasOwnProperty("width"))
                                object.width = message.width;
                            if (message.height != null && message.hasOwnProperty("height"))
                                object.height = message.height;
                            if (message.enabled != null && message.hasOwnProperty("enabled"))
                                object.enabled = message.enabled;
                            if (message.visible != null && message.hasOwnProperty("visible"))
                                object.visible = message.visible;
                            if (message.components && message.components.length) {
                                object.components = [];
                                for (var j = 0; j < message.components.length; ++j)
                                    object.components[j] = $root.org.webswing.server.model.proto.ComponentTreeMsgProto.toObject(message.components[j], options);
                            }
                            if (message.hidden != null && message.hasOwnProperty("hidden"))
                                object.hidden = message.hidden;
                            if (message.selected != null && message.hasOwnProperty("selected"))
                                object.selected = message.selected;
                            return object;
                        };

                        /**
                         * Converts this ComponentTreeMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.ComponentTreeMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        ComponentTreeMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return ComponentTreeMsgProto;
                    })();

                    proto.WindowEventMsgInProto = (function() {

                        /**
                         * Properties of a WindowEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IWindowEventMsgInProto
                         * @property {string|null} [id] WindowEventMsgInProto id
                         * @property {number|null} [x] WindowEventMsgInProto x
                         * @property {number|null} [y] WindowEventMsgInProto y
                         * @property {number|null} [width] WindowEventMsgInProto width
                         * @property {number|null} [height] WindowEventMsgInProto height
                         * @property {boolean|null} [close] WindowEventMsgInProto close
                         * @property {boolean|null} [focus] WindowEventMsgInProto focus
                         */

                        /**
                         * Constructs a new WindowEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a WindowEventMsgInProto.
                         * @implements IWindowEventMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IWindowEventMsgInProto=} [properties] Properties to set
                         */
                        function WindowEventMsgInProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * WindowEventMsgInProto id.
                         * @member {string} id
                         * @memberof org.webswing.server.model.proto.WindowEventMsgInProto
                         * @instance
                         */
                        WindowEventMsgInProto.prototype.id = "";

                        /**
                         * WindowEventMsgInProto x.
                         * @member {number} x
                         * @memberof org.webswing.server.model.proto.WindowEventMsgInProto
                         * @instance
                         */
                        WindowEventMsgInProto.prototype.x = 0;

                        /**
                         * WindowEventMsgInProto y.
                         * @member {number} y
                         * @memberof org.webswing.server.model.proto.WindowEventMsgInProto
                         * @instance
                         */
                        WindowEventMsgInProto.prototype.y = 0;

                        /**
                         * WindowEventMsgInProto width.
                         * @member {number} width
                         * @memberof org.webswing.server.model.proto.WindowEventMsgInProto
                         * @instance
                         */
                        WindowEventMsgInProto.prototype.width = 0;

                        /**
                         * WindowEventMsgInProto height.
                         * @member {number} height
                         * @memberof org.webswing.server.model.proto.WindowEventMsgInProto
                         * @instance
                         */
                        WindowEventMsgInProto.prototype.height = 0;

                        /**
                         * WindowEventMsgInProto close.
                         * @member {boolean} close
                         * @memberof org.webswing.server.model.proto.WindowEventMsgInProto
                         * @instance
                         */
                        WindowEventMsgInProto.prototype.close = false;

                        /**
                         * WindowEventMsgInProto focus.
                         * @member {boolean} focus
                         * @memberof org.webswing.server.model.proto.WindowEventMsgInProto
                         * @instance
                         */
                        WindowEventMsgInProto.prototype.focus = false;

                        /**
                         * Creates a new WindowEventMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.WindowEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowEventMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.WindowEventMsgInProto} WindowEventMsgInProto instance
                         */
                        WindowEventMsgInProto.create = function create(properties) {
                            return new WindowEventMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified WindowEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowEventMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.WindowEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowEventMsgInProto} message WindowEventMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        WindowEventMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.id != null && message.hasOwnProperty("id"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
                            if (message.x != null && message.hasOwnProperty("x"))
                                writer.uint32(/* id 2, wireType 0 =*/16).sint32(message.x);
                            if (message.y != null && message.hasOwnProperty("y"))
                                writer.uint32(/* id 3, wireType 0 =*/24).sint32(message.y);
                            if (message.width != null && message.hasOwnProperty("width"))
                                writer.uint32(/* id 4, wireType 0 =*/32).sint32(message.width);
                            if (message.height != null && message.hasOwnProperty("height"))
                                writer.uint32(/* id 5, wireType 0 =*/40).sint32(message.height);
                            if (message.close != null && message.hasOwnProperty("close"))
                                writer.uint32(/* id 6, wireType 0 =*/48).bool(message.close);
                            if (message.focus != null && message.hasOwnProperty("focus"))
                                writer.uint32(/* id 7, wireType 0 =*/56).bool(message.focus);
                            return writer;
                        };

                        /**
                         * Decodes a WindowEventMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.WindowEventMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.WindowEventMsgInProto} WindowEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        WindowEventMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.WindowEventMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.id = reader.string();
                                    break;
                                case 2:
                                    message.x = reader.sint32();
                                    break;
                                case 3:
                                    message.y = reader.sint32();
                                    break;
                                case 4:
                                    message.width = reader.sint32();
                                    break;
                                case 5:
                                    message.height = reader.sint32();
                                    break;
                                case 6:
                                    message.close = reader.bool();
                                    break;
                                case 7:
                                    message.focus = reader.bool();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a WindowEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.WindowEventMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.WindowEventMsgInProto} WindowEventMsgInProto
                         */
                        WindowEventMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.WindowEventMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.WindowEventMsgInProto();
                            if (object.id != null)
                                message.id = String(object.id);
                            if (object.x != null)
                                message.x = object.x | 0;
                            if (object.y != null)
                                message.y = object.y | 0;
                            if (object.width != null)
                                message.width = object.width | 0;
                            if (object.height != null)
                                message.height = object.height | 0;
                            if (object.close != null)
                                message.close = Boolean(object.close);
                            if (object.focus != null)
                                message.focus = Boolean(object.focus);
                            return message;
                        };

                        /**
                         * Creates a plain object from a WindowEventMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.WindowEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.WindowEventMsgInProto} message WindowEventMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        WindowEventMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.id = "";
                                object.x = 0;
                                object.y = 0;
                                object.width = 0;
                                object.height = 0;
                                object.close = false;
                                object.focus = false;
                            }
                            if (message.id != null && message.hasOwnProperty("id"))
                                object.id = message.id;
                            if (message.x != null && message.hasOwnProperty("x"))
                                object.x = message.x;
                            if (message.y != null && message.hasOwnProperty("y"))
                                object.y = message.y;
                            if (message.width != null && message.hasOwnProperty("width"))
                                object.width = message.width;
                            if (message.height != null && message.hasOwnProperty("height"))
                                object.height = message.height;
                            if (message.close != null && message.hasOwnProperty("close"))
                                object.close = message.close;
                            if (message.focus != null && message.hasOwnProperty("focus"))
                                object.focus = message.focus;
                            return object;
                        };

                        /**
                         * Converts this WindowEventMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.WindowEventMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        WindowEventMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return WindowEventMsgInProto;
                    })();

                    proto.ActionEventMsgInProto = (function() {

                        /**
                         * Properties of an ActionEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IActionEventMsgInProto
                         * @property {string|null} [actionName] ActionEventMsgInProto actionName
                         * @property {string|null} [data] ActionEventMsgInProto data
                         * @property {Uint8Array|null} [binaryData] ActionEventMsgInProto binaryData
                         * @property {string|null} [windowId] ActionEventMsgInProto windowId
                         * @property {org.webswing.server.model.proto.ActionEventMsgInProto.ActionEventTypeProto|null} [eventType] ActionEventMsgInProto eventType
                         */

                        /**
                         * Constructs a new ActionEventMsgInProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents an ActionEventMsgInProto.
                         * @implements IActionEventMsgInProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IActionEventMsgInProto=} [properties] Properties to set
                         */
                        function ActionEventMsgInProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * ActionEventMsgInProto actionName.
                         * @member {string} actionName
                         * @memberof org.webswing.server.model.proto.ActionEventMsgInProto
                         * @instance
                         */
                        ActionEventMsgInProto.prototype.actionName = "";

                        /**
                         * ActionEventMsgInProto data.
                         * @member {string} data
                         * @memberof org.webswing.server.model.proto.ActionEventMsgInProto
                         * @instance
                         */
                        ActionEventMsgInProto.prototype.data = "";

                        /**
                         * ActionEventMsgInProto binaryData.
                         * @member {Uint8Array} binaryData
                         * @memberof org.webswing.server.model.proto.ActionEventMsgInProto
                         * @instance
                         */
                        ActionEventMsgInProto.prototype.binaryData = $util.newBuffer([]);

                        /**
                         * ActionEventMsgInProto windowId.
                         * @member {string} windowId
                         * @memberof org.webswing.server.model.proto.ActionEventMsgInProto
                         * @instance
                         */
                        ActionEventMsgInProto.prototype.windowId = "";

                        /**
                         * ActionEventMsgInProto eventType.
                         * @member {org.webswing.server.model.proto.ActionEventMsgInProto.ActionEventTypeProto} eventType
                         * @memberof org.webswing.server.model.proto.ActionEventMsgInProto
                         * @instance
                         */
                        ActionEventMsgInProto.prototype.eventType = 0;

                        /**
                         * Creates a new ActionEventMsgInProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.ActionEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IActionEventMsgInProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.ActionEventMsgInProto} ActionEventMsgInProto instance
                         */
                        ActionEventMsgInProto.create = function create(properties) {
                            return new ActionEventMsgInProto(properties);
                        };

                        /**
                         * Encodes the specified ActionEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.ActionEventMsgInProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.ActionEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.IActionEventMsgInProto} message ActionEventMsgInProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        ActionEventMsgInProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.actionName != null && message.hasOwnProperty("actionName"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.actionName);
                            if (message.data != null && message.hasOwnProperty("data"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.data);
                            if (message.binaryData != null && message.hasOwnProperty("binaryData"))
                                writer.uint32(/* id 3, wireType 2 =*/26).bytes(message.binaryData);
                            if (message.windowId != null && message.hasOwnProperty("windowId"))
                                writer.uint32(/* id 4, wireType 2 =*/34).string(message.windowId);
                            if (message.eventType != null && message.hasOwnProperty("eventType"))
                                writer.uint32(/* id 5, wireType 0 =*/40).int32(message.eventType);
                            return writer;
                        };

                        /**
                         * Decodes an ActionEventMsgInProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.ActionEventMsgInProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.ActionEventMsgInProto} ActionEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        ActionEventMsgInProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.ActionEventMsgInProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.actionName = reader.string();
                                    break;
                                case 2:
                                    message.data = reader.string();
                                    break;
                                case 3:
                                    message.binaryData = reader.bytes();
                                    break;
                                case 4:
                                    message.windowId = reader.string();
                                    break;
                                case 5:
                                    message.eventType = reader.int32();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates an ActionEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.ActionEventMsgInProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.ActionEventMsgInProto} ActionEventMsgInProto
                         */
                        ActionEventMsgInProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.ActionEventMsgInProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.ActionEventMsgInProto();
                            if (object.actionName != null)
                                message.actionName = String(object.actionName);
                            if (object.data != null)
                                message.data = String(object.data);
                            if (object.binaryData != null)
                                if (typeof object.binaryData === "string")
                                    $util.base64.decode(object.binaryData, message.binaryData = $util.newBuffer($util.base64.length(object.binaryData)), 0);
                                else if (object.binaryData.length)
                                    message.binaryData = object.binaryData;
                            if (object.windowId != null)
                                message.windowId = String(object.windowId);
                            switch (object.eventType) {
                            case "init":
                            case 0:
                                message.eventType = 0;
                                break;
                            case "user":
                            case 1:
                                message.eventType = 1;
                                break;
                            }
                            return message;
                        };

                        /**
                         * Creates a plain object from an ActionEventMsgInProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.ActionEventMsgInProto
                         * @static
                         * @param {org.webswing.server.model.proto.ActionEventMsgInProto} message ActionEventMsgInProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        ActionEventMsgInProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.actionName = "";
                                object.data = "";
                                if (options.bytes === String)
                                    object.binaryData = "";
                                else {
                                    object.binaryData = [];
                                    if (options.bytes !== Array)
                                        object.binaryData = $util.newBuffer(object.binaryData);
                                }
                                object.windowId = "";
                                object.eventType = options.enums === String ? "init" : 0;
                            }
                            if (message.actionName != null && message.hasOwnProperty("actionName"))
                                object.actionName = message.actionName;
                            if (message.data != null && message.hasOwnProperty("data"))
                                object.data = message.data;
                            if (message.binaryData != null && message.hasOwnProperty("binaryData"))
                                object.binaryData = options.bytes === String ? $util.base64.encode(message.binaryData, 0, message.binaryData.length) : options.bytes === Array ? Array.prototype.slice.call(message.binaryData) : message.binaryData;
                            if (message.windowId != null && message.hasOwnProperty("windowId"))
                                object.windowId = message.windowId;
                            if (message.eventType != null && message.hasOwnProperty("eventType"))
                                object.eventType = options.enums === String ? $root.org.webswing.server.model.proto.ActionEventMsgInProto.ActionEventTypeProto[message.eventType] : message.eventType;
                            return object;
                        };

                        /**
                         * Converts this ActionEventMsgInProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.ActionEventMsgInProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        ActionEventMsgInProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        /**
                         * ActionEventTypeProto enum.
                         * @name org.webswing.server.model.proto.ActionEventMsgInProto.ActionEventTypeProto
                         * @enum {string}
                         * @property {number} init=0 init value
                         * @property {number} user=1 user value
                         */
                        ActionEventMsgInProto.ActionEventTypeProto = (function() {
                            var valuesById = {}, values = Object.create(valuesById);
                            values[valuesById[0] = "init"] = 0;
                            values[valuesById[1] = "user"] = 1;
                            return values;
                        })();

                        return ActionEventMsgInProto;
                    })();

                    proto.ActionEventMsgOutProto = (function() {

                        /**
                         * Properties of an ActionEventMsgOutProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IActionEventMsgOutProto
                         * @property {string|null} [actionName] ActionEventMsgOutProto actionName
                         * @property {string|null} [data] ActionEventMsgOutProto data
                         * @property {Uint8Array|null} [binaryData] ActionEventMsgOutProto binaryData
                         * @property {string|null} [windowId] ActionEventMsgOutProto windowId
                         */

                        /**
                         * Constructs a new ActionEventMsgOutProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents an ActionEventMsgOutProto.
                         * @implements IActionEventMsgOutProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IActionEventMsgOutProto=} [properties] Properties to set
                         */
                        function ActionEventMsgOutProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * ActionEventMsgOutProto actionName.
                         * @member {string} actionName
                         * @memberof org.webswing.server.model.proto.ActionEventMsgOutProto
                         * @instance
                         */
                        ActionEventMsgOutProto.prototype.actionName = "";

                        /**
                         * ActionEventMsgOutProto data.
                         * @member {string} data
                         * @memberof org.webswing.server.model.proto.ActionEventMsgOutProto
                         * @instance
                         */
                        ActionEventMsgOutProto.prototype.data = "";

                        /**
                         * ActionEventMsgOutProto binaryData.
                         * @member {Uint8Array} binaryData
                         * @memberof org.webswing.server.model.proto.ActionEventMsgOutProto
                         * @instance
                         */
                        ActionEventMsgOutProto.prototype.binaryData = $util.newBuffer([]);

                        /**
                         * ActionEventMsgOutProto windowId.
                         * @member {string} windowId
                         * @memberof org.webswing.server.model.proto.ActionEventMsgOutProto
                         * @instance
                         */
                        ActionEventMsgOutProto.prototype.windowId = "";

                        /**
                         * Creates a new ActionEventMsgOutProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.ActionEventMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.IActionEventMsgOutProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.ActionEventMsgOutProto} ActionEventMsgOutProto instance
                         */
                        ActionEventMsgOutProto.create = function create(properties) {
                            return new ActionEventMsgOutProto(properties);
                        };

                        /**
                         * Encodes the specified ActionEventMsgOutProto message. Does not implicitly {@link org.webswing.server.model.proto.ActionEventMsgOutProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.ActionEventMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.IActionEventMsgOutProto} message ActionEventMsgOutProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        ActionEventMsgOutProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.actionName != null && message.hasOwnProperty("actionName"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.actionName);
                            if (message.data != null && message.hasOwnProperty("data"))
                                writer.uint32(/* id 2, wireType 2 =*/18).string(message.data);
                            if (message.binaryData != null && message.hasOwnProperty("binaryData"))
                                writer.uint32(/* id 3, wireType 2 =*/26).bytes(message.binaryData);
                            if (message.windowId != null && message.hasOwnProperty("windowId"))
                                writer.uint32(/* id 4, wireType 2 =*/34).string(message.windowId);
                            return writer;
                        };

                        /**
                         * Decodes an ActionEventMsgOutProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.ActionEventMsgOutProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.ActionEventMsgOutProto} ActionEventMsgOutProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        ActionEventMsgOutProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.ActionEventMsgOutProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.actionName = reader.string();
                                    break;
                                case 2:
                                    message.data = reader.string();
                                    break;
                                case 3:
                                    message.binaryData = reader.bytes();
                                    break;
                                case 4:
                                    message.windowId = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates an ActionEventMsgOutProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.ActionEventMsgOutProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.ActionEventMsgOutProto} ActionEventMsgOutProto
                         */
                        ActionEventMsgOutProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.ActionEventMsgOutProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.ActionEventMsgOutProto();
                            if (object.actionName != null)
                                message.actionName = String(object.actionName);
                            if (object.data != null)
                                message.data = String(object.data);
                            if (object.binaryData != null)
                                if (typeof object.binaryData === "string")
                                    $util.base64.decode(object.binaryData, message.binaryData = $util.newBuffer($util.base64.length(object.binaryData)), 0);
                                else if (object.binaryData.length)
                                    message.binaryData = object.binaryData;
                            if (object.windowId != null)
                                message.windowId = String(object.windowId);
                            return message;
                        };

                        /**
                         * Creates a plain object from an ActionEventMsgOutProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.ActionEventMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.ActionEventMsgOutProto} message ActionEventMsgOutProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        ActionEventMsgOutProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.actionName = "";
                                object.data = "";
                                if (options.bytes === String)
                                    object.binaryData = "";
                                else {
                                    object.binaryData = [];
                                    if (options.bytes !== Array)
                                        object.binaryData = $util.newBuffer(object.binaryData);
                                }
                                object.windowId = "";
                            }
                            if (message.actionName != null && message.hasOwnProperty("actionName"))
                                object.actionName = message.actionName;
                            if (message.data != null && message.hasOwnProperty("data"))
                                object.data = message.data;
                            if (message.binaryData != null && message.hasOwnProperty("binaryData"))
                                object.binaryData = options.bytes === String ? $util.base64.encode(message.binaryData, 0, message.binaryData.length) : options.bytes === Array ? Array.prototype.slice.call(message.binaryData) : message.binaryData;
                            if (message.windowId != null && message.hasOwnProperty("windowId"))
                                object.windowId = message.windowId;
                            return object;
                        };

                        /**
                         * Converts this ActionEventMsgOutProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.ActionEventMsgOutProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        ActionEventMsgOutProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return ActionEventMsgOutProto;
                    })();

                    proto.AudioEventMsgOutProto = (function() {

                        /**
                         * Properties of an AudioEventMsgOutProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IAudioEventMsgOutProto
                         * @property {string|null} [id] AudioEventMsgOutProto id
                         * @property {org.webswing.server.model.proto.AudioEventMsgOutProto.AudioEventTypeProto|null} [eventType] AudioEventMsgOutProto eventType
                         * @property {Uint8Array|null} [data] AudioEventMsgOutProto data
                         * @property {number|null} [time] AudioEventMsgOutProto time
                         * @property {number|null} [loop] AudioEventMsgOutProto loop
                         */

                        /**
                         * Constructs a new AudioEventMsgOutProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents an AudioEventMsgOutProto.
                         * @implements IAudioEventMsgOutProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IAudioEventMsgOutProto=} [properties] Properties to set
                         */
                        function AudioEventMsgOutProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * AudioEventMsgOutProto id.
                         * @member {string} id
                         * @memberof org.webswing.server.model.proto.AudioEventMsgOutProto
                         * @instance
                         */
                        AudioEventMsgOutProto.prototype.id = "";

                        /**
                         * AudioEventMsgOutProto eventType.
                         * @member {org.webswing.server.model.proto.AudioEventMsgOutProto.AudioEventTypeProto} eventType
                         * @memberof org.webswing.server.model.proto.AudioEventMsgOutProto
                         * @instance
                         */
                        AudioEventMsgOutProto.prototype.eventType = 0;

                        /**
                         * AudioEventMsgOutProto data.
                         * @member {Uint8Array} data
                         * @memberof org.webswing.server.model.proto.AudioEventMsgOutProto
                         * @instance
                         */
                        AudioEventMsgOutProto.prototype.data = $util.newBuffer([]);

                        /**
                         * AudioEventMsgOutProto time.
                         * @member {number} time
                         * @memberof org.webswing.server.model.proto.AudioEventMsgOutProto
                         * @instance
                         */
                        AudioEventMsgOutProto.prototype.time = 0;

                        /**
                         * AudioEventMsgOutProto loop.
                         * @member {number} loop
                         * @memberof org.webswing.server.model.proto.AudioEventMsgOutProto
                         * @instance
                         */
                        AudioEventMsgOutProto.prototype.loop = 0;

                        /**
                         * Creates a new AudioEventMsgOutProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.AudioEventMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.IAudioEventMsgOutProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.AudioEventMsgOutProto} AudioEventMsgOutProto instance
                         */
                        AudioEventMsgOutProto.create = function create(properties) {
                            return new AudioEventMsgOutProto(properties);
                        };

                        /**
                         * Encodes the specified AudioEventMsgOutProto message. Does not implicitly {@link org.webswing.server.model.proto.AudioEventMsgOutProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.AudioEventMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.IAudioEventMsgOutProto} message AudioEventMsgOutProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        AudioEventMsgOutProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.id != null && message.hasOwnProperty("id"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
                            if (message.eventType != null && message.hasOwnProperty("eventType"))
                                writer.uint32(/* id 2, wireType 0 =*/16).int32(message.eventType);
                            if (message.data != null && message.hasOwnProperty("data"))
                                writer.uint32(/* id 3, wireType 2 =*/26).bytes(message.data);
                            if (message.time != null && message.hasOwnProperty("time"))
                                writer.uint32(/* id 4, wireType 5 =*/37).float(message.time);
                            if (message.loop != null && message.hasOwnProperty("loop"))
                                writer.uint32(/* id 5, wireType 0 =*/40).sint32(message.loop);
                            return writer;
                        };

                        /**
                         * Decodes an AudioEventMsgOutProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.AudioEventMsgOutProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.AudioEventMsgOutProto} AudioEventMsgOutProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        AudioEventMsgOutProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.AudioEventMsgOutProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.id = reader.string();
                                    break;
                                case 2:
                                    message.eventType = reader.int32();
                                    break;
                                case 3:
                                    message.data = reader.bytes();
                                    break;
                                case 4:
                                    message.time = reader.float();
                                    break;
                                case 5:
                                    message.loop = reader.sint32();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates an AudioEventMsgOutProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.AudioEventMsgOutProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.AudioEventMsgOutProto} AudioEventMsgOutProto
                         */
                        AudioEventMsgOutProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.AudioEventMsgOutProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.AudioEventMsgOutProto();
                            if (object.id != null)
                                message.id = String(object.id);
                            switch (object.eventType) {
                            case "play":
                            case 0:
                                message.eventType = 0;
                                break;
                            case "stop":
                            case 1:
                                message.eventType = 1;
                                break;
                            case "update":
                            case 2:
                                message.eventType = 2;
                                break;
                            case "dispose":
                            case 3:
                                message.eventType = 3;
                                break;
                            }
                            if (object.data != null)
                                if (typeof object.data === "string")
                                    $util.base64.decode(object.data, message.data = $util.newBuffer($util.base64.length(object.data)), 0);
                                else if (object.data.length)
                                    message.data = object.data;
                            if (object.time != null)
                                message.time = Number(object.time);
                            if (object.loop != null)
                                message.loop = object.loop | 0;
                            return message;
                        };

                        /**
                         * Creates a plain object from an AudioEventMsgOutProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.AudioEventMsgOutProto
                         * @static
                         * @param {org.webswing.server.model.proto.AudioEventMsgOutProto} message AudioEventMsgOutProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        AudioEventMsgOutProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults) {
                                object.id = "";
                                object.eventType = options.enums === String ? "play" : 0;
                                if (options.bytes === String)
                                    object.data = "";
                                else {
                                    object.data = [];
                                    if (options.bytes !== Array)
                                        object.data = $util.newBuffer(object.data);
                                }
                                object.time = 0;
                                object.loop = 0;
                            }
                            if (message.id != null && message.hasOwnProperty("id"))
                                object.id = message.id;
                            if (message.eventType != null && message.hasOwnProperty("eventType"))
                                object.eventType = options.enums === String ? $root.org.webswing.server.model.proto.AudioEventMsgOutProto.AudioEventTypeProto[message.eventType] : message.eventType;
                            if (message.data != null && message.hasOwnProperty("data"))
                                object.data = options.bytes === String ? $util.base64.encode(message.data, 0, message.data.length) : options.bytes === Array ? Array.prototype.slice.call(message.data) : message.data;
                            if (message.time != null && message.hasOwnProperty("time"))
                                object.time = options.json && !isFinite(message.time) ? String(message.time) : message.time;
                            if (message.loop != null && message.hasOwnProperty("loop"))
                                object.loop = message.loop;
                            return object;
                        };

                        /**
                         * Converts this AudioEventMsgOutProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.AudioEventMsgOutProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        AudioEventMsgOutProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        /**
                         * AudioEventTypeProto enum.
                         * @name org.webswing.server.model.proto.AudioEventMsgOutProto.AudioEventTypeProto
                         * @enum {string}
                         * @property {number} play=0 play value
                         * @property {number} stop=1 stop value
                         * @property {number} update=2 update value
                         * @property {number} dispose=3 dispose value
                         */
                        AudioEventMsgOutProto.AudioEventTypeProto = (function() {
                            var valuesById = {}, values = Object.create(valuesById);
                            values[valuesById[0] = "play"] = 0;
                            values[valuesById[1] = "stop"] = 1;
                            values[valuesById[2] = "update"] = 2;
                            values[valuesById[3] = "dispose"] = 3;
                            return values;
                        })();

                        return AudioEventMsgOutProto;
                    })();

                    proto.WindowDockMsgProto = (function() {

                        /**
                         * Properties of a WindowDockMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @interface IWindowDockMsgProto
                         * @property {string|null} [windowId] WindowDockMsgProto windowId
                         */

                        /**
                         * Constructs a new WindowDockMsgProto.
                         * @memberof org.webswing.server.model.proto
                         * @classdesc Represents a WindowDockMsgProto.
                         * @implements IWindowDockMsgProto
                         * @constructor
                         * @param {org.webswing.server.model.proto.IWindowDockMsgProto=} [properties] Properties to set
                         */
                        function WindowDockMsgProto(properties) {
                            if (properties)
                                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                    if (properties[keys[i]] != null)
                                        this[keys[i]] = properties[keys[i]];
                        }

                        /**
                         * WindowDockMsgProto windowId.
                         * @member {string} windowId
                         * @memberof org.webswing.server.model.proto.WindowDockMsgProto
                         * @instance
                         */
                        WindowDockMsgProto.prototype.windowId = "";

                        /**
                         * Creates a new WindowDockMsgProto instance using the specified properties.
                         * @function create
                         * @memberof org.webswing.server.model.proto.WindowDockMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowDockMsgProto=} [properties] Properties to set
                         * @returns {org.webswing.server.model.proto.WindowDockMsgProto} WindowDockMsgProto instance
                         */
                        WindowDockMsgProto.create = function create(properties) {
                            return new WindowDockMsgProto(properties);
                        };

                        /**
                         * Encodes the specified WindowDockMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowDockMsgProto.verify|verify} messages.
                         * @function encode
                         * @memberof org.webswing.server.model.proto.WindowDockMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.IWindowDockMsgProto} message WindowDockMsgProto message or plain object to encode
                         * @param {$protobuf.Writer} [writer] Writer to encode to
                         * @returns {$protobuf.Writer} Writer
                         */
                        WindowDockMsgProto.encode = function encode(message, writer) {
                            if (!writer)
                                writer = $Writer.create();
                            if (message.windowId != null && message.hasOwnProperty("windowId"))
                                writer.uint32(/* id 1, wireType 2 =*/10).string(message.windowId);
                            return writer;
                        };

                        /**
                         * Decodes a WindowDockMsgProto message from the specified reader or buffer.
                         * @function decode
                         * @memberof org.webswing.server.model.proto.WindowDockMsgProto
                         * @static
                         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                         * @param {number} [length] Message length if known beforehand
                         * @returns {org.webswing.server.model.proto.WindowDockMsgProto} WindowDockMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        WindowDockMsgProto.decode = function decode(reader, length) {
                            if (!(reader instanceof $Reader))
                                reader = $Reader.create(reader);
                            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.org.webswing.server.model.proto.WindowDockMsgProto();
                            while (reader.pos < end) {
                                var tag = reader.uint32();
                                switch (tag >>> 3) {
                                case 1:
                                    message.windowId = reader.string();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                                }
                            }
                            return message;
                        };

                        /**
                         * Creates a WindowDockMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @function fromObject
                         * @memberof org.webswing.server.model.proto.WindowDockMsgProto
                         * @static
                         * @param {Object.<string,*>} object Plain object
                         * @returns {org.webswing.server.model.proto.WindowDockMsgProto} WindowDockMsgProto
                         */
                        WindowDockMsgProto.fromObject = function fromObject(object) {
                            if (object instanceof $root.org.webswing.server.model.proto.WindowDockMsgProto)
                                return object;
                            var message = new $root.org.webswing.server.model.proto.WindowDockMsgProto();
                            if (object.windowId != null)
                                message.windowId = String(object.windowId);
                            return message;
                        };

                        /**
                         * Creates a plain object from a WindowDockMsgProto message. Also converts values to other types if specified.
                         * @function toObject
                         * @memberof org.webswing.server.model.proto.WindowDockMsgProto
                         * @static
                         * @param {org.webswing.server.model.proto.WindowDockMsgProto} message WindowDockMsgProto
                         * @param {$protobuf.IConversionOptions} [options] Conversion options
                         * @returns {Object.<string,*>} Plain object
                         */
                        WindowDockMsgProto.toObject = function toObject(message, options) {
                            if (!options)
                                options = {};
                            var object = {};
                            if (options.defaults)
                                object.windowId = "";
                            if (message.windowId != null && message.hasOwnProperty("windowId"))
                                object.windowId = message.windowId;
                            return object;
                        };

                        /**
                         * Converts this WindowDockMsgProto to JSON.
                         * @function toJSON
                         * @memberof org.webswing.server.model.proto.WindowDockMsgProto
                         * @instance
                         * @returns {Object.<string,*>} JSON object
                         */
                        WindowDockMsgProto.prototype.toJSON = function toJSON() {
                            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                        };

                        return WindowDockMsgProto;
                    })();

                    return proto;
                })();

                return model;
            })();

            return server;
        })();

        return webswing;
    })();

    return org;
})();

module.exports = $root;
