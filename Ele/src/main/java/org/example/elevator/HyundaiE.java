package org.example.elevator;

import lombok.Getter;
import org.example.common.ElevatorStatus;
import org.example.person.Person;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
public class HyundaiE extends Elevator {

	private int firstIn = 0;
	private final Set<Integer> callerSet = new HashSet<>();
	private final ScheduledExecutorService service = Executors.newScheduledThreadPool( 4 );

	public HyundaiE( String name, int max_weigh ) {
		super( max_weigh, name );
	}

	/**
	 * 사람이 엘레베이터에 탑승 로직
	 * 1. 처음 탑승객을 기준으로 방향 설정
	 * 2. 방향이 같은 탑승객만 탑승
	 * 3. 무게 확인
	 * 4. 탈 사람 리스트에서 탄 사람들을 뺀 나머지를 리턴
	 *
	 *
	 * @param people
	 * @return
	 */
	@Override
	public List< Person > getIn( List<Person> people ) {

		if( people != null ) {
			List< Person > copyP = people.stream().collect( Collectors.toList() );

			List< Person > recordWhoGetIn = new ArrayList<>();

			if ( this.currentStatus == ElevatorStatus.STOP )
				this.currentStatus = people.get( 0 ).getPeopleStatus();

			people.stream().takeWhile( person -> this.getCurrentWeight() + person.getWeight() < this.getMAX_WEIGH() )
					.filter( person -> person.getPeopleStatus() == this.currentStatus )
					.forEach( person -> {

						recordWhoGetIn.add( person );

						this.currentWeight += person.getWeight();

						if ( destinationMap.containsKey( person.getGoFloor() ) ) {

							destinationMap.get( person.getGoFloor() ).add( person );
						} else {

							destinationMap.put( person.getGoFloor(), List.of( person ) );
						}
					} );

			recordWhoGetIn.stream().forEach(
					person -> copyP.remove( person )
			);

			return copyP;
		}
		return Collections.emptyList();
	}

	/**
	 * 매초마다 동작
	 * 1. 현재 층에 내릴사람 체크
	 * 2. 목적지 map, 호출한 사람의 set 이 없을때까지 동작
	 *
	 *
	 */
	@Override
	public void work() {

		service.scheduleAtFixedRate( () -> {

			try {

				synchronized ( System.out ) {
					System.out.println( " destination : " +destinationMap.toString());
					System.out.println( " caller : " +callerSet.toString());
					System.out.println( "********************************" );
					System.out.println( "*   이름 : " + this.name + "                   " );
					System.out.println( "*   무게 : " + this.currentWeight + "                    " );
					System.out.println( "*   현재층 : " + this.currentFloor + "                   " );
					System.out.println( "*   눌린 층 : " + this.destinationMap.keySet() + "                 " );
					System.out.println( "*   부른 사람 층 " + this.callerSet.toString()     );
					System.out.println( "*                              " );
					System.out.println( "********************************" );
				}

				if ( destinationMap.containsKey( this.currentFloor ) ){
					getOut();
				}

				if( callerSet.contains( this.currentFloor ) ){
					callerSet.remove( this.currentFloor );
				}

				if ( destinationMap.isEmpty() && callerSet.isEmpty() ) {

					this.currentWeight = 0;
					this.firstIn = 0;
					this.currentStatus = ElevatorStatus.STOP;
					System.out.println( "############## [ " + this.name + " 완전 멈춤 " + "현재 층 : " + this.currentFloor + " ] ##################" );

				}

				if ( this.currentStatus == ElevatorStatus.DOWN ) {

					this.currentFloor -= 1;
				} else if ( this.currentStatus == ElevatorStatus.UP ) {

					this.currentFloor += 1;
				}

			} catch ( Exception e ){

				e.printStackTrace();
			}
		},2,1, TimeUnit.SECONDS );

	}

	/**
	 * 호출한 사람을 향해서 이동
	 *
	 * @param personCall
	 */
	@Override
	public void registerWait( int personCall ) {

		if ( personCall < this.currentFloor )
			this.currentStatus = ElevatorStatus.DOWN;
		else if ( personCall > this.currentFloor )
			this.currentStatus = ElevatorStatus.UP;

		callerSet.add( personCall );
	}

	/**
	 * 탑승객이 현재층에서 내림
	 *
	 */
	private void getOut() {

		if ( destinationMap.containsKey( this.currentFloor ) ) {

			destinationMap.get( this.currentFloor ).stream().forEach(
					person -> this.currentWeight -= person.getWeight()
			);

			destinationMap.remove( this.currentFloor );

		}
	}
}
