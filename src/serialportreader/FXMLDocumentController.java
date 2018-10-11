/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialportreader;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import com.fazecast.jSerialComm.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

/**
 *
 * @author melvinlyf
 */
class GloVar {
    public static SerialPort COM_PORT = null;
    
    //Input and Output Streams for Sending and Receiving Data
    public static InputStream input = null;
    public static OutputStream output = null;
    
    public static String CurrDist = null;

}

public class FXMLDocumentController implements Initializable {
    
    @FXML 
    ChoiceBox choice_box;
    ObservableList<Object> myChoiceBoxData = FXCollections.observableArrayList();
    Button OPEN_PORT;
    Label SENSOR_VALUE;
    @FXML TableView<DataStruct> tableView;
    
    public static ObservableList <DataBank> Data = 
            FXCollections.observableArrayList(
                new DataBank("Field1","Field2"),
                new DataBank("Field3","Field4"));
   
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
    }
    
    @FXML
    private void handleOpenPort(ActionEvent event) throws IOException {
        if(GloVar.COM_PORT!= null) 
        {
            //Open Port
            System.out.println("Initialising Connection to " + GloVar.COM_PORT);
            boolean portStatus = GloVar.COM_PORT.openPort();          
            System.out.println("handleOpenPort Status: " + portStatus + " Port ID: " +GloVar.COM_PORT.getSystemPortName());
            
            //Set Baud Rate
            GloVar.COM_PORT.setBaudRate(38400);
            
            //Init Streams
            GloVar.input = GloVar.COM_PORT.getInputStream();
            GloVar.output = GloVar.COM_PORT.getOutputStream();
            
            //Init Cal
            DateFormat dateFormat = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss:SSS");

            GloVar.COM_PORT.addDataListener(new SerialPortDataListener(){
                
                @Override
                public int getListeningEvents() 
                {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }
                
                @Override
                public void serialEvent(SerialPortEvent event)
                {
                    //If no data avail, exit function
                    if(event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                        return;  
                    
                    try {
                        char singleData = (char)GloVar.input.read();
                        
                        if(singleData!=13) //if NotEqual CR detected
                        {
                            if(GloVar.CurrDist == null)
                                GloVar.CurrDist=Character.toString(singleData);
                            else
                                GloVar.CurrDist = GloVar.CurrDist.concat(Character.toString(singleData));

                            System.out.print(singleData);
                        }
                        else
                        {     
                            Date date = new Date();
                            String cTime = dateFormat.format(date);
                            
                            addData(cTime,GloVar.CurrDist);
                            
                            GloVar.CurrDist = null; //reset temp buffer
                            

                            //Data.add(new DataBank(cTime,GloVar.CurrDist));

                            
                            System.out.print("    " + cTime);
                            System.out.println("");
                        }
                        
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }


                }
            });
        }
    }
            
    @FXML
    private void handleClosePort(ActionEvent event) {
        if(GloVar.COM_PORT!= null) 
        {

            boolean portStatus = GloVar.COM_PORT.closePort();

            
            System.out.println("handleClosePort Status: " + portStatus + " Port ID: " +GloVar.COM_PORT.getSystemPortName());
        }
        else
        {
            System.out.println("Close Port Button Error!, No device selected!");
        }
    }
          
    @FXML
    protected void addData (String tTime, String tDist){
        ObservableList<DataStruct> dataStore = tableView.getItems();
        dataStore.add(new DataStruct(tTime,tDist));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        myChoiceBoxData.addAll((Object[])SerialPort.getCommPorts());
        
        choice_box.getItems().clear();
        choice_box.setItems(myChoiceBoxData);
        
        choice_box.setOnAction((event) -> {          
            GloVar.COM_PORT=SerialPort.getCommPorts()[choice_box.getSelectionModel().getSelectedIndex()];
            System.out.println("ChoiceBox Action: " + GloVar.COM_PORT);
        });
        
    }
    
}
    class DataBank {
        private final SimpleStringProperty TIME_STAMP;
        private final SimpleStringProperty DIST_VALUE;
        
        DataBank(String tStamp, String dValue) {
            this.TIME_STAMP = new SimpleStringProperty(tStamp);
            this.DIST_VALUE = new SimpleStringProperty(dValue);
        }
        
        public String getTimeStamp() {
            return TIME_STAMP.get();
        }
        
        public void setTimeStamp(String tStamp) {
            TIME_STAMP.set(tStamp);
        }
        
        public String getDistValue() {
            return DIST_VALUE.get();
        }
        
        public void setDistValue(String dValue) {
            DIST_VALUE.set(dValue);
        }
    }

