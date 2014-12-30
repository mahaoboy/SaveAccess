package it.com.winagile.statistics.rest;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AddInfoFuncTest {

	@Before
	public void setup() {

	}

	@After
	public void tearDown() {

	}

	@Test
	public void messageIsValid() {

		assertEquals("wrong message", "Hello World", "Hello World");
	}
}
