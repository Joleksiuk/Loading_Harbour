package gui;

import app.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
//Klasa ta jest głównym Kontrolerem interfejsu graficznego

public class SceneController implements Initializable {

    //powołanie obiektów, znajdujących się w aplikacji
    @FXML private Pane simulationPane;
    @FXML public Pane resignedCarsPane;
    //Textfield - miejsce gdzie wprowadza się wartość
    @FXML private TextField lowCapacityCarsTextField;
    @FXML private TextField highCapacityCarsTextField;
    @FXML private TextField startWorkHourTextField;
    @FXML private TextField endWorkHourTextField;
    @FXML private TextField startBreakHourTextField;
    @FXML private TextField endBreakHourTextField;
    @FXML private TextField joiningQueueMaxDelayTextField;
    @FXML private TextField joiningQueueMinDelayTextField;
    @FXML private TextField loadingMaxDelayTextField;
    @FXML private TextField loadingMinDelayTextField;
    @FXML private TextField minutesToResignTextField;
    //Label - napis
    @FXML private Label time;
    @FXML private Label loader1Status;
    @FXML private Label loader2Status;
    @FXML private Label loader3Status;
    @FXML private Label loader4Status;

    //obiekty kolejki i aplikacji
    Queue queue;
    Application application;
    //ustawienia
    Config config;

    int [] loadersX1cords=new int[4];
    int [] loadersX2cords=new int[4];
    int [] loadersYCords =new int[4];

    public void carResigning(Car car){

        Platform.runLater(() -> {
            Rectangle rect = new Rectangle();
            rect.setHeight(15);
            rect.setWidth(53);
            rect.setX(948);
            rect.setY(300);
            rect.setFill(Color.rgb(59,59,59));
            simulationPane.getChildren().add(rect);
        });

        Rectangle rect = new Rectangle();
        simulationPane.getChildren().add(rect);
        rect.setHeight(15);
        if(car.capacity.equals("Low")) {
            rect.setWidth(22);
        }else {rect.setWidth(53);}
        rect.setX(car.x);
        rect.setY(300);
        rect.setFill(car.color);

        car.transition.setNode(rect);
        car.transition.setDuration(Duration.millis(4000));
        car.transition.setByX(948-car.x);
        car.transition.play();

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.config = new Config("E:/GotoweProjekty/PW_Ladunek/src/resources/config.properties");
        initializeTextFields();
        initializeLoaderLabels();
        initializeCords();

    }
    private void initializeLoaderLabels(){
        loader1Status.setText("LOADER 1");
        loader2Status.setText("LOADER 2");
        loader3Status.setText("LOADER 3");
        loader4Status.setText("LOADER 4");
    }
    private void setLoaderLabels(String text){
        loader1Status.setText(text);
        loader2Status.setText(text);
        loader3Status.setText(text);
        loader4Status.setText(text);
    }
    private void initializeTextFields(){
        this.lowCapacityCarsTextField.setText(Integer.toString( config.lowCapacityCars));
        this.highCapacityCarsTextField.setText(Integer.toString( config.highCapacityCars));
        this.startWorkHourTextField.setText(Integer.toString(config.startWorkHour));
        this.endWorkHourTextField.setText(Integer.toString( config.endWorkHour));
        this.startBreakHourTextField.setText(Integer.toString( config.startBreakHour));
        this.endBreakHourTextField.setText(Integer.toString( config.endBreakHour));
        this.joiningQueueMaxDelayTextField.setText(Integer.toString( config.joiningQueueMaxDelay));
        this.joiningQueueMinDelayTextField.setText(Integer.toString( config.joiningQueueMinDelay));
        this.loadingMaxDelayTextField.setText(Integer.toString( config.loadingCarMaxDelay));
        this.loadingMinDelayTextField.setText(Integer.toString( config.loadingCarMinDelay));
        this.minutesToResignTextField.setText(Integer.toString( config.minutesAfterDriversResign));
    }
    public void getValuesFromTextFields(){
        this.config.lowCapacityCars =Integer.parseInt( lowCapacityCarsTextField.getText());
        this.config.highCapacityCars =Integer.parseInt( highCapacityCarsTextField.getText());
        this.config.startWorkHour =Integer.parseInt( startWorkHourTextField.getText());
        this.config.endWorkHour =Integer.parseInt( endWorkHourTextField.getText());
        this.config.startBreakHour =Integer.parseInt( startBreakHourTextField.getText());
        this.config.endBreakHour =Integer.parseInt( endBreakHourTextField.getText());
        this.config.joiningQueueMaxDelay =Integer.parseInt( joiningQueueMaxDelayTextField.getText());
        this.config.joiningQueueMinDelay=Integer.parseInt( joiningQueueMinDelayTextField.getText());
        this.config.loadingCarMaxDelay =Integer.parseInt( loadingMaxDelayTextField.getText());
        this.config.loadingCarMinDelay =Integer.parseInt( loadingMinDelayTextField.getText());
        this.config.minutesAfterDriversResign =Integer.parseInt( minutesToResignTextField.getText());
    }
    private void initializeCords(){
        loadersX1cords[0]=36;
        loadersX1cords[1]=290;
        loadersX1cords[2]=540;
        loadersX1cords[3]=770;

        loadersX2cords[0]=67;
        loadersX2cords[1]=321;
        loadersX2cords[2]=570;
        loadersX2cords[3]=801;

        loadersYCords[0]=463;
        loadersYCords[1]=458;
        loadersYCords[2]=458;
        loadersYCords[3]=454;
    }
    private Rectangle spawnCar(int x, int y, String capacity, Color color, Pane pane){
        Rectangle rect = new Rectangle();
        rect.setHeight(15);
        if(capacity.equals("Low")) {
            rect.setWidth(22);
        }else {rect.setWidth(53);}
        rect.setX(x);
        rect.setY(y);
        rect.setFill(color);
        if(x<900) {
            pane.getChildren().add(rect);
        }
        return rect;
    }
    public void stopSimulation(ActionEvent actionEvent) {
        application.workClock.on = false;
        for(int i=0;i<4;i++) {
            application.loaders[i].retCars.clear();
        }
        application.loadedCars.clear();
        application.queue.cars.clear();
        application.queue.resignedCars.clear();

        clearCargo();
        clearQueue();
        clearResignedCars();
        updateLoaders();

        setLoaderLabels("Stopped");
    }
    public void clearCargo(){
        Platform.runLater(() -> {
            Rectangle rect = new Rectangle();
            rect.setHeight(100);
            rect.setWidth(870);
            rect.setX(0);
            rect.setY(625);
            rect.setFill(Color.rgb(149,117,80));
            simulationPane.getChildren().add(rect);
        });
    }
    public void startSimulation(ActionEvent actionEvent){

        getValuesFromTextFields();
        this.application=new Application(this,config);
        application.start();
        this.queue = application.queue;

    }
    private void updateLoaders(){

        if(application!=null && application.workClock.workTime==true) {

            for (int i = 0; i < application.loaders.length; i++) {

                clearLoader(loadersX1cords[i], loadersYCords[i]);
                if (application.loaders[i].retCars.size()!=0) {
                    spawnCar(loadersX1cords[i], loadersYCords[i], application.loaders[i].retCars.get(0).capacity, application.loaders[i].retCars.get(0).color, simulationPane);
                    if (application.loaders[i].retCars.size() == 2) {
                        spawnCar(loadersX2cords[i], loadersYCords[i], application.loaders[i].retCars.get(1).capacity, application.loaders[i].retCars.get(1).color,simulationPane);
                    }
                }
            }
        }
    }
    private void updateCargo(){

        String tempCapacity;
        int cargoX = 10;
        int cargoY = 630;

        if(application.loadedCars.size()!=0){

            for(int i=0; i<application.loadedCars.size();i++){
                if(cargoX>830){
                    cargoX=10;
                    cargoY+=20;
                }
                tempCapacity = application.loadedCars.get(i).capacity;
                spawnCar(cargoX,cargoY,tempCapacity,application.loadedCars.get(i).color,simulationPane);
                if(tempCapacity=="High"){
                    cargoX +=53;
                }
                else{cargoX+=25;}
            }
        }
    }
    private void clearLoader(int x, int y){
        spawnCar(x,y,"High",Color.rgb(104,88,108),simulationPane);
    }
    public void updateQueue(){

        clearQueue();
        int x = 50;
        final int y = 260;

        for(int i=0; i<queue.cars.size();i++){
            final int fx = x;
            final Car car = queue.cars.get(i);
            car.x = x;
            car.y = y;
            Platform.runLater(() -> {
                car.rectangle= spawnCar(fx, y, car.capacity, car.color, simulationPane);
            });

            if(car.capacity.equals("Low")){
                x+=25;
            }
            else{
                x+=50;
            }

        }
    }
    private void spawnResignedCar(int x, int y, String capacity, Color color, Pane pane){

        Rectangle rect = new Rectangle();
        rect.setWidth(15);
        if(capacity.equals("Low")) {
            rect.setHeight(22);
        }else {rect.setHeight(44);}
        rect.setX(x);
        rect.setY(y);
        rect.setFill(color);
        pane.getChildren().add(rect);
    }
    public void updateResignedCars(){
        int x = 5;
        int y = 40;

        for(int i=0; i<queue.resignedCars.size();i++){
            final int fx = x;
            final int fy = y;
            final Car car = queue.resignedCars.get(i);

            Platform.runLater(() -> {
                spawnResignedCar(fx,fy,car.capacity,car.color,resignedCarsPane);
            });
            if(x<100) {
                x += 18;
            }
            else {
                x=5;
                y+=50;
            }
        }
    }
    private void clearResignedCars(){
        Platform.runLater(() -> {
            Rectangle rect = new Rectangle();
            rect.setHeight(800);
            rect.setWidth(130);
            rect.setX(0);
            rect.setY(30);
            rect.setFill(Color.GRAY);
            resignedCarsPane.getChildren().add(rect);
        });
    }
    private void clearQueue(){
        Platform.runLater(() -> {
            Rectangle rect = new Rectangle();
            rect.setHeight(15);
            rect.setWidth(950);
            rect.setX(50);
            rect.setY(260);
            rect.setFill(Color.rgb(59,59,59));
            simulationPane.getChildren().add(rect);
        });
    }
    public void setTime(int hour, int minute) {

        String textHour;
        String textMinute;
        if (hour < 10 || hour == 0) {
            textHour = '0' + Integer.toString(hour);
        } else {
            textHour = Integer.toString(hour);
        }
        if (minute < 10 || minute == 0) {
            textMinute = '0' + Integer.toString(minute);
        } else {
            textMinute = Integer.toString(minute);
        }
        Platform.runLater(() -> {
            time.setText(textHour + ":" + textMinute);
            updateQueue();
            updateLoaders();
            updateCargo();
            updateLabels();
            updateResignedCars();
        });
    }
    private void updateLabels(){
       if(application.workClock.workTime==true && application.workClock.breakTime==false){
           setLoaderLabels("Working...");
       }
       if(application.workClock.breakTime==true && application.workClock.workTime==true){
           setLoaderLabels("Break time");
       }
       if(application.workClock.breakTime==false && application.workClock.workTime==false){
           initializeLoaderLabels();
       }
    }


}

