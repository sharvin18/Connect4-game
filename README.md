# Connect4-game
Connect 4 game using Java FX - Intellij IDE

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("init");
    }

    private Controller controller;

    @Override
    public void start (Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();

        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().addAll(menuBar);

        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private MenuBar createMenu() {
        Menu fileMenu = new Menu("File");

        MenuItem newMenuItem = new MenuItem("New game");
        newMenuItem.setOnAction(event -> controller.resetGame());

        MenuItem resetmenuItem = new MenuItem("Reset game");
        resetmenuItem.setOnAction(event -> controller.resetGame());

        SeparatorMenuItem separateMenu = new SeparatorMenuItem();

        MenuItem exitMenuItem = new MenuItem("Exit game");
        exitMenuItem.setOnAction(event -> exitGame());

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu);

        fileMenu.getItems().addAll(newMenuItem, resetmenuItem, separateMenu, exitMenuItem);

        Menu helpMenu = new Menu("Help");

        MenuItem aboutMenu = new MenuItem("About Connect4");
        aboutMenu.setOnAction(event -> aboutConnect4Game());

        SeparatorMenuItem separateHelpMenu = new SeparatorMenuItem();
        MenuItem aboutMeItem = new MenuItem("About me");
        aboutMeItem.setOnAction(event -> aboutMe());

        helpMenu.getItems().addAll(aboutMenu, separateHelpMenu, aboutMeItem);

        menuBar.getMenus().addAll(helpMenu);


        return menuBar;

    }

    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the developer");
        alert.setHeaderText("Sharvin Dedhia");
        alert.setContentText("This is my first desktop game!");
        ButtonType btn = new ButtonType("Good job!");
        alert.getButtonTypes().setAll(btn);
        alert.show();
    }

    private void aboutConnect4Game() {
        Alert alertDialogue = new Alert(Alert.AlertType.INFORMATION);
        alertDialogue.setTitle("About Connect Four");
        alertDialogue.setHeaderText("How to play?");
        alertDialogue.setContentText("Connect Four is a two-player connection game in which the players first choose a " +
                        "color and then take turns dropping colored discs from the top into a seven-column, " +
                        "six-row vertically suspended grid. The pieces fall straight down, occupying the next" +
                        " available space within the column. The objective of the game is" +
                        " to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs." +
                        " Connect Four is a solved game. The first player can always win by playing the right moves.");
        alertDialogue.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }
}
