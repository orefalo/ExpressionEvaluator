package com.crionics.expr;

/**
 * @author Olivier Refalo orefalo@mail.dotcom.fr
 * @see http://www.crionics.com/orefalo
 * @version 1.00
 * 
 *          Arithmetic evaluator. This class is able read and compute
 *          expressions such as:
 * 
 *          ((3+5)/39) VAR_A*VAR_B 35=35 returns -> TRUE
 * 
 *          It implements: +,-, /, *, % (modulo), | (or), & (and), = (equal), #
 *          (different)
 * 
 *          Warning, operator priorities or not implemented -> use parenthesis !
 */

final public class Expression {

    private static final char FLOAT_SEPARATOR = '.';

    private int idx_;
    private String expr_;
    private Object parameters_ = null;

    private Expression()
    {
    }

    public Expression(String _expr)
    {
	expr_ = _expr;
	idx_ = 0;
    }

    abstract class BinaryOperator {
	BinaryOperator()
	{
	}

	public abstract double evaluate(double _arg1, double _arg2) throws IllegalArgumentException;

    }

    final class SubOperator extends BinaryOperator {
	public double evaluate(double _arg1, double _arg2) throws IllegalArgumentException
	{
	    return (_arg1 - _arg2);
	}
    }

    final class AddOperator extends BinaryOperator {
	public double evaluate(double _arg1, double _arg2) throws IllegalArgumentException
	{
	    return (_arg1 + _arg2);
	}
    }

    final class MulOperator extends BinaryOperator {
	public double evaluate(double _arg1, double _arg2) throws IllegalArgumentException
	{
	    return (_arg1 * _arg2);
	}
    }

    final class ModOperator extends BinaryOperator {
	public double evaluate(double _arg1, double _arg2) throws IllegalArgumentException
	{
	    if (_arg2 == 0)
		throw new IllegalArgumentException("% by zero");
	    return (_arg1 % _arg2);
	}
    }

    final class DivOperator extends BinaryOperator {
	public double evaluate(double _arg1, double _arg2) throws IllegalArgumentException
	{
	    if (_arg2 == 0)
		throw new IllegalArgumentException("Division by zero");
	    return (_arg1 / _arg2);
	}
    }

    final class AndOperator extends BinaryOperator {
	public double evaluate(double _arg1, double _arg2) throws IllegalArgumentException
	{
	    boolean a = getBoolean(_arg1);
	    boolean b = getBoolean(_arg2);
	    return (getDouble(a && b));
	}
    }

    final class OrOperator extends BinaryOperator {

	public double evaluate(double _arg1, double _arg2) throws IllegalArgumentException
	{
	    boolean a = getBoolean(_arg1);
	    boolean b = getBoolean(_arg2);
	    return (getDouble(a || b));
	}
    }

    final class EqualOperator extends BinaryOperator {
	public double evaluate(double _arg1, double _arg2) throws IllegalArgumentException
	{
	    return (getDouble(_arg1 == _arg2));
	}
    }

    final class DifferentOperator extends BinaryOperator {
	public double evaluate(double _arg1, double _arg2) throws IllegalArgumentException
	{
	    return (getDouble(_arg1 != _arg2));
	}
    }

    private boolean getBoolean(double _v)
    {
	return (_v != 0);
    }

    private double getDouble(boolean _b)
    {
	if (_b == true)
	    return (1);
	return (0);
    }

    public double evaluate(Object _param) throws ExpressionException, IllegalArgumentException
    {
	idx_ = 0;

	if (checkParenthesis())
	{
	    parameters_ = _param;
	    double v = evaluate();

	    // free link to resource
	    parameters_ = null;
	    return (v);
	} else
	    throw new ExpressionException("Parenthesis mismatch");
    }

    private double evaluate() throws ExpressionException, IllegalArgumentException
    {

	double result = 0;

	while (idx_ < expr_.length())
	{
	    char c = expr_.charAt(idx_);

	    if (c == ')')
	    {
		idx_++;
		return (result);
	    }

	    result = getValue(c);
	    while (idx_ < expr_.length())
	    {
		c = expr_.charAt(idx_);

		if (c == ')')
		{
		    idx_++;
		    return (result);
		}

		BinaryOperator op = getOperator(c);
		double r2 = evaluate();
		result = op.evaluate(result, r2);
	    }
	}
	return (result);
    }

    // Returns true if the expression pointer points on the given variable name
    private boolean checkVariable(String _vName)
    {

	if (expr_.toUpperCase().startsWith(_vName, idx_))
	{
	    idx_ += _vName.length();
	    return (true);
	} else
	    return (false);
    }

    // Read a Value.
    private double getValue(char _c) throws ExpressionException, IllegalArgumentException
    {

	// This is the place where you could add some unary operators!
	// parameters_ is an object passed as a parameter of evaluate()

	if (parameters_ instanceof MyTestObject)
	{
	    MyTestObject o = (MyTestObject) parameters_;
	    if (checkVariable("VAR_A"))
		return (o.getA());
	    if (checkVariable("VAR_B"))
		return (o.getB());
	}

	// End of parameters processing.

	if (_c == '(')
	{
	    idx_++;
	    return (evaluate());
	} else if (_c == '-')
	{
	    idx_++;
	    return (-getValue(expr_.charAt(idx_)));
	}
	return (readFloat());
    }

    // Read ASCII -> double
    private double getAsc(String _s)
    {
	double result = 0;

	if (_s != null)
	{
	    for (int i = 0; i < _s.length(); i++)
		result = (result * 256) + (double) _s.charAt(i);
	}
	return (result);
    }

    /**
     * This function returns true if the parenthesis syntaxe is correct as many
     * '(' as ')'
     */
    private boolean checkParenthesis()
    {
	int nbpar = 0;
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < expr_.length(); i++)
	{
	    char c = expr_.charAt(i);

	    if (c != ' ')
		sb.append(c);

	    if (c == '(')
		nbpar++;
	    else if (c == ')')
		nbpar--;

	}

	expr_ = new String(sb);

	return (nbpar == 0);
    }

    private boolean isDigit(char _c)
    {
	return ('0' <= _c && _c <= '9');
    }

    // ASCII -> int convertion
    // returns -1 if ASCII char is '.'
    // returns -2 if the caracter in not [0-9] or .
    private int readDigit()
    {
	if (idx_ < expr_.length())
	{
	    char c = expr_.charAt(idx_);

	    if (c == FLOAT_SEPARATOR)
		return (-1);

	    if (isDigit(c))
		return (c - '0');
	}
	return (-2);
    }

    // Read a float
    private double readFloat()
    {
	double fp = 0;
	int ip = readIntPart();

	if (idx_ < expr_.length() && expr_.charAt(idx_) == FLOAT_SEPARATOR)
	{
	    idx_++;
	    fp = readFloatPart();
	}
	return (ip + fp);
    }

    // Returns the integer part of an ASCII float
    private int readIntPart()
    {
	int r = 0, v = 0;

	// while caracter is valid
	v = readDigit();
	while (v != -2)
	{
	    // if '.' caracter -> return result
	    if (v == -1)
		return (r);

	    idx_++;
	    r = (r * 10) + v;
	    v = readDigit();
	}
	return (r);
    }

    // Returns the float part of an ASCII float
    private double readFloatPart()
    {
	double r = 0;
	int v = 0;

	// while caracter is valid [0-9]
	v = readDigit();
	while (v != -2 && v != -1)
	{
	    idx_++;
	    r = (r + v) / 10;
	    v = readDigit();
	}
	return (r);
    }

    public String toString()
    {
	return (expr_);
    }

    BinaryOperator getOperator(char c) throws ExpressionException
    {
	// If you have new binary operators, add them here
	switch (c) {
	case '+':
	    idx_++;
	    return (ao_);
	case '-':
	    idx_++;
	    return (so_);
	case '/':
	    idx_++;
	    return (do_);
	case '*':
	    idx_++;
	    return (mo_);
	case '%':
	    idx_++;
	    return (Mo_);
	case '|':
	    idx_++;
	    return (oo_);
	case '&':
	    idx_++;
	    return (Ao_);
	case '=':
	    idx_++;
	    return (eo_);
	case '#':
	    idx_++;
	    return (Do_);
	default:
	    throw new ExpressionException("Invalid operator :" + c);
	}
    }

    private final SubOperator so_ = new SubOperator();
    private final AddOperator ao_ = new AddOperator();
    private final MulOperator mo_ = new MulOperator();
    private final DivOperator do_ = new DivOperator();
    private final ModOperator Mo_ = new ModOperator();
    private final EqualOperator eo_ = new EqualOperator();
    private final DifferentOperator Do_ = new DifferentOperator();
    private final OrOperator oo_ = new OrOperator();
    private final AndOperator Ao_ = new AndOperator();

}
