package ru.yandex.praktikumchatapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository

class ChatViewModel(
    val isWithReplies: Boolean = true
) : ViewModel() {

    private val repository = ChatRepository()

    // TODO Задание 3: добавьте состояние shouldShowKeyboard

    // TODO Задание 4: замените messages и shouldShowKeyboard на state
    private val _chatState = MutableStateFlow(ChatState(emptyList(), false)) // TODO Задание 1: замените на Flow
    val chatState: StateFlow<ChatState> = _chatState

    init {
        viewModelScope.launch {
            while (isWithReplies) {
                repository.getReplyMessage().collect { response ->
                    _chatState.update { state ->
                        state.copy(messages = state.messages + Message.OtherMessage(response), shouldShowKeyboard = true)
                    }
                }
            }
        }
    }

    fun sendMyMessage(messageText: String) {
        _chatState.update { state ->
            state.copy(messages = state.messages + Message.MyMessage(messageText))
        }
    }
}