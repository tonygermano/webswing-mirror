package org.webswing.server.model;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.Before;
import org.junit.Test;
import org.webswing.server.util.ProtoMapper;

import sun.net.www.protocol.file.FileURLConnection;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Message.Builder;

public class ProtoBufferCompetenessTest {

	private static final String inMsgs = "org.webswing.model.c2s";
	private static final String outMsgs = "org.webswing.model.s2c";

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCompletenessOfProtoBufferDefinition() throws ClassNotFoundException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		ArrayList<Class<?>> inMsgClasses = getClassesForPackage(inMsgs);
		ArrayList<Class<?>> outMsgClasses = getClassesForPackage(outMsgs);
		ArrayList<Class<?>> allClasses = new ArrayList<Class<?>>();
		allClasses.addAll(inMsgClasses);
		allClasses.addAll(outMsgClasses);
		Map<Class<?>, Class<?>> classProtoMap = new HashMap<Class<?>, Class<?>>();
		List<String> notFoundClasses = new ArrayList<String>();
		for (Class<?> c : allClasses) {
			try {
				String className = c.getName().substring(c.getName().lastIndexOf(".") + 1);
				String protoClassName = ProtoMapper.basicProtoPackage + "$" + (className.replaceAll("\\$", "Proto\\$")) + "Proto";
				Class<?> protoClass = cl.loadClass(protoClassName);
				classProtoMap.put(c, protoClass);
			} catch (Exception e) {
				e.printStackTrace();
				notFoundClasses.add(c.getName());
			}
		}
		assertTrue("Protocol Buffer counterpart class could not be resolved for these model classes: " + notFoundClasses, notFoundClasses.size() == 0);
		for (Class<?> c : classProtoMap.keySet()) {
			Class<?> protoClass = classProtoMap.get(c);
			if (c.isEnum()) {
				assertTrue("ProtoBuf class " + protoClass + " is not enum", protoClass.isEnum());
				List<?> enumCons = Arrays.asList(c.getEnumConstants());
				List<?> protoEnumCons = Arrays.asList(protoClass.getEnumConstants());
				for (Object e : enumCons) {
					Enum<?> enm = (Enum<?>) e;
					boolean found = false;
					for (Object pe : protoEnumCons) {
						Enum<?> penm = (Enum<?>) pe;
						if (penm.name().equals(enm.name())) {
							found = true;
						}
					}
					assertTrue("Enum " + enm.name() + " not found in " + protoClass, found);
				}
			} else {
				Field[] fields = c.getDeclaredFields();
				Builder b = null;
				try {
					b = (Builder) protoClass.getDeclaredMethod("newBuilder").invoke(null);
				} catch (Exception e) {
					assertTrue("NOT a protobuf model class" + classProtoMap.get(c), false);
				}
				for (Field field : fields) {
					if (Modifier.isStatic(field.getModifiers())) {
						continue;
					}
					field.setAccessible(true);
					String fieldName = field.getName();
					FieldDescriptor protoField = b.getDescriptorForType().findFieldByName(fieldName);
					assertTrue("Field " + fieldName + " not found in " + protoClass, protoField != null);
					if (Collection.class.isAssignableFrom(field.getType())) {
						assertTrue("List field '" + fieldName + "' is not repeated in " + classProtoMap.get(c), protoField.isRepeated());
					} else {
						assertTrue("List field '" + fieldName + "' is repeated in " + classProtoMap.get(c), !protoField.isRepeated());
						if (protoField.getJavaType().equals(JavaType.MESSAGE)) {
							assertTrue("Type of field '" + fieldName + "' in " + classProtoMap.get(c) + " is not correct.", classProtoMap.get(field.getType()).getSimpleName().equals(protoField.toProto().getTypeName().substring(protoField.toProto().getTypeName().lastIndexOf(".") + 1)));
						} else if (protoField.getJavaType().equals(JavaType.ENUM)) {
							assertTrue("Type of field '" + fieldName + "' in " + classProtoMap.get(c) + " is not correct.", classProtoMap.get(field.getType()).getSimpleName().equals(protoField.toProto().getTypeName().substring(protoField.toProto().getTypeName().lastIndexOf(".") + 1)));
							assertTrue("Type of field '" + fieldName + "' in " + classProtoMap.get(c) + " is Enum.", field.getType().isEnum());
						} else if (protoField.getJavaType().equals(JavaType.BOOLEAN)) {
							assertTrue("Type of field '" + fieldName + "' in " + classProtoMap.get(c) + " is boolean", Boolean.class == field.getType() || Boolean.TYPE == field.getType());
						} else if (protoField.getJavaType().equals(JavaType.BYTE_STRING)) {
							assertTrue("Type of field '" + fieldName + "' in " + classProtoMap.get(c) + " is bytess", byte[].class.isAssignableFrom(field.getType()));
						} else if (protoField.getJavaType().equals(JavaType.INT)) {
							assertTrue("Type of field '" + fieldName + "' in " + classProtoMap.get(c) + " is integer", Integer.class == field.getType() || Integer.TYPE == field.getType());
						}
					}
				}
			}
		}
	}

	/**
	 * Private helper method
	 * 
	 * @param directory
	 *            The directory to start with
	 * @param pckgname
	 *            The package name to search for. Will be needed for getting the Class object.
	 * @param classes
	 *            if a file isn't loaded but still is in the directory
	 * @throws ClassNotFoundException
	 */
	private static void checkDirectory(File directory, String pckgname, ArrayList<Class<?>> classes) throws ClassNotFoundException {
		File tmpDirectory;

		if (directory.exists() && directory.isDirectory()) {
			final String[] files = directory.list();

			for (final String file : files) {
				if (file.endsWith(".class")) {
					try {
						classes.add(Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
					} catch (final NoClassDefFoundError e) {
						// do nothing. this class hasn't been found by the
						// loader, and we don't care.
					}
				} else if ((tmpDirectory = new File(directory, file)).isDirectory()) {
					checkDirectory(tmpDirectory, pckgname + "." + file, classes);
				}
			}
		}
	}

	/**
	 * Private helper method.
	 * 
	 * @param connection
	 *            the connection to the jar
	 * @param pckgname
	 *            the package name to search for
	 * @param classes
	 *            the current ArrayList of all classes. This method will simply add new classes.
	 * @throws ClassNotFoundException
	 *             if a file isn't loaded but still is in the jar file
	 * @throws IOException
	 *             if it can't correctly read from the jar file.
	 */
	private static void checkJarFile(JarURLConnection connection, String pckgname, ArrayList<Class<?>> classes) throws ClassNotFoundException, IOException {
		final JarFile jarFile = connection.getJarFile();
		final Enumeration<JarEntry> entries = jarFile.entries();
		String name;

		for (JarEntry jarEntry = null; entries.hasMoreElements() && ((jarEntry = entries.nextElement()) != null);) {
			name = jarEntry.getName();

			if (name.contains(".class")) {
				name = name.substring(0, name.length() - 6).replace('/', '.');

				if (name.contains(pckgname)) {
					classes.add(Class.forName(name));
				}
			}
		}
	}

	/**
	 * Attempts to list all the classes in the specified package as determined by the context class loader
	 * 
	 * @param pckgname
	 *            the package name to search
	 * @return a list of classes that exist within that package
	 * @throws ClassNotFoundException
	 *             if something went wrong
	 */
	public static ArrayList<Class<?>> getClassesForPackage(String pckgname) throws ClassNotFoundException {
		final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

		try {
			final ClassLoader cld = Thread.currentThread().getContextClassLoader();

			if (cld == null)
				throw new ClassNotFoundException("Can't get class loader.");

			final Enumeration<URL> resources = cld.getResources(pckgname.replace('.', '/'));
			URLConnection connection;

			for (URL url = null; resources.hasMoreElements() && ((url = resources.nextElement()) != null);) {
				try {
					connection = url.openConnection();

					if (connection instanceof JarURLConnection) {
						checkJarFile((JarURLConnection) connection, pckgname, classes);
					} else if (connection instanceof FileURLConnection) {
						try {
							checkDirectory(new File(URLDecoder.decode(url.getPath(), "UTF-8")), pckgname, classes);
						} catch (final UnsupportedEncodingException ex) {
							throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)", ex);
						}
					} else
						throw new ClassNotFoundException(pckgname + " (" + url.getPath() + ") does not appear to be a valid package");
				} catch (final IOException ioex) {
					throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname, ioex);
				}
			}
		} catch (final NullPointerException ex) {
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)", ex);
		} catch (final IOException ioex) {
			throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname, ioex);
		}

		return classes;
	}
}
