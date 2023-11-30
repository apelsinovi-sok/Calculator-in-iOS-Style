package com.example.mycalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {
    var calculatorState by mutableStateOf(CalculatorState())
        private set

    fun onAction(calculatorAction: CalculatorAction) {
        when (calculatorAction) {
            is CalculatorAction.Number -> enterNumber(calculatorAction.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> calculatorState = CalculatorState()
            is CalculatorAction.Operation -> enterOperation(calculatorAction.operation)
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Delete -> performDeletion()
        }
    }

    private fun performDeletion() {
        when {
            calculatorState.number2.isNotBlank() -> calculatorState = calculatorState.copy(
                number2 = calculatorState.number2.dropLast(1)
            )

            calculatorState.operation != null -> calculatorState = calculatorState.copy(
                operation = null
            )

            calculatorState.number1.isNotBlank() -> calculatorState = calculatorState.copy(
                number1 = calculatorState.number1.dropLast(1)
            )
        }
    }

    private fun performCalculation() {
        val number1 = calculatorState.number1.toDoubleOrNull()
        val number2 = calculatorState.number2.toDoubleOrNull()
        if (number1 != null && number2 != null) {
            val result = when (calculatorState.operation) {
                is CalculatorOperation.Add -> number1 + number2
                is CalculatorOperation.Subtract -> number1 - number2
                is CalculatorOperation.Multiply -> number1 * number2
                is CalculatorOperation.Divide -> number1 / number2
                null -> return
            }

            var resultAsString = formatDouble(result).take(8)

            if (resultAsString.length >= MAX_NUM_LENGTH+1) resultAsString = "Error"

            calculatorState = calculatorState.copy(
                number1 = resultAsString,
                number2 = "",
                operation = null
            )
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        if (calculatorState.number1.length >= MAX_NUM_LENGTH) {
            return
        }
        if (calculatorState.number1.isNotBlank()) {
            calculatorState = calculatorState.copy(operation = operation)
        }
    }

    private fun enterDecimal() {
        if (calculatorState.operation == null && !calculatorState.number1.contains(".")
            && calculatorState.number1.isNotBlank()
        ) {
            calculatorState = calculatorState.copy(

                number1 = calculatorState.number1 + "."
            )
        }

        if (!calculatorState.number2.contains(".") && calculatorState.number2.isNotBlank()
        ) {
            calculatorState = calculatorState.copy(

                number1 = calculatorState.number2 + "."
            )
        }
    }

    private fun enterNumber(number: Int) {
        if (calculatorState.operation == null) {
            if (calculatorState.number1.length >= MAX_NUM_LENGTH) {
                return
            }
            calculatorState = calculatorState.copy(
                number1 = calculatorState.number1 + number
            )
            return
        }
        if (calculatorState.number2.length >=(MAX_NUM_LENGTH-calculatorState.number1.length-1)) {
            return
        }
        calculatorState = calculatorState.copy(
            number2 = calculatorState.number2 + number
        )
    }

    private fun formatDouble(number: Double): String {
        val formatted = number.toString()
        return if (formatted.endsWith(".0")) {
            formatted.substring(0, formatted.length - 2)
        } else {
            formatted
        }
    }


    companion object {
        private const val MAX_NUM_LENGTH = 8
    }
}