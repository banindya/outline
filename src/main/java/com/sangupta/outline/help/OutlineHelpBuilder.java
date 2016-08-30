package com.sangupta.outline.help;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.outline.Outline;
import com.sangupta.outline.OutlineMetadata;
import com.sangupta.outline.annotations.Argument;
import com.sangupta.outline.annotations.Arguments;
import com.sangupta.outline.annotations.Command;
import com.sangupta.outline.annotations.Option;
import com.sangupta.outline.parser.ParseResult;
import com.sangupta.outline.util.OutlineUtil;

public class OutlineHelpBuilder {
	
	/**
	 * The metadata associated with this {@link Outline} generation
	 * 
	 */
    protected final OutlineMetadata meta;
    
    /**
     * The result of parsing the arguments
     */
    protected final ParseResult result;

    public OutlineHelpBuilder(OutlineMetadata meta, ParseResult result) {
    	this.meta = meta;
    	this.result = result;
	}

	/**
     * Construct the help text for whatever has been requested.
     * 
     * @return
     */
    public String getHelpText() {
        List<String> helpLines = getHelpLines();
        return HelpFormatter.format(helpLines);
    }
    
    /**
     * Create the help lines for the given command.
     * 
     * @return
     */
    public List<String> getHelpLines() {
        if(this.meta.singleCommandMode) {
            return showSingleCommandHelp();
        }
        
        if(result.arguments.isEmpty()) {
            return showHelpSummary();
        }
        
        return showCommandHelp();
    }

    /**
     * Show help for a single command mode.
     * 
     * @return
     */
    private List<String> showSingleCommandHelp() {
        List<String> lines = new ArrayList<>();
        
        final String command = this.meta.commandNames.keySet().iterator().next();
        final String group = null;
        
        lines.add(this.meta.name + ": " + this.meta.description);
        lines.add(null);
        lines.add("Usage:");
        lines.add(getUsageLine(command, group));
        lines.add(null);
        lines.addAll(getOptionsSection(command, group));
        lines.add(null);
        lines.addAll(getArgumentsSection(command, group));
        
        return lines;
    }
    
    /**
     * Create the help lines for applicable arguments to the command.
     * 
     * @return
     */
    private List<String> getArgumentsSection(final String command, final String group) {
    	List<String> lines = new ArrayList<>();

    	lines.add("Available arguments:");
    	lines.add(null);
		lines.addAll(buildArgumentsForCommand(command, group));

		return lines;
    }

    /**
     * Create the list of all arguments that are applicable to this particular command.
     * 
     * @param class1
     * @return
     */
    private List<String> buildArgumentsForCommand(final String command, final String group) {
		List<String> lines = new ArrayList<>();
		
		List<Object> arguments = this.meta.commandArguments.getValues(command);
		if(AssertUtils.isEmpty(arguments)) {
			return lines;
		}
		
		int count = 1;
		for(Object obj : arguments) {
			// iterate over them
			if(obj instanceof Argument) {
				Argument arg = (Argument) obj;
				
				lines.add("\t<" + nonEmpty(arg.title(), "arg" + count++) + ">");
				lines.add("\t\t" + arg.description());
				lines.add(null);
			}
			
			if(obj instanceof Arguments) {
				Arguments args = (Arguments) obj;

				lines.add("\t<" + nonEmpty(args.title(), "arguments") + ">");
				lines.add("\t\t" + args.description());
				lines.add(null);
			}
		}
		
		return lines;
	}
    
    /**
     * Return the first non-empty value.
     * 
     * @param value1
     * @param value2
     * @return
     */
    private static String nonEmpty(String value1, String value2) {
    	if(AssertUtils.isNotEmpty(value1)) {
    		return value1;
    	}
    	
    	return value2;
    }

	/**
     * Create the help lines for applicable options.
     * 
     * @return
     */
    private List<String> getOptionsSection(final String command, final String group) {
        List<String> lines = new ArrayList<>();
        
        if(this.meta.globalOptions.isEmpty() && this.meta.commandOptions.isEmpty()) {
        	return lines;
        }
        
        lines.add("Available options:");
        lines.add(null);
        
        // add all global options
        lines.addAll(buildForOptions(this.meta.globalOptions));
        
        // build for group options - only if needed
        if(!this.meta.commandGroups.isEmpty()) {
            
        }
        
        // add all command options
        if(this.result.command != null) {
        	if(this.meta.singleCommandMode) {
        		lines.addAll(buildForOptions(this.meta.commandOptions.values().iterator().next()));
        	} else {
        		lines.addAll(buildForOptions(this.meta.commandOptions.get(this.result.command)));
        	}
        }
        
        return lines;
    }

    /**
     * Build the options list.
     * 
     * @param options
     * @return
     */
    private List<String> buildForOptions(Map<String, Option> optionMap) {
        List<String> lines = new ArrayList<>();
        
        if(AssertUtils.isEmpty(optionMap)) {
        	return lines;
        }

        Collection<Option> optionsCollection = optionMap.values();
        
        Set<Option> options = new HashSet<>(optionsCollection);
        for(Option option : options) {
            String[] names = option.name();
            lines.add("\t" + OutlineUtil.join(names, ", "));
            lines.add("\t\t" + option.description());
            lines.add(null);
        }
        
        return lines;
    }

    /**
     * Construct the usage line.
     * 
     * @return
     */
    private String getUsageLine(final String command, final String group) {
    	StringBuilder builder = new StringBuilder(1024);
    	
    	builder.append('\t');
    	builder.append(this.meta.name);
    	builder.append(' ');
    	
    	// all global options must be shown here
    	if(AssertUtils.isNotEmpty(this.meta.globalOptions)) {
    		boolean first = true;
    		
    		// show one for each of the param
    		Set<Option> options = new HashSet<>(this.meta.globalOptions.values());
    		for(Option option : options) {
    			String[] names = option.name();
    			builder.append(" [");
    			
    			first = true;
    			if(names.length > 1) {
    				builder.append('(');
    			}
    			for(String name : names) {
    				if(!first) {
    					builder.append(" | ");
    				}
    				
    				first = false;
    				builder.append(name);
    			}
    			if(names.length > 1) {
    				builder.append(')');
    			}

    			for(int index = 0; index < option.arity(); index++) {
    				if(option.arity() == 1) {
    					builder.append(" <arg>");
    				} else {
    					builder.append(" <arg" + (index + 1) + ">");
    				}
    			}
    			
    			builder.append(']');
    		}
    	}
    	
    	// then we show the command
    	// this is only applicable for multi-command mode
    	if(!this.meta.singleCommandMode) {
    		builder.append(" <command>");
    	}
    	
    	// and lastly we need to display the arguments that can be passed
    	// this needs to be displayed, only if there are arguments that can be applied
    	// to a command
    	if(this.meta.singleCommandMode) {
    		builder.append(" [<args>]");
    	} else {
    		if(AssertUtils.isEmpty(this.result.group)) {
    			// group is not applicable - this is for all commands within the eco-system
    			if(!this.meta.commandArguments.isEmpty()) {
    				builder.append(" [<args>]");
    			}
    		} else {
    			// group is applicable
    			List<Command> commands = this.meta.commandGroups.getValues(this.result.group);
    			
    		}
    	}
    	
    	return builder.toString();
    }

    /**
     * Show help for a specific command or group.
     * 
     * @return
     */
    private List<String> showCommandHelp() {
        return null;
    }

    /**
     * Show help summary for multi-command mode.
     * 
     * @return
     */
    private List<String> showHelpSummary() {
    	List<String> lines = new ArrayList<>();
        
    	final String command = this.result.command;
    	final String group = this.result.group;
    	
        lines.add(this.meta.name + ": " + this.meta.description);
        lines.add(null);
        lines.add("Usage:");
        lines.add(getUsageLine(command, group));
        lines.add(null);
        lines.addAll(getOptionsSection(command, group));
        lines.add(null);
        lines.addAll(getCommands());
        
        return lines;
    }

    /**
     * Return information on all available commands - this could be list of all
     * commands at global level, or at group level
     * 
     * @return
     */
	private List<String> getCommands() {
		List<String> lines = new ArrayList<>();
		
        lines.add("Available commands:");
        lines.add(null);
        
        if(AssertUtils.isNotEmpty(this.meta.commandNames)) {
        	Set<Command> commands = new HashSet<>(this.meta.commandNames.values());
        	for(Command command : commands) {
        		if(command == null) {
        			// this happens because of the help keyword
        			continue;
        		}
        		
                lines.add("\t" + command.name());
                lines.add("\t\t" + command.description());
                lines.add(null);
        	}
        }
		
		return lines;
	}
	
}
