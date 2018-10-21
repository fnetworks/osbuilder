package org.fnet.osbuilder.util;

public class Uncheck {

	@FunctionalInterface
	public interface ThrowingSupplier<T> {
		T get() throws Exception;
	}

	@FunctionalInterface
	public interface ThrowingRunnable {
		void run() throws Exception;
	}

	public static <T> T uncheck(ThrowingSupplier<T> supplier) {
		try {
			return supplier.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void uncheck(ThrowingRunnable runnable) {
		try {
			runnable.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
