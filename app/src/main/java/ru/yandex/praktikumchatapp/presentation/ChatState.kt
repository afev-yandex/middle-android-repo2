package ru.yandex.praktikumchatapp.presentation

data class ChatState(val messages: List<Message>, val shouldShowKeyboard: Boolean)