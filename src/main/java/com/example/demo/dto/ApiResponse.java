package com.example.demo.dto;

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

//    Success factory Method
    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(true,"OK",data);
    }
//    Success With Custom Message
    public static <T> ApiResponse <T> success(String message,T data)
    {
        return new ApiResponse<>(true,message,data);
    }
//    Error factory Method
    public static <T> ApiResponse <T> error(String message){
        return new ApiResponse<>(false,message,null);
    }
    public boolean isSuccess(){
        return success;
    }
    public String getMessage(){
        return message;
    }
    public T getData(){
        return data;
    }
}
