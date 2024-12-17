package com.example.foresti.utils

import com.chibatching.kotpref.KotprefModel


object AppPref : KotprefModel() {
    var passwordCalculatorScreen by stringPref("")
    var phoneNumberService by stringPref("0961735552")
}