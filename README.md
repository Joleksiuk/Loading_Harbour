# Loading_Harbour
Simple loading harbour simulation. It is multithreded application synchronizing access to shared memory. The threads are representing loaders, cars or timers. 

It should be assumed that N excavators and K light duty cars and L heavy duty cars exist. One excavator supports two small vehicles or one large one at the same time. In addition, it should be assumed that excavator operators have a lunch break between 1300 and 1400, and impatient drivers resign from loading if they do not arrive at the position within the assumed maximum waiting time, e.g. 0.5 hours.

2. Synthetic description of the problem - assumptions made

To compile the code fully, you must have the Javafx library configured in the InteliJ environment.

Class Name Representation of the assumption
Application Creates car and charger threads, creates all objects, and connects them to a graphical interface
Car The car class is a weft class that simulates the operation of a car. It joins the queue at random intervals and waits for loading. It exits after loading or after the quit time has elapsed.
Config The class that stores all the default settings taken from the file at the beginning. Then it overwrites them by the user entered.
Loader The loader class is a thread class that simulates the operation of a loader by loading cars onto a ship. The work of the thread strongly depends on the timer. The charger starts and stops working via the clock signal, and it also leaves and returns from a break. Loading takes place at random intervals.
Queue The class that controls the queue of cars. He presents it by means of a list.
Stopper A class that acts as a timer. Each car starts a timer when it joins the queue. After the cancellation time has elapsed, the Stopper notifies the car. It ends when the car resigns from loading or is loaded
WorkClock The thread class that simulates a clock. It measures the day in an accelerated manner. Controls the work of chargers.
Main The class that starts the javaFX GUI thread
SceneController Class that controls the graphical interface and invokes the application thread.

The simulation works as follows:

  - The user enters the following:
      -  Number of low and high volume cars.
      - Time range for the possible loading time
      - Time frame for possible vehicle connection
      -Time of beginning and end of work
      -Time of the beginning and end of the break
      - Minutes after which the car is abandoned.
  - Cars are joining in the maximum time and waiting for loading.
  - At the same time, one charger can charge 2 small-volume cars or one large-volume car. The loading order is done using the priority in the queue. Assumptions of problematic situations:
      - When a small-volume car is first in the queue, however, there are no small-volume cars in the queue. The car is loaded individually.
      - When a low volume car is first in line, but a high volume car is second in line. The queue is searched for a small car and the next small car is selected for loading.
  - The charger starts and stops clockwise. The charger does not work during the break.
  - The loading time is random and is within the range entered by the user.
  - After loading, the trucks go to the loading list that corresponds to the ship.
  - When the thread of the car joins the queue, a stopwatch is started that measures how much time the car has spent in the queue. If the stopper counts down the resignation time, the car is removed from the queue and added to the list of resigned cars, and then the car and stopper thread ends.

3. List of shared resources

Shared Resource Description
Queue Cars join the queue and are removed from it at various times. Chargers take cars of various capacities from the queue.

4. List of highlighted synchronization points

- Taking a car from the queue
- Removal of the car from the queue

5. List of synchronization objects

- Lock access
- Condition empty
- Condition breakTime
- Condition workTime

6. List of sequential processes

- Car
- Stopper (Timer)
- Loader
- WorkClock

Process data synchronization is based on the use of a monitor and locks mechanism.
The Queue process allows only one sequential process to manipulate the queue.

When the queue is empty and the Loader wants to download a resource from the queue, it enters the monitor and stops on the Queue Empty condition. When a new car is added to the queue, the charger will be unlocked.

When the break time occurs and the charger wants to download another car, it stops at the Break Time condition. It will be unlocked when the clock has finished running.

When the working time is over and the charger wants the car from the queue, it stops on the WorkingTime condition. It will be unlocked when the clock has finished running.

When the car resigns and wants to be removed from the queue, it must be granted access to the monitor.


