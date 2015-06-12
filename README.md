#NAME
api - a Java library for managing thread-local API singletons

#DESCRIPTION
This Java library provides a generic abstraction for managing thread-local API singletons.

APIs provide some service to an application or to the implementations of other APIs.
A question arises about how to locate a reference to the API.

Static singletons provide one way to do this, but it is often difficult to safely
and satisfactorily vary the implementation of such singletons as requirements change particularly
in cases where a varied implementation is only required in certain contexts and not others.

In many cases dependency injection techniques provide a suitable resolution of this question.
However, it isn't always possible or convenient to use a bean factory to perform dependency
injection in every context that requires it. Dependency injection also does not work so well
in cases where the API encapsulates state that is local to the current thread or
execution context.

This library provides a third way - an API manager that allows the implementation of an API
to vary, on a per thread basis, within a limited scope according to the requirements of that
scope. This style of service location is particularly useful for APIs that encapsulate
state that is local to the current thread or execution context and that can't easily be communicated
with method parameters from the context that establishes the state to the context that needs to
make use of it.

The APIManager type represents a manager for API instances that can do the following things:

* <code>get()</code> - provide a reference to the current implementation of an API type for the current thread.
* <code>with()</code> - vary the current implementation of an API type, for the duration of
the execution of a Runnable or Callable block of code
* <code>reset()</code> - to cleanup "naked" calls to the <code>get()</code> method that might occur outside
the scope of a <code>with()</code> call.

Note that there is no <code>set()</code> method - the implementation of the managed API type can be changed in
a controlled manner by the <code>with()</code> method but only for the duration of execution of the
associated Callable or Runnable. By the time the <code>with()</code> method returns, the original
implementation of the API type will be restored as the thread's current implementation.

Concrete APIManager implementations implement the protected <code>newAPI()</code> method which can construct
a usable default instance of the API type. If there is ever a need to vary the default
implementation, then the application programmer can arrange to execute application code that
needs the alternative implementation within a block of code that executes within the scope of
a <code>APIManager.with()</code> call.

Ownership of the concrete APIManager instance is typically encapsulated by the instance of a
final class that exposes static methods that delegate to the APIManager instance thereby allowing
access to the current instance of the API type for the current thread without needing to provide
any other reference.

The following example shows the boiler plate to create a final class that delegates the
implementation of 4 static methods to an instance of a concrete APIManager implementation.
In this example, the API type is a type called Foo and the type FooAPI provides access to
instances of this type, as follows:

	public final class FooAPI {

	  private static final APIManager<FOO> manager = new AbstractAPIManagerImpl<Foo>() {
	    public Foo newAPI() {
	      return ...; // defaultAPI implementation
	    }
	  };

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
	  		throws Exception
	  {
	  	  return manager.with(api, c);
	  }

	  public static void with(Foo api, Runnable r)
	  {
	  	  manager.with(api, r);
	  }
	}

Consumers of the FooAPI obtain an instance of Foo with code that looks like this:

    Foo foo = FooAPI.get()

 Should the implementation of Foo ever need to change, then the application can provide
 an alternative implementation (say, Bar) for the duration of a call to a Runnable. For example:

    FooAPI.with(
    	new Bar(),
    	new Runnable() {
    	    public void run() {
    	    	Foo foo = FooAPI.get(); // foo will be initialized with a reference to a Bar

    	    	// ... other code.
    	    }
        });