package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.JAlice;
import edu.cmu.cs.stage3.alice.authoringtool.JAliceFrame;
import edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BasicBehaviorPanel;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor;
import edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ResponseEditor;
import edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter;
import edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Group;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import edu.cmu.cs.stage3.io.FileUtilities;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.GoldenRatio;
import edu.cmu.cs.stage3.progress.ProgressCancelException;
import edu.cmu.cs.stage3.progress.ProgressObserver;
import edu.cmu.cs.stage3.swing.ContentPane;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;











































































public class ExportCodeForPrintingContentPane
  extends ContentPane
{
  private AuthoringTool m_authoringTool;
  private JFileChooser m_pathFileChooser = new JFileChooser();
  
  private JTextField m_authorNameTextField = new JTextField();
  private JTextField m_pathTextField = new JTextField();
  private JPanel m_elementsToBeExportedPanel = new JPanel();
  
  private Vector m_okActionListeners = new Vector();
  private JButton m_exportButton = new JButton(
    Messages.getString("Export_Code"));
  private JButton m_cancelButton = new JButton(
    Messages.getString("Cancel"));
  
  public ExportCodeForPrintingContentPane(AuthoringTool authoringTool)
  {
    m_authoringTool = authoringTool;
    
    ArrayList extensions = new ArrayList();
    extensions
      .add(new ExtensionFileFilter(
      "htm", "*.htm"));
    extensions
      .add(new ExtensionFileFilter(
      "html", "*.html"));
    m_pathFileChooser
      .setFileFilter(new ExtensionGroupFileFilter(
      extensions, Messages.getString("Web_pages")));
    
    Configuration authoringToolConfig = 
      Configuration.getLocalConfiguration(JAlice.class
      .getPackage());
    String path = authoringToolConfig
      .getValue("directories.worldsDirectory");
    if (path != null) {
      final File dir = new File(path);
      if ((dir != null) && (dir.exists()) && (dir.isDirectory())) {
        try
        {
          Runnable testRun = new Runnable() {
            public void run() {
              m_pathFileChooser.setCurrentDirectory(dir);
            }
          };
          if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeAndWait(testRun);
          }
        }
        catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}catch (InterruptedException e)
        {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
    
    m_pathFileChooser.setDialogType(0);
    m_pathFileChooser
      .setFileSelectionMode(0);
    m_pathFileChooser.setApproveButtonText(Messages.getString("Set_File"));
    
    JButton selectAllButton = new JButton(
      Messages.getString("Select_All"));
    selectAllButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ExportCodeForPrintingContentPane.this.setAllSelected(true);
      }
      
    });
    JButton deselectAllButton = new JButton(
      Messages.getString("Deselect_All"));
    deselectAllButton
      .addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e) {
          ExportCodeForPrintingContentPane.this.setAllSelected(false);
        }
        

      });m_authorNameTextField
      .addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e) {
          m_exportButton.doClick();
        }
        
      });
    m_pathTextField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        m_exportButton.doClick();
      }
      
    });
    JButton browseButton = new JButton(
      Messages.getString("Browse___"));
    browseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ExportCodeForPrintingContentPane.this.handleBrowseButton();
      }
      
    });
    m_exportButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ExportCodeForPrintingContentPane.this.handleExportButton();
      }
      
    });
    JScrollPane whatToPrintScrollPane = new JScrollPane(
      m_elementsToBeExportedPanel);
    m_elementsToBeExportedPanel.setLayout(new GridBagLayout());
    m_elementsToBeExportedPanel.setBackground(Color.white);
    





    JPanel selectPanel = new JPanel();
    selectPanel.setLayout(new GridBagLayout());
    GridBagConstraints gbcSelect = new GridBagConstraints();
    anchor = 18;
    fill = 1;
    gridwidth = 0;
    selectPanel.add(selectAllButton, gbcSelect);
    selectPanel.add(deselectAllButton, gbcSelect);
    weighty = 1.0D;
    selectPanel.add(new JLabel(), gbcSelect);
    
    JPanel pathPanel = new JPanel();
    pathPanel.setLayout(new GridBagLayout());
    GridBagConstraints gbcPath = new GridBagConstraints();
    anchor = 18;
    fill = 1;
    gridwidth = -1;
    pathPanel.add(new JLabel(Messages.getString("Export_to_")), 
      gbcPath);
    gridwidth = 0;
    weightx = 1.0D;
    pathPanel.add(m_pathTextField, gbcPath);
    weightx = 0.0D;
    
    JPanel authorPanel = new JPanel();
    authorPanel.setLayout(new GridBagLayout());
    GridBagConstraints gbcAuthor = new GridBagConstraints();
    anchor = 18;
    fill = 1;
    gridwidth = -1;
    authorPanel.add(
      new JLabel(Messages.getString("Author_s_name_")), 
      gbcAuthor);
    gridwidth = 0;
    weightx = 1.0D;
    authorPanel.add(m_authorNameTextField, gbcAuthor);
    weightx = 0.0D;
    
    JPanel okCancelPanel = new JPanel();
    okCancelPanel.setLayout(new GridBagLayout());
    GridBagConstraints gbcOKCancel = new GridBagConstraints();
    insets.left = 8;
    okCancelPanel.add(m_exportButton, gbcOKCancel);
    okCancelPanel.add(m_cancelButton, gbcOKCancel);
    
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    anchor = 18;
    fill = 1;
    insets.left = 8;
    insets.top = 8;
    insets.right = 8;
    
    gridwidth = -1;
    add(new JLabel(Messages.getString("What_to_export_")), gbc);
    gridwidth = 0;
    add(new JLabel(), gbc);
    
    insets.top = 0;
    weighty = 1.0D;
    gridwidth = -1;
    weightx = 1.0D;
    add(whatToPrintScrollPane, gbc);
    weightx = 0.0D;
    gridwidth = 0;
    add(selectPanel, gbc);
    weighty = 0.0D;
    
    insets.top = 8;
    
    gridwidth = -1;
    add(pathPanel, gbc);
    gridwidth = 0;
    add(browseButton, gbc);
    
    gridwidth = -1;
    add(authorPanel, gbc);
    gridwidth = 0;
    add(new JLabel(), gbc);
    
    insets.bottom = 8;
    gridwidth = 0;
    add(okCancelPanel, gbc);
    
    int height = 400;
    int width = 
      GoldenRatio.getLongerSideLength(height);
    setPreferredSize(new Dimension(width, height));
  }
  
  public void preDialogShow(JDialog dialog) {
    super.preDialogShow(dialog);
    initialize("");
  }
  
  public void postDialogShow(JDialog dialog) {
    super.postDialogShow(dialog);
  }
  
  public String getTitle() {
    return Messages.getString("Export_to_HTML___");
  }
  
  private void fireOKActionListeners() {
    ActionEvent e = new ActionEvent(this, 
      1001, 
      Messages.getString("OK"));
    for (int i = 0; i < m_okActionListeners.size(); i++) {
      ActionListener l = 
        (ActionListener)m_okActionListeners.elementAt(i);
      l.actionPerformed(e);
    }
  }
  
  public void addOKActionListener(ActionListener l) {
    m_okActionListeners.addElement(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    m_okActionListeners.removeElement(l);
  }
  
  public void addCancelActionListener(ActionListener l) {
    m_cancelButton.addActionListener(l);
  }
  
  public void removeCancelActionListener(ActionListener l) {
    m_cancelButton.removeActionListener(l);
  }
  
  public void initialize(String authorName) {
    setAllSelected(true);
    m_authorNameTextField.setText(authorName);
    m_authorNameTextField.setName(authorName);
    

    Configuration authoringToolConfig = 
      Configuration.getLocalConfiguration(JAlice.class
      .getPackage());
    File file = new File(
      authoringToolConfig.getValue("directories.worldsDirectory"), 
      getWorldName(m_authoringTool.getCurrentWorldLocation()) + 
      ".html");
    m_pathTextField.setText(file.getAbsolutePath());
    
    World world = m_authoringTool.getWorld();
    Vector objectsToEdit = new Vector();
    addObjectsToEdit(world, objectsToEdit);
    if (world != null) {
      for (int i = 0; i < sandboxes.size(); i++) {
        addObjectsToEdit(
          (Sandbox)sandboxes
          .get(i), 
          objectsToEdit);
      }
      for (int i = 0; i < groups.size(); i++) {
        Group o = (Group)groups.get(i);
        for (int j = 0; j < o.size(); j++) {
          addObjectsToEdit(
            (Sandbox)o.getChildAt(j), 
            objectsToEdit);
        }
      }
    }
    buildWhatToPrintPanel(objectsToEdit);
  }
  
  public File getFileToExportTo() {
    String path = m_pathTextField.getText();
    return new File(path);
  }
  
  private void setAllSelected(boolean isSelected) {
    for (int i = 0; i < m_elementsToBeExportedPanel.getComponentCount(); i++) {
      Component componentI = m_elementsToBeExportedPanel
        .getComponent(i);
      if ((componentI instanceof CustomCheckBox)) {
        ((CustomCheckBox)componentI).setSelected(isSelected);
      }
    }
  }
  
  private void addObjectsToEdit(Sandbox sandbox, Vector toAddTo)
  {
    if ((sandbox != null) && (
      (behaviors.size() != 0) || 
      (responses.size() != 0) || 
      (questions.size() != 0))) {
      toAddTo.add(name.getStringValue());
      for (int i = behaviors.size() - 1; i >= 0; i--) {
        toAddTo.add(behaviors.get(i));
      }
      for (int i = 0; i < responses.size(); i++) {
        toAddTo.add(responses.get(i));
      }
      for (int i = 0; i < questions.size(); i++) {
        toAddTo.add(questions.get(i));
      }
    }
  }
  
  protected void buildWhatToPrintPanel(Vector objectsToPrint) {
    m_elementsToBeExportedPanel.removeAll();
    CustomListButton currentButton = null;
    int count = 0;
    boolean isWorld = false;
    for (int i = 0; i < objectsToPrint.size(); i++) {
      Object currentObject = objectsToPrint.elementAt(i);
      JComponent toAdd = null;
      int leftIndent = 0;
      if ((currentObject instanceof String)) {
        currentButton = new CustomListButton();
        currentButton.setText(currentObject.toString());
        toAdd = currentButton;
        leftIndent = 0;
        if (currentButton.getText().equalsIgnoreCase("world")) {
          isWorld = true;
        } else {
          isWorld = false;
        }
      }
      else {
        toAdd = new CustomCheckBox();
        if (currentButton != null) {
          currentButton.addCheckBox((CustomCheckBox)toAdd);
        }
        if ((currentObject instanceof Behavior)) {
          JComponent gui = 
            GUIFactory.getGUI(currentObject);
          image = m_authoringTool
            .getJAliceFrame().getImageForComponent(gui);
          gui = gui;
          object = currentObject;
          toAdd.setOpaque(false);
        } else if ((currentObject instanceof UserDefinedResponse)) {
          CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = new CallToUserDefinedResponsePrototype(
            (UserDefinedResponse)currentObject);
          JComponent gui = 
            GUIFactory.getGUI(callToUserDefinedResponsePrototype);
          image = m_authoringTool
            .getJAliceFrame().getImageForComponent(gui);
          gui = gui;
          object = currentObject;
          toAdd.setOpaque(false);
        } else if ((currentObject instanceof UserDefinedQuestion)) {
          CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype = new CallToUserDefinedQuestionPrototype(
            (UserDefinedQuestion)currentObject);
          JComponent gui = 
            GUIFactory.getGUI(callToUserDefinedQuestionPrototype);
          image = m_authoringTool
            .getJAliceFrame().getImageForComponent(gui);
          gui = gui;
          object = currentObject;
          toAdd.setOpaque(false);
        }
        if (isWorld) {
          ((CustomCheckBox)toAdd).setSelected(true);
        } else {
          ((CustomCheckBox)toAdd).setSelected(false);
        }
        ((CustomCheckBox)toAdd).setIndex(i);
      }
      Color bgColor = Color.white;
      if ((currentObject instanceof Response)) {
        bgColor = 
          AuthoringToolResources.getColor("userDefinedResponseEditor");
      } else if ((currentObject instanceof UserDefinedQuestion)) {
        bgColor = 
          AuthoringToolResources.getColor("userDefinedQuestionEditor");
      } else if ((currentObject instanceof BasicBehaviorPanel)) {
        bgColor = 
          AuthoringToolResources.getColor("behavior");
      }
      

      toAdd.setBackground(bgColor);
      m_elementsToBeExportedPanel.add(toAdd, new GridBagConstraints(0, i, 
        1, 1, 0.0D, 0.0D, 17, 
        2, new Insets(0, 
        leftIndent, 0, 0), 0, 0));
      count++;
    }
    m_elementsToBeExportedPanel.add(Box.createVerticalGlue(), 
      new GridBagConstraints(0, count, 1, 1, 1.0D, 1.0D, 
      10, 1, 
      new Insets(0, 0, 0, 0), 0, 0));
    m_elementsToBeExportedPanel.revalidate();
    m_elementsToBeExportedPanel.repaint();
  }
  

  private void handleBrowseButton()
  {
    int result = DialogManager.showDialog(
      m_pathFileChooser, null);
    if (result == 0) {
      File file = m_pathFileChooser.getSelectedFile();
      if (file != null) {
        String path = file.getAbsolutePath();
        if ((!path.endsWith(".html")) && (!path.endsWith(".htm")))
        {

          path = path + ".html";
        }
        m_pathTextField.setText(path);
      }
    }
  }
  
  private void handleWriteProblem(File file) {
    DialogManager.showMessageDialog(
    
      Messages.getString("Cannot_write_to___") + file.getAbsolutePath() + "\"", 
      Messages.getString("Cannot_write"), 
      0);
  }
  
  private void handleExportButton() {
    if (m_authorNameTextField.getText().length() == 0)
    {
      DialogManager.showMessageDialog(
        Messages.getString("You_must_enter_the_author_s_name_before_proceeding_"), 
        Messages.getString("You_have_not_entered_the_author_s_name"), 
        0);
    } else {
      File file = getFileToExportTo();
      if (file.exists()) {
        int result = 
          DialogManager.showConfirmDialog(
          Messages.getString("You_are_about_to_save_over_an_existing_file__Are_you_sure_you_want_to_"), 
          Messages.getString("Save_Over_Warning"), 
          0);
        if (result == 0) {}

      }
      else
      {
        try
        {
          file.createNewFile();
        } catch (Throwable t) {
          handleWriteProblem(file);
          return;
        }
      }
      if (file.canWrite()) {
        fireOKActionListeners();
      } else {
        handleWriteProblem(file);
      }
    }
  }
  
  private static String getWorldName(File worldFile) {
    if (worldFile != null) {
      return FileUtilities.getBaseName(worldFile);
    }
    return Messages.getString("Unnamed_World");
  }
  
  private JComponent getComponentForObject(Object toFind)
  {
    for (int i = 0; i < m_elementsToBeExportedPanel.getComponentCount(); i++) {
      Component c = m_elementsToBeExportedPanel.getComponent(i);
      if (((c instanceof CustomCheckBox)) && 
        (object == toFind)) {
        return gui;
      }
    }
    
    return null;
  }
  

  public void getHTML(StringBuffer buffer, File worldFile, boolean addHeaderAndFooter, boolean addAuthor, ProgressObserver progressObserver)
    throws ProgressCancelException
  {
    if (progressObserver != null) {
      progressObserver.progressBegin(m_elementsToBeExportedPanel
        .getComponentCount());
    }
    
    String worldName = getWorldName(worldFile);
    if (addHeaderAndFooter) {
      buffer.append("<html>\n<head>\n<title>" + worldName + 
        "'s Code</title>\n</head>\n<body>\n");
    }
    
    buffer.append("<h1>" + worldName + "'s Code</h1>\n");
    if (addAuthor) {
      buffer.append("<h1> Created by: " + m_authorNameTextField.getText() + 
        "</h1>\n");
    }
    boolean notOnBehaviorsYet = true;
    boolean notOnResponsesYet = true;
    boolean notOnQuestionsYet = true;
    String currentTitle = "";
    boolean anyItemsYet = false;
    for (int i = 0; i < m_elementsToBeExportedPanel.getComponentCount(); i++) {
      if ((m_elementsToBeExportedPanel.getComponent(i) instanceof CustomListButton)) {
        String name = ((CustomListButton)m_elementsToBeExportedPanel
          .getComponent(i)).getText();
        currentTitle = "<h2>" + name + "</h2>\n";
        anyItemsYet = false;
        notOnBehaviorsYet = true;
        notOnResponsesYet = true;
        notOnQuestionsYet = true;
      } else if ((m_elementsToBeExportedPanel.getComponent(i) instanceof CustomCheckBox)) {
        CustomCheckBox currentBox = (CustomCheckBox)m_elementsToBeExportedPanel
          .getComponent(i);
        if (currentBox.isSelected()) {
          if (!anyItemsYet) {
            buffer.append(currentTitle);
            anyItemsYet = true;
          }
          if (!(object instanceof Behavior)) {
            Component currentEditor = m_authoringTool
              .getEditorForElement((Element)object);
            if ((currentEditor instanceof CompositeElementEditor)) {
              if ((currentEditor instanceof ResponseEditor)) {
                if (notOnResponsesYet) {
                  buffer.append("<h3>Methods</h3>\n");
                  notOnResponsesYet = false;
                }
              }
              else if (notOnQuestionsYet) {
                String cappedQuestionString = AuthoringToolResources.QUESTION_STRING
                  .substring(0, 1).toUpperCase() + 
                  AuthoringToolResources.QUESTION_STRING
                  .substring(1);
                buffer.append("<h3>" + cappedQuestionString + 
                  "s</h3>\n");
                notOnQuestionsYet = false;
              }
              
              buffer.append("<table cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">\n");
              ((CompositeElementEditor)currentEditor)
                .getHTML(buffer, true);
              buffer.append("\n</table>\n<br>\n<br>\n");
            }
          } else {
            if (notOnBehaviorsYet) {
              buffer.append("<h3>Events</h3>\n");
              notOnBehaviorsYet = false;
            }
            JComponent component = getComponentForObject(object);
            if ((component instanceof BasicBehaviorPanel)) {
              BasicBehaviorPanel behaviorPanel = (BasicBehaviorPanel)component;
              buffer.append("<table  style=\"border-left: 1px solid #c0c0c0; border-top: 1px solid #c0c0c0; border-bottom: 1px solid #c0c0c0; border-right: 1px solid #c0c0c0\" cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">\n");
              behaviorPanel.getHTML(buffer, true);
              buffer.append("\n</table>\n<br>\n<br>\n");
            }
          }
        }
      }
      if (progressObserver != null) {
        progressObserver.progressUpdate(i, null);
      }
    }
    if (addHeaderAndFooter) {
      buffer.append("</body>\n</html>\n");
    }
    if (progressObserver != null) {
      progressObserver.progressEnd();
    }
  }
}
