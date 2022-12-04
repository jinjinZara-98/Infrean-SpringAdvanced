package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 필드에 전략을 보관하는 방식
 */
//탬플릿 메서드 패턴은 부모 클래스에 변하지 않는 템플릿을 두고,
//변하는 부분을 자식 클래스에 두어서 상속을 사용해서 문제를 해결했다.
//전략 패턴은 변하지 않는 부분을 Context 라는 곳에 두고,
//변하는 부분을 Strategy 라는 인터페이스를 만들고 해당 인터페이스를 구현하도록 해서 문제를 해결한다.
//상속이 아니라 위임으로 문제를 해결하는 것이다.
//전략 패턴에서 Context는 변하지 않는 템플릿 역할을 하고, Strategy 는 변하는 알고리즘 역할을 한다.
//GOF 디자인 패턴에서 정의한 전략 패턴의 의도는 다음과 같다.
//> 알고리즘 제품군을 정의하고 각각을 캡슐화하여 상호 교환 가능하게 만들자.
//전략을 사용하면 알고리즘을 사용하는 클라이언트와 독립적으로 알고리즘을 변경할 수 있다

//공통로직, 변하지 않는 로직을 가지고 있는 템플릿 역할을 하는 코드
//전략 패턴에서는 이것을 컨텍스트(문맥)이라 한다
//쉽게 이야기해서 컨텍스트(문맥)는 크게 변하지 않지만, 그 문맥 속에서 strategy 를 통해 일부 전략이 변경
@Slf4j
public class ContextV1 {

    /**
     * 이 필드에 변하는 부분인 Strategy의 구현체를 주입, 원하는걸 주입해서 실행
     * Context 는 Strategy 인터페이스에만 의존한다는 점이다.
     * */
    //덕분에 Strategy의 구현체를 변경하거나 새로 만들어도 Context 코드에는 영향을 주지 않는다
    //스프링에서 의존관계 주입에서 사용하는 방식이 바로 전략 패턴
    private Strategy strategy;

    public ContextV1(Strategy strategy) {
        this.strategy = strategy;
    }

    public void execute() {
        long startTime = System.currentTimeMillis();

        //비즈니스 로직 실행
        strategy.call(); //위임
        //비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }
}
