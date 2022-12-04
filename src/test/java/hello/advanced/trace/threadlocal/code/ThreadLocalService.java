package hello.advanced.trace.threadlocal.code;

import lombok.extern.slf4j.Slf4j;
//FieldService의 동시성 문제를 해결한

//쓰레드 로컬은 해당 쓰레드만 접근할 수 있는 특별한 저장소를 말한다. 쉽게 이야기해서 물건 보관 창구를 떠올리면 된다.
//여러 사람이 같은 물건 보관 창구를 사용하더라도 창구 직원은 사용자를 인식해서 사용자별로 확실하게 물건을 구분해준다.
//사용자A, 사용자B 모두 창구 직원을 통해서 물건을 보관하고, 꺼내지만 창구 지원이 사용자에 따라 보관한 물건을 구분해주는

//해당 쓰레드가 쓰레드 로컬을 모두 사용하고 나면 ThreadLocal.remove() 를 호출해서 쓰레드 로컬에 저장된 값을 제거해주어야
//쉽게 이야기해서 다음의 마지막 로그를 출력하고 나면 값을 다쓴 쓰레드 로컬의 값을 제거
@Slf4j
public class ThreadLocalService {

    /**쓰레드 로컬을 사용하면 각 쓰레드마다 별도의 내부 저장소를 제공한다.**/
    //따라서 같은 인스턴스의 쓰레드 로컬 필드에 접근해도 문제 없다.
    //쓰레드 로컬을 통해서 데이터를 조회할 때도 thread-A 가 조회하면 쓰레드 로컬은 thread-A 전용 보관소에서 userA 데이터를 반환해준다.
    //물론 thread-B 가 조회하면 thread-B 전용 보관소에서 userB 데이터를 반환

    //ThreadLocal 사용법
    //값 저장: ThreadLocal.set(xxx)
    //값 조회: ThreadLocal.get()
    //값 제거: ThreadLocal.remove(
    private ThreadLocal<String> nameStore = new ThreadLocal<>();

    public String logic(String name) {
        //nameStore.get() 저장하기 전에 값을 뺸
        log.info("저장 name = {} -> nameStore = {}", name, nameStore.get());

        //스레드로컬 값 저장
        nameStore.set(name);
        sleep(1000);

        //값 빼옴
        log.info("조회 nameStore = {}", nameStore.get());

        return nameStore.get();
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
