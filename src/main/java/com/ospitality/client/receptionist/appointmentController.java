package com.ospitality.client.receptionist;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSnackbar;
import com.ospitality.client.common;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class appointmentController {

    public Button appointmentBTN;
    public StackPane navbarStackPane;
    public StackPane mainpane;
    public TabPane tabPane;
    public StackPane newAppointmentTab;
    public StackPane modifyAppointmentTab;
    public Button yearGoPrevBtn1,yearGoPrevBtn2,yearGoNextBtn1,yearGoNextBtn2;
    public JFXCheckBox visitedCheckBox;
    @FXML
    private Button dashboardBTN;
    @FXML
    private Button logOutBTN;
    @FXML
    private Button settingsBTN;

    JFXSnackbar jfxSnackbar;

    @FXML
    void callDashboard() throws IOException {
        Stage stage = (Stage) dashboardBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dashboard.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }

    @FXML
    void callLogOut() throws IOException {
        common.logout(mainpane);
    }

    

    @FXML
    void callSettings() throws IOException {
        Stage stage = (Stage) settingsBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/ospitality/client/settings.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }

    public void callAppointment() throws IOException {
        Stage stage = (Stage) appointmentBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("appointment.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }


    @FXML
    public Label successTxt;
    @FXML
    public Button submitBTN;
    @FXML
    private TextField patientIDTxt1;
    @FXML
    private ComboBox<?> chooseDepartmentCB;
    @FXML
    private Button newPatientBTN;
    @FXML
    private AnchorPane datePicker1;
    @FXML
    public ListView<String> appointmentList;
    @FXML
    public Label patientName;
    @FXML
    public TextField departmentCB;
    @FXML
    public AnchorPane datePicker2;
    @FXML
    public Button searchBTN;
    @FXML
    private TextField patientIDTxt;
    @FXML
    private Button saveBTN;
    @FXML
    private Button deleteBTN;
    @FXML
    private StackPane innerStack;
    String date, data;

    @FXML
    private TextField dateField1,dateField2;
    @FXML
    private AnchorPane datepicker1, datepicker2;

    @FXML
    private AnchorPane dayPane1, dayPane2;

    @FXML
    private GridPane days1, days2;

    @FXML
    private Button BtnDay29,BtnDay291;

    @FXML
    private Button BtnDay30,BtnDay301;

    @FXML
    private Button BtnDay31,BtnDay311;

    @FXML
    private AnchorPane monthPane1, monthPane2;

    @FXML
    private GridPane months1, months2;

    @FXML
    private AnchorPane yearPane1, yearPane2;

    @FXML
    private GridPane years1, years2;

    @FXML
    private Label yearBtn1, yearBtn2;

    @FXML
    private Label monthBtn1, monthBtn2;

    @FXML
    private Label dayBtn1, dayBtn2;

    Label result = new Label();

    JFXSnackbar jfxSnackbar2;

    @FXML
    public void initialize(){

        jfxSnackbar = new JFXSnackbar((Pane) submitBTN.getParent());
        jfxSnackbar2 = new JFXSnackbar((Pane) searchBTN.getParent());

        // SETTING TABPANE TABS TO FULL WIDTH //
        tabPane.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            tabPane.setTabMinWidth((tabPane.getWidth()-80) / tabPane.getTabs().size());
            tabPane.setTabMaxWidth((tabPane.getWidth()-80) / tabPane.getTabs().size());
        });
        innerStack.setVisible(false);
        disabler();
        LocalDate today = LocalDate.now();

        // LISTENER FOR THE CUSTOM DATEPICKER IN NEW APPOINTMENT TAB //
        {

            yearPane1.setVisible(false);
            monthPane1.setVisible(false);
            dayPane1.setVisible(false);
            dayBtn1.setText(String.valueOf(today.getDayOfMonth()));
            monthBtn1.setText(String.valueOf(getIntMonth(String.valueOf(today.getMonth()))));
            yearBtn1.setText(String.valueOf(today.getYear()));
            dayBtnClicked1();

            yearSetter1();

        }

        // LISTENER FOR THE CUSTOM DATEPICKER IN MODIFY APPOINTMENT TAB //
        {
            yearPane2.setVisible(false);
            monthPane2.setVisible(false);
            dayPane2.setVisible(false);
            dayBtn2.setText(String.valueOf(today.getDayOfMonth()));
            monthBtn2.setText(String.valueOf(getIntMonth(String.valueOf(today.getMonth()))));
            yearBtn2.setText(String.valueOf(today.getYear()));

            yearSetter2();

        }
    }



    @FXML
    void callNewPatient( ) throws IOException {
        Stage stage = (Stage) newPatientBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("patientsRegister.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }

    @FXML
    void callSubmit( ) {

        //  SUBMIT NEW APPOINTMENT  //

        if(!patientIDTxt1.getText().startsWith("Pat")){
            Label l = new Label("Please Enter Correct Patient ID, Starting with Pat");
            JFXSnackbar snackbar = new JFXSnackbar((Pane) searchBTN.getParent());
            l.setStyle("-fx-text-fill: red");
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(l, Duration.millis(2000)));
            return;
        }

        if(patientIDTxt1.getText().isEmpty() || dateField1.getText().isEmpty()
                || chooseDepartmentCB.getValue().toString().isEmpty()){
            Label l = new Label("Some Field Seems Empty!!");
            JFXSnackbar snackbar = new JFXSnackbar((Pane) submitBTN.getParent());
            l.setStyle("-fx-text-fill: red");
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(l, Duration.millis(2000)));
            return;
        }

        String id  = patientIDTxt1.getText();
        String department = (String) chooseDepartmentCB.getValue();
        LocalDate date = LocalDate.parse(dateField1.getText(), DateTimeFormatter.ofPattern("d/M/y"));

        try {
            String str = date+"./"+id+"./"+department;
            DataOutputStream dout = common.getDout();
            DataInputStream din = common.getDin();

            dout.writeUTF("RNA");
            dout.writeUTF(str);

            if(din.readBoolean()){
                if(din.readBoolean()&&din.readBoolean()){
                    result.setText("Patient "+ id +" now Has an appointment for "+ department +" on "+date+" .");
                    patientIDTxt1.setText("");
                    chooseDepartmentCB.setValue(null);
                    dateField1.setText(null);

                }else{
                    result.setText("Error Occurred!");
                }
            }else{
                result.setText("Unregistered Patient!");
            }
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent(result, Duration.millis(3000)));

        } catch (IOException throwables) {
            throwables.printStackTrace();
        }
    }



    @FXML
    void callSearch( ) {

        if(!patientIDTxt.getText().startsWith("Pat")){
            Label l = new Label("Please Enter Correct Patient ID, Starting with Pat");
            JFXSnackbar snackbar = new JFXSnackbar((Pane) searchBTN.getParent());
            l.setStyle("-fx-text-fill: red");
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(l, Duration.millis(2000)));
            return;
        }

        disabler();
        if(patientIDTxt.getText().isEmpty()){
            Label l = new Label("Please Enter Patient ID");
            JFXSnackbar snackbar = new JFXSnackbar((Pane) searchBTN.getParent());
            l.setStyle("-fx-text-fill: red");
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(l, Duration.millis(2000)));
        }else{
            try{
                DataOutputStream dout = common.getDout();
                DataInputStream din = common.getDin();

                dout.writeUTF("RFA");
                dout.writeUTF(patientIDTxt.getText());

                if(din.readBoolean()) {
                    innerStack.setVisible(true);
                    appointmentList.getItems().clear();
                    patientName.setText("Patient Name: " + din.readUTF());
                    if (din.readBoolean()) {
                        appointmentList.setDisable(false);
                        String app;
                        while (!(app = din.readUTF()).equals("!!")) {
                            appointmentList.getItems().add(app);
                        }
                    } else {
                        appointmentList.setDisable(true);
                        JFXSnackbar snackbar = new JFXSnackbar((Pane) searchBTN.getParent());
                        Label l = new Label("No appointment for Patient " + patientIDTxt.getText());
                        l.setStyle("-fx-text-fill: red");
                        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(l, Duration.millis(2000)));
                    }
                }else {
                    Label l = new Label("No Patient with id: "+patientIDTxt.getText());
                    JFXSnackbar snackbar = new JFXSnackbar((Pane) searchBTN.getParent());
                    l.setStyle("-fx-text-fill: red");
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(l, Duration.millis(2000)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void callGetAppointment() throws ParseException {
        data = appointmentList.getSelectionModel().getSelectedItem();
        boolean visited = (data.split("\\./")[1]).equals("true");
        data = data.split("\\./")[0];
        enabler();
        date = data.substring(0,10);
        boolean notUpcoming = LocalDate.parse(date).isBefore(LocalDate.now());
        datePicker2.setDisable(notUpcoming);
        date = new SimpleDateFormat("dd/MM/yyyy").format( new SimpleDateFormat("yyyy-MM-dd").parse(data));
        dateField2.setText(date);
        departmentCB.setText(data.substring(12));
        visitedCheckBox.selectedProperty().set(visited);
        datePicker2.setDisable(visited || notUpcoming);
        deleteBTN.setDisable(visited || notUpcoming);
        saveBTN.setDisable(visited || notUpcoming);
    }


    @FXML
    void callDelete( ) throws IOException {
        String str = patientIDTxt.getText()+"./"+date+"./"+data.substring(12);
        DataOutputStream dout = common.getDout();
        DataInputStream din = common.getDin();
        dout.writeUTF("RDA");
        dout.writeUTF(str);
        if(din.readBoolean()&&din.readBoolean()){
            JFXSnackbar snackbar = new JFXSnackbar((Pane) searchBTN.getParent());
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("Appointment Deleted"), Duration.millis(2000)));
            disabler();
            callSearch();
        }
    }

    @FXML
    void callSave( ) throws IOException {
        if(!date.equals(dateField2.getText())){
            String str = departmentCB.getText()+"./"+dateField2.getText()+"./"+patientIDTxt.getText()+"./"+date+"./"+data.substring(12);
            DataOutputStream dout = common.getDout();
            DataInputStream din = common.getDin();
            dout.writeUTF("RUA");
            dout.writeUTF(str);
            din.readBoolean();
            disabler();
            callSearch();
            jfxSnackbar2.enqueue(new JFXSnackbar.SnackbarEvent(new Label("Appointment Modified Successfully"), Duration.millis(2000)));
        }
    }



    void enabler(){
        saveBTN.setVisible(true);
        deleteBTN.setVisible(true);
        departmentCB.setVisible(true);
        datePicker2.setVisible(true);
        visitedCheckBox.setVisible(true);
    }

    void disabler(){
        saveBTN.setVisible(false);
        deleteBTN.setVisible(false);
        departmentCB.setVisible(false);
        datePicker2.setVisible(false);
        visitedCheckBox.setVisible(false);
    }


    //      DATEPICKER METHODS FOR MODIFY TAB DATE PICKER START     //

    private void returnDate2() {
        dateField2.setText(dayBtn2.getText()+"/"+ monthBtn2.getText()+"/"+ yearBtn2.getText());
        datepicker2.setVisible(false);
    }

    @FXML
    void callYearGoNext2() {
        patientsRegisterController.o(years1);
        yearSetter2();
    }

    @FXML
    void callYearGoPrev2() {
        patientsRegisterController.n(years1, years2.getChildren());
        yearSetter2();
    }

    @FXML
    void dayBtnClicked2() {
        int month = Integer.parseInt(monthBtn1.getText());
        yearPane2.setVisible(false);
        monthPane2.setVisible(false);
        dayPane2.setVisible(true);

        if(month==4||month==6||month==9||month==11){
            BtnDay311.setVisible(false);
            BtnDay301.setVisible(true);
            BtnDay291.setVisible(true);
        }else if(month==2){
            BtnDay301.setVisible(false);
            BtnDay311.setVisible(false);
            BtnDay291.setVisible(Integer.parseInt(yearBtn1.getText()) % 4 == 0);
        }else{
            BtnDay291.setVisible(true);
            BtnDay301.setVisible(true);
            BtnDay311.setVisible(true);
        }
    }

    @FXML
    void monthBtnClicked2() {
        yearPane2.setVisible(false);
        monthPane2.setVisible(true);
        dayPane2.setVisible(false);


    }

    @FXML
    void yearBTNClicked2() {
        yearPane2.setVisible(true);
        monthPane2.setVisible(false);
        dayPane2.setVisible(false);

    }


    public void callPick2() {
        if(datepicker2.isVisible()){
            datepicker2.setVisible(false);
            datePicker2.setMaxHeight(40);
        }else{
            datepicker2.setVisible(true);
            dayPane2.setVisible(true);
            datePicker2.setMaxHeight(240);
        }
    }


    private void daySetter1(){
        days1.getChildren().forEach(
                item -> {
                    Button button = (Button) item;

                    button.setDisable(Integer.parseInt(yearBtn1.getText()) == LocalDate.now().getYear() &&
                            Integer.parseInt(monthBtn1.getText()) == LocalDate.now().getMonthValue() &&
                            Integer.parseInt(button.getText()) < LocalDate.now().getDayOfMonth());

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

    private void monthSetter1(){
        months1.getChildren().forEach(
                item -> {
                    Button month = (Button) item;
                    month.setDisable(Integer.parseInt(yearBtn1.getText()) == LocalDate.now().getYear() &&
                            getIntMonth(month.getText()) < LocalDate.now().getMonthValue());
                    month.setOnAction(
                            actionEvent -> {
                                String monthStr = month.getText();
                                monthBtn1.setText(String.valueOf(getIntMonth(monthStr)));
                                monthPane1.setVisible(false);
                                daySetter1();
                                dayBtnClicked1();
                            }
                    );
                }
        );
        daySetter1();
    }

    private void yearSetter1(){
        years1.getChildren().forEach(
                item -> {
                    Button year = (Button) item;

                    if(Integer.parseInt(year.getText())<LocalDate.now().getYear()){
                        yearGoPrevBtn1.setDisable(true);
                        year.setDisable(true);
                    }else{
                        yearGoPrevBtn1.setDisable(false);
                        year.setDisable(false);
                    }

                    year.setOnAction(
                            actionEvent -> {
                                String yearStr = year.getText();
                                yearBtn1.setText(yearStr);
                                yearPane1.setVisible(false);
                                monthSetter1();
                                monthBtnClicked1();
                            }
                    );
                }
        );
        monthSetter1();
    }

    private void daySetter2(){
        days2.getChildren().forEach(
                item -> {
                    Button button = (Button) item;

                    button.setDisable(Integer.parseInt(yearBtn2.getText()) == LocalDate.now().getYear() &&
                            Integer.parseInt(monthBtn2.getText()) == LocalDate.now().getMonthValue() &&
                            Integer.parseInt(button.getText()) < LocalDate.now().getDayOfMonth());

                    button.setOnAction(
                            actionEvent -> {
                                int day = Integer.parseInt(button.getText());
                                dayBtn2.setText(String.valueOf(day));
                                dayPane2.setVisible(false);
                                returnDate2();
                                datepicker2.setVisible(false);
                                datePicker2.setMaxHeight(40);
                                datePicker2.setStyle("-fx-padding:0 0 0 0");
                            }
                    );
                }
        );
    }

    private void monthSetter2(){
        months2.getChildren().forEach(
                item -> {
                    Button month = (Button) item;

                    month.setDisable(Integer.parseInt(yearBtn2.getText()) == LocalDate.now().getYear() &&
                            getIntMonth(month.getText()) < LocalDate.now().getMonthValue());

                    month.setOnAction(
                            actionEvent -> {
                                String monthStr = month.getText();
                                monthBtn2.setText(String.valueOf(getIntMonth(monthStr)));
                                monthPane2.setVisible(false);
                                daySetter2();
                                dayBtnClicked2();
                            }
                    );
                }
        );
        daySetter2();
    }

    private void yearSetter2(){
        years2.getChildren().forEach(
                item -> {
                    Button year = (Button) item;
                    if(Integer.parseInt(year.getText())<LocalDate.now().getYear()){
                        yearGoPrevBtn2.setDisable(true);
                        year.setDisable(true);
                    }else{
                        yearGoPrevBtn2.setDisable(false);
                        year.setDisable(false);
                    }
                    year.setOnAction(
                            actionEvent -> {
                                String yearStr = year.getText();
                                yearBtn2.setText(yearStr);
                                yearPane2.setVisible(false);
                                monthSetter2();
                                monthBtnClicked2();
                            }
                    );
                }
        );
        monthSetter2();
    }


    //      DATEPICKER METHODS FOR MODIFY APPOINTMENTS END      //


    //      DATEPICKER FUNCTIONING METHODS FOR NEW APPOINTMENT TAB      //

    private void returnDate1() {
        dateField1.setText(dayBtn1.getText()+"/"+ monthBtn1.getText()+"/"+ yearBtn1.getText());
        datepicker1.setVisible(false);
    }

    @FXML
    void callYearGoNext1() {
        patientsRegisterController.o(years1);
        yearSetter1();
    }

    @FXML
    void callYearGoPrev1() {
        patientsRegisterController.m(years1);
        yearSetter1();
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
            datePicker1.setStyle("-fx-padding:220 0 0 0");
        }
    }

    //       DATEPICKER METHODS FOR NEW APPOINTMENT TAB END      //


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


    public void changingExpertiseNotAllowed(MouseEvent mouseEvent) {
        JFXSnackbar snackbar = new JFXSnackbar((Pane) searchBTN.getParent());
        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new Label("Expertise Can't Be Changed, Please Delete And Create A New Appointment")));
    }
}
