# Sample solution for Elevators Coding Challenge

## Task

Write an elevator simulator system that implements the following interfaces (they are also located in the src directory of this repo along with Javadoc):

    public interface ElevatorControler {
        Elevator callElevator(int fromFloor, int direction);
    }
    
    public interface Elevator {
        void moveElevator(int toFloor);
        void requestFloor(int floor);
        boolean isBusy();
        int currentFloor();
    }

Create a test where you run this simulator for a building with 100 floors and 6 elevators. Come up with some creative ways of testing your system (i.e. create more requests than elevators, etc).

## Solution 

See comments.md file
