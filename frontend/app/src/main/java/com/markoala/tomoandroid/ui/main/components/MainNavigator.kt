package com.markoala.tomoandroid.ui.main

import androidx.compose.runtime.mutableStateListOf

class MainNavigator {

    private val _stack = mutableStateListOf<MainStackEntry>(MainStackEntry.Tab(BottomTab.Home))
    val stack: List<MainStackEntry> get() = _stack

    val currentEntry: MainStackEntry
        get() = _stack.last()

    val currentTab: BottomTab?
        get() = (_stack.lastOrNull { it is MainStackEntry.Tab } as? MainStackEntry.Tab)?.tab



    fun push(entry: MainStackEntry) {
        if (_stack.lastOrNull() == entry && entry !is MainStackEntry.Tab) return
        _stack.add(entry)
    }

    fun pop() {
        if (_stack.size > 1) _stack.removeAt(_stack.lastIndex)
    }

    fun openTab(tab: BottomTab) {
        val tabEntry = MainStackEntry.Tab(tab)
        if (_stack.lastOrNull() == tabEntry) return
        _stack.add(tabEntry)
    }
}
