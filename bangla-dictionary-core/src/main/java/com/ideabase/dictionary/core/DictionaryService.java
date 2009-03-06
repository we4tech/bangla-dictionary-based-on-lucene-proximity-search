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

package com.ideabase.dictionary.core;

import com.ideabase.dictionary.common.DictionaryResult;
import com.ideabase.dictionary.common.DictionaryQuery;

import java.io.File;

/**
 * Dictionary service is the service level exposed API.
 * this service used for accessing all required functionalities.
 * all external communicator such as web service server will use this API.
 * this API is used for exposing internal data and searchig structure.
 *
 * this API is also used for provsioning event and enhanching through
 * exposed event and plugin architecture.
 *
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public interface DictionaryService {

  /**
   * Add new word to the dictionary
   * @param pWord language term. ie. hello
   */
  void addWord(final String pWord);

  /**
   * Remove specific word from the dictionary.
   * @param pWord language term, ie, hello
   */
  void removeWord(final String pWord);

  /**
   * Search word in dictionary.
   * @param pWord look up for word in dictionary
   * @param pMax maximum number of suggested words
   * @return {@code DictionaryResult} is returned
   */
  DictionaryResult searchWord(final String pWord, final int pMax);

  /**
   * Respell the given word in the form of how it pronouce.
   * @param pWord ie. hello world, provide single word.
   * @return new spell for the given word.
   */
  String phoneticSpell(final String pWord);

  /**
   * Import words from the dictionary.txt to the data directory.
   * @param pFile dictionary data file
   */
  void importData(final File pFile);
}
