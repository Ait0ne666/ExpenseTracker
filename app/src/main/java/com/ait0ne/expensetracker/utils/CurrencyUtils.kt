package com.ait0ne.expensetracker.utils

import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.CurrencyRates

class CurrencyUtils {


    companion object {
        fun convertCurrency(amount: Float, from: Currency, to: Currency, rates: CurrencyRates): Float {

            if (from == to) return amount


            if (from == Currency.RUB) {
                return amount / getRateForCurrency(rates, to)
            } else if (to == Currency.RUB) {


                return amount * getRateForCurrency(rates, from)


            } else {
                return amount * getRateForCurrency(rates, from) / getRateForCurrency(rates, to)
            }


        }



        fun getRateForCurrency(rates: CurrencyRates, currency: Currency): Float {
            return when (currency) {
                Currency.THB -> {
                   rates.tbh
                }

                Currency.USD -> {
                    rates.usd
                }

                Currency.RUB -> {
                    1F
                }

                Currency.EUR -> {
                    rates.eur
                }

            }
        }
    }
}