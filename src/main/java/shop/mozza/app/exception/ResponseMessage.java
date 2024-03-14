package shop.mozza.app.exception;

public class ResponseMessage {

    //test
    public static final String TEST_SUCCESS = "테스트 성공";
    public static final String MAKE_MEETING_SUCCESS = "모임 생성이 완료되었습니다.";
    public static final String MAKE_MEETING_FAILED = "모임 생성이 실패하였습니다.";
    public static final String GUEST_LOGIN_SUCCESS =  "비회원 등록이 완료되었습니다.";
    public static final String GUEST_LOGIN_FAILED = "비회원 사용자 등록이 실패했습니다.";
    public static final String SET_NOTIFICATION_ON_SUCCESS = "카카오톡 알림 수신 동의 완료";
    public static final String SET_NOTIFICATION_OFF_SUCCESS = "카카오톡 알림 수신 거부 완료";
    public static final String SET_NOTIFICATION_FAILED = "카카오톡 알림 수신 동의가 정상적으로 처리되지 않았습니다.";
    public static final String GET_MEEITNG_INFO_FAILED = "링크 접속에 실패했습니다.";
    public static final String GET_MEEITNG_FAILED = "모임이 존재하지 않습니다.";
    public static final String GET_MEEITNG_INFO_SUCCESS = "링크 접속 성공했습니다.";
    public static final String SUBMIT_SCHEDULE_SUCCESS = "희망 일정 선택을 완료했습니다.";
    public static final String SUBMIT_SCHEDULE_FAILED = "희망 일정 선택에 실패했습니다.";


    public static final String LOGOUT_SUCCESS = "로그아웃 성공";
    public static final String LOGOUT_FAILED = "로그아웃 실패";


    public static final String GET_ALL_MEETING_SUCCESS = "전체 모임 조회에 성공했습니다.";
    public static final String GET_ALL_MEETING_FAILED = "모임 목록을 불러올 수 없습니다.";
    public static final String NO_MEEITNG_LIST_ERROR = "모임 목록이 없습니다.";
}
