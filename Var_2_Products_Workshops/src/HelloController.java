package com.example.demoexamnewfour;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


public class HelloController {
    @FXML
    private VBox contentBox;

    @FXML
    private ImageView icon;

    private HBox lastClickedWorkshopBox = null;
    private DBConnection dbConnection = new DBConnection();
    private WorkshopDAO workshopDAO = new WorkshopDAO();


    /**
     * Метод для загрузки данных о цехах из базы данных в HBox.
     */
    private void loadDataIntoHBox(List<Workshop> workshops) {
        try {
            contentBox.getChildren().clear(); // Обязательно очищаем перед добавлением новых данных
            for (Workshop workshop : workshops) {
                // Создаем новый HBox для каждого цеха
                HBox workshopBox = createWorkshopBlock(workshop);
                // Добавляем созданный HBox в корневой VBox
                contentBox.getChildren().add(workshopBox);
            }
            System.out.println("Данные успешно загружены!"); // Уведомление о загрузке данных
        } catch (Exception e) {
            AlertsUtil.showAlert("Ошибка", "Не удалось загрузить данные из базы данных: " + e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    /**
     * Инициализация формы.
     */
    @FXML
    public void initialize() {
        try {
//          Получение подлючения
            dbConnection.getConnection();
            System.out.println("Успешное соединение с Базой Данных!");

//          Получение иконки и её вставка
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/demoexamnewfour/icon.png")));
            icon.setImage(image);

//          Загрузка данных из БД в боксы
            List<Workshop> workshops = workshopDAO.getAllWorkshops();
            loadDataIntoHBox(workshops);

        } catch (SQLException e) {
            System.out.println("Error Date Base connection!");
        }

    }


    /**
     * Метод, который отрисовывает каждый отдельный Hbox для цеха
     */
    private HBox createWorkshopBlock(Workshop workshop) {
        HBox mainHBox = new HBox();
        mainHBox.setSpacing(10);
        mainHBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #BBDCFA");

        // Левая колонка (VBox)
        VBox lVBox = new VBox();
        lVBox.setSpacing(5);
        lVBox.getChildren().addAll(
                new Label("Название цеха: " + workshop.getWorkshopName()),
                new Label("Тип цеха: " + workshop.getWorkshopType()),
                new Label("Количество рабочих: " + workshop.getWorkersCount())
        );
        HBox.setHgrow(lVBox, Priority.ALWAYS); // Занимает всё доступное пространство слева

        mainHBox.getChildren().addAll(lVBox); // Добавляем ЛЕВУЮ КОЛОНКУ в HBox

        // Установка обработчика кликов
        ChangeClick(mainHBox, workshop.getWorkshopId());

        return mainHBox;
    }

    /**
     *  Метод для перехода на новую сцену (контроллер) - не используется для цехов, но оставлен для примера
     */
    private void ChangeWorkshopClick(MouseEvent event, int workshopId) {
        if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) { // Проверяем, двойной ли клик
            // Здесь можно добавить логику для открытия формы редактирования цеха, если потребуется
            AlertsUtil.showAlert("Информация", "Двойной клик по цеху ID: " + workshopId, Alert.AlertType.INFORMATION);
        }
    }


    /**
     * Метод для обновление стиля HBox
     */
    private void updateHBoxStyles() {
        for (javafx.scene.Node node : contentBox.getChildren()) {
            node.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #BBDCFA"); // Возвращаем обычный стиль
        }
    }
    /** Обработчик кликов --- отслеживает нажатие на определенный Hbox и вызывает метод для перехода на новую сцену
     */
    private void ChangeClick(HBox workshopBox, int workshopId) {
        workshopBox.setUserData(workshopId); // Установка ID цеха

        workshopBox.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (lastClickedWorkshopBox != null && lastClickedWorkshopBox != workshopBox) {
                    // Снимаем выделение с предыдущего элемента
                    lastClickedWorkshopBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #BBDCFA");
                }
                // Инвертируем выделение текущего элемента
                if (workshopBox.getStyle().contains("-fx-background-color: #ADD8E6")) {
                    workshopBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #BBDCFA"); // Снимаем выделение
                    lastClickedWorkshopBox = null; // Сбрасываем последний выбранный элемент
                } else {
                    workshopBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #0C4882"); // Добавляем выделение
                    lastClickedWorkshopBox = workshopBox; // Обновляем последний выбранный элемент
                }
                if (event.getClickCount() == 2) {
                    ChangeWorkshopClick(event, workshopId); // Передаем ID в метод изменения
                }}
        });
    }


    /**
     * Метод для кнопки, чтобы подтягивать изменения с БД в форму, при нажатии на кнопку!!!
     */
    @FXML
    private void updateListWorkshops(){
        try{
            dbConnection.getConnection();

            List<Workshop> workshops = workshopDAO.getAllWorkshops();

            loadDataIntoHBox(workshops);
            AlertsUtil.showAlert("Успех", "Обновление данных по цехам ", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            System.out.println("Обновление базы данных произошло с ошибкой");
            AlertsUtil.showAlert("Ошибка", "Обновление  данных в БД произошло с ошибкой: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}

