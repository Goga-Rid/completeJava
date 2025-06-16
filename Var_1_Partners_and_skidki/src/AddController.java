package com.example.demoexam;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

import java.sql.SQLException;

public class AddController {

    @FXML
    private TextField typeField;

    @FXML
    public TextField nameField;

    @FXML
    private TextField directorField;

    @FXML
    public TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField innField;


    @Setter
    private Stage stage;


    private HelloController helloController;

    public void setFirstController(HelloController helloController) {
        this.helloController = helloController;
    }

    private PartnerDAO partnerDAO = new PartnerDAO(); // Для доступа к базе данных



    @FXML
    private void saveChanges() {
        try {
        // Собираем данные из полей ввода
        String type = typeField.getText();
        String name = nameField.getText();
        String director = directorField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String inn = innField.getText();



        Partner newPartner = new Partner();
        newPartner.setPartner_type(type);
        newPartner.setPartner_name(name);
        newPartner.setDirector(director);
        newPartner.setEmail(email);
        newPartner.setPhone(phone);
        newPartner.setLegal_address(address);
        newPartner.setInn(inn);

            // Добавляем нового партнера в базу данных
            partnerDAO.create(newPartner);

            // Закрываем окно
            stage.close();
            AlertsUtil.showAlert("Успех", "Новый партнер успешно добавлен!", Alert.AlertType.INFORMATION);

        } catch (SQLException e) {
            // Обработка ошибки базы данных
            AlertsUtil.showAlert("Ошибка", "Не удалось добавить нового партнера: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }



}
