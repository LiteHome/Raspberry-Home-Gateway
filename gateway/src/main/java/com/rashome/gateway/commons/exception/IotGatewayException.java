package com.rashome.gateway.commons.exception;

public class IotGatewayException extends Exception {
    
    public IotGatewayException(){
        super();
    }

    public IotGatewayException(String message){
        super(message);
    }

    public IotGatewayException(Throwable e){
        super(e);
    }

    public IotGatewayException(String message, Throwable e){
        super(message, e);
    }

    public static IotGatewayException parametersInMessage(String message, Object... objects){
        return new IotGatewayException(String.format(message, objects));
    }

    public static IotGatewayException deviceIsNotRegist() {
        return new IotGatewayException("设备未注册");
    }


    public static IotGatewayException nullParameters(String objectName){
        return new IotGatewayException(String.format("%s 是 null", objectName));
    }

    public static IotGatewayException nullParameters(String... objectNames){

        StringBuilder stringBuilder = new StringBuilder();

        for (String objectName : objectNames) {
            stringBuilder.append(objectName + ", ");
        }

        stringBuilder.deleteCharAt(stringBuilder.length()-2);

        stringBuilder.append("其中一个是 null, 或者全部是 null");

        return new IotGatewayException(stringBuilder.toString());
    }
}
