/*
 * Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package ensemble.samples.controls.dnd.body;

import ensemble.samples.controls.dnd.clothes.Cloth;
import ensemble.samples.controls.dnd.images.ImageManager;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

import java.util.Map;

/**
 * Container for body that accepts drops. Draggable details dropped here
 * are equipped.
 * 
 */

public class BodyElement {
    private final Pane bodyPane;
    private final ImageView bodyImage;
    private Pane itemPane;
    private Map<String, Cloth> items;
    
    public void setItemsInfo(Pane p, Map<String, Cloth> m) {
        itemPane = p;
        items = m;
    }

    public Pane getBodyPane() {
        return bodyPane;
    }
    
    public BodyElement() {
        bodyPane = new Pane();
        bodyImage = new ImageView(ImageManager.getResource("body.png"));
        
        bodyPane.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            // If this is a meaningful drop...
            if (db.hasString()) {
                // Get an item ID here, which was stored when the drag started.
                String nodeId = db.getString();
                // ...search for the item in unequipped items. If it is there...
                ImageView cloth = (ImageView) itemPane.lookup("#" + nodeId);
                if (cloth != null) {
                    // ... the item is removed from the unequipped list
                    // and attached to body.
                    itemPane.getChildren().remove(cloth);
                    bodyPane.getChildren().add(cloth);
                    cloth.relocate(0, 0);
                    success = true;
                }
                // ...anyway, the item is now equipped.
                items.get(nodeId).putOn();
            }
            event.setDropCompleted(success);
            event.consume();
        });
        
        bodyPane.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != bodyImage &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        
        bodyPane.getChildren().add(bodyImage);
        bodyPane.setMinWidth(bodyImage.getImage().getWidth());
        bodyPane.setPadding(new Insets(10.0));
    }
    
    public Node getNode() {
        return bodyPane;
    }
    
}
