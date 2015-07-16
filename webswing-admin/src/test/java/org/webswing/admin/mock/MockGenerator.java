/*
 * Copyright (c) 2011, 1000kit.org, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.webswing.admin.mock;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Generates the random field in the instance.
 * 
 */
public class MockGenerator {

	/**
	 * The types of the field.
	 */
	private enum FieldTypes {

		/**
		 * The integer, Integer type.
		 */
		INTEGER,
		/**
		 * The boolean, Boolean type.
		 */
		BOOLEAN,
		/**
		 * The double, Double type.
		 */
		DOUBLE,
		/**
		 * The float, Float type.
		 */
		FLOAT,
		/**
		 * The String type.
		 */
		STRING,
		/**
		 * The XmlGregorianCalendar type.
		 */
		XMLGREGORIANCALENDAR,
		/**
		 * The Date type.
		 */
		DATE,
		/**
		 * The Calendar type.
		 */
		CALENDAR,
		/**
		 * The Class type.
		 */
		CLASS,
		/**
		 * The Object type.
		 */
		OBJECT,
		/**
		 * The locale type.
		 */
		LOCALE;
	}

	/**
	 * The generic type name.
	 */
	private static final String GENERIC_TYPE_NAME = "T";
	/**
	 * The random generator.
	 */
	private static final Random random = new Random();
	/**
	 * The map of the field types.
	 */
	private static final Map<Class<?>, FieldTypes> types = new HashMap<Class<?>, FieldTypes>();

	/**
	 * Setup the field types.
	 */
	static {
		types.put(int.class, FieldTypes.INTEGER);
		types.put(long.class, FieldTypes.INTEGER);
		types.put(Integer.class, FieldTypes.INTEGER);
		types.put(boolean.class, FieldTypes.BOOLEAN);
		types.put(Boolean.class, FieldTypes.BOOLEAN);
		types.put(Double.class, FieldTypes.DOUBLE);
		types.put(double.class, FieldTypes.DOUBLE);
		types.put(Float.class, FieldTypes.FLOAT);
		types.put(float.class, FieldTypes.FLOAT);
		types.put(String.class, FieldTypes.STRING);
		types.put(XMLGregorianCalendar.class, FieldTypes.XMLGREGORIANCALENDAR);
		types.put(Date.class, FieldTypes.DATE);
		types.put(Calendar.class, FieldTypes.CALENDAR);
		types.put(Locale.class, FieldTypes.LOCALE);
		types.put(Class.class, FieldTypes.CLASS);
		types.put(Object.class, FieldTypes.OBJECT);
	}

	/**
	 * Create the mock data.
	 * 
	 * @param result
	 *            the result class.
	 * @return the instance of the class.
	 */
	public static <T> T createMockData(Class<T> clazz) {
		return createMockData(clazz, null, null, Object.class);
	}

	/**
	 * Creates the list of mock data.
	 * 
	 * @param <T>
	 *            the mock class type.
	 * @param clazz
	 *            the mock class.
	 * @param size
	 *            the size of the list.
	 * @return the instance of the class.
	 */
	public static <T> List<T> createMockDatas(Class<T> clazz, int size) {
		return createMockDatas(clazz, Object.class, size);
	}

	/**
	 * Creates the mock data.
	 * 
	 * @param <T>
	 *            the mock class type.
	 * @param result
	 *            the mock class.
	 * @param parentClazz
	 *            the parent class.
	 * @return the instance of the class.
	 */
	public static <T> T createMockData(Class<T> result, Class<?> parentClazz) {
		return createMockData(result, null, null, parentClazz);
	}

	/**
	 * Creates the list of mock data.
	 * 
	 * @param <T>
	 *            the mock class type.
	 * @param clazz
	 *            the mock class.
	 * @param parentClazz
	 *            the parent class.
	 * @param size
	 *            the size of the list.
	 * @return the instance of the class.
	 */
	public static <T> List<T> createMockDatas(Class<T> clazz, Class<?> parentClazz, int size) {
		List<T> result = new ArrayList<T>();
		for (int i = 0; i < size; i++) {
			result.add(createMockData(clazz, null, null, parentClazz));
		}
		return result;
	}

	/**
	 * Create the mock data.
	 * 
	 * @param result
	 *            the result class.
	 * @param impl
	 *            the map of implementation class for the interfaces.
	 * 
	 * @return the instance of the class.
	 */
	public static <T> T createMockData(Class<T> result, Map<Class<?>, Class<?>> impl) {
		return createMockData(result, null, impl, Object.class);
	}

	/**
	 * Creates the mock data.
	 * 
	 * @param result
	 *            the result instance class.
	 * @param parents
	 *            the parent stack.
	 * @param impl
	 *            the map of implementation class for the interfaces.
	 * 
	 * @return the mock instance of the class.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T createMockData(Class<T> result, Stack parents, Map<Class<?>, Class<?>> impl, Class<?> parentClass) {
		try {
			if (parents == null) {
				parents = new Stack();
			}

			T data = result.getConstructor().newInstance();
			parents.push(data);

			if (data != null) {

				List<Field> fields = findAllFields(data.getClass(), parentClass);

				for (Field field : fields) {
					Class<?> type = field.getType();
					FieldTypes fieldType = types.get(type);
					if (fieldType != null) {
						Object value = null;
						switch (fieldType) {
						case INTEGER:
							value = random.nextInt(1000);
							break;
						case BOOLEAN:
							value = random.nextBoolean();
							break;
						case DOUBLE:
							value = random.nextDouble() * 1000;
							break;
						case FLOAT:
							value = random.nextFloat() * 1000;
						case STRING:
							value = UUID.randomUUID().toString();
							break;
						case XMLGREGORIANCALENDAR:
							value = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(random.nextInt(30) + 1990, random.nextInt(11) + 1, random.nextInt(27) + 1, 0);
							break;
						case DATE:
							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.DAY_OF_YEAR, random.nextInt(365));
							value = cal.getTime();
							break;
						case CALENDAR:
							Calendar calendar = Calendar.getInstance();
							calendar.add(Calendar.DAY_OF_YEAR, random.nextInt(365));
							value = calendar;
							break;
						case CLASS:
							value = Object.class;
							break;
						case LOCALE:
							// FIXME: create random locale instance
							value = Locale.ENGLISH;
							break;
						case OBJECT:
							// check generic field
							if (GENERIC_TYPE_NAME.equals(field.getGenericType().toString())) {
								Class clazz = data.getClass();
								Stack copy = new Stack();
								copy.addAll(parents);
								value = createMockData((Class<T>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0], copy, impl, parentClass);
							} else {
								value = UUID.randomUUID().toString();
							}
							break;
						}
						setProperty(field, data, value);
					} else {
						if (type.isEnum()) {
							Object value = field.getType().getEnumConstants()[random.nextInt(field.getType().getEnumConstants().length)];
							setProperty(field, data, value);
						} else if (List.class.isAssignableFrom(type)) {

							int count = random.nextInt(5) + 1;
							List list = new ArrayList();
							for (int i = 0; i < count; i++) {
								Stack copy = new Stack();
								copy.addAll(parents);
								Object obj1 = createMockData((Class<T>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0], copy, impl, parentClass);
								list.add(obj1);
							}

							setProperty(field, data, list);
						} else if (Set.class.isAssignableFrom(type)) {
							int count = random.nextInt(5) + 1;
							Set set = new HashSet();
							for (int i = 0; i < count; i++) {
								Stack copy = new Stack();
								copy.addAll(parents);
								Object obj1 = createMockData((Class<T>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0], copy, impl, parentClass);
								set.add(obj1);
							}

							setProperty(field, data, set);
						} else if (type.isInterface()) {
							Object value = null;
							if (impl != null && !impl.isEmpty()) {
								Class<?> iClass = impl.get(type);
								if (iClass != null) {
									Stack copy = new Stack();
									copy.addAll(parents);
									value = createMockData(iClass, copy, impl, parentClass);
								}
							}
							setProperty(field, data, value);
						} else if (type.isArray()) {
							int count = random.nextInt(5) + 1;
							setProperty(field, data, Array.newInstance(type.getComponentType(), count));
						} else if (parentsContains(type, parents) != null) {
							setProperty(field, data, parentsContains(type, parents));
						} else {
							Stack copy = new Stack();
							copy.addAll(parents);
							Object obj = createMockData(type, copy, impl, parentClass);
							setProperty(field, data, obj);
						}
					}

				}
			}
			return data;
		} catch (Exception e) {
			// FIXME: rewrite
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Finds all fields of the class.
	 * 
	 * @param objectClazz
	 *            the object class.
	 * 
	 * @return the list of fields.
	 */
	private static List<Field> findAllFields(Class<?> objectClazz, Class<?> parentClass) {
		List<Field> result = new ArrayList<Field>();
		Class<?> clazz = objectClazz;
		int level = 0;
		do {
			Field[] f = clazz.getDeclaredFields();
			for (Field field : f) {
				if (!Modifier.isStatic(field.getModifiers())) {
					result.add(field);
				}
			}
			clazz = clazz.getSuperclass();
			level = level + 1;
		} while (clazz != null && !clazz.equals(parentClass));
		return result;
	}

	/**
	 * Sets the value to the field in the instance.
	 * 
	 * @param field
	 *            the field.
	 * @param object
	 *            the instance.
	 * @param value
	 *            the value.
	 * 
	 * @throws Exception
	 *             if the method fails.
	 */
	private static void setProperty(Field field, Object object, Object value) throws Exception {
		if (field != null) {
			boolean accessible = field.isAccessible();
			try {
				field.setAccessible(true);
				field.set(object, value);
			} finally {
				field.setAccessible(accessible);
			}
		}
	}

	/**
	 * Returns the parent instance.
	 * 
	 * @param type
	 *            the type.
	 * @param parents
	 *            the parents stack.
	 * 
	 * @return the instance.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T parentsContains(Class<T> type, Stack parents) {
		for (Object o : parents) {
			if (o.getClass().equals(type)) {
				return (T) o;
			}
		}
		return null;
	}
}