#NAME
api - a Java library for managing thread-local API singletons

#DESCRIPTION
This Java library provides a generic abstraction for managing thread-local API singletons.

APIs provide some service to an application or to the implementations of other APIs. A question arises about how to locate a reference to the API. Static singletons provide one way to do this, but it is often difficult to safely and satisfactorily vary the implementation of such singletons as requirements change.

In many cases dependency injection techniques provide a suitable resolution of this question that works. However, it isn't always possible or convenient to use a bean factory to perform dependency injection in every context that requires it.

This library provides a third way - an API manager that allows the implementation of an API to vary, on a per thread basis, within a limited
scope according to the requirements of that scope.

The APIManager type represents a manager for API instances that can do the following things:

* <code>get()</code> - provide a reference to the current implementation of an API type
* <code>with()</code> - vary the current implementation of an API type, for the duration of the execution of a block of a Runnable or Callable block of code
* <code>reset()</code> - to cleanup "naked" calls to the get method that might occur outside the scope of a <code>with()</code> call.

Concrete APIManager implementations implement the newAPI() which can construct
a usable default instance of the API type. If there is ever a need to vary the default implementation, then the application programmer can arrange to execute
application code that needs the alternative implementation within a block
of code that executes within the scope of a <code>with()</code> call.

The following example shows the boiler plate to create a final class that delegates the implementation of 4 static methods to an instance of a concrete APIManager implementation. In this example, the API type is a type called Foo and the type FooAPI provides access to instances of this type, as follows:

	public final class FooAPI {

	  private static final APIManager<API> manager = new AbstractAPIManagerImpl<Foo>() {
	    public Foo newAPI() {
	      return ...; // defaultAPI implementation
	    }
	  }

	  private FooAPI() {}

	  public static Foo get()
	  {
	  	return manager.get();
	  }

	  public static void reset()
	  {
	  	  manager.reset();
	  }

	  public static <P> P with(Foo api, Callable<P> c)
	  {
	  	  return manager.with(api, c);
	  }

	  public static void with(Foo api, Runnable r)
	  {
	  	  manager.with(api, r);
	  }
	}

Consumers of the FooAPI obtain an instance with code that looks like this:

    Foo foo = FooAPI.get()
