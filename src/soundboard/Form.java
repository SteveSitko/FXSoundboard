package soundboard;

import java.io.File;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class Form extends Application {
	
	// Control defaults
	private final double VOLUME_DEFAULT = 50;
	private final double RATE_DEFAULT = 1.0;
	
	// Main container
	private Scene scene;
	private BorderPane root;
	
	// Control bar
	private GridPane grid_control;
	
	private Label lbl_volume;
	private Slider slider_volume;
	
	private Label lbl_rate;
	private Slider slider_rate;
	
	private Button btn_resetControls;

	// Accordion
	private Accordion accord;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primary) {
		initializeAndAddComponents();
		
		primary.setScene(scene);
		primary.setTitle("Soundboard");
		primary.show();
	}
	
	/**
	 * Create each component and add it to the scene graph
	 */
	private void initializeAndAddComponents() {
		// Main container
		root = new BorderPane();
		scene = new Scene(root, 850, 775);
		scene.getStylesheets().add(getClass().getResource("form.css").toExternalForm());
		
		// Control bar (bottom)
		grid_control = new GridPane();
		grid_control.setId("controlBar");
		grid_control.setAlignment(Pos.CENTER);
		grid_control.setHgap(10);
		grid_control.setPadding(new Insets(7, 10, 0, 10));
		
		lbl_volume = new Label("Volume: ");
		slider_volume = new Slider(0, 100, 50);		// Min 0, max 100, initial 50
		slider_volume.setShowTickMarks(true);
		slider_volume.setShowTickLabels(true);
		slider_volume.setMajorTickUnit(50);
		
		lbl_rate = new Label("Playback Rate: ");
		slider_rate = new Slider(0, 3, 1);
		slider_rate.setShowTickMarks(true);
		slider_rate.setShowTickLabels(true);
		slider_rate.setMajorTickUnit(1);
		slider_rate.setMinorTickCount(3);
		
		btn_resetControls = new Button("Reset");
		btn_resetControls.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				slider_volume.setValue(VOLUME_DEFAULT);
				slider_rate.setValue(RATE_DEFAULT);
			}
		});
		
		grid_control.add(btn_resetControls, 0, 0);
		grid_control.add(lbl_volume, 1, 0);
		grid_control.add(slider_volume, 2, 0);
		grid_control.add(lbl_rate, 3, 0);
		grid_control.add(slider_rate, 4, 0);
		
		// Create accordion and add it to the root node (center)
		accord = new Accordion();
		
		File sounds = new File(System.getProperty("user.home") + "/Desktop/Sounds");
		
		File[] possibleDirectories = sounds.listFiles();		// Get all possible folders in sounds folder
		
		try {
			accord.getPanes().add(createPane("Uncategorized", sounds));
			
			for (File f : possibleDirectories) {
				if (f.isDirectory()) {
					accord.getPanes().add(createPane(f.getName(), f));
				}
			}
		} catch (Exception e) {
			System.out.print("\007");
			System.out.flush();
			e.printStackTrace();
		}
		
		root.setCenter(accord);
		root.setBottom(grid_control);
		
	}
	
	/**
	 * Create a new TitledPane
	 * @param paneName Display name of pane
	 * @param folder Directory path for sounds
	 * @return TitledPane containing buttons for each sound located in given folder
	 */
	private TitledPane createPane(String paneName, File folder) {
		FlowPane flow = new FlowPane(10, 10);

		ArrayList<Button> list = createButtonList(folder);
		for (Button btn : list) {
			flow.getChildren().add(btn);
		}
		
		TitledPane returnPane = new TitledPane(paneName, flow);
		return returnPane;
	}
	
	/**
	 * Create a list of buttons containing sounds from given folder
	 * @param folder Directory of sounds
	 * @return ArrayList of buttons
	 */
	private ArrayList<Button> createButtonList(File folder) {
		ArrayList<Button> list = new ArrayList<Button>();
		
		
		
		try {
			File[] files = folder.listFiles();		// Get list of files in folder to iterate through
			
			for (File f : files) {					// Iterate through each file
				if (f.isFile()) {
					list.add(createButton(f));
				}
			}
		} catch (Exception e) {
			System.out.println("Error creating buttons in folder ");
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * Creates a Button that will play a specific sound when pressed
	 * @param path Path to the file that the Button will play
	 * @return Button
	 */
	private Button createButton(File path) {
		Button btn = new Button();
		
		String filename = path.getName();
		String button_text = filename.substring(0, path.getName().indexOf('.'));		// Chop off extension and .
		btn.setText(button_text);
		btn.setId("soundButton");
		
		btn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				try {
					MediaPlayer player = new MediaPlayer(new Media(path.toURI().toString()));	// URI representation of path to media file
					player.setVolume(slider_volume.getValue() / 100);
					player.setRate(slider_rate.getValue());
					player.play();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
			}
		});
		
		return btn;
	}
	
}
