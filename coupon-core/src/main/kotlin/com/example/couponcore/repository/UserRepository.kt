package com.example.couponcore.repository

import com.example.couponcore.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class UserRepository(
    private val userJpaRepository: UserJpaRepository,
    private val jdbcTemplate: JdbcTemplate
) : UserJpaRepository by userJpaRepository {

    fun batchInsert(users: Collection<User>) {
        users.chunked(1000).forEach { chunkedUserList ->
            jdbcTemplate.batchUpdate("insert into users(user_id) values(?)",
                object : BatchPreparedStatementSetter {
                    override fun setValues(ps: PreparedStatement, i: Int) {
                        ps.setString(1, chunkedUserList[i].userId)
                    }

                    override fun getBatchSize() = chunkedUserList.size
                }
            )
            Thread.sleep(1000)
        }
    }

}

interface UserJpaRepository : JpaRepository<User, Long>