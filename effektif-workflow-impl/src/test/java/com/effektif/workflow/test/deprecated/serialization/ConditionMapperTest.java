package com.effektif.workflow.test.deprecated.serialization;/* Copyright (c) 2015, Effektif GmbH.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.effektif.workflow.api.condition.And;
import com.effektif.workflow.api.condition.Comparator;
import com.effektif.workflow.api.condition.Condition;
import com.effektif.workflow.api.condition.Contains;
import com.effektif.workflow.api.condition.ContainsIgnoreCase;
import com.effektif.workflow.api.condition.Equals;
import com.effektif.workflow.api.condition.EqualsIgnoreCase;
import com.effektif.workflow.api.condition.GreaterThan;
import com.effektif.workflow.api.condition.GreaterThanOrEqual;
import com.effektif.workflow.api.condition.HasNoValue;
import com.effektif.workflow.api.condition.HasValue;
import com.effektif.workflow.api.condition.IsFalse;
import com.effektif.workflow.api.condition.IsTrue;
import com.effektif.workflow.api.condition.LessThan;
import com.effektif.workflow.api.condition.LessThanOrEqual;
import com.effektif.workflow.api.condition.Not;
import com.effektif.workflow.api.condition.NotContains;
import com.effektif.workflow.api.condition.NotContainsIgnoreCase;
import com.effektif.workflow.api.condition.NotEquals;
import com.effektif.workflow.api.condition.NotEqualsIgnoreCase;
import com.effektif.workflow.api.condition.Or;
import com.effektif.workflow.api.condition.SingleBindingCondition;
import com.effektif.workflow.api.workflow.Binding;
import com.effektif.workflow.impl.bpmn.BpmnMapper;
import com.effektif.workflow.impl.deprecated.json.Mappings;
import com.effektif.workflow.impl.memory.TestConfiguration;

/**
 * Tests BPMN serialisation for conditions.
 *
 * @author Peter Hilton
 */
public class ConditionMapperTest {

  protected static final Logger log = LoggerFactory.getLogger(ConditionMapperTest.class);
  static BpmnMapper bpmnMapper;

  @BeforeClass
  public static void initialize() {
    Mappings mappings = new Mappings();
    bpmnMapper = new BpmnMapper(new TestConfiguration());
    bpmnMapper.setMappings(mappings);
  }

  @Test
  public void testContains() {
    testComparator(Contains.class, "version", "PATCH");
  }

  @Test
  public void testNotContains() {
    testComparator(NotContains.class, "version", "PATCH");
  }

  @Test
  public void testContainsIgnoreCase() {
    testComparator(ContainsIgnoreCase.class, "version", "PATCH");
  }

  @Test
  public void testNotContainsIgnoreCase() {
    testComparator(NotContainsIgnoreCase.class, "version", "PATCH");
  }

  @Test
  public void testEquals() {
    testComparator(Equals.class, "version", "3.0");
  }

  @Test
  public void testNotEquals() {
    testComparator(NotEquals.class, "version", "3.0");
  }

  @Test
  public void testEqualsIgnoreCase() {
    testComparator(EqualsIgnoreCase.class, "version", "v3");
  }

  @Test
  public void testNotEqualsIgnoreCase() {
    testComparator(NotEqualsIgnoreCase.class, "version", "v3");
  }

  @Test
  public void testGreaterThan() {
    testComparator(GreaterThan.class, "issues", "10");
  }

  @Test
  public void testGreaterThanOrEqual() {
    testComparator(GreaterThanOrEqual.class, "issues", "10");
  }

  @Test
  public void testLessThan() {
    testComparator(LessThan.class, "issues", "10");
  }

  @Test
  public void testLessThanOrEqual() {
    testComparator(LessThanOrEqual.class, "issues", "10");
  }

  @Test
  public void testHasValue() {
    testSingleCondition(HasValue.class, "testsPassed");
  }

  @Test
  public void testHasNoValue() {
    testSingleCondition(HasNoValue.class, "testsPassed");
  }

  @Test
  public void testIsTrue() {
    testSingleCondition(IsTrue.class, "testsPassed");
  }

  @Test
  public void testIsFalse() {
    testSingleCondition(IsFalse.class, "testsPassed");
  }

  @Test
  public void testAnd() {
    Condition issues = new LessThan().left(new Binding().expression("issues")).right(new Binding().value("10"));
    Condition tests = new IsTrue().left(new Binding<String>().expression("testsPassed"));
    And condition = new And().condition(issues).condition(tests);
    condition = serialize(condition, And.class);

    assertEquals(2, condition.getConditions().size());

    // Note: the IsTrue condition is first, because conditions are deserialised in alphabetical order of class name.
    assertEquals(IsTrue.class, condition.getConditions().get(0).getClass());
    assertEquals("testsPassed", ((IsTrue) condition.getConditions().get(0)).getLeft().getExpression());

    assertEquals(LessThan.class, condition.getConditions().get(1).getClass());
  }

  @Test
  public void testOr() {
    Condition issues = new LessThan().left(new Binding().expression("issues")).right(new Binding().value("10"));
    Condition tests = new IsTrue().left(new Binding<String>().expression("testsPassed"));
    Or condition = new Or().condition(issues).condition(tests);
    condition = serialize(condition, Or.class);

    assertEquals(2, condition.getConditions().size());

    // Note: the IsTrue condition is first, because conditions are deserialised in alphabetical order of class name.
    assertEquals(IsTrue.class, condition.getConditions().get(0).getClass());
    assertEquals("testsPassed", ((IsTrue) condition.getConditions().get(0)).getLeft().getExpression());

    assertEquals(LessThan.class, condition.getConditions().get(1).getClass());
  }

  @Test
  public void testNot() {
    Condition issues = new LessThan().left(new Binding().expression("issues")).right(new Binding().value("10"));
    Not condition = new Not().condition(issues);
    condition = serialize(condition, Not.class);
    assertEquals(LessThan.class, condition.getCondition().getClass());
    LessThan deserialisedIssues = (LessThan) condition.getCondition();
    assertEquals("issues", deserialisedIssues.getLeft().getExpression());
    assertEquals("10", deserialisedIssues.getRight().getValue());
  }

  /**
   * Tests a comparator’s BPMN serialisation with instance that compares the given expression and value.
   */
  private <T extends Comparator> void testComparator(Class<T> type, String expression, String value) {
    try {
      T condition = (T) type.newInstance();
      condition.setLeft(new Binding<String>().expression(expression));
      condition.setRight(new Binding<String>().value(value));
      condition = serialize(condition, type);
      assertEquals(expression, condition.getLeft().getExpression());
      assertEquals(value, condition.getRight().getValue());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Tests a comparator’s BPMN serialisation with an instance that has the given expression.
   */
  private <T extends SingleBindingCondition> void testSingleCondition(Class<T> type, String expression) {
    try {
      T condition = (T) type.newInstance();
      condition.setLeft(new Binding<String>().expression(expression));
      condition = serialize(condition, type);
      assertEquals(expression, condition.getLeft().getExpression());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected <T extends Condition> T serialize(T condition, Class<T> conditionClass) {
    String xmlString = bpmnMapper.writeToString(condition);
    log.info("\n" + xmlString + "\n");
    T deserialisedCondition = bpmnMapper.readCondition(xmlString, conditionClass);
    assertNotNull(deserialisedCondition);
    assertTrue(deserialisedCondition.getClass().equals(conditionClass));
    return deserialisedCondition;
  }
}
