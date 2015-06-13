package com.wildducktheories.api.impl;

import org.junit.Assert;
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

	/**
	 * Test that resets causes a different object to be allocated. That the same
	 * object will be returned without a reset and that the reset method is called on reset.
	 */
	@Test
	public void testReset() {
		final  TestAPIManager manager = new TestAPIManager();

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
}
