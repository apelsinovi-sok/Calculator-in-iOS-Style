package com.example.mycalculator

fun calculateTextSize(text: String): Int {
    return when {
        text.length <= 8 -> 80
        text.length <= 12 -> 50
        text.length <= 14 -> 35
        else -> 35
    }
}