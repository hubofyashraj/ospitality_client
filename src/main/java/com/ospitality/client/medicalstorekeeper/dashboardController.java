package com.ospitality.client.medicalstorekeeper;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Objects;

public class dashboardController {

    public StackPane mainpane;
    public ImageView userImg;
    public Label userNameTxt;
    public Label roleTxt;
    public Label emailTxt;
    public Label phnoTxt;
    public Label designationTxt;
    public Label joiningDateTxt;
    @FXML
    private Button dashboardBTN;
    @FXML
    private Button settingsBTN;


    @FXML
    public TableView<Medicine> selectedMedicineTableView;
    @FXML
    public TableColumn<Medicine,String> selectedMedicineName;
    @FXML
    public TableColumn<Medicine,String> medicRate;
    @FXML
    public TableColumn<Medicine,String> medicAmount;
    @FXML
    public Label totalAmountTxt;
    @FXML
    public TableColumn<Medicine,String> medicNos;
    @FXML
    private ComboBox<String> patientID;


    @FXML
    private Label patientNameTxt;

    @FXML
    private Label ageTxt;

    @FXML
    private Label medicineSellerNameTxt;

    @FXML
    private ComboBox<String> medicineName;




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

    @FXML
    public void initialize() throws IOException, URISyntaxException {
        userNameTxt.setText(user.getUserName());
        roleTxt.setText(user.getRole());
        designationTxt.setText(user.getDesignation());
        emailTxt.setText(user.getWorkEmail());
        phnoTxt.setText(user.getPhNo());
        joiningDateTxt.setText(user.getJoiningDate());


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


        ContextMenu menu = new ContextMenu();
        MenuItem item = new MenuItem("Delete");
        menu.getItems().add(item);

        item.setOnAction(
                actionEvent -> {
                    Medicine med= selectedMedicineTableView.getSelectionModel().getSelectedItem();
                    selectedMedicineTableView.getItems().remove(med);
                    totalAmountTxt.setText(String.valueOf(Double.parseDouble(totalAmountTxt.getText())-med.getAmount()));
                }
        );

        selectedMedicineTableView.setContextMenu(menu);


        patientSearch = patientID.getItems();
        selectedMedicineTableView.setItems(data);
        medicineName.setDisable(true);
        patientNameTxt.setText(null);
        ageTxt.setText(null);

        medicineSellerNameTxt.setText(user.getUserName());

        selectedMedicineName.setCellValueFactory(new PropertyValueFactory<>("name"));
        medicNos.setCellValueFactory(new PropertyValueFactory<>("nos"));
        medicRate.setCellValueFactory(new PropertyValueFactory<>("rate"));
        medicAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        DataOutputStream dout = common.getDout();
        DataInputStream din  = common.getDin();


        patientID.getEditor().setOnKeyPressed(
                keyEvent -> {
                    try{
                        if(patientID.getEditor().getText().length()!=0){
                            dout.writeUTF("MSP");
                            dout.writeUTF(patientID.getEditor().getText());
                            patientSearch.clear();
                            if(din.readBoolean()){
                                String[] str = din.readUTF().split("\\./");
                                patientSearch.addAll(str);
                                patientID.show();
                                patientID.setOnAction(
                                        actionEvent -> {
                                            String s= patientID.getValue();
                                            if(s!=null){
                                                patientNameTxt.setText(s.split("__")[0]);
                                                ageTxt.setText(s.split("__")[1]);
                                            }
                                        }
                                );

                                medicineName.setDisable(false);
                            }
                            else{
                                medicineName.setDisable(true);
                                patientNameTxt.setText(null);
                                ageTxt.setText(null);
                                totalAmountTxt.setText("0.0");
                            }
                        }else{
                            patientSearch.clear();
                            patientID.hide();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );


        ObservableList<String> medicineListItems = medicineName.getItems();
        medicineName.getEditor().setOnKeyPressed(
                keyEvent -> {
                    try {
                        if(medicineName.getEditor().getText().length()!=0){
                            dout.writeUTF("MMI");
                            dout.writeUTF(medicineName.getEditor().getText());

                            medicineListItems.clear();

                            if(din.readBoolean()){
                                String str  = din.readUTF();
                                String[] arr = str.split("\\./");
                                medicineListItems.addAll(arr);
                            }


                            medicineName.setOnAction(
                                    actionEvent -> {
                                        String str = medicineName.getValue();
                                        if(!str.contains("₹")){
                                            return;
                                        }
                                        JFXDialogLayout content = new JFXDialogLayout();
                                        Text text = new Text("Enter units\n\n(NOTE : USE x/y notation instead of decimal)");
                                        text.setFill(Color.GRAY);
                                        content.setHeading(text);

                                        TextField nos  = new TextField();
                                        nos.setPromptText("Nos. in digits");
                                        content.setBody(nos);
                                        Button button = new Button("Click");
                                        content.setActions(button);
                                        JFXDialog dialog = new JFXDialog(mainpane,content, JFXDialog.DialogTransition.CENTER);
                                        button.setOnAction(
                                                event -> {
                                                    if(nos.getText().length()!=0){
                                                        String[] arr = nos.getText().split("/");

                                                        double nosD = arr.length==2?round((double)Integer.parseInt(arr[0])/Integer.parseInt(arr[1]))
                                                                :Integer.parseInt(arr[0]);

                                                        double am = round(nosD*Double.parseDouble(str.split("₹")[1]));

                                                        Medicine med = new Medicine(str.split("₹")[0],
                                                                nosD, Double.parseDouble(str.split("₹")[1]),am);
                                                        data.addAll(med);
                                                        double amount = (Double.parseDouble(totalAmountTxt.getText())+med.getAmount());
                                                        totalAmountTxt.setText(Double.toString(round(amount)));
                                                        dialog.close();
                                                    }else {
                                                        text.setFill(Color.RED);
                                                    }

                                                }
                                        );
                                        dialog.show();

                                    }
                            );

                            medicineName.show();

                        }else{
                            medicineListItems.clear();
                            medicineName.hide();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

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
    void callLogOut() {
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



    public record Medicine(String name, double nos, double rate, double amount) {

        public double getAmount() {
            return amount;
        }

        public String getName() {
            return name;
        }

        public double getNos() {
            return nos;
        }

        public double getRate() {
            return rate;
        }
    }

    final ObservableList<Medicine> data= FXCollections.observableArrayList();

    ObservableList<String> patientSearch;
    public double round(double num){
        BigDecimal big = new BigDecimal(Double.toString(num));
        big = big.setScale(2, RoundingMode.HALF_UP);
        return Double.parseDouble(String.valueOf(big));
    }

    @FXML
    void callGenerateBill() {

        PrinterJob pj = PrinterJob.createPrinterJob();
        if(pj!=null){
            Printer printer = Printer.getDefaultPrinter();
            PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT,0,0,0,0);
            JobSettings jobSettings = pj.getJobSettings();
            jobSettings.setPrintQuality(PrintQuality.HIGH);
            jobSettings.setPageLayout(pageLayout);
            jobSettings.setPrintQuality(PrintQuality.DRAFT);
            pj.showPrintDialog(mainpane.getScene().getWindow());
            boolean printed = pj.printPage(getBill());
            if(printed){
                pj.endJob();
            }
        }
    }

    private StackPane getBill(){
        StackPane bill = new StackPane();

        bill.setMinWidth(480);
        bill.setMinHeight(750);
        bill.setPrefWidth(595);
        bill.setMaxWidth(595);

        Label hospitalName = new Label("HOSPITAL NAME");
        Label medicineBill = new Label("MEDICINE BILL");
        Label _1 = new Label("__________________________________________________");
        Label _2 = new Label("__________________________________________________");
        Label patientName = new Label("Patient Name ");
        Label patientAge = new Label("Age ");
        Label medicineSoldBy = new Label("Medicine Sold By : ");
        Label totalAmount = new Label("Total (Rs)");

        StackPane.setAlignment(hospitalName,Pos.TOP_CENTER);
        StackPane.setMargin(hospitalName,new Insets(20,0,0,0));
        hospitalName.setStyle("-fx-text-fill: #4effe4; -fx-font-size: 28; -fx-font-weight: bold");
        hospitalName.setGraphicTextGap(4);

        StackPane.setAlignment(medicineBill,Pos.TOP_CENTER);
        StackPane.setMargin(medicineBill,new Insets(80,0,0,0));
        medicineBill.setStyle("-fx-text-fill: #4effe4; -fx-font-size: 28; -fx-font-weight: bold");
        medicineBill.setGraphicTextGap(4);

        StackPane.setAlignment(_1,Pos.TOP_LEFT);
        StackPane.setMargin(_1,new Insets(120,0,0,0));
        _1.setStyle("-fx-text-fill: #4effe4; -fx-font-size: 17; -fx-font-weight: bold");
        StackPane.setAlignment(_2,Pos.TOP_RIGHT);
        StackPane.setMargin(_2,new Insets(120,0,0,0));
        _2.setStyle("-fx-text-fill: #4effe4; -fx-font-size: 17; -fx-font-weight: bold");

        StackPane.setAlignment(patientName,Pos.TOP_LEFT);
        StackPane.setMargin(patientName,new Insets(150,0,0,20));
        StackPane.setAlignment(patientAge,Pos.TOP_RIGHT);
        StackPane.setMargin(patientAge,new Insets(150,150,0,0));
        StackPane.setAlignment(totalAmount,Pos.BOTTOM_RIGHT);
        StackPane.setMargin(totalAmount,new Insets(0,100,20,0));
        StackPane.setAlignment(medicineSoldBy,Pos.BOTTOM_LEFT);
        StackPane.setMargin(medicineSoldBy,new Insets(0,0,20,20));


        Label date = new Label(LocalDate.now().toString());
        Label name = new Label(patientNameTxt.getText());
        Label age = new Label(ageTxt.getText());
        Label seller = new Label(medicineSellerNameTxt.getText());
        Label total = new Label(totalAmountTxt.getText());


        StackPane.setAlignment(date,Pos.TOP_LEFT);
        StackPane.setMargin(date,new Insets(120,0,0,20));
        StackPane.setAlignment(name,Pos.TOP_LEFT);
        StackPane.setMargin(name,new Insets(150,0,0,125));
        StackPane.setAlignment(age,Pos.TOP_RIGHT);
        StackPane.setMargin(age,new Insets(150,100,0,0));
        StackPane.setAlignment(seller,Pos.BOTTOM_LEFT);
        StackPane.setMargin(seller,new Insets(0,0,20,150));
        StackPane.setAlignment(total,Pos.BOTTOM_RIGHT);
        StackPane.setMargin(total,new Insets(0,40,20,0));


        TableColumn<Medicine,String> particulars = new TableColumn<>("Particulars");
        TableColumn<Medicine,String> nos = new TableColumn<>("Nos(Unit)");
        TableColumn<Medicine,String> rate = new TableColumn<>("Rate (Rs.)");
        TableColumn<Medicine,String> amount = new TableColumn<>("Amount");

        particulars.setCellValueFactory(new PropertyValueFactory<>("name"));
        nos.setCellValueFactory(new PropertyValueFactory<>("nos"));
        rate.setCellValueFactory(new PropertyValueFactory<>("rate"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));


        TableView<Medicine> medicineTable = new TableView<>();
        medicineTable.getColumns().addAll(particulars,nos,rate,amount);
        medicineTable.setItems(selectedMedicineTableView.getItems());
        StackPane pane = new StackPane(medicineTable);
        pane.setMaxWidth(575);
        pane.setPrefWidth(575);
        pane.setPrefHeight(500);

        pane.setAlignment(Pos.CENTER);

        particulars.setMinWidth(240);
        nos.setMinWidth(100);
        rate.setMinWidth(110);
        amount.setMinWidth(110);

        medicineTable.setMaxWidth(575);
        medicineTable.setEditable(true);


        StackPane.setAlignment(pane, Pos.BOTTOM_CENTER);
        StackPane.setMargin(pane,new Insets(200,10,50,10));

        bill.getChildren().addAll(hospitalName,medicineBill,date,_1,_2,patientName,name,patientAge,age,pane,medicineSoldBy,seller,totalAmount,total);

        return bill;
    }

}
//₹