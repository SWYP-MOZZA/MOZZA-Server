package shop.mozza.app.exception;

public class CustomExceptions {

    //테스트
    public static class Exception extends RuntimeException {
        public Exception(String message) { super(message);}
    }
    public static class MeetingNotFoundException extends RuntimeException{
        public MeetingNotFoundException(String message){super(message);}
    }

}
