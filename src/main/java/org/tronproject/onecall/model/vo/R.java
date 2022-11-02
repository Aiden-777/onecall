package org.tronproject.onecall.model.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-09-11 11:01
 */
@Getter
@Setter
@Builder
public class R {
    private Boolean status;

    private Object data;

    private String message;

    private static final String OK_MESSAGE = "success";

    private static final String ERROR_MESSAGE = "failure";

    private R() {
    }

    public R(Boolean status, Object data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static R ok() {
        return new R(true, null, OK_MESSAGE);
    }

    public static R ok(Object data) {
        return new R(true, data, OK_MESSAGE);
    }

    public static R error(String message) {
        return new R(false, null, message);
    }

}
