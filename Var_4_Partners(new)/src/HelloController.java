package com.example.demoexamnewfour;


import com.example.demoexamnewfour.*;
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

    private HBox lastClickedPartnerBox = null;
    private DBConnection dbConnection = new DBConnection();
    private PartnerDAO partnerDAO = new PartnerDAO();


    /**
     * Метод для загрузки данных о партнерах из базы данных в HBox.
     */
    private void loadDataIntoHBox(List<Partner> partners) {
        try {
            contentBox.getChildren().clear(); // Обязательно очищаем перед добавлением новых данных
            for (Partner partner : partners) {
                // Создаем новый HBox для каждого партнера
                HBox partnerBox = createPartnerBlock(partner);
                // Добавляем созданный HBox в корневой VBox
                contentBox.getChildren().add(partnerBox);
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

//          Расчет общего количества продаж и обновление скидок
            List<Partner> partners = partnerDAO.calculateTotalSalesForPartners();

//          Загрузка данных из БД в боксы
            loadDataIntoHBox(partners);

        } catch (SQLException e) {
            System.out.println("Error Date Base connection!");
        }

    }


    @FXML
    protected void addNewPartner() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("add.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 600, 500);

            AddController secondController = fxmlLoader.getController();
            secondController.setFirstController(this);
            secondController.setStage(stage);
            stage.setTitle("Добавление нового партнёра");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            AlertsUtil.showAlert("Ошибка", "Не удалось открыть форму добавления партнера: " + e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @FXML //Метод для кнопки удаления
    private void deletePartner() {
        try {
            // Получаем выбранного партнера из contentBox
            HBox selectedPartnerBox = (HBox) contentBox.getChildren()
                    .stream()
                    .filter(node -> node.getStyle().contains("-fx-background-color: #ADD8E6"))
                    .findFirst()
                    .orElse(null);

            if (selectedPartnerBox != null) {
                int partnerId = (int) selectedPartnerBox.getUserData(); // Получаем ID партнера из userData
                partnerDAO.delete(partnerId); // Удаление партнера
                updateListPartners(); // Обновление списка после удаления
                AlertsUtil.showAlert("Успех", "Партнер успешно удален!", Alert.AlertType.INFORMATION);
            } else {
                AlertsUtil.showAlert("Внимание", "Выберите партнера для удаления.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            AlertsUtil.showAlert("Ошибка", "Не удалось удалить партнера: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e){
            AlertsUtil.showAlert("Ошибка", "Не удалось удалить партнера: " + e.getMessage(), Alert.AlertType.ERROR);
        }

    }


    /**
     * Метод, который отрисовывает каждый  отдельный Hbox
     */
    private HBox createPartnerBlock(Partner partner) {
        HBox mainHBox = new HBox();
        mainHBox.setSpacing(10);
        mainHBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #BBDCFA");

        // Левая колонка (VBox)
        VBox lVBox = new VBox();
        lVBox.setSpacing(5);
        lVBox.getChildren().addAll(
                new Label(partner.getPartnerType() + " | " + partner.getPartnerName()),
                new Label("Директор: " + partner.getDirector()),
                new Label("Телефон: " + partner.getPhone()),
                new Label("Рейтинг: " + partner.getRating())
        );
        HBox.setHgrow(lVBox, Priority.ALWAYS); // Занимает всё доступное пространство слева


        // Правая колонка (Label)
        VBox rVBox = new VBox();
        rVBox.setSpacing(5);
        rVBox.getChildren().addAll(
                new Label( partner.getDiscount() + "%")
        );
        rVBox.setStyle("-fx-padding: 0 70 0 0; -fx-font-size: 15px"); // 70px от правой границы, чтобы по макету выглядело
        mainHBox.getChildren().addAll(lVBox, rVBox); // Добавляем ЛЕВУЮ И ПРАВУЮ КОЛОНКИ в HBox


        // Установка обработчика кликов
        ChangeClick(mainHBox, partner.getPartnerId());


        return mainHBox;
    }

    /**
     *  Метод для перехода на новую сцену (контроллер)
     */
    private void ChangePartnerClick(MouseEvent event, int partnerId) {
        if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) { // Проверяем, двойной ли клик
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("second.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(fxmlLoader.load(), 600, 500);

                // Получаем контроллер edit.fxml
                SecondController secondController = fxmlLoader.getController();
                // Передаем ID партнера в EditController
                secondController.loadPartnerData(partnerId, stage); // Загружаем данные по id партнера
                secondController.setFirstController(this); // Устанавливаем ссылку на первый контроллер
                stage.setTitle("Изменение заявки");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                AlertsUtil.showAlert("Ошибка", "Не удалось открыть редактор: " + e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
            }
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
    private void ChangeClick(HBox partnerBox, int partnerId) {
        partnerBox.setUserData(partnerId); // Установка ID партнера

        partnerBox.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (lastClickedPartnerBox != null && lastClickedPartnerBox != partnerBox) {
                    // Снимаем выделение с предыдущего элемента
                    lastClickedPartnerBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #BBDCFA");
                }
                // Инвертируем выделение текущего элемента
                if (partnerBox.getStyle().contains("-fx-background-color: #ADD8E6")) {
                    partnerBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #BBDCFA"); // Снимаем выделение
                    lastClickedPartnerBox = null; // Сбрасываем последний выбранный элемент
                } else {
                    partnerBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #0C4882"); // Добавляем выделение
                    lastClickedPartnerBox = partnerBox; // Обновляем последний выбранный элемент
                }
                if (event.getClickCount() == 2) {
                    ChangePartnerClick(event, partnerId); // Передаем ID в метод изменения
                }}
        });
    }


    /**
     * Метод для кнопки, чтобы подтягивать изменения с БД в форму, при нажатии на кнопку!!!
     */
    @FXML
    private void updateListPartners(){
        try{
            dbConnection.getConnection();

            List<Partner> partners = partnerDAO.calculateTotalSalesForPartners();

            loadDataIntoHBox(partners);
            AlertsUtil.showAlert("Успех", "Обновление данных у партнеров ", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            System.out.println("Обновление базы данных произошло с ошибкой");
            AlertsUtil.showAlert("Ошибка", "Обновление  данных в БД произошло с ошибкой: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
