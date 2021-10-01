package app;

import gui.SceneController;
import javafx.application.Platform;

import java.util.LinkedList;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Queue{

    String name;
    public LinkedList<Car> cars = new LinkedList<Car>();
    public LinkedList<Car> resignedCars= new LinkedList<Car>();

    //Działanie zamka polega na stworzeniu iluzji monitora -  w monitorze utworzonym na sekcji krytycznej jednocześnie poruszać się może TYLKO jeden wątek
    //poruszając się po monitorze wątek może napotkać warunek - Condition np gdy kolejka jest pusta. Oznacza to że wątek zatrzymuje w miejscu warunku i
    // dopuszcza kolejny wątek do wejścia do monitora. Jeśli kolejny wątek zatrzyma się na tym samym warunku i nastepnie kolejka się zapełni czyli
    //warunek zostanie odblokowany to odblokowywanie wątków następuje w takiej kolejności jakie były zablokowywane.
    //zamek dostępu do monitora
    Lock access;
    //warunek pustej kolejki
    Condition empty;
    //warunek czasu przerwy łądowarki
    Condition breakTime;
    //warunek czasu pracy łądowarki
    Condition workTime;

    //Kontroler interfejsu graficznego
    SceneController sceneController;

    Queue(String name, SceneController sceneController){
        this.name = name;
        this.access = new ReentrantLock();
        this.empty = access.newCondition();
        this.breakTime = access.newCondition();
        this.workTime = access.newCondition();
        this.sceneController = sceneController;
    }

    //wyświetlenie kolejki
    public void displayQueue(){
        for (int i = 0; i < cars.size(); i++) {
            System.out.print(cars.get(i).id + " ");
        }
        System.out.println();
    }

    //Załadunek może być wykonywane na 1 aucie o dużej pojemności lub 2 autach o małej pojemności
    // Jeśli pierwszy w kolejce jest samochód o małej pojemności dobierany jest do niego następny samochód z kolejki o małej pojemności
    public int searchForNextLowCapacityCar(){
        for (int i = 1; i < cars.size(); i++) {
            if(cars.get(i).capacity=="Low"){
                return i;
            }
        }
        return -1;
    }

    //załadowanie auta
    public LinkedList<Car> loadCar(LinkedList<Car> retCars, Car car){
       //dodanie auta do listy retCars - czyli tymczasowego załadunku u danej ładowarki
        retCars.add(car);
        //odblokowanie semafora samochodu z warunkiem loaded = true
        car.loaded = true;
        car.carSem.release();
        //usunięcie samochodu z kolejki oczekujących
        removeCar(car);
        //zwrócenie referencji listy tymczasowego załadunku
        return  retCars;
    }

    //pobranie załadunku przez Ładowarkę
    public LinkedList<Car> getLoad(Loader loader){

        LinkedList<Car> retCars= new LinkedList<Car>();
        try{
            //zamknięcie dostępu do monitora
            access.lock();
            //sprawdzenie czy jest przerwa -> jeśli tak ładowarka czeka na warunku do momentu odblokowania przez zegar
            if(loader.workClock.breakTime==true){
                try {
                    System.out.println("app.Loader : "+loader.id+" - Start of the break time");
                    breakTime.await();
                    System.out.println("app.Loader : "+loader.id+" - End of daily breaK time");

                } catch (Exception e) { System.out.println(e.getMessage());}
            }

            //sprawdzenie czy jest czas pracy -> jeśli nie ładowarka czeka na waruunku do momentu odblokowania przez zegar
            if(loader.workClock.workTime==false){
                try {
                    System.out.println("app.Loader : "+loader.id+"- End of daily work time");
                    workTime.await();
                    System.out.println("app.Loader : "+loader.id+"- Start of daily work time");
                } catch (Exception e) { System.out.println(e.getMessage());}
            }
            System.out.println("app.Loader : "+loader.id+" - I am in the monitor");


            //sprawdzenie czy jest pusta kolejka -> jeśli tak czeka na warunku pustej kolejki do momentu gdy zostanie dodane jakieś auto
            if (cars.size() == 0) {
                try {
                    empty.await();
                    System.out.println("app.Loader : "+loader.id+" - The queue is empty");

                } catch (Exception e) { System.out.println(e.getMessage());}
            }

            //sprawdzenie pojemności pierwszego auata w kolejce
            String capacity = cars.getFirst().capacity;
            //pobranie
            loadCar(retCars,cars.getFirst());

            //jeśli pierwsze auto było małej pojemności dobierane jest kolejne auto z kolejki o małej pojemności jeśli nie -> następuje ząładunek
            if(capacity.equals("Low")) {
                int lcIndex= searchForNextLowCapacityCar();
                if (lcIndex != -1) {
                    loadCar(retCars,cars.get(lcIndex));
                }
            }

            //zmienne wypisujące odpowiednie wartości na konsoli
            System.out.print("app.Loader : "+loader.id+" - I took a load of: ");
            for(int i=0;i<retCars.size();i++){
                System.out.print("       car id: "+retCars.get(i).id+ "        Capacity: "+ retCars.get(i).capacity);
            }
            System.out.println();
        }finally {
            System.out.println("app.Loader : "+loader.id+" - I stopped accessing queue");
            access.unlock();
            return retCars;
        }
    }

    //auto zrezygnowało -> musi uzyskać dostęp do monitora aby usunąc się z kolejki
    public void carResigned(Car car){
        try {
            access.lock();
            cars.remove(car);
            //funkcja ta pozwala na rezygnacji na animacji ( bez platform run later ekran by sie nie odświeżył )
            Platform.runLater(() -> {
                sceneController.carResigning(car);
            });
            //dodanie do listy aut które zrezygnowały
            resignedCars.add(car);
        }finally {
            access.unlock();
        }
    }

    public void addCar(Car car) {
        try {
            access.lock();
            cars.add(car);
            empty.signal();
        }finally {
            access.unlock();
        }
    }

    public void removeCar(Car car) {
        try {
            access.lock();
            cars.remove(car);
            empty.signal();
        }finally {
            access.unlock();
        }
    }
}
