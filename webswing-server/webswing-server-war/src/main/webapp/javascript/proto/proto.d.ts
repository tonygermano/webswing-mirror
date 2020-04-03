import * as $protobuf from "protobufjs";
/** Namespace org. */
export namespace org {

    /** Namespace webswing. */
    namespace webswing {

        /** Namespace server. */
        namespace server {

            /** Namespace model. */
            namespace model {

                /** Namespace proto. */
                namespace proto {

                    /** Properties of an AppFrameMsgOutProto. */
                    interface IAppFrameMsgOutProto {

                        /** AppFrameMsgOutProto applications */
                        applications?: (org.webswing.server.model.proto.IApplicationInfoMsgProto[]|null);

                        /** AppFrameMsgOutProto linkAction */
                        linkAction?: (org.webswing.server.model.proto.ILinkActionMsgProto|null);

                        /** AppFrameMsgOutProto moveAction */
                        moveAction?: (org.webswing.server.model.proto.IWindowMoveActionMsgProto|null);

                        /** AppFrameMsgOutProto cursorChange */
                        cursorChange?: (org.webswing.server.model.proto.ICursorChangeEventMsgProto|null);

                        /** AppFrameMsgOutProto copyEvent */
                        copyEvent?: (org.webswing.server.model.proto.ICopyEventMsgProto|null);

                        /** AppFrameMsgOutProto pasteRequest */
                        pasteRequest?: (org.webswing.server.model.proto.IPasteRequestMsgProto|null);

                        /** AppFrameMsgOutProto fileDialogEvent */
                        fileDialogEvent?: (org.webswing.server.model.proto.IFileDialogEventMsgProto|null);

                        /** AppFrameMsgOutProto windows */
                        windows?: (org.webswing.server.model.proto.IWindowMsgProto[]|null);

                        /** AppFrameMsgOutProto closedWindow */
                        closedWindow?: (org.webswing.server.model.proto.IWindowMsgProto|null);

                        /** AppFrameMsgOutProto event */
                        event?: (org.webswing.server.model.proto.SimpleEventMsgOutProto|null);

                        /** AppFrameMsgOutProto jsRequest */
                        jsRequest?: (org.webswing.server.model.proto.IJsEvalRequestMsgOutProto|null);

                        /** AppFrameMsgOutProto javaResponse */
                        javaResponse?: (org.webswing.server.model.proto.IJsResultMsgProto|null);

                        /** AppFrameMsgOutProto pixelsRequest */
                        pixelsRequest?: (org.webswing.server.model.proto.IPixelsAreaRequestMsgOutProto|null);

                        /** AppFrameMsgOutProto playback */
                        playback?: (org.webswing.server.model.proto.IPlaybackInfoMsgProto|null);

                        /** AppFrameMsgOutProto sessionId */
                        sessionId?: (string|null);

                        /** AppFrameMsgOutProto startTimestamp */
                        startTimestamp?: (string|null);

                        /** AppFrameMsgOutProto sendTimestamp */
                        sendTimestamp?: (string|null);

                        /** AppFrameMsgOutProto focusEvent */
                        focusEvent?: (org.webswing.server.model.proto.IFocusEventMsgProto|null);

                        /** AppFrameMsgOutProto componentTree */
                        componentTree?: (org.webswing.server.model.proto.IComponentTreeMsgProto[]|null);

                        /** AppFrameMsgOutProto directDraw */
                        directDraw?: (boolean|null);

                        /** AppFrameMsgOutProto actionEvent */
                        actionEvent?: (org.webswing.server.model.proto.IActionEventMsgOutProto|null);

                        /** AppFrameMsgOutProto compositingWM */
                        compositingWM?: (boolean|null);

                        /** AppFrameMsgOutProto audioEvent */
                        audioEvent?: (org.webswing.server.model.proto.IAudioEventMsgOutProto|null);

                        /** AppFrameMsgOutProto dockAction */
                        dockAction?: (org.webswing.server.model.proto.IWindowDockMsgProto|null);

                        /** AppFrameMsgOutProto accessible */
                        accessible?: (org.webswing.server.model.proto.IAccessibilityMsgProto|null);

                        /** AppFrameMsgOutProto windowSwitchList */
                        windowSwitchList?: (org.webswing.server.model.proto.IWindowSwitchMsgProto[]|null);
                    }

                    /** Represents an AppFrameMsgOutProto. */
                    class AppFrameMsgOutProto implements IAppFrameMsgOutProto {

                        /**
                         * Constructs a new AppFrameMsgOutProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IAppFrameMsgOutProto);

                        /** AppFrameMsgOutProto applications. */
                        public applications: org.webswing.server.model.proto.IApplicationInfoMsgProto[];

                        /** AppFrameMsgOutProto linkAction. */
                        public linkAction?: (org.webswing.server.model.proto.ILinkActionMsgProto|null);

                        /** AppFrameMsgOutProto moveAction. */
                        public moveAction?: (org.webswing.server.model.proto.IWindowMoveActionMsgProto|null);

                        /** AppFrameMsgOutProto cursorChange. */
                        public cursorChange?: (org.webswing.server.model.proto.ICursorChangeEventMsgProto|null);

                        /** AppFrameMsgOutProto copyEvent. */
                        public copyEvent?: (org.webswing.server.model.proto.ICopyEventMsgProto|null);

                        /** AppFrameMsgOutProto pasteRequest. */
                        public pasteRequest?: (org.webswing.server.model.proto.IPasteRequestMsgProto|null);

                        /** AppFrameMsgOutProto fileDialogEvent. */
                        public fileDialogEvent?: (org.webswing.server.model.proto.IFileDialogEventMsgProto|null);

                        /** AppFrameMsgOutProto windows. */
                        public windows: org.webswing.server.model.proto.IWindowMsgProto[];

                        /** AppFrameMsgOutProto closedWindow. */
                        public closedWindow?: (org.webswing.server.model.proto.IWindowMsgProto|null);

                        /** AppFrameMsgOutProto event. */
                        public event: org.webswing.server.model.proto.SimpleEventMsgOutProto;

                        /** AppFrameMsgOutProto jsRequest. */
                        public jsRequest?: (org.webswing.server.model.proto.IJsEvalRequestMsgOutProto|null);

                        /** AppFrameMsgOutProto javaResponse. */
                        public javaResponse?: (org.webswing.server.model.proto.IJsResultMsgProto|null);

                        /** AppFrameMsgOutProto pixelsRequest. */
                        public pixelsRequest?: (org.webswing.server.model.proto.IPixelsAreaRequestMsgOutProto|null);

                        /** AppFrameMsgOutProto playback. */
                        public playback?: (org.webswing.server.model.proto.IPlaybackInfoMsgProto|null);

                        /** AppFrameMsgOutProto sessionId. */
                        public sessionId: string;

                        /** AppFrameMsgOutProto startTimestamp. */
                        public startTimestamp: string;

                        /** AppFrameMsgOutProto sendTimestamp. */
                        public sendTimestamp: string;

                        /** AppFrameMsgOutProto focusEvent. */
                        public focusEvent?: (org.webswing.server.model.proto.IFocusEventMsgProto|null);

                        /** AppFrameMsgOutProto componentTree. */
                        public componentTree: org.webswing.server.model.proto.IComponentTreeMsgProto[];

                        /** AppFrameMsgOutProto directDraw. */
                        public directDraw: boolean;

                        /** AppFrameMsgOutProto actionEvent. */
                        public actionEvent?: (org.webswing.server.model.proto.IActionEventMsgOutProto|null);

                        /** AppFrameMsgOutProto compositingWM. */
                        public compositingWM: boolean;

                        /** AppFrameMsgOutProto audioEvent. */
                        public audioEvent?: (org.webswing.server.model.proto.IAudioEventMsgOutProto|null);

                        /** AppFrameMsgOutProto dockAction. */
                        public dockAction?: (org.webswing.server.model.proto.IWindowDockMsgProto|null);

                        /** AppFrameMsgOutProto accessible. */
                        public accessible?: (org.webswing.server.model.proto.IAccessibilityMsgProto|null);

                        /** AppFrameMsgOutProto windowSwitchList. */
                        public windowSwitchList: org.webswing.server.model.proto.IWindowSwitchMsgProto[];

                        /**
                         * Creates a new AppFrameMsgOutProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns AppFrameMsgOutProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IAppFrameMsgOutProto): org.webswing.server.model.proto.AppFrameMsgOutProto;

                        /**
                         * Encodes the specified AppFrameMsgOutProto message. Does not implicitly {@link org.webswing.server.model.proto.AppFrameMsgOutProto.verify|verify} messages.
                         * @param message AppFrameMsgOutProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IAppFrameMsgOutProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes an AppFrameMsgOutProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns AppFrameMsgOutProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.AppFrameMsgOutProto;

                        /**
                         * Creates an AppFrameMsgOutProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns AppFrameMsgOutProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.AppFrameMsgOutProto;

                        /**
                         * Creates a plain object from an AppFrameMsgOutProto message. Also converts values to other types if specified.
                         * @param message AppFrameMsgOutProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.AppFrameMsgOutProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this AppFrameMsgOutProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of an AccessibilityMsgProto. */
                    interface IAccessibilityMsgProto {

                        /** AccessibilityMsgProto id */
                        id?: (string|null);

                        /** AccessibilityMsgProto role */
                        role?: (string|null);

                        /** AccessibilityMsgProto text */
                        text?: (string|null);

                        /** AccessibilityMsgProto tooltip */
                        tooltip?: (string|null);

                        /** AccessibilityMsgProto value */
                        value?: (string|null);

                        /** AccessibilityMsgProto description */
                        description?: (string|null);

                        /** AccessibilityMsgProto columnheader */
                        columnheader?: (string|null);

                        /** AccessibilityMsgProto password */
                        password?: (boolean|null);

                        /** AccessibilityMsgProto toggle */
                        toggle?: (boolean|null);

                        /** AccessibilityMsgProto selstart */
                        selstart?: (number|null);

                        /** AccessibilityMsgProto selend */
                        selend?: (number|null);

                        /** AccessibilityMsgProto rowheight */
                        rowheight?: (number|null);

                        /** AccessibilityMsgProto rows */
                        rows?: (number|null);

                        /** AccessibilityMsgProto size */
                        size?: (number|null);

                        /** AccessibilityMsgProto position */
                        position?: (number|null);

                        /** AccessibilityMsgProto level */
                        level?: (number|null);

                        /** AccessibilityMsgProto colindex */
                        colindex?: (number|null);

                        /** AccessibilityMsgProto rowindex */
                        rowindex?: (number|null);

                        /** AccessibilityMsgProto colcount */
                        colcount?: (number|null);

                        /** AccessibilityMsgProto rowcount */
                        rowcount?: (number|null);

                        /** AccessibilityMsgProto states */
                        states?: (string[]|null);

                        /** AccessibilityMsgProto min */
                        min?: (number|null);

                        /** AccessibilityMsgProto max */
                        max?: (number|null);

                        /** AccessibilityMsgProto val */
                        val?: (number|null);

                        /** AccessibilityMsgProto screenX */
                        screenX?: (number|null);

                        /** AccessibilityMsgProto screenY */
                        screenY?: (number|null);

                        /** AccessibilityMsgProto width */
                        width?: (number|null);

                        /** AccessibilityMsgProto height */
                        height?: (number|null);

                        /** AccessibilityMsgProto hierarchy */
                        hierarchy?: (org.webswing.server.model.proto.IAccessibilityHierarchyMsgProto[]|null);
                    }

                    /** Represents an AccessibilityMsgProto. */
                    class AccessibilityMsgProto implements IAccessibilityMsgProto {

                        /**
                         * Constructs a new AccessibilityMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IAccessibilityMsgProto);

                        /** AccessibilityMsgProto id. */
                        public id: string;

                        /** AccessibilityMsgProto role. */
                        public role: string;

                        /** AccessibilityMsgProto text. */
                        public text: string;

                        /** AccessibilityMsgProto tooltip. */
                        public tooltip: string;

                        /** AccessibilityMsgProto value. */
                        public value: string;

                        /** AccessibilityMsgProto description. */
                        public description: string;

                        /** AccessibilityMsgProto columnheader. */
                        public columnheader: string;

                        /** AccessibilityMsgProto password. */
                        public password: boolean;

                        /** AccessibilityMsgProto toggle. */
                        public toggle: boolean;

                        /** AccessibilityMsgProto selstart. */
                        public selstart: number;

                        /** AccessibilityMsgProto selend. */
                        public selend: number;

                        /** AccessibilityMsgProto rowheight. */
                        public rowheight: number;

                        /** AccessibilityMsgProto rows. */
                        public rows: number;

                        /** AccessibilityMsgProto size. */
                        public size: number;

                        /** AccessibilityMsgProto position. */
                        public position: number;

                        /** AccessibilityMsgProto level. */
                        public level: number;

                        /** AccessibilityMsgProto colindex. */
                        public colindex: number;

                        /** AccessibilityMsgProto rowindex. */
                        public rowindex: number;

                        /** AccessibilityMsgProto colcount. */
                        public colcount: number;

                        /** AccessibilityMsgProto rowcount. */
                        public rowcount: number;

                        /** AccessibilityMsgProto states. */
                        public states: string[];

                        /** AccessibilityMsgProto min. */
                        public min: number;

                        /** AccessibilityMsgProto max. */
                        public max: number;

                        /** AccessibilityMsgProto val. */
                        public val: number;

                        /** AccessibilityMsgProto screenX. */
                        public screenX: number;

                        /** AccessibilityMsgProto screenY. */
                        public screenY: number;

                        /** AccessibilityMsgProto width. */
                        public width: number;

                        /** AccessibilityMsgProto height. */
                        public height: number;

                        /** AccessibilityMsgProto hierarchy. */
                        public hierarchy: org.webswing.server.model.proto.IAccessibilityHierarchyMsgProto[];

                        /**
                         * Creates a new AccessibilityMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns AccessibilityMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IAccessibilityMsgProto): org.webswing.server.model.proto.AccessibilityMsgProto;

                        /**
                         * Encodes the specified AccessibilityMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.AccessibilityMsgProto.verify|verify} messages.
                         * @param message AccessibilityMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IAccessibilityMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes an AccessibilityMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns AccessibilityMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.AccessibilityMsgProto;

                        /**
                         * Creates an AccessibilityMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns AccessibilityMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.AccessibilityMsgProto;

                        /**
                         * Creates a plain object from an AccessibilityMsgProto message. Also converts values to other types if specified.
                         * @param message AccessibilityMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.AccessibilityMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this AccessibilityMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of an AccessibilityHierarchyMsgProto. */
                    interface IAccessibilityHierarchyMsgProto {

                        /** AccessibilityHierarchyMsgProto id */
                        id?: (string|null);

                        /** AccessibilityHierarchyMsgProto role */
                        role?: (string|null);

                        /** AccessibilityHierarchyMsgProto text */
                        text?: (string|null);

                        /** AccessibilityHierarchyMsgProto position */
                        position?: (number|null);

                        /** AccessibilityHierarchyMsgProto size */
                        size?: (number|null);
                    }

                    /** Represents an AccessibilityHierarchyMsgProto. */
                    class AccessibilityHierarchyMsgProto implements IAccessibilityHierarchyMsgProto {

                        /**
                         * Constructs a new AccessibilityHierarchyMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IAccessibilityHierarchyMsgProto);

                        /** AccessibilityHierarchyMsgProto id. */
                        public id: string;

                        /** AccessibilityHierarchyMsgProto role. */
                        public role: string;

                        /** AccessibilityHierarchyMsgProto text. */
                        public text: string;

                        /** AccessibilityHierarchyMsgProto position. */
                        public position: number;

                        /** AccessibilityHierarchyMsgProto size. */
                        public size: number;

                        /**
                         * Creates a new AccessibilityHierarchyMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns AccessibilityHierarchyMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IAccessibilityHierarchyMsgProto): org.webswing.server.model.proto.AccessibilityHierarchyMsgProto;

                        /**
                         * Encodes the specified AccessibilityHierarchyMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.AccessibilityHierarchyMsgProto.verify|verify} messages.
                         * @param message AccessibilityHierarchyMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IAccessibilityHierarchyMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes an AccessibilityHierarchyMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns AccessibilityHierarchyMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.AccessibilityHierarchyMsgProto;

                        /**
                         * Creates an AccessibilityHierarchyMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns AccessibilityHierarchyMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.AccessibilityHierarchyMsgProto;

                        /**
                         * Creates a plain object from an AccessibilityHierarchyMsgProto message. Also converts values to other types if specified.
                         * @param message AccessibilityHierarchyMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.AccessibilityHierarchyMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this AccessibilityHierarchyMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a FocusEventMsgProto. */
                    interface IFocusEventMsgProto {

                        /** FocusEventMsgProto type */
                        type: org.webswing.server.model.proto.FocusEventMsgProto.FocusEventTypeProto;

                        /** FocusEventMsgProto x */
                        x?: (number|null);

                        /** FocusEventMsgProto y */
                        y?: (number|null);

                        /** FocusEventMsgProto w */
                        w?: (number|null);

                        /** FocusEventMsgProto h */
                        h?: (number|null);

                        /** FocusEventMsgProto caretX */
                        caretX?: (number|null);

                        /** FocusEventMsgProto caretY */
                        caretY?: (number|null);

                        /** FocusEventMsgProto caretH */
                        caretH?: (number|null);

                        /** FocusEventMsgProto editable */
                        editable?: (boolean|null);
                    }

                    /** Represents a FocusEventMsgProto. */
                    class FocusEventMsgProto implements IFocusEventMsgProto {

                        /**
                         * Constructs a new FocusEventMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IFocusEventMsgProto);

                        /** FocusEventMsgProto type. */
                        public type: org.webswing.server.model.proto.FocusEventMsgProto.FocusEventTypeProto;

                        /** FocusEventMsgProto x. */
                        public x: number;

                        /** FocusEventMsgProto y. */
                        public y: number;

                        /** FocusEventMsgProto w. */
                        public w: number;

                        /** FocusEventMsgProto h. */
                        public h: number;

                        /** FocusEventMsgProto caretX. */
                        public caretX: number;

                        /** FocusEventMsgProto caretY. */
                        public caretY: number;

                        /** FocusEventMsgProto caretH. */
                        public caretH: number;

                        /** FocusEventMsgProto editable. */
                        public editable: boolean;

                        /**
                         * Creates a new FocusEventMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns FocusEventMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IFocusEventMsgProto): org.webswing.server.model.proto.FocusEventMsgProto;

                        /**
                         * Encodes the specified FocusEventMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.FocusEventMsgProto.verify|verify} messages.
                         * @param message FocusEventMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IFocusEventMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a FocusEventMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns FocusEventMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.FocusEventMsgProto;

                        /**
                         * Creates a FocusEventMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns FocusEventMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.FocusEventMsgProto;

                        /**
                         * Creates a plain object from a FocusEventMsgProto message. Also converts values to other types if specified.
                         * @param message FocusEventMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.FocusEventMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this FocusEventMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    namespace FocusEventMsgProto {

                        /** FocusEventTypeProto enum. */
                        enum FocusEventTypeProto {
                            focusLost = 1,
                            focusGained = 2,
                            focusWithCarretGained = 3,
                            focusPasswordGained = 4
                        }
                    }

                    /** Properties of an ApplicationInfoMsgProto. */
                    interface IApplicationInfoMsgProto {

                        /** ApplicationInfoMsgProto name */
                        name: string;

                        /** ApplicationInfoMsgProto base64Icon */
                        base64Icon?: (Uint8Array|null);

                        /** ApplicationInfoMsgProto url */
                        url?: (string|null);
                    }

                    /** Represents an ApplicationInfoMsgProto. */
                    class ApplicationInfoMsgProto implements IApplicationInfoMsgProto {

                        /**
                         * Constructs a new ApplicationInfoMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IApplicationInfoMsgProto);

                        /** ApplicationInfoMsgProto name. */
                        public name: string;

                        /** ApplicationInfoMsgProto base64Icon. */
                        public base64Icon: Uint8Array;

                        /** ApplicationInfoMsgProto url. */
                        public url: string;

                        /**
                         * Creates a new ApplicationInfoMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns ApplicationInfoMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IApplicationInfoMsgProto): org.webswing.server.model.proto.ApplicationInfoMsgProto;

                        /**
                         * Encodes the specified ApplicationInfoMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.ApplicationInfoMsgProto.verify|verify} messages.
                         * @param message ApplicationInfoMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IApplicationInfoMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes an ApplicationInfoMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns ApplicationInfoMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.ApplicationInfoMsgProto;

                        /**
                         * Creates an ApplicationInfoMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns ApplicationInfoMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.ApplicationInfoMsgProto;

                        /**
                         * Creates a plain object from an ApplicationInfoMsgProto message. Also converts values to other types if specified.
                         * @param message ApplicationInfoMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.ApplicationInfoMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this ApplicationInfoMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a LinkActionMsgProto. */
                    interface ILinkActionMsgProto {

                        /** LinkActionMsgProto action */
                        action: org.webswing.server.model.proto.LinkActionMsgProto.LinkActionTypeProto;

                        /** LinkActionMsgProto src */
                        src: string;
                    }

                    /** Represents a LinkActionMsgProto. */
                    class LinkActionMsgProto implements ILinkActionMsgProto {

                        /**
                         * Constructs a new LinkActionMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.ILinkActionMsgProto);

                        /** LinkActionMsgProto action. */
                        public action: org.webswing.server.model.proto.LinkActionMsgProto.LinkActionTypeProto;

                        /** LinkActionMsgProto src. */
                        public src: string;

                        /**
                         * Creates a new LinkActionMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns LinkActionMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.ILinkActionMsgProto): org.webswing.server.model.proto.LinkActionMsgProto;

                        /**
                         * Encodes the specified LinkActionMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.LinkActionMsgProto.verify|verify} messages.
                         * @param message LinkActionMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.ILinkActionMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a LinkActionMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns LinkActionMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.LinkActionMsgProto;

                        /**
                         * Creates a LinkActionMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns LinkActionMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.LinkActionMsgProto;

                        /**
                         * Creates a plain object from a LinkActionMsgProto message. Also converts values to other types if specified.
                         * @param message LinkActionMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.LinkActionMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this LinkActionMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    namespace LinkActionMsgProto {

                        /** LinkActionTypeProto enum. */
                        enum LinkActionTypeProto {
                            file = 0,
                            url = 1,
                            print = 2,
                            redirect = 3
                        }
                    }

                    /** Properties of a WindowMoveActionMsgProto. */
                    interface IWindowMoveActionMsgProto {

                        /** WindowMoveActionMsgProto sx */
                        sx?: (number|null);

                        /** WindowMoveActionMsgProto sy */
                        sy?: (number|null);

                        /** WindowMoveActionMsgProto dx */
                        dx?: (number|null);

                        /** WindowMoveActionMsgProto dy */
                        dy?: (number|null);

                        /** WindowMoveActionMsgProto width */
                        width?: (number|null);

                        /** WindowMoveActionMsgProto height */
                        height?: (number|null);
                    }

                    /** Represents a WindowMoveActionMsgProto. */
                    class WindowMoveActionMsgProto implements IWindowMoveActionMsgProto {

                        /**
                         * Constructs a new WindowMoveActionMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IWindowMoveActionMsgProto);

                        /** WindowMoveActionMsgProto sx. */
                        public sx: number;

                        /** WindowMoveActionMsgProto sy. */
                        public sy: number;

                        /** WindowMoveActionMsgProto dx. */
                        public dx: number;

                        /** WindowMoveActionMsgProto dy. */
                        public dy: number;

                        /** WindowMoveActionMsgProto width. */
                        public width: number;

                        /** WindowMoveActionMsgProto height. */
                        public height: number;

                        /**
                         * Creates a new WindowMoveActionMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns WindowMoveActionMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IWindowMoveActionMsgProto): org.webswing.server.model.proto.WindowMoveActionMsgProto;

                        /**
                         * Encodes the specified WindowMoveActionMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowMoveActionMsgProto.verify|verify} messages.
                         * @param message WindowMoveActionMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IWindowMoveActionMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a WindowMoveActionMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns WindowMoveActionMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.WindowMoveActionMsgProto;

                        /**
                         * Creates a WindowMoveActionMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns WindowMoveActionMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.WindowMoveActionMsgProto;

                        /**
                         * Creates a plain object from a WindowMoveActionMsgProto message. Also converts values to other types if specified.
                         * @param message WindowMoveActionMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.WindowMoveActionMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this WindowMoveActionMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a CursorChangeEventMsgProto. */
                    interface ICursorChangeEventMsgProto {

                        /** CursorChangeEventMsgProto cursor */
                        cursor: string;

                        /** CursorChangeEventMsgProto b64img */
                        b64img?: (Uint8Array|null);

                        /** CursorChangeEventMsgProto x */
                        x?: (number|null);

                        /** CursorChangeEventMsgProto y */
                        y?: (number|null);

                        /** CursorChangeEventMsgProto curFile */
                        curFile?: (string|null);

                        /** CursorChangeEventMsgProto winId */
                        winId?: (string|null);
                    }

                    /** Represents a CursorChangeEventMsgProto. */
                    class CursorChangeEventMsgProto implements ICursorChangeEventMsgProto {

                        /**
                         * Constructs a new CursorChangeEventMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.ICursorChangeEventMsgProto);

                        /** CursorChangeEventMsgProto cursor. */
                        public cursor: string;

                        /** CursorChangeEventMsgProto b64img. */
                        public b64img: Uint8Array;

                        /** CursorChangeEventMsgProto x. */
                        public x: number;

                        /** CursorChangeEventMsgProto y. */
                        public y: number;

                        /** CursorChangeEventMsgProto curFile. */
                        public curFile: string;

                        /** CursorChangeEventMsgProto winId. */
                        public winId: string;

                        /**
                         * Creates a new CursorChangeEventMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns CursorChangeEventMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.ICursorChangeEventMsgProto): org.webswing.server.model.proto.CursorChangeEventMsgProto;

                        /**
                         * Encodes the specified CursorChangeEventMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.CursorChangeEventMsgProto.verify|verify} messages.
                         * @param message CursorChangeEventMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.ICursorChangeEventMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a CursorChangeEventMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns CursorChangeEventMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.CursorChangeEventMsgProto;

                        /**
                         * Creates a CursorChangeEventMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns CursorChangeEventMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.CursorChangeEventMsgProto;

                        /**
                         * Creates a plain object from a CursorChangeEventMsgProto message. Also converts values to other types if specified.
                         * @param message CursorChangeEventMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.CursorChangeEventMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this CursorChangeEventMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a CopyEventMsgProto. */
                    interface ICopyEventMsgProto {

                        /** CopyEventMsgProto text */
                        text?: (string|null);

                        /** CopyEventMsgProto html */
                        html?: (string|null);

                        /** CopyEventMsgProto img */
                        img?: (Uint8Array|null);

                        /** CopyEventMsgProto files */
                        files?: (string[]|null);

                        /** CopyEventMsgProto other */
                        other?: (boolean|null);
                    }

                    /** Represents a CopyEventMsgProto. */
                    class CopyEventMsgProto implements ICopyEventMsgProto {

                        /**
                         * Constructs a new CopyEventMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.ICopyEventMsgProto);

                        /** CopyEventMsgProto text. */
                        public text: string;

                        /** CopyEventMsgProto html. */
                        public html: string;

                        /** CopyEventMsgProto img. */
                        public img: Uint8Array;

                        /** CopyEventMsgProto files. */
                        public files: string[];

                        /** CopyEventMsgProto other. */
                        public other: boolean;

                        /**
                         * Creates a new CopyEventMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns CopyEventMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.ICopyEventMsgProto): org.webswing.server.model.proto.CopyEventMsgProto;

                        /**
                         * Encodes the specified CopyEventMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.CopyEventMsgProto.verify|verify} messages.
                         * @param message CopyEventMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.ICopyEventMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a CopyEventMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns CopyEventMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.CopyEventMsgProto;

                        /**
                         * Creates a CopyEventMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns CopyEventMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.CopyEventMsgProto;

                        /**
                         * Creates a plain object from a CopyEventMsgProto message. Also converts values to other types if specified.
                         * @param message CopyEventMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.CopyEventMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this CopyEventMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a PasteRequestMsgProto. */
                    interface IPasteRequestMsgProto {

                        /** PasteRequestMsgProto title */
                        title?: (string|null);

                        /** PasteRequestMsgProto message */
                        message?: (string|null);
                    }

                    /** Represents a PasteRequestMsgProto. */
                    class PasteRequestMsgProto implements IPasteRequestMsgProto {

                        /**
                         * Constructs a new PasteRequestMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IPasteRequestMsgProto);

                        /** PasteRequestMsgProto title. */
                        public title: string;

                        /** PasteRequestMsgProto message. */
                        public message: string;

                        /**
                         * Creates a new PasteRequestMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns PasteRequestMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IPasteRequestMsgProto): org.webswing.server.model.proto.PasteRequestMsgProto;

                        /**
                         * Encodes the specified PasteRequestMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.PasteRequestMsgProto.verify|verify} messages.
                         * @param message PasteRequestMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IPasteRequestMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a PasteRequestMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns PasteRequestMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.PasteRequestMsgProto;

                        /**
                         * Creates a PasteRequestMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns PasteRequestMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.PasteRequestMsgProto;

                        /**
                         * Creates a plain object from a PasteRequestMsgProto message. Also converts values to other types if specified.
                         * @param message PasteRequestMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.PasteRequestMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this PasteRequestMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a FileDialogEventMsgProto. */
                    interface IFileDialogEventMsgProto {

                        /** FileDialogEventMsgProto eventType */
                        eventType: org.webswing.server.model.proto.FileDialogEventMsgProto.FileDialogEventTypeProto;

                        /** FileDialogEventMsgProto allowDownload */
                        allowDownload?: (boolean|null);

                        /** FileDialogEventMsgProto allowUpload */
                        allowUpload?: (boolean|null);

                        /** FileDialogEventMsgProto allowDelete */
                        allowDelete?: (boolean|null);

                        /** FileDialogEventMsgProto filter */
                        filter?: (string|null);

                        /** FileDialogEventMsgProto isMultiSelection */
                        isMultiSelection?: (boolean|null);

                        /** FileDialogEventMsgProto selection */
                        selection?: (string|null);
                    }

                    /** Represents a FileDialogEventMsgProto. */
                    class FileDialogEventMsgProto implements IFileDialogEventMsgProto {

                        /**
                         * Constructs a new FileDialogEventMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IFileDialogEventMsgProto);

                        /** FileDialogEventMsgProto eventType. */
                        public eventType: org.webswing.server.model.proto.FileDialogEventMsgProto.FileDialogEventTypeProto;

                        /** FileDialogEventMsgProto allowDownload. */
                        public allowDownload: boolean;

                        /** FileDialogEventMsgProto allowUpload. */
                        public allowUpload: boolean;

                        /** FileDialogEventMsgProto allowDelete. */
                        public allowDelete: boolean;

                        /** FileDialogEventMsgProto filter. */
                        public filter: string;

                        /** FileDialogEventMsgProto isMultiSelection. */
                        public isMultiSelection: boolean;

                        /** FileDialogEventMsgProto selection. */
                        public selection: string;

                        /**
                         * Creates a new FileDialogEventMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns FileDialogEventMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IFileDialogEventMsgProto): org.webswing.server.model.proto.FileDialogEventMsgProto;

                        /**
                         * Encodes the specified FileDialogEventMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.FileDialogEventMsgProto.verify|verify} messages.
                         * @param message FileDialogEventMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IFileDialogEventMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a FileDialogEventMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns FileDialogEventMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.FileDialogEventMsgProto;

                        /**
                         * Creates a FileDialogEventMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns FileDialogEventMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.FileDialogEventMsgProto;

                        /**
                         * Creates a plain object from a FileDialogEventMsgProto message. Also converts values to other types if specified.
                         * @param message FileDialogEventMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.FileDialogEventMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this FileDialogEventMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    namespace FileDialogEventMsgProto {

                        /** FileDialogEventTypeProto enum. */
                        enum FileDialogEventTypeProto {
                            Open = 0,
                            Close = 1,
                            AutoUpload = 2,
                            AutoSave = 3
                        }
                    }

                    /** Properties of a WindowSwitchMsg. */
                    interface IWindowSwitchMsg {

                        /** WindowSwitchMsg id */
                        id: string;

                        /** WindowSwitchMsg title */
                        title?: (string|null);

                        /** WindowSwitchMsg modalBlocked */
                        modalBlocked?: (boolean|null);
                    }

                    /** Represents a WindowSwitchMsg. */
                    class WindowSwitchMsg implements IWindowSwitchMsg {

                        /**
                         * Constructs a new WindowSwitchMsg.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IWindowSwitchMsg);

                        /** WindowSwitchMsg id. */
                        public id: string;

                        /** WindowSwitchMsg title. */
                        public title: string;

                        /** WindowSwitchMsg modalBlocked. */
                        public modalBlocked: boolean;

                        /**
                         * Creates a new WindowSwitchMsg instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns WindowSwitchMsg instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IWindowSwitchMsg): org.webswing.server.model.proto.WindowSwitchMsg;

                        /**
                         * Encodes the specified WindowSwitchMsg message. Does not implicitly {@link org.webswing.server.model.proto.WindowSwitchMsg.verify|verify} messages.
                         * @param message WindowSwitchMsg message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IWindowSwitchMsg, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a WindowSwitchMsg message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns WindowSwitchMsg
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.WindowSwitchMsg;

                        /**
                         * Creates a WindowSwitchMsg message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns WindowSwitchMsg
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.WindowSwitchMsg;

                        /**
                         * Creates a plain object from a WindowSwitchMsg message. Also converts values to other types if specified.
                         * @param message WindowSwitchMsg
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.WindowSwitchMsg, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this WindowSwitchMsg to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a WindowMsgProto. */
                    interface IWindowMsgProto {

                        /** WindowMsgProto id */
                        id: string;

                        /** WindowMsgProto content */
                        content?: (org.webswing.server.model.proto.IWindowPartialContentMsgProto[]|null);

                        /** WindowMsgProto directDraw */
                        directDraw?: (Uint8Array|null);

                        /** WindowMsgProto title */
                        title?: (string|null);

                        /** WindowMsgProto posX */
                        posX?: (number|null);

                        /** WindowMsgProto posY */
                        posY?: (number|null);

                        /** WindowMsgProto width */
                        width?: (number|null);

                        /** WindowMsgProto height */
                        height?: (number|null);

                        /** WindowMsgProto name */
                        name?: (string|null);

                        /** WindowMsgProto type */
                        type?: (org.webswing.server.model.proto.WindowMsgProto.WindowTypeProto|null);

                        /** WindowMsgProto modalBlocked */
                        modalBlocked?: (boolean|null);

                        /** WindowMsgProto ownerId */
                        ownerId?: (string|null);

                        /** WindowMsgProto state */
                        state?: (number|null);

                        /** WindowMsgProto internalWindows */
                        internalWindows?: (org.webswing.server.model.proto.IWindowMsgProto[]|null);

                        /** WindowMsgProto dockMode */
                        dockMode?: (org.webswing.server.model.proto.WindowMsgProto.DockModeProto|null);
                    }

                    /** Represents a WindowMsgProto. */
                    class WindowMsgProto implements IWindowMsgProto {

                        /**
                         * Constructs a new WindowMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IWindowMsgProto);

                        /** WindowMsgProto id. */
                        public id: string;

                        /** WindowMsgProto content. */
                        public content: org.webswing.server.model.proto.IWindowPartialContentMsgProto[];

                        /** WindowMsgProto directDraw. */
                        public directDraw: Uint8Array;

                        /** WindowMsgProto title. */
                        public title: string;

                        /** WindowMsgProto posX. */
                        public posX: number;

                        /** WindowMsgProto posY. */
                        public posY: number;

                        /** WindowMsgProto width. */
                        public width: number;

                        /** WindowMsgProto height. */
                        public height: number;

                        /** WindowMsgProto name. */
                        public name: string;

                        /** WindowMsgProto type. */
                        public type: org.webswing.server.model.proto.WindowMsgProto.WindowTypeProto;

                        /** WindowMsgProto modalBlocked. */
                        public modalBlocked: boolean;

                        /** WindowMsgProto ownerId. */
                        public ownerId: string;

                        /** WindowMsgProto state. */
                        public state: number;

                        /** WindowMsgProto internalWindows. */
                        public internalWindows: org.webswing.server.model.proto.IWindowMsgProto[];

                        /** WindowMsgProto dockMode. */
                        public dockMode: org.webswing.server.model.proto.WindowMsgProto.DockModeProto;

                        /**
                         * Creates a new WindowMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns WindowMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IWindowMsgProto): org.webswing.server.model.proto.WindowMsgProto;

                        /**
                         * Encodes the specified WindowMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowMsgProto.verify|verify} messages.
                         * @param message WindowMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IWindowMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a WindowMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns WindowMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.WindowMsgProto;

                        /**
                         * Creates a WindowMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns WindowMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.WindowMsgProto;

                        /**
                         * Creates a plain object from a WindowMsgProto message. Also converts values to other types if specified.
                         * @param message WindowMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.WindowMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this WindowMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    namespace WindowMsgProto {

                        /** WindowTypeProto enum. */
                        enum WindowTypeProto {
                            basic = 1,
                            html = 2,
                            internal = 3,
                            internalHtml = 4,
                            internalWrapper = 5
                        }

                        /** DockModeProto enum. */
                        enum DockModeProto {
                            none = 1,
                            dockable = 2,
                            autoUndock = 3
                        }
                    }

                    /** Properties of a WindowSwitchMsgProto. */
                    interface IWindowSwitchMsgProto {

                        /** WindowSwitchMsgProto id */
                        id: string;

                        /** WindowSwitchMsgProto title */
                        title?: (string|null);

                        /** WindowSwitchMsgProto modalBlocked */
                        modalBlocked?: (boolean|null);
                    }

                    /** Represents a WindowSwitchMsgProto. */
                    class WindowSwitchMsgProto implements IWindowSwitchMsgProto {

                        /**
                         * Constructs a new WindowSwitchMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IWindowSwitchMsgProto);

                        /** WindowSwitchMsgProto id. */
                        public id: string;

                        /** WindowSwitchMsgProto title. */
                        public title: string;

                        /** WindowSwitchMsgProto modalBlocked. */
                        public modalBlocked: boolean;

                        /**
                         * Creates a new WindowSwitchMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns WindowSwitchMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IWindowSwitchMsgProto): org.webswing.server.model.proto.WindowSwitchMsgProto;

                        /**
                         * Encodes the specified WindowSwitchMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowSwitchMsgProto.verify|verify} messages.
                         * @param message WindowSwitchMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IWindowSwitchMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a WindowSwitchMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns WindowSwitchMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.WindowSwitchMsgProto;

                        /**
                         * Creates a WindowSwitchMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns WindowSwitchMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.WindowSwitchMsgProto;

                        /**
                         * Creates a plain object from a WindowSwitchMsgProto message. Also converts values to other types if specified.
                         * @param message WindowSwitchMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.WindowSwitchMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this WindowSwitchMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a WindowPartialContentMsgProto. */
                    interface IWindowPartialContentMsgProto {

                        /** WindowPartialContentMsgProto positionX */
                        positionX?: (number|null);

                        /** WindowPartialContentMsgProto positionY */
                        positionY?: (number|null);

                        /** WindowPartialContentMsgProto width */
                        width?: (number|null);

                        /** WindowPartialContentMsgProto height */
                        height?: (number|null);

                        /** WindowPartialContentMsgProto base64Content */
                        base64Content?: (Uint8Array|null);
                    }

                    /** Represents a WindowPartialContentMsgProto. */
                    class WindowPartialContentMsgProto implements IWindowPartialContentMsgProto {

                        /**
                         * Constructs a new WindowPartialContentMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IWindowPartialContentMsgProto);

                        /** WindowPartialContentMsgProto positionX. */
                        public positionX: number;

                        /** WindowPartialContentMsgProto positionY. */
                        public positionY: number;

                        /** WindowPartialContentMsgProto width. */
                        public width: number;

                        /** WindowPartialContentMsgProto height. */
                        public height: number;

                        /** WindowPartialContentMsgProto base64Content. */
                        public base64Content: Uint8Array;

                        /**
                         * Creates a new WindowPartialContentMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns WindowPartialContentMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IWindowPartialContentMsgProto): org.webswing.server.model.proto.WindowPartialContentMsgProto;

                        /**
                         * Encodes the specified WindowPartialContentMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowPartialContentMsgProto.verify|verify} messages.
                         * @param message WindowPartialContentMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IWindowPartialContentMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a WindowPartialContentMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns WindowPartialContentMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.WindowPartialContentMsgProto;

                        /**
                         * Creates a WindowPartialContentMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns WindowPartialContentMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.WindowPartialContentMsgProto;

                        /**
                         * Creates a plain object from a WindowPartialContentMsgProto message. Also converts values to other types if specified.
                         * @param message WindowPartialContentMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.WindowPartialContentMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this WindowPartialContentMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** SimpleEventMsgOutProto enum. */
                    enum SimpleEventMsgOutProto {
                        applicationAlreadyRunning = 0,
                        shutDownNotification = 1,
                        tooManyClientsNotification = 2,
                        continueOldSession = 3,
                        configurationError = 4,
                        sessionStolenNotification = 5,
                        unauthorizedAccess = 6,
                        shutDownAutoLogoutNotification = 7,
                        sessionTimeoutWarning = 8,
                        sessionTimedOutNotification = 9,
                        applicationBusy = 10
                    }

                    /** Properties of a JsEvalRequestMsgOutProto. */
                    interface IJsEvalRequestMsgOutProto {

                        /** JsEvalRequestMsgOutProto correlationId */
                        correlationId?: (string|null);

                        /** JsEvalRequestMsgOutProto thisObjectId */
                        thisObjectId?: (string|null);

                        /** JsEvalRequestMsgOutProto type */
                        type?: (org.webswing.server.model.proto.JsEvalRequestMsgOutProto.JsEvalRequestTypeProto|null);

                        /** JsEvalRequestMsgOutProto evalString */
                        evalString?: (string|null);

                        /** JsEvalRequestMsgOutProto params */
                        params?: (org.webswing.server.model.proto.IJsParamMsgProto[]|null);

                        /** JsEvalRequestMsgOutProto garbageIds */
                        garbageIds?: (string[]|null);
                    }

                    /** Represents a JsEvalRequestMsgOutProto. */
                    class JsEvalRequestMsgOutProto implements IJsEvalRequestMsgOutProto {

                        /**
                         * Constructs a new JsEvalRequestMsgOutProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IJsEvalRequestMsgOutProto);

                        /** JsEvalRequestMsgOutProto correlationId. */
                        public correlationId: string;

                        /** JsEvalRequestMsgOutProto thisObjectId. */
                        public thisObjectId: string;

                        /** JsEvalRequestMsgOutProto type. */
                        public type: org.webswing.server.model.proto.JsEvalRequestMsgOutProto.JsEvalRequestTypeProto;

                        /** JsEvalRequestMsgOutProto evalString. */
                        public evalString: string;

                        /** JsEvalRequestMsgOutProto params. */
                        public params: org.webswing.server.model.proto.IJsParamMsgProto[];

                        /** JsEvalRequestMsgOutProto garbageIds. */
                        public garbageIds: string[];

                        /**
                         * Creates a new JsEvalRequestMsgOutProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns JsEvalRequestMsgOutProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IJsEvalRequestMsgOutProto): org.webswing.server.model.proto.JsEvalRequestMsgOutProto;

                        /**
                         * Encodes the specified JsEvalRequestMsgOutProto message. Does not implicitly {@link org.webswing.server.model.proto.JsEvalRequestMsgOutProto.verify|verify} messages.
                         * @param message JsEvalRequestMsgOutProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IJsEvalRequestMsgOutProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a JsEvalRequestMsgOutProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns JsEvalRequestMsgOutProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.JsEvalRequestMsgOutProto;

                        /**
                         * Creates a JsEvalRequestMsgOutProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns JsEvalRequestMsgOutProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.JsEvalRequestMsgOutProto;

                        /**
                         * Creates a plain object from a JsEvalRequestMsgOutProto message. Also converts values to other types if specified.
                         * @param message JsEvalRequestMsgOutProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.JsEvalRequestMsgOutProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this JsEvalRequestMsgOutProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    namespace JsEvalRequestMsgOutProto {

                        /** JsEvalRequestTypeProto enum. */
                        enum JsEvalRequestTypeProto {
                            eval = 0,
                            call = 1,
                            setMember = 2,
                            getMember = 3,
                            deleteMember = 4,
                            setSlot = 5,
                            getSlot = 6
                        }
                    }

                    /** Properties of a JsParamMsgProto. */
                    interface IJsParamMsgProto {

                        /** JsParamMsgProto primitive */
                        primitive?: (string|null);

                        /** JsParamMsgProto jsObject */
                        jsObject?: (org.webswing.server.model.proto.IJSObjectMsgProto|null);

                        /** JsParamMsgProto javaObject */
                        javaObject?: (org.webswing.server.model.proto.IJavaObjectRefMsgProto|null);

                        /** JsParamMsgProto array */
                        array?: (org.webswing.server.model.proto.IJsParamMsgProto[]|null);
                    }

                    /** Represents a JsParamMsgProto. */
                    class JsParamMsgProto implements IJsParamMsgProto {

                        /**
                         * Constructs a new JsParamMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IJsParamMsgProto);

                        /** JsParamMsgProto primitive. */
                        public primitive: string;

                        /** JsParamMsgProto jsObject. */
                        public jsObject?: (org.webswing.server.model.proto.IJSObjectMsgProto|null);

                        /** JsParamMsgProto javaObject. */
                        public javaObject?: (org.webswing.server.model.proto.IJavaObjectRefMsgProto|null);

                        /** JsParamMsgProto array. */
                        public array: org.webswing.server.model.proto.IJsParamMsgProto[];

                        /**
                         * Creates a new JsParamMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns JsParamMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IJsParamMsgProto): org.webswing.server.model.proto.JsParamMsgProto;

                        /**
                         * Encodes the specified JsParamMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.JsParamMsgProto.verify|verify} messages.
                         * @param message JsParamMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IJsParamMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a JsParamMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns JsParamMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.JsParamMsgProto;

                        /**
                         * Creates a JsParamMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns JsParamMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.JsParamMsgProto;

                        /**
                         * Creates a plain object from a JsParamMsgProto message. Also converts values to other types if specified.
                         * @param message JsParamMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.JsParamMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this JsParamMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a JSObjectMsgProto. */
                    interface IJSObjectMsgProto {

                        /** JSObjectMsgProto id */
                        id?: (string|null);
                    }

                    /** Represents a JSObjectMsgProto. */
                    class JSObjectMsgProto implements IJSObjectMsgProto {

                        /**
                         * Constructs a new JSObjectMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IJSObjectMsgProto);

                        /** JSObjectMsgProto id. */
                        public id: string;

                        /**
                         * Creates a new JSObjectMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns JSObjectMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IJSObjectMsgProto): org.webswing.server.model.proto.JSObjectMsgProto;

                        /**
                         * Encodes the specified JSObjectMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.JSObjectMsgProto.verify|verify} messages.
                         * @param message JSObjectMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IJSObjectMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a JSObjectMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns JSObjectMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.JSObjectMsgProto;

                        /**
                         * Creates a JSObjectMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns JSObjectMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.JSObjectMsgProto;

                        /**
                         * Creates a plain object from a JSObjectMsgProto message. Also converts values to other types if specified.
                         * @param message JSObjectMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.JSObjectMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this JSObjectMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a JavaObjectRefMsgProto. */
                    interface IJavaObjectRefMsgProto {

                        /** JavaObjectRefMsgProto id */
                        id?: (string|null);

                        /** JavaObjectRefMsgProto methods */
                        methods?: (string[]|null);
                    }

                    /** Represents a JavaObjectRefMsgProto. */
                    class JavaObjectRefMsgProto implements IJavaObjectRefMsgProto {

                        /**
                         * Constructs a new JavaObjectRefMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IJavaObjectRefMsgProto);

                        /** JavaObjectRefMsgProto id. */
                        public id: string;

                        /** JavaObjectRefMsgProto methods. */
                        public methods: string[];

                        /**
                         * Creates a new JavaObjectRefMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns JavaObjectRefMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IJavaObjectRefMsgProto): org.webswing.server.model.proto.JavaObjectRefMsgProto;

                        /**
                         * Encodes the specified JavaObjectRefMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.JavaObjectRefMsgProto.verify|verify} messages.
                         * @param message JavaObjectRefMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IJavaObjectRefMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a JavaObjectRefMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns JavaObjectRefMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.JavaObjectRefMsgProto;

                        /**
                         * Creates a JavaObjectRefMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns JavaObjectRefMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.JavaObjectRefMsgProto;

                        /**
                         * Creates a plain object from a JavaObjectRefMsgProto message. Also converts values to other types if specified.
                         * @param message JavaObjectRefMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.JavaObjectRefMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this JavaObjectRefMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a JsResultMsgProto. */
                    interface IJsResultMsgProto {

                        /** JsResultMsgProto correlationId */
                        correlationId?: (string|null);

                        /** JsResultMsgProto error */
                        error?: (string|null);

                        /** JsResultMsgProto value */
                        value?: (org.webswing.server.model.proto.IJsParamMsgProto|null);
                    }

                    /** Represents a JsResultMsgProto. */
                    class JsResultMsgProto implements IJsResultMsgProto {

                        /**
                         * Constructs a new JsResultMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IJsResultMsgProto);

                        /** JsResultMsgProto correlationId. */
                        public correlationId: string;

                        /** JsResultMsgProto error. */
                        public error: string;

                        /** JsResultMsgProto value. */
                        public value?: (org.webswing.server.model.proto.IJsParamMsgProto|null);

                        /**
                         * Creates a new JsResultMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns JsResultMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IJsResultMsgProto): org.webswing.server.model.proto.JsResultMsgProto;

                        /**
                         * Encodes the specified JsResultMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.JsResultMsgProto.verify|verify} messages.
                         * @param message JsResultMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IJsResultMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a JsResultMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns JsResultMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.JsResultMsgProto;

                        /**
                         * Creates a JsResultMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns JsResultMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.JsResultMsgProto;

                        /**
                         * Creates a plain object from a JsResultMsgProto message. Also converts values to other types if specified.
                         * @param message JsResultMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.JsResultMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this JsResultMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a PlaybackInfoMsgProto. */
                    interface IPlaybackInfoMsgProto {

                        /** PlaybackInfoMsgProto current */
                        current?: (number|null);

                        /** PlaybackInfoMsgProto total */
                        total?: (number|null);
                    }

                    /** Represents a PlaybackInfoMsgProto. */
                    class PlaybackInfoMsgProto implements IPlaybackInfoMsgProto {

                        /**
                         * Constructs a new PlaybackInfoMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IPlaybackInfoMsgProto);

                        /** PlaybackInfoMsgProto current. */
                        public current: number;

                        /** PlaybackInfoMsgProto total. */
                        public total: number;

                        /**
                         * Creates a new PlaybackInfoMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns PlaybackInfoMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IPlaybackInfoMsgProto): org.webswing.server.model.proto.PlaybackInfoMsgProto;

                        /**
                         * Encodes the specified PlaybackInfoMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.PlaybackInfoMsgProto.verify|verify} messages.
                         * @param message PlaybackInfoMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IPlaybackInfoMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a PlaybackInfoMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns PlaybackInfoMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.PlaybackInfoMsgProto;

                        /**
                         * Creates a PlaybackInfoMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns PlaybackInfoMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.PlaybackInfoMsgProto;

                        /**
                         * Creates a plain object from a PlaybackInfoMsgProto message. Also converts values to other types if specified.
                         * @param message PlaybackInfoMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.PlaybackInfoMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this PlaybackInfoMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a PixelsAreaRequestMsgOutProto. */
                    interface IPixelsAreaRequestMsgOutProto {

                        /** PixelsAreaRequestMsgOutProto correlationId */
                        correlationId?: (string|null);

                        /** PixelsAreaRequestMsgOutProto x */
                        x?: (number|null);

                        /** PixelsAreaRequestMsgOutProto y */
                        y?: (number|null);

                        /** PixelsAreaRequestMsgOutProto w */
                        w?: (number|null);

                        /** PixelsAreaRequestMsgOutProto h */
                        h?: (number|null);
                    }

                    /** Represents a PixelsAreaRequestMsgOutProto. */
                    class PixelsAreaRequestMsgOutProto implements IPixelsAreaRequestMsgOutProto {

                        /**
                         * Constructs a new PixelsAreaRequestMsgOutProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IPixelsAreaRequestMsgOutProto);

                        /** PixelsAreaRequestMsgOutProto correlationId. */
                        public correlationId: string;

                        /** PixelsAreaRequestMsgOutProto x. */
                        public x: number;

                        /** PixelsAreaRequestMsgOutProto y. */
                        public y: number;

                        /** PixelsAreaRequestMsgOutProto w. */
                        public w: number;

                        /** PixelsAreaRequestMsgOutProto h. */
                        public h: number;

                        /**
                         * Creates a new PixelsAreaRequestMsgOutProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns PixelsAreaRequestMsgOutProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IPixelsAreaRequestMsgOutProto): org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto;

                        /**
                         * Encodes the specified PixelsAreaRequestMsgOutProto message. Does not implicitly {@link org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto.verify|verify} messages.
                         * @param message PixelsAreaRequestMsgOutProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IPixelsAreaRequestMsgOutProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a PixelsAreaRequestMsgOutProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns PixelsAreaRequestMsgOutProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto;

                        /**
                         * Creates a PixelsAreaRequestMsgOutProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns PixelsAreaRequestMsgOutProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto;

                        /**
                         * Creates a plain object from a PixelsAreaRequestMsgOutProto message. Also converts values to other types if specified.
                         * @param message PixelsAreaRequestMsgOutProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.PixelsAreaRequestMsgOutProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this PixelsAreaRequestMsgOutProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of an InputEventsFrameMsgInProto. */
                    interface IInputEventsFrameMsgInProto {

                        /** InputEventsFrameMsgInProto events */
                        events?: (org.webswing.server.model.proto.IInputEventMsgInProto[]|null);

                        /** InputEventsFrameMsgInProto paste */
                        paste?: (org.webswing.server.model.proto.IPasteEventMsgInProto|null);

                        /** InputEventsFrameMsgInProto copy */
                        copy?: (org.webswing.server.model.proto.ICopyEventMsgInProto|null);

                        /** InputEventsFrameMsgInProto upload */
                        upload?: (org.webswing.server.model.proto.IUploadEventMsgInProto|null);

                        /** InputEventsFrameMsgInProto selected */
                        selected?: (org.webswing.server.model.proto.IFilesSelectedEventMsgInProto|null);

                        /** InputEventsFrameMsgInProto jsResponse */
                        jsResponse?: (org.webswing.server.model.proto.IJsResultMsgProto|null);

                        /** InputEventsFrameMsgInProto javaRequest */
                        javaRequest?: (org.webswing.server.model.proto.IJavaEvalRequestMsgInProto|null);

                        /** InputEventsFrameMsgInProto playback */
                        playback?: (org.webswing.server.model.proto.IPlaybackCommandMsgInProto|null);

                        /** InputEventsFrameMsgInProto pixelsResponse */
                        pixelsResponse?: (org.webswing.server.model.proto.IPixelsAreaResponseMsgInProto|null);

                        /** InputEventsFrameMsgInProto window */
                        window?: (org.webswing.server.model.proto.IWindowEventMsgInProto|null);

                        /** InputEventsFrameMsgInProto action */
                        action?: (org.webswing.server.model.proto.IActionEventMsgInProto|null);
                    }

                    /** Represents an InputEventsFrameMsgInProto. */
                    class InputEventsFrameMsgInProto implements IInputEventsFrameMsgInProto {

                        /**
                         * Constructs a new InputEventsFrameMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IInputEventsFrameMsgInProto);

                        /** InputEventsFrameMsgInProto events. */
                        public events: org.webswing.server.model.proto.IInputEventMsgInProto[];

                        /** InputEventsFrameMsgInProto paste. */
                        public paste?: (org.webswing.server.model.proto.IPasteEventMsgInProto|null);

                        /** InputEventsFrameMsgInProto copy. */
                        public copy?: (org.webswing.server.model.proto.ICopyEventMsgInProto|null);

                        /** InputEventsFrameMsgInProto upload. */
                        public upload?: (org.webswing.server.model.proto.IUploadEventMsgInProto|null);

                        /** InputEventsFrameMsgInProto selected. */
                        public selected?: (org.webswing.server.model.proto.IFilesSelectedEventMsgInProto|null);

                        /** InputEventsFrameMsgInProto jsResponse. */
                        public jsResponse?: (org.webswing.server.model.proto.IJsResultMsgProto|null);

                        /** InputEventsFrameMsgInProto javaRequest. */
                        public javaRequest?: (org.webswing.server.model.proto.IJavaEvalRequestMsgInProto|null);

                        /** InputEventsFrameMsgInProto playback. */
                        public playback?: (org.webswing.server.model.proto.IPlaybackCommandMsgInProto|null);

                        /** InputEventsFrameMsgInProto pixelsResponse. */
                        public pixelsResponse?: (org.webswing.server.model.proto.IPixelsAreaResponseMsgInProto|null);

                        /** InputEventsFrameMsgInProto window. */
                        public window?: (org.webswing.server.model.proto.IWindowEventMsgInProto|null);

                        /** InputEventsFrameMsgInProto action. */
                        public action?: (org.webswing.server.model.proto.IActionEventMsgInProto|null);

                        /**
                         * Creates a new InputEventsFrameMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns InputEventsFrameMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IInputEventsFrameMsgInProto): org.webswing.server.model.proto.InputEventsFrameMsgInProto;

                        /**
                         * Encodes the specified InputEventsFrameMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.InputEventsFrameMsgInProto.verify|verify} messages.
                         * @param message InputEventsFrameMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IInputEventsFrameMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes an InputEventsFrameMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns InputEventsFrameMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.InputEventsFrameMsgInProto;

                        /**
                         * Creates an InputEventsFrameMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns InputEventsFrameMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.InputEventsFrameMsgInProto;

                        /**
                         * Creates a plain object from an InputEventsFrameMsgInProto message. Also converts values to other types if specified.
                         * @param message InputEventsFrameMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.InputEventsFrameMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this InputEventsFrameMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of an InputEventMsgInProto. */
                    interface IInputEventMsgInProto {

                        /** InputEventMsgInProto handshake */
                        handshake?: (org.webswing.server.model.proto.IConnectionHandshakeMsgInProto|null);

                        /** InputEventMsgInProto key */
                        key?: (org.webswing.server.model.proto.IKeyboardEventMsgInProto|null);

                        /** InputEventMsgInProto mouse */
                        mouse?: (org.webswing.server.model.proto.IMouseEventMsgInProto|null);

                        /** InputEventMsgInProto event */
                        event?: (org.webswing.server.model.proto.ISimpleEventMsgInProto|null);

                        /** InputEventMsgInProto timestamps */
                        timestamps?: (org.webswing.server.model.proto.ITimestampsMsgInProto|null);

                        /** InputEventMsgInProto focus */
                        focus?: (org.webswing.server.model.proto.IWindowFocusMsgInProto|null);
                    }

                    /** Represents an InputEventMsgInProto. */
                    class InputEventMsgInProto implements IInputEventMsgInProto {

                        /**
                         * Constructs a new InputEventMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IInputEventMsgInProto);

                        /** InputEventMsgInProto handshake. */
                        public handshake?: (org.webswing.server.model.proto.IConnectionHandshakeMsgInProto|null);

                        /** InputEventMsgInProto key. */
                        public key?: (org.webswing.server.model.proto.IKeyboardEventMsgInProto|null);

                        /** InputEventMsgInProto mouse. */
                        public mouse?: (org.webswing.server.model.proto.IMouseEventMsgInProto|null);

                        /** InputEventMsgInProto event. */
                        public event?: (org.webswing.server.model.proto.ISimpleEventMsgInProto|null);

                        /** InputEventMsgInProto timestamps. */
                        public timestamps?: (org.webswing.server.model.proto.ITimestampsMsgInProto|null);

                        /** InputEventMsgInProto focus. */
                        public focus?: (org.webswing.server.model.proto.IWindowFocusMsgInProto|null);

                        /**
                         * Creates a new InputEventMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns InputEventMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IInputEventMsgInProto): org.webswing.server.model.proto.InputEventMsgInProto;

                        /**
                         * Encodes the specified InputEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.InputEventMsgInProto.verify|verify} messages.
                         * @param message InputEventMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IInputEventMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes an InputEventMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns InputEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.InputEventMsgInProto;

                        /**
                         * Creates an InputEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns InputEventMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.InputEventMsgInProto;

                        /**
                         * Creates a plain object from an InputEventMsgInProto message. Also converts values to other types if specified.
                         * @param message InputEventMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.InputEventMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this InputEventMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a WindowFocusMsgInProto. */
                    interface IWindowFocusMsgInProto {

                        /** WindowFocusMsgInProto windowId */
                        windowId?: (string|null);

                        /** WindowFocusMsgInProto htmlPanelId */
                        htmlPanelId?: (string|null);
                    }

                    /** Represents a WindowFocusMsgInProto. */
                    class WindowFocusMsgInProto implements IWindowFocusMsgInProto {

                        /**
                         * Constructs a new WindowFocusMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IWindowFocusMsgInProto);

                        /** WindowFocusMsgInProto windowId. */
                        public windowId: string;

                        /** WindowFocusMsgInProto htmlPanelId. */
                        public htmlPanelId: string;

                        /**
                         * Creates a new WindowFocusMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns WindowFocusMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IWindowFocusMsgInProto): org.webswing.server.model.proto.WindowFocusMsgInProto;

                        /**
                         * Encodes the specified WindowFocusMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowFocusMsgInProto.verify|verify} messages.
                         * @param message WindowFocusMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IWindowFocusMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a WindowFocusMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns WindowFocusMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.WindowFocusMsgInProto;

                        /**
                         * Creates a WindowFocusMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns WindowFocusMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.WindowFocusMsgInProto;

                        /**
                         * Creates a plain object from a WindowFocusMsgInProto message. Also converts values to other types if specified.
                         * @param message WindowFocusMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.WindowFocusMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this WindowFocusMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a TimestampsMsgInProto. */
                    interface ITimestampsMsgInProto {

                        /** TimestampsMsgInProto startTimestamp */
                        startTimestamp?: (string|null);

                        /** TimestampsMsgInProto sendTimestamp */
                        sendTimestamp?: (string|null);

                        /** TimestampsMsgInProto renderingTime */
                        renderingTime?: (string|null);

                        /** TimestampsMsgInProto ping */
                        ping?: (number|null);
                    }

                    /** Represents a TimestampsMsgInProto. */
                    class TimestampsMsgInProto implements ITimestampsMsgInProto {

                        /**
                         * Constructs a new TimestampsMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.ITimestampsMsgInProto);

                        /** TimestampsMsgInProto startTimestamp. */
                        public startTimestamp: string;

                        /** TimestampsMsgInProto sendTimestamp. */
                        public sendTimestamp: string;

                        /** TimestampsMsgInProto renderingTime. */
                        public renderingTime: string;

                        /** TimestampsMsgInProto ping. */
                        public ping: number;

                        /**
                         * Creates a new TimestampsMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns TimestampsMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.ITimestampsMsgInProto): org.webswing.server.model.proto.TimestampsMsgInProto;

                        /**
                         * Encodes the specified TimestampsMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.TimestampsMsgInProto.verify|verify} messages.
                         * @param message TimestampsMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.ITimestampsMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a TimestampsMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns TimestampsMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.TimestampsMsgInProto;

                        /**
                         * Creates a TimestampsMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns TimestampsMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.TimestampsMsgInProto;

                        /**
                         * Creates a plain object from a TimestampsMsgInProto message. Also converts values to other types if specified.
                         * @param message TimestampsMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.TimestampsMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this TimestampsMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a ConnectionHandshakeMsgInProto. */
                    interface IConnectionHandshakeMsgInProto {

                        /** ConnectionHandshakeMsgInProto clientId */
                        clientId?: (string|null);

                        /** ConnectionHandshakeMsgInProto connectionId */
                        connectionId?: (string|null);

                        /** ConnectionHandshakeMsgInProto viewId */
                        viewId?: (string|null);

                        /** ConnectionHandshakeMsgInProto browserId */
                        browserId?: (string|null);

                        /** ConnectionHandshakeMsgInProto desktopWidth */
                        desktopWidth?: (number|null);

                        /** ConnectionHandshakeMsgInProto desktopHeight */
                        desktopHeight?: (number|null);

                        /** ConnectionHandshakeMsgInProto applicationName */
                        applicationName?: (string|null);

                        /** ConnectionHandshakeMsgInProto mirrored */
                        mirrored?: (boolean|null);

                        /** ConnectionHandshakeMsgInProto directDrawSupported */
                        directDrawSupported?: (boolean|null);

                        /** ConnectionHandshakeMsgInProto documentBase */
                        documentBase?: (string|null);

                        /** ConnectionHandshakeMsgInProto params */
                        params?: (org.webswing.server.model.proto.IParamMsgProto[]|null);

                        /** ConnectionHandshakeMsgInProto locale */
                        locale?: (string|null);

                        /** ConnectionHandshakeMsgInProto url */
                        url?: (string|null);

                        /** ConnectionHandshakeMsgInProto timeZone */
                        timeZone?: (string|null);

                        /** ConnectionHandshakeMsgInProto dockingSupported */
                        dockingSupported?: (boolean|null);

                        /** ConnectionHandshakeMsgInProto touchMode */
                        touchMode?: (boolean|null);

                        /** ConnectionHandshakeMsgInProto accessiblityEnabled */
                        accessiblityEnabled?: (boolean|null);
                    }

                    /** Represents a ConnectionHandshakeMsgInProto. */
                    class ConnectionHandshakeMsgInProto implements IConnectionHandshakeMsgInProto {

                        /**
                         * Constructs a new ConnectionHandshakeMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IConnectionHandshakeMsgInProto);

                        /** ConnectionHandshakeMsgInProto clientId. */
                        public clientId: string;

                        /** ConnectionHandshakeMsgInProto connectionId. */
                        public connectionId: string;

                        /** ConnectionHandshakeMsgInProto viewId. */
                        public viewId: string;

                        /** ConnectionHandshakeMsgInProto browserId. */
                        public browserId: string;

                        /** ConnectionHandshakeMsgInProto desktopWidth. */
                        public desktopWidth: number;

                        /** ConnectionHandshakeMsgInProto desktopHeight. */
                        public desktopHeight: number;

                        /** ConnectionHandshakeMsgInProto applicationName. */
                        public applicationName: string;

                        /** ConnectionHandshakeMsgInProto mirrored. */
                        public mirrored: boolean;

                        /** ConnectionHandshakeMsgInProto directDrawSupported. */
                        public directDrawSupported: boolean;

                        /** ConnectionHandshakeMsgInProto documentBase. */
                        public documentBase: string;

                        /** ConnectionHandshakeMsgInProto params. */
                        public params: org.webswing.server.model.proto.IParamMsgProto[];

                        /** ConnectionHandshakeMsgInProto locale. */
                        public locale: string;

                        /** ConnectionHandshakeMsgInProto url. */
                        public url: string;

                        /** ConnectionHandshakeMsgInProto timeZone. */
                        public timeZone: string;

                        /** ConnectionHandshakeMsgInProto dockingSupported. */
                        public dockingSupported: boolean;

                        /** ConnectionHandshakeMsgInProto touchMode. */
                        public touchMode: boolean;

                        /** ConnectionHandshakeMsgInProto accessiblityEnabled. */
                        public accessiblityEnabled: boolean;

                        /**
                         * Creates a new ConnectionHandshakeMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns ConnectionHandshakeMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IConnectionHandshakeMsgInProto): org.webswing.server.model.proto.ConnectionHandshakeMsgInProto;

                        /**
                         * Encodes the specified ConnectionHandshakeMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.ConnectionHandshakeMsgInProto.verify|verify} messages.
                         * @param message ConnectionHandshakeMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IConnectionHandshakeMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a ConnectionHandshakeMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns ConnectionHandshakeMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.ConnectionHandshakeMsgInProto;

                        /**
                         * Creates a ConnectionHandshakeMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns ConnectionHandshakeMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.ConnectionHandshakeMsgInProto;

                        /**
                         * Creates a plain object from a ConnectionHandshakeMsgInProto message. Also converts values to other types if specified.
                         * @param message ConnectionHandshakeMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.ConnectionHandshakeMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this ConnectionHandshakeMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a ParamMsgProto. */
                    interface IParamMsgProto {

                        /** ParamMsgProto name */
                        name?: (string|null);

                        /** ParamMsgProto value */
                        value?: (string|null);
                    }

                    /** Represents a ParamMsgProto. */
                    class ParamMsgProto implements IParamMsgProto {

                        /**
                         * Constructs a new ParamMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IParamMsgProto);

                        /** ParamMsgProto name. */
                        public name: string;

                        /** ParamMsgProto value. */
                        public value: string;

                        /**
                         * Creates a new ParamMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns ParamMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IParamMsgProto): org.webswing.server.model.proto.ParamMsgProto;

                        /**
                         * Encodes the specified ParamMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.ParamMsgProto.verify|verify} messages.
                         * @param message ParamMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IParamMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a ParamMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns ParamMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.ParamMsgProto;

                        /**
                         * Creates a ParamMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns ParamMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.ParamMsgProto;

                        /**
                         * Creates a plain object from a ParamMsgProto message. Also converts values to other types if specified.
                         * @param message ParamMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.ParamMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this ParamMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a KeyboardEventMsgInProto. */
                    interface IKeyboardEventMsgInProto {

                        /** KeyboardEventMsgInProto type */
                        type?: (org.webswing.server.model.proto.KeyboardEventMsgInProto.KeyEventTypeProto|null);

                        /** KeyboardEventMsgInProto character */
                        character?: (number|null);

                        /** KeyboardEventMsgInProto keycode */
                        keycode?: (number|null);

                        /** KeyboardEventMsgInProto alt */
                        alt?: (boolean|null);

                        /** KeyboardEventMsgInProto ctrl */
                        ctrl?: (boolean|null);

                        /** KeyboardEventMsgInProto shift */
                        shift?: (boolean|null);

                        /** KeyboardEventMsgInProto meta */
                        meta?: (boolean|null);
                    }

                    /** Represents a KeyboardEventMsgInProto. */
                    class KeyboardEventMsgInProto implements IKeyboardEventMsgInProto {

                        /**
                         * Constructs a new KeyboardEventMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IKeyboardEventMsgInProto);

                        /** KeyboardEventMsgInProto type. */
                        public type: org.webswing.server.model.proto.KeyboardEventMsgInProto.KeyEventTypeProto;

                        /** KeyboardEventMsgInProto character. */
                        public character: number;

                        /** KeyboardEventMsgInProto keycode. */
                        public keycode: number;

                        /** KeyboardEventMsgInProto alt. */
                        public alt: boolean;

                        /** KeyboardEventMsgInProto ctrl. */
                        public ctrl: boolean;

                        /** KeyboardEventMsgInProto shift. */
                        public shift: boolean;

                        /** KeyboardEventMsgInProto meta. */
                        public meta: boolean;

                        /**
                         * Creates a new KeyboardEventMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns KeyboardEventMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IKeyboardEventMsgInProto): org.webswing.server.model.proto.KeyboardEventMsgInProto;

                        /**
                         * Encodes the specified KeyboardEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.KeyboardEventMsgInProto.verify|verify} messages.
                         * @param message KeyboardEventMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IKeyboardEventMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a KeyboardEventMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns KeyboardEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.KeyboardEventMsgInProto;

                        /**
                         * Creates a KeyboardEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns KeyboardEventMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.KeyboardEventMsgInProto;

                        /**
                         * Creates a plain object from a KeyboardEventMsgInProto message. Also converts values to other types if specified.
                         * @param message KeyboardEventMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.KeyboardEventMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this KeyboardEventMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    namespace KeyboardEventMsgInProto {

                        /** KeyEventTypeProto enum. */
                        enum KeyEventTypeProto {
                            keypress = 0,
                            keydown = 1,
                            keyup = 2
                        }
                    }

                    /** Properties of a MouseEventMsgInProto. */
                    interface IMouseEventMsgInProto {

                        /** MouseEventMsgInProto type */
                        type?: (org.webswing.server.model.proto.MouseEventMsgInProto.MouseEventTypeProto|null);

                        /** MouseEventMsgInProto x */
                        x?: (number|null);

                        /** MouseEventMsgInProto y */
                        y?: (number|null);

                        /** MouseEventMsgInProto wheelDelta */
                        wheelDelta?: (number|null);

                        /** MouseEventMsgInProto button */
                        button?: (number|null);

                        /** MouseEventMsgInProto ctrl */
                        ctrl?: (boolean|null);

                        /** MouseEventMsgInProto alt */
                        alt?: (boolean|null);

                        /** MouseEventMsgInProto shift */
                        shift?: (boolean|null);

                        /** MouseEventMsgInProto meta */
                        meta?: (boolean|null);

                        /** MouseEventMsgInProto buttons */
                        buttons?: (number|null);

                        /** MouseEventMsgInProto timeMilis */
                        timeMilis?: (number|null);

                        /** MouseEventMsgInProto winId */
                        winId?: (string|null);
                    }

                    /** Represents a MouseEventMsgInProto. */
                    class MouseEventMsgInProto implements IMouseEventMsgInProto {

                        /**
                         * Constructs a new MouseEventMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IMouseEventMsgInProto);

                        /** MouseEventMsgInProto type. */
                        public type: org.webswing.server.model.proto.MouseEventMsgInProto.MouseEventTypeProto;

                        /** MouseEventMsgInProto x. */
                        public x: number;

                        /** MouseEventMsgInProto y. */
                        public y: number;

                        /** MouseEventMsgInProto wheelDelta. */
                        public wheelDelta: number;

                        /** MouseEventMsgInProto button. */
                        public button: number;

                        /** MouseEventMsgInProto ctrl. */
                        public ctrl: boolean;

                        /** MouseEventMsgInProto alt. */
                        public alt: boolean;

                        /** MouseEventMsgInProto shift. */
                        public shift: boolean;

                        /** MouseEventMsgInProto meta. */
                        public meta: boolean;

                        /** MouseEventMsgInProto buttons. */
                        public buttons: number;

                        /** MouseEventMsgInProto timeMilis. */
                        public timeMilis: number;

                        /** MouseEventMsgInProto winId. */
                        public winId: string;

                        /**
                         * Creates a new MouseEventMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns MouseEventMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IMouseEventMsgInProto): org.webswing.server.model.proto.MouseEventMsgInProto;

                        /**
                         * Encodes the specified MouseEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.MouseEventMsgInProto.verify|verify} messages.
                         * @param message MouseEventMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IMouseEventMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a MouseEventMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns MouseEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.MouseEventMsgInProto;

                        /**
                         * Creates a MouseEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns MouseEventMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.MouseEventMsgInProto;

                        /**
                         * Creates a plain object from a MouseEventMsgInProto message. Also converts values to other types if specified.
                         * @param message MouseEventMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.MouseEventMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this MouseEventMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    namespace MouseEventMsgInProto {

                        /** MouseEventTypeProto enum. */
                        enum MouseEventTypeProto {
                            mousemove = 0,
                            mousedown = 1,
                            mouseup = 2,
                            mousewheel = 3,
                            dblclick = 4
                        }
                    }

                    /** Properties of a CopyEventMsgInProto. */
                    interface ICopyEventMsgInProto {

                        /** CopyEventMsgInProto type */
                        type?: (org.webswing.server.model.proto.CopyEventMsgInProto.CopyEventMsgTypeProto|null);

                        /** CopyEventMsgInProto file */
                        file?: (string|null);
                    }

                    /** Represents a CopyEventMsgInProto. */
                    class CopyEventMsgInProto implements ICopyEventMsgInProto {

                        /**
                         * Constructs a new CopyEventMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.ICopyEventMsgInProto);

                        /** CopyEventMsgInProto type. */
                        public type: org.webswing.server.model.proto.CopyEventMsgInProto.CopyEventMsgTypeProto;

                        /** CopyEventMsgInProto file. */
                        public file: string;

                        /**
                         * Creates a new CopyEventMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns CopyEventMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.ICopyEventMsgInProto): org.webswing.server.model.proto.CopyEventMsgInProto;

                        /**
                         * Encodes the specified CopyEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.CopyEventMsgInProto.verify|verify} messages.
                         * @param message CopyEventMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.ICopyEventMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a CopyEventMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns CopyEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.CopyEventMsgInProto;

                        /**
                         * Creates a CopyEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns CopyEventMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.CopyEventMsgInProto;

                        /**
                         * Creates a plain object from a CopyEventMsgInProto message. Also converts values to other types if specified.
                         * @param message CopyEventMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.CopyEventMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this CopyEventMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    namespace CopyEventMsgInProto {

                        /** CopyEventMsgTypeProto enum. */
                        enum CopyEventMsgTypeProto {
                            copy = 0,
                            cut = 1,
                            getFileFromClipboard = 2
                        }
                    }

                    /** Properties of a PasteEventMsgInProto. */
                    interface IPasteEventMsgInProto {

                        /** PasteEventMsgInProto text */
                        text?: (string|null);

                        /** PasteEventMsgInProto html */
                        html?: (string|null);

                        /** PasteEventMsgInProto img */
                        img?: (string|null);

                        /** PasteEventMsgInProto special */
                        special?: (boolean|null);
                    }

                    /** Represents a PasteEventMsgInProto. */
                    class PasteEventMsgInProto implements IPasteEventMsgInProto {

                        /**
                         * Constructs a new PasteEventMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IPasteEventMsgInProto);

                        /** PasteEventMsgInProto text. */
                        public text: string;

                        /** PasteEventMsgInProto html. */
                        public html: string;

                        /** PasteEventMsgInProto img. */
                        public img: string;

                        /** PasteEventMsgInProto special. */
                        public special: boolean;

                        /**
                         * Creates a new PasteEventMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns PasteEventMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IPasteEventMsgInProto): org.webswing.server.model.proto.PasteEventMsgInProto;

                        /**
                         * Encodes the specified PasteEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.PasteEventMsgInProto.verify|verify} messages.
                         * @param message PasteEventMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IPasteEventMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a PasteEventMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns PasteEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.PasteEventMsgInProto;

                        /**
                         * Creates a PasteEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns PasteEventMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.PasteEventMsgInProto;

                        /**
                         * Creates a plain object from a PasteEventMsgInProto message. Also converts values to other types if specified.
                         * @param message PasteEventMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.PasteEventMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this PasteEventMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a SimpleEventMsgInProto. */
                    interface ISimpleEventMsgInProto {

                        /** SimpleEventMsgInProto type */
                        type?: (org.webswing.server.model.proto.SimpleEventMsgInProto.SimpleEventTypeProto|null);
                    }

                    /** Represents a SimpleEventMsgInProto. */
                    class SimpleEventMsgInProto implements ISimpleEventMsgInProto {

                        /**
                         * Constructs a new SimpleEventMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.ISimpleEventMsgInProto);

                        /** SimpleEventMsgInProto type. */
                        public type: org.webswing.server.model.proto.SimpleEventMsgInProto.SimpleEventTypeProto;

                        /**
                         * Creates a new SimpleEventMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns SimpleEventMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.ISimpleEventMsgInProto): org.webswing.server.model.proto.SimpleEventMsgInProto;

                        /**
                         * Encodes the specified SimpleEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.SimpleEventMsgInProto.verify|verify} messages.
                         * @param message SimpleEventMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.ISimpleEventMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a SimpleEventMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns SimpleEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.SimpleEventMsgInProto;

                        /**
                         * Creates a SimpleEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns SimpleEventMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.SimpleEventMsgInProto;

                        /**
                         * Creates a plain object from a SimpleEventMsgInProto message. Also converts values to other types if specified.
                         * @param message SimpleEventMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.SimpleEventMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this SimpleEventMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    namespace SimpleEventMsgInProto {

                        /** SimpleEventTypeProto enum. */
                        enum SimpleEventTypeProto {
                            unload = 0,
                            killSwing = 1,
                            paintAck = 2,
                            repaint = 3,
                            downloadFile = 4,
                            deleteFile = 5,
                            hb = 6,
                            cancelFileSelection = 7,
                            requestComponentTree = 8,
                            requestWindowSwitchList = 9
                        }
                    }

                    /** Properties of a FilesSelectedEventMsgInProto. */
                    interface IFilesSelectedEventMsgInProto {

                        /** FilesSelectedEventMsgInProto files */
                        files?: (string[]|null);
                    }

                    /** Represents a FilesSelectedEventMsgInProto. */
                    class FilesSelectedEventMsgInProto implements IFilesSelectedEventMsgInProto {

                        /**
                         * Constructs a new FilesSelectedEventMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IFilesSelectedEventMsgInProto);

                        /** FilesSelectedEventMsgInProto files. */
                        public files: string[];

                        /**
                         * Creates a new FilesSelectedEventMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns FilesSelectedEventMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IFilesSelectedEventMsgInProto): org.webswing.server.model.proto.FilesSelectedEventMsgInProto;

                        /**
                         * Encodes the specified FilesSelectedEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.FilesSelectedEventMsgInProto.verify|verify} messages.
                         * @param message FilesSelectedEventMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IFilesSelectedEventMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a FilesSelectedEventMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns FilesSelectedEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.FilesSelectedEventMsgInProto;

                        /**
                         * Creates a FilesSelectedEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns FilesSelectedEventMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.FilesSelectedEventMsgInProto;

                        /**
                         * Creates a plain object from a FilesSelectedEventMsgInProto message. Also converts values to other types if specified.
                         * @param message FilesSelectedEventMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.FilesSelectedEventMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this FilesSelectedEventMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of an UploadEventMsgInProto. */
                    interface IUploadEventMsgInProto {

                        /** UploadEventMsgInProto fileName */
                        fileName?: (string|null);

                        /** UploadEventMsgInProto tempFileLocation */
                        tempFileLocation?: (string|null);
                    }

                    /** Represents an UploadEventMsgInProto. */
                    class UploadEventMsgInProto implements IUploadEventMsgInProto {

                        /**
                         * Constructs a new UploadEventMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IUploadEventMsgInProto);

                        /** UploadEventMsgInProto fileName. */
                        public fileName: string;

                        /** UploadEventMsgInProto tempFileLocation. */
                        public tempFileLocation: string;

                        /**
                         * Creates a new UploadEventMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns UploadEventMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IUploadEventMsgInProto): org.webswing.server.model.proto.UploadEventMsgInProto;

                        /**
                         * Encodes the specified UploadEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.UploadEventMsgInProto.verify|verify} messages.
                         * @param message UploadEventMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IUploadEventMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes an UploadEventMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns UploadEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.UploadEventMsgInProto;

                        /**
                         * Creates an UploadEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns UploadEventMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.UploadEventMsgInProto;

                        /**
                         * Creates a plain object from an UploadEventMsgInProto message. Also converts values to other types if specified.
                         * @param message UploadEventMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.UploadEventMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this UploadEventMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a JavaEvalRequestMsgInProto. */
                    interface IJavaEvalRequestMsgInProto {

                        /** JavaEvalRequestMsgInProto correlationId */
                        correlationId?: (string|null);

                        /** JavaEvalRequestMsgInProto objectId */
                        objectId?: (string|null);

                        /** JavaEvalRequestMsgInProto method */
                        method?: (string|null);

                        /** JavaEvalRequestMsgInProto params */
                        params?: (org.webswing.server.model.proto.IJsParamMsgProto[]|null);
                    }

                    /** Represents a JavaEvalRequestMsgInProto. */
                    class JavaEvalRequestMsgInProto implements IJavaEvalRequestMsgInProto {

                        /**
                         * Constructs a new JavaEvalRequestMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IJavaEvalRequestMsgInProto);

                        /** JavaEvalRequestMsgInProto correlationId. */
                        public correlationId: string;

                        /** JavaEvalRequestMsgInProto objectId. */
                        public objectId: string;

                        /** JavaEvalRequestMsgInProto method. */
                        public method: string;

                        /** JavaEvalRequestMsgInProto params. */
                        public params: org.webswing.server.model.proto.IJsParamMsgProto[];

                        /**
                         * Creates a new JavaEvalRequestMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns JavaEvalRequestMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IJavaEvalRequestMsgInProto): org.webswing.server.model.proto.JavaEvalRequestMsgInProto;

                        /**
                         * Encodes the specified JavaEvalRequestMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.JavaEvalRequestMsgInProto.verify|verify} messages.
                         * @param message JavaEvalRequestMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IJavaEvalRequestMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a JavaEvalRequestMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns JavaEvalRequestMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.JavaEvalRequestMsgInProto;

                        /**
                         * Creates a JavaEvalRequestMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns JavaEvalRequestMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.JavaEvalRequestMsgInProto;

                        /**
                         * Creates a plain object from a JavaEvalRequestMsgInProto message. Also converts values to other types if specified.
                         * @param message JavaEvalRequestMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.JavaEvalRequestMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this JavaEvalRequestMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a PlaybackCommandMsgInProto. */
                    interface IPlaybackCommandMsgInProto {

                        /** PlaybackCommandMsgInProto command */
                        command?: (org.webswing.server.model.proto.PlaybackCommandMsgInProto.PlaybackCommandProto|null);
                    }

                    /** Represents a PlaybackCommandMsgInProto. */
                    class PlaybackCommandMsgInProto implements IPlaybackCommandMsgInProto {

                        /**
                         * Constructs a new PlaybackCommandMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IPlaybackCommandMsgInProto);

                        /** PlaybackCommandMsgInProto command. */
                        public command: org.webswing.server.model.proto.PlaybackCommandMsgInProto.PlaybackCommandProto;

                        /**
                         * Creates a new PlaybackCommandMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns PlaybackCommandMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IPlaybackCommandMsgInProto): org.webswing.server.model.proto.PlaybackCommandMsgInProto;

                        /**
                         * Encodes the specified PlaybackCommandMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.PlaybackCommandMsgInProto.verify|verify} messages.
                         * @param message PlaybackCommandMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IPlaybackCommandMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a PlaybackCommandMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns PlaybackCommandMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.PlaybackCommandMsgInProto;

                        /**
                         * Creates a PlaybackCommandMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns PlaybackCommandMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.PlaybackCommandMsgInProto;

                        /**
                         * Creates a plain object from a PlaybackCommandMsgInProto message. Also converts values to other types if specified.
                         * @param message PlaybackCommandMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.PlaybackCommandMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this PlaybackCommandMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    namespace PlaybackCommandMsgInProto {

                        /** PlaybackCommandProto enum. */
                        enum PlaybackCommandProto {
                            reset = 0,
                            play = 1,
                            stop = 2,
                            step = 3,
                            step10 = 4,
                            step100 = 5
                        }
                    }

                    /** Properties of a PixelsAreaResponseMsgInProto. */
                    interface IPixelsAreaResponseMsgInProto {

                        /** PixelsAreaResponseMsgInProto correlationId */
                        correlationId?: (string|null);

                        /** PixelsAreaResponseMsgInProto pixels */
                        pixels?: (string|null);
                    }

                    /** Represents a PixelsAreaResponseMsgInProto. */
                    class PixelsAreaResponseMsgInProto implements IPixelsAreaResponseMsgInProto {

                        /**
                         * Constructs a new PixelsAreaResponseMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IPixelsAreaResponseMsgInProto);

                        /** PixelsAreaResponseMsgInProto correlationId. */
                        public correlationId: string;

                        /** PixelsAreaResponseMsgInProto pixels. */
                        public pixels: string;

                        /**
                         * Creates a new PixelsAreaResponseMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns PixelsAreaResponseMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IPixelsAreaResponseMsgInProto): org.webswing.server.model.proto.PixelsAreaResponseMsgInProto;

                        /**
                         * Encodes the specified PixelsAreaResponseMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.PixelsAreaResponseMsgInProto.verify|verify} messages.
                         * @param message PixelsAreaResponseMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IPixelsAreaResponseMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a PixelsAreaResponseMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns PixelsAreaResponseMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.PixelsAreaResponseMsgInProto;

                        /**
                         * Creates a PixelsAreaResponseMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns PixelsAreaResponseMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.PixelsAreaResponseMsgInProto;

                        /**
                         * Creates a plain object from a PixelsAreaResponseMsgInProto message. Also converts values to other types if specified.
                         * @param message PixelsAreaResponseMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.PixelsAreaResponseMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this PixelsAreaResponseMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a ComponentTreeMsgProto. */
                    interface IComponentTreeMsgProto {

                        /** ComponentTreeMsgProto componentType */
                        componentType?: (string|null);

                        /** ComponentTreeMsgProto name */
                        name?: (string|null);

                        /** ComponentTreeMsgProto value */
                        value?: (string|null);

                        /** ComponentTreeMsgProto screenX */
                        screenX?: (number|null);

                        /** ComponentTreeMsgProto screenY */
                        screenY?: (number|null);

                        /** ComponentTreeMsgProto width */
                        width?: (number|null);

                        /** ComponentTreeMsgProto height */
                        height?: (number|null);

                        /** ComponentTreeMsgProto enabled */
                        enabled?: (boolean|null);

                        /** ComponentTreeMsgProto visible */
                        visible?: (boolean|null);

                        /** ComponentTreeMsgProto components */
                        components?: (org.webswing.server.model.proto.IComponentTreeMsgProto[]|null);

                        /** ComponentTreeMsgProto hidden */
                        hidden?: (boolean|null);

                        /** ComponentTreeMsgProto selected */
                        selected?: (boolean|null);
                    }

                    /** Represents a ComponentTreeMsgProto. */
                    class ComponentTreeMsgProto implements IComponentTreeMsgProto {

                        /**
                         * Constructs a new ComponentTreeMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IComponentTreeMsgProto);

                        /** ComponentTreeMsgProto componentType. */
                        public componentType: string;

                        /** ComponentTreeMsgProto name. */
                        public name: string;

                        /** ComponentTreeMsgProto value. */
                        public value: string;

                        /** ComponentTreeMsgProto screenX. */
                        public screenX: number;

                        /** ComponentTreeMsgProto screenY. */
                        public screenY: number;

                        /** ComponentTreeMsgProto width. */
                        public width: number;

                        /** ComponentTreeMsgProto height. */
                        public height: number;

                        /** ComponentTreeMsgProto enabled. */
                        public enabled: boolean;

                        /** ComponentTreeMsgProto visible. */
                        public visible: boolean;

                        /** ComponentTreeMsgProto components. */
                        public components: org.webswing.server.model.proto.IComponentTreeMsgProto[];

                        /** ComponentTreeMsgProto hidden. */
                        public hidden: boolean;

                        /** ComponentTreeMsgProto selected. */
                        public selected: boolean;

                        /**
                         * Creates a new ComponentTreeMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns ComponentTreeMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IComponentTreeMsgProto): org.webswing.server.model.proto.ComponentTreeMsgProto;

                        /**
                         * Encodes the specified ComponentTreeMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.ComponentTreeMsgProto.verify|verify} messages.
                         * @param message ComponentTreeMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IComponentTreeMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a ComponentTreeMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns ComponentTreeMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.ComponentTreeMsgProto;

                        /**
                         * Creates a ComponentTreeMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns ComponentTreeMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.ComponentTreeMsgProto;

                        /**
                         * Creates a plain object from a ComponentTreeMsgProto message. Also converts values to other types if specified.
                         * @param message ComponentTreeMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.ComponentTreeMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this ComponentTreeMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of a WindowEventMsgInProto. */
                    interface IWindowEventMsgInProto {

                        /** WindowEventMsgInProto id */
                        id?: (string|null);

                        /** WindowEventMsgInProto x */
                        x?: (number|null);

                        /** WindowEventMsgInProto y */
                        y?: (number|null);

                        /** WindowEventMsgInProto width */
                        width?: (number|null);

                        /** WindowEventMsgInProto height */
                        height?: (number|null);

                        /** WindowEventMsgInProto close */
                        close?: (boolean|null);

                        /** WindowEventMsgInProto focus */
                        focus?: (boolean|null);
                    }

                    /** Represents a WindowEventMsgInProto. */
                    class WindowEventMsgInProto implements IWindowEventMsgInProto {

                        /**
                         * Constructs a new WindowEventMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IWindowEventMsgInProto);

                        /** WindowEventMsgInProto id. */
                        public id: string;

                        /** WindowEventMsgInProto x. */
                        public x: number;

                        /** WindowEventMsgInProto y. */
                        public y: number;

                        /** WindowEventMsgInProto width. */
                        public width: number;

                        /** WindowEventMsgInProto height. */
                        public height: number;

                        /** WindowEventMsgInProto close. */
                        public close: boolean;

                        /** WindowEventMsgInProto focus. */
                        public focus: boolean;

                        /**
                         * Creates a new WindowEventMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns WindowEventMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IWindowEventMsgInProto): org.webswing.server.model.proto.WindowEventMsgInProto;

                        /**
                         * Encodes the specified WindowEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowEventMsgInProto.verify|verify} messages.
                         * @param message WindowEventMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IWindowEventMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a WindowEventMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns WindowEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.WindowEventMsgInProto;

                        /**
                         * Creates a WindowEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns WindowEventMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.WindowEventMsgInProto;

                        /**
                         * Creates a plain object from a WindowEventMsgInProto message. Also converts values to other types if specified.
                         * @param message WindowEventMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.WindowEventMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this WindowEventMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of an ActionEventMsgInProto. */
                    interface IActionEventMsgInProto {

                        /** ActionEventMsgInProto actionName */
                        actionName?: (string|null);

                        /** ActionEventMsgInProto data */
                        data?: (string|null);

                        /** ActionEventMsgInProto binaryData */
                        binaryData?: (Uint8Array|null);

                        /** ActionEventMsgInProto windowId */
                        windowId?: (string|null);

                        /** ActionEventMsgInProto eventType */
                        eventType?: (org.webswing.server.model.proto.ActionEventMsgInProto.ActionEventTypeProto|null);
                    }

                    /** Represents an ActionEventMsgInProto. */
                    class ActionEventMsgInProto implements IActionEventMsgInProto {

                        /**
                         * Constructs a new ActionEventMsgInProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IActionEventMsgInProto);

                        /** ActionEventMsgInProto actionName. */
                        public actionName: string;

                        /** ActionEventMsgInProto data. */
                        public data: string;

                        /** ActionEventMsgInProto binaryData. */
                        public binaryData: Uint8Array;

                        /** ActionEventMsgInProto windowId. */
                        public windowId: string;

                        /** ActionEventMsgInProto eventType. */
                        public eventType: org.webswing.server.model.proto.ActionEventMsgInProto.ActionEventTypeProto;

                        /**
                         * Creates a new ActionEventMsgInProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns ActionEventMsgInProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IActionEventMsgInProto): org.webswing.server.model.proto.ActionEventMsgInProto;

                        /**
                         * Encodes the specified ActionEventMsgInProto message. Does not implicitly {@link org.webswing.server.model.proto.ActionEventMsgInProto.verify|verify} messages.
                         * @param message ActionEventMsgInProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IActionEventMsgInProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes an ActionEventMsgInProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns ActionEventMsgInProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.ActionEventMsgInProto;

                        /**
                         * Creates an ActionEventMsgInProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns ActionEventMsgInProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.ActionEventMsgInProto;

                        /**
                         * Creates a plain object from an ActionEventMsgInProto message. Also converts values to other types if specified.
                         * @param message ActionEventMsgInProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.ActionEventMsgInProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this ActionEventMsgInProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    namespace ActionEventMsgInProto {

                        /** ActionEventTypeProto enum. */
                        enum ActionEventTypeProto {
                            init = 0,
                            user = 1
                        }
                    }

                    /** Properties of an ActionEventMsgOutProto. */
                    interface IActionEventMsgOutProto {

                        /** ActionEventMsgOutProto actionName */
                        actionName?: (string|null);

                        /** ActionEventMsgOutProto data */
                        data?: (string|null);

                        /** ActionEventMsgOutProto binaryData */
                        binaryData?: (Uint8Array|null);

                        /** ActionEventMsgOutProto windowId */
                        windowId?: (string|null);
                    }

                    /** Represents an ActionEventMsgOutProto. */
                    class ActionEventMsgOutProto implements IActionEventMsgOutProto {

                        /**
                         * Constructs a new ActionEventMsgOutProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IActionEventMsgOutProto);

                        /** ActionEventMsgOutProto actionName. */
                        public actionName: string;

                        /** ActionEventMsgOutProto data. */
                        public data: string;

                        /** ActionEventMsgOutProto binaryData. */
                        public binaryData: Uint8Array;

                        /** ActionEventMsgOutProto windowId. */
                        public windowId: string;

                        /**
                         * Creates a new ActionEventMsgOutProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns ActionEventMsgOutProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IActionEventMsgOutProto): org.webswing.server.model.proto.ActionEventMsgOutProto;

                        /**
                         * Encodes the specified ActionEventMsgOutProto message. Does not implicitly {@link org.webswing.server.model.proto.ActionEventMsgOutProto.verify|verify} messages.
                         * @param message ActionEventMsgOutProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IActionEventMsgOutProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes an ActionEventMsgOutProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns ActionEventMsgOutProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.ActionEventMsgOutProto;

                        /**
                         * Creates an ActionEventMsgOutProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns ActionEventMsgOutProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.ActionEventMsgOutProto;

                        /**
                         * Creates a plain object from an ActionEventMsgOutProto message. Also converts values to other types if specified.
                         * @param message ActionEventMsgOutProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.ActionEventMsgOutProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this ActionEventMsgOutProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    /** Properties of an AudioEventMsgOutProto. */
                    interface IAudioEventMsgOutProto {

                        /** AudioEventMsgOutProto id */
                        id?: (string|null);

                        /** AudioEventMsgOutProto eventType */
                        eventType?: (org.webswing.server.model.proto.AudioEventMsgOutProto.AudioEventTypeProto|null);

                        /** AudioEventMsgOutProto data */
                        data?: (Uint8Array|null);

                        /** AudioEventMsgOutProto time */
                        time?: (number|null);

                        /** AudioEventMsgOutProto loop */
                        loop?: (number|null);
                    }

                    /** Represents an AudioEventMsgOutProto. */
                    class AudioEventMsgOutProto implements IAudioEventMsgOutProto {

                        /**
                         * Constructs a new AudioEventMsgOutProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IAudioEventMsgOutProto);

                        /** AudioEventMsgOutProto id. */
                        public id: string;

                        /** AudioEventMsgOutProto eventType. */
                        public eventType: org.webswing.server.model.proto.AudioEventMsgOutProto.AudioEventTypeProto;

                        /** AudioEventMsgOutProto data. */
                        public data: Uint8Array;

                        /** AudioEventMsgOutProto time. */
                        public time: number;

                        /** AudioEventMsgOutProto loop. */
                        public loop: number;

                        /**
                         * Creates a new AudioEventMsgOutProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns AudioEventMsgOutProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IAudioEventMsgOutProto): org.webswing.server.model.proto.AudioEventMsgOutProto;

                        /**
                         * Encodes the specified AudioEventMsgOutProto message. Does not implicitly {@link org.webswing.server.model.proto.AudioEventMsgOutProto.verify|verify} messages.
                         * @param message AudioEventMsgOutProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IAudioEventMsgOutProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes an AudioEventMsgOutProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns AudioEventMsgOutProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.AudioEventMsgOutProto;

                        /**
                         * Creates an AudioEventMsgOutProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns AudioEventMsgOutProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.AudioEventMsgOutProto;

                        /**
                         * Creates a plain object from an AudioEventMsgOutProto message. Also converts values to other types if specified.
                         * @param message AudioEventMsgOutProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.AudioEventMsgOutProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this AudioEventMsgOutProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }

                    namespace AudioEventMsgOutProto {

                        /** AudioEventTypeProto enum. */
                        enum AudioEventTypeProto {
                            play = 0,
                            stop = 1,
                            update = 2,
                            dispose = 3
                        }
                    }

                    /** Properties of a WindowDockMsgProto. */
                    interface IWindowDockMsgProto {

                        /** WindowDockMsgProto windowId */
                        windowId?: (string|null);
                    }

                    /** Represents a WindowDockMsgProto. */
                    class WindowDockMsgProto implements IWindowDockMsgProto {

                        /**
                         * Constructs a new WindowDockMsgProto.
                         * @param [properties] Properties to set
                         */
                        constructor(properties?: org.webswing.server.model.proto.IWindowDockMsgProto);

                        /** WindowDockMsgProto windowId. */
                        public windowId: string;

                        /**
                         * Creates a new WindowDockMsgProto instance using the specified properties.
                         * @param [properties] Properties to set
                         * @returns WindowDockMsgProto instance
                         */
                        public static create(properties?: org.webswing.server.model.proto.IWindowDockMsgProto): org.webswing.server.model.proto.WindowDockMsgProto;

                        /**
                         * Encodes the specified WindowDockMsgProto message. Does not implicitly {@link org.webswing.server.model.proto.WindowDockMsgProto.verify|verify} messages.
                         * @param message WindowDockMsgProto message or plain object to encode
                         * @param [writer] Writer to encode to
                         * @returns Writer
                         */
                        public static encode(message: org.webswing.server.model.proto.IWindowDockMsgProto, writer?: $protobuf.Writer): $protobuf.Writer;

                        /**
                         * Decodes a WindowDockMsgProto message from the specified reader or buffer.
                         * @param reader Reader or buffer to decode from
                         * @param [length] Message length if known beforehand
                         * @returns WindowDockMsgProto
                         * @throws {Error} If the payload is not a reader or valid buffer
                         * @throws {$protobuf.util.ProtocolError} If required fields are missing
                         */
                        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): org.webswing.server.model.proto.WindowDockMsgProto;

                        /**
                         * Creates a WindowDockMsgProto message from a plain object. Also converts values to their respective internal types.
                         * @param object Plain object
                         * @returns WindowDockMsgProto
                         */
                        public static fromObject(object: { [k: string]: any }): org.webswing.server.model.proto.WindowDockMsgProto;

                        /**
                         * Creates a plain object from a WindowDockMsgProto message. Also converts values to other types if specified.
                         * @param message WindowDockMsgProto
                         * @param [options] Conversion options
                         * @returns Plain object
                         */
                        public static toObject(message: org.webswing.server.model.proto.WindowDockMsgProto, options?: $protobuf.IConversionOptions): { [k: string]: any };

                        /**
                         * Converts this WindowDockMsgProto to JSON.
                         * @returns JSON object
                         */
                        public toJSON(): { [k: string]: any };
                    }
                }
            }
        }
    }
}
