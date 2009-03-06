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

/**
 * Build dictionary query.
 * example - <br>
 *  new DictionaryQuery.Builder().match("hello").orMatchPrefix()
 *  .orMatchSuffix().orMatchSimilarity().build();
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class DictionaryQuery {

  private String mWord;
  private boolean mMatchPrefix;
  private boolean mMatchSuffix;
  private boolean mMatchSimilarity;

  public String getWord() {
    return mWord;
  }

  public void setWord(final String pWord) {
    mWord = pWord;
  }

  public boolean isMatchPrefix() {
    return mMatchPrefix;
  }

  public void setMatchPrefix(final boolean pMatchPrefix) {
    mMatchPrefix = pMatchPrefix;
  }

  public boolean isMatchSuffix() {
    return mMatchSuffix;
  }

  public void setMatchSuffix(final boolean pMatchSuffix) {
    mMatchSuffix = pMatchSuffix;
  }

  public boolean isMatchSimilarity() {
    return mMatchSimilarity;
  }

  public void setMatchSimilarity(final boolean pMatchSimilarity) {
    mMatchSimilarity = pMatchSimilarity;
  }

  /**
   * Builder is used for building dictionary query with a fluent interface.
   */
  public static class Builder {

    private final DictionaryQuery mQuery;

    public Builder() {
      mQuery = new DictionaryQuery();
    }

    public Builder match(final String pWord) {
      mQuery.setWord(pWord);
      return this;
    }

    public Builder orMatchPrefix() { mQuery.setMatchPrefix(true); return this; }
    public Builder orMatchSuffix() { mQuery.setMatchSuffix(true); return this; }
    public Builder orMatchSimilarity() { mQuery.setMatchSimilarity(true); return this; }

    public DictionaryQuery build() {
      return mQuery;
    }
  }
}
