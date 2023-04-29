package tech.onsibey.squarelife.detector.imageprocessor

object Processor {
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
