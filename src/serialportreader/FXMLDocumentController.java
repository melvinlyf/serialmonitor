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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public static InputStream P1_Input = null;
    public static OutputStream P1_Output = null;
    public static String P1_Buff = null;
    public static int P1_ValidCount = 0;
    public static int P1_InvalidCount = 0;
    public static long P1_PrevTime = 0;
    public static long P1_CurrTime = 0;
    public static int P1_SecondsElapsed = 0;
    public static String P1_FilePath = null;    
    
    public static InputStream P2_Input = null;
    public static OutputStream P2_Output = null;
    public static String P2_Buff = null;
    public static int P2_ValidCount = 0;
    public static int P2_InvalidCount = 0;    
    public static long P2_PrevTime = 0;
    public static long P2_CurrTime = 0;
    public static int P2_SecondsElapsed = 0;
    public static String P2_FilePath = null;
    
    public static BufferedWriter P1_BW = null;
    public static BufferedWriter P2_BW = null;
}

public class FXMLDocumentController implements Initializable {
    
    @FXML ChoiceBox CHOICE_BOX_1;
    @FXML ChoiceBox CHOICE_BOX_2;
    @FXML TableView<DataStruct> TV_1;
    @FXML TableView<DataStruct> TV_2;
    
    ObservableList<Object> myChoiceBoxData = FXCollections.observableArrayList(); 

@FXML
private void handleOpenPort_1(ActionEvent event) throws IOException {
    if(GV.COM_PORT_1!= null) 
    {
        
        //Open Port
        System.out.println("Initialising Connection to " + GV.COM_PORT_1);   
        System.out.println("handleOpenPort Status: " + GV.COM_PORT_1.openPort() + " Port ID: " +GV.COM_PORT_1.getSystemPortName());
        
        GV.COM_PORT_1.setBaudRate(57600);   //Set Baud Rate

        //Init Streams
        GV.P1_Input = GV.COM_PORT_1.getInputStream();
        GV.P1_Output = GV.COM_PORT_1.getOutputStream();

        //Init Cal
        DateFormat dateFormat = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss:SSS");

        GV.P1_BW = new BufferedWriter(new FileWriter(GV.P1_FilePath));
        GV.P1_BW.write("COM Port 1:");
        GV.P1_BW.newLine();
        
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
                    char singleData = (char)GV.P1_Input.read();

                    if(singleData!=13) //if NotEqual CR detected
                    {
                        if(GV.P1_Buff == null)
                            GV.P1_Buff=Character.toString(singleData);
                        else
                            GV.P1_Buff = GV.P1_Buff.concat(Character.toString(singleData));
                    }
                    else
                    {     
                        LocalTime timestamp = LocalTime.now();
                        String cTime = String.valueOf(timestamp.toNanoOfDay());
                        System.out.println(String.valueOf(timestamp.getHour()) 
                                + ":" + String.valueOf(timestamp.getMinute())
                                + ":" + String.valueOf(timestamp.getSecond())
                                + "." + String.valueOf(timestamp.getNano()));
                        
                        P1_AddData(cTime,GV.P1_Buff);

                        if(isInteger(GV.P1_Buff)) {
                            GV.P1_ValidCount++;
                        }
                        else {
                            GV.P1_InvalidCount++;
                        }
                        GV.P1_BW.write(cTime + ";"+ GV.P1_Buff);
                        GV.P1_BW.newLine();
                        
                        GV.P1_CurrTime = System.currentTimeMillis();
                        if(GV.P1_CurrTime > (GV.P1_PrevTime + 1000))
                        {
                            GV.P1_PrevTime=GV.P1_CurrTime;
                            GV.P1_SecondsElapsed++;
                            System.out.println("Port 1 Valid Data: " + GV.P1_ValidCount + " ; Invalid Data: " 
                                    + GV.P1_InvalidCount + " ; " +GV.P1_SecondsElapsed + " second has passed");
                            GV.P1_ValidCount = 0;
                            GV.P1_InvalidCount = 0;
                        }
                        GV.P1_Buff = null; //reset temp buffer
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
        System.out.println("handleOpenPort Status: " + GV.COM_PORT_2.openPort()  + " Port ID: " +GV.COM_PORT_2.getSystemPortName());

        //Set Baud Rate
        GV.COM_PORT_2.setBaudRate(57600);

        //Init Streams
        GV.P2_Input = GV.COM_PORT_2.getInputStream();
        GV.P2_Output = GV.COM_PORT_2.getOutputStream();

        //Init Cal
        DateFormat dateFormat = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss:SSS");

        GV.P2_BW = new BufferedWriter(new FileWriter(GV.P2_FilePath));
        GV.P2_BW.write("COM Port 2:");
        GV.P2_BW.newLine();
        
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
                    char singleData = (char)GV.P2_Input.read();

                    if(singleData!=13) //if NotEqual CR detected
                    {
                        if(GV.P2_Buff == null)
                            GV.P2_Buff=Character.toString(singleData);
                        else
                            GV.P2_Buff = GV.P2_Buff.concat(Character.toString(singleData));

                        //System.out.print(singleData);
                    }
                    else
                    {                           
                        Date date = new Date();
                        String cTime = dateFormat.format(date);
                        
                        cTime = dateFormat.format(System.currentTimeMillis());
                        
                        P2_AddData(cTime,GV.P2_Buff);

                        if(isInteger(GV.P2_Buff))
                        {
                            GV.P2_ValidCount++;
                        }
                        else
                        {
                            GV.P2_InvalidCount++;
                        }
                        GV.P2_BW.write(cTime + ";"+ GV.P2_Buff);
                        GV.P2_BW.newLine();
                        
                        GV.P2_CurrTime = System.currentTimeMillis();
                        if(GV.P2_CurrTime > (GV.P2_PrevTime + 1000))
                        {
                            GV.P2_PrevTime=GV.P2_CurrTime;
                            GV.P2_SecondsElapsed++;
                            System.out.println("Port 2 Valid Data: " + GV.P2_ValidCount + " ; Invalid Data: " 
                                    + GV.P2_InvalidCount + " ; " +GV.P2_SecondsElapsed + " second has passed");
                            GV.P2_ValidCount = 0;
                            GV.P2_InvalidCount = 0;
                        }
                        GV.P2_Buff = null; //reset temp buffer
                    }

                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }//end of event
        });
    }
}
    
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * FXML Function: @FXML private void handleClosePort_X(ActionEvent event)      *  
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Author: Melvin Lian      Revision : 0                                       *
 * Comments:                                                                   *
 * - where (X) refers to the Device ID (i.e. 1, 2...)                          *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */   
@FXML
private void handleClosePort_1(ActionEvent event) {
    if(GV.COM_PORT_1!= null) 
    {
        GV.COM_PORT_1.removeDataListener();
        System.out.println("handleClosePort Status: " + GV.COM_PORT_1.closePort() + " Port ID: " +GV.COM_PORT_1.getSystemPortName());
        
        try {
            if(GV.P1_BW != null)
            {
                GV.P1_BW.flush();
                GV.P1_BW.close();
            }
        }
        catch (IOException ex)
        {
        }  
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
        GV.COM_PORT_2.removeDataListener();
        System.out.println("handleClosePort Status: " + GV.COM_PORT_2.closePort() + " Port ID: " +GV.COM_PORT_2.getSystemPortName());
        try {
            if(GV.P2_BW != null)
            {
                GV.P2_BW.flush();
                GV.P2_BW.close();
            }
        }
        catch (IOException ex)
        {
        }  
    }
    else
    {
        System.out.println("Close Port Button Error!, No device selected!");
    }
}


public static boolean isInteger(String str) {
    if (str == null) {
        return false;
    }
    
    if (str.isEmpty()) {
        return false;
    }
    
    int i = 0;
    int length = str.length();
    
    if(str.charAt(0) == '-') {
        if(length == 1) {
            return false;
        }
        i = 1;
    }
    for( ; i < length ; i++) {
        char c = str.charAt(i);
        if (c <= '/' || c >= ':') {
            return false;
        }
    }
    return true;
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Function: @FXML protected void P(X)_AddData (String tTime, String tDist)    *  
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Author: Melvin Lian      Revision : 0                                       *
 * Comments:                                                                   *
 * - where (X) refers to the Device ID (i.e. P1, P2...)                        *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */         
@FXML
protected void P1_AddData (String tTime, String tDist){
    ObservableList<DataStruct> dataStore = TV_1.getItems();
    dataStore.add(new DataStruct(tTime,tDist));
}

@FXML
protected void P2_AddData (String tTime, String tDist){
    ObservableList<DataStruct> dataStore = TV_2.getItems();
    dataStore.add(new DataStruct(tTime,tDist));
}
    
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Function: public void initialize(URL url, ResourceBundle rb)                *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Author: Melvin Lian      Revision : 0                                       *
 * Comments: New Entity                                                        *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
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
    
    String WorkingDir = System.getProperty("user.dir");
    System.out.println("Working Directory = " + WorkingDir);
    GV.P1_FilePath = WorkingDir + File.separator + "PortOneLog.txt";
    GV.P2_FilePath = WorkingDir + File.separator + "PortTwoLog.txt";
    System.out.println("Log 1 File Directory =" +GV.P1_FilePath);
    System.out.println("Log 2 File Directory =" +GV.P2_FilePath);

}
    
} //end of main class

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Databank Class                                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Author: Melvin Lian      Revision : 0                                       *
 * Comments: New Entity                                                        *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
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