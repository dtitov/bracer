/*
 * Copyright 2014 Dmytro Titov
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

package com.autsia.bracer.tests;

import com.autsia.bracer.BracerParser;
import org.apache.commons.math3.complex.Complex;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

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

    @Test
    public void testSimpleBoolean() throws Exception {
        bracerParser.parse("true or false");
        Assert.assertEquals("1", bracerParser.evaluate());
    }

    @Test
    public void testBoolean() throws Exception {
        bracerParser.parse("( ( true and ( false or ( true and ( ( true ) or ( false ) ) ) ) ) and ( ( false ) ) )");
        Assert.assertEquals("0", bracerParser.evaluate());

        Collection<String> stackRPN = bracerParser.getStackRPN();
        Assert.assertThat(stackRPN, IsIterableContainingInOrder.contains("&", "0", "&", "|", "&", "|", "0", "1", "1", "0", "1"));
    }

    @Test
    public void testBooleanNot1() throws Exception {
        bracerParser.parse("not( ( true and ( false or ( true and ( not( true ) or ( false ) ) ) ) ) and ( ( false ) ) )");
        Assert.assertEquals("1", bracerParser.evaluate());
    }

    @Test
    public void testBooleanNot2() throws Exception {
        bracerParser.parse("(((true | false) & not(false)) | (true | false))");
        Assert.assertEquals("1", bracerParser.evaluate());

        Collection<String> stackRPN = bracerParser.getStackRPN();
        Assert.assertThat(stackRPN, IsIterableContainingInOrder.contains("|", "|", "0", "1", "&", "not", "0", "|", "0", "1"));
    }

    @Test
    public void testUnaryNot() throws Exception {
        bracerParser.parse("(! (false))");
        Assert.assertEquals("1", bracerParser.evaluate());
    }

}
