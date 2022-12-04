package hello.advanced.trace.threadlocal;

import hello.advanced.trace.threadlocal.code.FieldService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class FieldServiceTest {

    private FieldService fieldService = new FieldService();

    @Test
    void field() {
        log.info("main start");

        //스레드 2개 만듬
        Runnable userA = () -> {
            fieldService.logic("userA");
        };

        Runnable userB = () -> {
            fieldService.logic("userB");
        };

        //각 스레드는 파라미터로 들어온 로직을 실행하게 됨
        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");

        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");

        //첫번째 스레드 시작하고 2초 시고 그 다음 스레드
        threadA.start();
        //동시성 문제 발생X
        //문제, 의됴한대로 결과가 안나옴, 로그가 하나밖에 안나옴, 메인스레드가 시작해놓고 스레드b는 도는데 메인스레드가 돌다가 테스트 끝내버림
        //참고로 FieldService.logic() 메서드는 내부에 sleep(1000) 으로 1초의 지연이 있다.
        //따라서 1초 이후에 호출하면 순서대로 실행할 수 있다. 여기서는 넉넉하게 2초 (2000ms)를 설정
        //동싱성 문제 발생 할 수 없음
//        sleep(2000);

        /**동시성 문제 발생, 두번쨰가 첫번째 안끝나는데 들어간거**/
        //a가 저장하고 조회혀려고 하기 전에 b가 저장해버려서 a가 아닌 b가 조회됨
        //결과적으로 Thread-A 입장에서는 저장한 데이터와 조회한 데이터가 다른 문제가 발생
        //이처럼 여러 쓰레드가 동시에 같은 인스턴스의 필드 값을 변경하면서 발생하는 문제를 동시성 문제
        //이런 동시성 문제는 여러 쓰레드가 같은 인스턴스의 필드에 접근해야 하기 때문에
        //트래픽이 적은 상황에서는 확률상 잘 나타나지 않고, 트래픽이 점점 많아질 수 록 자주 발생한다.
        //특히 스프링 빈 처럼 싱글톤 객체의 필드를 변경하며 사용할 때 이러한 동시성 문제를 조심

        //이런 동시성 문제는 지역 변수에서는 발생하지 않는다. 지역 변수는 쓰레드마다 각각 다른 메모리 영역이 할당된다.
        /**동시성 문제가 발생하는 곳은 같은 인스턴스의 필드(주로 싱글톤에서 자주 발생)
           또는 static 같은 공용 필드에 접근할 때 발생한다
           동시성 문제는 값을 읽기만 하면 발생하지 않는다. 어디선가 값을 변경하기 때문에 발생.**/

        /**그렇다면 지금처럼 싱글톤 객체의 필드를 사용하면서 동시성 문제를 해결하려면 어떻게 해야할까?
        //이럴 때 사용하는 것이 바로 쓰레드 로컬이다.**/
        //원래 2000이엿음, 이렇게하면 문제 안생김
        //너무 빠르게 B를 시작해 A가 조회할 때
        //원래 저장했던 A가 삭제되고 B가 저장되며
        //조회되는게 A가 아닌 B
        sleep(100);
        threadB.start();

        //메인 쓰레드 종료 대기, 메인스레드도 3초정도 기다리게
        sleep(3000);
        log.info("main exit");
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }
}
