package beaconsperth;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * JUnit test for <code>SuffixURLFilter</code>.
 *
 * @author Andrzej Bialecki
 */
public class TestSuffixURLFilter extends TestCase {
  private static final String suffixes =
    "# this is a comment\n" +
    "\n" +
    ".gif\n" +
    ".jpg\n" +
    ".js\n";

  private static final String[] urls = new String[] {
    "http://www.example.com/test.gif",
    "http://www.example.com/TEST.GIF",
    "http://www.example.com/test.jpg",
    "http://www.example.com/test.JPG",
    "http://www.example.com/test.html",
    "http://www.example.com/test.HTML",
    "http://www.example.com/test.html?q=abc.js",
    "http://www.example.com/test.js?foo=bar&baz=bar#12333",
  };

  private static String[] urlsModeAccept = new String[] {
    null,
    urls[1],
    null,
    urls[3],
    urls[4],
    urls[5],
    null,
    urls[7]
  };



  private SuffixURLFilter filter = null;

  public TestSuffixURLFilter(String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(TestSuffixURLFilter.class);
  }

  public static void main(String[] args) {
    TestRunner.run(suite());
  }

  @Override
public void setUp() throws IOException {
    filter = new SuffixURLFilter(new StringReader(suffixes));
  }

  public void testModeAccept() {
    filter.setIgnoreCase(false);
    filter.setModeAccept(true);
    for (int i = 0; i < urls.length; i++) {
      assertTrue(urlsModeAccept[i] == filter.filter(urls[i]));
    }
  }

  public void testModeReject() {
    filter.setIgnoreCase(false);
    filter.setModeAccept(false);
    for (int i = 0; i < urls.length; i++) {
      assertTrue(urlsModeReject[i] == filter.filter(urls[i]));
    }
  }

  public void testModeAcceptIgnoreCase() {
    filter.setIgnoreCase(true);
    filter.setModeAccept(true);
    for (int i = 0; i < urls.length; i++) {
      assertTrue(urlsModeAcceptIgnoreCase[i] == filter.filter(urls[i]));
    }
  }

  public void testModeRejectIgnoreCase() {
    filter.setIgnoreCase(true);
    filter.setModeAccept(false);
    for (int i = 0; i < urls.length; i++) {
      assertTrue(urlsModeRejectIgnoreCase[i] == filter.filter(urls[i]));
    }
  }

  public void testModeAcceptAndNonPathFilter() {
    filter.setModeAccept(true);
    filter.setFilterFromPath(false);
    for (int i = 0; i < urls.length; i++) {
      assertTrue(urlsModeAcceptAndNonPathFilter[i] == filter.filter(urls[i]));
    }
  }

  public void testModeAcceptAndPathFilter() {
    filter.setModeAccept(true);
    filter.setFilterFromPath(true);
    for (int i = 0; i < urls.length; i++) {
      assertTrue(urlsModeAcceptAndPathFilter[i] == filter.filter(urls[i]));
    }
  }

}
