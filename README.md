# 프로젝트명 - 비품인
[![](https://user-images.githubusercontent.com/122663756/233110315-2fe1d54e-974e-482b-b43d-fc7e7bd0aa0c.png)](https://user-images.githubusercontent.com/122663756/233110315-2fe1d54e-974e-482b-b43d-fc7e7bd0aa0c.png)

## 목차

- [1. 팀원 소개](#팀원-소개)
- [2. 비품인 배포 사이트](#비품인-배포-사이트)
- [3. 비품인 시연 영상 사이트](#비품인-시연-영상-사이트)
- [4. 깃허브 레포](#깃허브-레포)
- [5. 프로젝트 기능](#프로젝트-기능)
- [6. BE 기술 스택](#be-기술-스택)
- [7. BE 기술적 의사결정](#be-기술적-의사결정)
- [8. BE 트러블 슈팅](#be-트러블-슈팅)
- [9. 서비스 아키텍쳐](#서비스-아키텍쳐)
- [10. ERD](#erd)
- [11. API 명세](#api-명세)

## 팀원 소개

| 스택  | 이름   | 깃허브 주소                 |
|-----| ------ | --------------------------- |
| BE  | 신경연 | https://github.com/bestfarmer94   |
| BE  | 나도관 | https://github.com/DOGWANNA   |
| BE  | 김유영 | https://github.com/yykim1010   |


## [](https://www.bipumin.shop/)비품인 배포 사이트

[비품인](https://www.bipumin.shop/)

## [](https://www.youtube.com/watch?v=_1JAHVzat2Q)비품인 시연 영상 사이트

[비품인.youtube](https://www.youtube.com/watch?v=_1JAHVzat2Q)

## 깃허브 레포

> FE : [https://github.com/Bipum-In/Bipum-In-FE](https://github.com/Bipum-In/Bipum-In-FE)

> BE : [https://github.com/Bipum-In/Bipum-In-BE](https://github.com/Bipum-In/Bipum-In-BE)


## 프로젝트 기능
비품 관리 협업툴인 비품인 서비스는 마스터, 관리자, 사원으로 나뉘어 서비스를 사용할 수 있습니다.

## 🌟 1. 회원가입 및 로그인

-  회원가입 시 구글 계정을 통해 로그인을 할 수 있습니다.
-  로그인에 성공하여 토큰을 발급 받아야만 모든 페이지를 정상적으로 이용할 수 있습니다.
  <details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233054214-85e022c0-ae57-4e19-932d-509c284dfc99.png)](https://user-images.githubusercontent.com/122663756/233054214-85e022c0-ae57-4e19-932d-509c284dfc99.png)
[![](https://user-images.githubusercontent.com/122663756/233054435-a17b0146-1410-4c1d-a4c6-24570d6c193c.png)](https://user-images.githubusercontent.com/122663756/233054435-a17b0146-1410-4c1d-a4c6-24570d6c193c.png)
</details>

#

## 🗝️ 2. 마스터 계정 로그인

-  최초 마스터 계정으로 로그인 시 회사의 초기 부서 설정을 할 수 있습니다.
-  비품 총괄 관리자를 선임/해임 할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233054950-1a685026-8930-40a7-8c54-9e2dd579160f.png)](https://user-images.githubusercontent.com/122663756/233054950-1a685026-8930-40a7-8c54-9e2dd579160f.png)
[![](https://user-images.githubusercontent.com/122663756/233086608-82087ed2-d39f-40f8-b8ca-9d083864ae77.gif)](https://user-images.githubusercontent.com/122663756/233086608-82087ed2-d39f-40f8-b8ca-9d083864ae77.gif)
</details>

#

## 👨‍💼 3. 관리자 모드

### 3-1. 대시보드

- 대시보드에서는 비품 종류를 카테고리별로 분류하여 수량과 상태를 한 눈에 확인할 수 있습니다
- 알림 박스에서 최신순으로 유저에 대한 알림을 실시간으로 받을 수 있습니다.
- 또한 상단 검색기능을 통해 비품과 유저에 대한 요청을 확인 할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233061803-5d593256-65ae-4370-95a3-42cc5b5a8db1.gif)](https://user-images.githubusercontent.com/122663756/233061803-5d593256-65ae-4370-95a3-42cc5b5a8db1.gif)
</details>

- 실시간으로 받은 알림을 클릭 해 모달을 띄우고 대시보드 안에서도 쉽게 비품을 부여하거나 상세정보 등을 확인할 수 있습니다.
- 헤더에 있는 알림창 에서도 같은 기능을 사용할 수 있습니다.
<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233085503-6b128617-05de-4a05-94e9-7e3e5b27dc0b.gif)](https://user-images.githubusercontent.com/122663756/233085503-6b128617-05de-4a05-94e9-7e3e5b27dc0b.gif)

</details>

#

### 3-2.요청현황
- 요청 별로 '처리 전', '처리 중', '처리 완료' 상태를 쉽게 구분하여 확인할 수 있습니다
- 상단에 각각의 요청 탭을 통해 확인도 가능합니다.
<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233063805-c8461c1f-7d87-400b-becb-03422421cc5d.gif)](https://user-images.githubusercontent.com/122663756/233063805-c8461c1f-7d87-400b-becb-03422421cc5d.gif)
</details>

#

### 3-3.  비품관리

- 상태별로 비품에 대한 조회가 가능합니다.
- 모달을 배치하여 상태를 파악 후 손쉽게 비품을 등록할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233064686-8a81991f-9d2f-44be-9395-442eefa66fd9.gif)](https://user-images.githubusercontent.com/122663756/233064686-8a81991f-9d2f-44be-9395-442eefa66fd9.gif)
</details>

- 해당 비품에 대한 상세정보를 알고싶을 때는 원하는 리스트를 클릭하여
  비품에 대한 히스토리와 상태를 확인할 수 있습니다.
- 협력업체 수정, 사용자 수정이 가능하고, 이미지를 클릭 시 확대된 이미지를 확인할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233064808-511fe8e9-8a4f-4aa9-88b1-243b1b138ff1.gif)](https://user-images.githubusercontent.com/122663756/233064808-511fe8e9-8a4f-4aa9-88b1-243b1b138ff1.gif)
</details>

- 키워드를 검색하여 원하는 비품에 대한 정보를 손쉽게 확인 할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233065650-8f8a74cd-d602-47c3-808e-cbcf99f69d9d.gif)](https://user-images.githubusercontent.com/122663756/233065650-8f8a74cd-d602-47c3-808e-cbcf99f69d9d.gif)
</details>


#

### 3-4. 비품등록

- 비품 단일 등록 시에는 관리자가 원하는 이미지와 함께 비품을 등록할 수 있습니다.
- 네이버 쇼핑 API를 통해 자동으로 이미지를 크롤링하여 비품을 손쉽게 등록할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233155225-9d490161-4bdd-4b36-b9ee-55c283dc4830.gif)](https://user-images.githubusercontent.com/122663756/233155225-9d490161-4bdd-4b36-b9ee-55c283dc4830.gif)
</details>

- 비품에 맞는 소분류가 존재하지 않을 시 직접 입력하여 추가할 수도 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233155886-97123b28-fb84-47c7-a52d-011a68221efc.gif)](https://user-images.githubusercontent.com/122663756/233155886-97123b28-fb84-47c7-a52d-011a68221efc.gif)
</details>


- 복수 등록 시에는 엑셀을 통해 더욱 편리하게 여러가지 비품을 등록할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233071936-eb8c90d5-ded7-4af3-be7b-22cea5bd2a61.gif)](https://user-images.githubusercontent.com/122663756/233071936-eb8c90d5-ded7-4af3-be7b-22cea5bd2a61.gif)
</details>

#

### 3-5. 관리자 설정

- 카테고리 관리, 부서 및 권한 관리, 협력 업체 관리를 할 수 있습니다.
- 카테고리 관리에서는 추가, 수정, 삭제 가 가능합니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233073074-7ad8b708-a8e1-4048-be27-3eec1c26520b.gif)](https://user-images.githubusercontent.com/122663756/233073074-7ad8b708-a8e1-4048-be27-3eec1c26520b.gif)
</details>

- 부서 및 권한 관리에서는 공용 비품 책임자를 선임/해임이 가능합니다.
- 부서 내 사원 또한 삭제할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233073550-dede87ca-2313-4608-8b0b-cc0658c01c3f.gif)](https://user-images.githubusercontent.com/122663756/233073550-dede87ca-2313-4608-8b0b-cc0658c01c3f.gif)
</details>

- 협력 업체 관리에서는 회사에 비품을 제공하는 협력사를 관리할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233074137-b248e697-a933-4d9f-9853-addb88591e47.gif)](https://user-images.githubusercontent.com/122663756/233074137-b248e697-a933-4d9f-9853-addb88591e47.gif)
</details>

#

### 3-6.모드 전환
- 관리자 권한을 갖은 계정은 유저 모드 전환 기능이 있기 때문에
  유저 계정을 만들 필요 없이 유저 기능을 이용할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233075365-cd374466-296e-4910-afc8-0a041a295a10.gif)](https://user-images.githubusercontent.com/122663756/233075365-cd374466-296e-4910-afc8-0a041a295a10.gif)
</details>

#

## 👥 4. 유저 모드
### 4-1. 대시보드

- 유저 대시보드에서는 유저가 사용 중인 비품 목록을 확인할 수 있습니다.
- 개인에게 부여된 비품 목록과 공용 비품 목록을 확인할 수 있습니다.
<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233077825-2937f44b-06c4-4fab-aa5b-b7cd9b18076a.gif)](https://user-images.githubusercontent.com/122663756/233077825-2937f44b-06c4-4fab-aa5b-b7cd9b18076a.gif)
</details>

#

### 4-2. 요청하기

- 비품에 대한 비품 요청, 반납 요청, 수리 요청 등을 할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233079157-5c597406-4729-4f5c-b78a-b8a1d15ec05b.gif)](https://user-images.githubusercontent.com/122663756/233079157-5c597406-4729-4f5c-b78a-b8a1d15ec05b.gif)
</details>

#

### 4-3. 요청 내역

- 유저가 요청했던 히스토리를 확인할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233079562-144d43cb-892d-41e9-b257-9addafdfdfac.gif)](https://user-images.githubusercontent.com/122663756/233079562-144d43cb-892d-41e9-b257-9addafdfdfac.gif)
</details>

#


### 4-4. 재고 보기

- 재고보기 기능을 통해, 요청하고 싶은 비품의 배고를 파악 할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233080062-ee13cec8-5160-46d5-807e-ac03898d1882.gif)](https://user-images.githubusercontent.com/122663756/233080062-ee13cec8-5160-46d5-807e-ac03898d1882.gif)
</details>

#

## 5. ✏️ 내 정보 수정 기능
- 내 정보에서 회원가입 시 입력했던 비밀번호를 입력하여 수정 할 수 있습니다.
<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233081376-13d464dc-0c57-4e38-8dd1-11feab10213c.gif)](https://user-images.githubusercontent.com/122663756/233081376-13d464dc-0c57-4e38-8dd1-11feab10213c.gif)
</details>

- 만약 비밀번호를 분실했을 경우 비밀번호 찾기를 통해 임시 비밀번호를 발급받아 로그인할 수 있습니다.

<details>
  <summary>이미지 더보기</summary>

[![](https://user-images.githubusercontent.com/122663756/233082374-5d7a7f84-c75f-4a17-81db-81ab53bf1d0f.gif)](https://user-images.githubusercontent.com/122663756/233082374-5d7a7f84-c75f-4a17-81db-81ab53bf1d0f.gif)

</details>

#

## BE 기술 스택

<div> 
  <img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/IntelliJIDEA-000000?style=for-the-badge&logo=IntelliJIDEA&logoColor=white">
  <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white">
  <img src="https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=Ubuntu&logoColor=white">
  <img src="https://img.shields.io/badge/jsonwebtokens-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white">
  <img src="https://img.shields.io/badge/SSE-000000?style=for-the-badge&logo=&logoColor=white"/> <br>
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
  <img src="https://img.shields.io/badge/AmazonEC2-FF9900?style=for-the-badge&logo=AmazonEC2&logoColor=white"/> 
  <img src="https://img.shields.io/badge/AmazonRDS-527FFF?style=for-the-badge&logo=AmazonRDS&logoColor=white"/> <br>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/CODEDEPLOY-181717?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=nginx&logoColor=white"/>
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"/>
  <img src="https://img.shields.io/badge/GithubActions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"/>
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/>

</div>

## BE 기술적 의사결정


| 기술                   | 설명                                                |
|----------------------|---------------------------------------------------|
| NGINX, Let’s Encrypt | 클라이언트와 서버의 통신 보안성 향상을 위해서 https를 적용하고자 함          
| Redis                | 접속 제어를 위해 REFRESH TOKEN이 필요                       |
| CI/CD                | 프로젝트 개발에 따른 코드 통합, 배포 과정에서 비효율적인 공수 발생            |
| SSE                  | 실시간 알림처리를 위해 클라이언트와 서버 간 연결을 유지할 수 있는 기술이 요구됨     |
| SMS                  | 일반 유저의 경우 저희 서비스는 가끔씩만 사용하게 될 서비스이기 때문에 알림 기능이 필요 |
| Image Crawling       | 이미지는 매번 업로드 하는 것은 매우 힘들지만, 시각적 효과는 매우 뛰어남                |

## BE 트러블 슈팅

### ☑ SSE (1)

- 문제상황

> 프론트에서 구독요청을 보내면 정상적으로 연결이 된 것이 확인되나, 1분 후에 연결이 끊기고 재요청을 보내는 현상이 발생

- 원인

> 리버스 프록시로 사용하는 NGINX 설정 중 연결 후 메시지 송수신이 없을 시 1분 후 연결이 종료되도록 하는 기본 설정이 존재

- 해결

> Nginx 서버 설정 파일에 proxy_read_timeout 3600000 , proxy_send_timeout 3600000 항목 추가하여 연결 유효시간을 1시간으로 설정

### ☑ SSE (2)

- 문제상황

> 프론트에서 SSE 구독 요청을 하여도 SSEEmitter가 반환되지 않고 pending 상태가 유지
>
- 원인

> NGINX 서버 설정이 HTTP 1.0으로 설정

- 해결

> Nginx 서버 설정 파일에 proxy_set_header Connection ''; , proxy_http_version 1.1; 항목 추가하여 HTTP 버전을 1.1로 변경

### ☑ Entity 설계

- 문제상황

> 처음에는 요청 Entity가 분리 되어 있었는데, ADMIN 모드에서는 현황을 한눈에 보기 위해 Sorting이 필수였는데 이 기능이 매번 Union과 함께 복잡한 쿼리 필요

- 원인

> 요청의 종류마다 필요한 정보가 다르기 때문에 Entity를 분리 하였는데, 사용자의 측면에서 모든 것을 커스텀 하게 맞춘 것이 원인

- 해결

> Requests Entity 들을 하나로 통합하고 최대한 융통적으로 Validation 및 필드 설정을 통해 사용에 불편함이 없도록 설계 시도 및 예외처리도 집중적으로 관리 시도

### ☑ 비품 History

- 문제상황

> 유저와의 요청/승인 Process 가 아닌 관리자의 비품 강제 수정에서의 history 관리

- 원인

> 요청 이력을 통해 history 집계를 진행하는데, 요청 이력이 없음

- 해결

> 유저의 강제 주입이나 변경시에 자동 입력으로 요청을 만들게끔 구현하여 관리
## 서비스 아키텍쳐

[![](https://user-images.githubusercontent.com/122663756/233143022-363f0b4f-aa39-4254-a817-dfa6b9626dba.png)](https://user-images.githubusercontent.com/122663756/233143022-363f0b4f-aa39-4254-a817-dfa6b9626dba.png)

#

## ERD

[![](https://user-images.githubusercontent.com/122663756/233172411-2721db4b-0bda-432d-bbde-047631de2fb4.png)](https://user-images.githubusercontent.com/122663756/233172411-2721db4b-0bda-432d-bbde-047631de2fb4.png)

## API 명세
[비품인 API 명세 스웨거](https://bipum-in.shop/swagger-ui/index.html)