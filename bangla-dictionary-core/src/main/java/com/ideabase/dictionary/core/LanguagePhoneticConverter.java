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

/**
 * Convert any word into the phonetic symbol.
 * ie. মানুষ is spelled as "manush"
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public interface LanguagePhoneticConverter {

  /**
   * return the implementated language
   * @return implemented language ie. EN_UK, BN_BD
   */
  String getLanguage();

  /**
   * return the phonetic spell of the given word
   * @param pWord word which needs to spell in phonetic
   * @return new word based on phonetic spell
   */
  String convert(final String pWord);
}
