package org.webswing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.webswing.directdraw.DirectDraw;
import org.webswing.toolkit.directdraw.WebImage;
import org.webswing.toolkit.directdraw.instructions.DrawConstantPool;

import com.google.protobuf.CodedOutputStream;

public class DrawServlet extends HttpServlet {

	private static final long serialVersionUID = 2084660222487051245L;
	private static Image wimage;
	private static Image image = getImage(false);

	private static DirectDraw dd= new DirectDraw();
	
	protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		if(request.getPathInfo()!=null&& request.getPathInfo().contains("tests")){
			String encoded = encode(getTestMethods());
			response.getWriter().print(encoded);
			return;
		}
		
		String testmethod = request.getParameter("test");
		boolean resetCache = request.getParameter("reset") != null;

		if(resetCache){
			dd.resetConstantCache();
		}
		
		Image wimage = getImage(true);
		Image image = getImage(false);
		JsonMsg json= new JsonMsg();
		draw(wimage, image,testmethod,json);

		long start=System.currentTimeMillis();
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		((WebImage) wimage).toMessage(dd).writeTo(baos);
		json.protoImg = encodeBytes(baos.toByteArray());
		json.protoRenderTime+=(System.currentTimeMillis()-start);
		
		start=System.currentTimeMillis();
		json.originalImg = encodeImage((BufferedImage) image);
		json.originalRenderTime+=(System.currentTimeMillis()-start);
		
//		FileUtils.writeByteArrayToFile(new File("target/tmp/"+tstmp+".wi"), baos.toByteArray());
		json.protoRenderSize=baos.size();
//		FileUtils.writeByteArrayToFile(new File("target/tmp/"+tstmp+".png"), getPngImage((BufferedImage) image));
		json.originalRenderSize=getPngImage((BufferedImage) image).length;
		String encoded = encode(json);
		response.getWriter().print(encoded);
	}

	private void draw(Image i, Image wi, String testmethod,JsonMsg json) {
		Graphics g = i.getGraphics();
		Graphics g2 = wi.getGraphics();
		try {
			Method m = Tests.class.getDeclaredMethod(testmethod, Graphics2D.class);
			long start=System.currentTimeMillis();
			m.invoke(null, g);
			json.originalRenderTime+= (System.currentTimeMillis()-start);
			start=System.currentTimeMillis();
			m.invoke(null, g2);
			json.protoRenderTime+=(System.currentTimeMillis()-start);
		} catch (Exception e) {
			e.printStackTrace();
		}
		g.dispose();
		g2.dispose();
	}

	private static Image getImage(boolean web) {
		return web ? new WebImage(500, 100) : new BufferedImage(500, 100, BufferedImage.TYPE_INT_ARGB);
	}

	private static final ObjectMapper mapper = new ObjectMapper();

	public static String encodeImage(BufferedImage window) {
		return Base64.encodeBase64String(getPngImage(window));
	}
	
	public static String encodeBytes(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	public static byte[] getPngImage(BufferedImage imageContent) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
			ImageIO.write(imageContent, "png", ios);
			byte[] result = baos.toByteArray();
			baos.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String[] getTestMethods(){
		List<String> result=new ArrayList<String>();
		for(Method m:Tests.class.getDeclaredMethods()){
			if(m.getName().endsWith("Test"))
			result.add(m.getName());
		}
		Collections.sort(result);
		return result.toArray(new String[result.size()]);
	}

	public static String encode(Serializable m) {
		try {
			if (m instanceof String) {
				return (String) m;
			}
			return mapper.writeValueAsString(m);
		} catch (IOException e) {
			return null;
		}
	}
}
