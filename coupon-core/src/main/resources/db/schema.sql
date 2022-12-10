CREATE TABLE `coupon`.`users`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` varchar(255) NOT NULL comment '유저 ID',
    PRIMARY KEY (`id`),
    KEY       `user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT '유저 정보';

