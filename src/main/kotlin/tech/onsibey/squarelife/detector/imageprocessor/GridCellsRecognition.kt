package tech.onsibey.squarelife.detector.imageprocessor

import ij.ImagePlus

interface GridCellsRecognition {
    fun divideImageByGrid(imagePlus: ImagePlus, gridSize: Int = 5): List<List<ImagePlus>>

    fun recognizeDominantColours(images: List<List<ImagePlus>>): List<List<CellColor>>
}