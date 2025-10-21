package com.example.unlox

import android.content.Context

class AppPreferences(private val ctx: Context) {
    private val prefs = ctx.getSharedPreferences("app_blocker_prefs", Context.MODE_PRIVATE)
    private val KEY_BLOCKED = "blocked_packages"

    fun getBlocked(): MutableSet<String> {
        return prefs.getStringSet(KEY_BLOCKED, emptySet())?.toMutableSet() ?: mutableSetOf()
    }

    fun setBlocked(set: Set<String>) {
        prefs.edit().putStringSet(KEY_BLOCKED, set).apply()
    }

    fun addBlocked(pkg: String) {
        val s = getBlocked()
        s.add(pkg)
        setBlocked(s)
    }

    fun removeBlocked(pkg: String) {
        val s = getBlocked()
        s.remove(pkg)
        setBlocked(s)
    }

    fun isBlocked(pkg: String): Boolean {
        return getBlocked().contains(pkg)
    }
}