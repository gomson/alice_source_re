package edu.cmu.cs.stage3.io;

import edu.cmu.cs.stage3.lang.Messages;
import java.io.PrintStream;






















public class TokenBlock
{
  public String tokenName = null;
  public String tokenArgs = null;
  public String tokenContents = null;
  public int tokenEndIndex = 0;
  
  public TokenBlock() {}
  
  public TokenBlock(String tokenName, String tokenContents, int tokenEndIndex) { this.tokenName = tokenName;
    this.tokenContents = tokenContents;
    this.tokenEndIndex = tokenEndIndex;
    tokenArgs = new String();
  }
  
  public TokenBlock(String tokenName, String tokenArgs, String tokenContents, int tokenEndIndex) {
    this.tokenName = tokenName;
    this.tokenContents = tokenContents;
    this.tokenEndIndex = tokenEndIndex;
    this.tokenArgs = tokenArgs;
  }
  
  public static TokenBlock getTokenBlock(int beginIndex, String content) {
    int openTokenStart = content.indexOf('<', beginIndex);
    if (openTokenStart == -1) {
      return new TokenBlock();
    }
    int openTokenStop = content.indexOf('>', openTokenStart);
    if (openTokenStop == -1) {
      System.out.println(Messages.getString("___found_with_no_closing_____"));
      return null;
    }
    
    String tokenName = content.substring(openTokenStart + 1, openTokenStop).trim();
    String tokenArgs = new String();
    
    int whiteSpace = tokenName.indexOf(" ");
    if (whiteSpace != -1) {
      tokenArgs = tokenName.substring(whiteSpace + 1, tokenName.length()).trim();
      tokenName = tokenName.substring(0, whiteSpace);
    }
    int closeTokenStart = content.indexOf("</" + tokenName + ">", openTokenStop);
    if (closeTokenStart == -1) {
      System.out.println(Messages.getString("No_closing_token____") + tokenName + Messages.getString("___found_"));
      return null;
    }
    
    String blockContents = content.substring(openTokenStop + 1, closeTokenStart);
    int endIndex = closeTokenStart + tokenName.length() + 3;
    
    return new TokenBlock(tokenName, tokenArgs, blockContents, endIndex);
  }
}
