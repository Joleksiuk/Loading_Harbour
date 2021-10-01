package app;

import java.util.LinkedList;
import java.util.Random;

//Klasa wątkowa Ładowarki -  załadowuje auta na statek
public class Loader extends Thread{

    public int id;
    //Ustawienia domyślne symulacji
    Config config;
    //Kolejka samochodów do załadunku
    Queue queue;
    //Zegar odmierzający czas pracy ładowarki lub czas przerw
    WorkClock workClock;
    Random rand = new Random();

    //retCars to lista samochodów, które w danej chwili są załadowywane przez TĄ KONKRETNĄ ładowarkę
    public LinkedList<Car> retCars;
    //loadedCars to referencja kolejki WSZYSTKICH załadowanych aut przez WSZYSTKIE ładowarki
    public LinkedList<Car> loadedCars;

    Loader(int id, Queue queue, WorkClock workClock, Config config, LinkedList<Car> loadedCars ){
        this.config = config;
        this.queue = queue;
        this.workClock = workClock;
        this.id = id;
        this.retCars= new LinkedList<Car>();
        this.loadedCars = loadedCars;
    }

    //uśpienie wątku na okreslony czas - funkcja delay pilnuje aby wprowadzone wartosci opoźnień w ustawieniach były prawidłowe np w przypadku gdy min> max
    private void delay(int delayMin, int delayMax){
        if(delayMin<=delayMax && delayMin>0 && delayMax >0 ) {
            try {
                Thread.sleep(rand.nextInt(delayMax - delayMin) + delayMin);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                Thread.sleep(rand.nextInt(8000) + 2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //załadunek aut, jako argument ładowarka dostaje liste aut do załadunku a następnie nastąpi uśpienie wątku
    public void loading(LinkedList<Car> retCars){
        System.out.println("app.Loader : "+id+" - I am loading...");
        delay(config.loadingCarMinDelay,config.loadingCarMaxDelay);
        System.out.println("app.Loader : "+id+" - I finished loading ");

        //dodanie załadowanych aut do listy załadowanych aut
        for(int i=0; i<retCars.size();i++){
            loadedCars.add(retCars.get(i));
        }
        // po wybudzeniu ze sleepa opróźniana jest lista retCars
        retCars.clear();


    }

    //po wywołaniu metody wątek.start wywołuje sie funkcja run() - wątek zaczyna pracę
    public void run(){
        working();
    }

    public void working(){
        //wątek ładowarki działa gdy działa zegar - do kliknięcia przycisku stop
        while (workClock.on) {
            //System.out.println("app.Loader : "+id+" - I am free");
            // ładowarka wysyła zapytanie do kolejki o możliwośc pobrania aut do załadunku
            retCars =  queue.getLoad(this);
            //jeśli nastąpi pobranie - wykonuje załadunek
            loading(retCars);
        }
    }
}
