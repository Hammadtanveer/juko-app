package com.example.juko

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform