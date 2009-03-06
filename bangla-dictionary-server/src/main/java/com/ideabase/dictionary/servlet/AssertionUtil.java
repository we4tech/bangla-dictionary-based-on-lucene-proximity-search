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

import java.util.Map;
import java.util.List;

/**
 * Verify specified value
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class AssertionUtil {

  public static void assertNotNull(final Object pValue, final String pMessage) {
    if (pValue == null) {
      throwExecption(pMessage);
    } else {
      final boolean emptyString =
          (pValue instanceof String) && ((String) pValue).length() == 0;
      final boolean emptyMap =
          pValue instanceof Map && ((Map) pValue).isEmpty();
      final boolean emptyList =
          pValue instanceof List && ((List) pValue).isEmpty();
      if (emptyString || emptyMap || emptyList) {
        throwExecption(pMessage);
      }

    }
  }

  private static void throwExecption(final String pMessage) {
    throw new IllegalArgumentException(pMessage);
  }
}
