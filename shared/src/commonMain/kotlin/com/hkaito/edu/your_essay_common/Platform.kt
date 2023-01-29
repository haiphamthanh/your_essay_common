package com.hkaito.edu.your_essay_common

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform