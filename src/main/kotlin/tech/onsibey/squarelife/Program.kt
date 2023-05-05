package tech.onsibey.squarelife

import tech.onsibey.squarelife.detector.datainterpreter.Interpreter
import tech.onsibey.squarelife.detector.imageprocessor.Processor
import tech.onsibey.squarelife.simulator.powers.IkuTurso
import tech.onsibey.squarelife.simulator.powers.Ukko
import tech.onsibey.squarelife.usercommunication.Communicator.askNumberOfCycles
import tech.onsibey.squarelife.usercommunication.Communicator.greetUser
import tech.onsibey.squarelife.usercommunication.Communicator.initializationFromPhoto
import tech.onsibey.squarelife.usercommunication.Communicator.negotiatePhoto
import tech.onsibey.squarelife.visualisation.GifEvolutionCycleGenerator

object Program {
    @JvmStatic
    fun main(args: Array<String>) {
        greetUser()

        val god = when {
            initializationFromPhoto() -> {
                val imageBoard = Processor(negotiatePhoto()).processImageBoard()
                val mail = Interpreter(imageBoard).prepareMailman()

                Ukko(mail, askNumberOfCycles())
            }
            else -> IkuTurso
        }

        GifEvolutionCycleGenerator(god.evolutionResult).visualize()
    }
}
