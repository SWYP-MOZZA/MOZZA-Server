package shop.mozza.app.exception;

public class CustomExceptions {

    //테스트
    public static class testException extends RuntimeException{
        public testException(String message){
            super(message);
        }
    }

}
