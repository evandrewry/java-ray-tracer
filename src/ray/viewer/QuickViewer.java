/*
 * Created on Jun 21, 2005
 * Copyright 2005 Program of Computer Grpahics, Cornell University
 */
package ray.viewer;

import javax.swing.JFrame;

import ray.misc.Image;


/**
 * @author arbree
 * Jun 21, 2005
 * QuickViewer.java
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class QuickViewer extends JFrame {
	
	private static final long serialVersionUID = 3257563992804045113L;
	
	/**
	 * Internal panel used to display images
	 */
	protected PanelDisplay imageDisplayPanel;
	
	/**
	 * Constructor sets the close action of this frame
	 */
	public QuickViewer() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * @see javax.swing.JFrame#frameInit()
	 */
	protected void frameInit() {
		
		super.frameInit();
		imageDisplayPanel = new PanelDisplay(this);
		getContentPane().add(imageDisplayPanel);	
	}
	
	/**
	 * Sets the currently viewable image
	 * @param inImage
	 */
	public void setImage(Image inImage) {	
		imageDisplayPanel.setImage(inImage);	
	}
	public void setImage(Image inImage, int xl, int yl, int xh, int yh) {	
		imageDisplayPanel.setImage(inImage, xl, yl, xh, yh);	
	}
	
	/**
	 * Starts the viewing
	 */
	public void view() {
		pack();
		setVisible(true);	
	}
	
	/**
	 * Creates a QuickViewer object for the current image, displays it and returns a reference to it.
	 * @param inImage
	 * @return the QiuckViewer object created
	 */
	public static QuickViewer createImageViewer(Image inImage) {
		
		QuickViewer out = new QuickViewer();
		out.setImage(inImage);
		out.view();
		
		return out;
	}
}
