import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application{


	public static void main(String[] args) 
	{launch(args);}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setTitle("Math Tutor");
		
		Organizer organizer = new Organizer(3,1,1,false);
		Scene scene = new Scene(organizer.getRoot());
		
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	

}
