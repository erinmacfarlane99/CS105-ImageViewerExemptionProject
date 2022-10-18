import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * ImageViewer is the main class of the image viewer application. It builds and
 * displays the application GUI and initialises all other components.
 * 
 * To start the application, create an object of this class.
 * 
 * @author Michael Kölling and David J. Barnes.
 * @version 3.1
 */ 
public class ImageViewer 
{
	//static fields:
	private static final String VERSION = "Version 3.1";
	private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

	// fields:
	private JFrame frame;
	private ImagePanel imagePanel;
	private JLabel filenameLabel;
	private JLabel statusLabel;
	private JButton smallerButton;
	private JButton largerButton;
	private OFImage currentImage;

	//added fields
	private JButton undoButton;
	private JButton redoButton;
	private JButton reloadButton;
	private JScrollPane scrollPane;
	private JButton rotateLeft;
	private JButton rotateRight;
	private int time;
	private int counter;

	private List<Filter> filters;
	private ArrayList<OFImage> undoArraylist;
	private ArrayList<OFImage> redoArraylist;
	private ArrayList<OFImage> slideshowPhotos;

	/**
	 * Create an ImageViewer and display its GUI on screen.
	 */
	public ImageViewer()
	{
		currentImage = null;
		undoArraylist = new ArrayList<>();
		redoArraylist = new ArrayList<>();
		slideshowPhotos = new ArrayList<>();
		filters = createFilters();
		makeFrame();
	}

	// ---- implementation of menu functions ----

	/**
	 * Open function: open a file chooser to select a new image file,
	 * and then display the chosen image.
	 */
	private void openFile()
	{
		int returnVal = fileChooser.showOpenDialog(frame);

		if(returnVal != JFileChooser.APPROVE_OPTION) {
			return;  // cancelled
		}
		File selectedFile = fileChooser.getSelectedFile();
		currentImage = ImageFileManager.loadImage(selectedFile);

		if(currentImage == null) {   // image file was not a valid image
			JOptionPane.showMessageDialog(frame,
					"The file was not in a recognized image file format.",
					"Image Load Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		imagePanel.setImage(currentImage);
		setButtonsEnabled(true);
		showFilename(selectedFile.getPath());
		showStatus("File loaded.");
		frame.pack();
	}

	/**
	 * Close function: close the current image.
	 */
	private void close()
	{
		currentImage = null;
		imagePanel.clearImage();
		showFilename(null);
		setButtonsEnabled(false);
	}
	
	/**
	 * Save As function: save the current image to a file.
	 */
	private void saveAs()
	{
		if(currentImage != null) {
			int returnVal = fileChooser.showSaveDialog(frame);

			if(returnVal != JFileChooser.APPROVE_OPTION) {
				return;  // cancelled
			}
			File selectedFile = fileChooser.getSelectedFile();
			ImageFileManager.saveImage(currentImage, selectedFile);

			showFilename(selectedFile.getPath());
		}
	}

	/**
	 * Quit function: quit the application.
	 */
	private void quit()
	{
		System.exit(0);
	}    

	/**
	 * Apply a given filter to the current image.
	 * 
	 * @param filter   The filter object to be applied.
	 */
	private void applyFilter(Filter filter)
	{
		if(currentImage != null) {
			undoArraylist.add(currentImage);
			int width = currentImage.getWidth();
	        int height = currentImage.getHeight();
	        OFImage newImage = new OFImage(width, height);

	        for(int x = 0; x < width; x++) {
	            for(int y = 0; y < height; y++) {
	                newImage.setRGB(x, y, currentImage.getRGB(x, y));
	            }
	        }
	        currentImage = newImage;
	        filter.apply(currentImage);
	        imagePanel.setImage(currentImage);
			frame.pack();
			showStatus("Applied: " + filter.getName());
			setUndoButton(true);
		}
		else {
			showStatus("No image loaded.");
		}
	}

	/**
	 * 'About' function: show the 'about' box.
	 */
	private void showAbout()
	{
		JOptionPane.showMessageDialog(frame, 
				"ImageViewer\n" + VERSION,
				"About ImageViewer", 
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Make the current picture larger.
	 */
	private void makeLarger()
	{
		if(currentImage != null) {
			undoArraylist.add(currentImage);
			// create new image with double size
			int width = currentImage.getWidth();
			int height = currentImage.getHeight();
			OFImage newImage = new OFImage(width * 2, height * 2);

			// copy pixel data into new image
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					Color col = currentImage.getPixel(x, y);
					newImage.setPixel(x * 2, y * 2, col);
					newImage.setPixel(x * 2 + 1, y * 2, col);
					newImage.setPixel(x * 2, y * 2 + 1, col);
					newImage.setPixel(x * 2+1, y * 2 + 1, col);
				}
			}

			currentImage = newImage;
			imagePanel.setImage(currentImage);
			frame.pack();
			setUndoButton(true);
		}
	}    


	/**
	 * Make the current picture smaller.
	 */
	private void makeSmaller()
	{	
		if(currentImage != null) {
			undoArraylist.add(currentImage);
			// create new image with double size
			int width = currentImage.getWidth() / 2;
			int height = currentImage.getHeight() / 2;
			OFImage newImage = new OFImage(width, height);

			// copy pixel data into new image
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					newImage.setPixel(x, y, currentImage.getPixel(x * 2, y * 2));
				}
			}

			currentImage = newImage;
			imagePanel.setImage(currentImage);
			frame.pack();
			setUndoButton(true);
		}
	}

	/**
	 * RotateLeft: rotate the image left by 90 degrees.
	 */
	private void rotateLeft() {
		undoArraylist.add(currentImage);

		int height = currentImage.getHeight();
		int width = currentImage.getWidth();
		OFImage rotatedImg = new OFImage(height, width);

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				rotatedImg.setPixel(y, width - x - 1, currentImage.getPixel(x, y));
			}
		}
		currentImage = rotatedImg;
		showStatus("Rotated image 90 degrees left");
		imagePanel.setImage(currentImage);
		frame.pack();
		setUndoButton(true);	
	}

	/**
	 * RotateRight: rotate the image right by 90 degrees.
	 */
	private void rotateRight() {
		undoArraylist.add(currentImage);

		int height = currentImage.getHeight();
		int width = currentImage.getWidth();
		OFImage rotatedImg = new OFImage(height, width);

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				rotatedImg.setPixel(height - y - 1, x, currentImage.getPixel(x, y));
			}
		}
		showStatus("Rotated image 90 degrees right");
		currentImage = rotatedImg;
		imagePanel.setImage(currentImage);
		frame.pack();
		setUndoButton(true);
	}

	/**
	 * Undo: undo an action that has been applied to image.
	 */
	private void undo()
	{
		redoArraylist.add(currentImage); //Adds image before undo to re-do array list
		currentImage = undoArraylist.get(undoArraylist.size() - 1); //Sets current image to last element of undo array list
		undoArraylist.remove(undoArraylist.size() - 1); //Removes last element of array list
		imagePanel.setImage(currentImage);
		statusLabel.setText("Feature un-done");
		frame.pack();

		if(undoArraylist.size() == 0) {
    			setUndoButton(false);
		}
		setRedoButton(true); //Make re-do button visible
	}

	/** 
	 * Redo: re-do the action that has been undone.
	 */	
	private void redo()
	{	
		undoArraylist.add(currentImage); //Add current image to undo array list 
		currentImage = redoArraylist.get(redoArraylist.size() - 1); //make current image equal to last image in re-do
		redoArraylist.remove(redoArraylist.size() - 1); //remove last image from re-do
		imagePanel.setImage(currentImage); 
		statusLabel.setText("Feature re-done");
		frame.pack();
		
		if(redoArraylist.size() == 0) {
			setRedoButton(false);
		}

		setUndoButton(true);
	}
	
	/**
	 * Reload: reload the original version of the image being displayed.
	 */
	
	private void reload() {
		File selectedFile = fileChooser.getSelectedFile();
		currentImage = ImageFileManager.loadImage(selectedFile);
		imagePanel.setImage(currentImage);
		statusLabel.setText("Reloaded");
		frame.pack();
		setUndoButton(false);
		setRedoButton(false);
	}

	// ---- support methods ----

	/**
	 * Show the file name of the current image in the file display label.
	 * 'null' may be used as a parameter if no file is currently loaded.
	 * 
	 * @param filename  The file name to be displayed, or null for 'no file'.
	 */
	
	public void showFilename(String filename)
	{
		if(filename == null) {
			filenameLabel.setText("No file displayed.");
		}
		else {
			filenameLabel.setText("File: " + filename);
		}
	}

	/**
	 * Show a message in the status bar at the bottom of the screen.
	 * 
	 * @param text The status message.
	 */
	public void showStatus(String text)
	{
		statusLabel.setText(text);
	}

	/**
	 * Enable or disable all toolbar buttons.
	 * 
	 * @param status  'true' to enable the buttons, 'false' to disable.
	 */
	private void setButtonsEnabled(boolean status)
	{
		smallerButton.setEnabled(status);
		largerButton.setEnabled(status);
		reloadButton.setEnabled(status);
		rotateLeft.setEnabled(status);   
		rotateRight.setEnabled(status);  
	}

	/**
	 * Enable or disable undo button.
	 * 
	 * @param status 'true' to enable the button, 'false' to disable.
	 */
	private void setUndoButton(boolean status) 
	{	
		undoButton.setEnabled(status);
	}

	/**
	 * Enable or disable re-do button.
	 * 
	 * @param status 'true' to enable the button, 'false' to disable.
	 */
	private void setRedoButton(boolean status) 
	{	
		redoButton.setEnabled(status);
	}
	
	/**
	 * Fills array list with photos to be displayed in a slide show.
	 */
	public void fillArraylist()
    {
		int returnVal = fileChooser.showOpenDialog(frame);

		if(returnVal != JFileChooser.APPROVE_OPTION) {
			return;  // cancelled
		}
		File selectedFile = fileChooser.getSelectedFile();
		currentImage = ImageFileManager.loadImage(selectedFile);
		slideshowPhotos.add(currentImage); //Add photo to array list of images
		
		if(currentImage == null) {   // image file was not a valid image
			JOptionPane.showMessageDialog(frame,
					"The file was not in a recognized image file format.",
					"Image Load Error",
					JOptionPane.ERROR_MESSAGE);
			return; }
		} 
	
	/**
	 * Set display time per photo for slide show.
	 */
	public void setPhotoTime() {
		try {
		String t = (String)JOptionPane.showInputDialog(frame, "Please enter the number of seconds you would like each photo to display for.", JOptionPane.PLAIN_MESSAGE);
		time = Integer.parseInt(t) * 1000; //Multiplies by 1000 because it is in milliseconds
		if (time <= 0) {
			JOptionPane.showMessageDialog(null,"You have not entered a valid number, please try again");
		} }
		catch (Exception e) {
			JOptionPane.showMessageDialog(null,"You have not entered a valid number, please try again (decimal numbers not accepted)");
		}
				
	}
	
	/**
	 * Displays the images selected by user in a slide show.
	 */
	public void startSlideShow()
	{
		try {
			if (time == 0) {
				JOptionPane.showMessageDialog(null,"You have not selected how long you would like each image to display for. ");
				close();
			}
			counter = 0;
			showStatus("Slide Show Loaded");
			filenameLabel.setText("Image " + (counter + 1) + " displaying");
			
			currentImage = slideshowPhotos.get(counter);
			imagePanel.setImage(currentImage);
			setButtonsEnabled(true);
			
			frame.pack();	
				Timer timer = new Timer(time, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						counter++;
						currentImage = slideshowPhotos.get(counter); 
						imagePanel.setImage(currentImage); 
						filenameLabel.setText("Image " + (counter + 1) + " displaying");
						frame.pack();	
						//Checks if it has looped through every image in array list and clears the array list if so
						if (counter == (slideshowPhotos.size() - 1) ) { 
							showStatus("Slide Show Complete!");
							imagePanel.setImage(currentImage); 
							slideshowPhotos.clear();
						}
					}
				});
				timer.start();
			}
		catch(ArrayIndexOutOfBoundsException e) {
			JOptionPane.showMessageDialog(null,"There are no images to display.");
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(null,"Error starting slide show, please try again.");
		}
	}

	/**
	 * Create a list with all the known filters.
	 * @return The list of filters.
	 */
	private List<Filter> createFilters()
	{
		List<Filter> filterList = new ArrayList<>();
		filterList.add(new DarkerFilter("Darker"));
		filterList.add(new LighterFilter("Lighter"));
		filterList.add(new ThresholdFilter("Threshold"));
		filterList.add(new InvertFilter("Invert"));
		filterList.add(new SolarizeFilter("Solarize"));
		filterList.add(new SmoothFilter("Smooth"));
		filterList.add(new PixelizeFilter("Pixelize"));
		filterList.add(new MirrorFilter("Mirror"));
		filterList.add(new GrayScaleFilter("Grayscale"));
		filterList.add(new EdgeFilter("Edge Detection"));
		filterList.add(new FishEyeFilter("Fish Eye"));
		filterList.add(new SepiaFilter("Sepia"));
		return filterList;
	}

	// ---- Swing stuff to build the frame and all its components and menus ----

	/**
	 * Create the Swing frame and its content.
	 */
	private void makeFrame()
	{

		frame = new JFrame("ImageViewer");
		JPanel contentPane = (JPanel)frame.getContentPane();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));

		makeMenuBar(frame);

		// Specify the layout manager with nice spacing
		contentPane.setLayout(new BorderLayout(6, 6));

		// Create the image pane in the centre
		imagePanel = new ImagePanel();
		imagePanel.setBorder(new EtchedBorder());
		contentPane.add(imagePanel, BorderLayout.CENTER);

		// Create two labels at top and bottom for the file name and status messages
		filenameLabel = new JLabel();
		contentPane.add(filenameLabel, BorderLayout.NORTH);

		statusLabel = new JLabel(VERSION);
		contentPane.add(statusLabel, BorderLayout.SOUTH);

		//ScrollPane becomes visible when photo is too large for panel
		scrollPane = new JScrollPane(imagePanel);
		frame.getContentPane().add(scrollPane);
		scrollPane.setPreferredSize(null);

		// Create the tool bar with the buttons
		JPanel toolbar = new JPanel();
		toolbar.setLayout(new GridLayout(0, 1));

		smallerButton = new JButton("Smaller");
		smallerButton.addActionListener(e -> makeSmaller());
		toolbar.add(smallerButton);

		largerButton = new JButton("Larger");
		largerButton.addActionListener(e -> makeLarger());
		toolbar.add(largerButton);

		undoButton = new JButton("Undo");
		undoButton.addActionListener(e -> undo());
		toolbar.add(undoButton);

		redoButton = new JButton("Redo");
		redoButton.addActionListener(e -> redo());
		toolbar.add(redoButton);
		
		rotateLeft = new JButton("Rotate Left");
		rotateLeft.addActionListener(e -> rotateLeft());
		toolbar.add(rotateLeft);
		
		rotateRight = new JButton("Rotate Right");
		rotateRight.addActionListener(e -> rotateRight());
		toolbar.add(rotateRight);
		
		reloadButton = new JButton("Reload Image");
		reloadButton.addActionListener(e -> reload());
		toolbar.add(reloadButton);

		// Add tool bar into panel with flow layout for spacing
		JPanel flow = new JPanel();
		flow.add(toolbar);

		contentPane.add(flow, BorderLayout.WEST);

		// building is done - arrange the components      
		showFilename(null);
		setButtonsEnabled(false);
		setUndoButton(false);
		setRedoButton(false);
		frame.pack();

		// place the frame at the center of the screen and show
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(d.width/2 - frame.getWidth()/2, d.height/2 - frame.getHeight()/2);
		frame.setVisible(true);
	}

	/**
	 * Create the main frame's menu bar.
	 * 
	 * @param frame   The frame that the menu bar should be added to.
	 */
	private void makeMenuBar(JFrame frame)
	{
		final int SHORTCUT_MASK =
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		JMenuBar menubar = new JMenuBar();
		frame.setJMenuBar(menubar);

		JMenu menu;
		JMenuItem item;

		// create the File menu
		menu = new JMenu("File");
		menubar.add(menu);

		item = new JMenuItem("Open...");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
		item.addActionListener(e -> openFile());
		menu.add(item);

		item = new JMenuItem("Close");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, SHORTCUT_MASK));
		item.addActionListener(e -> close());   
		menu.add(item);
		menu.addSeparator();

		item = new JMenuItem("Save As...");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
		item.addActionListener(e -> saveAs());
		menu.add(item);
		menu.addSeparator();

		item = new JMenuItem("Quit");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
		item.addActionListener(e -> quit());
		menu.add(item);


		// create the Filter menu
		menu = new JMenu("Filter");
		menubar.add(menu);

		for(Filter filter : filters) {
			item = new JMenuItem(filter.getName());
			item.addActionListener(e -> applyFilter(filter));
			menu.add(item);
		}

		// create the Help menu
		menu = new JMenu("Help");
		menubar.add(menu);

		item = new JMenuItem("About ImageViewer...");
		item.addActionListener(e -> showAbout());
		menu.add(item); 
		
		//Create Slide show menu
		menu = new JMenu("Create Slide show");
		menubar.add(menu);

		item = new JMenuItem("Select Images");
		item.addActionListener(e -> fillArraylist());
		menu.add(item); 
		
		item = new JMenuItem("Select Time Per Image");
		item.addActionListener(e -> setPhotoTime());
		menu.add(item); 
		
		item = new JMenuItem("Start Slideshow");
		item.addActionListener(e -> startSlideShow());
		menu.add(item); 

	}
	} 
