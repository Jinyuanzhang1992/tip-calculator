package com.example.tipcalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tipcalculator.ui.theme.TipCalculatorTheme
import androidx.compose.ui.text.font.FontWeight
import com.example.tipcalculator.components.OutlinedInputFieldForMoney
import com.example.tipcalculator.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen {
                BillForm()
            }
        }
    }
}

@Composable
fun MainScreen(content: @Composable () -> Unit = {}) {
    TipCalculatorTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Surface(
                modifier = Modifier.padding(
                    top = innerPadding.calculateTopPadding(), start = 16.dp, end = 16.dp
                )
            ) {
                content()
            }
        }
    }
}

@Composable
fun BillForm() {
    var showResSection by remember { mutableStateOf(false) }
    var sliderPositionState by remember { mutableFloatStateOf(0.05f) }
    var totalBillAmount by remember { mutableDoubleStateOf(0.0) }
    var totalTipAmount by remember { mutableDoubleStateOf(0.0) }
    var splitPeopleNumber by remember { mutableIntStateOf(2) }
    var totalAmount by remember { mutableDoubleStateOf(0.0) }
    var totalPerPerson by remember { mutableDoubleStateOf(0.0) }
    val totalBillState = remember { mutableStateOf("") }
    val range: IntRange = 1..100
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            }
    ) {
        TopHeader(totalPerPerson)
        Spacer(modifier = Modifier.height(25.dp))
        Surface(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            shape = CircleShape.copy(all = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color(0xFF6650a4))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                EnterBillAmount(totalBillState = totalBillState) { billAmount ->
                    showResSection = true
                    totalBillAmount = billAmount.toDouble()
                    totalTipAmount = totalBillAmount * sliderPositionState
                    totalPerPerson = (totalBillAmount + totalTipAmount) / splitPeopleNumber
                    totalAmount = totalBillAmount + totalTipAmount
                }
                when {
                    showResSection -> {
                        SplitButtons(
                            splitPeopleNumber, range
                        ) {
                            splitPeopleNumber = it
                            totalTipAmount = totalBillAmount * sliderPositionState
                            totalPerPerson = (totalBillAmount + totalTipAmount) / splitPeopleNumber
                            totalAmount = totalBillAmount + totalTipAmount
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        TotalTip(totalTipAmount)
                        Spacer(modifier = Modifier.height(10.dp))
                        TotalAmount(totalAmount)
                        Spacer(modifier = Modifier.height(10.dp))
                        TipPercentage(sliderPositionState) {
                            sliderPositionState = it
                            totalTipAmount = totalBillAmount * sliderPositionState
                            totalPerPerson = (totalBillAmount + totalTipAmount) / splitPeopleNumber
                            totalAmount = totalBillAmount + totalTipAmount
                        }
                    }

                    else -> Box {}
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        ClearButton {
            showResSection = false
            totalBillAmount = 0.0
            totalTipAmount = 0.0
            splitPeopleNumber = 2
            totalAmount = 0.0
            totalPerPerson = 0.0
            totalBillState.value = ""
            sliderPositionState = 0.05f
            focusManager.clearFocus()
        }
    }
}

@Composable
fun ClearButton(clearAllState: () -> Unit) {
    Button(
        onClick = {
            clearAllState()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {
        Text("Clear")
    }
}

@Composable
fun TotalAmount(totalAmount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Total Amount",
            modifier = Modifier.align(Alignment.CenterVertically),
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .padding(horizontal = 3.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val formattedAmount = "%.2f".format(totalAmount)
            Text(
                text = "$ $formattedAmount",
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun TipPercentage(
    sliderPositionState: Float,
    updateState: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val formattedPercentage = "%.0f".format(sliderPositionState * 100)
        Text(
            text = "$formattedPercentage%",
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = sliderPositionState,
            onValueChange = { newVal ->
                updateState(newVal)
            },
            valueRange = 0f..1f,
            steps = 19,
            onValueChangeFinished = {
                Log.d("Slider", "Value: $sliderPositionState")
            }
        )
    }
}

@Composable
fun TotalTip(totalTipAmount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Total Tip",
            modifier = Modifier.align(Alignment.CenterVertically),
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .padding(horizontal = 3.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val formattedTipAmount = "%.2f".format(totalTipAmount)
            Text(
                text = "$ $formattedTipAmount",
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun SplitButtons(
    splitPeopleNumber: Int,
    range: IntRange,
    updatePersonNumber: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Split",
            modifier = Modifier.align(Alignment.CenterVertically),
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .padding(horizontal = 3.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundIconButton(
                imageVector = Icons.Default.Remove,
                onClick = {
                    if (splitPeopleNumber > 1)
                        updatePersonNumber(splitPeopleNumber - 1)
                }
            )
            Text(
                text = "$splitPeopleNumber",
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            RoundIconButton(
                imageVector = Icons.Default.Add,
                onClick = {
                    if (splitPeopleNumber < range.last)
                        updatePersonNumber(splitPeopleNumber + 1)
                }
            )
        }
    }
}

@Composable
fun EnterBillAmount(
    modifier: Modifier = Modifier,
    totalBillState: MutableState<String>,
    onValueChange: (String) -> Unit = {}
) {
    val validState = remember(totalBillState.value) {
        //返回一个布尔值，指示字符串是否不为空
        totalBillState.value.trim().isNotEmpty()
    }
    OutlinedInputFieldForMoney(
        valueState = totalBillState,
        labelId = "Enter Bill",
        modifier = modifier,
    ) { inputValue ->
        inputValue.takeIf { validState }?.let { onValueChange(inputValue) }
    }
}

@Composable
fun TopHeader(totalPerPerson: Double) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val totalAmount = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$ $totalAmount",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 35.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TipCalculatorTheme {
        MainScreen {
            BillForm()
        }
    }
}