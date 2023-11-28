package org.example;

import org.example.elevator.Elevator;
import org.example.elevator.HyundaiE;
import org.example.person.Person;
import org.example.person.SeoulPerson;
import org.example.scheduler.Monitor;

public class Main {


	public static void main( String[] args ) throws InterruptedException {

		Monitor monitor = new Monitor();

		Person p1 = new SeoulPerson( "s1",78,1,4 );
		Person p7 = new SeoulPerson( "s7",108,1,5 );

		Person p8 = new SeoulPerson( "s8",22,8,4 );
		Person p9 = new SeoulPerson( "s9",130,1,3 );

		Person p2 = new SeoulPerson( "s2",45,2,10 );
		Person p3 = new SeoulPerson( "s3",51,7,2 );
		Person p4 = new SeoulPerson( "s4",89,10,1 );
		Person p5 = new SeoulPerson( "s5",60,5,3 );
		Person p6 = new SeoulPerson( "s6",55,1,6 );

		Elevator elevator = new HyundaiE( "h1", 300 );
		Elevator elevator1 = new HyundaiE( "h2", 200 );

		monitor.enroll( elevator );
		monitor.enroll( elevator1 );

		monitor.enroll( p1 );
		monitor.enroll( p2 );
		monitor.enroll( p3 );
		monitor.enroll( p4 );
		monitor.enroll( p5 );
		monitor.enroll( p6 );
		monitor.enroll( p7 );
		monitor.enroll( p8 );
		monitor.enroll( p9 );

		monitor.work();

		elevator.work();
		elevator1.work();

	}

}