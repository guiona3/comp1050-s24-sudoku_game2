package newSudoku;

import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

public class woRand extends Application {

    private int[][] solution;
    private int[][] puzzle;
    private TextArea messageArea;

    @Override
    public void start(Stage primaryStage) {
      primaryStage.setTitle("Sudoku Puzzle");
      primaryStage.setFullScreen(true);
      solution = generateSolution();
        puzzle = generatePuzzle(solution, 20);
        messageArea = new TextArea();

        // Default UI setup
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100.0 / 18);
        for (int i = 0; i < 9; i++) {
            grid.getColumnConstraints().add(columnConstraints);
        }

        TextField[][] cells = new TextField[9][9];
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField cell = new TextField();
                cell.setPrefWidth(40);
                cell.setAlignment(Pos.CENTER);
                int value = puzzle[row][col];
                if (value != 0) {
                    cell.setText(String.valueOf(value));
                    cell.setEditable(false);
                }
                cells[row][col] = cell;
                grid.add(cell, col, row);
            }
        }

        Button checkButton = new Button("Check");
        checkButton.setPrefWidth(100);
        checkButton.setOnAction(e -> {
            if (isSolved(cells)) {
                appendMessage("Congratulations! Puzzle solved correctly.");
            } else {
                appendMessage("Oops! Puzzle solved incorrectly.");
            }
        });

        Text promptText = new Text("Please Enter Number of Blank Cells (Easy: 20 | Medium: 35 | Hard: 45):");
        TextField inputField = new TextField();
        inputField.setPrefWidth(100);
        inputField.setAlignment(Pos.CENTER);

        Button customButton = new Button("Apply");
        customButton.setAlignment(Pos.CENTER_LEFT);
        customButton.setPrefWidth(100);
        customButton.setOnAction(e -> setCustomDifficulty(cells, inputField.getText()));

        HBox customInput = new HBox(10);
        customInput.setAlignment(Pos.CENTER);
        inputField.setPrefWidth(150);
        customInput.getChildren().addAll(promptText, inputField, customButton);

        grid.add(checkButton, 4, 16);
        grid.add(customInput, 0, 10);
        grid.add(customButton, 2, 12);
        grid.add(inputField, 2, 11);
        grid.add(messageArea, 0, 13, 9, 1);

        Scene scene = new Scene(grid, 450, 550);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sudoku Puzzle");
        primaryStage.show();
    }

    private void appendMessage(String message) {
        messageArea.appendText(message + "\n");
    }

    private void setCustomDifficulty(TextField[][] cells, String userInput) {
        try {
            int numCellsToBlank = Integer.parseInt(userInput);
            if (numCellsToBlank >= 0 && numCellsToBlank <= 81) {
                puzzle = generatePuzzle(solution, numCellsToBlank);
                refreshPuzzle(cells);
            } else {
                appendMessage("Please enter a number between 0 and 81.");
            }
        } catch (NumberFormatException e) {
            appendMessage("Invalid input. Please enter a valid number.");
        }
    }

    private void refreshPuzzle(TextField[][] cells) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField cell = cells[row][col];
                int value = puzzle[row][col];
                cell.setText(value != 0 ? String.valueOf(value) : "");
                cell.setEditable(value == 0);
            }
        }
    }

    private boolean isSolved(TextField[][] cells) {
        int[][] userSolution = new int[9][9];
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField cell = cells[row][col];
                String value = cell.getText().trim();
                if (!value.isEmpty()) {
                    userSolution[row][col] = Integer.parseInt(value);
                } else {
                    return false;
                }
            }
        }
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (userSolution[row][col] != solution[row][col]) {
                    if (userSolution[row][col] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int[][] generatePuzzle(int[][] solution, int numCellsToBlank) {
        int[][] puzzle = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(solution[i], 0, puzzle[i], 0, 9);
        }
        blankCells(puzzle, numCellsToBlank);
        return puzzle;
    }

    private int[][] generateSolution() {
        int[][] puzzle = new int[9][9];
        solveSudoku(puzzle);
        return puzzle;
    }

    private void blankCells(int[][] puzzle, int numCellsToBlank) {
        Random random = new Random();
        int count = 0;
        while (count < numCellsToBlank) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);
            if (puzzle[row][col] != 0) {
                puzzle[row][col] = 0;
                count++;
            }
        }
    }

    private boolean solveSudoku(int[][] puzzle) {
        return solveSudokuUtil(puzzle, 0, 0);
    }

    private boolean solveSudokuUtil(int[][] puzzle, int row, int col) {
        if (row == 9) {
            row = 0;
            if (++col == 9) {
                return true;
            }
        }
        if (puzzle[row][col] != 0) {
            return solveSudokuUtil(puzzle, row + 1, col);
        }
        for (int num = 1; num <= 9; num++) {
            if (isValidMove(puzzle, row, col, num)) {
                puzzle[row][col] = num;
                if (solveSudokuUtil(puzzle, row + 1, col)) {
                    return true;
                }
            }
        }
        puzzle[row][col] = 0;
        return false;
    }

    private boolean isValidMove(int[][] puzzle, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (puzzle[row][i] == num || puzzle[i][col] == num) {
                return false;
            }
        }
        int boxRow = row - row % 3;
        int boxCol = col - col % 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (puzzle[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
// adding to get github repo to work
