package hello.advanced.trace;

import java.util.UUID;

//id level을 묶어서 TraceId, id는 796bccd9, 공백을 레벨
public class TraceId {

    //로그 추적기는 트랜잭션ID와 깊이를 표현하는 방법이 필요하다.
    //여기서는 트랜잭션ID와 깊이를 표현하는 level을 묶어서 TraceId 라는 개념을 만들었다.
    //TraceId 는 단순히 id (트랜잭션ID)와 level 정보를 함께 가지고 있다.
    //[796bccd9] OrderController.request() //트랜잭션ID:796bccd9, level:0
    //[796bccd9] |-->OrderService.orderItem() //트랜잭션ID:796bccd9, level:1
    //[796bccd9] | |-->OrderRepository.save()//트랜잭션ID:796bccd9, level:2

    private String id;
    private int level;

    //기본 생성자, 그냥 new로 객체 생성하면 자동으로 아이디 만듬
    public TraceId() {
        this.id = createId();
        this.level = 0;
    }

    //생성자
    private TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    //TraceId 를 처음 생성하면 createId() 를 사용해서 UUID를 만들어낸다
    //여기서는 이렇게 만들어진 값을 트랜잭션ID
    //같은 트랜잭션이면 아이디는 같고 깊이가 깊어지는
    private String createId() {
        //uuid 랜덤값을 id로, 너무 길어서 앞에부터 8번째까지만 씀, 이렇게해도 중복될 일은 거의 없음
        return UUID.randomUUID().toString().substring(0, 8);
    }

    //현재 아이디에 레벨 증가시킨 TraceId객체 반환
    public TraceId createNextId() {
        return new TraceId(id, level + 1);
    }

    //현재 아이디에 레벨 감소
    public TraceId createPreviousId() {
        return new TraceId(id, level - 1);
    }

    //첫번째 level인지
    public boolean isFirstLevel() {
        return level == 0;
    }

    //id lebel 꺼내서 볼 수 있게
    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }
}
