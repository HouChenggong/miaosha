package cn.monitor4all.miaoshadao.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author xiyou
 * @version V1.0
 * @ClassName: ServerResponse
 * @Description: TODO
 * @Date: 2019/12/30 14:11
 */

@Setter
@Getter
@NoArgsConstructor
public class ServerResponse<T> implements Serializable {
    private int status;
    private String msg;
    private T data;

    //TODO-xiyou 直接判断接口是否成功
    private Boolean areSuccess;

    private ServerResponse(int status, Boolean areSuccess) {
        this.status = status;
        this.areSuccess = areSuccess;
    }

    private ServerResponse(int status, String msg, Boolean areSuccess) {
        this.status = status;
        this.msg = msg;
        this.areSuccess = areSuccess;
    }

    private ServerResponse(int status, String msg, T data, Boolean areSuccess) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.areSuccess = areSuccess;
    }

    private ServerResponse(int status, T data, Boolean areSuccess) {
        this.status = status;
        this.data = data;
        this.areSuccess = areSuccess;
    }


    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    //Success
    public static <T> ServerResponse<T> createBySuccess() {

        return new ServerResponse<T>(ResponseCodeEnum.SUCCESS.getCode(), true);
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
        return new ServerResponse<T>(ResponseCodeEnum.SUCCESS.getCode(), msg, true);
    }

    public static <T> ServerResponse<T> createBySuccess(T data) {
        return new ServerResponse<T>(ResponseCodeEnum.SUCCESS.getCode(), data, true);
    }


    public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
        return new ServerResponse<T>(ResponseCodeEnum.SUCCESS.getCode(), msg, data, true);
    }

    //Error
    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse<T>(ResponseCodeEnum.ERROR.getCode(), ResponseCodeEnum.ERROR.getDesc(), false);
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage) {
        return new ServerResponse<T>(ResponseCodeEnum.ERROR.getCode(), errorMessage, false);
    }


    public static <T> ServerResponse<T> createByErrorMessageAndData(String errorMessage, T data) {
        return new ServerResponse<T>(ResponseCodeEnum.ERROR.getCode(), errorMessage, data, false);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMessage) {
        return new ServerResponse<T>(errorCode, errorMessage, false);
    }


}
