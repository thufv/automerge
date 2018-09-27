/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone.bugpatterns;

import com.google.errorprone.CompilationTestHelper;
import com.google.errorprone.bugpatterns.ArrayToString;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author adgar@google.com (Mike Edgar)
 */
public class ArrayToStringTest {

  private CompilationTestHelper compilationHelper;

  @Before
  public void setUp() {
    //compilationHelper = new CompilationTestHelper(new ArrayToString.Scanner());
    compilationHelper = new CompilationTestHelper(new ArrayToStringChecker().createScanner());
  }

  @Test
  public void testPositiveCase() throws Exception {
    compilationHelper.assertCompileFailsWithMessages(
        new File(this.getClass().getResource("ArrayToStringPositiveCases.java").toURI()));
  }

  @Test
  public void testNegativeCase() throws Exception {
    compilationHelper.assertCompileSucceeds(
        new File(this.getClass().getResource("ArrayToStringNegativeCases.java").toURI()));
  }
}