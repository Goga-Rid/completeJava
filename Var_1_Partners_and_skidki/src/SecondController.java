package com.example.demoexam;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class SecondController {

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


    private HelloController helloController;
    private Stage stage;
    private Partner selectedPartner;


    private PartnerDAO partnerDAO = new PartnerDAO(); // Для доступа к базе данных


    public void loadPartnerData(int partnerId, Stage stage) {
        try {
            System.out.println("Загружаем данные для партнера с ID: " + partnerId);
            selectedPartner = partnerDAO.getPartner(partnerId);
            this.stage = stage;
            if (selectedPartner != null) {
                typeField.setText(selectedPartner.getPartner_type());
                nameField.setText(selectedPartner.getPartner_name());
                directorField.setText(selectedPartner.getDirector());
                emailField.setText(selectedPartner.getEmail());
                phoneField.setText(selectedPartner.getPhone());
                addressField.setText(selectedPartner.getLegal_address());
                innField.setText(selectedPartner.getInn());
            } else {
                System.out.println("Партнер с ID " + partnerId + " не найден.");
            }
        } catch (SQLException e) {
            AlertsUtil.showAlert("Ошибка", "Не удалось загрузить данные партнёра: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void setFirstController(HelloController helloController) {
        this.helloController = helloController;
    }

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

            // Обновляем статус заявки в базе данных
            partnerDAO.update(
                    selectedPartner.getPartnerId(),
                    type,
                    name,
                    director,
                    email,
                    phone,
                    address,
                    inn
            );

            stage.close();



            // Показываем уведомление об успешном обновлении
            AlertsUtil.showAlert("Успех", "Данные партнера успешно обновлены!", Alert.AlertType.INFORMATION);

        } catch (SQLException e) {
            // Обработка ошибки базы данных
            AlertsUtil.showAlert("Ошибка", "Не удалось обновить Данные партнера: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
