package com.example.couponcore.repository

import com.example.couponcore.domain.CouponPolicy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class CouponPolicyRepository(
    private val couponPolicyJpaRepository: CouponPolicyJpaRepository
) : QuerydslRepositorySupport(CouponPolicy::class.java), CouponPolicyJpaRepository by couponPolicyJpaRepository {

    @PersistenceContext
    override fun setEntityManager(entityManager: EntityManager) {
        super.setEntityManager(entityManager)
    }


}

interface CouponPolicyJpaRepository : JpaRepository<CouponPolicy, Long>