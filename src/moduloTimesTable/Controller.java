package moduloTimesTable;

import javafx.animation.AnimationTimer;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * TimesTableFXML.fxml sheet Controller. All primary logic for the
 * application occurs here.
 */
public class Controller implements Initializable{

    // Javafx members
    public Slider fpsSlider;
    public TextField stepInput;
    public Button setTimeBttn;
    public ColorPicker colorSelection;
    public TextField fpsDisplay;
    public Button photoBttn;
    public Button saveViewBttn;
    public ToggleButton playPause;
    public Pane drawPane;
    public Button setDotBttn;
    public TextField dotInput;
    public TextField nodeInput;
    public Button nodeGoBttn;
    public Button resetBttn;
    public ToggleButton rainbowBttn;
    public ToggleButton colorCycleBttn;
    public CheckBox dotsEnabled;


    // Class members for functions
    private int fps = 30,
        timesStep = 2,
        dots = 10,      // Number of dots in animation
        currentVal = 1; //value for times table

    private final ArrayList<WritableImage> savedImages = new ArrayList<>();
    private double msFps = 1_000_000_000 / ((float) fps); //fps in nanoseconds
                    private final double hueShiftRate = 30;
    private final double hueShift = hueShiftRate/dots;

    private Coordinates[] classCoords;  // Current dot coordinates at class level
    private Color currentColor = Color.WHITE;
    private final Vector<Circle> groupDots = new Vector<>();

    /**
     * Timer to control FPS and color shifts
     * FPS is calculated as milliseconds and updated here
     */
    AnimationTimer timer = new AnimationTimer() {
        private long lastUpdate;

        @Override
        public void handle(long now) {
            if (now - lastUpdate >= msFps) {
                // Update colors if rainbow or color cycle is selected
                if(rainbowBttn.isSelected()) {
                    currentColor = currentColor.deriveColor(hueShift, 1, 1, 1);
                }
                else if(colorCycleBttn.isSelected()) {
                    currentColor = currentColor.deriveColor(hueShift, 1, 1, 1);
                    setColorAll();
                }

                drawLine();
                lastUpdate = now ;
            }
        }
    };

    /**
     * Called when stage is launched. Initializes the stage.
     * Displays default values and establishes a listener for
     * the fpsSlider.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        dotInput.setText(String.valueOf(dots));
        drawPane.setStyle("-fx-background-color: black;");
        fpsDisplay.setText(String.valueOf(fps));
        fpsSlider.setValue(fps);
        stepInput.setText(String.valueOf(timesStep));

        fpsSlider.setOnMouseReleased(event -> {
            fps = (int) fpsSlider.getValue();
            fpsDisplay.setText(String.valueOf(fps));
            msFps = 1_000_000_000 / (float)fps;
        });

        classCoords = TTAnimationMath.calcPointCoordinates(dots);
        initAnimation(classCoords);
    }

    /**
     * Called when new times value is entered and set button pressed.
     * Resets and pauses animation.
     */
    public void timeSetPressed() {
        currentVal = 1;
        timesStep = Integer.parseInt(stepInput.getText());
        initAnimation(classCoords);
    }

    /**
     * Called when color is selected from colorSelection node.
     * Will then set the color of all children in pane
     * drawPane to the specified color.
     */
    public void colorSet() {
        currentColor = colorSelection.getValue();
        setColorAll();
    }

    /**
     * Called when go to Node button is pressed. Resets the animation
     * and runs until specific node is reached. Node specified is grabbed
     * from nodeInput text field. Will display an error pop-up in the
     * event the node requested is larger than the number of nodes available.
     */
    public void goBttnPressed() {
        int requestedNode = Integer.parseInt(nodeInput.getText());
        if(requestedNode > dots) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Try something smaller...");
            alert.setContentText("Node requested too large of a number." +
                    "Must be smaller than current number of dots.");
            alert.showAndWait();
            return;
        }
        resetBttn.fire();
        for(currentVal= 1; currentVal < requestedNode; currentVal++) {
            drawLine();
        }
    }

    /**
     * Called when the play/pause button is pressed.
     * Will play/pause the animation by starting and stopping
     * animationTimer timer.
     */
    public void playPressed() {
        if(playPause.isSelected()) {
            timer.start();
        }
        else {
            timer.stop();
        }
    }

    /**
     * Called when photoBttn pressed. Launches a new
     * stage that shows photos that have been previously saved,
     * allows the user to cycle through the photos. Will
     * launch an error pop-up if not photos have been saved.
     */
    public void photosPress() {

        // Ensure that images have been saved already
        // Error prompt will appear if none have been saved
        if(savedImages.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Photos Found...");
            alert.setContentText("Try Taking some with the save " +
                                "image button in the main window!");
            alert.showAndWait();
            return;
        }

        //Otherwise continue with displaying photos
        // NOTE: FINAL Arrays are used here so they can be modified
        // in the button functions
        // Setup a new stage to display images
        final Stage successWindow = new Stage();

        // Setup images, get first image and set it to be displayed
        final int[] currentImgIndex = {0};
        WritableImage currentImg = savedImages.get(currentImgIndex[0]);
        final ImageView[] imgview = {new ImageView(currentImg)};

        // Right Button
        Button right = new Button ("→");
        right.setOnAction(event -> {
            // Iterate to next image
            currentImgIndex[0]++;
            // Iterate to beginning if images have been looped through
            if(currentImgIndex[0] >= savedImages.size())
                currentImgIndex[0] = 0;
            // Set the image in image view
            imgview[0].setImage(savedImages.get(currentImgIndex[0]));
        });

        // Left Button
        Button left = new Button ("←");
        left.setOnAction(event -> {
            // Iterate to prev image
            currentImgIndex[0]--;
            // Iterate to end if images have been looped through
            if(currentImgIndex[0] < 0)
                currentImgIndex[0] = savedImages.size() - 1;
            // Set the image in image view
            imgview[0].setImage(savedImages.get(currentImgIndex[0]));
        });

        // Setup window and buttons to display nicely
        right.setPrefWidth(450);
        right.setPrefHeight(75);
        left.setPrefWidth(450);
        left.setPrefHeight(75);
        HBox hbox = new HBox(left, right);
        VBox vbox = new VBox(imgview[0], hbox);
        Scene success = new Scene(vbox);
        successWindow.setScene(success);
        successWindow.setTitle("Saved Photos");
        successWindow.setResizable(false);
        successWindow.show();   //Show Stage
    }

    /**
     * Saves current animation view using snapshot() method.
     * Photos are added in a WritableImage vector called
     * savedImages. Will show pop-up notifying user that the
     * image has been saved.
     */
    public void saveView() {
        savedImages.add(drawPane.snapshot(null, null)); //save image

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Photo Success");
        alert.setHeaderText("Success!");
        alert.setContentText("A photo of the current view was saved");

        alert.showAndWait();
    }


    /**
     * Called when set button for dot input is pressed.
     * Will set new dot count value and reset the animation.
     * Calculates a new set of coordinates based on input.
     */
    public void dotSetPressed() {
        dots = Integer.parseInt(dotInput.getText());
        resetBttn.fire();
        classCoords = TTAnimationMath.calcPointCoordinates(dots);
        initAnimation(classCoords);
    }

    /**
     * Called either by other functions through fire() or when
     * resetBttn is pressed. Resets the animation and reverts to
     * default values. Pauses animation.
     * */
    public void resetPressed() {
        currentVal = 1;
        playPause.setSelected(false);
        colorCycleBttn.setSelected(false);
        rainbowBttn.setSelected(false);
        currentColor = Color.WHITE;
        initAnimation(classCoords);
        timer.stop();
    }

    /**
     * Called by other local functions. Clears the pane of any animations
     * and will optionally draw new set of dots if dots are enabled.
     * Accepts coordinates to draw.
     *
     * @param coords coordinates to bew drawn as an array
     */
    private void initAnimation(Coordinates[] coords) {

        drawPane.getChildren().clear(); //clear pane
        drawDots(coords); //Draw Coordinates
    }

    /**
     * Draws dots for the animation based on calculated coordinates.
     * Will draw dots as white if the rainbowBttn is enabled. Otherwise
     * uses the color. Dots are drawn as circles and added to groupDots vector
     * for use in other functions. Added to children of drawPane.
     *
     * @param coords coordinates to be drawn as array
     */
    private void drawDots(Coordinates[] coords){
        Color tempColor = currentColor;

        if(rainbowBttn.isSelected()) tempColor = Color.WHITE;

        if(dotsEnabled.isSelected()) {
            float x, y;
            Circle[] circs = new Circle[coords.length];
            Circle temp;

            for(int i = 0; i < coords.length; ++i){
                x = coords[i].getX();
                y = coords[i].getY();
                temp = new Circle(2, tempColor);
                temp.relocate(x, y);
                circs[i] = temp;
                groupDots.add(temp);
                drawPane.getChildren().add(circs[i]);
            }
        }

    }

    /**
     * Called by other local functions. Creates and draws new
     * line on drawPane in order based on currentVal.
     */
    private void drawLine()
    {
        Line newLine = TTAnimationMath.calcLine (
                    currentVal, timesStep, classCoords, currentColor, dots);
        drawPane.getChildren().add(newLine);
        //Complete iterations to next value
        currentVal++;
    }

    /**
     * Called when rainbowBttn is pressed. Resets current color to red
     * if color is white. Disables the colorCycle button id enabled. Sets the
     * color of the dots to white.
     */
    // Rainbow pressed and color cycle pressed disable eachother
    public void rainbowPressed() {
        colorCycleBttn.setSelected(false);
        // Color modifying function does not work if color is white
        if(currentColor.equals(Color.WHITE)) currentColor = Color.RED;
        // Update dot color
        if(rainbowBttn.isSelected()) {
            setColorDots();
        }
    }

    /**
     * Called when color cycle is Selected. Disables rainbow button.
     * Sets current color to red.
     */
    public void colorCyclePressed() {
        rainbowBttn.setSelected(false);
        // Color modifying function does not work if color is white
        if(currentColor.equals(Color.WHITE))
            currentColor = Color.RED;
    }

    /**
     * Called by other local functions.
     * Sets the color of all children in drawPane. Both
     * circles and lines.
     */
    private void setColorAll() {

        for(Node child : drawPane.getChildren()){
            Shape shape = (Shape) child;
            shape.setFill(currentColor);
            shape.setStroke(currentColor);
        }
    }

    /**
     * Called only by rainbowPressed().
     * Sets the color of the dots only to white
     */
    private void setColorDots()
    {
        for(Node child : drawPane.getChildren()){
            Shape shape = (Shape) child;
            shape.setFill(Color.WHITE);
        }
    }

    /**
     * Called when the dots enabled button is checked
     * or unchecked. Will draw dots if none are present
     * or set the opacity of existing dots to zero.
     */
    public void dotsCheckBoxClicked() {
        // Check if dots are being enabled or disabled
        if(dotsEnabled.isSelected()) {
            drawDots(classCoords);
            return;
        }

        for(Circle circle : groupDots){
            circle.setOpacity(0);
        }
    }
}
