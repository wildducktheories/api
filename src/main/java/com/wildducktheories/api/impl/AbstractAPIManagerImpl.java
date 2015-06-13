package com.wildducktheories.api.impl;

import java.util.concurrent.Callable;

import com.wildducktheories.api.API;
import com.wildducktheories.api.APIManager;

/**
 * An abstract implementation of the {@link APIManager} interface.
 * <p>
 * Concrete sub types should implement the newAPI() method.
 * <p>
 * @author jonseymour
 * @param <A> The API type.
 */
public abstract class AbstractAPIManagerImpl<A extends API> implements APIManager<A> {

	/**
	 * An per thread override for the thread's current scheduler.
	 */
	private final ThreadLocal<A> perThread = new ThreadLocal<A>();

	/**
	 * Constructs a default instance of the API type.
	 * @return A new instance of the API type.
	 */
	public abstract A create();

	@Override
	public A get() {
		A api = perThread.get();
		if (api == null) {
			api = create();
			perThread.set(api);
		}
		return api;
	}

	/**
	 * Run the specified {@link Runnable} with the specified {@link API} as the
	 * current {@link API} for the current thread.
	 * @param api The specified {@link API}I
	 * @param run The specified {@link Runnable}
	 */
	public void with(final A api, final Runnable runnable) {
		final A saved = perThread.get();
		try {
			perThread.set(api);
			runnable.run();
		} finally {
			if (saved == null) {
				perThread.remove();
			} else {
				perThread.set(saved);
			}
		}
	}

	/**
	 * Run the specified {@link Callable} with the specified {@link API} as the
	 * current {@link API} for the current thread.
	 * @param api The specified {@link API}I
	 * @param run The specified {@link Runnable}
	 * @return The result of the {@link Callable}
	 * @throws Exception The exception thrown by the specified {@link Callable}, if any.
	 */
	public <P> P with(final A api, final Callable<P> callable)
		throws Exception
	{
		final A saved = perThread.get();
		try {
			perThread.set(api);
			return callable.call();
		} finally {
			if (saved == null) {
				perThread.remove();
			} else {
				perThread.set(saved);
			}
		}
	}

	/**
	 * Release all thread local resources.
	 */
	public void reset() {
		get().reset();
		perThread.remove();
	}
}
