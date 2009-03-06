/**
 * $Id$
 * *****************************************************************************
 *    Copyright (C) 2005 - 2007 somewhere in .Net ltd.
 *    All Rights Reserved.  No use, copying or distribution of this
 *    work may be made except in accordance with a valid license
 *    agreement from somewhere in .Net LTD.  This notice must be included on
 *    all copies, modifications and derivatives of this work.
 * *****************************************************************************
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 * *****************************************************************************
 */

package com.ideabase.dictionary.servlet;

import org.jmock.MockObjectTestCase;
import org.jmock.Mock;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import nu.xom.*;

/**
 * test {@code DictionaryServiceServlet}
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class DictionaryServiceServletTest extends MockObjectTestCase {
  private final Logger mLogger = LogManager.getLogger(getClass());
  private DictionaryServiceServlet mServlet;
  private static final String SERVICE_URI = "/spellchecker/service";
  private CharArrayWriter mCharArrayWriter;
  private static final String ATTR_STATE = "state";
  private static final String ATTR_MATCH_COUNT = "matchCount";
  private static final String ATTR_WORDS = "words";
  private String mRequestUri = null;
  private String mRequestMtethod = "GET";
  private String mRequestParameterWord;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    initiateDictionaryServiceServlet();
  }

  private void initiateDictionaryServiceServlet() throws ServletException {
    // set initial configuration
    System.setProperty("config.indexDir",
          "/Users/nhmtanveerhossainkhanhasan/java-tmp/dict-index");

    // initiate servlet
    mServlet = new DictionaryServiceServlet();
    mServlet.init(mockServletConfig());
  }

  private ServletConfig mockServletConfig() {
    final Mock mock = mock(ServletConfig.class);

    // set expectation
    mock.stubs()
        .method("getServletName").withAnyArguments()
        .will(returnValue(mServlet.getClass().getName()));

    mock.stubs()
        .method("getServletContext").withNoArguments()
        .will(returnValue(mockServletContext()));
    return (ServletConfig) mock.proxy();
  }

  private ServletContext mockServletContext() {
    final Mock mock = mock(ServletContext.class);

    // set expectation

    return (ServletContext) mock.proxy();
  }

  // TEST - search
  public Map<String, Object> testServiceSearch()
      throws IOException, ServletException, ParsingException {
    mLogger.debug("Service search");

    mRequestUri = "/search.xml";
    mRequestMtethod = "GET";
    final HttpServletRequest request = mockServletRequest();
    final HttpServletResponse response = mockServletResponse(); 
    mServlet.service(request, response);

    final String xmlResponse = mCharArrayWriter.toString();
    mLogger.debug("Response - " + xmlResponse);

    // parse xml and find state = 'true'
    final Map<String, Object> result = parse(xmlResponse);

    // assert result
    assertNotNull(result);
    assertEquals(result.get(ATTR_STATE), Boolean.TRUE);
    assertNotNull(result.get(ATTR_WORDS));
    assertNotNull(result.get(ATTR_MATCH_COUNT));

    mLogger.debug(result);

    return result;
  }

  // TEST - add
  public void testServiceAddWord()
      throws IOException, ServletException,
             ParsingException, InterruptedException {
    mLogger.debug("Service add word");

    mRequestUri = "/add.xml";
    mRequestMtethod = "GET";
    mRequestParameterWord = "কাসেমাসে";
    final HttpServletRequest request = mockServletRequest();
    final HttpServletResponse response = mockServletResponse();
    mServlet.service(request, response);

    final String xmlResponse = mCharArrayWriter.toString();
    mLogger.debug("Xml response - " + xmlResponse);
    // parse xml response
    final Map<String, Object> result = parse(xmlResponse);

    assertNotNull(result);
    assertEquals(result.get(ATTR_STATE), true);
    Thread.sleep(2000);

    // perform search
    final Map<String, Object> searchResult = testServiceSearch();
    final List<String> words = (List<String>) searchResult.get(ATTR_WORDS);

    boolean found = false;
    final String phoneticWord =
        mServlet.getDictionaryService().phoneticSpell(mRequestParameterWord);
    for (final String word : words) {
      found = mServlet.getDictionaryService().
          phoneticSpell(word).equals(phoneticWord);
      if (found) {
        break;
      }
    }
    assertTrue(found);
  }

  private Map<String, Object> parse(final String pXmlResponse)
      throws ParsingException, IOException {
    final Map<String, Object> map = new HashMap<String, Object>();

    final Builder builder = new Builder();
    final Document document =
        builder.build(new ByteArrayInputStream(pXmlResponse.getBytes()));

    // find state
    final Element rootElement = document.getRootElement();
    map.put(ATTR_STATE,
        Boolean.valueOf(rootElement.getAttributeValue(ATTR_STATE)));

    // find word elements
    final Elements wordElements = rootElement.getChildElements("word");
    if (wordElements != null) {
      final List<String> words = new ArrayList<String>();
      for (int i = 0; i < wordElements.size(); i++) {
        final Element wordElement = wordElements.get(i);
        words.add(wordElement.getValue());
      }
      map.put(ATTR_WORDS, words);
    }

    // find total match count
    final Element matchCountElement =
        rootElement.getFirstChildElement("matchCount");
    if (matchCountElement != null) {
      map.put(ATTR_MATCH_COUNT, Integer.parseInt(matchCountElement.getValue()));
    }
    return map;
  }

  private HttpServletResponse mockServletResponse() {
    final Mock mock = mock(HttpServletResponse.class);

    // set expectation
    mock.stubs()
        .method("setCharacterEncoding")
        .with(stringContains("UTF8"));

    mock.stubs()
        .method("setContentType")
        .with(stringContains("text/xml"));

    mock.stubs()
        .method("getWriter")
        .withNoArguments()
        .will(returnValue(new PrintWriter(mCharArrayWriter = new CharArrayWriter())));
    
    return (HttpServletResponse) mock.proxy();
  }

  private HttpServletRequest mockServletRequest() {
    final Mock mock = mock(HttpServletRequest.class);

    // set expectation
    mock.stubs()
        .method("getRequestURI")
        .withNoArguments()
        .will(returnValue(SERVICE_URI + mRequestUri));

    if (mRequestParameterWord == null) {
      mRequestParameterWord = "জাম";
    }
    mock.stubs()
        .method("getParameter")
        .with(stringContains("word"))
        .will(returnValue(mRequestParameterWord));

    mock.stubs()
        .method("getParameter")
        .with(stringContains("max"))
        .will(returnValue("10"));

    mock.stubs()
        .method("getMethod")
        .withNoArguments()
        .will(returnValue(mRequestMtethod));

    return (HttpServletRequest) mock.proxy();
  }

}
