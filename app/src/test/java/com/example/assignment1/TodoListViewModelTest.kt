package com.example.assignment1
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
@ExtendWith(CoroutinesTestExtension::class, InstantExecutorExtension::class)
class TodoListViewModelTest{

    @Test
    fun testCreateTodoWithCorrectCredentials() = runTest {


        val todo = mockk<Todo>()
        every { todo.completed} returns false
        every { todo.description} returns "test case"
        every { todo.id} returns "0"



        val api = mockk<InterfaceApi>()
        coEvery { api.createTodos(any(), any(),any(),any()) } returns todo



        val viewModel = TodoListViewModel(null,api)


        val viewStates = mutableListOf<TodoListViewModel.ViewState>()
        viewModel.viewState.observeForever {
            viewStates.add(it)
        }

        viewModel.createTodo(Todo("test case",false,"0"),"mock")



        assertEquals(false, viewModel.userdata.value?.completed)
        assertEquals("0", viewModel.userdata.value?.id)
        assertEquals("test case", viewModel.userdata.value?.description)



        assertEquals(
            listOf(TodoListViewModel.ViewState.Loading, TodoListViewModel.ViewState.Success),
            viewStates
        )


        coVerify { api.createTodos("0", "Bearer mock" ,"48fcacf7-46e1-4285-9d47-76472c1673d1",Todo("test case",false,"0")) }
    }
    @Test
    fun `test clearError sets errorLiveData to null`() {
        val api = mockk<InterfaceApi>(relaxed = true)
        val viewModel = TodoListViewModel(null,api)
        viewModel.clearError()
        assert(viewModel.errorLiveData.value == null)
    }
    @Test
    fun `test clearError fails to set errorLiveData to null`() {
        val api = mockk<InterfaceApi>(relaxed = true)
        val viewModel = TodoListViewModel(null,api)
        viewModel.clearError()
        viewModel.errorLiveData.value = "error"
        assert(viewModel.errorLiveData.value != null)
    }
    @Test
    fun testCreateTodoWithIncorrectCredentials() = runTest {
        val api = mockk<InterfaceApi>()
        coEvery { api.createTodos("0", "Bearer unauthorized" ,"48fcacf7-46e1-4285-9d47-76472c1673d1",Todo("failed case",false,"0")) } throws RuntimeException("")

        val viewModel = TodoListViewModel(null,api)

        val viewStates = mutableListOf<TodoListViewModel.ViewState>()
        viewModel.viewState.observeForever {
            viewStates.add(it)
        }

        viewModel.createTodo(Todo("failed case",false,"0"),"mock")

        coEvery { api.createTodos("0", "Bearer unauthorized" ,"48fcacf7-46e1-4285-9d47-76472c1673d1",Todo("failed case",false,"0")) }

        assertNull(viewModel.userdata.value)
        assertEquals(
            listOf(TodoListViewModel.ViewState.Loading, TodoListViewModel.ViewState.Error("Failed")),
            viewStates
        )
    }


}