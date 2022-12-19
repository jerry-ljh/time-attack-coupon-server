# time-attack-coupon-server

### 목표

높은 User Traffic이 발생해도 다운되지 않는 선착순 쿠폰 발급 서버를 구성한다.

### 요구 조건

1. 동일한 유저에게 쿠폰 발급은 1회만 가능해야한다.
2. 유저가 쿠폰 발급을 여러번 요청해도 가장 일찍 요청을 보낸 기준으로 선착순 발급 처리한다.
3. 쿠폰 발급 대기열 순위가 확인 가능해야한다.

### 구현 포인트

1. redis기반으로 유저 트래픽을 대응한다.
2. reids에서 제공하는 데이터 구조로 요구 사항을 만족 시킨다.
3. redis를 사용하는김에 쿠폰 발급 동시성 문제를 distributed lock으로 처리한다.

### API 서버 구조

<img width="797" alt="image" src="https://user-images.githubusercontent.com/87708830/208379585-37874e3a-8723-4710-b03a-968a8eb23303.png">  

* api 서버에서는 쿠폰 발급 api(대기열 등록 api)를 제공한다.
    * 대기열은 ZSET을 사용한다.
        - 중복 제거 & score기반 sort를 제공한다.
    * ZADD 명령어를 사용하여 대기열을 등록한다.
    * ZADD에 NX를 붙이면 새로운 값일 때만 동작한다.
        * 중복 요청에 score가 업데이트되지않는다.
        * 가장 먼저 요청이 발생한 시점을 기준으로 쿠폰을 발급 할 수 있다.

+ api 서버에서는 쿠폰 발급 순위 확인 api(대기열 순위 확인 api)를 제공한다.
    + 대기열 순위 확인은 ZRANK를 사용한다.

### 배치 서버 구조
<img width="861" alt="image" src="https://user-images.githubusercontent.com/87708830/208386832-c584d8ce-4031-4fca-8063-c055ae456cc9.png">

* 쿠폰 발급 요청 트래픽과 쿠폰 발급 로직을 분리시킨다. (api server/ batch server)
* 쿠폰 발급 대상을 ZSET에서 읽어온다.
    * ZPOPMIN 명령어를 사용하여 쿠폰 발급 대상을 선착순으로 가져온다.
* 쿠폰 발급은 async하게 이뤄진다.
    * async하게 이뤄져도 선착순 기준에 맞게 발급되어야한다.
    * chunk단위 처리중 쿠폰 발급 로직을 async하게 처리한다.
    * 남은 쿠폰 발급 수가 chunk size보다 작으면 sync로 발급한다.
* 중복 발급을 방지하기위해 SET을 사용하여 발급 마킹한다.
    * 쿠폰 발급 수량도 SET을 기반으로 확인할 수 있다.
    * SADD명령어로 쿠폰 발급을 마킹한다.
    * 남은 쿠폰 수 확인과 쿠폰 발급 연산은 동시성 문제를 가지고 있다.
        * distribute lock을 걸어 동시성 문제를 방지한다.
