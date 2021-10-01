package app;
import gui.SceneController;
import java.util.LinkedList;

//Klasa Application to klasa bazową symulacji - tworzy ona wszystkie obiekty wątków oraz łączy je z interfejsem graficznym
public class Application extends Thread {

    //Obiekt który kieruje cały interfejsem graficznym
    SceneController sceneController;
    //Obiekt zawierający wszystkie ustawienia symulacji
    public Config config;
    //Obiekt zawierający listę samochodów
    public Queue queue;
    //Obiekt zawierający listę załadowanych samochodów
    public LinkedList<Car> loadedCars;
    //Obiekt wątku zegara odmierzający czas pracy ładowarek
    public WorkClock workClock;

    //tablice, w których będą znajdować się obiekty wątków
    Car[] lowCapacityCars;
    Car[] highCapacityCars;
    public Loader[] loaders;

    //konstruktory pozwalające zainicjalizować/ utworzyć obiekty aplikacji
    public Application(SceneController sceneController, Config config){

        this.sceneController =sceneController;
        this.config = config;
        this.queue = new Queue("Queue",sceneController);
        this.loadedCars = new LinkedList<Car>();
        this.workClock = new WorkClock(9, 0, 0, config, queue,sceneController);
        this.lowCapacityCars = new Car[config.lowCapacityCars];
        this.highCapacityCars= new Car[config.highCapacityCars];
        this.loaders = new Loader[config.numberOfLoaders];

    }

    //uruchomienie wątku aplikacji
    public void run() {

        //Wystartowanie wątku zegara pracy ładowarek
        workClock.start();

        //utworzenie obiektów wątków samochodów o małej pojemności
        for (int i = 0; i < config.lowCapacityCars; i++) {
            lowCapacityCars[i] = new Car(i, "Low", config, queue);
        }
        //utworzenie obiektów wątków samochodów o dużej pojemności
        for (int i = 0; i < config.highCapacityCars; i++) {
            highCapacityCars[i] = new Car(i, "High", config, queue);
        }
        //utworzenie obiektów wątków ładowarek
        for (int i = 0; i < config.numberOfLoaders; i++) {
            loaders[i] = new Loader(i, queue, workClock, config, loadedCars);
        }

        //wystartowanie wątków samochodów o dużej pojemności
        for (int i = 0; i < config.highCapacityCars; i++) {
            highCapacityCars[i].start();
        }
        //wystartowanie wątków samochodów o małej pojemności
        for (int i = 0; i < config.lowCapacityCars; i++) {
            lowCapacityCars[i].start();
        }
        //wystartowanie wątków ładowarek
        for (int i = 0; i < loaders.length; i++) {
            loaders[i].start();
        }
        //funkcja join sprawa że wątek aplikacji NIE MOŻE zakończyć się przed zakończeniem wątku samochodu o małej pojemności
        for (int i = 0; i < config.lowCapacityCars; i++) {
            try {
                lowCapacityCars[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //funkcja join sprawa że wątek aplikacji NIE MOŻE zakończyć się przed zakończeniem wątku samochodu o dużej pojemności
        for (int i = 0; i < config.highCapacityCars; i++) {
            try {
                highCapacityCars[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //funkcja join sprawa że wątek aplikacji NIE MOŻE zakończyć się przed zakończeniem wątku ładowarek
        for (int i = 0; i < loaders.length; i++) {
            try {
                loaders[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //funkcja join sprawa że wątek aplikacji NIE MOŻE zakończyć się przed zakończeniem wątku zegara
        try {
            workClock.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
