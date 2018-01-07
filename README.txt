|| ********************************************************************* ||
|| 	Welcome to ImageMorph!					         ||
||		by Taylor Ecton						 ||
|| ********************************************************************* ||

Files:
	The .tar.gz file that contained this README.txt should also
	contain:
		- Jama-1.0.3.jar
		+ src/
		|-------- ImageMorph.java
		|-------- ImageMorphHelpWindow.java
		|-------- ImageMorphIO.java
		|-------- ImageMorphMenu.java
		|-------- ImageMorphController.java
		|-------- ImageMorphWindow.java
		|-------- MorphController.java
		|-------- SettingsPanel.java

Compilation:
	The project was written in IntelliJ IDEA, and is currently
	designed to be run using the IDE. If run from IntelliJ, make sure 
	that you add the Jama JAR as a dependency. I included it in the 
	ImageMorph.tar.gz file. To add it, in IntellJ select:
		File -> Project Structure... -> Modules
		and click the '+' to add a dependency, and select the
		Jama-1.0.3.jar.

	If run from the command line, make sure that you add the Jama JAR
	file to the CLASSPATH environment variable. This can be done by
	setting the CLASSPATH in a .bashrc or .bash_profile file or by
	using:
	    $ export CLASSPATH=$CLASSPATH:/absolute/path/to/Jama-1.0.3.jar

Running:
	While running, load in two images (no restrictions on size or
	aspect, though they will be resized to be no larger than 600*400),
	and drag the control points to the appropriate locations to
	set up your morph.

	The program requires the left image to be loaded in first (because
	the image sizes will both be set based on the start image) and the
	program will let the user know this if they attempt to load an
	end image first.

	Various settings for the lattice, morph, and images can be found in
	a Settings panel on the left hand side of the window. This settings
	panel can be hidden by selecting 'Options' in the menu bar, and
	selecting the 'Show Editor Settings' item.

	Pressing the 'Preview Morph' button after setting images for both
	panels will play a preview of the morph based on the current
	settings of the lattice in the two panels. The preview will play
	in the left panel, and the 'Preview Morph' button will change
	to 'Reset Morph'. Lattice cannot be adjusted until the morph is
	reset.

	Another important feature in the settings panel is the ability to
	toggle on/off the control points and lattice. They can clutter the
	images when displayed, and it is sometimes useful to disable them.

Saving/Loading:
	Saving and loading of projects is available. Just choose 'File'
	from the menu bar and select the Save or Load option.

	KNOWN ISSUES: To the best of my knowledge, the only issue with the
		      save/load feature currently is that it does not save
		      brightness settings for the image if they are
		      adjusted.

Exporting:
	To export a project as a series of JPEG files, choose 'File' ->
	'Export...', and select the directory in which the files should
	be saved. The morph will play out as the project is exporting.

Questions / Issue Reporting:
	If you have any questions not answered by this README.txt file,
	or if you come across any undocumented issues, please contact
	Taylor Ecton at the following email address:
		taylor.ecton@gmail.com
