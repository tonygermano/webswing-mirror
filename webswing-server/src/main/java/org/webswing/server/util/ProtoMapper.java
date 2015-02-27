package org.webswing.server.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.webswing.model.MsgOut;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

public class ProtoMapper {
	public static final String basicProtoPackage = "org.webswing.server.model.proto.Webswing";
	private Map<Class<?>, Class<?>> classProtoMap = new HashMap<Class<?>, Class<?>>();

	public byte[] encodeProto(MsgOut msg) throws IOException {
		Message result = buildMessage(msg);
		return result.toByteArray();
	}

	private Message buildMessage(Object msg) throws IOException {
		if (msg == null) {
			return null;
		}
		try {
			Class<?> c = msg.getClass();
			Class<?> protoClass = resolveProtoCounterpartClass(c);
			Builder b = null;
			try {
				b = (Builder) protoClass.getDeclaredMethod("newBuilder").invoke(null);
			} catch (Exception e) {
				throw new IOException("Not able to resolve ProtoBuffer builder for " + protoClass.getName() + ".", e);
			}
			if (b != null) {
				Field[] fields = c.getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true);
					String fieldName = field.getName();
					Object value = field.get(msg);
					FieldDescriptor protoField = b.getDescriptorForType().findFieldByName(fieldName);
					if (Collection.class.isAssignableFrom(field.getType())) {
						Collection<?> list = (Collection<?>) value;
						for (Object val : list) {
							resolveAndSetMessageValue(b, protoField, val, true);
						}
					} else {
						resolveAndSetMessageValue(b, protoField, value, false);
					}
				}
			}
			return b.build();
		} catch (Exception e) {
			throw new IOException("encoding " + msg.getClass() + " to ProtoBuffer format failed!", e);
		}
	}

	private Enum<?> resolveEnum(Object msg) throws IOException {
		if (msg == null) {
			return null;
		}
		try {
			Class<?> c = msg.getClass();
			Class<?> protoClass = resolveProtoCounterpartClass(c);
			if (c.isEnum() && protoClass.isEnum()) {
				List<?> enumCons = Arrays.asList(c.getEnumConstants());
				List<?> protoEnumCons = Arrays.asList(protoClass.getEnumConstants());
				for (Object e : enumCons) {
					Enum<?> enm = (Enum<?>) e;
					for (Object pe : protoEnumCons) {
						Enum<?> penm = (Enum<?>) pe;
						if (penm.name().equals(enm.name())) {
							return penm;
						}
					}
				}
			}
			throw new IOException("Could not resolve proto counterpart for " + c + "." + msg);
		} catch (Exception e) {
			throw new IOException("resolving Enum " + msg.getClass() + " to ProtoBuffer format failed!", e);
		}
	}

	private Class<?> resolveProtoCounterpartClass(Class<?> c) throws ClassNotFoundException {
		if (!classProtoMap.containsKey(c)) {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			String className = c.getName().substring(c.getName().lastIndexOf(".") + 1);
			String protoClassName = basicProtoPackage + "$" + (className.replaceAll("\\$", "Proto\\$")) + "Proto";
			Class<?> protoClass = cl.loadClass(protoClassName);
			classProtoMap.put(c, protoClass);
		}
		Class<?> protoClass = classProtoMap.get(c);
		return protoClass;
	}

	private void resolveAndSetMessageValue(Builder b, FieldDescriptor protoField, Object value, boolean list) throws IOException {
		if (protoField.getJavaType().equals(JavaType.MESSAGE)) {
			setField(b, protoField, buildMessage(value), list);
		} else if (protoField.getJavaType().equals(JavaType.ENUM)) {
			setField(b, protoField, resolveEnum(value), list);
		} else if (protoField.getJavaType().equals(JavaType.BYTE_STRING)) {
			setField(b, protoField, ByteString.readFrom(new ByteArrayInputStream((byte[]) value)), list);
		} else {
			setField(b, protoField, value, list);
		}
	}

	private void setField(Builder b, FieldDescriptor protoField, Object message, boolean list) {
		if (list) {
			b.addRepeatedField(protoField, message);
		} else {
			b.setField(protoField, message);
		}
	}

}
