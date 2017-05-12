package edu.cmu.cs.stage3.alice.authoringtool.datatransfer;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;


















public class CommonMathQuestionsTransferable
  implements Transferable
{
  public static DataFlavor commonMathQuestionsFlavor = AuthoringToolResources.getReferenceFlavorForClass(CommonMathQuestionsTransferable.class);
  protected DataFlavor myFlavor;
  protected DataFlavor[] flavors;
  
  public CommonMathQuestionsTransferable()
  {
    try {
      myFlavor = AuthoringToolResources.getReferenceFlavorForClass(CommonMathQuestionsTransferable.class);
      myFlavor.setHumanPresentableName("commonMathQuestionsTransferable");
    } catch (Exception e) {
      AuthoringTool.showErrorDialog(e.getMessage(), e);
    }
    
    flavors = new DataFlavor[2];
    flavors[0] = myFlavor;
    flavors[1] = DataFlavor.stringFlavor;
  }
  
  public DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }
  
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    for (int i = 0; i < flavors.length; i++) {
      if (flavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }
  
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (flavor.getRepresentationClass().equals(CommonMathQuestionsTransferable.class))
      return this;
    if (flavor.equals(DataFlavor.stringFlavor)) {
      return toString();
    }
    throw new UnsupportedFlavorException(flavor);
  }
}
