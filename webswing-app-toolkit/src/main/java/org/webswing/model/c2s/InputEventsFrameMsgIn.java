package org.webswing.model.c2s;

import java.util.List;

import org.webswing.model.MsgIn;
import org.webswing.model.jslink.JavaEvalRequestMsgIn;
import org.webswing.model.jslink.JsResultMsg;

public class InputEventsFrameMsgIn implements MsgIn {

	private static final long serialVersionUID = -7046154705244466351L;
	private List<InputEventMsgIn> events;
	private PasteEventMsgIn paste;
	private UploadedEventMsgIn uploaded;

	private JsResultMsg jsResponse;// java2js call return value
	private JavaEvalRequestMsgIn javaRequest;// js2java call

	public List<InputEventMsgIn> getEvents() {
		return events;
	}

	public void setEvents(List<InputEventMsgIn> events) {
		this.events = events;
	}

	public PasteEventMsgIn getPaste() {
		return paste;
	}

	public void setPaste(PasteEventMsgIn paste) {
		this.paste = paste;
	}

	public UploadedEventMsgIn getUploaded() {
		return uploaded;
	}

	public void setUploaded(UploadedEventMsgIn uploaded) {
		this.uploaded = uploaded;
	}

	public JsResultMsg getJsResponse() {
		return jsResponse;
	}

	public void setJsResponse(JsResultMsg jsResponse) {
		this.jsResponse = jsResponse;
	}

	public JavaEvalRequestMsgIn getJavaRequest() {
		return javaRequest;
	}

	public void setJavaRequest(JavaEvalRequestMsgIn javaRequest) {
		this.javaRequest = javaRequest;
	}

}
