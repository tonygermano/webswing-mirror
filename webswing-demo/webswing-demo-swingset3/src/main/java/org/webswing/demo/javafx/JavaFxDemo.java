package org.webswing.demo.javafx;

import com.sun.swingset3.DemoProperties;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@DemoProperties(value = "JavaFx", category = "Webswing", description = "Demonstrates embeding JavaFx components", sourceFiles = { "org/webswing/demo/javafx/JavaFxDemo.java" })
public class JavaFxDemo extends JPanel {

	public JavaFxDemo() {
		setLayout(new BorderLayout());
		final JFXPanel fxPanel = new JFXPanel();
		add(fxPanel,BorderLayout.NORTH);
		final Scene scene2 = createSceneWithSwingNode();
		fxPanel.setScene(scene2);
		final Scene scene = createJFXScene();


		final JButton toggle =new JButton("toggle SwingNode visibility");
		toggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fxPanel.getScene()==scene){
					fxPanel.setScene(scene2);
				}else{
					fxPanel.setScene(scene);
				}

			}
		});
		add(toggle,BorderLayout.SOUTH);

		Platform.setImplicitExit(false);
	}


	private static Scene createJFXScene() {
		Scene scene = new Scene(createContent(), 500, 300, Color.ALICEBLUE);
		return (scene);
	}

	private static Scene createSceneWithSwingNode() {
		BorderPane pane = new BorderPane();
		Group root = new Group();
		Scene scene = new Scene(pane, 500, 300, Color.ALICEBLUE);
		Text text = new Text();
		text.setX(40);
		text.setY(100);
		text.setFont(new Font(25));
		text.setText("Welcome JavaFX!");
		root.getChildren().add(text);

		final SwingNode swingNode = new SwingNode();
		pane.setTop(swingNode);
		pane.setCenter(root);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				swingNode.setContent(new ButtonHtml());
			}

		});
		return (scene);
	}
	public static Parent createContent() {
		final ColorPicker colorPicker = new ColorPicker(Color.GREEN);
		final Label coloredText = new Label("Colors");
		Font font = new Font(51);
		coloredText.setFont(font);
		final Button coloredButton = new Button("Colored Control");
		Color c = colorPicker.getValue();
		coloredText.setTextFill(c);
		coloredButton.setStyle(createRGBString(c));

		colorPicker.setOnAction(new EventHandler() {
			@Override public void handle(Event t) {
				Color newColor = colorPicker.getValue();
				coloredText.setTextFill(newColor);
				coloredButton.setStyle(createRGBString(newColor));
			}
		});

		VBox outerVBox = new VBox(coloredText, coloredButton, colorPicker);
		outerVBox.setAlignment(Pos.CENTER);
		outerVBox.setSpacing(20);
		outerVBox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);

		return outerVBox;
	}

	private static String createRGBString(Color c) {
		return "-fx-base: rgb(" + (c.getRed() * 255) + "," + (c.getGreen() * 255) + "," + (c.getBlue() * 255) + ");";
	}

	public static void main(String[] args) {
		DemoFx.main(args);
	}

	public static class DemoFx extends Application {
		public DemoFx(){
			super();
		}

		@Override
		public void start(Stage stage) {
			stage.setScene(createSceneWithSwingNode());
			stage.show();
		}

		public static void main(String[] args) {
			Application.launch(args);
		}
	}
}
