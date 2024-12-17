package com.example.foresti.calculator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foresti.MainActivity
import com.example.foresti.databinding.ActivityCalculatorBinding
import com.example.foresti.utils.AppPref

class CalculatorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalculatorBinding
    private var currentOperation: String? = null
    private var operand1: String = ""
    private var operand2: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (AppPref.passwordCalculatorScreen.isEmpty()) {
            startActivity(Intent(this@CalculatorActivity, MainActivity::class.java))
            finish()
        }
        setupListeners()
        gotoMainScreen()
    }

    private fun gotoMainScreen() {
        binding.btnEquals.setOnLongClickListener {
            if (binding.formula.text == AppPref.passwordCalculatorScreen) {
                startActivity(Intent(this@CalculatorActivity, MainActivity::class.java))
                finish()
            }
            return@setOnLongClickListener true
        }
    }

    private fun setupListeners() {
        // Numbers
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9
        )

        for (btn in numberButtons) {
            btn.setOnClickListener {
                val number = (it as? android.widget.TextView)?.text.toString()
                appendToFormula(number)
            }
        }

        // Operations
        binding.btnPlus.setOnClickListener { setOperation("+") }
        binding.btnMinus.setOnClickListener { setOperation("-") }
        binding.btnMultiply.setOnClickListener { setOperation("×") }
        binding.btnDivide.setOnClickListener { setOperation("÷") }

        // Equals
        binding.btnEquals.setOnClickListener { calculateResult() }

        // Clear
        binding.btnClear.setOnClickListener { clearCalculator() }
    }

    private fun appendToFormula(value: String) {
        if (currentOperation == null) {
            operand1 += value
        } else {
            operand2 += value
        }
        updateFormulaText()
    }

    private fun setOperation(operation: String) {
        if (operand1.isNotEmpty()) {
            currentOperation = operation
            updateFormulaText()
        }
    }

    private fun calculateResult() {
        if (operand1.isNotEmpty() && operand2.isNotEmpty() && currentOperation != null) {
            val result: Double? = try {
                val num1 = operand1.toDouble()
                val num2 = operand2.toDouble()
                when (currentOperation) {
                    "+" -> num1 + num2
                    "-" -> num1 - num2
                    "×" -> num1 * num2
                    "÷" -> if (num2 != 0.0) num1 / num2 else null
                    else -> null
                }
            } catch (e: Exception) {
                null
            }

            binding.result.text = result?.toString() ?: "Error"
        }
    }

    private fun clearCalculator() {
        operand1 = ""
        operand2 = ""
        currentOperation = null
        binding.formula.text = ""
        binding.result.text = "0"
    }

    private fun updateFormulaText() {
        val formula = buildString {
            append(operand1)
            if (currentOperation != null) append(" $currentOperation ")
            append(operand2)
        }
        binding.formula.text = formula
    }

}
