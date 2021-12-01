package com.storedobject.core;

import com.storedobject.common.MethodInvoker;
import com.storedobject.common.StringList;
import com.storedobject.core.StoredObjectUtility.MethodList;

import java.util.function.Function;

/**
 * Interface to convert a {@link StoredObject} to {@link String}.
 *
 * @param <T> Object type.
 * @author Syam
 */
@FunctionalInterface
public interface ObjectToString<T extends StoredObject> extends Function<T, String> {

	/**
	 * Create an {@link ObjectToString} implementation for a given set of attributes.
	 *
	 * @param objectClass Object class to convert.
	 * @param attributes Attributes of the object to convert.
	 * @param <O> Type of the object.
	 * @return Interface implementation.
	 */
	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, String... attributes) {
		return create(objectClass, StringList.create(attributes));
	}

	/**
	 * Create an {@link ObjectToString} implementation for a given set of attributes.
	 *
	 * @param objectClass Object class to convert.
	 * @param attributes Attributes of the object to convert.
	 * @param <O> Type of the object.
	 * @return Interface implementation.
	 */
	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, StringList attributes) {
		return create(objectClass, ", ", attributes);
	}

	/**
	 * Create an {@link ObjectToString} implementation for a given set of attributes.
	 *
	 * @param objectClass Object class to convert.
	 * @param attributes Attributes of the object to convert.
	 * @param delimiter Delimiter to separate the result of the attribute values.
	 * @param <O> Type of the object.
	 * @return Interface implementation.
	 */
	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, String delimiter,
															 StringList attributes) {
		return new O2S<>(objectClass, delimiter, attributes);
	}

	/**
	 * Create an {@link ObjectToString} implementation for a given set of attributes.
	 *
	 * @param objectClass Object class to convert.
	 * @param attributes Attributes of the object to convert.
	 * @param <O> Type of the object.
	 * @return Interface implementation.
	 */
	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, StringList attributes,
															 boolean showAttributes) {
		return create(objectClass, ", ", attributes, showAttributes);
	}

	/**
	 * Create an {@link ObjectToString} implementation for a given set of attributes.
	 *
	 * @param objectClass Object class to convert.
	 * @param attributes Attributes of the object to convert.
	 * @param delimiter Delimiter to separate the result of the attribute values.
	 * @param showAttributes Whether to show the name of the attribute or not.
	 * @param <O> Type of the object.
	 * @return Interface implementation.
	 */
	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, String delimiter,
															 StringList attributes, boolean showAttributes) {
		return new O2S<>(objectClass, delimiter, attributes, showAttributes);
	}

	/**
	 * Create an {@link ObjectToString} implementation for a given set of attributes.
	 *
	 * @param objectClass Object class to convert.
	 * @param displayMethod Method to use for conversion.
	 * @param <O> Type of the object.
	 * @return Interface implementation.
	 */
	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, MethodList displayMethod) {
		return new O2S<>(objectClass, displayMethod);
	}

	class O2S<O extends StoredObject> implements ObjectToString<O> {

		private Function<O, Object> function;

		public O2S(Class<O> objectClass, MethodList method) {
			if(method != null) {
				Class<?> c = method.getHead().getDeclaringClass();
				if (c.isAssignableFrom(objectClass)) {
					method.stringifyTail();
					function = o -> method.invoke(o, false);
				}
			}
		}

		public O2S(Class<O> objectClass, String delimiter, StringList attributes) {
			this(objectClass, delimiter, attributes, false);
		}

		public O2S(Class<O> objectClass, String delimiter, StringList attributes, boolean showAttributes) {
			if(attributes != null && !attributes.isEmpty()) {
				MethodInvoker[] methods = new MethodInvoker[attributes.size()];
				for(int i = 0; i < attributes.size(); i++) {
					methods[i] = StoredObjectUtility.createMethodList(objectClass, attributes.get(i));
					if(methods[i] != null) {
						((MethodList)methods[i]).stringifyTail();
					} else {
						String attribute = attributes.get(i);
						methods[i] = o -> "Method Error for '" + attribute + "'";
					}
				}
				function = o -> {
					StringBuilder sb = new StringBuilder();
					if(showAttributes) {
						sb.append("Id = ").append(o.getId()).append(delimiter).append("Tran = ").
								append(o.getTransactionId()).append(delimiter);
					}
					MethodInvoker m;
					for(int i = 0; i < methods.length; i++) {
						m = methods[i];
						if(i > 0) {
							sb.append(delimiter);
						}
						if(showAttributes) {
							sb.append(attributes.get(i)).append(" = ");
						}
						sb.append(StringUtility.toString(m.invoke(o, false)));
					}
					return sb.toString();
				};
			}
		}

		@Override
		public String apply(O object) {
			if(object == null) {
				return "";
			}
			if(function != null) {
				Object r = function.apply(object);
				return r == null ? "Error: " + object.toDisplay() : StringUtility.toString(r);
			}
			return object.toDisplay();
		}
	}
}
