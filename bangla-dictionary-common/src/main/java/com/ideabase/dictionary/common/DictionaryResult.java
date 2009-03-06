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

package com.ideabase.dictionary.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Dictionary result class is used for presenting collected dictionary search
 * result in an abstract object. 
 *
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class DictionaryResult {

  private final List<String> mSuggestedWords;
  private long mProbableMatch = 0;
  private boolean mSuggestionFound = false;

  /**
   * Default constructor.
   * @param pSuggestedWords list of suggested words
   * @param pProbableMatch number of suggested word found
   */
  public DictionaryResult(final List<String> pSuggestedWords,
                          final long pProbableMatch) {
    mSuggestedWords = pSuggestedWords;
    mProbableMatch = pProbableMatch;
    mSuggestionFound = !pSuggestedWords.isEmpty();
  }

  /**
   * Retrieve the list of suggested words
   * @return {@code List<String>} of suggested words
   */
  public List<String> getSuggestedWords() { return mSuggestedWords; }

  /**
   * Retrieve the number of probable suggestion found.
   * @return the number of probable suggestion found.
   */
  public long getProbableMatch() { return mProbableMatch; }

  /**
   * Return {@code true} if suggestion found
   * @return {@code true} if suggestion found.
   */
  public boolean isSuggestionFound() { return mSuggestionFound; }
}
