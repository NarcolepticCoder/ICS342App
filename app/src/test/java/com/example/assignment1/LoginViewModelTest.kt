package com.example.assignment1
import android.content.SharedPreferences
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
class LoginViewModelTest{

    @Test
    fun testLoginUserWithCorrectCredentials() = runTest {


        val user = mockk<NewUser>()
        every { user.user_id } returns "0"
        every { user.token} returns "1234-123124-123123-1231"



        val api = mockk<InterfaceApi>()
        coEvery { api.loginUser(any(), any()) } returns user



        val viewModel = LoginViewModel(null,api)


        val viewStates = mutableListOf<LoginViewModel.ViewState>()
        viewModel.viewState.observeForever {
            viewStates.add(it)
        }

        viewModel.loginUser(UserInfoRequest(null,"username", "password1"),null)


        assertEquals("0", viewModel.userdata.value?.user_id)
        assertEquals("1234-123124-123123-1231", viewModel.userdata.value?.token)



        assertEquals(
            listOf(LoginViewModel.ViewState.Loading, LoginViewModel.ViewState.Success),
            viewStates
        )


        coVerify { api.loginUser("48fcacf7-46e1-4285-9d47-76472c1673d1", UserInfoRequest(null,"username", "password1")) }
    }
    @Test
    fun testLoginUserWithIncorrectCredentials() = runTest {
        val api = mockk<InterfaceApi>()
        coEvery { api.loginUser("48fcacf7-46e1-4285-9d47-76472c1673d1", UserInfoRequest(null, "wrongusername", "wrongpassword1")) } throws RuntimeException("")

        val viewModel = LoginViewModel(null,api)

        val viewStates = mutableListOf<LoginViewModel.ViewState>()
        viewModel.viewState.observeForever {
            viewStates.add(it)
        }

        viewModel.loginUser(UserInfoRequest(null,"wrongusername", "wrongpassword1"),null)

        coEvery { api.loginUser("48fcacf7-46e1-4285-9d47-76472c1673d1", UserInfoRequest(null, "wrongusername", "wrongpassword1")) }

        assertNull(viewModel.userdata.value)
        assertEquals(
            listOf(LoginViewModel.ViewState.Loading, LoginViewModel.ViewState.Error("Failed")),
            viewStates
        )
    }


}