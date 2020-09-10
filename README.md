# CS351 Project 1: Modulo Times Tables Visualization
## Nicholas Livingstone UNM Fall 2020

This project is a JavaFX application that will animate a visualization
of integer times tables around a circle. A YouTube video explaining the concept
can be found [here](https://www.youtube.com/watch?v=qhbuKbxJsk8&vl=en). 

### Usage
The application includes a display for the animation on the left and a set of controls
on the right. 

Controls: 
* Number of dots can be controlled by inputting a number in the dot box and pressing set. 
* The slider allows the speed of the animation to be adjusted (1-60 fps)
* Custom time step can be inputted
* Color options can be picked while animation is running including static colors, a rainbow effect, and cycle of colors.
* Node input allows for the ability to go to a specific node but will not function if the node is larger
than the current number of dots in the animation.
* Dot enable check-box allows the visibility of dots on the perimeter of circle to be toggled
* Play/Pause button to start and stop the animation
* The ability to save and view photos of the animation. 
    * Save current view button takes a snapshot of the current view in the animation 
    * Snapshots can be viewed with the photos button. 
* Reset button will stop animation and revert to the following defaults: 
    * 10 dots
    * 30 FPS
    * 2 Times step
    * white color
    * dots enabled

*Note: Features including the speed, color, color cycle, and visual of dots can be adjusted while the animation is running,all other features will restart the animation.*

### Design and Architecture
The visual aspect of the application was created using SceneBuilder and FXML sheets. All other pop-ups and
windows were created directly though java code. 

The math of the application is calculated primarily by the functions of the **TTAnimationMath** class. **calcPointCoordinates**
calculates the coordinates of the dots representing the numbers on the perimeter of the circle by the use of
sin and cos functions. It creates a starting point at PI, and rotates the point clockwise to produce all other one.
The rotation is based on the number of dots set, the function divides the circle into that many number of points and
finds the relative rotation. The coordinates are added in the order they are created to an array for the animation.
This approach becomes very effective when calculating the lines of animation, as the numbers are indexed based on the number
they represent. i.e. 0 is the first coordinate calculated, and the **calcLine** function can utilize the modulo
operator when determining what dots are connected (also what the multiplication results in).


### Known Issues and Unfinished Features
* While incorrect input will not crash the application, many invalid inputs are possible and not caught through errors.
* Mixing color features while the animation is running will produce some interesting results and have not been fully
designed to run in sync with each other e.g. selecting a static color, then selecting color cycle or rainbow.
* Current approach to toggling dot visibility is inefficient and should be modified, as it's possible to produce an
excess of dots. 
#### To be added:
* the ability to save photos to the computer
* adding a slider to adjust the rate of change for the dynamic color features e.g. increasing the speed of
the rainbow effect. A default value has been set in the code, but the framework exists to eventually add this. 
* ability to resize application, the current design of the animation does not allow for scaling without direct modification of code. 