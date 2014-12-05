package org.webswing.tests;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.webswing.DrawServlet;
import org.webswing.Tests;
import org.webswing.directdraw.model.PathConst;
import org.webswing.directdraw.model.PointsConst;
import org.webswing.directdraw.model.TransformConst;
import org.webswing.directdraw.proto.Directdraw.ImageProto;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;

public class TestProto {

    public static void main(String[] args) throws IOException, URISyntaxException {
        
    	Path path = Paths.get(Tests.class.getClassLoader().getResource("ws.png").toURI());
        byte[] data = Files.readAllBytes(path);
        Message m = ImageProto.newBuilder().setData(ByteString.copyFrom(data)).setHashBytes(ByteString.copyFrom(new byte[] { 1, 2, 3, 4, 5, 6 })).build();
        System.out.println(DrawServlet.encodeBytes(m.toByteArray()));
        
        Color c = new Color(255, 255, 255, 0);
        int rgba = (c.getRGB() << 8) | c.getAlpha();
        System.out.println(String.format("%x", rgba));
        
         TransformConst transform = new TransformConst(new AffineTransform(1, 0, 0, 1, 300, 300));
        PointsConst points =new PointsConst(300,300);
        PathConst pathx = new PathConst(new Rectangle2D.Double(300,300,300,300).getPathIterator(null));
        
        System.out.println("transform: "+((Message)transform.toMessage(null)).toByteArray().length +" points:"+ ((Message)points.toMessage(null)).toByteArray().length+" path: "+((Message)pathx.toMessage(null)).toByteArray().length );
    }
}
