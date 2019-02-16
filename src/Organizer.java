
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Organizer {

	private BorderPane main;
	private int level, score, timeLeft;
	private Button[] questions;
	private int[] numbers;
	private int[] answers;
	private Label answerLabel, scoreLabel, countDownLabel;
	private boolean playing;
	private int operator;
	private boolean allowNeg;
	private VBox center;
	private int maximum;
	
	public Organizer(int level, int operator, int time, boolean allowNeg) {
		
		main = new BorderPane();
		allowNeg=false;
		numbers = new int[8];
		makeButtons();
		answers = new int[4];
		playing = false;
		setupProgram();
		getOptions();
	}
	
	private void makeButtons() {
		questions = new Button[4];
		for (int i=0;i<questions.length;i++) {
			questions[i] = new Button();
			questions[i].setOnAction(e -> checkAnswer(e));
			questions[i].setPrefSize(150, 100);
			if (operator==3) questions[i].setPrefSize(185, 100);
			questions[i].setStyle("-fx-font-size:30;");
		}
	}

	private void setupProgram() {
		answerLabel = new Label();
		answerLabel.setStyle("-fx-font-size:30;");
		answerLabel.setPrefWidth(70);
		if (operator==3) answerLabel.setPrefWidth(40);
		answerLabel.setAlignment(Pos.CENTER);
		
		scoreLabel = new Label("Score: " + score);
		scoreLabel.setStyle("-fx-font-size:30;-fx-text-fill: #00AA00;");
		
		countDownLabel = new Label("Time: " + timeLeft);
		countDownLabel.setStyle("-fx-font-size:30;-fx-text-fill: #00AA00;");
		
		makeQuestion(); //generate first question

		center = new VBox();
		HBox middle = new HBox();
		middle.setSpacing(20);
		middle.setAlignment(Pos.CENTER);
		center.setSpacing(20);
		center.setPadding(new Insets(10));
		center.setStyle("-fx-background-color: #4286f4;");
		center.setAlignment(Pos.CENTER);
		center.setPrefHeight(400);
		
		middle.getChildren().addAll(questions[0],answerLabel,questions[1]);
		
		if (level==1) {
			center.getChildren().add(middle);
		}
		else if (level==2) {
			center.getChildren().addAll(questions[2],middle);
		}
		else {
			center.getChildren().addAll(questions[2],middle, questions[3]);
		}
		
		main.setCenter(center);
		
		HBox bottom = new HBox(scoreLabel);
		bottom.setPrefWidth(500);
		bottom.setAlignment(Pos.CENTER);
		main.setBottom(bottom);
		
		HBox top = new HBox(countDownLabel);
		top.setPrefWidth(500);
		top.setAlignment(Pos.CENTER);
		main.setTop(top);
	}

	private void makeQuestion() {
		for (int i=0;i<numbers.length;i++) {
			numbers[i] = (int)(Math.random()*(maximum+1));
		}
		
		for (int i=0;i<level+1;i++) {
			switch (operator) {
			case 0://Adding
				questions[i].setText(numbers[i] + " + " + numbers[7-i]);
				answers[i] = numbers[i] + numbers[7-i];
				break;
			case 1://subtracting
				if (!allowNeg) {
					if(numbers[i]<numbers[7-i]) {
						int temp = numbers[7-i];
						numbers[7-i] = numbers[i];
						numbers[i]=temp;
					}
				}
				questions[i].setText(numbers[i] + " - " + numbers[7-i]);
				answers[i] = numbers[i] - numbers[7-i];
				break;
			case 2://multiplying
				questions[i].setText(numbers[i] + " x " + numbers[7-i]);
				answers[i] = numbers[i] * numbers[7-i];
				break;
			case 3://dividing
				if (numbers[7-i]==0) numbers[7-i]=1;
				questions[i].setText(
						(numbers[i]*numbers[7-i]) + " " + (char)247 +" " + numbers[7-i]);
				answers[i] = numbers[i];
				break;
			}
			questions[i].setId(""+answers[i]);
		}
		int pickOne = (int)(Math.random()*(level+1));
		answerLabel.setText(""+answers[pickOne]);
		
	}

	public void checkAnswer(Event e) {
		if (playing) {
			Button thisButton = (Button) e.getSource();
			if(answerLabel.getText().equals(thisButton.getId())) {
				score ++;
				scoreLabel.setText("Score: " + score);
				scoreLabel.setStyle("-fx-font-size:30;-fx-text-fill: #00AA00;");
			}
			else {
				scoreLabel.setStyle("-fx-font-size:30;-fx-text-fill: #AA0000;");
				if (score>0) score --;
				scoreLabel.setText("Score: " + score);
			}
			makeQuestion();
		}
	}
	
	public void startProgram() {
		playing=true;
		KeyFrame kf = new KeyFrame(Duration.millis(1000),e->countDown());
		Timeline counting = new Timeline(kf);
		counting.setCycleCount(timeLeft+1); //Number of cycles depends on the delta and the delta increment
		counting.play();
	}
	private void countDown() {
		if (timeLeft<=0) {
			playing= false;
			endGame();
		}
		else {
			timeLeft --;
			countDownLabel.setText("Time: " + timeLeft);
		}
	}

	private void endGame() {
		Pane newTop = new Pane();
		HBox newBottom = new HBox();
		Button startOver = new Button("Play Again");
		Button exit = new Button("Exit");
		startOver.setOnAction(e->getOptions());
		exit.setOnAction(e->Platform.exit());
		newBottom.setAlignment(Pos.CENTER);
		startOver.setPrefWidth(100);
		exit.setPrefWidth(100);
		newBottom.getChildren().addAll(startOver,exit);
		newBottom.setStyle("-fx-background-color: #8815a5;");
		main.setTop(newTop);
		main.setBottom(newBottom);
		
		center.setStyle("-fx-background-color: #8815a5;");
		center.getChildren().clear();
		Label topLabel = new Label("Game Over!");
		topLabel.setStyle("-fx-font:70 Georgia;-fx-text-fill: #FFFFFF;");
		Label middle1Label = new Label("Final Score:");
		middle1Label.setStyle("-fx-font:50 Georgia;-fx-text-fill: #FFFFFF;");
		Label middle2Label = new Label("" + score);
		middle2Label.setStyle("-fx-font:70 Impact;-fx-text-fill: #FFFFFF;");
		Label bottomLabel = new Label("Good Job!");
		bottomLabel.setStyle("-fx-font:70 Georgia;-fx-text-fill: #FFFFFF;");
		center.getChildren().addAll(topLabel,middle1Label,middle2Label,bottomLabel);
	}

	private void getOptions() {
		VBox pane = new VBox();
		pane.setSpacing(20);
		pane.setStyle("-fx-background-color: #e5913d;");
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Options");
		stage.setMinHeight(250);
		stage.setMinWidth(300);
		
		HBox options = new HBox();
		options.setSpacing(30);
		options.setAlignment(Pos.CENTER);
		
		VBox levelsBox = new VBox();
		levelsBox.setAlignment(Pos.CENTER);
		Label levelsLabel = new Label("Level");
		levelsLabel.setStyle("-fx-font:20 Arial;");
		ComboBox levels = new ComboBox();
		levels.getItems().addAll(1,2,3);
		levels.setValue(1);
		levelsBox.getChildren().addAll(levelsLabel,levels);
		
		HBox maxValue = new HBox();
		maxValue.setAlignment(Pos.CENTER);
		Label maxValLabel = new Label("Maximum Value: ");
		maxValLabel.setStyle("-fx-font:20 Arial;");
		ComboBox maxVal = new ComboBox();
		maxVal.getItems().addAll(4,5,6,7,8,9,10,11,12);
		maxVal.setValue(12);
		maxValue.getChildren().addAll(maxValLabel,maxVal);
		
		VBox timeBox = new VBox();
		timeBox.setAlignment(Pos.CENTER);
		Label timeLabel = new Label("Game\nLength");
		timeLabel.setStyle("-fx-font:20 Arial;");
		ComboBox time = new ComboBox();
		time.getItems().addAll(30,60,120);
		time.setValue(60);
		timeBox.getChildren().addAll(timeLabel,time);
		
		VBox mathOpps = new VBox();
		mathOpps.setSpacing(5);
		RadioButton add = new RadioButton("+");
		add.setStyle("-fx-font:20 Georgia;");
		RadioButton sub = new RadioButton("-");
		sub.setStyle("-fx-font:20 Georgia;");
		RadioButton mul = new RadioButton("x");
		mul.setStyle("-fx-font:20 Georgia;");
		RadioButton div = new RadioButton(""+(char)247);
		div.setStyle("-fx-font:20 Georgia;");
		ToggleGroup math = new ToggleGroup();
		add.setToggleGroup(math);
		sub.setToggleGroup(math);
		mul.setToggleGroup(math);
		div.setToggleGroup(math);
		add.setSelected(true);
		mathOpps.getChildren().addAll(add,sub,mul,div);
		
		//CheckBox allNeg = new CheckBox("Allow Negatives?");
		
		options.getChildren().addAll(levelsBox,mathOpps,timeBox);
		
		Button okButton = new Button();
		okButton.setText("Start Game");
		okButton.setOnAction(e -> stage.close());
		
		pane.getChildren().addAll(/*allNeg,*/maxValue,options,okButton);
		pane.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.showAndWait();
		
		level = (int) levels.getValue();
		if(add.isSelected()) operator=0;
		else if (sub.isSelected()) operator=1;
		else if (mul.isSelected()) operator=2;
		else operator=3;
		//allowNeg=allNeg.isSelected();
		score = 0;
		timeLeft=(int) time.getValue();
		maximum = (int) maxVal.getValue();
		makeButtons();
		makeQuestion();
		playing = true;
		setupProgram();
		startProgram();
	}

	public Pane getRoot() {
		return main;
	}

}
