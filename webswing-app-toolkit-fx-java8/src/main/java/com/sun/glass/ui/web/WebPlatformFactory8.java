package com.sun.glass.ui.web;

import com.sun.glass.ui.Application;
import org.webswing.javafx.toolkit.WebApplication8;

public class WebPlatformFactory8 extends AbstractWebPlatformFactory {
	@Override
	public Application createApplication() {
		return new WebApplication8();
	}
}