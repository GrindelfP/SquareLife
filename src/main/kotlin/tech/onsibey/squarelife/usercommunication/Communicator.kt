package tech.onsibey.squarelife.usercommunication

import tech.onsibey.squarelife.common.EntitySize.PHOTO_DIRECTORY_PATH
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
                    "If you want to go with default value, enter 0: "
        )
        while (true) {
            try {
                return readln().toInt()
            } catch (e: NumberFormatException) {
                println("Enter an integer number!")
            }
        }
    }

    fun negotiatePhoto(): String {
        do {
            println("Please, place your photo in the folder src/main/resources")
        } while (File(PHOTO_DIRECTORY_PATH).walk().any { !(it.isFile && it.name.endsWith(".jpg")) })

        return PHOTO_DIRECTORY_PATH
    }
}