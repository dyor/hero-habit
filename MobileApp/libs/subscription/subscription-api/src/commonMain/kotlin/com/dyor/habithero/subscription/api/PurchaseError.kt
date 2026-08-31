package com.dyor.habithero.subscription.api

data class PurchaseError(
    val message: String,
    val code: String? = null,
)
