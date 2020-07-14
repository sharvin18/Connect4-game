import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int Column = 7;
	private static final int Row = 6;
	private static final int Circle_Diameter = 80;
	private static final String discColour1 = "#24303E";
	private static final String discColour2 = "#4CAA88";

	private static String playerOne = "Player one";
	private static String playerTwo = "Player two";

	private boolean isPlayerOneTurn = true;

	private Disc[][] insertedDiscsArray = new Disc[Row][Column];

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscPane;

	@FXML
	public Label playerOneLabel;

	@FXML
	public Label turnLabel;

	@FXML
	public TextField playerOneTextField, playerTwoTextField;

	@FXML
	public Button setNamesButton;

	private boolean isAllowedToInsert = true;

	public void createPlayground() {

		Shape rectangleShape = createGameStructuralGrid();
		rootGridPane.add(rectangleShape, 0, 1);

		List<Rectangle> rectangleList = createClickableColumns();

		for (Rectangle rectangle: rectangleList) {
			rootGridPane.add(rectangle, 0,1);
		}

		setNamesButton.setOnAction(event -> {
			String playerOne = playerOneTextField.getText();
			String playerTwo = playerTwoTextField.getText();
			playerOneLabel.setText(isPlayerOneTurn? playerOne : playerTwo);
		});
	}

	private Shape createGameStructuralGrid() {

		Shape rectangleShape = new Rectangle((Column+1) * Circle_Diameter, (Row + 1) * Circle_Diameter);
		for ( int row = 0; row < Row; row++) {
			for (int col = 0; col < Column; col++) {

				Circle circle = new Circle();
				circle.setRadius(Circle_Diameter / 2);
				circle.setCenterX(Circle_Diameter / 2);
				circle.setCenterY(Circle_Diameter / 2);
				circle.setSmooth(true);

				circle.setTranslateX(col * (Circle_Diameter + 5) + Circle_Diameter / 4);
				circle.setTranslateY(row * (Circle_Diameter + 5) + Circle_Diameter / 4);

				rectangleShape = Shape.subtract(rectangleShape, circle);
			}
		}
		rectangleShape.setFill(Color.WHITE);

		return rectangleShape;
	}

	private List<Rectangle> createClickableColumns() {

		List<Rectangle> rectangleList = new ArrayList<>();

		for (int col = 0; col < Column; col++) {
			Rectangle rectangle = new Rectangle(Circle_Diameter, (Row + 1) * Circle_Diameter);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (Circle_Diameter + 5) + Circle_Diameter / 4);

			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

			final int column = col;
			rectangle.setOnMouseClicked(event -> {
				if (isAllowedToInsert) {
					isAllowedToInsert = false;
					insertDisc(new Disc(isPlayerOneTurn), column);
				}
			});

			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private void insertDisc(Disc disc, int column){
		int row = Row -1;

		while(row >= 0) {
			if (getDiscIfPresent(row, column) == null)
				break;
				row--;
		}
		if(row < 0)
			return;


		insertedDiscsArray[row][column] = disc;
		insertedDiscPane.getChildren().add(disc);
		int currentRow = row;

		disc.setTranslateX(column * (Circle_Diameter + 5) + Circle_Diameter / 4);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(row * (Circle_Diameter + 5) + Circle_Diameter / 4);

		translateTransition.setOnFinished(event -> {
			isAllowedToInsert = true;
			if (gameEnded(currentRow, column)) {
				gameOver();
				return;
			}
			isPlayerOneTurn = !isPlayerOneTurn;

			playerOneLabel.setText(isPlayerOneTurn? playerOne : playerTwo);

		});
		translateTransition.play();
	}

	private boolean gameEnded(int row, int column){

		List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)
										.mapToObj(r-> new Point2D(r, column))
										.collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
				.mapToObj(c-> new Point2D(row, c))
				.collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row - 3, column + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
									.mapToObj(i -> startPoint1.add(i, -i))
									.collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row - 3, column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startPoint2.add(i, i))
				.collect(Collectors.toList());

		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
						|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;

		for (Point2D point: points) {
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

			if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {

				chain++;
				if (chain == 4) {
					return true;
				}
			} else {
					chain = 0;
				}

			}
		return false;
		}

	private Disc getDiscIfPresent(int row, int column) {
		if (row >= Row || row<0 || column >= Column || column <0)
			return null;

			return insertedDiscsArray[row][column];

	}

	private void gameOver() {
		String winner = isPlayerOneTurn ? playerOne : playerTwo;
		System.out.println("Winner is: " + winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("The winner is " + winner);
		alert.setContentText("Do you want to play again? ");

		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No");
		alert.getButtonTypes().setAll(yesBtn, noBtn);

		Platform.runLater(() -> {
			Optional<ButtonType> buttonClicked = alert.showAndWait();

			if (buttonClicked.isPresent() && buttonClicked.get() == yesBtn) {
				resetGame();
			}else {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public void resetGame() {
		insertedDiscPane.getChildren().clear();

		for (int row = 0; row < insertedDiscsArray.length; row++) {
			for (int col = 0; col < insertedDiscsArray[row].length; col++) {
				insertedDiscsArray[row][col] = null;
			}
		}
		isPlayerOneTurn = true;
		playerOneLabel.setText(playerOne);

		createPlayground();
	}

	private static class Disc extends Circle {

		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove) {
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(Circle_Diameter/2);
			setFill(isPlayerOneMove? Color.valueOf(discColour1): Color.valueOf(discColour2));
			setCenterX(Circle_Diameter/2);
			setCenterY(Circle_Diameter/2);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
