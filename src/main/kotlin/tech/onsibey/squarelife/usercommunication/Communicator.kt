package tech.onsibey.squarelife.usercommunication

import tech.onsibey.squarelife.common.DEFAULT_EVOLUTION_CYCLES_LIMIT
import java.io.File
import java.util.Locale

object Communicator {


    fun greetUser() {
        println("Welcome to the Square Life!")
    }

    fun initializationFromPhoto(): Boolean {
        print("Would you like to start the evolution from provided photo? Y/n ")
        while (true) {
            val answer = readln()
            when {
                answer.uppercase(Locale.getDefault()) == "Y" -> return true
                answer.uppercase(Locale.getDefault()) == "N" -> return false
                else -> println("Enter Y or N!")
            }
        }
    }

    fun askNumberOfCycles(): Int {
        print(
            "Enter, how much cycles should pass during the simulation. " +
                    "If you want to go with default value ($DEFAULT_EVOLUTION_CYCLES_LIMIT cycles), enter d: "
        )

        return when (val input = readln()) {
            "d" -> DEFAULT_EVOLUTION_CYCLES_LIMIT
            else -> {
                try {
                    input.toInt()
                } catch (e: NumberFormatException) {
                    println("Your input was not a number so I will go with default value " +
                            "($DEFAULT_EVOLUTION_CYCLES_LIMIT cycles).")
                    DEFAULT_EVOLUTION_CYCLES_LIMIT
                }
            }
        }
    }

    fun negotiatePhoto(): String {
        var absolutePath = ""
        while (true) {
            print("Please, provide an absolute path to your source photo: ")
            absolutePath = readln()
            try {
                File(absolutePath).walk().any { !(it.isFile && it.name.endsWith(".jpg")) }
                return absolutePath
            } catch (e: IllegalArgumentException) {
                println("Photo not found in your path!")
            }
        }
    }
}