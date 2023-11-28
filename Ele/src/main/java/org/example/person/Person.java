package org.example.person;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.example.common.ElevatorStatus;

@Getter
@ToString
@EqualsAndHashCode( onlyExplicitlyIncluded = true )
public abstract class Person {

	@EqualsAndHashCode.Include
	protected final String name;
	@EqualsAndHashCode.Include
	protected final int weight;
	protected final int currentFloor;
	protected final int goFloor;
	private final ElevatorStatus peopleStatus;

	public Person( String name, int weight, int currentFloor, int goFloor ) {
		this.name = name;
		this.weight = weight;
		this.currentFloor = currentFloor;
		this.goFloor = goFloor;
		peopleStatus = this.goFloor > currentFloor ? ElevatorStatus.UP : ElevatorStatus.DOWN;
	}

}
