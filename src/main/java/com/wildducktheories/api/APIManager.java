package com.wildducktheories.api;

import java.util.concurrent.Callable;

/**
 * Manages the association of an instance of an API type with the current {@link Thread}.
 * <p>
 * APIs are thread-local singletons that provide some useful service to an application or another API.
 * <p>
 * A typical use of this type is as the delegate in a final class that exposes static methods for
 * each of the methods of this type for example.
 * <p>
 * <pre>
 * public final class SpecificAPI {
 *   private static final APIManager&lt;API&gt; manager = new AbstractAPIManagerImpl&lt;API&gt;() {
 *     public API create() {
 *       return ...; // defaultAPI implementation
 *     }
 *   };
 *
 *  private SpecificAPI() {}
 *  public static API get() { return manager.get(); }
 *  public static API create() { return manager.create(); }
 *  public static void reset() { manager.reset(); }
 *  public static &lt;P&gt; P with(API api, Callable&lt;P&gt; c) { return manager.with(api, c); }
 *  public static void with(API api, Runnable r) { manager.with(api, r); }
 * }
 * </pre>
 * Callers can then obtain an instance of the API with expressions of this kind:
 * <pre>
 * API api = SpecificAPI.get();
 * </pre>
 * @author jonseymour
 * @param <A> An extension of the API type.
 */
public interface APIManager<A extends API>
{
	/**
	 * @return A new instance of the API type A.
	 */
	A create();

	/**
	 * Get the current Thread's current instance of an API.
	 * @return An instance of A.
	 */
	A get();

	/**
	 * Release any thread local resources associated with the managed API.
	 */
	void reset();

	/**
	 * Call a {@link Callable} after setting the Thread's current instance of an API
	 * with the specified implementation for the duration of the execution of the {@link Callable}.
	 * <p>
	 * The current {@link Thread}'s instance of the API will be restored to the original reference
	 * before a call to this method returns.
	 * @param api The API implementation.
	 * @param callable The {@link Callable} to be executed with the API in place.
	 * @return The result of calling the {@link Callable}.
	 */
	<P> P with(A api, Callable<P> callable)
		throws Exception;

	/**
	 * Run a {@link Runnable} after setting the Thread's current instance of an API
	 * with the specified implementation for the duration of the execution of the {@link Runnable}.
	 * <p>
	 * The current {@link Thread}'s instance of the API will be restored to the original reference
	 * before a all to this method returns.
	 * @param api The API implementation.
	 * @param runnable The {@link Runnable} to execute.
	 */
	void with(A api, Runnable runnable);
}
