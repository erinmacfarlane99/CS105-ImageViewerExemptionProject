# CS105-ImageViewerExemptionProject
Project completed in order to gain exemption from the exam in CS105 - Programming Foundations.

Extends upon the ImageViewer (final version) from Chapter 13 of the BlueJ textbook - https://www.bluej.org/objects-first/.

Features added and explanations:
- **Reload Image**: this button when pressed will simply reload the image by opening up the original
version of the image from the file directory. The code is taken from the method ‘openFile’ in the
original Image Viewer system that opens up the chosen image.
- **Disabled menu functions**: I have disabled all menu functions when there
is no image being displayed. I used the ‘setButtonsEnabled’ method in order to do so and set
them to be greyed out when an image is not available to be altered. For my undo and redo
buttons I created two separate methods that change the status of each button so that I can can
control when they should be able to be clicked. Therefore, if a change is made to an image the
undo button will not be greyed out anymore because an undo can be done, and the redo button
becomes visible when an undo is made so that a redo of the undo can be called.
• **Undo/Redo functions**: I have added two buttons to the JFrame that call methods, when clicked,
to undo and redo changes a user has made to the image. These buttons can deal with multiple
undo’s and redo’s. In order to do this, I made 2 array lists filled with OFImages that store
versions of the image a user has altered before (undo array list) and after (redo array list)
changes have been made. At the beginning of methods that alter the image like filters, rotate
left, rotate right, smaller, larger I firstly always add the current image being displayed before the
change to the undo array list. In the undo method it begins by adding the current image being
displayed to the redo array list meaning that when redo is called the version of the image before
the undo can be displayed again. It then accesses the last image in the undo array list and
displays this inside the image panel. The redo method works very similarly by adding the current
image being displayed to the undo array list then displaying the last image in the redo array list
to the image panel. After each undo and redo they always remove the last image from the undo
or redo array lists because they are now in the opposite array lists to go back or forward to if
requested by a user.
- **Alteration to applyFilter method**: when undoing changes such as rotations and making the
image smaller or larger I was able to simply add the current image to the undo and redo array
lists because these changes being applied change the size or rotation of each pixel, hence
altering the image. However, the filters worked slightly different as they change the RGB values
of the pixels so in order to overcome this I had to add a nested loop that went through each
pixel column by column gaining the RGB value for all and then setting this to the new image.
Meaning I could still use the same undo and redo functions to undo and redo filters.
- **Rotate Left and Rotate Right by 90 degrees**: I added two buttons to the system named ‘Rotate
Right’ and ‘Rotate Left’ that called methods to rotate the image either left or right by 90 degrees
when pressed. The inner and outer loop for the nested loops means that each pixel in the image
will be altered column by column (from the top to the bottom). The outer loop counter (x) goes
up by one each time the inner loop has gone through every pixel in that column from top to
bottom then does the same again until the outer loop stops (i.e. all pixels have been changed).
For each pixel in the image it sets it so that for rotate right the pixel will move so that it’s flipped
90 degrees to the right and vice versa for the left. The code for these methods I got help from:
http://forum.codecall.net/topic/69182-java-image-rotation/.
- **Added Sepia Filter**: I used inheritance by inheriting the abstract class Filter to generate a new
filter that applies a Sepia tone to the current image. In order to do so I got help from the
website: https://www.dyclassroom.com/image-processing-project/how-to-convert-a-colorimage-
into-sepia-image to generate code that calculates the required RGB values from the
current RGB values per pixel in order to make the image appear slightly vintage, warm toned. It
takes the RGB values for each pixel and multiplies them by specific decimal numbers that apply
to the sepia tone and loops through each column testing the calculates values and sets the
RGB values accordingly using if else statements. In order to get this to appear in the Filter dropdown
menu I added it to the createFilters list.

    <img width="457" alt="Screenshot 2022-10-18 at 19 25 26" src="https://user-images.githubusercontent.com/116073553/196513577-53fd035f-7d77-4bfe-ae27-cdf31c5a01ee.png">

- **Scroll Pane**: I have added a JScrollPane that appears when the image being displayed in the
image panel is too large to fully be seen, this means a user can move about the image to see
the full thing or move to certain parts of the image. I added 3 lines of code to the method
makeFrame that sets the ScrollPane to appear around the image panel, adds it to the frame and
uses the method ‘setPrefferedSize()’ with the parameter ‘null’ to ask the UI what the best size is
for the scroll pane when needed meaning it only appears when the image is too big for the
image panel.

    <img width="400" alt="Screenshot 2022-10-18 at 19 25 43" src="https://user-images.githubusercontent.com/116073553/196513638-acd82e45-a0f0-49ae-b960-985bf2388646.png">

- **Slideshow**: I have created a new menu in the JFrame called ‘Create Slide show’ that has 3 drop
down menu items, all of which correspond to 3 different methods. The first menu item is called
‘Select Images’, this is a function that allows the user to select the images they would like to be
displayed in their slide show, one by one. This menu item calls ‘fillArrayList’ when clicked by a
user and this method uses some of the code from ‘openFile’ in the original image viewer system
to open up the directory and allow a user to select an image. However, this time when a user
selects an image file it is not automatically displayed but instead added to an array list filled with
images. The second menu item named ‘Select Time Per Image’ displays an input box that the
user can type in an integer for how long they would like each image in the slideshow to display
for. It uses a JOptionPane in order to ask the user for an input and sets this string input to an
integer. It uses checked Exceptions to catch errors so that if a user does not enter a valid
number then an error box will display to the user. Meaning the program does not crash with
unexpected input and the user knows to try and set the photo time again with a valid input. The
third menu item ‘Start Slideshow’ calls the method ‘startSlideShow’ which uses both of the
methods mentioned before ‘fillArrayList’ and ‘setPhotoTime’. It works by using a Timer which is
imported from Java Swing and sets the timer to display an image to the number of seconds
entered by the user before. If no time has been chosen by a user then an error message will
appear. The method firstly sets the image being displayed to the first image in the array list that
has been filled by a user and this image will display for the time chosen. The timer essentially
works as a loop, it performs the body of the code inside ‘actionPerformed’ and runs for the
chosen number of seconds. Each time it displays the next image in the array list, until the
counter has gone through every image and then it will delete all the photos in the array list and
the slideshow is complete. The method uses checked exceptions to throw errors when there are
no images in the array list and in general if the slideshow just won’t start, meaning the program
won’t crash.
