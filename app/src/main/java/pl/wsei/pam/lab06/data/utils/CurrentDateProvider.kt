package pl.wsei.pam.lab06.data.utils

import java.time.LocalDate

interface CurrentDateProvider {
    val currentDate: LocalDate
}

class SystemCurrentDateProvider : CurrentDateProvider {
    override val currentDate: LocalDate
        get() = LocalDate.now()
} 