package com.example.directoryapplication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.example.directoryapplication.presentation.auth.LoginScreen
import com.example.directoryapplication.presentation.theme.DirectoryApplicationTheme
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysAllElements() {
        composeTestRule.setContent {
            DirectoryApplicationTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }

        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Пароль").assertIsDisplayed()
        composeTestRule.onNodeWithText("Войти").assertIsDisplayed()
        composeTestRule.onNodeWithText("Справочник").assertIsDisplayed()
    }

    @Test
    fun loginScreen_loginButtonClickable() {
        composeTestRule.setContent {
            DirectoryApplicationTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }

        composeTestRule.onNodeWithText("Войти").assertHasClickAction()
    }

    @Test
    fun loginScreen_canTypeInEmailField() {
        composeTestRule.setContent {
            DirectoryApplicationTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }

        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("test@test.com")

        composeTestRule.onNodeWithText("test@test.com").assertIsDisplayed()
    }
}