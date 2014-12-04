package org.webswing.common;

import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;

public interface WindowDecoratorTheme {

    
    public static final String DECORATION_THEME_IMPL_PROP = "org.webswing.window.theme.impl";

    public static final String DECORATION_THEME_IMPL_DEFAULT = "org.webswing.theme.DefaultWindowDecoratorTheme";

    Insets getInsets();

    Image getWindowDecoration(Object window, int w, int h);

    WindowActionType getAction(Window w, Point e);

}
