package org.webswing;

import java.io.Serializable;

public class JsonMsg implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1190136636718443351L;
	public String originalImg;
	public String protoImg;

	public int originalRenderTime;
	public int protoRenderTime;
	
	public int originalRenderSize;
	public int protoRenderSize;
}
