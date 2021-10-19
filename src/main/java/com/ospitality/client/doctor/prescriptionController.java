package com.ospitality.client.doctor;

import com.ospitality.client.common;
import com.ospitality.client.user;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class prescriptionController {

    @FXML
    public ScrollPane prescriptionPage;

    @FXML
    public StackPane pane;

    @FXML
    public StackPane prescripStack;
    public TableView<Medicine> selectedMedicineTableView;
    public TableColumn<Medicine,String> selectedMedicineName;
    public TableColumn<Medicine,String> selectedMedicineAdministration;
    public TableColumn<Medicine,String> selectedMedicineTime;

    @FXML
    private Button backBTN;

    @FXML
    private Label hospitalNameTxt;

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
    private Label dateOfPrescriptionTxt;

    @FXML
    private ListView<String> medicineListView;

    @FXML
    private Button printBTN;

    @FXML
    private TextField searchMedicineTB;


    final DataOutputStream dout = common.getDout();
    final DataInputStream din = common.getDin();
    ObservableList<Medicine> data = FXCollections.observableArrayList(
            new Medicine(null,null,null)
    );

    @FXML
    void initialize(){
        selectedMedicineTableView.setItems(data);
        data = selectedMedicineTableView.getItems();

        doctorNameTxt.setText("Dr. "+ user.getUserName());
        phnoTxt.setText(user.getPhNo());
        departmentTxt.setText(user.getDesignation());

        dateOfPrescriptionTxt.setText(String.valueOf(common.getToday()));

        String patData = patientsAndScansController.patientData;

        patientNameTxt.setText(patData.split("\\./")[0]);
        patientAgeTxt.setText(patData.split("\\./")[1]);
        patientGenderTxt.setText(patData.split("\\./")[3]);

        medicineListView.setDisable(true);

        searchMedicineTB.setOnKeyPressed(
                keyEvent -> {
                    try {
                        if(searchMedicineTB.getText().length()!=0){

                            dout.writeUTF("DPMI");
                            dout.writeUTF(searchMedicineTB.getText());

                            if(din.readBoolean()){
                                medicineListView.setDisable(false);
                                medicineListView.getItems().clear();
                                String str  = din.readUTF();
                                String[] arr = str.split("\\./");
                                for(String x:arr){
                                    medicineListView.getItems().add(x);
                                }
                            }else{
                                medicineListView.setDisable(true);
                                medicineListView.getItems().clear();
                                medicineListView.getItems().add("!!!");
                            }
                        }else{
                            medicineListView.setDisable(true);
                            medicineListView.getItems().clear();
                            medicineListView.getItems().add("!!!");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );


        selectedMedicineName.setCellValueFactory(new PropertyValueFactory<>("name"));
        selectedMedicineAdministration.setCellValueFactory(new PropertyValueFactory<>("administration"));
        selectedMedicineTime.setCellValueFactory(new PropertyValueFactory<>("time"));


        medicineListView.setOnMouseClicked(
                mouseEvent -> {
                    String str = medicineListView.getSelectionModel().getSelectedItem();
                    AnchorPane pane = null;
                    try {
                        pane = common.getAnchorPane();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    TextField administration = (TextField) Objects.requireNonNull(pane).getChildren().get(0);
                    TextField time = (TextField) pane.getChildren().get(1);
                    Button button = (Button) pane.getChildren().get(2);
                    button.setOnAction(
                            actionEvent -> {
                                if(administration.getText().length()!=0 && time.getText().length()!=0){
                                    data.addAll(new Medicine(str,administration.getText(),time.getText()));
                                }
                                Window window = button.getScene().getWindow();
                                window.hide();
                                selectedMedicineTableView.setEffect(null);
                            }
                    );
                    Stage popup = new Stage();
                    popup.initStyle(StageStyle.UNDECORATED);
                    popup.initModality(Modality.WINDOW_MODAL);
                    popup.initOwner(backBTN.getScene().getWindow());
                    popup.setScene(new Scene(pane));
                    popup.show();
                    BoxBlur blur = new BoxBlur(3,3,3);
                    selectedMedicineTableView.setEffect(blur);
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
            PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT,30,0,90,0);
            JobSettings jobSettings = pj.getJobSettings();
            jobSettings.setPrintQuality(PrintQuality.HIGH);
            jobSettings.setPageLayout(pageLayout);
            jobSettings.setPrintQuality(PrintQuality.DRAFT);
            pj.showPrintDialog(prescripStack.getScene().getWindow());
            boolean printed = pj.printPage(prescripStack);
            if(printed){
                pj.endJob();
            }
        }
    }
}
