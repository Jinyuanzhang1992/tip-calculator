package com.example.tipcalculator.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


data class InputFieldConfig(
    val enabled: Boolean = true,
    val isSingleLine: Boolean = true,
    val keyboardType: KeyboardType = KeyboardType.Number,
    val imeAction: ImeAction = ImeAction.Done
)

@Composable
fun OutlinedInputFieldForMoney(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    config: InputFieldConfig = InputFieldConfig(),
    onValueChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val decimalPattern = Regex("^\\d*(\\.\\d{0,2})?$")

    OutlinedTextField(
        modifier = modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = valueState.value,
        onValueChange = { newValue ->
            if ((newValue.all { it.isDigit() || it == '.' }) && newValue.matches(decimalPattern) ) {
                valueState.value = newValue
            }
        },
        label = { Text(text = labelId) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.AttachMoney,
                contentDescription = "Dollars Icon"
            )
        },
        singleLine = config.isSingleLine,
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground),
        enabled = config.enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = config.keyboardType,
            imeAction = config.imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                onValueChange(valueState.value.trim())
                focusManager.clearFocus()
            }
        )
    )
}

