import app.cash.turbine.test
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.doReturn
import ru.yandex.praktikumchatapp.data.ChatApi
import ru.yandex.praktikumchatapp.data.ChatRepository
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message
import kotlin.collections.toSet

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(isWithReplies = false)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `send message should update state with MyMessage`() = runTest {
        val message = Message.MyMessage("TestMessage")

        // TODO Задание 5: допишите юнит-тест

        viewModel.sendMyMessage(message.text)
        viewModel.chatState.test {
            val state = awaitItem()
            assertEquals(1, state.messages.size)
            assertEquals(message, state.messages.first())
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }

        // TODO Задание 6: допишите юнит-тест
        val chunkSize = 10

        val jobs = messagesToSend.chunked(chunkSize).map { chunk ->
            launch(Dispatchers.Default) {
                chunk.forEach { message ->
                    viewModel.sendMyMessage(message.text)
                }
            }
        }

        jobs.joinAll()

        val receivedMessages = viewModel.chatState.value.messages
        val receivedMyMessages = receivedMessages.filterIsInstance<Message.MyMessage>()

        assertEquals(messagesToSend.size, receivedMessages.size)
        assertEquals(messagesToSend.size, receivedMyMessages.size)
        assertEquals(messagesToSend.toSet(), receivedMessages.toSet())

    }
}