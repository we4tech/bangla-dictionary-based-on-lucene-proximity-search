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

import com.ideabase.dictionary.core.DictionaryService;
import com.ideabase.dictionary.core.LanguagePhoneticConverter;
import com.ideabase.dictionary.common.DictionaryResult;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;

import java.io.*;
import java.util.concurrent.*;
import java.util.*;

/**
 * This implementation of {@see DictionaryService} is based on apache lucene
 * IR library.
 *
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class LuceneBasedDictionaryServiceImpl implements DictionaryService {

  private static final String PROPERTY_INDEX_DIR = "config.indexDir";
  private static final String FIELD_WORD = "word";
  private static final String FIELD_BANGLA_WORD = "bangla";

  private final Logger mLogger = LogManager.getLogger(getClass());
  private File mIndexDirectory;
  private LuceneIndexManager mIndexManager;
  private final ExecutorService mExecutorService =
      Executors.newSingleThreadExecutor();
  private final ScheduledExecutorService mScheduledExecutorService =
      Executors.newScheduledThreadPool(1);
  private final DictionaryResult mEmptyDictionaryResult =
      new DictionaryResult(Collections.EMPTY_LIST, 0);
  private ScheduledFuture mOptimizerScheduleFuture = null;
  private final LanguagePhoneticConverter mPhoneticConverter;


  /**
   * Default constructor
   */
  public LuceneBasedDictionaryServiceImpl(
      final LanguagePhoneticConverter pConverter) {
    mLogger.info("Constructing " + getClass().getName());
    mPhoneticConverter = pConverter;
    configure();
  }

  private void configure() {
    findLuceneIndexDirectory();
    initiateLuceneIndexManager();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          mIndexManager.cleanup();
        } catch (IOException e) {
          mLogger.warn("Failed to clean up index resource", e);
          throw new RuntimeException("Failed to close index", e);
        }
      }
    });
  }

  private void initiateLuceneIndexManager() {
    mLogger.info("Initiating lucene index manager.");
    mIndexManager = new LuceneIndexManager(mIndexDirectory);
  }

  private void findLuceneIndexDirectory() {
    final String indexDir = System.getProperty(PROPERTY_INDEX_DIR);
    if (indexDir == null) {
      throw new RuntimeException("index directory is not mentioned, " +
          "please use -D" + PROPERTY_INDEX_DIR + "=/path/to/index/dir");
    }
    mIndexDirectory = new File(indexDir);
    if (!mIndexDirectory.exists()) {
      mLogger.info("Index directory wasn't created before. now creating.");
      mIndexDirectory.mkdirs();
      mLogger.info("Index directory " + mIndexDirectory + " is created.");
    }
    mLogger.info("Index directory - " + mIndexDirectory);
  }

  public void addWord(final String pWord) {
    mExecutorService.execute(new Runnable() {
      public void run() {
        if (mLogger.isInfoEnabled()) {
          mLogger.info("Adding new word " + pWord);
        }
        try {
          final Document document = new Document();
          document.add(new Field(FIELD_WORD, phoneticSpell(pWord),
              Field.Store.NO, Field.Index.UN_TOKENIZED));
          document.add(new Field(FIELD_BANGLA_WORD, pWord,
              Field.Store.YES, Field.Index.UN_TOKENIZED));
          mIndexManager.addDocument(document);

          // add optimization request
          if (mOptimizerScheduleFuture == null) {
            mScheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
              public void run() {
                try {
                  mIndexManager.optimize();
                } catch (IOException e) {
                  mLogger.warn("Failed to optimize index", e);
                }
              }
            }, 60 * 60, 60 * 60, TimeUnit.SECONDS);
          }
        } catch (IOException e) {
          mLogger.warn("Failed to index content", e);
        }
      }
    });
  }

  public void removeWord(final String pWord) {
    throw new RuntimeException("THis method is not yet implemented");
  }

  public DictionaryResult searchWord(final String pWord, final int pMax) {
    try {
      final String phoneticSpell = phoneticSpell(pWord);
      if (mLogger.isDebugEnabled()) {
        mLogger.debug("Searching word - " + pWord + " : " + phoneticSpell);
      }
      final Query luceneQuery = buildLuceneQuery(phoneticSpell);
      if (mLogger.isDebugEnabled()) {
        mLogger.debug("Query - " + luceneQuery);
      }
      final Hits hits = mIndexManager.search(luceneQuery, Sort.RELEVANCE);
      if (mLogger.isDebugEnabled()) {
        mLogger.debug("HITS - " + hits);
      }
      int searchHitCount = hits.length();

      final List<String> suggestedWords = new ArrayList<String>();
      int count = 0;
      for (final Iterator iterator = hits.iterator(); iterator.hasNext(); ) {
        count++;
        final Hit hit = (Hit) iterator.next();
        suggestedWords.add(hit.get(FIELD_BANGLA_WORD));
        if (count == pMax) { break; }
        System.out.println("SCORE - " + hit.getScore());
      }
      return new DictionaryResult(suggestedWords, searchHitCount);
    } catch (Exception e) {
      mLogger.warn("Failed to perform search for query - " +
                   pWord, e);
    }
    return mEmptyDictionaryResult;
  }

  public String phoneticSpell(final String pWord) {
    if (mLogger.isDebugEnabled()) {
      mLogger.debug("phoneticSpell(" + pWord + ")");
    }
    return mPhoneticConverter.convert(pWord);
  }

  public void importData(final File pFile) {
    mLogger.info("Importing data from - " + pFile);
    if (pFile == null || !pFile.exists()) {
      throw new RuntimeException(pFile.toString() + " dictionary file not found.");
    }
    mExecutorService.execute(new Runnable() {
      public void run() {
        try {
          final FileInputStream inputStream = new FileInputStream(pFile);
          final BufferedReader reader =
              new BufferedReader(new InputStreamReader(inputStream));
          String word = null;
          while ((word = reader.readLine()) != null) {
            addWord(word);
          }
          inputStream.close();
          mLogger.info("Optimizing index");
          mIndexManager.flush();
          mIndexManager.optimize();
          mLogger.info("Import task has been completed.");
        } catch (Exception e) {
          mLogger.warn("Failed to import dictionary data", e);
        }
      }
    });
  }

  private Query buildLuceneQuery(final String pWord) throws ParseException {
    return new FuzzyQuery(new Term(FIELD_WORD, pWord));
  }
}
