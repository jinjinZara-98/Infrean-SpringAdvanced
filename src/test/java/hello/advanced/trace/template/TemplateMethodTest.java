package hello.advanced.trace.template;

import hello.advanced.trace.template.code.AbstractTemplate;
import hello.advanced.trace.template.code.SubClassLogic1;
import hello.advanced.trace.template.code.SubClassLogic2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TemplateMethodTest {

    @Test
    void templateMethodV0() {
        logic1();
        logic2();
    }

    //두 메서드는 비즈니스 로직 부분 빼고 코드가 같음, 시간을 측정하는 부분
    //중복이 되어있지만 메서드로 추출해서 해결하기 어려움
    //템플릿메서드 패턴을 적용해 변하는 부분: 비즈니스 로직, 변하지 않는 부분(시간 측정) 을 분리

    //핵심과 부가 기능이 섞여있는
    private void logic1() {
        long startTime = System.currentTimeMillis();

        //비즈니스 로직 실행
        log.info("비즈니스 로직1 실행");

        //비즈니스 로직 종료
        long endTime = System.currentTimeMillis();

        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }

    private void logic2() {
        long startTime = System.currentTimeMillis();

        //비즈니스 로직 실행
        log.info("비즈니스 로직2 실행");

        //비즈니스 로직 종료
        long endTime = System.currentTimeMillis();

        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }

    /**
     * 템플릿 메서드 패턴 적용
     * 템플릿 메서드 패턴은 이름 그대로 템플릿을 사용하는 방식이다. 템플릿은 기준이 되는 거대한 틀
     * 수정할일이 있으면 추상 클래스 한 곳에서만 하면 됨
     */
    @Test
    void templateMethodV1() {
        //다형성, 어떤 객체이냐에 따라 같은 메서드라도 행동이 다름
        //execute()는 부모꺼 사용하고 call() 은 자식이 상속해 사용
        AbstractTemplate template1 = new SubClassLogic1();
        template1.execute();

        AbstractTemplate template2 = new SubClassLogic2();
        template2.execute();
    }

    //템플릿 메서드 패턴은 SubClassLogic1 , SubClassLogic2 처럼 클래스를 계속 만들어야 하는 단점
    //익명 내부 클래스를 사용하면 이런 단점을 보완
    //익명 내부 클래스를 사용하면 객체 인스턴스를 생성하면서 동시에 생성할 클래스를 상속 받은 자식 클래스를 정의.
    //이 클래스는 SubClassLogic1 처럼 직접 지정하는 이름이 없고 클래스 내부에 선언되는 클래스여서 익명 내부 클래스
    @Test
    void templateMethodV2() {
        //추상템플릿클래스를 상속받은 클래스를 바로 만들고 로직을 바로 짜는, 클래스파일 만들 필요 없음
//        AbstractTemplate template1 = new AbstractTemplate() {
//            @Override
//            protected void call() {
//                log.info("비즈니스 로직1 실행");
//            }
//        }

        //실행하면 익명 클래스이므로 TemplateMethodTest$1로 현재 테스트 클래스에 $1을 붙여 이름을 만들어줌
        AbstractTemplate template1 = new AbstractTemplate() {
            @Override
            protected void call() {
                log.info("비즈니스 로직1 실행");
            }
        };
        log.info("클래스 이름1={}", template1.getClass());
        template1.execute();

        AbstractTemplate template2 = new AbstractTemplate() {
            @Override
            protected void call() {
                log.info("비즈니스 로직2 실행");
            }
        };
        log.info("클래스 이름2={}", template2.getClass());
        template2.execute();
    }
}

