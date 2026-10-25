package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    companion object {
        private const val INITIAL_DELAY_MS = 1000L
        private const val MAX_RETRIES = 5
    }
    fun getReplyMessage(): Flow<String> {
        return api.getReply()  // TODO Задание 2: добавьте обработку ошибок
            .retryWhen { cause, attempt ->
                if (attempt < MAX_RETRIES) {
                    // повторять запрос несколько раз с экспоненциальным увеличением тайм-аута
                    delay(INITIAL_DELAY_MS.milliseconds * 2.0.pow(attempt.toDouble()))
                    return@retryWhen true
                }
                return@retryWhen false
            }.catch { cause ->
                // чтобы приложение не крэшилось
                emit("Не удалось получить ответ: " + cause.message)
            }
    }
}