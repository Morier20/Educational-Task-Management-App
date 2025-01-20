package org.example.demo2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;

import java.time.temporal.TemporalAdjusters;
import java.util.LinkedList;

// Json failas užduočių informacijos saugojimui
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

public class HelloController {

    @FXML
    private Button AddElement, CancelButton, weekbtn, ShowAllButton, CancelButton2, ConfirmButton, ConfirmButton2, DeleteElement, EditElement, SearchButton,sortBtn;

    @FXML
    private ListView<Data> List,searchListOutput;

    @FXML
    private AnchorPane PopUpAdd, PopUpEdit,PopUpSearchResult;

    @FXML
    private TextField SearchBar, aprasymasAdd, aprasymasEdit, dalykasAdd, dalykasEdit, idAdd, pavadinimasAdd, pavadinimasEdit;

    @FXML
    private DatePicker terminasAdd,terminasEdit;

    @FXML
    private Label Status;
    private Data selectedForEditing;
    private  Stage stage;
    private LinkedList<Data> ProgrammData;
    private LinkedList<Data> SearchList;
    private final ErrorHandler errorHandler = new ErrorHandler();

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void handleCloseRequest() {

        if (stage != null) {
            stage.setOnCloseRequest(event -> {
                try {
                    writeDataToJson();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
    @FXML
    public void initialize(){
        if (List == null) {
            System.out.println("FXML");
        } else {
            System.out.println("List initialized");
            try {
                ProgrammData = inputFromJson();
            } catch (IOException e) {
                System.err.println("Input from JSON error: " + e.getMessage());
                ProgrammData = new LinkedList<>();
            }
            cellSetings(List);
        }
        if (searchListOutput == null) {
            System.out.println("searchListOutput is not initialized.");
        } else {
            System.out.println("searchListOutput initialized.");
            cellSetings(searchListOutput);
            SearchList  = new LinkedList<Data>();
        }
        showCurrentWeekData();
        controller();

    }
    private void cellSetings(ListView<Data> UncompletedList){
        UncompletedList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Data item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null && !empty) {
                    setGraphic(createItemBox(item));
                } else {
                    setGraphic(null);
                }
            }
            private VBox createItemBox(Data item) {
                VBox vBox = new VBox(4);
                vBox.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0; -fx-border-color: #d3d3d3; -fx-border-width: 0 0 1 0;");

                Label aprasymasLabel = createLabel("Aprašymas: ", item.getAprasymas());
                Label dalykasLabel = createLabel("Dalykas: ", item.getDalykas());
                Label pavadinimasLabel = createLabel("Pavadinimas: ", item.getPavadinimas());
                Label terminasLabel = createLabel("Terminas: ", item.getTerminas().toString());

                vBox.getChildren().addAll(aprasymasLabel, dalykasLabel, pavadinimasLabel, terminasLabel);
                return vBox;
            }

            private Label createLabel(String prefix, String text) {
                return new Label(prefix + text);
            }
        });
    }
    private void addDataToList() {
        idAdd.setStyle("");
        aprasymasAdd.setStyle("");
        dalykasAdd.setStyle("");
        pavadinimasAdd.setStyle("");
        terminasAdd.setStyle("");
        int idValue = -1;
        // Klaidos pranešimai
        StringBuilder errorMessage = new StringBuilder();

        //Patikrinimas ar ID nėra tusčias ir yra skaičius
        if (idAdd.getText().isEmpty()) {
            idAdd.setStyle("-fx-background-color: red;");
            errorMessage.append("The 'ID' field cannot be empty.\n");
        } else {
            try {
                idValue = Integer.parseInt(idAdd.getText());
            } catch (NumberFormatException e) {
                idAdd.setStyle("-fx-background-color: red;");
                errorMessage.append("The 'ID' field must be a valid number.\n");
            }
            boolean idExists = false;
                for (Data item : ProgrammData) {
                    if (item.getId() == idValue) {
                        idExists = true;
                        break;
                    }
                }
                if (idExists) {
                    idAdd.setStyle("-fx-background-color: red;");
                    errorMessage.append("The 'ID' field must be unique. This ID already exists.\n");
                }
            }
        // Patikrinimas ar laukai ne tušti arba įvesti duomeniai ne per trumpi
        if (aprasymasAdd.getText().isEmpty() || aprasymasAdd.getText().length()<3) {
            aprasymasAdd.setStyle("-fx-background-color: red;");
            errorMessage.append("The 'Arpašymas' cannot be empty and must contain more than 3 characters.\n");
        }
        if (dalykasAdd.getText().isEmpty() || dalykasAdd.getText().length()<3) {
            dalykasAdd.setStyle("-fx-background-color: red;");
            errorMessage.append("The 'Dalykas' cannot be empty and must contain more than 3 characters.\n");
        }
        if (pavadinimasAdd.getText().isEmpty() || pavadinimasAdd.getText().length() < 3) {
            pavadinimasAdd.setStyle("-fx-background-color: red;");
            errorMessage.append("The 'Pavadinimas' field cannot be empty and must contain more than 3 characters.\n");
        }
        if (terminasAdd.getValue() == null || terminasAdd.getValue().isBefore(LocalDate.now())) {
            terminasAdd.setStyle("-fx-background-color: red;");
            errorMessage.append("The 'Terminas' field cannot be empty and must be later than the current date.\n");
        }

        // Jeigų yra kažkokia klaidą tai išvedamas klaidos pranešimas atskirame lange
        if (!errorMessage.isEmpty()) {
            errorHandler.showAlert("Error: \n" + errorMessage.toString());
        } else {
            // Jeigu klaidų nėra tai įvesti duomeniai išsaugojami
            Data newData = new Data(idValue,aprasymasAdd.getText(), dalykasAdd.getText(), pavadinimasAdd.getText(), terminasAdd.getValue());
            int insertIndex = 0; // Pozicijos duomenų pridejimui paiešką
            while (insertIndex < ProgrammData.size() && ProgrammData.get(insertIndex).getId() < idValue) {
                insertIndex++;
            }
            ProgrammData.add(insertIndex, newData); // Duomenų pridėjimas į Vienakrypti tiesinį sarašą (AddNodePosition)
            showDataList();
            PopUpAdd.setVisible(false);
        }

    }
    private void showDataList(){
        Status.setText("All Tasks");
        List.getItems().setAll(ProgrammData);
    }
    public void outputSearch(){
        searchListOutput.getItems().setAll(SearchList);
        PopUpSearchResult.setVisible(true);
    }
    @FXML
    private void deleteSelectedItem() {
        // Pasirinkto elemento saugojimas
        Data selectedItem = searchListOutput.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Elementas trinamas is pagrindinio sarašo ir iš search sarašo
            ProgrammData.remove(selectedItem);
            SearchList.remove(selectedItem);

            // Atnaujinam UI
            showDataList();
            outputSearch();

            errorHandler.showConfirmation("Element successfully deleted: " + selectedItem.getId());
        } else {
            errorHandler.showAlert("Please select an task to delete.");
        }
    }
    private void showCurrentWeekData() {
        Status.setText("Current Week");

        LocalDate today = LocalDate.now();

        // Radom savaites pradžios data
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // Radom savaites pabaigos data
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List.getItems().clear();

        // Pridedam tik atitinkančius salygui elementus
        for (Data data : ProgrammData) {
            LocalDate terminas = data.getTerminas();
            if (terminas != null && !terminas.isBefore(startOfWeek) && !terminas.isAfter(endOfWeek)) {
                List.getItems().add(data);
            }
        }
    }

    public void edit() {
        selectedForEditing = searchListOutput.getSelectionModel().getSelectedItem();

        if (selectedForEditing != null) {
            // Redagavimo laukai užpilduomį pasirinkto uždavinio duomenimis
            aprasymasEdit.setText(selectedForEditing.getAprasymas());
            dalykasEdit.setText(selectedForEditing.getDalykas());
            pavadinimasEdit.setText(selectedForEditing.getPavadinimas());
            terminasEdit.setValue(selectedForEditing.getTerminas());

            // Redagavimo menių atidarymas
            PopUpEdit.setVisible(true);

            ConfirmButton2.setOnAction(event -> {
                confirmEdit();
                search();
            });
            CancelButton2.setOnAction(event -> {
                aprasymasEdit.setText("");
                dalykasEdit.setText("");
                pavadinimasEdit.setText("");
                terminasEdit.setValue(null);
                PopUpEdit.setVisible(false);
            });

        } else {
            // Jeigu elementas nepasirinktas išvedamas klaidos pranešimas
            errorHandler.showAlert("Chose task for editing.");
            System.out.println("No task selected for editing.");
        }
    }
    @FXML
    private void confirmEdit() {

        aprasymasEdit.setStyle("");
        dalykasEdit.setStyle("");
        pavadinimasEdit.setStyle("");
        terminasEdit.setStyle("");

        StringBuilder errorMessage = new StringBuilder();

        if (aprasymasEdit.getText().isEmpty() || aprasymasEdit.getText().length() < 3) {
            aprasymasEdit.setStyle("-fx-background-color: red;");
            errorMessage.append("The 'Arpašymas' cannot be empty and must contain more than 3 characters.\n");
        }


        if (dalykasEdit.getText().isEmpty() || dalykasEdit.getText().length()<3) {
            dalykasEdit.setStyle("-fx-background-color: red;");
            errorMessage.append("The 'Dalykas' cannot be empty and must contain more than 3 characters.\n");
        }

        if (pavadinimasEdit.getText().isEmpty() || pavadinimasEdit.getText().length() < 3) {
            pavadinimasEdit.setStyle("-fx-background-color: red;");
            errorMessage.append("The 'Pavadinimas' field cannot be empty and must contain more than 3 characters.\n");
        }


        if (terminasEdit.getValue() == null || terminasEdit.getValue().isBefore(LocalDate.now())) {
            terminasEdit.setStyle("-fx-background-color: red;");
            errorMessage.append("The 'Terminas' field cannot be empty and must be later than the current date.\n");
        }

        //Jeigu yra klaida išvedamas pranešimas
        if (errorMessage.length() > 0) {
            errorHandler.showAlert("Input error:\n" + errorMessage.toString());
        } else {
            // Jeigu klaidų nėra atnaujinom duomenis
            selectedForEditing.setAprasymas(aprasymasEdit.getText());
            selectedForEditing.setDalykas(dalykasEdit.getText());
            selectedForEditing.setPavadinimas(pavadinimasEdit.getText());
            selectedForEditing.setTerminas(terminasEdit.getValue());

            // Atnaujinom duomenis sarašuose
            int index = ProgrammData.indexOf(selectedForEditing);
            ProgrammData.set(index, selectedForEditing);


            int searchIndex = SearchList.indexOf(selectedForEditing);
            SearchList.set(searchIndex, selectedForEditing);

            showDataList();
            outputSearch();

            PopUpEdit.setVisible(false);

            errorHandler.showConfirmation("Task successfully edited: " + selectedForEditing.getId());
        }
    }

    public void search() {
        String searchInput = SearchBar.getText(); // paieškos lauko ivestos informacijos saugojimas
        SearchList.clear();
        for (Data i : ProgrammData) {
            if (i.getPavadinimas().equalsIgnoreCase(searchInput) ||
                    i.getDalykas().equalsIgnoreCase(searchInput)) {
                SearchList.addFirst(i);
            } // patikrinimas ar yra vartotojo užklausai atitinkančių laukų
        }
        outputSearch();
        // UI elementai skirti valdyti rastus užduotis
        EditElement.setOnAction(event->{
            edit();
        });
        DeleteElement.setOnAction(event -> {
            deleteSelectedItem();
        });
    }

    public void controller() {
        AddElement.setOnAction(event->{
            PopUpAdd.setVisible(true);
            ConfirmButton.setOnAction(click->{
                addDataToList();
            });
            CancelButton.setOnAction(click->{
                PopUpAdd.setVisible(false);
            });
        });
        ShowAllButton.setOnAction(event->{
            if(PopUpSearchResult.isVisible()){
                SearchBar.clear();
                PopUpSearchResult.setVisible(false);
            }
            showDataList();
        });
        weekbtn.setOnAction(event->{
            if(PopUpSearchResult.isVisible()){
                SearchBar.clear();
                PopUpSearchResult.setVisible(false);
            }
            showCurrentWeekData();
        });
        SearchButton.setOnAction(event->{
            if(!SearchBar.getText().isEmpty())
            {
                Status.setText("Search Result");
                search();
                PopUpSearchResult.setVisible(true);
            }
        });
    }
    private void writeDataToJson () throws IOException {
        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(new File ("data.json"),ProgrammData);
    }
    public static LinkedList<Data> inputFromJson() throws IOException {
        ObjectReader reader = objectMapper.readerFor(new TypeReference<LinkedList<Data>>() {});
        return reader.readValue(new File("data.json"));
    }
}
