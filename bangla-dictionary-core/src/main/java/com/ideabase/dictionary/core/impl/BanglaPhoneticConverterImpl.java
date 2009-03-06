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

package com.ideabase.dictionary.core.impl;

import com.ideabase.dictionary.core.LanguagePhoneticConverter;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * Convert bangla word in phonetic form.
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class BanglaPhoneticConverterImpl implements LanguagePhoneticConverter {

  private final Logger mLogger = LogManager.getLogger(getClass());
  private final static String LANG = "bn";
  private final static Map<Character, String> PHONETIC_SPELL_MAP =
      new HashMap<Character, String>();

  static {
    // bangla support
    PHONETIC_SPELL_MAP.put('ঐ', "oi");
    PHONETIC_SPELL_MAP.put('ঠ', "th");
    PHONETIC_SPELL_MAP.put('র', "r");
    PHONETIC_SPELL_MAP.put('ী', "ii");
    PHONETIC_SPELL_MAP.put('ড', "d");
    PHONETIC_SPELL_MAP.put('ু', "u");
    PHONETIC_SPELL_MAP.put('ূ', "uu");
    PHONETIC_SPELL_MAP.put('ঢ', "dh");
    PHONETIC_SPELL_MAP.put('ল', "l");
    PHONETIC_SPELL_MAP.put('ও', "o");
    PHONETIC_SPELL_MAP.put('ণ', "n");
    PHONETIC_SPELL_MAP.put('ঔ', "ou");
    PHONETIC_SPELL_MAP.put('ত', "t");
    PHONETIC_SPELL_MAP.put('অ', "ao");
    PHONETIC_SPELL_MAP.put('আ', "a");
    PHONETIC_SPELL_MAP.put('ক', "k");
    PHONETIC_SPELL_MAP.put('থ', "th");
    PHONETIC_SPELL_MAP.put('খ', "kh");
    PHONETIC_SPELL_MAP.put('দ', "d");
    PHONETIC_SPELL_MAP.put('শ', "sh");
    PHONETIC_SPELL_MAP.put('ই', "i");
    PHONETIC_SPELL_MAP.put('গ', "g");
    PHONETIC_SPELL_MAP.put('ধ', "dh");
    PHONETIC_SPELL_MAP.put('ষ', "s");
    PHONETIC_SPELL_MAP.put('ে', "e");
    PHONETIC_SPELL_MAP.put('ঈ', "ii");
    PHONETIC_SPELL_MAP.put('ঘ', "gh");
    PHONETIC_SPELL_MAP.put('ন', "n");
    PHONETIC_SPELL_MAP.put('স', "sh");
    PHONETIC_SPELL_MAP.put('ৈ', "oi");
    PHONETIC_SPELL_MAP.put('উ', "u");
    PHONETIC_SPELL_MAP.put('ঙ', "ng");
    PHONETIC_SPELL_MAP.put('হ', "h");
    PHONETIC_SPELL_MAP.put('ঊ', "u");
    PHONETIC_SPELL_MAP.put('চ', "ch");
    PHONETIC_SPELL_MAP.put('প', "p");
    PHONETIC_SPELL_MAP.put('ছ', "ch");
    PHONETIC_SPELL_MAP.put('ফ', "ph");
    PHONETIC_SPELL_MAP.put('জ', "j");
    PHONETIC_SPELL_MAP.put('ব', "bo");
    PHONETIC_SPELL_MAP.put('ড়', "r");
    PHONETIC_SPELL_MAP.put('ঢ়', "rh");
    PHONETIC_SPELL_MAP.put('ঝ', "j");
    PHONETIC_SPELL_MAP.put('ভ', "b");
    PHONETIC_SPELL_MAP.put('হ', "h");
    PHONETIC_SPELL_MAP.put('ঞ', "ng");
    PHONETIC_SPELL_MAP.put('ম', "m");
    PHONETIC_SPELL_MAP.put('া', "a");
    PHONETIC_SPELL_MAP.put('এ', "e");
    PHONETIC_SPELL_MAP.put('ট', "t");
    PHONETIC_SPELL_MAP.put('য', "z");
    PHONETIC_SPELL_MAP.put('ি', "i");
    PHONETIC_SPELL_MAP.put('য়', "y");
    PHONETIC_SPELL_MAP.put('ঋ', "wr");

    // TODO: english support
  }

  public String getLanguage() {
    return LANG;
  }

  public String convert(final String pWord) {
    final StringBuilder builder = new StringBuilder();
    // read unicode character
    // iterate for each unicode character
    for (final char pChar : pWord.toCharArray()) {
      // lookup phonetic spell
      // merge them in string
      final String spell = getPhoneticSpell(pChar);
      if (spell != null) {
        builder.append(spell);
      }
    }
    // return the converted word
    return builder.toString();
  }

  private String getPhoneticSpell(final char pChar) {
    final String sound = PHONETIC_SPELL_MAP.get(pChar);
    if (mLogger.isDebugEnabled()) {
      mLogger.debug("char - " + pChar + ":" + sound);
    }
    return sound;
  }
}
