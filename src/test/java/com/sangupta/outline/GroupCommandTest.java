package com.sangupta.outline;

import org.junit.Assert;
import org.junit.Test;

import com.sangupta.outline.Outline;
import com.sangupta.outline.OutlineTestSupport.RemoteAddCommand;
import com.sangupta.outline.help.OutlineHelp;
import com.sangupta.outline.parser.OutlineParseResult;

/**
 * Tests related to where we test only with a group argument.
 * 
 * @author sangupta
 *
 */
public class GroupCommandTest {
	
	@Test
	public void testDefaultCommand() {
		Outline outline = OutlineTestSupport.getOutline();
		outline.withDefaultCommand(RemoteAddCommand.class);
		Assert.assertTrue(true); // we reached here

		// second case
		outline = OutlineTestSupport.getOutline();
		try {
			outline.withDefaultCommand(null);
			Assert.assertTrue(false); 
		} catch(IllegalArgumentException e) {
			Assert.assertTrue(true); 			
		}
		
		// third case
		outline = OutlineTestSupport.getOutline();
		try {
			outline.withDefaultCommand(GroupCommandTest.class);
			Assert.assertTrue(false); 
		} catch(IllegalArgumentException e) {
			Assert.assertTrue(true); 			
		}
	}

	@Test
	public void testGroupNoCommand() {
		Outline outline = OutlineTestSupport.getOutline();
		
		String[] args = "help remote".split(" ");
		Object instance = outline.parse(args);
		
		Assert.assertNotNull(instance);
	    Assert.assertTrue(instance instanceof OutlineHelp);
	    
	    OutlineHelp help = (OutlineHelp) instance;
	    OutlineParseResult result = help.getResult();
	    Assert.assertNotNull(result);
	    
	    Assert.assertEquals(null, result.command);
	    Assert.assertEquals("remote", result.group);
	}
	
	@Test
	public void testGroupInvalidCommand() {
		Outline outline = OutlineTestSupport.getOutline();
		
		String[] args = "help sangupta".split(" ");
		Object instance = outline.parse(args);
		
		Assert.assertNotNull(instance);
	    Assert.assertTrue(instance instanceof OutlineHelp);
	    
	    OutlineHelp help = (OutlineHelp) instance;
	    OutlineParseResult result = help.getResult();
	    Assert.assertNotNull(result);
	    
	    Assert.assertEquals("sangupta", result.command);
	    Assert.assertEquals(null, result.group);
	}
	
	@Test
	public void testGroupCorrectGroupInvalidCommand() {
		Outline outline = OutlineTestSupport.getOutline();
		
		String[] args = "help remote sangupta".split(" ");
		Object instance = outline.parse(args);
		
		Assert.assertNotNull(instance);
	    Assert.assertTrue(instance instanceof OutlineHelp);
	    
	    OutlineHelp help = (OutlineHelp) instance;
	    OutlineParseResult result = help.getResult();
	    Assert.assertNotNull(result);
	    
	    Assert.assertEquals("sangupta", result.command);
	    Assert.assertEquals("remote", result.group);
	}

	@Test
	public void testGroupWithCommand() {
		Outline outline = OutlineTestSupport.getOutline();
		
		String[] args = "help remote radd".split(" ");
		Object instance = outline.parse(args);
		
		Assert.assertNotNull(instance);
	    Assert.assertTrue(instance instanceof OutlineHelp);
	    
	    OutlineHelp help = (OutlineHelp) instance;
	    OutlineParseResult result = help.getResult();
	    Assert.assertNotNull(result);
	    
	    Assert.assertEquals("radd", result.command);
	    Assert.assertEquals("remote", result.group);
	}
	
	public static void main(String[] args) {
		Outline outline = OutlineTestSupport.getOutline();
		
		args = "help remote remote-add".split(" ");
		Object instance = outline.parse(args);
		
		((OutlineHelp) instance).showHelpIfRequested();
	}
}
