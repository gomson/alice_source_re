package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.util.Iterator;
import java.util.LinkedList;






















public class FormatTokenizer
{
  protected Iterator tokenIterator;
  
  public FormatTokenizer(String inputString)
  {
    if (inputString != null) {
      LinkedList tokens = new LinkedList();
      while (inputString.length() > 0) {
        if (inputString.startsWith("<<<")) {
          if (inputString.indexOf(">>>") > 0) {
            tokens.add(inputString.substring(0, inputString.indexOf(">>>") + 3));
            inputString = inputString.substring(inputString.indexOf(">>>") + 3);
          } else {
            tokens.add(inputString);
            inputString = "";
          }
        } else if (inputString.startsWith("<<")) {
          if (inputString.indexOf(">>") > 0) {
            tokens.add(inputString.substring(0, inputString.indexOf(">>") + 2));
            inputString = inputString.substring(inputString.indexOf(">>") + 2);
          } else {
            tokens.add(inputString);
            inputString = "";
          }
        } else if (inputString.startsWith("<")) {
          if (inputString.indexOf(">") > 0) {
            tokens.add(inputString.substring(0, inputString.indexOf(">") + 1));
            inputString = inputString.substring(inputString.indexOf(">") + 1);
          } else {
            tokens.add(inputString);
            inputString = "";
          }
        }
        else if (inputString.indexOf('<') > 0) {
          tokens.add(inputString.substring(0, inputString.indexOf('<')));
          inputString = inputString.substring(inputString.indexOf('<'));
        } else {
          tokens.add(inputString);
          inputString = "";
        }
      }
      

      tokenIterator = tokens.iterator();
    }
  }
  
  public boolean hasMoreTokens() {
    if (tokenIterator != null) {
      return tokenIterator.hasNext();
    }
    return false;
  }
  
  public String nextToken()
  {
    if (tokenIterator != null) {
      if (tokenIterator.hasNext()) {
        return (String)tokenIterator.next();
      }
      
      return null;
    }
    
    return null;
  }
}
