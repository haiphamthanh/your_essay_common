package com.hkaito.edu.kmm_base

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform