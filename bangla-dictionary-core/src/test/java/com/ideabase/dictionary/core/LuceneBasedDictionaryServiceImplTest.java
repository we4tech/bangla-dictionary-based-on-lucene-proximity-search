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

import junit.framework.TestCase;
import com.ideabase.dictionary.core.impl.LuceneBasedDictionaryServiceImpl;
import com.ideabase.dictionary.core.impl.BanglaPhoneticConverterImpl;
import com.ideabase.dictionary.common.DictionaryResult;

import java.io.File;

/**
 * Test {@see LuceneBasedDictionaryServiceImpl}
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class LuceneBasedDictionaryServiceImplTest extends TestCase {

  private DictionaryService mDictionaryService;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    System.setProperty("config.indexDir", "/Users/nhmtanveerhossainkhanhasan/java-tmp/dict-index");
    mDictionaryService = new LuceneBasedDictionaryServiceImpl(new BanglaPhoneticConverterImpl());
  }

  public void testAddWord() throws InterruptedException {
    mDictionaryService.addWord("hello");
    Thread.sleep(5000);
    
    final DictionaryResult result = mDictionaryService.searchWord("hel", 10);
    assertNotNull(result);
    assertEquals("hello", result.getSuggestedWords().toArray(new String[] {})[0]);

    System.out.println(result.getProbableMatch());
    System.out.println(result.getSuggestedWords());
  }

  public void testConvert() {
    final String bangla = "সকালে উঠিয়া আমি ইয়ামিয়ান, যায়";
    final StringBuilder builder = new StringBuilder();
    for (final String word : bangla.split("\\s")) {
      builder.append(mDictionaryService.phoneticSpell(word)).append(" ");
    }
    System.out.println("Bangla - " + bangla);
    System.out.println("Phonetic - " + builder.toString());
  }

  public void testImportData() throws InterruptedException {
    mDictionaryService.importData(new File("data/dictionary.out.txt"));
    assertTrue(true);
    Thread.sleep(100000);
  }

  public void testSuggestWord() {
    final String bangla = "কাজ";
    final DictionaryResult result = mDictionaryService.searchWord(bangla, 10);

    assertNotNull(result);
    assertNotNull(result.getSuggestedWords());
    System.out.println(result.getSuggestedWords());
  }
}
