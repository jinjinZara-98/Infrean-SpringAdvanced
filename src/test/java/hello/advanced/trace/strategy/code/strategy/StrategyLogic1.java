package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

//핵심로직 call메서드 구현체, 상속이 아닌 인터페이스에게 의존해
//컨텍스트 코드가 바뀌든 말든 여기엔 영향이 없는
@Slf4j
public class StrategyLogic1 implements Strategy {

    @Override
    public void call() {
        log.info("비즈니스 로직1 실행");
    }
}
