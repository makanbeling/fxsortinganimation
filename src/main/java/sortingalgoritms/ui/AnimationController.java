/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package sortingalgoritms.ui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import sortingalgoritms.util.RandomValues;
import sortingalgoritms.util.ISortOperator;

/**
 * FXML Controller class
 *
 * @author Eric Canull
 */
public class AnimationController extends AnchorPane implements ISortOperator {
    
    @FXML private GridPane barsGrid;
    @FXML private GridPane textFieldsGrid;
   
    private int indexPos;
   
    public AnimationController() {
        initialize();
    }
    
    /**
     * Initializes the controller class.
     */
    private void initialize() {
        try {
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AnimationController.class.getResource("/fxml/FXMLAnimationPane.fxml")); //NOI18N
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(AnimationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        barsGrid.widthProperty().addListener(evt -> addGridBars());
        barsGrid.heightProperty().addListener(evt -> addGridBars());
    }
    
    public void setPresetValues(String presetChoice) {
        RandomValues.setRandomSet(presetChoice, null);
     
        IntStream.range(0, 10).forEachOrdered(index -> {
            TextField tf = (TextField) textFieldsGrid.getChildren().get(index);
            tf.setText(String.valueOf(RandomValues.getArray()[index].getValue()));
        });
        
        addGridBars();
    }

    public void addGridBars() {

        if (RandomValues.getArray() == null
                || Double.isNaN(barsGrid.getWidth())
                || Double.isNaN(barsGrid.getHeight())) {
            return;
        }

        barsGrid.getChildren().removeAll(barsGrid.getChildren());
        final double width = (barsGrid.getWidth()/10d) + -6;
        IntStream.range(0, 10).forEachOrdered((int index) -> {
            CompareValue compareValue = RandomValues.getArray()[index];
            
            double height = calculateHeight(compareValue.getValue());
            Rectangle rect = new Rectangle(width, height);
            rect.setFill(compareValue.getColor());
            barsGrid.add(rect, index, 0);
        });
    }
    
    /**
     * Use slope and y-intercept formulas to calculate the bars height
     * for resizing.
     */
    private double calculateHeight(double value) {
        double y1 = 0;
        double y2 = barsGrid.getHeight();

        double x1 = RandomValues.getMaxValue();
        double x2 = 0;

        // 1st calculate the slope 
        double slope = (y1 - y2) / (x1 - x2);

        // 2nd calculate the y-Intercept
        double yIntercept = (y2 * x1 - y1 * x2) / (x1 - x2);

        // 3rd calculate the new height
        double height = y2 - (slope * value + yIntercept);

        return height;
    }

    @Override
    public Object apply(Object object) {
        if (indexPos == RandomValues.MAX_SIZE) {
            indexPos = 0;
        }

        while (indexPos < RandomValues.MAX_SIZE) {
            CompareValue compareValue = (CompareValue) object;   
            String color = Integer.toHexString(compareValue.getColor().hashCode());
            
            Rectangle rect = (Rectangle) barsGrid.getChildren().get(indexPos);   
            TextField textfield = (TextField) textFieldsGrid.getChildren().get(indexPos);
            
            rect.setFill(Color.web(color));
            
            textfield.setStyle("-fx-border-color: #" + color + ";" 
                             + "-fx-background-color: #" + color.replace("ff", "33") + ";");
          
            double height = calculateHeight(compareValue.getValue());
            Timeline tl = new Timeline();
            tl.setCycleCount(1);

            KeyValue k1 = new KeyValue(rect.heightProperty(), height);
            KeyFrame kf1 = new KeyFrame(Duration.millis(100), k1);
            tl.getKeyFrames().add(kf1);
            tl.play();

            textfield.setText(String.valueOf(compareValue.getValue()));

            indexPos++;
            break;
        }
        
        return null;
    }
}