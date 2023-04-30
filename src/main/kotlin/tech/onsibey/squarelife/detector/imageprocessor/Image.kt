package tech.onsibey.squarelife.detector.imageprocessor

class Processor(private val path: String) {
    fun processImageBoard(): ImageBoard {
        TODO()
    }
}

data class ImageBoard(val cells: List<List<Cell>>)

data class Cell(var isPainted: Boolean) {
    fun reverseState() {
        this.isPainted = !isPainted
    }
}
