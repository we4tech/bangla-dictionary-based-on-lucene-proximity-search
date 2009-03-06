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

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import com.ideabase.dictionary.core.DictionaryService;
import com.ideabase.dictionary.core.LanguagePhoneticConverter;
import com.ideabase.dictionary.core.impl.LuceneBasedDictionaryServiceImpl;
import com.ideabase.dictionary.core.impl.BanglaPhoneticConverterImpl;
import com.ideabase.dictionary.common.DictionaryResult;
import static com.ideabase.dictionary.servlet.AssertionUtil.assertNotNull;

/**
 * This servlet is used to interface with {@code DictionaryService}
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class DictionaryServiceServlet extends HttpServlet {

  private final Logger mLogger = LogManager.getLogger(getClass());
  private static final String TOKEN_SEARCH = "search";
  private static final String TOKEN_ADD = "add";
  private static final String PARAM_WORD = "word";

  // initiate dictionary services.
  private final static LanguagePhoneticConverter PHONETIC_CONVERTER =
      new BanglaPhoneticConverterImpl();
  private final static DictionaryService DICTIONARY_SERVICE =
      new LuceneBasedDictionaryServiceImpl(PHONETIC_CONVERTER);
  private static final String ELEMENT_MATCH_COUNT = "matchCount";
  private static final String DEFAULT_FORMAT = "xml";
  private static final String PARAM_MAX = "max";
  private static final int MAX_NUMBER_OF_SUGGESTED_WORDS = 10;

  @Override
  protected void service(final HttpServletRequest pRequest,
                         final HttpServletResponse pResponse)
      throws ServletException, IOException {
    if (mLogger.isDebugEnabled()) {
      mLogger.info("Serving service on request method - " +
                   pRequest.getMethod());
    }
    final String requestUri = pRequest.getRequestURI();
    // handle service based on uri
    // sample request /dict/search?word=hello
    if (requestUri == null || requestUri.length() == 0) {
      throw new RuntimeException("Service execution failed.");
    } else {
      mLogger.debug("Request URI - " + requestUri);
      final String[] parts = requestUri.split("/");
      String serviceRequestToken = parts[parts.length - 1].toLowerCase();

      // determine requested response format
      String serviceResponseFormat = DEFAULT_FORMAT;
      if (serviceRequestToken.indexOf(".") != -1) {
        final String[] tokenParts = serviceRequestToken.split("\\.");
        serviceResponseFormat = tokenParts[1];
        serviceRequestToken = tokenParts[0];
        mLogger.debug("URI - " + serviceRequestToken + " - " + serviceResponseFormat);
      }

      // handle service request
      final DictionaryServiceResponse response = handleDictionaryServiceRequest(
          serviceRequestToken, pRequest);

      // prepare service response
      prepareDictionaryServiceResponse(
          serviceResponseFormat, response, pResponse);
    }
  }

  private void prepareDictionaryServiceResponse(
      final String pFormat, final DictionaryServiceResponse pResponse,
      final HttpServletResponse pServletResponse) throws IOException {
    if (mLogger.isDebugEnabled()) {
      mLogger.debug("preparing dictionary service response - " + pResponse);
    }
    pServletResponse.setCharacterEncoding("UTF8");
    pServletResponse.setContentType(DictionaryServiceResponseFactory.
        getInstance().getMimeType(pFormat));
    pServletResponse.getWriter().print(DictionaryServiceResponseFactory.
        getInstance().generateOutput(pFormat, pResponse));
  }

  private DictionaryServiceResponse handleDictionaryServiceRequest(
      final String pServiceToken, final HttpServletRequest pServletRequest) {
    if (mLogger.isDebugEnabled()) {
      mLogger.debug("processing dictionary service request - " +
                    pServiceToken);
    }
    final DictionaryServiceResponse response = new DictionaryServiceResponse();

    // /service/search.xml
    if (TOKEN_SEARCH.equalsIgnoreCase(pServiceToken)) {
      // find search word
      final String word = pServletRequest.getParameter(PARAM_WORD);
      assertNotNull(word, "parameter 'word' is required.");

      // find maximum number of suggested words
      final String maxSuggestedWordsString = pServletRequest.getParameter(PARAM_MAX);
      int maxSuggestedWords = MAX_NUMBER_OF_SUGGESTED_WORDS;
      if (maxSuggestedWordsString != null) {
        maxSuggestedWords = Integer.parseInt(maxSuggestedWordsString);
      }
      performSearchOperation(word, maxSuggestedWords, response);
    }

    // /service/add.xml
    else if (TOKEN_ADD.equalsIgnoreCase(pServiceToken)) {
      // find search word
      final String word = pServletRequest.getParameter(PARAM_WORD);
      assertNotNull(word, "parameter 'word' is required.");

      // perform add new word action
      performAddNewWord(word, response);
    }
    return response;
  }

  private void performAddNewWord(final String pWord,
                                 final DictionaryServiceResponse pResponse) {
    if (mLogger.isDebugEnabled()) {
      mLogger.debug("Adding new word - " + pWord);
    }
    try {
      DICTIONARY_SERVICE.addWord(pWord);
      pResponse.setSuccess(true);
      if (mLogger.isInfoEnabled()) {
        mLogger.info("Successfully added new word - " + pWord);
      }
    } catch(Exception e) {
      mLogger.warn("failed to add new word(" + pWord + ")", e);
      pResponse.setSuccess(false);
    }
  }

  private void performSearchOperation(
      final String pWord, final int pMaxSuggestedWords,
      final DictionaryServiceResponse pResponse) {

    if (mLogger.isDebugEnabled()) {
      mLogger.debug("Search for finding right spell of word - " + pWord +
                    " return only "+ pMaxSuggestedWords + " words.");
    }
    // perform search operation
    final DictionaryResult result =
        DICTIONARY_SERVICE.searchWord(pWord, pMaxSuggestedWords);
    if (result.getProbableMatch() > 0) {
      pResponse.setSuccess(true);
      pResponse.setWords(result.getSuggestedWords());
      pResponse.addElement(ELEMENT_MATCH_COUNT,
          String.valueOf(result.getProbableMatch()));
    }
  }

  /**
   * Return instance of {@code DictionaryService}
   * @return instance of {@code DictionaryService}
   */
  public DictionaryService getDictionaryService() {
    return DICTIONARY_SERVICE;
  }
}
