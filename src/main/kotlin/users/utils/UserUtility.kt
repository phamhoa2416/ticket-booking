package users.utils

import kotlinx.datetime.*
import java.time.LocalDate as JavaLocalDate
import java.time.LocalDateTime as JavaLocalDateTime

fun JavaLocalDateTime.toKotlinxDateTime(): LocalDateTime {
    return LocalDateTime(
        year = this.year,
        monthNumber = this.monthValue,
        dayOfMonth = this.dayOfMonth,
        hour = this.hour,
        minute = this.minute,
        second = this.second,
        nanosecond = this.nano
    )
}

fun JavaLocalDate.toKotlinxLocalDate(): LocalDate {
    return LocalDate(
        year = this.year,
        monthNumber = this.monthValue,
        dayOfMonth = this.dayOfMonth
    )
}

fun LocalDateTime.toJavaLocalDateTime(): JavaLocalDateTime {
    return JavaLocalDateTime.of(
        this.year,
        this.monthNumber,
        this.dayOfMonth,
        this.hour,
        this.minute,
        this.second,
        this.nanosecond
    )
}

fun LocalDate.toJavaLocalDate(): JavaLocalDate {
    return JavaLocalDate.of(
        this.year,
        this.monthNumber,
        this.dayOfMonth
    )
}