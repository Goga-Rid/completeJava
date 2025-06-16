package com.example.demoexam;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class HelloController {

    @FXML
    private ImageView icon;

    @FXML
    private VBox dinamicBox;

    private DBConnection dbConnection = new DBConnection();
    private FMJobDAO memberDAO = new FMJobDAO();

    @FXML
    protected void addNewMember() {
        // Логика для добавления нового члена семьи
    }

    @FXML
    protected void deleteMember() {
        // Логика для удаления члена семьи
    }

    @FXML
    public void initialize() {
        try {
            dbConnection.getConnection();
            System.out.println("Успешное соединение с БАЗОЙ ДАННЫХ!");

            Image image = new Image(getClass().getResourceAsStream("/com/example/demoexam/icon.png"));
            icon.setImage(image);

            memberDAO.updateTotalExpenses();
            List<FMJob> members = memberDAO.getAllMembers();
            loadDataIntoHBox(members);
        } catch (SQLException e) {
            System.out.println("При запуске приложения возникли проблемы с соединением с БАЗОЙ ДАННЫХ!");
        }
    }

    private void loadDataIntoHBox(List<FMJob> members) {
        try {
            dinamicBox.getChildren().clear(); // Очищаем перед добавлением новых данных

            for (FMJob member : members) {
                // Создаем новый HBox для каждого члена семьи
                HBox memberBox = createMemberBlock(member);
                // Добавляем созданный HBox в корневой VBox
                dinamicBox.getChildren().add(memberBox);
            }
            System.out.println("Данные успешно загружены!"); // Уведомление о загрузке данных
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось загрузить данные из базы данных: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void ChangeMemberClick(MouseEvent event, int memberId) {
        if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) { // Проверяем, двойной ли клик
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Second.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(fxmlLoader.load(), 1000, 500);
                SecondController secondController = fxmlLoader.getController();
                secondController.loadMemberData(memberId, stage); // Загружаем данные по id члена семьи
                secondController.setFirstController(this); // Устанавливаем ссылку на первый контроллер
                stage.setTitle("Изменение данных члена семьи");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                showAlert("Ошибка", "Не удалось открыть редактор: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void ChangeClick(HBox memberBox, int memberId) {
        memberBox.setUserData(memberId); // Установка ID партнера

        memberBox.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                ChangeMemberClick(event, memberId); // Передаем ID в метод изменения
            }
        });
    }

    private HBox createMemberBlock(FMJob member) throws SQLException {
        HBox mainHBox = new HBox();
        mainHBox.setSpacing(10);
        mainHBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #EBE84B");

        // Левая колонка (VBox)
        VBox lVBox = new VBox();
        lVBox.setSpacing(5);
        lVBox.getChildren().addAll(
                new Label(member.getFio()){{
                    setStyle("-fx-font-size: 20px;");
                }}, // ФИО
                new Label("Количество лет в компании: " + calculateYearsInCompany(member.getStart_date().toLocalDate())), // Стаж работы
                new Label(member.getPosition()), // Должность
                new Label(member.getOrg_name()), // Имя организации
                new Label("Суммарный оклад: " + member.getSalary() + " руб.")
        );

        VBox rVBox = new VBox();
        rVBox.setSpacing(5);
        rVBox.getChildren().addAll(
                new Label(memberDAO.getBudgetStatus(member.getMemberJob_id())){{
                    setStyle("-fx-font-size: 20px;");
                }} // Соотношение трат к доходам
        );



        HBox.setHgrow(lVBox, Priority.ALWAYS);
        mainHBox.getChildren().addAll(lVBox, rVBox); // Добавляем левые и правые элементы в HBox

        ChangeClick(mainHBox, member.getMemberJob_id());

        return mainHBox;
    }

    private int calculateYearsInCompany(LocalDate startDate) {
        if (startDate == null) {
            return 0; // Если дата не указана, возвращаем 0
        }
        LocalDate currentDate = LocalDate.now();
        return Period.between(startDate, currentDate).getYears();
    }


    @FXML
    private void updateListMembers() {
        try {
            List<FMJob> members = memberDAO.getAllMembers();
            loadDataIntoHBox(members);
            showAlert("Успех", "Данные успешно обновлены!", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении данных: " + e.getMessage());
            showAlert("Ошибка", "Не удалось обновить данные: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
