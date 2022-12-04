package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

//핵심로직 call메서드 구현체
@Slf4j
public class StrategyLogic2 implements Strategy {

    @Override
    public void call() {
        log.info("비즈니스 로직2 실행");
    }
}
