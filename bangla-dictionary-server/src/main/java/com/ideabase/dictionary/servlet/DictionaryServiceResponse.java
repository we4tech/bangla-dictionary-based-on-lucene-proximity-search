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

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Dictionary service response
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class DictionaryServiceResponse {
  private boolean mState;
  private List<String> mWords;
  private final Map<String, String> mElementsMap = new HashMap<String, String>();

  public boolean isSuccess() {
    return mState;
  }

  public void setSuccess(final boolean pState) {
    mState = pState;
  }

  public List<String> getWords() {
    return mWords;
  }

  public void setWords(final List<String> pWords) {
    mWords = pWords;
  }

  public DictionaryServiceResponse addElement(final String pElementName,
                                              final String pElementValue) {
    mElementsMap.put(pElementName, pElementValue);
    return this;
  }

  public DictionaryServiceResponse removeElement(final String pElementName) {
    mElementsMap.remove(pElementName);
    return this;
  }

  public Map<String, String> getElements() {
    return mElementsMap;
  }
}
