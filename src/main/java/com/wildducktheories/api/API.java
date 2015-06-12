package com.wildducktheories.api;

/**
 * Implementations of this interface provide a service to an application or to another API.
 * <p>
 * APIs may use {@link ThreadLocal} objects to store per-Thread state. If so, they must
 * implement the <code>reset()</code> method to release references to this state.
 * <p>
 * Consumers of the API obtain instances of the API from a related final class whose
 * static methods delegate to an instance of implementations of the {@link APIManager} type.
 * <p>
 * @see APIManager
 * @author jonseymour
 */
public interface API {
	/**
	 * API's may allocate Thread local resources. The reset method is called to release
	 * such resources prior to releasing control of a {@link Thread}.
	 */
	public void reset();
}
