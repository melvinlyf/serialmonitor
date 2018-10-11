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

class GV {
    //Variable
    public static SerialPort COM_PORT_1 = null;
    public static SerialPort COM_PORT_2 = null;
    
    //Input and Output Streams for Sending and Receiving Data
    public static InputStream input_P1 = null;
    public static OutputStream output_P1 = null;
    public static InputStream input_P2 = null;
    public static OutputStream output_P2 = null;
    
    public static String CurrDist = null;

}

public class FXMLDocumentController implements Initializable {
    
    @FXML ChoiceBox CHOICE_BOX_1;
    @FXML ChoiceBox CHOICE_BOX_2;
    @FXML TableView<DataStruct> TV_1;
    @FXML TableView<DataStruct> TV_2;
    
    ObservableList<Object> myChoiceBoxData = FXCollections.observableArrayList();
    Button OPEN_PORT;
    Label SENSOR_VALUE;

    
//    public static ObservableList <DataBank> Data = 
//            FXCollections.observableArrayList(
//                new DataBank("Field1","Field2"),
//                new DataBank("Field3","Field4"));
   
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
    }
    
    @FXML
    private void handleOpenPort_1(ActionEvent event) throws IOException {
        if(GV.COM_PORT_1!= null) 
        {
            //Open Port
            System.out.println("Initialising Connection to " + GV.COM_PORT_1);
            boolean portStatus = GV.COM_PORT_1.openPort();          
            System.out.println("handleOpenPort Status: " + portStatus + " Port ID: " +GV.COM_PORT_1.getSystemPortName());
            
            //Set Baud Rate
            GV.COM_PORT_1.setBaudRate(38400);
            
            //Init Streams
            GV.input_P1 = GV.COM_PORT_1.getInputStream();
            GV.output_P1 = GV.COM_PORT_1.getOutputStream();
            
            //Init Cal
            DateFormat dateFormat = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss:SSS");

            GV.COM_PORT_1.addDataListener(new SerialPortDataListener(){
                
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
                        char singleData = (char)GV.input_P1.read();
                        
                        if(singleData!=13) //if NotEqual CR detected
                        {
                            if(GV.CurrDist == null)
                                GV.CurrDist=Character.toString(singleData);
                            else
                                GV.CurrDist = GV.CurrDist.concat(Character.toString(singleData));

                            System.out.print(singleData);
                        }
                        else
                        {     
                            Date date = new Date();
                            String cTime = dateFormat.format(date);
                            
                            addData_P1(cTime,GV.CurrDist);
                            
                            GV.CurrDist = null; //reset temp buffer
                            
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
    private void handleOpenPort_2(ActionEvent event) throws IOException {
        if(GV.COM_PORT_2!= null) 
        {
            //Open Port
            System.out.println("Initialising Connection to " + GV.COM_PORT_2);
            boolean portStatus = GV.COM_PORT_2.openPort();          
            System.out.println("handleOpenPort Status: " + portStatus + " Port ID: " +GV.COM_PORT_2.getSystemPortName());
            
            //Set Baud Rate
            GV.COM_PORT_2.setBaudRate(38400);
            
            //Init Streams
            GV.input_P2 = GV.COM_PORT_2.getInputStream();
            GV.output_P2 = GV.COM_PORT_2.getOutputStream();
            
            //Init Cal
            DateFormat dateFormat = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss:SSS");

            GV.COM_PORT_2.addDataListener(new SerialPortDataListener(){
                
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
                        char singleData = (char)GV.input_P2.read();
                        
                        if(singleData!=13) //if NotEqual CR detected
                        {
                            if(GV.CurrDist == null)
                                GV.CurrDist=Character.toString(singleData);
                            else
                                GV.CurrDist = GV.CurrDist.concat(Character.toString(singleData));

                            System.out.print(singleData);
                        }
                        else
                        {     
                            Date date = new Date();
                            String cTime = dateFormat.format(date);
                            
                            addData_P2(cTime,GV.CurrDist);
                            
                            GV.CurrDist = null; //reset temp buffer
                            
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
    private void handleClosePort_1(ActionEvent event) {
        if(GV.COM_PORT_1!= null) 
        {

            boolean portStatus = GV.COM_PORT_1.closePort();

            
            System.out.println("handleClosePort Status: " + portStatus + " Port ID: " +GV.COM_PORT_1.getSystemPortName());
        }
        else
        {
            System.out.println("Close Port Button Error!, No device selected!");
        }
    }
    
    @FXML
    private void handleClosePort_2(ActionEvent event) {
        if(GV.COM_PORT_2!= null) 
        {

            boolean portStatus = GV.COM_PORT_2.closePort();

            
            System.out.println("handleClosePort Status: " + portStatus + " Port ID: " +GV.COM_PORT_2.getSystemPortName());
        }
        else
        {
            System.out.println("Close Port Button Error!, No device selected!");
        }
    }
          
    @FXML
    protected void addData_P1 (String tTime, String tDist){
        ObservableList<DataStruct> dataStore = TV_1.getItems();
        dataStore.add(new DataStruct(tTime,tDist));
    }
    
    @FXML
    protected void addData_P2 (String tTime, String tDist){
        ObservableList<DataStruct> dataStore = TV_2.getItems();
        dataStore.add(new DataStruct(tTime,tDist));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        myChoiceBoxData.addAll((Object[])SerialPort.getCommPorts());
        
        CHOICE_BOX_1.getItems().clear();
        CHOICE_BOX_2.getItems().clear();

        CHOICE_BOX_1.setItems(myChoiceBoxData);
        CHOICE_BOX_2.setItems(myChoiceBoxData);

        CHOICE_BOX_1.setOnAction((event) -> {          
            GV.COM_PORT_1=SerialPort.getCommPorts()[CHOICE_BOX_1.getSelectionModel().getSelectedIndex()];
            System.out.println("ChoiceBox1 Action: " + GV.COM_PORT_1);
        });
        
        CHOICE_BOX_2.setOnAction((event) -> {          
            GV.COM_PORT_2=SerialPort.getCommPorts()[CHOICE_BOX_2.getSelectionModel().getSelectedIndex()];
            System.out.println("ChoiceBox2 Action: " + GV.COM_PORT_2);
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

