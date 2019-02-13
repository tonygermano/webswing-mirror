package com.sun.glass.ui.web;

import com.sun.glass.ui.Application;
import org.webswing.javafx.toolkit.WebApplication11;

public class WebPlatformFactory11 extends AbstractWebPlatformFactory {
	@Override
	public Application createApplication() {
		return new WebApplication11();
	}
}