package com.storedobject.core;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;
import com.storedobject.common.JSON;
import com.storedobject.common.MathUtility;
import com.storedobject.common.SORuntimeException;

import java.util.*;
import java.util.function.Function;

/**
 * The Formula class provides a mechanism to define, evaluate, and retrieve results of expressions or formulas.
 * It supports setting variables, expressions, constants, and custom functions and allows resolving dependencies
 * and retrieving computed values.
 *
 * @author Syam
 */
public class Formula {

    private Reporter reporter;
    private final Map<String, Expression> expressions = new TreeMap<>();
    private final Map<String, Function<String, Object>> functions = new TreeMap<>();
    private final Map<String, Object> values = new TreeMap<>();

    /**
     * Default constructor for the Formula class.
     * This constructor initializes a new instance of the Formula class
     * without setting any initial properties or performing any operations.
     */
    public Formula() {
    }

    /**
     * Constructs a new Formula object and initializes it with the provided formula.
     *
     * @param formula the formula to be set, represented as a string. It can be a single formula
     *                or multiple formulas separated by semicolons. If the input is null
     *                or empty, no formulas are set.
     */
    public Formula(String formula) {
        setFormula(formula);
    }

    public Reporter getReporter() {
        return reporter;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    private static void checkVar(String v) {
        if(v == null || v.isEmpty()) {
            throw new SORuntimeException("Variable name cannot be empty");
        }
        if (!v.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            throw new SORuntimeException("Variable name must contain only alphabets, digits and underscores");
        }
    }

    /**
     * Sets the formula(s) to be processed. Accepts a semicolon-separated string of formulas
     * and applies each individually.
     *
     * @param formula a semicolon-separated string representing one or more formulas to be set;
     *                if null or empty, the method performs no action.
     */
    public void setFormula(String formula) {
        if(formula == null || formula.isBlank()) {
            return;
        }
        String[] formulas = formula.split(";");
        for(String f: formulas) {
            setSingleFormula(f);
        }
    }

    private void setSingleFormula(String formula) {
        int p = formula.indexOf('=');
        if(p < 0) {
            throw new SORuntimeException("Unable to determine variable name");
        }
        setFormula(formula.substring(0, p).trim(), formula.substring(p + 1).trim());
    }

    /**
     * Sets a formula for a given variable name. The formula is parsed
     * and validated before being associated with the variable name.
     *
     * @param variableName the name of the variable to which the formula will be assigned.
     *                     It must be a non-null, non-empty string consisting only of
     *                     letters, digits, and underscores, and should start with a
     *                     letter or an underscore.
     * @param formula      the string representation of the formula to be associated
     *                     with the variable. The formula must be parseable and valid.
     *                     If validation fails, a runtime exception is thrown.
     * @throws SORuntimeException if the variable name is invalid or the formula cannot be parsed.
     */
    public void setFormula(String variableName, String formula) {
        if(formula == null || formula.isBlank()) {
            return;
        }
        checkVar(variableName);
        Expression e = new Expression(formula);
        try {
            e.validate();
        } catch (ParseException ex) {
            throw new SORuntimeException(ex);
        }
        expressions.put(variableName, e);
    }

    /**
     * Associates a function with a variable identified by the specified name
     * for use in formula processing. The function takes a string as input
     * and returns an object as output. The variable name must adhere to the
     * specified naming conventions.
     *
     * @param variableName the name of the variable to associate the function with.
     *                     It must not be null, empty, or contain invalid characters.
     *                     Valid names consist of alphabets, digits, and underscores,
     *                     and must start with an alphabet or underscore.
     * @param function     the function to associate with the specified variable.
     *                     The function takes a string as input and returns an
     *                     object as the result. It must not be null.
     * @throws SORuntimeException if the provided variable name is null, empty, or invalid.
     * @throws NullPointerException if the provided function is null.
     */
    public void setFunction(String variableName, Function<String, Object> function) {
        checkVar(variableName);
        functions.put(variableName, function);
    }

    /**
     * Associates a specified variable name with a {@link ShortNameFunction}.
     *
     * @param variableName the name of the variable to be associated with the function
     * @param shortNameFunction the ShortNameFunction to be set for the specified variable name
     */
    public void setFunction(String variableName, ShortNameFunction shortNameFunction) {
        checkVar(variableName);
        functions.put(variableName, sn -> shortNameFunction.getFunction(this));
    }

    /**
     * Assigns a specified value to the given variable name in the formula's context.
     * The method validates the variable name using {@code checkVar} and processes the value
     * before storing it in the {@code values} map.
     *
     * @param variableName the name of the variable to be assigned a value.
     *                     The name must not be null, empty, or invalid;
     *                     it should only contain alphabets, digits, and underscores,
     *                     and must not start with a digit.
     * @param value        the value to be assigned to the variable.
     *                     If the value is an instance of {@code Number}, it is converted
     *                     to a {@code BigDecimal} using {@code MathUtility.toBigDecimal}.
     */
    public void setValue(String variableName, Object value) {
        checkVar(variableName);
        values.put(variableName, val(value));
    }

    private static Object val(Object o) {
        if(o instanceof Number n) {
            return MathUtility.toBigDecimal(n);
        }
        return o;
    }

    /**
     * Retrieves the value associated with the specified variable name.
     * If the variable does not have a directly assigned value,
     * the method determines the value using associated functions or expressions.
     *
     * @param variableName the name of the variable to retrieve the value for;
     *                     it must not be null or empty, and must follow
     *                     valid naming conventions (letters, digits, and underscores).
     * @return the value associated with the given variable name. If the variable is not found,
     *         its value is derived using related functions or expressions, or null if it cannot be determined.
     * @throws SORuntimeException if there is a circular reference, or if a value for a required
     *                             variable cannot be determined.
     */
    public Object getValue(String variableName) {
        return getValue(variableName, new ArrayList<>());
    }

    private Object getValue(String variableName, List<Set<String>> varsList) {
        Object o = values.get(variableName);
        if(o != null) return o;
        Function<String, Object> function = functions.get(variableName);
        if(function != null) return function.apply(variableName);
        Expression e = expressions.get(variableName);
        if(e == null) return null;
        try {
            Map<String, Object> values = new HashMap<>();
            Set<String> vars = vars(e);
            for (String v: vars) {
                if(this.values.containsKey(v)) {
                    values.put(v, this.values.get(v));
                    continue;
                }
                if(contains(varsList, v)) {
                    throw new SORuntimeException("Circular reference - " + v);
                }
            }
            varsList.add(vars);
            for (String v: vars) {
                o = getValue(v, varsList);
                if(o == null) {
                    throw new SORuntimeException("Can't determine value of variable - " + v);
                }
                values.put(v, o);
            }
            return val(e.withValues(values).evaluate());
        } catch (ParseException | EvaluationException ex) {
            throw new SORuntimeException(ex);
        }
    }

    /**
     * Retrieves the value associated with the specified variable name and casts it to the specified type.
     * The method attempts to retrieve the value for the given variable name and casts it to the provided class type.
     * If the value cannot be cast to the specified type, a {@code ClassCastException} is caught, and a runtime exception is thrown.
     *
     * @param <T>           the type of the value to be returned.
     * @param variableClass the class representing the type to which the value should be cast.
     *                      It must not be null.
     * @param variableName  the name of the variable to retrieve the value for.
     *                      Must not be null or empty and must adhere to valid naming conventions
     *                      (letters, digits, and underscores).
     * @return the value associated with the given variable name, cast to the specified type.
     * @throws SORuntimeException if a type cast fails or if the underlying retrieval encounters an error.
     */
    public <T> T getValue(Class<T> variableClass, String variableName) {
        try {
            return variableClass.cast(getValue(variableName));
        } catch (ClassCastException ex) {
            throw new SORuntimeException(ex);
        }
    }

    private static boolean contains(List<Set<String>> varsList, String variableName) {
        return varsList.stream().anyMatch(vars -> vars.contains(variableName));
    }

    private static Object val(EvaluationValue ev) {
        return ev.getValue();
    }

    private Set<String> vars(Expression expression) {
        Set<String> vars = new HashSet<>();
        try {
            collect(new JSON(expression.getAbstractSyntaxTree().toJSON()), vars);
        } catch (ParseException e) {
            throw new SORuntimeException(e);
        }
        return vars;
    }

    private void collect(JSON json, Set<String> vars) {
        if("VARIABLE_OR_CONSTANT".equals(json.getString("type"))) {
            String v = json.getString("value");
            if (!isConstant(v)) {
                vars.add(v);
            }
        }
        JSON children = json.get("children");
        if(children != null) {
            int n = children.getArraySize();
            for(int i = 0; i < n; i++) {
                collect(children.get(i), vars);
            }
        }
    }

    private static boolean isConstant(String name) {
        return switch (name) {
            case "PI", "E", "NULL", "TRUE", "FALSE", "DT_FORMAT_ISO_DATE_TIME", "DT_FORMAT_LOCAL_DATE_TIME", "DT_FORMAT_LOCAL_DATE" -> true;
            default -> false;
        };
    }
}
