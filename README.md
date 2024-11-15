2024.11.12
    [Front]
        - MemoGridBoard 완성
            - 메모 item UI 및 크기 조절 완료
            - Paging 기능도 데이터에 직접 연결 완료
    [Back]
        - Memo response Body 사이즈가 커서 컬럼 사이즈 오류 발생중 -> MaxLength를 1000에서 900으로 줄임

2024.11.13
    [Front]
        - MemoListBoard 완성

2024.11.15
    [Front]
        - MemoList에서(Grid,List) 즐겨찾기 변경 기능 추가 (API Call)
            - MemoBoardItem에서 API Call한 뒤에 화면에 반영하려고 MemoBoardItem에서 useState 사용하니 꼬인다...!!
                -> memoBoard에서 함수 만들어줘서 memoBoardItem으로 던져줘서 해결!
    [Back]
        - 메모 update시 조건 추가하여 변경할 값만 넣도록 query 변경
        - /memo/setting patch에 있던 즐겨찾기, 카테고리 변경을 /memo patch 로 변경



[To Do List] // 코드내에 TDL 주석 체크!
- 프론트엔드
    - Memo 구현
        - MemoDetailInfo Page 구현
        - Memo 작성 Page 구현
    - Calendar Page 구현
    - Routine Page 구현
    - Main Page 구현


- 백엔드
    - 메모 리스트 조회시, favorite, update로 order by!
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