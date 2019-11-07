package sertel_javafx_better;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jssc.SerialPortList;


public class port_selec {
	
	static String choice;
	public static String sel_choice = null;
	
	public static void display() {  
		Stage window = new Stage();
		Text porttx = new Text("Port");
		porttx.setId("porttx");
		Button connect = new Button("Yes");
		ObservableList<String> portNames = FXCollections.observableArrayList(SerialPortList.getPortNames());
		ChoiceBox<String> ports = new ChoiceBox<>(portNames);
		choice = null;
		ports.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue ov, Number value, Number new_value) {
				ports.getSelectionModel().select(new_value.intValue());
				choice = portNames.get(new_value.intValue());		
			}	
		});
		connect.setOnAction(e -> {
			sel_choice = choice;
			window.close();
		});
		VBox layout = new VBox(40,porttx,ports,connect);
		layout.setAlignment(Pos.CENTER);
		Scene scene = new Scene(layout,200,200);
		scene.getStylesheets().add(port_selec.class.getResource("temp.css").toExternalForm());
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Port Selection");
		window.setScene(scene);
		window.showAndWait();	
	}
}
