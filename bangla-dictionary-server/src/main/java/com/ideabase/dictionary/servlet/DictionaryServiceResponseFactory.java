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

import java.util.Map;
import java.util.HashMap;

/**
 * Dictionary service response factory is used to generate response in
 * many different response format. ie. xml, json etc..
 *
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class DictionaryServiceResponseFactory {

  private final Logger mLogger = LogManager.getLogger(getClass());
  private static final String FORMAT_XML = "xml";
  private static final Map<String, String> MIMETYPE_TABLE =
      new HashMap<String, String>();

  static {
    MIMETYPE_TABLE.put("xml", "text/xml");
    MIMETYPE_TABLE.put("json", "application/json");
  }

  private static final DictionaryServiceResponseFactory INSTANCE =
      new DictionaryServiceResponseFactory();

  private DictionaryServiceResponseFactory() {}

  public static DictionaryServiceResponseFactory getInstance() {
    return INSTANCE;
  }

  public String generateOutput(
      final String pFormat, final DictionaryServiceResponse pResponse) {
    if (FORMAT_XML.equalsIgnoreCase(pFormat)) {
      if (mLogger.isInfoEnabled()) {
        mLogger.debug("Processing response in format - " + pFormat);
      }
      return generateXmlOutput(pFormat, pResponse);
    }
    return null;
  }

  public String getMimeType(final String pFormat) {
    return MIMETYPE_TABLE.get(pFormat);
  }

  private String generateXmlOutput(
      final String pFormat, final DictionaryServiceResponse pResponse) {
    final StringBuilder builder = new StringBuilder();
    builder.append("<Response state='").append(String.valueOf(pResponse.isSuccess())).append("'>");
    if (pResponse.isSuccess() && pResponse.getWords() != null) {
      for (final String word : pResponse.getWords()) {
        builder.append("<word><![CDATA[").append(word).append("]]></word>");
      }
    }

    // generate other elements
    for (final Map.Entry<String, String> entry :
        pResponse.getElements().entrySet()) {
      builder.append("<").append(entry.getKey()).append(">").
          append(entry.getValue()).
          append("</").append(entry.getKey()).append(">");
    }
    builder.append("</Response>");
    return builder.toString();
  }
}
