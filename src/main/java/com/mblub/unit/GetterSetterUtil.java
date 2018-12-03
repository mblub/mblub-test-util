package com.mblub.unit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Helper for testing basic getters and setters of a class.
 * 
 * Currently supports only classes with a no-arg constructor, and only fields of
 * certain recognized types (all other classes/fields are skipped).
 * 
 * @author mblubaugh
 * 
 */
public class GetterSetterUtil {

  // Map of recognized classes to object generators
  private static final Map<Class<?>, ObjectGenerator> OBJECT_GENERATOR_MAP = buildObjectGeneratorMap();
  private static final String RECOGNIZED_TYPES = formatObjectGeneratorTypes(OBJECT_GENERATOR_MAP);

  private static Map<Class<?>, ObjectGenerator> buildObjectGeneratorMap() {
    ObjectGenerator[] generators = new ObjectGenerator[] { new ObjectGenerator(String.class) {
      @Override
      public Object generateObject() {
        return RandomStringUtils.randomAlphanumeric(10);
      }
    }, new ObjectGenerator(Integer.class) {
      @Override
      public Object generateObject() {
        return RandomUtils.nextInt();
      }
    }, new ObjectGenerator(Calendar.class) {
      @Override
      public Object generateObject() {
        return new GregorianCalendar();
      }
    } };

    Map<Class<?>, ObjectGenerator> generatorMap = new HashMap<Class<?>, ObjectGenerator>();
    for (ObjectGenerator generator : generators) {
      generatorMap.put(generator.getClassOfGeneratedObject(), generator);
    }
    return generatorMap;
  }

  private static String formatObjectGeneratorTypes(Map<Class<?>, ObjectGenerator> generatorMap) {
    StringBuilder sb = new StringBuilder();
    for (ObjectGenerator generator : generatorMap.values()) {
      sb.append(generator.getClassOfGeneratedObject().getName());
      sb.append(", ");
    }
    sb.deleteCharAt(sb.length() - 2);
    return sb.toString();
  }

  // Class to be tested
  private Class<?> classUnderTest;

  // no-arg constructor
  private Constructor<?> constructor;

  /**
   * Helper for test of test
   * 
   * @return classUnderTest
   */
  protected Class<?> getClassUnderTest() {
    return classUnderTest;
  }

  /**
   * Helper for test of test
   * 
   * @return constructor
   */
  protected Constructor<?> getConstructor() {
    return constructor;
  }

  /**
   * Helper for test of test
   * 
   * @param classUnderTest
   *          classUnderTest
   */
  protected void setClassUnderTest(Class<?> classUnderTest) {
    this.classUnderTest = classUnderTest;
  }

  /**
   * Helper for test of test
   * 
   * @param constructor
   *          constructor
   */
  protected void setConstructor(Constructor<?> constructor) {
    this.constructor = constructor;
  }

  private GetterSetterUtil(Class<?> classUnderTest) {
    this.classUnderTest = classUnderTest;
  }

  /**
   * Construct and initialize a tester for a given class.
   * 
   * @param classUnderTest
   *          classUnderTest
   * @return GetterSetterUtil
   */
  public static GetterSetterUtil getTesterForClass(Class<?> classUnderTest) {
    GetterSetterUtil tester = new GetterSetterUtil(classUnderTest);
    tester.initialize();
    return tester;
  }

  /**
   * Set up the test utility: get the no-arg constructor
   */
  protected void initialize() {
    try {
      constructor = classUnderTest.getConstructor();
    } catch (SecurityException se) {
      log("Skipping class " + classUnderTest.getName() + " due to SecurityException: " + se);
    } catch (NoSuchMethodException nsme) {
      log("Skipping class " + classUnderTest.getName() + " because no-argument constructor is not declared.");
    }
  }

  /**
   * Test all the getters and setters of fields of recognized types.
   * 
   * @throws Exception
   *           excpetion
   */
  public void runTest() throws Exception {
    if (constructor != null) {
      log("Testing class " + classUnderTest.getName() + " for fields of recognized types (" + RECOGNIZED_TYPES
              + ")...");
      for (Field field : classUnderTest.getDeclaredFields()) {
        testFieldIfIncluded(field);
      }
    }
  }

  /**
   * Check if the field is to be included, and if so, run tests on that field.
   * 
   * @param field
   *          field object
   * @throws Exception
   *           exception
   */
  protected void testFieldIfIncluded(Field field) throws Exception {
    if (Modifier.isStatic(field.getModifiers())) {
      log("  Skipping field " + field.getName() + " because it is static.");
    } else if (Modifier.isFinal(field.getModifiers())) {
      log("  Skipping field " + field.getName() + " because it is final.");
    } else {
      boolean isFound = false;
      for (Class<?> recognizedClass : OBJECT_GENERATOR_MAP.keySet()) {
        if (recognizedClass.isAssignableFrom(field.getType())) {
          isFound = true;
          testField(classUnderTest, constructor, field, recognizedClass);
          break;
        }
      }
      if (!isFound) {
        log("  Skipping field " + field.getName() + " because its type (" + field.getType().getName()
                + ") is not recognized.");
      }
    }
  }

  /**
   * Test the getter and setter of one field.
   * 
   * @param classUnderTest
   *          classUnderTest
   * @param constructor
   *          constructor
   * @param field
   *          field object
   * @param recognizedClass
   *          recognizedClass
   * @throws Exception
   *           exception
   */
  protected void testField(final Class<?> classUnderTest, final Constructor<?> constructor, final Field field,
          final Class<?> recognizedClass) throws Exception {
    String fieldName = field.getName();
    final Object objectUnderTest = constructor.newInstance();
    field.setAccessible(true);

    ObjectGenerator objectGenerator = getGeneratorFor(recognizedClass);

    String getterName = "get" + StringUtils.capitalize(fieldName);
    try {
      Method getter = classUnderTest.getDeclaredMethod(getterName);
      if (recognizedClass.isAssignableFrom(getter.getReturnType())) {
        Object expectedValue = objectGenerator.generateObject();
        field.set(objectUnderTest, expectedValue);
        final Object actualValue = getter.invoke(objectUnderTest);
        assertEquals("Value returned by method " + getterName + " after setting field " + fieldName, expectedValue,
                actualValue);
      } else {
        log("  Skipping getter for field " + fieldName + " because " + getterName + "() returns "
                + getter.getReturnType().getName() + ", not " + field.getType().getName());
      }
    } catch (NoSuchMethodException nsme) {
      log("  Skipping getter for field " + fieldName + " because " + getterName + "() is not declared.");
    }

    String setterName = "set" + StringUtils.capitalize(fieldName);
    try {
      Method setter = classUnderTest.getDeclaredMethod(setterName, field.getType());
      if (setter.getReturnType().equals(Void.TYPE)) {
        Object expectedValue = objectGenerator.generateObject();
        setter.invoke(objectUnderTest, expectedValue);
        final Object actualValue = field.get(objectUnderTest);
        assertEquals("Value of field " + fieldName + " after invoking method " + setterName, expectedValue,
                actualValue);
      } else {
        log("  Skipping setter for field " + fieldName + " because " + setterName + "(" + field.getType().getName()
                + ") returns " + setter.getReturnType().getName() + ", not void.");
      }
    } catch (NoSuchMethodException nsme) {
      log("  Skipping setter for field " + fieldName + " because " + setterName + "(" + field.getType().getName()
              + ") is not declared.");
    }

    String witherName = "with" + StringUtils.capitalize(fieldName);
    try {
      Method wither = classUnderTest.getDeclaredMethod(witherName, field.getType());
      if (wither.getReturnType().equals(classUnderTest)) {
        Object expectedValue = objectGenerator.generateObject();
        Object actualResult = wither.invoke(objectUnderTest, expectedValue);
        final Object actualValue = field.get(objectUnderTest);
        assertEquals("Value of field " + fieldName + " after invoking method " + witherName, expectedValue,
                actualValue);
        org.junit.jupiter.api.Assertions.assertSame(objectUnderTest, actualResult, "result of method " + witherName);
      } else {
        log("  Skipping \"with\" method for field " + fieldName + " because " + witherName + "("
                + field.getType().getName() + ") returns " + wither.getReturnType().getName() + ", not "
                + classUnderTest + ".");
      }
    } catch (NoSuchMethodException nsme) {
      log("  Skipping \"with\" method for field " + fieldName + " because " + witherName + "("
              + field.getType().getName() + ") is not declared.");
    }
  }

  /**
   * Get the generator for the recognized class
   * 
   * @param recognizedClass
   *          recognizedClass
   * @return ObjectGenerator
   */
  protected ObjectGenerator getGeneratorFor(final Class<?> recognizedClass) {
    return OBJECT_GENERATOR_MAP.get(recognizedClass);
  }

  /**
   * Local implementation of assertEquals, to facilitate testing of testField
   * method
   * 
   * @param description
   *          description
   * @param expectedValue
   *          expected value
   * @param actualValue
   *          actual value
   * @throws Exception
   *           exception
   */
  protected void assertEquals(String description, Object expectedValue, Object actualValue) throws Exception {
    org.junit.jupiter.api.Assertions.assertEquals(expectedValue, actualValue, description);
  }

  /**
   * Local log helper
   * 
   * @param message
   *          message
   */
  protected void log(String message) {
    // TODO: configure log4j for maven-surefire-plugin, and use log4j here
    // instead of System.out
    System.out.println(message);
  }

  /**
   * Generic object generator for use in testing.
   * 
   * TODO research whether there are basic Apache utilities for this (or other
   * library)
   * 
   * @author mblubaugh
   * 
   */
  public static abstract class ObjectGenerator {
    private Class<?> classOfGeneratedObject;

    public ObjectGenerator(Class<?> classOfGeneratedObject) {
      this.classOfGeneratedObject = classOfGeneratedObject;
    }

    public Class<?> getClassOfGeneratedObject() {
      return classOfGeneratedObject;
    }

    public abstract Object generateObject();
  }
}
