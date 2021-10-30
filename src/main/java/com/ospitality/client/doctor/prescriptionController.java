package com.ospitality.client.doctor;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.ospitality.client.common;
import com.ospitality.client.user;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Stack;

public class prescriptionController {


    @FXML
    public StackPane pane;

    @FXML
    public TableView<Medicine> selectedMedicineTableView;
    @FXML
    public TableColumn<Medicine,String> selectedMedicineName;
    @FXML
    public TableColumn<Medicine,String> selectedMedicineAdministration;
    @FXML
    public TableColumn<Medicine,String> selectedMedicineTime;

    @FXML
    private Button backBTN;


    @FXML
    private Label doctorNameTxt;

    @FXML
    private Label departmentTxt;

    @FXML
    private Label phnoTxt;

    @FXML
    private Label patientNameTxt;

    @FXML
    private Label patientAgeTxt;

    @FXML
    private Label patientGenderTxt;


    @FXML
    private ComboBox<String> searchMedicineTB;


    final DataOutputStream dout = common.getDout();
    final DataInputStream din = common.getDin();
    ObservableList<Medicine> data = FXCollections.observableArrayList(
            new Medicine(null,null,null)
    );
    @FXML
    private StackPane mainpane;

    @FXML
    void initialize(){
        selectedMedicineTableView.setItems(data);
        data = selectedMedicineTableView.getItems();

        doctorNameTxt.setText("Dr. "+ user.getUserName());
        phnoTxt.setText(user.getPhNo());
        departmentTxt.setText(user.getDesignation());


        String[] patData = dashboardController.patientData.split("\\./");

        patientNameTxt.setText(patData[0]);
        patientAgeTxt.setText(patData[1]);
        patientGenderTxt.setText(patData[3]);


        searchMedicineTB.getEditor().setOnKeyPressed(
                keyEvent -> {
                    try {
                        if(searchMedicineTB.getEditor().getText().length()!=0){

                            dout.writeUTF("DPMI");
                            dout.writeUTF(searchMedicineTB.getEditor().getText());

                            if(din.readBoolean()){
                                String str  = din.readUTF();
                                String[] arr = str.split("\\./");
                                for(String x:arr){
                                    searchMedicineTB.getItems().add(x);
                                    searchMedicineTB.show();
                                }
                            }else{
                                searchMedicineTB.getItems().clear();
                                searchMedicineTB.hide();
                            }
                        }else{
                            searchMedicineTB.getItems().clear();
                            searchMedicineTB.hide();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );


        selectedMedicineName.setCellValueFactory(new PropertyValueFactory<>("name"));
        selectedMedicineAdministration.setCellValueFactory(new PropertyValueFactory<>("administration"));
        selectedMedicineTime.setCellValueFactory(new PropertyValueFactory<>("time"));


        searchMedicineTB.setOnAction(
                mouseEvent -> {

                    if(searchMedicineTB.getEditor().getText().length()!=0){
                        String str = searchMedicineTB.getValue();

                        JFXDialogLayout content = new JFXDialogLayout();
                        Text text = new Text("Enter Administration and Period");
                        text.setStyle("-fx-text-fill: #a1a1a1");
                        content.setHeading(text);

                        TextField adm = new TextField();
                        adm.setPromptText("Administration (times/days)");
                        adm.setPrefWidth(100);
                        adm.setMaxWidth(150);
                        StackPane.setAlignment(adm,Pos.CENTER_LEFT);
                        StackPane.setMargin(adm,new Insets(0,0,0,20));
                        TextField period = new TextField();
                        period.setPromptText("Period (days)");
                        period.setPrefWidth(100);
                        period.setMaxWidth(150);
                        StackPane.setAlignment(period,Pos.CENTER_RIGHT);
                        StackPane.setMargin(period,new Insets(0,20,0,0));
                        Button click = new Button("CLICK");

                        StackPane pane = new StackPane(adm,period);

                        content.setBody(pane);

                        content.setActions(click);

                        JFXDialog dialog = new JFXDialog(mainpane,content, JFXDialog.DialogTransition.CENTER);

                        click.setOnAction(
                                actionEvent -> {
                                    if(!adm.getText().isEmpty() && !period.getText().isEmpty()){
                                        data.addAll(new Medicine(str,adm.getText(),period.getText()));
                                        dialog.close();
                                    }else{
                                        text.setFill(Color.RED);
                                    }

                                }
                        );

                        dialog.show();

                    }
                }
        );


        ContextMenu menu = new ContextMenu();
        MenuItem item = new MenuItem("Delete");
        menu.getItems().add(item);

        item.setOnAction(
                actionEvent -> {
                    Medicine med= selectedMedicineTableView.getSelectionModel().getSelectedItem();
                    selectedMedicineTableView.getItems().remove(med);
                }
        );

    }


    public record Medicine(String name, String administration, String time) {

        public String getName() {
            return name;
        }

        public String getAdministration() {
            return administration;
        }

        public String getTime() {
            return time;
        }
    }


    @FXML
    void callBack( ) throws IOException {
        Stage stage = (Stage) backBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("patient.fxml")));
        Scene sc = stage.getScene();
        Scene scene = new Scene(root,sc.getWidth(),sc.getHeight());
        stage.setScene(scene);
    }

    @FXML
    void callPrint() {
        PrinterJob pj = PrinterJob.createPrinterJob();
        if(pj!=null){
            Printer printer = Printer.getDefaultPrinter();
            PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT,Printer.MarginType.HARDWARE_MINIMUM);
            PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT,0,0,0,30);
            JobSettings jobSettings = pj.getJobSettings();
            jobSettings.setPrintQuality(PrintQuality.HIGH);
            jobSettings.setPageLayout(pageLayout);
            jobSettings.setPrintQuality(PrintQuality.DRAFT);
            pj.showPrintDialog(backBTN.getScene().getWindow());
            boolean printed = pj.printPage(getPrescription());
            if(printed){
                pj.endJob();
            }
        }
    }

    @FXML
    public void callClear(){
        selectedMedicineTableView.getItems().clear();
    }


    private StackPane getPrescription(){
        StackPane prescription = new StackPane();

        prescription.setStyle("-fx-background-color: whitesmoke");
        prescription.setPrefWidth(570);
        prescription.setPrefHeight(800);
        prescription.setMaxWidth(570);
        prescription.setPrefWidth(600);
        prescription.setPrefHeight(800);
        prescription.setMaxWidth(600);
        prescription.setMaxHeight(800);


        StackPane header = new StackPane();

        header.setPrefHeight(100);
        header.setMaxHeight(100);
        StackPane.setAlignment(header, Pos.TOP_CENTER);

        Label hospitalName = new Label("Hospital Name");

        hospitalName.setStyle("-fx-text-fill: #1bc7d7; -fx-font-size: 19; -fx-font-weight: bold");
        StackPane.setAlignment(hospitalName,Pos.TOP_CENTER);
        StackPane.setMargin(hospitalName,new Insets(10,0,0,0));

        Label doctorName = new Label("Dr. "+user.getUserName());
        StackPane.setAlignment(doctorName,Pos.BOTTOM_LEFT);
        StackPane.setMargin(doctorName,new Insets(0,0,30,10));

        Label department = new Label(user.getDesignation());
        StackPane.setAlignment(department,Pos.BOTTOM_LEFT);
        StackPane.setMargin(department,new Insets(0,0,10,10));

        Label contact = new Label("Ph.No.   "+user.getPhNo());
        StackPane.setAlignment(contact,Pos.BOTTOM_RIGHT);
        StackPane.setMargin(contact,new Insets(0,10,20,0));

        Label _1 = new Label("_________________________________________________________");
        StackPane.setAlignment(_1,Pos.BOTTOM_LEFT);
        _1.setStyle("-fx-text-fill: #22c0f0; -fx-font-size: 13; -fx-font-weight: bold");

        Label _2 = new Label("_________________________________________________________");
        StackPane.setAlignment(_2,Pos.BOTTOM_RIGHT);
        _2.setStyle("-fx-text-fill: #22c0f0; -fx-font-size: 13; -fx-font-weight: bold");

        header.getChildren().addAll(hospitalName,doctorName,department,contact,_1,_2);

        StackPane footer = new StackPane();
        StackPane.setMargin(footer,new Insets(100,0,0,0));

        String[] patData = dashboardController.patientData.split("\\./");

        Label patientName = new Label("Patient Name: "+patData[0]);
        StackPane.setAlignment(patientName,Pos.TOP_LEFT);
        StackPane.setMargin(patientName,new Insets(10,0,0,10));

        Label age = new Label("Age: "+patData[1]);
        StackPane.setAlignment(age,Pos.TOP_RIGHT);
        StackPane.setMargin(age,new Insets(10,10,0,0));

        Label gender = new Label("Gender: "+patData[3]);
        StackPane.setAlignment(gender, Pos.TOP_RIGHT);
        StackPane.setMargin(gender,new Insets(30,10,0,0));

        Label date = new Label("Date: "+ LocalDate.now());
        StackPane.setAlignment(date,Pos.TOP_LEFT);
        StackPane.setMargin(date,new Insets(30,0,0,10));

        Label r = new Label("R");
        r.setStyle("-fx-font-size: 49; -fx-font-weight: bold; -fx-font-family: 'DejaVu Sans Mono'");
        StackPane.setAlignment(r,Pos.TOP_LEFT);
        StackPane.setMargin(r,new Insets(60,0,0,10));

        Label x = new Label("X");
        x.setStyle("-fx-font-size: 26; -fx-font-family: 'DejaVu Sans Mono'; -fx-font-weight: bold");
        StackPane.setAlignment(x,Pos.TOP_LEFT);
        StackPane.setMargin(x,new Insets(83,0,0,35));

        Label _3 = new Label("_________________________________________________________");
        _3.setStyle("-fx-text-fill: #22c0f0; -fx-font-size: 13; -fx-font-weight: bold");
        StackPane.setAlignment(_3,Pos.BOTTOM_LEFT);
        StackPane.setMargin(_3,new Insets(0,0,35,0));

        Label _4 = new Label("_________________________________________________________");
        _4.setStyle("-fx-text-fill: #22c0f0; -fx-font-size: 13; -fx-font-weight: bold");
        StackPane.setAlignment(_4,Pos.BOTTOM_RIGHT);
        StackPane.setMargin(_4,new Insets(0,0,35,0));

        StackPane tablePane = new StackPane();
        StackPane.setAlignment(tablePane,Pos.BOTTOM_CENTER);
        StackPane.setMargin(tablePane,new Insets(120,0,50,0));
        tablePane.setPrefHeight(430);
        tablePane.setPrefWidth(512);
        tablePane.setPadding(new Insets(0,10,0,10));
        TableView<Medicine> tableView = new TableView<>();
        TableColumn<Medicine,String> medicineName = new TableColumn<>("MEDICINE NAME");
        TableColumn<Medicine,String> medicineAdministration = new TableColumn<>("ADMINISTRATION (times/day)");
        TableColumn<Medicine,String> medicineTime = new TableColumn<>("FOR DAYS");


        medicineName.setMinWidth(225);
        medicineAdministration.setMinWidth(225);
        medicineTime.setMinWidth(100);

        tableView.getColumns().addAll(medicineName,medicineAdministration,medicineTime);
        tableView.setStyle("-fx-vbar-policy: never");
        medicineName.setMinWidth(240);
        medicineAdministration.setMinWidth(240);
        medicineTime.setMinWidth(100);

        tableView.getColumns().addAll(medicineName,medicineAdministration,medicineTime);

        medicineName.setCellValueFactory(new PropertyValueFactory<>("name"));
        medicineAdministration.setCellValueFactory(new PropertyValueFactory<>("administration"));
        medicineTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        tableView.getItems().addAll(selectedMedicineTableView.getItems());

        tablePane.getChildren().add(tableView);

        Label sign = new Label("Doctor's Signature ____________");
        StackPane.setAlignment(sign,Pos.BOTTOM_LEFT);
        StackPane.setMargin(sign,new Insets(0,0,10,10));

        footer.getChildren().addAll(patientName,age,gender,date,r,x,_3,_4,tablePane,sign);

        prescription.getChildren().addAll(header,footer);

        return prescription;
    }
}
