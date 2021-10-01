package app;

import gui.SceneController;

public class WorkClock extends Thread {

    static
    int hour;
    int minutes;
    int seconds;
    Config config;
    public boolean breakTime;
    public boolean workTime;
    public boolean on;
    Queue queue;
    SceneController sceneController;


    WorkClock(int startHour, int startMinute, int startSecond, Config config, Queue queue, SceneController sceneController){
        this.hour = startHour;
        this.minutes = startMinute;
        this.seconds = startSecond;
        this.breakTime = false;
        this.workTime = false;
        this.config = config;
        this.queue = queue;
        this.sceneController = sceneController;
        this.on = true;
    }

    public void run (){
        while(on) {
            try {
                Thread.sleep(0,config.pasteOfTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(hour == config.startBreakHour){
                breakTime= true;
            }
            if(hour == config.endBreakHour){
                breakTime=false;
                try {
                    queue.access.lock();
                    queue.breakTime.signal();
                }finally {
                    queue.access.unlock();
                }
            }
            if(hour == config.startWorkHour){

                workTime = true;
                try {
                    queue.access.lock();
                    queue.workTime.signal();
                }finally {
                    queue.access.unlock();
                }
            }
            if(hour == config.endWorkHour){
                workTime = false;
            }
            if(hour == config.hoursLimit){
                hour = 0;

            }
            if(minutes == config.minutesLimit){
                hour++;
                minutes=0;

            }
            if(seconds == config.secondsLimit){
                minutes++;
                sceneController.setTime(hour,minutes);
                seconds = 0;
            }
            seconds ++;
        }
    }
}
