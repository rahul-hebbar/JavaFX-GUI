package sertel_javafx_better;
	
import java.util.concurrent.TimeUnit;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.TickLabelLocation;
import eu.hansolo.medusa.TickLabelOrientation;
import eu.hansolo.medusa.TickMarkType;
import eu.hansolo.medusa.Gauge.KnobType;
import eu.hansolo.medusa.Gauge.NeedleBehavior;
import eu.hansolo.medusa.Gauge.NeedleShape;
import eu.hansolo.medusa.Gauge.NeedleType;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.skins.SimpleDigitalSkin;
import eu.hansolo.medusa.skins.SlimSkin;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;



public class Main extends Application {
	static SerialPort serialPort;
	static byte[] packet;
	static Stage window;
	static Scene sc1;
	public static String val;
	static SerialPort serial;
	static Gauge temp_g;
	static Gauge pre_g;
	static Gauge hum_g;
	static Gauge winsp_g;
	static Gauge windir_g;
	static Label windir_lb;
	static Label lat_lab;
	static Label long_lab;
	static Label alti_lab;
	static Label pak_lab;
	static ChoiceBox<String> winsp_c;
	static ChoiceBox<String> temp_c;
	static ChoiceBox<String> pre_c;
	static ChoiceBox<String> hum_c;
	static String val_flag = null;
	
	@Override
	public void start(Stage primaryStage) throws Exception{
		window = primaryStage;
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		
		//initial tiles(children of grid)
		VBox tit_root = new VBox();
		VBox temp_root = new VBox(30);
		VBox pre_root = new VBox(30);
		VBox hum_root = new VBox(30);
		HBox gps_root = new HBox(20);
		VBox pak_root = new VBox(5);
		VBox windir_root = new VBox(30);
		VBox winsp_root = new VBox(30);
		VBox logo_root = new VBox();
		
		//final tiles(children of parent_root)
		GridPane grid = new GridPane();
		//final pane on stage
 		VBox parent_root = new VBox();
		
		// Menu
		Menu mn = new Menu("Settings");
		MenuItem setting = new MenuItem("Port Selection");
		mn.getItems().add(setting);
		MenuBar mb = new MenuBar();
		mb.setPrefWidth(primaryScreenBounds.getWidth());
		mb.getMenus().add(mn);
		
		//Title
		Text tit_tx = new Text("Ground Reference");
		tit_tx.setId("tittx");
		
		// Temperature
		Text temp_tx = new Text();
		temp_tx.setText("Temperature");
		temp_tx.setId("temptx");
		temp_g = new Gauge();
		temp_g.setSkin(new SlimSkin(temp_g));
		temp_g.setPrefSize(200, 200);
		temp_g.setForegroundBaseColor(Color.CYAN);
		temp_g.setBarBackgroundColor(Color.GREY);
		temp_g.setBarColor(Color.CYAN);
		temp_g.setAnimated(true);
		temp_g.setAnimationDuration(500);
		String temp_st[] = {"C","F"};
		temp_c = new ChoiceBox<>(FXCollections.observableArrayList(temp_st));
		temp_c.getSelectionModel().selectFirst();
		temp_g.setUnit(temp_st[0]);
		temp_c.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue ov, Number value, Number new_value) {
				temp_g.setUnit(temp_st[new_value.intValue()]);			
			}	
		});
//		Button temp_but = new Button("");
		
		//Pressure
		Text pres_tx = new Text();
		pres_tx.setText("Pressure");
		pres_tx.setId("temptx");
		pre_g = new Gauge();
		pre_g.setSkin(new SlimSkin(pre_g));
		pre_g.setPrefSize(200, 200);
		pre_g.setForegroundBaseColor(Color.CYAN);
		pre_g.setBarBackgroundColor(Color.GREY);
		pre_g.setBarColor(Color.CYAN);
		pre_g.setAnimated(true);
		pre_g.setAnimationDuration(500);
		String pre_st[] = {"hPa","bar"};
		pre_c = new ChoiceBox<>(FXCollections.observableArrayList(pre_st));
		pre_c.getSelectionModel().selectFirst();
		pre_g.setUnit(pre_st[0]);
		pre_c.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue ov, Number value, Number new_value) {
				pre_g.setUnit(pre_st[new_value.intValue()]);			
			}	
		});
//		Button pres_but = new Button();
		
		//Humidity
		Text hum_tx = new Text();
		hum_tx.setText("Humidity");
		hum_tx.setId("temptx");
		hum_g = new Gauge();
		hum_g.setSkin(new SlimSkin(hum_g));
		hum_g.setMaxSize(200,200);
		hum_g.setForegroundBaseColor(Color.CYAN);
		hum_g.setBarBackgroundColor(Color.GREY);
		hum_g.setBarColor(Color.CYAN);
		hum_g.setAnimated(true);
		hum_g.setAnimationDuration(500);
		String hum_st[] = {"RH"};
		hum_c = new ChoiceBox<>(FXCollections.observableArrayList(hum_st));
		hum_c.getSelectionModel().selectFirst();
		hum_g.setUnit(hum_st[0]);
		hum_c.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue ov, Number value, Number new_value) {
				hum_g.setUnit(hum_st[new_value.intValue()]);			
			}	
		});
//		Button hum_but = new Button();

		
		//GPS,altitude
		Text gps_tx = new Text();
		gps_tx.setText("GPS");
		gps_tx.setId("temptx");
		Text lat_tx = new Text();
		lat_tx.setText("Latitude");
		lat_tx.setId("temptx");
		Text long_tx = new Text();
		long_tx.setText("Longitude");
		long_tx.setId("temptx");
		Text alti_tx = new Text();
		alti_tx.setText("Altitude");
		alti_tx.setId("temptx");
		
		lat_lab = new Label();
		lat_lab.setId("latlab");
		lat_lab.setPrefSize(120, 25);
		long_lab = new Label();
		long_lab.setId("latlab");
		long_lab.setPrefSize(120, 25);
		alti_lab = new Label();
		alti_lab.setId("latlab");
		alti_lab.setPrefSize(100, 25);
		
		//Wind speed,Wind direction
		Text windir_tx = new Text();
		windir_tx.setText("Wind Direction");
		windir_tx.setId("temptx");
		Text winsp_tx = new Text();
		winsp_tx.setText("Wind Speed");
		winsp_tx.setId("temptx");
		
		windir_lb = new Label();
		windir_lb.setId("latlab");
		windir_lb.setPrefSize(100, 50);
		
		windir_g = GaugeBuilder.create()
				.skinType(SkinType.GAUGE)
                .prefSize(180, 180)
                .borderPaint(Gauge.DARK_COLOR)
                .minValue(0)
                .maxValue(360)
                .startAngle(180)
                .angleRange(360)
                .autoScale(false)
                .minorTickMarksVisible(false)
                .mediumTickMarksVisible(false)
                .majorTickMarksVisible(true)
                .majorTickMarkType(TickMarkType.TRIANGLE)
                .customTickLabelsEnabled(true)
                .tickLabelLocation(TickLabelLocation.OUTSIDE)
                .tickLabelOrientation(TickLabelOrientation.ORTHOGONAL)
                .minorTickSpace(1)
                .majorTickSpace(25)
                .customTickLabels("N","E","S","W")
                .customTickLabelFontSize(25)
                .knobType(KnobType.FLAT)
                .knobColor(Gauge.DARK_COLOR)
                .needleShape(NeedleShape.FLAT)
                .needleType(NeedleType.VARIOMETER)
                .needleBehavior(NeedleBehavior.OPTIMIZED)
                .tickLabelColor(Gauge.DARK_COLOR)
                .animated(true)
                .animationDuration(500)
                .valueVisible(false)
                .layoutX(780)
                .layoutY(100)
                .build();
		
		winsp_g = new Gauge();
		winsp_g.setSkin(new SimpleDigitalSkin(winsp_g));
		winsp_g.setPrefSize(200, 200);
		winsp_g.setForegroundBaseColor(Color.CYAN);
		winsp_g.setBarBackgroundColor(Color.GREY);
		winsp_g.setBarColor(Color.CYAN);
		winsp_g.setLayoutX(970);
		winsp_g.setLayoutY(100);
		winsp_g.setAnimated(true);
		winsp_g.setAnimationDuration(500);
		String winsp_st[] = {"m/s","knots"};
		winsp_c = new ChoiceBox<>(FXCollections.observableArrayList(winsp_st));
		winsp_c.getSelectionModel().selectFirst();
		winsp_g.setUnit(winsp_st[0]);
		winsp_c.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue ov, Number value, Number new_value) {
				winsp_g.setUnit(winsp_st[new_value.intValue()]);			
			}	
		});
		
		//logo
		Label logo = new Label();
		Image img = new Image(getClass().getResourceAsStream("logo.png"));
		ImageView imgv = new ImageView(img);
		imgv.setFitHeight(logo_root.getPrefHeight()-10);
		imgv.setFitWidth(logo_root.getPrefWidth()-10);
		logo.setGraphic(imgv);
		
		// Packet
		Text pak_tx = new Text();
		pak_tx.setText("Packet");
		pak_tx.setId("temptx");
		pak_lab = new Label();
		pak_lab.setId("paklab");
		pak_lab.setPrefSize(750, 15);
		
		//Menu bar action
		setting.setOnAction(e -> {
			port_selec.display();
			val = port_selec.sel_choice;
		});
		
		//Stages
		temp_root.setId("temprt");
		pre_root.setId("temprt");
		hum_root.setId("temprt");
		pak_root.setId("temprt");
		windir_root.setId("temprt");
		winsp_root.setId("temprt");
		logo_root.setId("temprt");
		tit_root.setId("temprt");
		tit_root.getChildren().add(tit_tx);
		temp_root.getChildren().addAll(temp_tx,temp_g,temp_c);
		pre_root.getChildren().addAll(pres_tx,pre_g,pre_c);
		hum_root.getChildren().addAll(hum_tx,hum_g,hum_c);
		gps_root.getChildren().addAll(gps_tx,lat_tx,lat_lab,long_tx,long_lab,alti_tx,alti_lab);
		windir_root.getChildren().addAll(windir_tx,windir_g,windir_lb);
		winsp_root.getChildren().addAll(winsp_tx,winsp_g,winsp_c);
		logo_root.getChildren().add(logo);
		pak_root.getChildren().addAll(gps_root,pak_tx,pak_lab);
		tit_root.setAlignment(Pos.CENTER);
		temp_root.setAlignment(Pos.CENTER);
		pre_root.setAlignment(Pos.CENTER);
		hum_root.setAlignment(Pos.CENTER);
		gps_root.setAlignment(Pos.CENTER);
		pak_root.setAlignment(Pos.CENTER);
		windir_root.setAlignment(Pos.CENTER);
		winsp_root.setAlignment(Pos.CENTER);
		logo_root.setAlignment(Pos.CENTER);
		
		final int numCols = 5;
        final int numRows = 8;
        for (int i = 0; i < numCols; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / numCols);
            grid.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / numRows);
            grid.getRowConstraints().add(rowConst);         
        }
		grid.setId("root");
		grid.setVgap(5);
		grid.setHgap(5);
		grid.prefHeightProperty().bind(parent_root.heightProperty());
		grid.prefWidthProperty().bind(parent_root.widthProperty());
		grid.add(tit_root, 0,0,5,1);
		grid.add(temp_root, 0, 1,1,5);
		grid.add(pre_root, 1, 1,1,5);
		grid.add(hum_root, 2, 1,1,5);
		grid.add(pak_root, 0,6,3,3);
		grid.add(windir_root, 3, 1,1,5);
		grid.add(winsp_root, 4, 1,1,5);
		grid.add(logo_root, 3,6,2,3);
			
		//Scene
		parent_root.getChildren().addAll(mb,grid);
		Scene scene = new Scene(parent_root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		window.setTitle("Sertel");
		window.setScene(scene);
		window.show();
		window.setX(primaryScreenBounds.getMinX());
        window.setY(primaryScreenBounds.getMinY());
        window.setWidth(primaryScreenBounds.getWidth());
        window.setHeight(primaryScreenBounds.getHeight());
        window.setOnCloseRequest(e -> {
        	Platform.exit();
        	System.exit(0);
        });
		}
	
	public static void main(String[] args) {
		Thread th = new Thread(new Main().new ChildThread());
		th.start();	
		launch(args);
	}
	
	class ChildThread extends Thread 
	{ 
	    @Override
	    public void run()  
	    { 	
	    	while(true) {
	    		//necessary delay
	    		try {
					TimeUnit.MILLISECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    		//establish serial connection 
		    	if(val != null && val != val_flag) {
		    		try {
		    			val_flag = val;
		    			//connect port 
		    	    	serialPort = new SerialPort(val);
		    	        serialPort.openPort();//Open port
		    	        serialPort.setParams(19200, 8, 1, 0);//Set params
		    	      //confirm connection
		    	    	Platform.runLater(new Runnable() {
		      			      @Override public void run() {
		      			           Alert conn_alert = new Alert(AlertType.INFORMATION);
		      			           conn_alert.setHeaderText("Connection Successfully");
		      			           conn_alert.setContentText("Connected to selected port successfully");
		      			           conn_alert.showAndWait();
		      			           }
		                		});
		    	        serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener
		    	    }
		    	    catch (SerialPortException ex) {
		    	    	//connection to port failed
		    	    	Platform.runLater(new Runnable() {
		      			      @Override public void run() {
		      			           Alert conn_alert = new Alert(AlertType.ERROR);
		      			           conn_alert.setHeaderText("Could'nt connect to selected Port");
		      			           conn_alert.setContentText("Please check device connection and port number");
		      			           conn_alert.showAndWait();
		      			           }
		                		});
		    	    }
		    	}
	    	}
	    } 
	}
	
	static class SerialPortReader implements SerialPortEventListener {

	    public void serialEvent(SerialPortEvent event) {
	    	//serial data event listener
	        if(event.isRXCHAR() && event.getEventValue()>0 && val != null){
	                try {
	                    packet = serialPort.readBytes(120);
	                    if(packet[1] == 'Q') {
	                    String par = new String(packet);
	                    String[] params = par.split(",");
	                    System.out.println(par);
	                    System.out.println(params[9] + params[1]);
	                    if(params.length >12) {
		                    if(params[1] != null && params[2] !=null && params[5] != null && params[6] != null && params[7] != null && params[9] != null) {
		                    double temp_val = Double.valueOf(params[7]);
		                    double hum_val = Double.valueOf(params[6]);
		                    double pre_val = Double.valueOf(params[5]);
		                    double winsp_val = Double.valueOf(params[2]);
		                    double windir_val = Double.valueOf(params[1])*100/360;
		                    Platform.runLater(new Runnable() {
			      			      @Override public void run() {
			      			    	  if(temp_g.getUnit() == "C") {
			      			    		  temp_g.setValue(temp_val);
			      			    		  temp_g.setMaxValue(60);
			      			    		  temp_g.setMinValue(-90);
			      			    	  }
			      			    	  if(temp_g.getUnit() == "F") {
			      			    		  temp_g.setValue((temp_val*9/5)+32);
			      			    		  temp_g.setMaxValue(140);
			      			    		  temp_g.setMinValue(-130);
			      			    	  }
			      			    	  if(pre_g.getUnit() == "hPa") {
			      			    		  pre_g.setValue(pre_val);
			      			    		  pre_g.setMaxValue(1070);
			      			    		  pre_g.setMinValue(850);
			      			    	  }
			      			          if(pre_g.getUnit()=="bar") pre_g.setValue(pre_val);
			      			          if(hum_g.getUnit() == "RH") hum_g.setValue(hum_val);
			      			          if(hum_g.getUnit() == "") hum_g.setValue(hum_val);
			      			          if(winsp_g.getUnit() == "knots") winsp_g.setValue(winsp_val);
			      			          if(winsp_g.getUnit() == "m/s")winsp_g.setValue(winsp_val); 
			      			          if(windir_val<101)windir_g.setValue(windir_val); 
			      			          String[] gps_val = params[9].split(":");
			      			          lat_lab.setText(gps_val[0]);
			      			          long_lab.setText(gps_val[1]);
			      			          alti_lab.setText(gps_val[2]);
			      			          pak_lab.setText(par);
			      			          if(windir_val == 0 || windir_val == 100) windir_lb.setText(params[1] + " N");
			      			          if(windir_val == 25) windir_lb.setText(params[1] + " E");
			      			          if(windir_val == 50) windir_lb.setText(params[1] + " S");
			      			          if(windir_val == 75) windir_lb.setText(params[1] + " W");
			      			          if(windir_val< 25 && windir_val > 0) windir_lb.setText(params[1] + " NE");
			      			          if(windir_val< 50 && windir_val > 25) windir_lb.setText(params[1] + " SE");
			      			          if(windir_val< 75 && windir_val > 50) windir_lb.setText(params[1] + " SW");
			      			          if(windir_val< 100 && windir_val > 75) windir_lb.setText(params[1] + " NW");
			      			           }
			                		});
		                         }
		                    }
	                    }
	                }
	                catch (SerialPortException ex) {
	                	Platform.runLater(new Runnable() {
	      			      @Override public void run() {
	      			           Alert data_alert = new Alert(AlertType.ERROR);
	      			           data_alert.setHeaderText("Found no incoming data");
	      			           data_alert.setContentText("Device not transmitting data");
	      			           data_alert.showAndWait();
	      			           }
	                		});
	                	val = null;
	                }
	        }
	    }
	}
}



