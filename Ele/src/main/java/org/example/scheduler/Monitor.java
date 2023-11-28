package org.example.scheduler;

import org.example.elevator.Elevator;
import org.example.common.ElevatorStatus;
import org.example.person.Person;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Monitor{

	private static final Map<Integer, List< Person >> FLOOR_MAP = new ConcurrentHashMap<>();
	private static final Map<String, Elevator > ELEVATOR_MAP = new ConcurrentHashMap<>();

	private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);

	public Monitor(){


	}

	// 엘레베이터 등록
	public void enroll( Elevator elevator ){
		ELEVATOR_MAP.put( elevator.getName(), elevator );
	}

	// 사람 등록
	public void enroll( Person person ){

		if( FLOOR_MAP.containsKey( person.getCurrentFloor() ) ){

			List< Person > people = FLOOR_MAP.get( person.getCurrentFloor() ).stream().collect( Collectors.toList());
			people.add( person );
			FLOOR_MAP.put( person.getCurrentFloor(), people );

		} else{

			FLOOR_MAP.put( person.getCurrentFloor(), List.of(person));
		}
	}

	/**
	 * 사람을 기준으로 엘레베이터 선택
	 * 호출을 한 사람과 층이 가까운 엘레베이터를 보내줌
	 * 1. 멈춰있는 엘레베이터가 우선적으로 선택
	 * 2. 엘레베이터의 현재 층 수를 비교해서 가장 가까운 엘레베이터 선택
	 * 3. 둘다 움직이는 경우에는 엘레베이터가 멈출때까지 기다렸다가 선택 (나중에 수정)
	 *
	 * @param person
	 * @return
	 */
	private Elevator selectElevator( Person person ){

		if( !FLOOR_MAP.isEmpty() ) {

			Elevator findFirst = ELEVATOR_MAP.values().stream()
					.filter( elevator -> elevator.getCurrentStatus() == ElevatorStatus.STOP )
					.min( ( e1, e2 ) -> {
						return Math.abs( e1.getCurrentFloor() - person.getCurrentFloor() ) - Math.abs( e2.getCurrentFloor() - person.getCurrentFloor() );
					} )
					.orElse( null );


			return findFirst;
		}

		return null;
	}

	/**
	 * 층별 사람을 기준으로 엘레베이터 호출
	 * 1. 각 층마다 첫번째 사람을 기준으로 엘레베이터 호출
	 * 2. 엘레베이터에 호출한 사람의 현재 층 수를 등록
	 *
	 */
	public void callElevator(  ) {

		FLOOR_MAP.keySet().stream().forEach(
				integer -> {
					FLOOR_MAP.get( integer ).stream().limit( 1 )
							.forEach( person -> {
								Elevator elevator = selectElevator( FLOOR_MAP.get( integer ).get( 0 ) );

								if( elevator != null )

									elevator.registerWait( integer );

							} );
				}
		);
	}

	/**
	 * 매초마다 엘레베이터와 사람을 감시해서 로직 실행
	 * 1. 각 엘레베이터의 현재 층을 기준으로 사람이 있는지 확인
	 * 2. 사람이 있으면 엘레베이터 탑승 (무게 초과전까지)
	 * 3. 다른 층에 남아있는 사람들은 엘레베이터를 호출
	 *
	 */
	public void work(){

		executor.scheduleAtFixedRate( ()->{

			try {

				ELEVATOR_MAP.values().stream().forEach(
						elevator -> {

								if ( FLOOR_MAP.get( elevator.getCurrentFloor() ) != null ) {

									System.out.println( "############## wait person : " + FLOOR_MAP.get( elevator.getCurrentFloor() ).toString() );

									List< Person > in = elevator.getIn( FLOOR_MAP.get( elevator.getCurrentFloor() ) );

									if ( in.size() == 0 ) {
										FLOOR_MAP.remove( elevator.getCurrentFloor() );
									} else {
										FLOOR_MAP.put( elevator.getCurrentFloor(), in );
									}
								}

						}
				);

				callElevator();

			} catch ( Exception e ){
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}, 0,1, TimeUnit.SECONDS );
	}


}
