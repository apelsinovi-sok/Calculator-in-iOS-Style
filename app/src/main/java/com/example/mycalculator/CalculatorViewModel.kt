package com.example.mycalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.math.BigDecimal

class CalculatorViewModel : ViewModel() {
    var calculatorState by mutableStateOf(CalculatorState())
        private set

    private fun enterOperation(operator: CalculatorOperation) {
        if (calculatorState.number1.length >= MAX_NUM_LENGTH) {
            return
        }
        if (calculatorState.number1.isNotEmpty() && calculatorState.operation == null) {
            calculatorState = calculatorState.copy(operation = operator)


        } else if (calculatorState.number1.isNotEmpty() && calculatorState.operation != null && calculatorState.number2.isNotEmpty()) {
            performCalculation()
            calculatorState = if (calculatorState.number1.length < MAX_NUM_LENGTH - 1) {
                calculatorState.copy(operation = operator, number2 = "")


            } else {
                calculatorState.copy(operation = null, number2 = "")
            }

        } else if (calculatorState.number1.isNotEmpty() && calculatorState.operation != null && calculatorState.number2.isEmpty()) {
            calculatorState = calculatorState.copy(operation = operator)
        }
    }

    private fun enterNumber(number: Int) {
        val currentNumber = if (calculatorState.operation == null) {
            calculatorState.number1
        } else {
            calculatorState.number2
        }

        val updatedNumber = when {
            currentNumber == "0" -> number.toString()
            currentNumber.startsWith("0") && currentNumber != "0" -> {
                if (currentNumber.contains('.')) {
                    currentNumber + number
                } else {
                    currentNumber.substring(1) + number
                }
            }
            else -> currentNumber + number
        }

        calculatorState = if (calculatorState.operation == null) {
            calculatorState.copy(number1 = updatedNumber)
        } else {
            calculatorState.copy(number2 = updatedNumber)
        }
    }



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
        val number1 = calculatorState.number1.toBigDecimalOrNull()
        val number2 = calculatorState.number2.toBigDecimalOrNull()
        if (number1 != null && number2 != null) {
            val result = when (calculatorState.operation) {
                is CalculatorOperation.Add -> number1 + number2
                is CalculatorOperation.Subtract -> number1 - number2
                is CalculatorOperation.Multiply -> number1 * number2
                is CalculatorOperation.Divide -> number1 / number2
                null -> return
            }


            val resultAsString = formatDouble(result).take(15)

//            if (resultAsString.length >= MAX_NUM_LENGTH) resultAsString = "Error"

            calculatorState = calculatorState.copy(
                number1 = resultAsString,
                number2 = "",
                operation = null
            )
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

    private fun formatDouble(number: BigDecimal): String {
        val formatted = number.toString()
        return if (formatted.endsWith(".0")) {
            formatted.substring(0, formatted.length - 2)
        } else {
            formatted
        }
    }


    companion object {
        private const val MAX_NUM_LENGTH = 18
    }
}