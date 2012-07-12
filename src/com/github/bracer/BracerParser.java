/*
 * Copyright 2012 Dmytro Titov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.bracer;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.complex.ComplexFormat;

/**
 * Class for parsing and evaluating math expressions
 * 
 * @author Dmytro Titov
 * @version 0.5.0
 * @since 0.1.0
 */
public class BracerParser {
	/* list of available functions */
	private final String[] FUNCTIONS = { "abs", "acos", "arg", "asin", "atan",
			"conj", "cos", "cosh", "exp", "imag", "log", "neg", "pow", "real",
			"sin", "sinh", "sqrt", "tan", "tanh" };
	/* list of available operators */
	private final String OPERATORS = "+-*/";
	/* separator of arguments */
	private final String SEPARATOR = ",";
	/* imaginary symbol */
	private final String IMAGINARY = "I";
	/* variable token */
	private final String VARIABLE = "var";
	/* settings for complex formatting */
	private ComplexFormat complexFormat = new ComplexFormat(IMAGINARY);
	/* settings for numbers formatting */
	private NumberFormat numberFormat = NumberFormat.getInstance();
	/* temporary stack that holds operators, functions and brackets */
	private Stack<String> stackOperations = new Stack<String>();
	/* stack for holding expression converted to reversed polish notation */
	private Stack<String> stackRPN = new Stack<String>();
	/* stack for holding the calculations result */
	private Stack<String> stackAnswer = new Stack<String>();

	/**
	 * Class ctor for setting up the complex format of the parser
	 * 
	 * @param precision
	 *            Number of digits after the dot
	 * @since 0.2.0
	 */
	public BracerParser(int precision) {
		setPrecision(precision);
	}

	/**
	 * Set the precision of the real and imaginary parts of numbers
	 * 
	 * @param precision
	 *            Number of digits after the dot
	 * @since 0.2.0
	 */
	public void setPrecision(int precision) {
		numberFormat.setMinimumFractionDigits(precision);
		numberFormat.setMaximumFractionDigits(precision);
		complexFormat.setRealFormat(numberFormat);
		complexFormat.setImaginaryFormat(numberFormat);
	}

	/**
	 * Get the precision of the real and imaginary parts of numbers
	 * 
	 * @return Precision
	 * @since 0.2.0
	 */
	public int getPrecision() {
		return numberFormat.getMinimumFractionDigits();
	}

	/**
	 * Parses the math expression (complicated formula) and stores the result
	 * 
	 * @param expression
	 *            <code>String</code> input expression (math formula)
	 * @throws <code>ParseException</code> if the input expression is not
	 *         correct
	 * @since 0.3.0
	 */
	public void parse(String expression) throws ParseException {
		/* cleaning stacks */
		stackOperations.clear();
		stackRPN.clear();

		/*
		 * make some preparations: remove spaces; handle unary + and -, handle
		 * degree character
		 */
		expression = expression.replace(" ", "")
				.replace("°", "*" + Double.toString(Math.PI) + "/180")
				.replace("(-", "(0-").replace(",-", ",0-").replace("(+", "(0+")
				.replace(",+", ",0+");
		if (expression.charAt(0) == '-' || expression.charAt(0) == '+') {
			expression = "0" + expression;
		}
		/* splitting input string into tokens */
		StringTokenizer stringTokenizer = new StringTokenizer(expression,
				OPERATORS + SEPARATOR + "()", true);

		/* loop for handling each token - shunting-yard algorithm */
		while (stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			if (isSeparator(token)) {
				while (!stackOperations.empty()
						&& !isOpenBracket(stackOperations.lastElement())) {
					stackRPN.push(stackOperations.pop());
				}
			} else if (isOpenBracket(token)) {
				stackOperations.push(token);
			} else if (isCloseBracket(token)) {
				while (!stackOperations.empty()
						&& !isOpenBracket(stackOperations.lastElement())) {
					stackRPN.push(stackOperations.pop());
				}
				stackOperations.pop();
				if (!stackOperations.empty()
						&& isFunction(stackOperations.lastElement())) {
					stackRPN.push(stackOperations.pop());
				}
			} else if (isNumber(token)) {
				if (token.equals(IMAGINARY)) {
					stackRPN.push(complexFormat.format(new Complex(0, 1)));
				} else if (token.contains(IMAGINARY)) {
					stackRPN.push(complexFormat.format(complexFormat.parse("0+"
							+ token)));
				} else {
					stackRPN.push(token);
				}
			} else if (isOperator(token)) {
				while (!stackOperations.empty()
						&& isOperator(stackOperations.lastElement())
						&& getPrecedence(token) <= getPrecedence(stackOperations
								.lastElement())) {
					stackRPN.push(stackOperations.pop());
				}
				stackOperations.push(token);
			} else if (isFunction(token)) {
				stackOperations.push(token);
			} else {
				throw new ParseException("Unrecognized token", 0);
			}
		}
		while (!stackOperations.empty()) {
			stackRPN.push(stackOperations.pop());
		}

		/* reverse stack */
		Collections.reverse(stackRPN);
	}

	/**
	 * Evaluates once parsed math expression with no variable included
	 * 
	 * @return <code>String</code> representation of the result
	 * @throws <code>ParseException</code> if the input expression is not
	 *         correct
	 * @since 0.1.0
	 */
	public String evaluate() throws ParseException {
		if (!stackRPN.contains("var")) {
			return evaluate(0);
		}
		throw new ParseException("Unrecognized token: var", 0);
	}

	/**
	 * Evaluates once parsed math expression with "var" variable included
	 * 
	 * @param variableValue
	 *            User-specified <code>Double</code> value
	 * @return <code>String</code> representation of the result
	 * @throws <code>ParseException</code> if the input expression is not
	 *         correct
	 * @since 0.3.0
	 */
	public String evaluate(double variableValue) throws ParseException {
		/* check if is there something to evaluate */
		if (stackRPN.empty()) {
			return "";
		}

		/* clean answer stack */
		stackAnswer.clear();

		/* get the clone of the RPN stack for further evaluating */
		@SuppressWarnings("unchecked")
		Stack<String> stackRPN = (Stack<String>) this.stackRPN.clone();

		/* enroll the variable value into expression */
		Collections.replaceAll(stackRPN, VARIABLE,
				Double.toString(variableValue));

		/* evaluating the RPN expression */
		while (!stackRPN.empty()) {
			String token = stackRPN.pop();
			if (isNumber(token)) {
				stackAnswer.push(token);
			} else if (isOperator(token)) {
				Complex a = complexFormat.parse(stackAnswer.pop());
				Complex b = complexFormat.parse(stackAnswer.pop());
				if (token.equals("+")) {
					stackAnswer.push(complexFormat.format(b.add(a)));
				} else if (token.equals("-")) {
					stackAnswer.push(complexFormat.format(b.subtract(a)));
				} else if (token.equals("*")) {
					stackAnswer.push(complexFormat.format(b.multiply(a)));
				} else if (token.equals("/")) {
					stackAnswer.push(complexFormat.format(b.divide(a)));
				}
			} else if (isFunction(token)) {
				Complex a = complexFormat.parse(stackAnswer.pop());
				if (token.equals("abs")) {
					stackAnswer.push(complexFormat.format(a.abs()));
				} else if (token.equals("acos")) {
					stackAnswer.push(complexFormat.format(a.acos()));
				} else if (token.equals("arg")) {
					stackAnswer.push(complexFormat.format(a.getArgument()));
				} else if (token.equals("asin")) {
					stackAnswer.push(complexFormat.format(a.asin()));
				} else if (token.equals("atan")) {
					stackAnswer.push(complexFormat.format(a.atan()));
				} else if (token.equals("conj")) {
					stackAnswer.push(complexFormat.format(a.conjugate()));
				} else if (token.equals("cos")) {
					stackAnswer.push(complexFormat.format(a.cos()));
				} else if (token.equals("cosh")) {
					stackAnswer.push(complexFormat.format(a.cosh()));
				} else if (token.equals("exp")) {
					stackAnswer.push(complexFormat.format(a.exp()));
				} else if (token.equals("imag")) {
					stackAnswer.push(complexFormat.format(a.getImaginary()));
				} else if (token.equals("log")) {
					stackAnswer.push(complexFormat.format(a.log()));
				} else if (token.equals("neg")) {
					stackAnswer.push(complexFormat.format(a.negate()));
				} else if (token.equals("real")) {
					stackAnswer.push(complexFormat.format(a.getReal()));
				} else if (token.equals("sin")) {
					stackAnswer.push(complexFormat.format(a.sin()));
				} else if (token.equals("sinh")) {
					stackAnswer.push(complexFormat.format(a.sinh()));
				} else if (token.equals("sqrt")) {
					stackAnswer.push(complexFormat.format(a.sqrt()));
				} else if (token.equals("tan")) {
					stackAnswer.push(complexFormat.format(a.tan()));
				} else if (token.equals("tanh")) {
					stackAnswer.push(complexFormat.format(a.tanh()));
				} else if (token.equals("pow")) {
					Complex b = complexFormat.parse(stackAnswer.pop());
					stackAnswer.push(complexFormat.format(b.pow(a)));
				}
			}
		}

		if (stackAnswer.size() > 1) {
			throw new ParseException("Some operator is missing", 0);
		}

		return stackAnswer.pop();
	}

	/**
	 * Evaluates non-variable expression and returns it's value as a Complex
	 * object
	 * 
	 * @return <code>Complex</code> representation of complex number
	 * @throws <code>ParseException</code> if the input expression is not
	 *         correct
	 * @since 0.4.0
	 */
	public Complex evaluateComplex() throws ParseException {
		return complexFormat.parse(evaluate());
	}

	/**
	 * Evaluates variable expression and returns it's value as a Complex object
	 * 
	 * @param variableValue
	 *            User-specified <code>Double</code> value
	 * @return <code>Complex</code> representation of complex number
	 * @throws <code>ParseException</code> if the input expression is not
	 *         correct
	 * @since 0.4.0
	 */
	public Complex evaluateComplex(double variableValue) throws ParseException {
		return complexFormat.parse(evaluate(variableValue));
	}

	/**
	 * Converts <code>Complex</code> object to it's <code>String</code>
	 * representation
	 * 
	 * @param number
	 *            Input <code>Complex</code> number to convert
	 * @return <code>String</code> representation of complex number
	 * @since 0.5.0
	 */
	public String format(Complex number) {
		return complexFormat.format(number);
	}

	/**
	 * Check if the token is number (0-9, <code>IMAGINARY</code> or
	 * <code>VARIABLE</code>)
	 * 
	 * @param token
	 *            Input <code>String</code> token
	 * @return <code>boolean</code> output
	 * @since 0.1.0
	 */
	private boolean isNumber(String token) {
		try {
			Double.parseDouble(token);
		} catch (Exception e) {
			if (token.contains(IMAGINARY) || token.equals(VARIABLE)) {
				return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * Check if the token is function (e.g. "sin")
	 * 
	 * @param token
	 *            Input <code>String</code> token
	 * @return <code>boolean</code> output
	 * @since 0.1.0
	 */
	private boolean isFunction(String token) {
		for (String item : FUNCTIONS) {
			if (item.equals(token)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the token is <code>SEPARATOR</code>
	 * 
	 * @param token
	 *            Input <code>String</code> token
	 * @return <code>boolean</code> output
	 * @since 0.1.0
	 */
	private boolean isSeparator(String token) {
		return token.equals(SEPARATOR);
	}

	/**
	 * Check if the token is opening bracket
	 * 
	 * @param token
	 *            Input <code>String</code> token
	 * @return <code>boolean</code> output
	 * @since 0.1.0
	 */
	private boolean isOpenBracket(String token) {
		return token.equals("(");
	}

	/**
	 * Check if the token is closing bracket
	 * 
	 * @param token
	 *            Input <code>String</code> token
	 * @return <code>boolean</code> output
	 * @since 0.1.0
	 */
	private boolean isCloseBracket(String token) {
		return token.equals(")");
	}

	/**
	 * Check if the token is operator (e.g. "+")
	 * 
	 * @param token
	 *            Input <code>String</code> token
	 * @return <code>boolean</code> output
	 * @since 0.1.0
	 */
	private boolean isOperator(String token) {
		return OPERATORS.contains(token);
	}

	/**
	 * Gets the precedence of the operator
	 * 
	 * @param token
	 *            Input <code>String</code> token
	 * @return <code>byte</code> precedence
	 * @since 0.1.0
	 */
	private byte getPrecedence(String token) {
		if (token.equals("+") || token.equals("-")) {
			return 1;
		}
		return 2;
	}
}
