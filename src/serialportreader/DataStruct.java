/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialportreader;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author melvinlyf
 */
public class DataStruct {
    private final SimpleStringProperty timeData = new SimpleStringProperty("");
    private final SimpleStringProperty distData = new SimpleStringProperty("");
    
    public DataStruct() {
        this("","");
    }
    
    public DataStruct(String timeData, String distData) {
        setTimeData(timeData);
        setDistData(distData);
    }
    
    public String getTimeData() {
        return timeData.get();
    }
    
    public void setTimeData(String fTime) {
        timeData.set(fTime);
    }
    
    public String getDistData() {
        return distData.get();
    }
    
    public void setDistData(String fDist) {
        distData.set(fDist);
    }

}
