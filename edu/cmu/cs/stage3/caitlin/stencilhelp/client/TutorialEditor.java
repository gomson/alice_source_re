package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

public class TutorialEditor extends JDialog
{
  private StencilManager stencilManager;
  private JButton showHideTutorialButton;
  private boolean toggleShowTutorial = true;
  private JPanel pagesPanel;
  int pageCount;
  int pageIndex;
  private JScrollPane pagesScrollPane;
  private static String TITLE = "Tutorial Builder";
  
  public TutorialEditor(StencilManager paramStencilManager)
    throws HeadlessException
  {
    super(paramStencilManager != null ? AuthoringTool.getHack().getJAliceFrame() : null, TITLE, false);
    stencilManager = paramStencilManager;
    Container localContainer = getContentPane();
    JPanel localJPanel = new JPanel(new BorderLayout());
    localJPanel.add(createPagePanel(), "North");
    localContainer.add(localJPanel, "North");
    localContainer.add(createMenuPanel(), "Center");
    localContainer.add(createTutorialMenuPanel(), "South");
    setDefaultCloseOperation(0);
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent paramAnonymousWindowEvent)
      {
        stencilManager.setInstructorMode(false);
      }
      
      public void windowStateChanged(WindowEvent paramAnonymousWindowEvent)
      {
        super.windowStateChanged(paramAnonymousWindowEvent);
        pagesPanel.setSize(400, 50);
      }
    });
    setMinimumSize(new Dimension(450, 350));
    pagesPanel.setSize(400, 50);
    setLocation(400, 400);
    setResizable(false);
  }
  
  private Component createPagePanel()
  {
    pagesPanel = new JPanel();
    TitledBorder localTitledBorder = BorderFactory.createTitledBorder("Pages");
    pagesPanel.setBorder(localTitledBorder);
    resetPagesPanel();
    pagesPanel.setSize(new Dimension(440, 50));
    pagesPanel.setMaximumSize(new Dimension(440, 100));
    pagesScrollPane = new JScrollPane(pagesPanel, 21, 32);
    return pagesScrollPane;
  }
  
  private void resetPagesPanel()
  {
    pageIndex = stencilManager.getStencilNumber();
    pageCount = stencilManager.getNumberOfStencils();
    pagesPanel.removeAll();
    for (int i = 0; i < pageCount; i++)
    {
      final int j = i;
      JButton localJButton = makeButton(i + 1, new AbstractAction()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          TutorialEditor.this.onPageClicked(j);
        }
      });
      pagesPanel.add(localJButton);
    }
  }
  
  private void updatePagePanel(int paramInt)
  {
    pageIndex = stencilManager.getStencilNumber();
    pageCount = stencilManager.getNumberOfStencils();
    if (paramInt > 0)
    {
      final int i = pageCount - 1;
      JButton localJButton = makeButton(pageCount, new AbstractAction()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          TutorialEditor.this.onPageClicked(i);
        }
      });
      pagesPanel.add(localJButton);
    }
    else if ((paramInt < 0) && (pageCount > 1))
    {
      pagesPanel.remove(pageCount);
      invalidate();
    }
    else
    {
      resetPagesPanel();
    }
    pagesPanel.validate();
    pagesPanel.repaint();
    pagesScrollPane.validate();
  }
  
  private void onPageClicked(int paramInt)
  {
    stencilManager.gotoStencil(paramInt);
  }
  
  private JPanel createMenuPanel()
  {
    JPanel localJPanel = new JPanel(new GridLayout(2, 1));
    localJPanel.add(createNoteMenuPanel());
    localJPanel.add(createPageMenuPanel());
    return localJPanel;
  }
  
  private static JButton makeButton(String paramString, Action paramAction)
  {
    JButton localJButton = new JButton(paramString);
    localJButton.setAction(paramAction);
    localJButton.setText(paramString);
    return localJButton;
  }
  
  private JPanel createTutorialMenuPanel()
  {
    JButton localJButton1 = makeButton("New tutorial", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        stencilManager.newTutorial();
        stencilManager.setWriteEnabled(true);
      }
    });
    JButton localJButton2 = makeButton("Open tutorial...", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        stencilManager.loadStencilsFile();
        stencilManager.setWriteEnabled(true);
      }
    });
    JButton localJButton3 = makeButton("Save tutorial", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        stencilManager.saveStencilsFile();
      }
    });
    showHideTutorialButton = makeButton(getToggleButtonText(), new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        toggleShowTutorial = (!toggleShowTutorial);
        stencilManager.showStencilPanel(toggleShowTutorial);
      }
    });
    JPanel localJPanel1 = new JPanel(new GridLayout(3, 1));
    TitledBorder localTitledBorder = BorderFactory.createTitledBorder("Tutorial menu");
    localJPanel1.setBorder(localTitledBorder);
    JPanel localJPanel2 = new JPanel();
    localJPanel2.add(localJButton1);
    localJPanel2.add(localJButton2);
    localJPanel2.add(localJButton3);
    JPanel localJPanel3 = new JPanel();
    localJPanel3.add(showHideTutorialButton);
    localJPanel1.add(localJPanel2);
    localJPanel1.add(localJPanel3);
    return localJPanel1;
  }
  
  public void update()
  {
    updatePagePanel(0);
  }
  
  private JPanel createPageMenuPanel()
  {
    JButton localJButton1 = makeButton("New page", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        stencilManager.insertNewStencil(true);
        TutorialEditor.this.updatePagePanel(1);
        TutorialEditor.this.displayNewNoteDialog();
      }
    });
    JButton localJButton2 = makeButton("Remove page", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        stencilManager.removeCurrStencil();
        TutorialEditor.this.updatePagePanel(-1);
      }
    });
    JButton localJButton3 = makeButton("Next", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        stencilManager.showNextStencil();
      }
    });
    JButton localJButton4 = makeButton("Back", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        stencilManager.showPreviousStencil();
      }
    });
    JPanel localJPanel = new JPanel();
    TitledBorder localTitledBorder = BorderFactory.createTitledBorder("Page menu");
    localJPanel.setBorder(localTitledBorder);
    localJPanel.add(localJButton4);
    localJPanel.add(localJButton1);
    localJPanel.add(localJButton2);
    localJPanel.add(localJButton3);
    return localJPanel;
  }
  
  private JPanel createNoteMenuPanel()
  {
    JButton localJButton1 = makeButton("New note...", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        TutorialEditor.this.displayNewNoteDialog();
      }
    });
    JButton localJButton2 = makeButton("Remove last note", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        stencilManager.removeLastStencilObject();
      }
    });
    JPanel localJPanel = new JPanel();
    TitledBorder localTitledBorder = BorderFactory.createTitledBorder("Note menu");
    localJPanel.setBorder(localTitledBorder);
    localJPanel.add(localJButton1);
    localJPanel.add(new JLabel("      "));
    localJPanel.add(localJButton2);
    return localJPanel;
  }
  
  private void displayNewNoteDialog()
  {
    NewNoteDialog localNewNoteDialog = new NewNoteDialog(stencilManager, this);
    localNewNoteDialog.display(true);
  }
  
  private Point getPosition()
  {
    return new Point(getX(), getY());
  }
  
  private String getToggleButtonText()
  {
    return "Show/Hide tutorial";
  }
  
  public static JRadioButton getSelection(ButtonGroup paramButtonGroup)
  {
    Enumeration localEnumeration = paramButtonGroup.getElements();
    while (localEnumeration.hasMoreElements())
    {
      JRadioButton localJRadioButton = (JRadioButton)localEnumeration.nextElement();
      if (localJRadioButton.getModel() == paramButtonGroup.getSelection())
        return localJRadioButton;
    }
    return null;
  }
  
  public void display(boolean paramBoolean)
  {
    validate();
    setVisible(paramBoolean);
  }
}
