/**
 *
 * outline - command line argument parser
 * Copyright (c) 2015-2016, Sandeep Gupta
 * 
 * http://sangupta.com/projects/outline
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
 
package com.sangupta.outline;

import com.sangupta.outline.Outline;
import com.sangupta.outline.annotations.Argument;
import com.sangupta.outline.annotations.Arguments;
import com.sangupta.outline.annotations.Command;
import com.sangupta.outline.annotations.Option;
import com.sangupta.outline.annotations.OptionType;
import com.sangupta.outline.cmdfactory.OutlineDefaultCommandFactory;

public class OutlineTestSupport {
	
	public static Outline getOutline() {
        Outline outline = new Outline("git")
                                    .withDescription("the powerful SCM tool")
                                    .withDefaultCommand(AddCommand.class)
                                    .withHelpKeyword("help")
                                    .withCommandFactory(new OutlineDefaultCommandFactory())
                                    .withCommands(AddCommand.class, ResetCommand.class)
                                    .withCommands(RemoteAddCommand.class, RemoteRemoveCommand.class)
                                    .withCommands(AllOptionCommand.class)
                                    .withHelpOnIncorrectArguments(true);
        
        outline.withGroup("mygroup")
               .withDescription("some stupid group")
               .withCommands(GroupAddCommand.class, GroupRemoveCommand.class);
        
        return outline;
	}
    
    public static abstract class GlobalCommand {
    	
    	@Option(name = "--global", description = "This is some description for the global flag", type = OptionType.GLOBAL, arity = 0)
    	protected String g;
        
        @Option(name = { "-g1", "--global1" }, description = "This is some description for the global1 flag", type = OptionType.GLOBAL)
        protected String g1;
        
        @Option(name = { "-g2", "--global2" }, description = "This is some description for the global2 flag", type = OptionType.GLOBAL, arity = 2)
        protected String[] g2;
        
    }
    
    @Command(name = "population-check", description = "all types of options are specified here, including required attributes and allowed values")
    public static class AllOptionCommand {
    	
    	@Option(name = "-bo", description = "primitive boolean")
    	public boolean bool;

    	@Option(name = "-bo2", description = "primitive boolean")
    	public Boolean bool2;

    	@Option(name = "-i", description = "primitive integer")
    	public int integer;
    	
    	@Option(name = "-d", description = "primitive double")
    	public double dbl;
    	
    	@Option(name = "-f", description = "primitive float")
    	public float flt;
    	
    	@Option(name = "-l", description = "primitive long")
    	public long lng;
    	
    	@Option(name = "-short", description = "primitive short")
    	public short shrt;
    	
    	@Option(name = "-b", description = "primitive byte")
    	public byte bite;
    	
    	@Option(name = "-c", description = "primitive char")
    	public char chr;
    	
    	@Option(name = "-s", description = "String")
    	public String strng;
    	
    	@Option(name = "-i2", description = "object integer")
    	public Integer integer2;
    	
    	@Option(name = "-d2", description = "object double")
    	public Double dbl2;
    	
    	@Option(name = "-f2", description = "object float")
    	public Float flt2;
    	
    	@Option(name = "-l2", description = "object long")
    	public Long lng2;
    	
    	@Option(name = "-short2", description = "object short")
    	public Short shrt2;
    	
    	@Option(name = "-b2", description = "object bite")
    	public Byte bite2;
    	
    	@Option(name = "-c2", description = "object char")
    	public Character chr2;
    	
    	@Option(name = "-stringArray", description = "string array", arity = 5)
    	public String[] multiString;
    	
    }

    @Command(name = "add", description = "add command")
    public static class AddCommand extends GlobalCommand {
        
    }
    
    @Command(name = "reset", description = "reset command")
    public static class ResetCommand extends GlobalCommand {
        
    }
    
    public static class RemoteCommand extends GlobalCommand {
        
        @Option(name = "-gr1", type = OptionType.GROUP)
        protected String gr1;
        
        @Option(name = "-gr2", type = OptionType.GROUP, arity = 2)
        protected String[] gr2;
        
    }
    
    @Command(group = "remote", name = "remote-add", description = "remote add global command")
    public static class RemoteAddCommand extends RemoteCommand {
        
        @Option(name = "-c1", description = "command specific option 1")
        protected String c1;
        
        @Option(name = "-c2", arity = 2, description = "command specific option 2")
        protected String[] c2;
        
        @Argument(order = 0, description = "the first argument", required = true, title = "file")
        protected String a1;

        @Argument(order = 1, description = "the second argument")
        protected String a2;
        
        @Arguments(description = "all other arguments")
        protected String[] a3;
        
    }
    
    @Command(group = "remote", name = "remote-remove", description = "remote remove global command")
    public static class RemoteRemoveCommand extends RemoteCommand {
        
    }
    
    @Command(name = "add", description = "remote add group command")
    public static class GroupAddCommand extends GlobalCommand  {
        
    }
    
    @Command(name = "remove", description = "remote remove group command")
    public static class GroupRemoveCommand extends GlobalCommand {
        
    }
}
