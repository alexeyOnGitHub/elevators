# My comments

## pom.xml file.
  * should replace log4j dependency with slf4j, which is more flexible and more common for libraries nowadays
  * about easymock - I generally avoid using mocks for tests. you can often rewrite code in a way that allows testing without them.
  * version for maven-compiler-plugin is not specified. this can lead to unpredictable build failures in the future. 
    should always set dependency version. I added this:
    `<version>3.2</version>`
  * Use FEST-style asserts library: org.easytesting:fest-assert:1.4.   Here is what you can do with my ElevatorAssert class:
```  
    Elevator elevator = ...
    assertThat(elevator).hasCurrentFloor(3);
    assertThat(elevator).hasName("Elevator 1");
    assertThat(elevator).onlyVisited(2, 1, 2, 3);
```

## Code
  * sample classes are missing "com.locusenergy.homework" package declaration. 
  * Not clear who and why would use moveElevator() function on Elevator. It is public so I assume anybody can call it directly without using our controller. 
    does not seem like a good idea. is it supposed to force-move the elevator to the new floor without planned stops? 
    should it just update the current floor variable without bothering about consequences? I have not implemented that function because
    * it is not clear what it should do
    * it seems dangerous anyway
       
  * InvalidRequest should be renamed to InvalidRequestException
  * ElevatorController should not use int value for specifying direction, even if it is assumed that constants will be used.
you would need to check "what if illegal value is provided". why bother? use an enum with values UP, DOWN, UNKNOWN instead. 
UP and DOWN will be used by client code while UNKNOWN will be used by Elevator implementation class to show that 
it does not have a specific direction it needs to move towards at that moment. 
```
    Elevator callElevator(int fromFloor, DIRECTION direction) throws InvalidRequestException;
```    
then this requirement is automatically simplified:
```
    @param direction If > 0, going up; if <= 0 going down.
```
also, how will this work when user presses both "up" and "down" button on the floor to call an elevator?..	
	 
  * This javadoc copied from Elevator class is so long that I suggest renaming the function to make it easier to understand:
>	 /**
>     * Requests the Elevator to move to a certain floor. This method immitates press of a button inside the
>     * elevator. Therefore, it should not move the Elevator immidiately but just register the request.
>     * 
>     * Bonus: this method should throw InvalidStateException if the Elevator is NOT busy.
>     */
>    void requestFloor(int floor);

   This function can be renamed to addFloorToRequestedFloors or maybe something more elegant for clarity.  	 

  * Elevator interface does not describe if floors start with 0 or 1. In my solution they start with 1. I documented this in the API.
  * I added some TODO items that would be nice to address some time later.
