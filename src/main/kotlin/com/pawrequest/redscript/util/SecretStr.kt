package com.pawrequest.redscript.util

class SecretStr(private val value: String) {
    override fun toString(): String = "***"
    fun get(): String = value
}