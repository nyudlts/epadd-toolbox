package edu.nyu.dlts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class EPADDSwitchController {
    
    private String propertiesLocation;
    private EPADDProperties properties = new EPADDProperties();
    private ObservableList<String> modeValues = FXCollections.observableArrayList("appraisal", "processing", "delivery", "discovery");
    
    @FXML
    protected void initialize() {
        String username = System.getProperty("user.name");
        propertiesLocation = "C:\\Users\\" + username + "\\epadd.properties";
        Boolean propsExists = new File(propertiesLocation).exists();
        if(propsExists == false) {
            output.append("* properties files: " + propertiesLocation + " does not exist.\n");
            resultOutput.setText(output.toString());    
        } else {
            output.append("* properties file exists: " + propertiesLocation+ "\n");
            resultOutput.setText(output.toString());
            Exception e = readProperties();
            if(e != null) {
                output.append("* error loading properties file:"+  e.getMessage() + "\n");
                resultOutput.setText(output.toString());
            } else {
                output.append("* properties file loaded\n");
                resultOutput.setText(output.toString());
                loadModeComboBox();
                setBaseDirField();
                setAdmin();
            }
        }
    }

    @FXML
    private ComboBox<String> modeComboBox;
    private void loadModeComboBox() {
        modeComboBox.setItems(FXCollections.observableList(modeValues));
        modeComboBox.getSelectionModel().select(properties.getProperty("epadd.mode"));
    }

    @FXML
    private TextField baseDir;
    private void setBaseDirField() {
        baseDir.setText(properties.getProperty("epadd.base.dir").replaceAll("\\\\\\\\", "\\\\"));
    }

    @FXML
    private TextField adminName;
    
    @FXML
    private TextField adminEmail;

    private void setAdmin() {
        String adminTxt = properties.getProperty("admin");
        String[] adminSplit = adminTxt.split(",");
        adminName.setText(adminSplit[0].trim());
        adminEmail.setText(adminSplit[1].trim());
    }

    private Exception readProperties() {
        output.append("* opening properties files at: " + propertiesLocation + "\n");
        resultOutput.setText(output.toString());

        try {
            BufferedReader reader = new BufferedReader(new FileReader(propertiesLocation));
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.contains("=")) {
                    String[] split = line.split("=");
                    properties.setProperty(split[0], split[1]);
                }
            }
            reader.close();
            return null;
        } catch(Exception e) {
            return e;
        }    
    }

    @FXML
    private Text resultStatus;

    @FXML
    private Text resultOutput;

    private StringBuffer output = new StringBuffer();

    @FXML
    protected void updatePropertiesFile() {
        //backup the properties file
        output.append("* backing up properties file\n");
        resultOutput.setText(output.toString());
        IOException e = backupPropertiesFile();
        if(e != null) {
            output.append("* error backing up file\n" + e.getMessage() + "\n");
            resultOutput.setText(output.toString());
        } else {
            output.append("* properties file backed up\n");
            resultOutput.setText(output.toString());
        }

        //serialize the properties file
        output.append("* serializing properties\n");
        resultOutput.setText(output.toString());
        String fileOutput = serializeProperties();
        output.append("* properties file serialized\n");
        resultOutput.setText(output.toString());

        //write properties file
        output.append("* writing properties file to " + propertiesLocation + "\n");
        resultOutput.setText(output.toString());
        e = writePropertiesFile(fileOutput);
        if(e != null) {
            output.append("* error updating properties file\n" + e.getMessage() + "\n");
            resultOutput.setText(output.toString());
        } else {
            output.append("* properties file written\n");
            resultOutput.setText(output.toString());
        }

        output.append("* properties file update complete\n");
        resultOutput.setText(output.toString());

        //reload properties file:
        output.append("* reloading properties\n");
        resultOutput.setText(output.toString());
        readProperties();

    }

    private String serializeProperties() {
        StringBuffer sb = new StringBuffer();
        sb.append("epadd.mode=" + modeComboBox.getSelectionModel().getSelectedItem() + "\n");
        String bd =  baseDir.getText().replaceAll("\\\\", "\\\\\\\\");
        sb.append("epadd.base.dir=" + bd + "\n");
        sb.append("admin=" + adminName.getText() + ", " + adminEmail.getText()  + "\n");
        return sb.toString();
    }

    private IOException backupPropertiesFile() {

        resultOutput.setText(output.toString());
        Path source = Paths.get(propertiesLocation);
        Path destination = Paths.get(propertiesLocation);
        String epaddDirectory = baseDir.getText().replaceAll("\\\\\\\\", "\\\\");
        destination = Paths.get(epaddDirectory + "\\" + destination.getFileName() + "-bk0");
        output.append("* backing up " + source.toString() + "\n  to " + destination.toString() + "\n");
        resultOutput.setText(output.toString());
        try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            return null;
        } catch (IOException e) {
            return e;
        }
    }

    private IOException writePropertiesFile(String fileContent) {
        Path source = Paths.get(propertiesLocation);
        String path = source.toAbsolutePath().toString();
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(fileContent);
            writer.flush();
            writer.close();
            return null;
        } catch (IOException e) {
            return e;
        }
    }
}