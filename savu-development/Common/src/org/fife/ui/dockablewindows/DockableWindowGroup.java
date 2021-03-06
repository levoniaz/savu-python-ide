/*
 * 10/21/2005
 *
 * DockableWindowGroup.java - A panel containing a bunch of dockable windows
 * in a tabbed pane.
 * Copyright (C) 2005 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.ui.dockablewindows;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;

import org.fife.ui.SubstanceUtils;
import org.fife.ui.UIUtil;
import org.fife.ui.dockablewindows.DockableWindowPanel.ContentPanel;


/**
 * A panel containing a bunch of <code>DockableWindow</code>s contained
 * in a tabbed pane.  Instances of this class contain all docked windows on
 * any edge of a <code>DockableWindowPanel</code>.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class DockableWindowGroup extends JPanel {

	private static final long serialVersionUID = 1L;
	public static final Color C_OUTPUT_AREA_BACKGROUND_COLOR = new Color(50, 50, 40);
	public static final Color C_BORDER_AREA_BACKGROUND_COLOR = new Color(20, 20, 10);
	public static final Color C_ACCENT_BACKGROUND_COLOR = new Color(70, 70, 60);
	public static final Color C_MAIN_BACKGROUND_COLOR = new Color(30, 30, 20);
	private ContentPanel parent;
	private JTabbedPane tabbedPane;
	private TitlePanel titlePanel;


	/**
	 * Constructor.
	 */
	public DockableWindowGroup(ContentPanel parent) {
		this.parent = parent;
		setLayout(new BorderLayout());
		//COLORCHANGE - border around main container
		setBorder(BorderFactory.createLineBorder(C_BORDER_AREA_BACKGROUND_COLOR, 2));
		tabbedPane = new DockedTabbedPane();
		tabbedPane.addChangeListener(titlePanel);
	}
	public TitlePanel getTitlePanel() {
		return titlePanel;
	}
	

	/**
	 * Adds the specified dockable window to the tabbed pane.
	 *
	 * @param window The dockable window to add.
	 * @return <code>true</code> always.
	 */

	public boolean addDockableWindow(DockableWindow window) {
		
		add(window);
		titlePanel = new TitlePanel(window.getDockableWindowName());
		add(titlePanel, BorderLayout.NORTH);
		// Setting selected index causes flicker in editor caret.
		//tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
		// Force title panel to update as sometimes JTabbedPane doesn't
		// fire stateChanged() events (if active index doesn't change).
		//titlePanel.stateChanged(null);
		return true;
	}


	/**
	 * Returns whether or not the specified dockable window is contained in
	 * this <code>DWindPanel</code>.
	 *
	 * @param window The dockable window to look for.
	 * @return The index in the tabbed pane of the dockable window, or
	 *         <code>-1</code> if it is not in this panel.
	 */
	public int containsDockableWindow(DockableWindow window) {
		int count = tabbedPane.getTabCount();
		for (int i=0; i<count; i++) {
			Component c = tabbedPane.getComponentAt(i);
			if (c==window) {
				return i;
			}
		}
		return -1; // Doesn't contain the specified dockable window.
	}


	public void focusActiveDockableWindow() {
		DockableWindow dwind = getDockableWindowAt(tabbedPane.getSelectedIndex());
		dwind.focused();
	}


	/**
	 * Returns the number of dockable windows contained in this tabbed
	 * pane.
	 *
	 * @return The dockable window count in this tabbed pane.
	 */
	public int getDockableWindowCount() {
		return tabbedPane.getTabCount();
	}


	/**
	 * Returns an array containing the dockable windows in this tabbed pane.
	 *
	 * @return The dockable windows.
	 */
	public DockableWindow[] getDockableWindows() {
		int count = tabbedPane.getTabCount();
		DockableWindow[] windows = new DockableWindow[count];
		for (int i=0; i<count; i++) {
			windows[i] = (DockableWindow)tabbedPane.getComponentAt(i);
		}
		return windows;
	}


	/**
	 * Returns the dockable window at the specified index.
	 *
	 * @param index The index.
	 * @return The dockable window.
	 */
	public DockableWindow getDockableWindowAt(int index) {
		return (DockableWindow)tabbedPane.getComponentAt(index);
	}
	


	/**
	 * Refreshes the name of the specified dockable window tab.  This will
	 * also refresh the text in the title bar (since a dockable window's title
	 * defaults to its name if none is specified) if necessary.
	 *
	 * @param index The index of the dockable window to refresh.
	 * @see #refreshTabTitle(int)
	 */
	public void refreshTabName(int index) {
		if (index>=0 && index<tabbedPane.getTabCount()) {
			DockableWindow w = (DockableWindow)tabbedPane.getComponentAt(index);
			String name = w.getDockableWindowName();
			tabbedPane.setTitleAt(index, name);
			refreshTabTitle(index);
		}
	}


	/**
	 * Refreshes a dockable window tab's title.  All this does is check whether
	 * a dockable window is the selected one, and if it is, ensures that the
	 * title in the title pane is displaying the dockable window's current
	 * title.
	 *
	 * @param index The index of the dockable window to refresh.
	 * @see #refreshTabName(int)
	 */
	public void refreshTabTitle(int index) {
		if (index==tabbedPane.getSelectedIndex()) {
			DockableWindow w = (DockableWindow)tabbedPane.getComponentAt(index);
			String title = w.getDockableWindowTitle();
			titlePanel.setTitle(title);
		}
	}


	/**
	 * Removes the specified dockable window from this tabbed pane.
	 *
	 * @param window The dockable window.
	 * @return Whether or not the window was successfully removed.
	 */
	public boolean removeDockableWindow(DockableWindow window) {
		int index = containsDockableWindow(window);
		if (index>-1) {
			tabbedPane.removeTabAt(index);
			//window.setActive(false);
			// Force title panel to update as sometimes JTabbedPane doesn't
			// fire stateChanged() events (if active index doesn't change).
			titlePanel.stateChanged(null);
			return true;
		}
		return false;
	}


	/**
	 * Sets the selected dockable window.  Does nothing if the index is invalid.
	 *
	 * @param index The dockable window to select.
	 */
	public void setActiveDockableWindow(int index) {
		if (index>=0 && index<tabbedPane.getTabCount()) {
			tabbedPane.setSelectedIndex(index);
		}
	}


	/**
	 * The tabbed pane use to switch between multiple grouped docked windows.
	 */
	public class DockedTabbedPane extends JTabbedPane {

		private JPopupMenu popup;

		public DockedTabbedPane() {
			super(BOTTOM);
			enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		}

		private JPopupMenu getPopupMenu() {
			if (popup==null) {
				DockableWindowPanel dwindPanel= parent.getDockableWindowPanel();
				popup = Actions.createRedockPopupMenu(dwindPanel);
			}
			return popup;
		}

		protected void paintComponent(Graphics g) {
			// As of Java 6, still no way for custom LaF's to pick up on
			// Swing's (i.e. the OS's) default AA settings without
			// subclassing components.
			Graphics2D g2d = (Graphics2D)g;
			Map old = UIUtil.setNativeRenderingHints(g2d);
			super.paintComponent(g);
			g2d.setRenderingHints(old);
		}

		protected void processMouseEvent(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				if (e.isPopupTrigger()) {
					int x = e.getX();
					int y = e.getY();
					int index = indexAtLocation(x, y);
					if (index!=-1) {
						setSelectedIndex(index);
						DockableWindow dwind = (DockableWindow)getSelectedComponent();
						putClientProperty("DockableWindow", dwind);
						JPopupMenu popup = getPopupMenu();
						popup.show(this, x, y);
					}
				}
			}
			else {
				super.processMouseEvent(e);
			}
		}

		public void setSelectedIndex(int index) {
			super.setSelectedIndex(index);
			final DockableWindow dwind = (DockableWindow)getSelectedComponent();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					dwind.focused();
				}
			});
		}

		public void setUI(TabbedPaneUI ui) {
			// Keep using tabbed pane ui so laf stays the same, but need to
			// set a new one to pick up new tabbed pane colors, fonts, etc.
			super.setUI(new DockedWindowTabbedPaneUI());
		}

	}


	/**
	 * Panel that displays the currently-active dockable windows' title.
	 */
	public class TitlePanel extends JPanel implements ChangeListener {

		public JLabel label;
		private JButton minimizeButton;
		private Color gradient1;
		private Color gradient2;

		public TitlePanel(String title) {
			super(new BorderLayout());
			label = new JLabel(" " + title);
			Font newLabelFont=new Font(label.getFont().getName(),Font.BOLD,label.getFont().getSize()); 
			label.setFont(newLabelFont);
			add(label);
			label.setForeground(Color.WHITE);
			//COLORCHANGE - foreground change works, background doesn't.
			//minimizeButton = new JButton(new MinimizeAction());
			//minimizeButton.setOpaque(false);
			//JToolBar tb = new JToolBar();
			//tb.setFloatable(false);
			//tb.setOpaque(false);
			//tb.setBorder(null);
			//Removed for Savu
			//tb.add(minimizeButton);
			//add(tb, BorderLayout.LINE_END);


		}

		/**
		 * Performs a gentler "darker" operation than Color.darker().
		 *
		 * @param c
		 * @return
		 */
		public Color darker(Color c) {
			final double FACTOR = 0.85;
			return new Color(Math.max((int)(c.getRed()  *FACTOR), 0), 
					Math.max((int)(c.getGreen()*FACTOR), 0),
					Math.max((int)(c.getBlue() *FACTOR), 0));
		}

		public Dimension getMinimumSize() {
			// So we don't keep "dockable window" panels from getting small.
			Dimension d = getPreferredSize();
			d.width = 32;
			return d;
		}

		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			d.height = 20;
			return d;
		}

		private boolean getUseCustomColors() {
			String laf = UIManager.getLookAndFeel().getClass().getName();
			return laf.endsWith("WindowsLookAndFeel") ||
					laf.endsWith("MetalLookAndFeel");
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;
			//COLORCHANGE FOR PLUGIN TITLE PANEL
			GradientPaint paint = new GradientPaint(
												0,0, new Color(30,170,79),
												0,getHeight(),new Color(0,120,29));
			Paint oldPaint = g2d.getPaint();
			g2d.setPaint(paint);
			Rectangle bounds = getBounds();
			g2d.fillRect(0,0, bounds.width,bounds.height);
			g2d.setPaint(oldPaint);
g2d.setColor(DockableWindowGroup.C_OUTPUT_AREA_BACKGROUND_COLOR);
g2d.drawLine(0,0, bounds.width-1,0);
g2d.drawLine(0,0, 0,bounds.height-1);
g2d.drawLine(bounds.width-1,0, bounds.width-1,bounds.height-1);
g2d.drawLine(0,bounds.height-1, bounds.width-1,bounds.height-1);
		}

		public void setTitle(String title) {
			label.setText(title);
		}

		public void stateChanged(ChangeEvent e) {
			int index = tabbedPane.getSelectedIndex();
			if (index>-1) {
				DockableWindow w = getDockableWindowAt(index);
				setTitle(w.getDockableWindowTitle());
			}
		}

		private void refreshGradientColors() {
			gradient1 = gradient2 = null;
			if (getUseCustomColors()) {
				gradient1 = new Color(225,233,241);//200,200,255);
				gradient2 = new Color(153,180,209);//40,93,220);
			}
			else if (SubstanceUtils.isSubstanceInstalled()) {
				try {
					gradient1 = SubstanceUtils.getSubstanceColor("ultraLightColor");
					gradient2 = SubstanceUtils.getSubstanceColor("lightColor");
				} catch (RuntimeException re) { // FindBugs
					throw re;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (gradient1==null) {
				gradient1 = UIManager.getColor("TextField.selectionBackground");
				if (gradient1==null) {
					gradient1 = UIManager.getColor("textHighlight");
					if (gradient1==null) {
						gradient1 = new Color(153,180,209);
					}
				}
				gradient2 = darker(gradient1);
			}
		}

		private void refreshLabelForeground() {
			Color c = null;
			if (getUseCustomColors()) {
				// Unfortunately we must force a reset of the Label's
				// foreground, even though its updateUI() should have done so,
				// since we had to install a non-ColorUIResource to get a
				// color change for Nimbus.
				c = UIManager.getColor("Label.foreground");
			}
			else if (SubstanceUtils.isSubstanceInstalled()) {
				c = UIManager.getColor("Label.foreground");
//				try {
//					c = SubstanceUtils.getSubstanceColor("selectionForegroundColor");
//				} catch (RuntimeException re) { // FindBugs
//					throw re;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			}
			if (c==null) {
				c = UIManager.getColor("TextField.selectionForeground");
				if (c==null) {
					c = UIManager.getColor("nimbusSelectedText"); // Nimbus!!!
					if (c==null) {
						c = UIManager.getColor("textHighlightText");
						if (c==null) {
							c = Color.black;
						}
					}
				}
			}
			// Nimbus ignores ColorUIResources (!), but honors Colors, so
			// unfortunately we must ensure we have a true "Color" here.
			c = new Color(c.getRed(), c.getGreen(), c.getBlue());
			label.setForeground(c);
		}

		public void updateUI() {
			super.updateUI();
			if (label!=null) {
				label.updateUI();
			}
			refreshGradientColors();
			if (label!=null) {
				refreshLabelForeground();
			}
		}

		private class MinimizeAction extends AbstractAction {

			public MinimizeAction() {
				putValue(SHORT_DESCRIPTION, // tool tip
						DockableWindow.getString("Button.Minimize"));
				Icon icon = new ImageIcon(getClass().getResource("minimize.png"));
				putValue(SMALL_ICON, icon);
			}

			public void actionPerformed(ActionEvent e) {
				parent.setCollapsed(true);
			}

		}

	}


}