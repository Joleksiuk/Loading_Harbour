package app;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

//Klasa Config zawiera atrybuty ustawień symulacji, w momencie gdy otwierany jest interfejs graficzny, domyślne wartości
//ustawień graficznych są pobierane z pliku data.properties do obiektu Config, jeśli użytkownik wpisze w pola tekstowe inne ustawienia
//a następnie wcisnie przycisk START atrybuty obiektu config zostaną nadpisane wartościami wprowadzonymi przez użytkownika.
public class Config {

    static int hoursLimit = 24;
    static int minutesLimit = 60;
    static int secondsLimit = 60 ;
    public int lowCapacityCars ;
    public int highCapacityCars ;
    public int numberOfLoaders ;
    public int startWorkHour ;
    public int endWorkHour ;
    public int startBreakHour;
    public int endBreakHour ;
    public int minutesAfterDriversResign;
    public int pasteOfTime;
    public int joiningQueueMinDelay;
    public int joiningQueueMaxDelay;
    public int loadingCarMinDelay;
    public int loadingCarMaxDelay;

    public Config(String inputStream){
        try(InputStream input = new FileInputStream(inputStream)){

            //Pobieranie wartosci z pliku data.properties
            Properties prop = new Properties();
            prop.load(input);
            this.lowCapacityCars = Integer.parseInt(prop.getProperty("lowCapacityCars"));
            this.highCapacityCars=Integer.parseInt(prop.getProperty("highCapacityCars"));
            this.numberOfLoaders=Integer.parseInt(prop.getProperty("numberOfLoaders"));
            this.startWorkHour=Integer.parseInt(prop.getProperty("startWorkHour"));
            this.endWorkHour=Integer.parseInt(prop.getProperty("endWorkHour"));
            this.startBreakHour=Integer.parseInt(prop.getProperty("startBreakHour"));
            this.endBreakHour=Integer.parseInt(prop.getProperty("endBreakHour"));
            this.minutesAfterDriversResign=Integer.parseInt(prop.getProperty("minutesAfterDriversResign"));
            this.pasteOfTime=Integer.parseInt(prop.getProperty("pasteOfTime"));
            this.joiningQueueMinDelay=Integer.parseInt(prop.getProperty("joiningQueueMinDelay"));
            this.joiningQueueMaxDelay=Integer.parseInt(prop.getProperty("joiningQueueMaxDelay"));
            this.loadingCarMinDelay=Integer.parseInt(prop.getProperty("loadingCarMinDelay"));
            this.loadingCarMaxDelay=Integer.parseInt(prop.getProperty("loadingCarMaxDelay"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
