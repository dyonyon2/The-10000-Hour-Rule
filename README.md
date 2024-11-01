[To Do List] // 코드내에 TDL 주석 체크!
- 프론트엔드
    - 로그인 후

- 백엔드
    - 메모 작성 누르면 바로 빈 메모 생성으로 시작하자. -> 그 뒤 모든 작업들이 Memo_idx 가지고 시작!
    - res_data 오류일때 1024자 짜르기
    - 메모 수정
    - 굳이 owner_id를 가지고 있어야하나? 그냥 서비스에 콘텐츠 ID만 있으면 될거 같은데....?? => 그냥 가자. 목록 읽기 같은거는 session 체크만 하면 되지 뭐
    - 메모 읽기
    - 메모 목록 읽기 
    -> 1. 내 메모 / 2.  내 그룹 메모 / 3. 내가 팔로우한 메모 
        => 메모 제목, 메모 타입, 메모 소유자, 
    - 메모 정보 수정 리턴 값 확인! 
    - 메모 복사시 Image는 어떻게 할거? => 동일한 파일 가르키도록 IMG 테이블만 등록
    - 메모 검증 부분 스킵 (그룹, Follow 기능 완료한 뒤에)
    - 먼저 메모 기본 기능들부터 추가
    - 메모 읽을 때 STATUS 확인?


 [정리]
- 팔로우 기능 정리
    - 팔로우는 유저만 요청할 수 있음
    - 유저/그룹 팔로우
        - Access가 1, -1 인 유저/그룹에게 팔로우 신청할 수 있음
        - Access가 -1인 유저/그룹은 비공개 계정임. (정보, contents 공개 X, 팔로워에게만 공개)
    - 콘텐츠 팔로우
        - content의 Access 가 1, -1 인 경우 유저가 content에 대해서 팔로우 신청할 수 있음
        - content의 Access에 상관 없이 공유 키를 가지고 있는 경우 유저가 content에 대해서 팔로우 신청할 수 있음
- 응답 포맷 정하기 => JSON으로 전달하기
    - { "STATUS" : "", "MSG" : "{}" , "RES_CODE" : "",  "RES_DATE":"" }
    - STATUS : 0 처리중, 1 처리완료, -1 처리중 에러
    - MSG : 사용자에게 전달할 데이터
    - RES_CODE : 정상 000000, 에러 DB0001
    - RES_DATA : STATUS가 1일 때에는 특정 객체, 1일때 데이터 없으면 안된것으로 판단하면 됨, -1 에러일때에는 에러 메시지

- 각 Function에서 // 시스템(정상/오류), 로직(성공/실패)를 나누어  FunctionException 나누어 던지기

- 쿼리 스트링 데이터를 @ModelAttribute로 @RequestBody 처럼 객체로 저장 가능
- @RequestParam으로 하나 받아올 수도 있음
-  @GetMapping("/url/{data}") 에서 data를 @PathVariable로 받아올 수 있음