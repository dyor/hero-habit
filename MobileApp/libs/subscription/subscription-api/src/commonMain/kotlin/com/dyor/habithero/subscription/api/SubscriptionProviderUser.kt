package com.dyor.habithero.subscription.api

class SubscriptionProviderUser(
    val grantedAccesses: Map<String, GrantedAccess>,
    val activeSubscriptionIds: Set<String>,
)
