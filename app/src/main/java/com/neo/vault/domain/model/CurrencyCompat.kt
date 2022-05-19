package com.neo.vault.domain.model

import com.neo.vault.application
import com.neo.vault.util.extension.currency
import com.neo.vault.util.extension.locale
import java.util.*

enum class CurrencyCompat {
    BRL,
    USD,
    EUR;

    val currency: Currency get() = Currency.getInstance(name)

    companion object {
        fun default(): CurrencyCompat {
            return runCatching {
                valueOf(application.locale.currency.currencyCode)
            }.getOrElse {
                BRL
            }
        }
    }
}