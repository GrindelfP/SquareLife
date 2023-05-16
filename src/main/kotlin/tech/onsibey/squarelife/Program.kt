package tech.onsibey.squarelife

import tech.onsibey.squarelife.detector.datainterpreter.Interpreter
import tech.onsibey.squarelife.detector.imageprocessor.Processor
import tech.onsibey.squarelife.simulator.powers.IkuTurso
import tech.onsibey.squarelife.simulator.powers.Ukko
import tech.onsibey.squarelife.usercommunication.Communicator
import tech.onsibey.squarelife.usercommunication.Communicator.getCorrectPhotoInterpretationConfirmation
import tech.onsibey.squarelife.usercommunication.Communicator.greetUser
import tech.onsibey.squarelife.usercommunication.Communicator.initializationFromPhoto
import tech.onsibey.squarelife.usercommunication.Communicator.negotiatePhoto
import tech.onsibey.squarelife.visualisation.GifEvolutionCycleGenerator
import kotlin.system.exitProcess

object Program {
    @JvmStatic
    fun main(args: Array<String>) {
        greetUser()

        val god = when {
            initializationFromPhoto() -> {
                val imageBoard = Processor(negotiatePhoto()).processImageBoard()
                val mail = Interpreter(imageBoard).prepareMailman()

                val accepted = getCorrectPhotoInterpretationConfirmation(mail)

                if (!accepted) {
                    println("Error: Photo interpretation was not accepted.")
                    exitProcess(0)
                }

                Ukko(mail, Communicator.askNumberOfCycles())
            }
            else -> IkuTurso
        }

        GifEvolutionCycleGenerator(god.evolutionResult).visualize()
    }
}
