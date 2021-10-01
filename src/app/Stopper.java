package app;

import java.util.concurrent.Semaphore;

//Klasa stopper odmierza czas rezygnacji samochodu
public class Stopper extends Thread{

    int minutes;
    int minutesToWait;
    int seconds;

    boolean alarm;
    Config config;

    Semaphore carSem;
    boolean resigned;

    Stopper(int minutesToWait, Semaphore carSem, Config config){
        this.minutes = 0;
        this.config = config;
        this.alarm = false;
        this.minutesToWait = minutesToWait;
        this.carSem = carSem;
        this.resigned = false;
    }

    //działanie wątku polega na usypianie wątku na 0,1 milisekundy i inkrementowanie zmiennej sekundy do momentu upłynięcia n minut
    //wtedy odpala się alarm i wątek konczy prace
    public void run(){
        while(!alarm) {
            try {
                Thread.sleep(0,config.pasteOfTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(minutes == minutesToWait){
                alarm=true;
                this.resigned = true;
                carSem.release();
            }
            if(seconds == config.secondsLimit){
                minutes++;
                seconds = 0;
            }
            seconds ++;
        }
    }
}
