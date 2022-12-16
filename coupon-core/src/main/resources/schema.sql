CREATE TABLE `coupon`.`coupon_policies`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `title`            varchar(255) NOT NULL comment '쿠폰 타이틀',
    `quantity`         bigint(20) NOT NULL comment '발급 가능한 쿠폰 수',
    `issued_quantity`  bigint(20) NOT NULL comment '발행된 쿠폰 수',
    `date_issue_start` datetime(6) NOT NULL comment '발행 시작 일시',
    `date_issue_end`   datetime(6) NOT NULL comment '발행 종료 일시',
    `date_expire`      datetime(6) NOT NULL comment '만료 일시',
    PRIMARY KEY (`id`),
    KEY                `title` (`title`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT '쿠폰 정책 정보';

CREATE TABLE `coupon`.`user_coupon_policy_mappings`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `coupon_policy_id` bigint(20) NOT NULL comment '쿠폰 정책 ID',
    `user_id`          varchar(255) NOT NULL comment '유저 ID',
    `date_issued`      datetime(6) NOT NULL comment '발행 일시',
    `date_expire`      datetime(6) NOT NULL comment '만료 일시',
    `date_used`        datetime(6) NULL comment '사용 일시',
    PRIMARY KEY (`id`),
    KEY                `user_id_coupon_policy_id` (`user_id`, `coupon_policy_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT '유저 쿠폰 정책 매핑 정보';