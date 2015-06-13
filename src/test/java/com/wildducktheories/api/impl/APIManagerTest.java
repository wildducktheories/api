package com.wildducktheories.api.impl;

import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.wildducktheories.api.API;

public class APIManagerTest {

	static class TestAPI implements API {

		public boolean reset = false;

		@Override
		public void reset() {
			reset = true;
		}

	}

	static class TestAPIManager extends AbstractAPIManagerImpl<TestAPI>
	{
		public TestAPI create()
		{
			return new TestAPI();
		};
	}

	private TestAPIManager manager;

	@Before
	public void setup()
	{
		manager = new TestAPIManager();
	}

	@After
	public void after()
	{
		manager.reset();
	}

	/**
	 * Test that resets causes a different object to be allocated. That the same
	 * object will be returned without a reset and that the reset method is called on reset.
	 */
	@Test
	public void testReset() {

		TestAPI api1 = manager.get();
		TestAPI api2 = manager.get();
		Assert.assertFalse(api1.reset);
		manager.reset();
		TestAPI api3 = manager.get();

		Assert.assertNotNull(api1);
		Assert.assertNotNull(api3);
		Assert.assertSame(api1, api2);
		Assert.assertNotSame(api1, api3);
		Assert.assertTrue(api1.reset);
		Assert.assertFalse(api3.reset);
	}

	/**
	 * Test with(TestAPI, Callable)
	 * @throws Exception
	 */
	@Test
	public void testWithCallable() throws Exception {
		final TestAPI[] apis = new TestAPI[] { null, null, manager.create(), manager.get() };
		final Integer result = manager.with(apis[2], new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				apis[0] = manager.get();
				return 3141529;
			}
		});
		Assert.assertEquals("Result is passed through", 3141529, (int)result); // result passed through
		apis[1] = manager.get();
		manager.reset();
		Assert.assertNotNull("API is not null in body", apis[0]);
		Assert.assertNotNull("API is not null on return", apis[1]); // api is not null on return
		Assert.assertSame("API is restored on return", apis[1], apis[3]);
		Assert.assertNotSame("API is different inside and outside", apis[0], apis[1]); // api
		Assert.assertSame("API is same before and after", apis[0], apis[2]);
	}

	/**
	 * Test with(TestAPI, Callable)
	 */
	@Test
	public void testWithCallableThrows() {
		final Exception thrown = new Exception();
		final TestAPI[] apis = new TestAPI[] { null, null, manager.create(), manager.get() };
		try {
			manager.with(apis[2], new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					apis[0] = manager.get();
					throw thrown;
				}
			});
			Assert.fail("Failed to propagate exception");
		} catch (Exception e) {
			Assert.assertSame("Exception propagated", thrown, e);
			apis[1] = manager.get();
			manager.reset();
			Assert.assertNotNull("API is not null in body", apis[0]);
			Assert.assertNotNull("API is not null on return", apis[1]); // api is not null on return
			Assert.assertSame("API is restored on return", apis[1], apis[3]);
			Assert.assertNotSame("API is different inside and outside", apis[0], apis[1]); // api
			Assert.assertSame("API is same before and after", apis[0], apis[2]);
		}
	}



	/**
	 * Test with(TestAPI, Runnable)
	 */
	@Test
	public void testWithRunnable() {
		final TestAPI[] apis = new TestAPI[] { null, null, manager.create(), manager.get() };
		 manager.with(apis[2], new Runnable() {
			@Override
			public void run() {
				apis[0] = manager.get();
			}
		});
		apis[1] = manager.get();
		manager.reset();
		Assert.assertNotNull("API is not null in body", apis[0]);
		Assert.assertNotNull("API is not null on return", apis[1]); // api is not null on return
		Assert.assertSame("API is restored on return", apis[1], apis[3]);
		Assert.assertNotSame("API is different inside and outside", apis[0], apis[1]); // api
		Assert.assertSame("API is same before and after", apis[0], apis[2]);
	}

	/**
	 * Test with(TestAPI, Runnable)
	 */
	@Test
	public void testWithRunnableThrows() {
		final RuntimeException thrown = new RuntimeException();

		final TestAPI[] apis = new TestAPI[] { null, null, manager.create(), manager.get() };
		try {
			 manager.with(apis[2], new Runnable() {
				@Override
				public void run() {
					apis[0] = manager.get();
					throw thrown;
				}
			});
			 Assert.fail("expected RuntimeException to be thrown");
		} catch (RuntimeException e) {
			Assert.assertSame(thrown, e);
			apis[1] = manager.get();
			manager.reset();
			Assert.assertNotNull("API is not null in body", apis[0]);
			Assert.assertNotNull("API is not null on return", apis[1]); // api is not null on return
			Assert.assertSame("API is restored on return", apis[1], apis[3]);
			Assert.assertNotSame("API is different inside and outside", apis[0], apis[1]); // api
			Assert.assertSame("API is same before and after", apis[0], apis[2]);
		}
	}

}
