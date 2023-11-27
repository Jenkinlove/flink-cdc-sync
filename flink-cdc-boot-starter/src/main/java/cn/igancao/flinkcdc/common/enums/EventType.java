package cn.igancao.flinkcdc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventType {

    READ("READ", 0),
    CREATE("CREATE", 1),
    UPDATE("UPDATE", 2),
    DELETE("DELETE", 3),
    ;

    private final String name;

    private final Integer value;

}
