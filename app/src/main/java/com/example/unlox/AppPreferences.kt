package com.example.unlox

import android.content.Context

class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("app_blocker_prefs", Context.MODE_PRIVATE)
    private val KEY_ALLOWED = "allowed_packages"

    fun getAllowed(): MutableSet<String> {
        return prefs.getStringSet(KEY_ALLOWED, emptySet())?.toMutableSet() ?: mutableSetOf()
    }

    fun setAllowed(set: Set<String>) {
        prefs.edit().putStringSet(KEY_ALLOWED, set).apply()
    }

    fun addAllowed(pkg: String) {
        val s = getAllowed()
        s.add(pkg)
        setAllowed(s)
    }

    fun removeAllowed(pkg: String) {
        val s = getAllowed()
        s.remove(pkg)
        setAllowed(s)
    }

    fun isAllowed(pkg: String): Boolean {
        return getAllowed().contains(pkg)
    }
}