package tech.onsibey.squarelife

import tech.onsibey.squarelife.simulator.powers.IkuTurso
import java.util.*

object Program {
    @JvmStatic
    fun main(args: Array<String>) {
        // 1. greet user and ask the program mode
        // 2. if by photo:
        // 2.1 get path to the photo
        // 2.2 run image processor (gets photo returns 2d list of Cells - black and white)
        // 2.3 run detector (gets 2d list of Cells and returns size of the board and entity list)
        // 2.4 run simulator (gets size of the board and entity list and prints the evolution cycles)
        // 3. if not by photo:
        // 3.1 run simulator (gets nothing and prints the evolution cycles)
        IkuTurso
    }

    private fun initializationFromPhoto(): Boolean {
        println("Would you like to start the evolution from provided photo? Y/n")
        while (true) {
            val answer = readln()
            when {
                answer.uppercase(Locale.getDefault()) == "Y" -> return true
                answer.uppercase(Locale.getDefault()) == "N" -> return false
                else -> println("Enter Y or N!")
            }
        }
    }
}