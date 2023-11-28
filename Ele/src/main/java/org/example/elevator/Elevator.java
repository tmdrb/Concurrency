package org.example.elevator;

import lombok.Getter;
import org.example.common.ElevatorStatus;
import org.example.person.Person;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class Elevator {

	protected final int MAX_WEIGH;
	protected int currentFloor;
	protected final String name;
	protected int currentWeight;
	protected final Map< Integer, List< Person > > destinationMap = new ConcurrentHashMap<>();

	protected ElevatorStatus currentStatus;

	public Elevator( int max_weigh, String name ) {

		MAX_WEIGH = max_weigh;
		this.name = name;
		currentFloor = 1;
		currentWeight = 0;
		currentStatus = ElevatorStatus.STOP;
	}

	public abstract List<Person> getIn( List<Person> person );
	public abstract void work();
	public abstract void registerWait( int personCall );

}
