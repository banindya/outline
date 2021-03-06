package com.sangupta.outline;

import java.io.File;

import javax.inject.Inject;

import com.sangupta.outline.annotations.Argument;
import com.sangupta.outline.annotations.Arguments;
import com.sangupta.outline.annotations.Command;
import com.sangupta.outline.annotations.Option;
import com.sangupta.outline.annotations.OptionType;
import com.sangupta.outline.help.OutlineHelp;

@Command(name = "ping", description = "Ping networks")
public class PingCommandSupport {
	
	@Inject
	public OutlineHelp helpCommand;
	
	@Inject
	public File helpFile;
	
	@Option(name = "--global", description = "This is some description for the global flag", type = OptionType.GLOBAL, arity = 0)
	protected String g;
    
    @Option(name = { "-g1", "--global1" }, description = "This is some description for the global1 flag", type = OptionType.GLOBAL, allowedValues = { "hello", "world" })
    protected String g1;
    
    @Option(name = { "-g2", "--global2" }, description = "This is requried global2 flag", type = OptionType.GLOBAL, required = true)
    protected String g2;
    
    @Option(name = "-c1", description = "this is a command option for this particular command")
    protected String c1;
    
    @Option(name = "-c2", arity = 2, description = "this is a command option OP2 for this particular command")
    protected String[] c2;
    
    @Option(name = "--hidden", description = "this option is hidden and should not be shown in help text", hidden = true)
    protected boolean hidden;
    
    @Argument(order = 0, description = "this should be the first argument and why is it there I have no idea")
    protected String a1;

    @Argument(order = 1, description = "this should be the second argument and why is it there I have no idea")
    protected String a2;
    
    @Arguments(description = "this are all the remaining arguments on the command line and why they are there I have no idea")
    protected String[] a3;
    
	public void run() {
		System.out.println("This is the ping command");
	}

}
