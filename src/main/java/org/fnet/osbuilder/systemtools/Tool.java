package org.fnet.osbuilder.systemtools;

import java.util.ServiceLoader;
import java.util.function.Predicate;

public interface Tool {

	static <T extends Tool> T getTool(Class<T> cls) {
		for (T t : ServiceLoader.load(cls))
			if (t.isInstalled())
				return t;
		throw new RuntimeException("Tool for " + cls.getSimpleName() + " not found");
	}

	static <T extends Tool> T getTool(Class<T> cls, Predicate<T> check) {
		for (T t : ServiceLoader.load(cls)) {
			if (t.isInstalled() && check.test(t))
				return t;
		}
		throw new RuntimeException("Tool for " + cls.getSimpleName() + " not found");
	}

	String getName();

	boolean isInstalled();

}
