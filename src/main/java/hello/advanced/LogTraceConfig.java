package hello.advanced;

import hello.advanced.trace.hellotrace.logtrace.LogTrace;
import hello.advanced.trace.hellotrace.logtrace.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//수동으로 스프링 빈으로 등록하자. 수동으로 등록하면 향후 구현체를 편리하게 변경할 수 있다는 장점
@Configuration
public class LogTraceConfig {

    @Bean
    public LogTrace logTrace() {
        //이전에는 이거 씀
//        return new FieldLogTrace()

        //동시성 문제 해결한 스레드 로컬 동기화 적용
        return new ThreadLocalLogTrace();
    }
}
