package org.webswing.model.c2s;

import java.util.List;

public class JsonEventUploaded implements JsonEvent {

	private static final long serialVersionUID = 75198619L;
	public List<String> files;
	public String clientId;

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String s : files) {
			str.append(s);
			str.append(" ");
		}
		return str.toString();
	}
}