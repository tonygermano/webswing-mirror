package org.webswing.server.common.model.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Manifest implements Serializable {

	private static final long serialVersionUID = -9139818429697696599L;

	private String short_name;
	private String name;
	private List<IconDef> icons;
	private String start_url;
	private String background_color;
	private String display = "fullscreen";
	private String scope;
	private String theme_color;
	
	public static class IconDef implements Serializable {

		private static final long serialVersionUID = 4302581475216917869L;
		
		private String src;
		private String type = "image/png";
		private String sizes;
		
		public IconDef(String src, String sizes) {
			super();
			this.src = src;
			this.sizes = sizes;
		}
		
		public IconDef(String src, String type, String sizes) {
			super();
			this.src = src;
			this.type = type;
			this.sizes = sizes;
		}

		public String getSrc() {
			return src;
		}
		
		public void setSrc(String src) {
			this.src = src;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}
		
		public String getSizes() {
			return sizes;
		}
		
		public void setSizes(String sizes) {
			this.sizes = sizes;
		}
		
	}

	public String getShort_name() {
		return short_name;
	}

	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<IconDef> getIcons() {
		return icons;
	}
	
	public void setIcon(IconDef icon) {
		if (icons == null) {
			icons = new ArrayList<>();
		}
		icons.add(icon);
	}

	public void setIcons(List<IconDef> icons) {
		this.icons = icons;
	}

	public String getStart_url() {
		return start_url;
	}

	public void setStart_url(String start_url) {
		this.start_url = start_url;
	}

	public String getBackground_color() {
		return background_color;
	}

	public void setBackground_color(String background_color) {
		this.background_color = background_color;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getTheme_color() {
		return theme_color;
	}

	public void setTheme_color(String theme_color) {
		this.theme_color = theme_color;
	}
	
}
