package com.autsia.bracer.tests;

import com.autsia.bracer.BracerParser;
import org.apache.commons.math3.complex.Complex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class.
 * User: Dmytro
 * Date: 23.01.13
 * Time: 12:52
 */
public class BracerParserTest {
    private final String INPUT_NOVAR = "-sin(3+4I+cosh(6*I)/exp(10/pow(22,-1)))";
    private final String INPUT_VAR = "-sin(3+var*I+cosh(10*I)/exp(10/pow(22,-1)))";
    private final String OUTPUT = "-3.854 + 27.017I";
    private BracerParser bracerParser;

    @Before
    public void setUp() throws Exception {
        bracerParser = new BracerParser(3);
    }

    @Test
    public void testSetPrecision() throws Exception {
        bracerParser.setPrecision(10);
        Assert.assertEquals(10, bracerParser.getPrecision());
    }

    @Test
    public void testGetPrecision() throws Exception {
        Assert.assertEquals(3, bracerParser.getPrecision());
    }

    @Test
    public void testEvaluateNoVar() throws Exception {
        bracerParser.parse(INPUT_NOVAR);
        Assert.assertEquals(OUTPUT, bracerParser.evaluate());
    }

    @Test
    public void testEvaluateVar() throws Exception {
        bracerParser.parse(INPUT_VAR);
        Assert.assertEquals(OUTPUT, bracerParser.evaluate(4));
    }

    @Test
    public void testEvaluateComplexNoVar() throws Exception {
        bracerParser.parse(INPUT_NOVAR);
        Assert.assertEquals(new Complex(-3.854, 27.017), bracerParser.evaluateComplex());
    }

    @Test
    public void testEvaluateComplexVar() throws Exception {
        bracerParser.parse(INPUT_VAR);
        Assert.assertEquals(new Complex(-3.854, 27.017), bracerParser.evaluateComplex(4));
    }

    @Test
    public void testFormat() throws Exception {
        Assert.assertEquals(OUTPUT, bracerParser.format(new Complex(-3.854, 27.017)));
    }
}
