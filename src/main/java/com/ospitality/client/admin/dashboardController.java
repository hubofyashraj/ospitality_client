package com.ospitality.client.admin;

import com.jfoenix.controls.JFXDatePicker;
import com.ospitality.client.common;
import com.ospitality.client.user;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class dashboardController {
    @FXML
    public BarChart<String,Number> patientsBarChart;
    @FXML
    public PieChart ratioPieChart;
    @FXML
    public ImageView userImg;
    @FXML
    public Label userNameTxt;
    @FXML
    public Label roleTxt;
    @FXML
    public Label designationTxt;
    @FXML
    public Label emailTxt;
    @FXML
    public Label phoneTxt;
    @FXML
    public Label joiningDateTxt;
    @FXML
    public PieChart genderPieChart;
    @FXML
    public Button yearlyBTN;
    @FXML
    public Button monthlyBTN;
    @FXML
    public Button weaklyBTN;
    @FXML
    public CategoryAxis barX;
    @FXML
    public NumberAxis barY;
    @FXML
    public StackPane navbarStackPane;



    @FXML
    public  StackPane mainPane;
    @FXML
    private Button dashboardBTN;
    @FXML
    private Button logOutBTN;
    @FXML
    private Button settingsBTN;

    @FXML
    private Button accountsManagementBTN;

    public void updateUserPhoto() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Photo");
        File inputFile = chooser.showOpenDialog(null);
        if(inputFile!=null){
            common.updateDP(inputFile,userImg);
        }
    }

    public void removeUserPhoto() throws URISyntaxException, IOException {
        userImg.setImage(new Image(Objects.requireNonNull(getClass().getResource("/icons/user.png")).toURI().toString()));
        common.removeUserDp();
    }

    record visits(LocalDate date, int mVisit, int fVisit, int age1to10, int age11to20, int age21to30,
                  int age31to40, int age41to50, int age51to60, int age61abv) {

        public LocalDate getDate() {
            return date;
        }

        public int getMVisit() {
            return mVisit;
        }

        public int getFVisit() {
            return fVisit;
        }

        public int getAge1to10() {
            return age1to10;
        }

        public int getAge11to20() {
            return age11to20;
        }

        public int getAge21to30() {
            return age21to30;
        }

        public int getAge31to40() {
            return age31to40;
        }

        public int getAge41to50() {
            return age41to50;
        }

        public int getAge51to60() {
            return age51to60;
        }

        public int getAge61abv() {
            return age61abv;
        }
    }


    DataOutputStream DOut;
    DataInputStream din;
    final List<visits> obList = new ArrayList<>();

    @FXML
    public void initialize() throws IOException, ParseException, URISyntaxException {

        userNameTxt.setText(user.getUserName());
        roleTxt.setText(user.getRole());
        emailTxt.setText(user.getWorkEmail());
        phoneTxt.setText(user.getPhNo());
        designationTxt.setText(user.getDesignation());
        joiningDateTxt.setText(user.getJoiningDate());

        DOut = new DataOutputStream(common.getSocket().getOutputStream());
        din = new DataInputStream(common.getSocket().getInputStream());

        DOut.writeUTF("ADI");
        DOut.flush();

        boolean entries = din.readBoolean();
        if(entries){
            String str=din.readUTF();
            while(!str.equals("!!")){
                String[] arr = str.split("\\./");
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(arr[0]);
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                visits ob = new visits(localDate, Integer.parseInt(arr[1]), Integer.parseInt(arr[2]),
                        Integer.parseInt(arr[3]), Integer.parseInt(arr[4]), Integer.parseInt(arr[5]),
                        Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), Integer.parseInt(arr[8]),
                        Integer.parseInt(arr[9]));

                obList.add(ob);
                str=din.readUTF();
            }

        }


        callAllTime();

        ContextMenu contextMenu = new ContextMenu();
        MenuItem updateDP = new MenuItem("Update Profile");
        MenuItem remove = new MenuItem("Remove");
        contextMenu.getItems().addAll(updateDP,remove);
        updateDP.setOnAction(
                event -> {
                    try {
                        updateUserPhoto();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        remove.setOnAction(
                event -> {
                    try {
                        removeUserPhoto();
                    } catch (URISyntaxException | IOException e) {
                        e.printStackTrace();
                    }
                }
        );

        userImg.setOnContextMenuRequested(
                event -> {
                    if(contextMenu.isShowing()){
                        contextMenu.hide();
                    }else{
                        contextMenu.show(userImg,event.getScreenX(),event.getScreenY());
                    }
                }
        );

        common.loadDP(userImg);

    }

    public void callAllTime( ) {
        monthlyBTN.setDisable(false);
        yearlyBTN.setDisable(true);
        weaklyBTN.setDisable(false);
        barX.setLabel("Year");
        barY.setLabel("Number of patients");

        int yearCount=0;
        List<Integer> yearList = new ArrayList<>();

        XYChart.Series<String,Number> series = new XYChart.Series<>();

        patientsBarChart.getData().clear();

        int lastYear = 0;

        for (visits ob : obList) {
            LocalDate date = ob.getDate();
            int year = date.getYear();
            if (year != lastYear) {
                yearCount++;
                lastYear = year;
                yearList.add(lastYear);
            }
        }


        int[] malePatients = new int[yearCount];
        int[] femalePatients = new int[yearCount];
        int[] ageGrp1 = new int[yearCount];
        int[] ageGrp2 = new int[yearCount];
        int[] ageGrp3 = new int[yearCount];
        int[] ageGrp4 = new int[yearCount];
        int[] ageGrp5 = new int[yearCount];
        int[] ageGrp6 = new int[yearCount];
        int[] ageGrp7 = new int[yearCount];
        int[] totalPatients = new int[yearCount];

        lastYear = 0;
        int i=-1;
        for(visits ob : obList){
            LocalDate date = ob.getDate();
            int year = date.getYear();
            if(year!=lastYear){
                i++;
                lastYear=year;
            }

            malePatients[i]+=ob.getMVisit();
            femalePatients[i]+=ob.getFVisit();
            ageGrp1[i]+=ob.getAge1to10();
            ageGrp2[i]+=ob.getAge11to20();
            ageGrp3[i]+=ob.getAge21to30();
            ageGrp4[i]+=ob.getAge31to40();
            ageGrp5[i]+=ob.getAge41to50();
            ageGrp6[i]+=ob.getAge51to60();
            ageGrp7[i]+=ob.getAge61abv();
            totalPatients[i]=malePatients[i]+femalePatients[i];

        }

        int totalMaleAllTime = common.getArraySum(malePatients,malePatients.length);
        int totalFemaleAllTime = common.getArraySum(femalePatients,femalePatients.length);
        int totalGrp1AllTime = common.getArraySum(ageGrp1,ageGrp1.length);
        int totalGrp2AllTime = common.getArraySum(ageGrp2,ageGrp2.length);
        int totalGrp3AllTime = common.getArraySum(ageGrp3,ageGrp3.length);
        int totalGrp4AllTime = common.getArraySum(ageGrp4,ageGrp4.length);
        int totalGrp5AllTime = common.getArraySum(ageGrp5,ageGrp5.length);
        int totalGrp6AllTime = common.getArraySum(ageGrp6,ageGrp6.length);
        int totalGrp7AllTime = common.getArraySum(ageGrp7,ageGrp7.length);

        series.setName("patients");
        for(int j=0;j<yearCount;j++){
            series.getData().add(new XYChart.Data<>(String.valueOf(yearList.get(j)), totalPatients[j]));
        }

        patientsBarChart.getData().add(series);

        ObservableList<PieChart.Data> pieChartGender = FXCollections.observableArrayList(
                new PieChart.Data("Male",totalMaleAllTime),
                new PieChart.Data("Female",totalFemaleAllTime)
        );

        genderPieChart.setData(pieChartGender);
        genderPieChart.setClockwise(true);

        ObservableList<PieChart.Data> pieChartAge = FXCollections.observableArrayList(
                new PieChart.Data("1-10",totalGrp1AllTime),
                new PieChart.Data("11-20",totalGrp2AllTime),
                new PieChart.Data("21-30",totalGrp3AllTime),
                new PieChart.Data("31-40",totalGrp4AllTime),
                new PieChart.Data("41-50",totalGrp5AllTime),
                new PieChart.Data("51-60",totalGrp6AllTime),
                new PieChart.Data("61+",totalGrp7AllTime)
        );

        ratioPieChart.setData(pieChartAge);
        ratioPieChart.setClockwise(true);

    }

    public void callYearly() {
        monthlyBTN.setDisable(true);
        yearlyBTN.setDisable(false);
        weaklyBTN.setDisable(false);
        barX.setLabel("Month");
        barY.setLabel("Number of patients");
        patientsBarChart.getData().clear();


        String lastMonth = "";
        List<String> monthList = new ArrayList<>();

        int[] malePatients = new int[12];
        int[] femalePatients = new int[12];
        int[] ageGrp1 = new int[12];
        int[] ageGrp2 = new int[12];
        int[] ageGrp3 = new int[12];
        int[] ageGrp4 = new int[12];
        int[] ageGrp5 = new int[12];
        int[] ageGrp6 = new int[12];
        int[] ageGrp7 = new int[12];
        int[] totalPatients = new int[12];

        int i = -1;


        for(visits ob : obList){
            if(monthList.size()==12) break;

            String  month = ob.getDate().getMonth().name();
            if(!month.equals(lastMonth)){
                i++;
                monthList.add(month);
                lastMonth=month;
            }

            malePatients[i]+=ob.getMVisit();
            femalePatients[i]+=ob.getFVisit();
            ageGrp1[i]+=ob.getAge1to10();
            ageGrp2[i]+=ob.getAge11to20();
            ageGrp3[i]+=ob.getAge21to30();
            ageGrp4[i]+=ob.getAge31to40();
            ageGrp5[i]+=ob.getAge41to50();
            ageGrp6[i]+=ob.getAge51to60();
            ageGrp7[i]+=ob.getAge61abv();
            totalPatients[i]=malePatients[i]+femalePatients[i];

        }


        int totalMaleThisYear = common.getArraySum(malePatients,malePatients.length);
        int totalFemaleThisYear = common.getArraySum(femalePatients,femalePatients.length);
        int totalGrp1ThisYear = common.getArraySum(ageGrp1,ageGrp1.length);
        int totalGrp2ThisYear = common.getArraySum(ageGrp2,ageGrp2.length);
        int totalGrp3ThisYear = common.getArraySum(ageGrp3,ageGrp3.length);
        int totalGrp4ThisYear = common.getArraySum(ageGrp4,ageGrp4.length);
        int totalGrp5ThisYear = common.getArraySum(ageGrp5,ageGrp5.length);
        int totalGrp6ThisYear = common.getArraySum(ageGrp6,ageGrp6.length);
        int totalGrp7ThisYear = common.getArraySum(ageGrp7,ageGrp7.length);


        XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName("patients");
        for(int j=0;j<monthList.size();j++) {
            series.getData().add(new XYChart.Data<>(monthList.get(j), totalPatients[j]));
        }

        patientsBarChart.getData().add(series);

        ObservableList<PieChart.Data> pieChartGender = FXCollections.observableArrayList(
                new PieChart.Data("Male",totalMaleThisYear),
                new PieChart.Data("Female",totalFemaleThisYear)
        );

        genderPieChart.setData(pieChartGender);
        genderPieChart.setClockwise(true);

        ObservableList<PieChart.Data> pieChartAge = FXCollections.observableArrayList(
                new PieChart.Data("1-10",totalGrp1ThisYear),
                new PieChart.Data("11-20",totalGrp2ThisYear),
                new PieChart.Data("21-30",totalGrp3ThisYear),
                new PieChart.Data("31-40",totalGrp4ThisYear),
                new PieChart.Data("41-50",totalGrp5ThisYear),
                new PieChart.Data("51-60",totalGrp6ThisYear),
                new PieChart.Data("61+",totalGrp7ThisYear)
        );

        ratioPieChart.setData(pieChartAge);
        ratioPieChart.setClockwise(true);


    }

    public void callMonthly() {
        monthlyBTN.setDisable(false);
        yearlyBTN.setDisable(false);
        weaklyBTN.setDisable(true);
        barX.setLabel("Days");
        barY.setLabel("Number of patients");
        patientsBarChart.getData().clear();


        List<Integer> DaysList = new ArrayList<>();

        int[] malePatients = new int[28];
        int[] femalePatients = new int[28];
        int[] ageGrp1 = new int[28];
        int[] ageGrp2 = new int[28];
        int[] ageGrp3 = new int[28];
        int[] ageGrp4 = new int[28];
        int[] ageGrp5 = new int[28];
        int[] ageGrp6 = new int[28];
        int[] ageGrp7 = new int[28];
        int[] totalPatients = new int[28];

        int i = 0;


        for(visits ob : obList) {
            if (i < 28) {
                int day = ob.getDate().getDayOfMonth();
                DaysList.add(day);

                malePatients[i]+=ob.getMVisit();
                femalePatients[i]+=ob.getFVisit();
                ageGrp1[i]+=ob.getAge1to10();
                ageGrp2[i]+=ob.getAge11to20();
                ageGrp3[i]+=ob.getAge21to30();
                ageGrp4[i]+=ob.getAge31to40();
                ageGrp5[i]+=ob.getAge41to50();
                ageGrp6[i]+=ob.getAge51to60();
                ageGrp7[i]+=ob.getAge61abv();
                totalPatients[i]=malePatients[i]+femalePatients[i];
                i++;
            }
        }

        int totalMaleThisMonth = common.getArraySum(malePatients,malePatients.length);
        int totalFemaleThisMonth = common.getArraySum(femalePatients,femalePatients.length);
        int totalGrp1ThisMonth = common.getArraySum(ageGrp1,ageGrp1.length);
        int totalGrp2ThisMonth = common.getArraySum(ageGrp2,ageGrp2.length);
        int totalGrp3ThisMonth = common.getArraySum(ageGrp3,ageGrp3.length);
        int totalGrp4ThisMonth = common.getArraySum(ageGrp4,ageGrp4.length);
        int totalGrp5ThisMonth = common.getArraySum(ageGrp5,ageGrp5.length);
        int totalGrp6ThisMonth = common.getArraySum(ageGrp6,ageGrp6.length);
        int totalGrp7ThisMonth = common.getArraySum(ageGrp7,ageGrp7.length);

        XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName("patients");
        for(int j=0;j<DaysList.size();j++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(DaysList.get(j)), totalPatients[j]));
        }

        patientsBarChart.getData().add(series);


        ObservableList<PieChart.Data> pieChartGender = FXCollections.observableArrayList(
                new PieChart.Data("Male",totalMaleThisMonth),
                new PieChart.Data("Female",totalFemaleThisMonth)
        );

        genderPieChart.setData(pieChartGender);
        genderPieChart.setClockwise(true);

        ObservableList<PieChart.Data> pieChartAge = FXCollections.observableArrayList(
                new PieChart.Data("1-10",totalGrp1ThisMonth),
                new PieChart.Data("11-20",totalGrp2ThisMonth),
                new PieChart.Data("21-30",totalGrp3ThisMonth),
                new PieChart.Data("31-40",totalGrp4ThisMonth),
                new PieChart.Data("41-50",totalGrp5ThisMonth),
                new PieChart.Data("51-60",totalGrp6ThisMonth),
                new PieChart.Data("61+",totalGrp7ThisMonth)
        );

        ratioPieChart.setData(pieChartAge);
        ratioPieChart.setClockwise(true);

    }


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
        common.logout(mainPane);
    }

    


    @FXML
    void callAccountsManagement() throws IOException {
        Stage stage = (Stage) accountsManagementBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("accountsManagement.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }

    @FXML
    void callSettings() throws IOException {
        Stage stage = (Stage) settingsBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/ospitality/client/settings.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }

}
