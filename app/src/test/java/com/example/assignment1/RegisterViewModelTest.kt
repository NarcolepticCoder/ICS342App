package com.example.assignment1

import androidx.test.ext.junit.runners.AndroidJUnit4
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
class RegisterViewModelTest{

    @Test
    fun testRegisterUserWithCorrectCredentials() = runTest {

        val user = mockk<NewUser>()
        every { user.user_id } returns "0"
        every { user.token} returns "1234-123124-123123-1231"


        val api = mockk<InterfaceApi>()
        coEvery { api.createUser(any(), any()) } returns user


        val viewModel = RegisterViewModel(null,api)

        val viewStates = mutableListOf<RegisterViewModel.ViewState>()
        viewModel.viewState.observeForever {
            viewStates.add(it)
        }

        viewModel.registerUser(UserInfoRequest(null,"username", "password1"),null)


        assertEquals("0", viewModel.userdata.value?.user_id)
        assertEquals("1234-123124-123123-1231", viewModel.userdata.value?.token)

        assertEquals(
            listOf(RegisterViewModel.ViewState.Loading, RegisterViewModel.ViewState.Success),
            viewStates
        )


        coVerify { api.createUser("48fcacf7-46e1-4285-9d47-76472c1673d1", UserInfoRequest(null,"username", "password1")) }
    }
    @Test
    fun `test clearError sets errorLiveData to null`() = runTest{
        val api = mockk<InterfaceApi>(relaxed = true)
        val viewModel = RegisterViewModel(null,api)
        viewModel.clearError()
        assert(viewModel.errorLiveData.value == null)

    }


    @Test
    fun `test clearError fails to set errorLiveData to null`() = runTest{
        val api = mockk<InterfaceApi>(relaxed = true)
        val viewModel = RegisterViewModel(null,api)
        viewModel.clearError()
        viewModel.errorLiveData.value = "error"
        assert(viewModel.errorLiveData.value != null)
    }
    @Test
    fun testRegisterUserWithIncorrectCredentials() = runTest {
        val api = mockk<InterfaceApi>()
        coEvery { api.createUser("48fcacf7-46e1-4285-9d47-76472c1673d1", UserInfoRequest(null, "wrongusername", "wrongpassword1")) } throws RuntimeException("")

        val viewModel = RegisterViewModel(null,api)

        val viewStates = mutableListOf<RegisterViewModel.ViewState>()
        viewModel.viewState.observeForever {
            viewStates.add(it)
        }

        viewModel.registerUser(UserInfoRequest(null,"wrongusername", "wrongpassword1"),null)

        coEvery { api.createUser("48fcacf7-46e1-4285-9d47-76472c1673d1", UserInfoRequest(null, "wrongusername", "wrongpassword1")) }

        assertNull(viewModel.userdata.value)
        assertEquals(
            listOf(RegisterViewModel.ViewState.Loading, RegisterViewModel.ViewState.Error("Failed")),
            viewStates
        )
    }


}