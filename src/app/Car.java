package app;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;
import java.util.concurrent.Semaphore;

//Klasa wątkowa samochodu
public class Car extends Thread{

    int id;
    //pojemnośc samochodu
    public String capacity;
    //ustawienia domyslne
    Config config;
    //zmienna informująca o tym czy samochód został załadowany
    volatile boolean loaded;
    //Stopper to klasa wątku odmierzająca samochodowi czas od dołączenia do kolejki, w celu możliwości rezygnacji po x czasie.
    Stopper stopper;
    //kolor samochodu
    public Color color;
    //kordy samochodu potrzbne do animacji odjeżdżania w wyniku rezygnacji
    public int x = 0;
    public int y = 0;

    //zmienne potrzbne do animacji odjeżdżania
    public Rectangle rectangle;
    public TranslateTransition transition;

    //Semafor carSem służy do dostępu do kolejki, auto rezygnując z załadunku musi zostać usunięte z kolejki, jeżeli jednocześnie podczas pobierania
    //auta do załadunku przez ładowarkę rezygnuje one i zostanie usunięte wystąpi błąd zatem usuwanie musi miec oddzielny dostęp
    Semaphore carSem;

    //Referencja kolejki aut oczekujących na załadunek
    Queue queue;
    Random rand = new Random();


    Car(int id ,String capacity, Config config, Queue queue){
        this.id = id;
        this.capacity = capacity;
        this.config = config;
        this.queue = queue;
        this.transition =new TranslateTransition();

        //losowanie kolorku auta
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        this.color =Color.rgb(r,g,b);

        this.loaded = false;
        this.carSem = new Semaphore(0);
    }

    //uśpienie wątku na okreslony czas - następuje weryfikacja czy przedziały podane z ustawien domyślnych lub użytkownika sa poprawne
    private void delay(int delayMin, int delayMax){
        if(delayMin<=delayMax && delayMin>0 ) {
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

    //dołączanie auta do kolejki
    public void joinQueue(){
        //uśpienie wątku na losową wartość czasu mieszczącą się w przedziale w ustawien
        delay(config.joiningQueueMinDelay,config.joiningQueueMaxDelay);
        //dodanie auta do kolejki nie wymaga synchronizacji, gdyż pobieranie jest uzależnione od rozmiaru kolejeki
        queue.addCar(this);
        //inicjalizacja pracy stoppera odmierzającego czas rezygnacji
        stopper = new Stopper(config.minutesAfterDriversResign, carSem, config);
        stopper.start();

        //zmienne weryfikacji na konsoli
        queue.displayQueue();
        System.out.println("I am a car " + id + " I joined the queue");
    }

    //uruchomienie pracy wątku
    public void run(){

        //dołączenie do kolejki
        joinQueue();
        //cała akcja pobrania i załadunku odbywa się za pomocą obiektów Queue i Loader zatem auto czeka na semaforze,
        //jeśli semafor odblokuje ładowarka - zmieni zmienną loaded na true i samochód zakończy praćę jak załadowany
        //jeśli semafor odblokuje stopper - zmieni zmienną resigned na true  -  samochód zakończy pracę rezygnacją
        try {
            carSem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(stopper.resigned==true){
            System.out.println("I am a car " + id + " I resigned");
            queue.carResigned(this);
        }
        else if(loaded){
           System.out.println("I am a car " + id + " I've been removed from queue");
        }
        try {
            stopper.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
