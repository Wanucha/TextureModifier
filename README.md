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
* You can add properties file or image file as arguments, they will be opened on start
  * If you provide multiple files, the first one will be opened
### Known bugs
* Sometimes menu in GUI is hidden behind image view - click on properties tab, then it's always visible
* Does not handle jpg well (can mess up colors or throw errors) - use png
## Command line
* Run with arguments
  * Order of arguments does not matter (except command order)
### Single properties file
* Files are saved to current directory, you must define out-prefix or out-postfix
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
### Multiple image files
  * Absolute path
    * D:/image.png
  * Relative path (to current directory)
    * image.png
  * You can use wildcards * or ?
  * Examples:
    * 'img/*.png'
    * 'texture??.jpg'
### List of commands
  * Commands are executed in defined order using single properties
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
  * Here you have option to run commands with different properties - run the jar multiple times
  * Add pause to the end, so you will see the result
* Examples:
  * java -jar TextureModifier.jar settings.properties --blur C:/maps/*.png "images/normals/normal map?.png"
  * java -jar TextureModifier.jar settings.properties --blur --pixelate --seamless --blur images/*.png
