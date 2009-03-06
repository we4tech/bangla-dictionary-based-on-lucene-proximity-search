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

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.Hits;
import org.apache.lucene.document.Document;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Manage {@see IndexReader} and {@see IndexWriter}
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class LuceneIndexManager {

  private final Logger mLogger = LogManager.getLogger(getClass());
  private final FSDirectory mIndexDirectory;
  private IndexReader mIndexReader;
  private IndexWriter mIndexWriter;
  private IndexSearcher mIndexSearcher;
  private boolean mIndexChanged;

  public LuceneIndexManager(final File pIndexDirectory) {
    try {
      mIndexDirectory = FSDirectory.getDirectory(pIndexDirectory);
    } catch (IOException e) {
      throw new RuntimeException(
          "failed to construct LuceneIndexManager(" + pIndexDirectory + ")", e);
    }
    mLogger.debug("Constructing LuceneIndexManager(" + pIndexDirectory + ")");
    initiateIndexReader();
    initiateIndexWriter();
  }

  private void initiateIndexWriter() {
    mLogger.info("Initiating Index Writer");
    try {
      checkLock();
      // remove lock if exists
      mIndexWriter = new IndexWriter(
          mIndexDirectory, new StandardAnalyzer(), false);
      mIndexWriter.setUseCompoundFile(true);
    } catch (IOException e) {
      throw new RuntimeException("Failed to initiate index writer", e);
    }
  }

  private void checkLock() throws IOException {
    if (IndexReader.isLocked(mIndexDirectory)) {
      mLogger.info("Existing lock found, now cleaning up lock.");
      IndexReader.unlock(mIndexDirectory);
    }
  }

  private void initiateIndexReader() {
    mLogger.info("Initiating Index Reader");
    try {
      mIndexSearcher = new IndexSearcher(mIndexDirectory);
      if (mLogger.isDebugEnabled()) {
        mLogger.debug("Max doc - " + mIndexSearcher.maxDoc());
      }
    } catch(FileNotFoundException fne) {
      mLogger.warn("File not found");
    } catch (IOException e) {
      throw new RuntimeException("Failed to initiate index reader for " + mIndexDirectory, e);
    }
  }


  /**
   * Add new document to the index
   * @param pDocument lucene document
   * @throws IOException if failed to index the given document
   */
  public void addDocument(final Document pDocument) throws IOException {
    mLogger.info("Adding new document to the index.");
    if (mLogger.isDebugEnabled()) {
      mLogger.debug("DOCUMENT - " + pDocument);
    }
    synchronized (mIndexWriter) {
      mIndexWriter.addDocument(pDocument);
      mIndexChanged = true;
    }
  }

  /**
   * Optimize lucene index
   * @throws IOException if failed to optimize lucene index
   */
  public void optimize() throws IOException {
    mLogger.info("Optimizing index.");
    synchronized (mIndexWriter) {
      mIndexWriter.optimize();
    }
  }

  /**
   * Perform index search
   * @param pLuceneQuery lucene query
   * @param pSort sort parameter
   * @return {@code Hits} search results
   * @throws IOException if failed to perform search
   */
  public Hits search(final Query pLuceneQuery, final Sort pSort)
      throws IOException {
    return indexSearcher().search(pLuceneQuery, pSort);
  }

  private IndexSearcher indexSearcher() {
    try {
      if (mIndexSearcher != null && mIndexChanged) {
        mLogger.info("Reopening index reader.");
        synchronized (mIndexSearcher.getIndexReader()) {
          mIndexSearcher.getIndexReader().reopen();
          mIndexChanged = false;
        }
      }
      return mIndexSearcher;
    } catch (IOException e) {
      mLogger.warn("Failed to return index searcher", e);
    }
    return mIndexSearcher;
  }

  /**
   * Cleanup all active resources.
   */
  public void cleanup() throws IOException {
    mLogger.info("Cleaning up all active resources.");

    if (mIndexReader != null) {
      mIndexReader.close();
    }
    if (mIndexSearcher != null) {
      mIndexSearcher.close();
    }
    if (mIndexDirectory != null) {
      mIndexDirectory.close();
    }
  }

  public void flush() throws IOException {
    mIndexWriter.flush();
  }
}
