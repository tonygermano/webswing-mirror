package sk.viktor.ignored.special;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboPopup;

import sk.viktor.ignored.common.WebWindow;
import sk.viktor.ignored.model.s2c.JsonWindowInfo;

public class ComboPopupWrapper extends BasicComboPopup {

    /**
     * 
     */
    private static final long serialVersionUID = 1075679126883493919L;

    public ComboPopupWrapper(JComboBox cb) {
        super(cb);
    }

    /* (non-Javadoc)
     * @see javax.swing.plaf.basic.BasicComboPopup#computePopupBounds(int, int, int, int)
     * 
     * overriden to prevent displaying combobox popup list outside the frame; (only lightweigth popup is renderable in web)
     */
    @Override
    protected Rectangle computePopupBounds(int px, int py, int pw, int ph) {
        Rectangle oBounds = super.computePopupBounds(px, py, pw, ph);
        Component c = SwingUtilities.getRoot(comboBox);
        if(c instanceof WebWindow){
            WebWindow ww=(WebWindow) c;
            int borderThickness=2;
            JsonWindowInfo info = ww.getWindowInfo();
            Point cbPosition=SwingUtilities.convertPoint(comboBox, new Point(), c);
            int comboboxX = cbPosition.x-ww.getFrameTranslation().x;
            int comboboxY = cbPosition.y-ww.getFrameTranslation().y;
            if((comboboxX+oBounds.x+oBounds.width+borderThickness)>info.getWidth()){
                if((oBounds.x+oBounds.width+borderThickness)>info.getWidth()){
                    oBounds.x=0;
                    oBounds.width=info.getWidth()-borderThickness;
                }
                oBounds.x=oBounds.x-((comboboxX+oBounds.x+oBounds.width+borderThickness)-info.getWidth());
            }
            if((comboboxY+oBounds.y+oBounds.height+borderThickness)>info.getHeight()){
                if((oBounds.y+oBounds.height+borderThickness)>info.getHeight()){
                    oBounds.y=0;
                    oBounds.height=info.getHeight()-borderThickness;
                }
                oBounds.y=oBounds.y-((comboboxY+oBounds.y+oBounds.height+borderThickness)-info.getHeight());
            }
        }
        return oBounds;
    }
}
