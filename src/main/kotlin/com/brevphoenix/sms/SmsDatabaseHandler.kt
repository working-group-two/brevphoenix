package com.brevphoenix.sms

import com.brevphoenix.PhoneNumber
import com.brevphoenix.config
import org.jdbi.v3.core.Jdbi
import org.slf4j.LoggerFactory

object SmsDatabaseHandler {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val jdbi = Jdbi.create(config.databaseApp)

    init {
        init()
    }

    fun init() {
        logger.info("Creating sms table if not exists")
        jdbi.open().use { handle ->
            handle.execute(
                """
CREATE TABLE IF NOT EXISTS sms (
    id SERIAL PRIMARY KEY,
    eventId TEXT NOT NULL,
    sub TEXT NOT NULL,
    from_number TEXT NOT NULL,
    from_number_type TEXT NOT NULL,
    to_number TEXT NOT NULL,
    to_number_type TEXT NOT NULL,
    direction_from_subscriber boolean NOT NULL,
    text TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL
);
""".trimIndent()
            )
        }
    }

    fun insertSms(
        sms: Sms,
    ): Long = jdbi.withHandle<Long, Exception> {
        it.createUpdate(
            """
INSERT INTO sms (eventId, sub, from_number, from_number_type, to_number, to_number_type, direction_from_subscriber, text, timestamp)
VALUES (:eventId, :sub, :from_number, :from_number_type, :to_number, :to_number_type, :direction_from_subscriber, :text, :timestamp)
RETURNING id;
""".trimIndent()
        )
            .bind("eventId", sms.eventId)
            .bind("sub", sms.sub)
            .bind("from_number", when (sms.from) {
                is Address.InternationalNumber -> sms.from.phoneNumber.e164
                is Address.TextAddress -> sms.from.text
            })
            .bind("from_number_type", when (sms.from) {
                is Address.InternationalNumber -> "e164"
                is Address.TextAddress -> "text"
            })
            .bind("to_number", when (sms.to) {
                is Address.InternationalNumber -> sms.to.phoneNumber.e164
                is Address.TextAddress -> sms.to.text
            })
            .bind("to_number_type", when (sms.to) {
                is Address.InternationalNumber -> "e164"
                is Address.TextAddress -> "text"
            })
            .bind("direction_from_subscriber", sms.direction == Sms.Direction.FROM_SUBSCRIBER)
            .bind("text", sms.content)
            .bind("timestamp", sms.timestamp)
            .executeAndReturnGeneratedKeys()
            .mapTo(Long::class.java)
            .one()
    }

    fun getSmsForUser(e164: String): List<Sms> = jdbi.withHandle<List<Sms>, Exception> {
        it.createQuery("SELECT * FROM sms WHERE from_number = :e164 OR to_number = :e164")
            .bind("e164", e164)
            .map { rs, _ ->
                Sms(
                    id = rs.getLong("id"),
                    eventId = rs.getString("eventId"),
                    sub = rs.getString("sub"),
                    direction = if (rs.getBoolean("direction_from_subscriber")) Sms.Direction.FROM_SUBSCRIBER else Sms.Direction.TO_SUBSCRIBER,
                    from = rs.getString("from_number_type").let { type ->
                        when (type) {
                            "e164" -> Address.InternationalNumber(PhoneNumber.parse(rs.getString("from_number")))
                            "text" -> Address.TextAddress(rs.getString("from_number"))
                            else -> throw IllegalArgumentException("Invalid from number type: $type")
                        }
                    },
                    to = rs.getString("to_number_type").let { type ->
                        when (type) {
                            "e164" -> Address.InternationalNumber(PhoneNumber.parse(rs.getString("to_number")))
                            "text" -> Address.TextAddress(rs.getString("to_number"))
                            else -> throw IllegalArgumentException("Invalid to number type: $type")
                        }
                    },
                    content = rs.getString("text"),
                    timestamp = rs.getTimestamp("timestamp").toInstant(),
                )
            }
            .list()
    }
}
