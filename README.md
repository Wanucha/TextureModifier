# Texture modifier
* Forum: https://discord.gg/FuWxWYqcX4
* GitHub: https://github.com/Wanucha/TextureModifier
* Applies various transformations to input image
  * Can be used to pixelate images
  * Merge multiple maps into one
  * Blur image
  * Much more...
* Can be launched with GUI or from command line for batching
## GUI
* Run without arguments
* You can add settings file or image file as arguments, they will be opened on start
  * If you provide multiple image files, the first one will be opened
* Examples:
  * java -jar TextureModifier.jar settings.yml _(open settings on start)_
  * java -jar TextureModifier.jar settings.yml textures/image.png _(open settings and image on start)_
### Known bugs
* Sometimes menu in GUI is hidden behind image view - click on Settings tab, then it's always visible
* Does not handle jpg well (can mess up colors or throw errors) - use png
## Settings file
* The settings are saved to yml file, use it to create presets.
* The program can parse yml and legacy properties files
* Settings can be saved only as yml
## Command line
* Run with arguments
  * Provide list of files and commands 
  * Order of arguments does not matter (except command order)
* Example
  * java -jar TextureModifier.jar settings.yml --command1 --command3 --command2 path_to_image path_to_multiple_images
  * What will happen:
    * Load the settings
    * Process all the provided images
    * Run the commands in specified order for each image
### Output configuration for commands
* Files are saved to current directory, you must define out-prefix or out-postfix in the settings file
  * You can use prefix to define output directory
    * Example: out-prefix=output/
  * Postfix is inserted before file extension
    * Example: out-postfix=_n
    * image.bmp -> image_n.bmp
* If out-format is not specified, saves to same format as input image
  * Supported export formats: png, jpg, bmp, gif
* Define overwrite-type (what to do, when the output file already exists)
  * IGNORE - does not save the output
  * OVERWRITE - delete existing file and save the output
  * There is no interactive mode
### Possible image paths
  * Absolute path
    * D:/image.png
  * Relative path (to current directory)
    * image.png
  * You can use wildcards * or ?
  * Examples:
    * 'img/*.png'
    * 'texture??.jpg'
### List of commands
  * Commands are executed in defined order using single settings
  * Available commands:
    * --seamless
    * --blur
    * --pixelate
    * --fill_bg
    * --merge_maps
    * --multiply_color
    * --remove_alpha
### How to run
* Run directly from command line
* Create .bat file
  * Here you have option to run commands with different settings, just run the jar multiple times
  * Add pause to the end, so you will see the result
* Examples:
  * java -jar TextureModifier.jar settings.yml --blur C:/maps/*.png "images/normals/normal map?.png" _(blur all the provided pngs)_
  * java -jar TextureModifier.jar settings.yml --blur --pixelate --seamless --blur images/*.png _(take all pngs in folder 'images' and apply in order: blur, pixelate, seamless, blur again)_
