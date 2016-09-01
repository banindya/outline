package com.sangupta.outline.help;

import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.StringUtils;
import com.sangupta.outline.util.OutlineUtil;

/**
 * This class helps us write an indented string that breaks at a given length
 * without the callee being concerned over the line length.
 * 
 * @author sangupta
 *
 */
public class IndentedStringWriter {
	
	private final StringBuilder builder = new StringBuilder(10 * 1024); // initialize with 10kb of character space

	/**
	 * The line length to break at
	 */
	private final int lineLength;
	
	/**
	 * Replace tabs to 4 spaces 
	 */
	private final int tabLength = 4; 
	
	/**
	 * The current indentation level
	 */
	private int indentLevel = 0;
	
	/**
	 * The current pointer in the line - specifies how
	 * many number of characters have already been written to the line.
	 */
	private int currentPointer = 0;
	
	public IndentedStringWriter() {
		this(60); // default line length is 60 chars
	}

	public IndentedStringWriter(int lineLength) {
		this.lineLength = lineLength;
	}
	
	public void setIndentLevel(int indentLevel) {
		this.indentLevel = indentLevel;
	}
	
	public void incrementIndent() {
		this.indentLevel++;
		this.newLine();
	}
	
	public void decrementIndent() {
		this.indentLevel--;
		this.newLine();
	}
	
	/**
	 * Write the given string fragment to current line.
	 * 
	 * @param str
	 */
	public void write(String str) {
		if(AssertUtils.isEmpty(str)) {
			return;
		}
		
		int len = str.length();
		boolean breakNeeded = this.currentPointer + str.length() > this.lineLength;
		if(!breakNeeded) {
			this.builder.append(str);
			this.currentPointer += len;
			return;
		}
		
		// add the prefix - whatever we can achieve in the remaining space
		int breakPoint = this.lineLength - this.currentPointer;
		
		// check if break point is not breaking a word
		for(int index = breakPoint; index > 0; index--) {
			if(Character.isWhitespace(str.charAt(index))) {
				breakPoint = index;
				break;
			}
		}
		
		// add the prefix
		String prefix = str.substring(0, breakPoint);
		this.builder.append(prefix);
		this.newLine();
		
		// call recursive
		this.write(OutlineUtil.ltrim(str.substring(breakPoint)));
	}
	
	/**
	 * Write the given string fragment to current line and start a new line
	 * at the end.
	 * 
	 * @param str
	 */
	public void writeLine(String str) {
		this.write(str);
		this.newLine();
	}
	
	/**
	 * Start a new indented line.
	 * 
	 */
	public void newLine() {
		this.builder.append('\n');
		if(this.indentLevel == 0) {
			return;
		}
		
		int chars = this.indentLevel * this.tabLength;
		this.builder.append(StringUtils.repeat(' ', chars));
		this.currentPointer = chars;
	}
	
	/**
	 * Return the current string representation.
	 * 
	 */
	public String getString() {
		return this.builder.toString();
	}
	
	@Override
	public String toString() {
		return this.getString();
	}
}