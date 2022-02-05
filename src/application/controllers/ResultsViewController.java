package application.controllers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import util.CryptographyService;

public class ResultsViewController {
	
	private Stage stage = new Stage();
	private Scene scene;
	
	private TableView<Result> table;
	
	public ResultsViewController() {
		initialize();
	}
	
	private void initialize() {
		TableColumn<Result, String> column1 = new TableColumn<>("Korisnik");
		column1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Result, String>, ObservableValue<String>>(){
			
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<Result, String> f){
                return new SimpleStringProperty(f.getValue().getUsername());
            }
		});
		
		TableColumn<Result, String> column2 = new TableColumn<>("Vrijeme");
		column2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Result, String>, ObservableValue<String>>(){
			
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<Result, String> f){
                return new SimpleStringProperty(f.getValue().getTime());
            }
		});
		
		TableColumn<Result, String> column3 = new TableColumn<>("Rezultat");
		column3.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Result, String>, ObservableValue<String>>(){
			
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<Result, String> f){
                return new SimpleStringProperty(f.getValue().getResult());
            }
		});
		
		ObservableList<Result> items = FXCollections.observableArrayList();
		List<String> lines = readLines();
		if(lines != null) {
			for(String line : lines) {
				String[] parts = line.split(",");
				Result result = new Result(parts[0], parts[1], parts[2]);
				items.add(result);
			}
		}
		
		table = new TableView<>(items);
		table.setEditable(false);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().setAll(column1, column2, column3);
	}
	
	private List<String> readLines() {
		try {	
			byte[] bytes = Files.readAllBytes(Paths.get(CryptographyService.resultsFilePath));
			if(bytes.length != 0) {
				String resultsList = CryptographyService.decryptData(bytes, CryptographyService.resultsDataFilePath);
				String[] results = resultsList.split("#");
				List<String> list = new ArrayList<>();
				for(String result : results) {
					list.add(result);
				}
				
				return list;
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public void show() {
		scene = new Scene(table, 600, 400);
		stage.setScene(scene);
		stage.setTitle("CryptQuiz");
		stage.show();
	}
}

class Result {
	private String username;
	private String time;
	private String result;
	
	public Result(String username, String time, String result) {
		this.username = username;
		this.time = time;
		this.result = result;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getTime() {
		return this.time;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getResult() {
		return this.result;
	}
}
