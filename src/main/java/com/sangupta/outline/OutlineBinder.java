package com.sangupta.outline;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sangupta.jerry.ds.SimpleMultiMap;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.ReflectionUtils;
import com.sangupta.outline.annotations.Argument;
import com.sangupta.outline.annotations.Arguments;
import com.sangupta.outline.annotations.Option;
import com.sangupta.outline.annotations.OptionType;
import com.sangupta.outline.parser.ParseResult;

/**
 * Bind functions that bind the command {@link Object} instance to the
 * values that are received and parsed for this specific command.
 * 
 * @author sangupta
 *
 */
class OutlineBinder {
    
    final static Map<Class<?>, OutlineTypeConverter<?>> converters = new HashMap<>();
    
    static <T> void registerTypeConverter(Class<T> classOfT, OutlineTypeConverter<T> converter) {
        converters.put(classOfT, converter);
    }

    /**
     * Bind the resultant properties that we have got to the object instance. So that
     * the instance is ready-for-use.
     * 
     * @param instance
     * @param result
     */
    public static void bindInstanceToProperties(Class<?> clazz, Object instance, ParseResult result) {
        Field[] fields = clazz.getDeclaredFields();
        bindAllOptions(fields, instance, result);
        
        int startOrder = bindAllArgumentsWithOrder(fields, instance, result);
        
        bindRemainingArguments(fields, instance, result, startOrder);
    }

    /**
     * Bind all the remaining arguments that havae not yet been set using <code>@Argument</code> annotation
     * to any field that requires <code>@Arguments</code> annotation.
     *  
     * @param fields
     * @param instance
     * @param result
     * @param startOrder
     */
    private static void bindRemainingArguments(Field[] fields, Object instance, ParseResult result, int startOrder) {
        List<String> remaining;
        if(startOrder > 0) {
            remaining = result.arguments.subList(startOrder, result.arguments.size());
        } else {
            remaining = result.arguments;
        }
        
        for(Field field : fields) {
            Arguments arguments = field.getAnnotation(Arguments.class);
            if(arguments == null) {
                continue;
            }

            bindValueToField(field, instance, remaining);
            return;
        }
    }

    private static int bindAllArgumentsWithOrder(Field[] fields, Object instance, ParseResult result) {
        int maxOrderRead = -1;
        
        for(Field field : fields) {
            Argument argument = field.getAnnotation(Argument.class);
            if(argument == null) {
                continue;
            }
            
            int order = argument.order();
            if(order >= result.arguments.size()) {
                if(argument.required()) {
                    throw new RuntimeException("missing mandatory argument param");
                }
                
                continue;
            }
            
            maxOrderRead = Math.max(maxOrderRead, order);
            
            bindValueToField(field, instance, result.arguments.get(order));
        }
        
        return maxOrderRead + 1;
    }

    private static void bindAllOptions(Field[] fields, Object instance, ParseResult result) {
        for(Field field : fields) {
            // check for @Option annotation
            Option option = field.getAnnotation(Option.class);
            if(option == null) {
                continue;
            }

            String[] names = option.name();
            OptionType type = option.type();
            List<String> values = null;
            switch(type) {
                case COMMAND:
                    values = getOptionValues(result.commandOptions, names);
                    break;
                    
                case GLOBAL:
                    values = getOptionValues(result.globalOptions, names);
                    break;
                    
                case GROUP:
                    values = getOptionValues(result.groupOptions, names);
                    break;
                    
                default:
                    break;
            }
            
            if(option.required() && AssertUtils.isEmpty(values)) {
                throw new RuntimeException("Required param not available");
            }
            
            bindValueToField(field, instance, values);
        }
    }

    private static void bindValueToField(Field field, Object instance, Object value) {
        if(value instanceof List<?>) {
            List<?> values = (List<?>) value;
            
            if(values.isEmpty()) {
                ReflectionUtils.bindValueQuiet(field, instance, null);
                return;
            }
            
            if(values.size() == 1) {
                bindValueToField(field, instance, values.get(0));
                return;
            }
        }
        
        Class<?> fieldClass = field.getType();
        if(converters.containsKey(fieldClass)) {
            value = converters.get(fieldClass).convertFrom(field, instance, value);
        }
        
        try {
            ReflectionUtils.bindValue(field, instance, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getOptionValues(SimpleMultiMap<String, String> options, String[] names) {
        List<String> values = new ArrayList<>();
        
        for(String name : names) {
            List<String> list = options.getValues(name);
            if(list == null || list.isEmpty()) {
                continue;
            }
            
            values.addAll(list);
        }
        
        return values;
    }
}
