package com.ospitality.client.receptionist;

import com.jfoenix.controls.JFXSnackbar;
import com.ospitality.client.common;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class patientsRegisterController {

    public Label patientAddedTxt;
    public Button registerBTN;
    public Button yearGoPrevBtn1,yearGoNextBtn1;

    @FXML
    private TextField patientNameField;

    @FXML
    private ComboBox<?> patientGenderCB;


    @FXML
    private Button backBTN;

    @FXML
    private AnchorPane yearPane1,monthPane1,dayPane1,datepicker1;
    @FXML
    private Label dayBtn1,monthBtn1,yearBtn1;
    @FXML
    private GridPane days1,months1,years1;
    @FXML
    private AnchorPane datePicker1;
    @FXML
    private Button BtnDay29,BtnDay30,BtnDay31;
    @FXML
    private TextField dateField1;

    JFXSnackbar jfxSnackbar;

    private int getIntMonth(String month) {

        return switch (month) {
            case "JANUARY" -> 1;
            case "FEBRUARY" -> 2;
            case "MARCH" -> 3;
            case "APRIL" -> 4;
            case "MAY" -> 5;
            case "JUNE" -> 6;
            case "JULY" -> 7;
            case "AUGUST" -> 8;
            case "SEPTEMBER" -> 9;
            case "OCTOBER" -> 10;
            case "NOVEMBER" -> 11;
            case "DECEMBER" -> 12;
            default -> 0;
        };
    }

    @FXML
    public void initialize(){
        jfxSnackbar = new JFXSnackbar((Pane) registerBTN.getParent());
        {
            LocalDate today = LocalDate.now();

            yearPane1.setVisible(false);
            monthPane1.setVisible(false);
            dayPane1.setVisible(false);
            dayBtn1.setText(String.valueOf(today.getDayOfMonth()));
            monthBtn1.setText(String.valueOf(getIntMonth(String.valueOf(today.getMonth()))));
            yearBtn1.setText(String.valueOf(today.getYear()));
            dayBtnClicked1();

            yearSetter();

        }
    }

    private void daySetter(){
        days1.getChildren().forEach(
                item -> {
                    Button button = (Button) item;
                    button.setDisable(Integer.parseInt(yearBtn1.getText())==LocalDate.now().getYear() &&
                            Integer.parseInt(monthBtn1.getText())==LocalDate.now().getMonthValue() &&
                            Integer.parseInt(button.getText())>LocalDate.now().getDayOfMonth());
                    button.setOnAction(
                            actionEvent -> {
                                int day = Integer.parseInt(button.getText());
                                dayBtn1.setText(String.valueOf(day));
                                dayPane1.setVisible(false);
                                returnDate1();
                                datepicker1.setVisible(false);
                                datePicker1.setMaxHeight(40);
                                datePicker1.setStyle("-fx-padding:0 0 0 0");
                            }
                    );
                }
        );
    }


    private void monthSetter(){
        months1.getChildren().forEach(
                item -> {
                    Button month = (Button) item;
                    month.setDisable(Integer.parseInt(yearBtn1.getText())==LocalDate.now().getYear() &&
                            getIntMonth(month.getText())>LocalDate.now().getMonthValue());
                    month.setOnAction(
                            actionEvent -> {
                                String monthStr = month.getText();
                                monthBtn1.setText(String.valueOf(getIntMonth(monthStr)));
                                monthPane1.setVisible(false);
                                daySetter();
                                dayBtnClicked1();
                            }
                    );
                }
        );
        daySetter();
    }


    private void yearSetter(){
        years1.getChildren().forEach(
                item -> {
                    Button year = (Button) item;
                    if (Integer.parseInt(year.getText()) > LocalDate.now().getYear()) {
                        year.setDisable(true);
                        yearGoNextBtn1.setDisable(true);
                    }else{
                        year.setDisable(false);
                        yearGoNextBtn1.setDisable(false);
                    }
                    year.setOnAction(
                            actionEvent -> {
                                String yearStr = year.getText();
                                yearBtn1.setText(yearStr);
                                yearPane1.setVisible(false);
                                monthSetter();
                                monthBtnClicked1();
                            }
                    );
                }
        );
        monthSetter();
    }


    @FXML
    void callBack() throws IOException {
        Stage stage = (Stage) backBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("appointment.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);

    }

    @FXML
    void callRegister() throws IOException {

        if(patientNameField.getText().isEmpty() || patientGenderCB.getValue().toString().isEmpty() || dateField1.getText().isEmpty()){
            JFXSnackbar snackbar = new JFXSnackbar((Pane) registerBTN.getParent());
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("Some fields seem empty!!"),Duration.millis(1500)));
            return;
        }

        String name = patientNameField.getText();
        String gender = (String) patientGenderCB.getValue();
        LocalDate DOB = LocalDate.parse(dateField1.getText(), DateTimeFormatter.ofPattern("d/M/y"));
        LocalDate lastCheckup = LocalDate.now();
        int ageYears = Period.between(DOB, common.getToday()).getYears();

        String str = name+"./"+ageYears+"./"+DOB+"./"+gender+"./"+lastCheckup;

        DataOutputStream dout = common.getDout();
        DataInputStream din = common.getDin();

        dout.writeUTF("RNP");
        dout.writeUTF(str);
        if(din.readBoolean()){
            try{

                String newPatID = din.readUTF();

                jfxSnackbar.enqueue(
                        new JFXSnackbar.SnackbarEvent(
                                new Label("New Patient Registered\nPatient ID : " + newPatID),
                                Duration.millis(3000)));

                patientNameField.setText("");
                patientGenderCB.setValue(null);
                dateField1.setText(null);
            } catch (EOFException e) {
                e.printStackTrace();
            }
        }else{
            jfxSnackbar.enqueue(
                    new JFXSnackbar.SnackbarEvent(
                            new Label("Could not register patient!!"),
                            Duration.millis(3000)));

        }


    }

    private void returnDate1() {
        dateField1.setText(dayBtn1.getText()+"/"+ monthBtn1.getText()+"/"+ yearBtn1.getText());
        datepicker1.setVisible(false);
    }

    @FXML
    void callYearGoNext1() {
        o(years1);
        yearSetter();
    }

    static void o(GridPane years1) {
        Button first = (Button) years1.getChildren().get(0);
        int year = Integer.parseInt(first.getText());
        year+=30;

        final int[] finalYear = {year};
        years1.getChildren().forEach(
                item->{
                    Button btn = (Button) item;
                    btn.setText(String.valueOf(finalYear[0]));
                    finalYear[0]++;
                }
        );
    }

    @FXML
    void callYearGoPrev1() {
        m(years1);
        yearSetter();
    }

    static void m(GridPane years1) {
        n(years1, years1.getChildren());
    }

    static void n(GridPane years1, ObservableList<Node> children) {
        Button first = (Button) years1.getChildren().get(0);
        int year = Integer.parseInt(first.getText());
        year-=30;

        final int[] finalYear = {year};
        children.forEach(
                item->{
                    Button btn = (Button) item;
                    btn.setText(String.valueOf(finalYear[0]));
                    finalYear[0]++;
                }
        );
    }

    @FXML
    void dayBtnClicked1() {
        int month = Integer.parseInt(monthBtn1.getText());
        yearPane1.setVisible(false);
        monthPane1.setVisible(false);
        dayPane1.setVisible(true);

        if(month==4||month==6||month==9||month==11){
            BtnDay31.setVisible(false);
            BtnDay30.setVisible(true);
            BtnDay29.setVisible(true);
        }else if(month==2){
            BtnDay30.setVisible(false);
            BtnDay31.setVisible(false);
            BtnDay29.setVisible(Integer.parseInt(yearBtn1.getText()) % 4 == 0);
        }else{
            BtnDay29.setVisible(true);
            BtnDay30.setVisible(true);
            BtnDay31.setVisible(true);
        }
    }

    @FXML
    void monthBtnClicked1() {
        yearPane1.setVisible(false);
        monthPane1.setVisible(true);
        dayPane1.setVisible(false);


    }

    @FXML
    void yearBTNClicked1() {
        yearPane1.setVisible(true);
        monthPane1.setVisible(false);
        dayPane1.setVisible(false);

    }

    public void callPick1() {
        if(datepicker1.isVisible()){
            datepicker1.setVisible(false);
            datePicker1.setMaxHeight(40);
            datePicker1.setStyle("-fx-padding:0 0 0 0");
        }else{
            datepicker1.setVisible(true);
            dayPane1.setVisible(true);
            datePicker1.setMaxHeight(480);
            datePicker1.setStyle("-fx-padding:200 0 0 0");
        }
    }

}
