package com.example.couponcore.service

import com.example.couponcore.TestConfig
import com.example.couponcore.repository.UserCouponPolicyMappingRepository
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@Transactional
class UserCouponPolicyMappingServiceTest(
    private val userCouponPolicyMappingService: UserCouponPolicyMappingService,
    private val userCouponPolicyMappingRepository: UserCouponPolicyMappingRepository
) : TestConfig() {

    @Test
    fun `saveUserCouponPolicyMapping 유저와 발급된 쿠폰을 mapping하여 저장한다`() {
        // given
        val couponTitle = "TIME_SALE"
        val userId = "jerry"
        // when
        val mapping = userCouponPolicyMappingService.saveUserCouponPolicyMapping(userId, couponTitle)
        // then
        val result = userCouponPolicyMappingRepository.findByIdOrNull(mapping.id)!!
        result.couponPolicy.title shouldBe couponTitle
        result.userId shouldBe userId
    }

    @Test
    fun `saveUserCouponPolicyMapping 존재하지 않는 쿠폰은 매핑 저장에 실패한다`() {
        // given
        val couponTitle = "NOT_EXIST_COUPON"
        val userId = "jerry"
        // when && then
        assertThrows<NullPointerException> {
            userCouponPolicyMappingService.saveUserCouponPolicyMapping(
                userId,
                couponTitle
            )
        }
    }
}
