package tech.onsibey.squarelife.detector.imageprocessor

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ParetoSetTest {
    @Test
    fun `GIVEN set of integers WHEN applying paretoSet() function to it THEN get pareto's set of the initial set`() {
        val initialSet = setOf(
            3, 4, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57,
            58, 150, 160, 180, 200, 270, 400
        )
        val expectedSingleCriteriaParetoSet = SingleCriteriaParetoSet(
            listOf(46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58)
        )
        val actualParetoSet = SingleCriteriaParetoSet(initialSet.toList())

        assertThat(actualParetoSet).isEqualTo(expectedSingleCriteriaParetoSet)
        assertThat(actualParetoSet.averageInt()).isEqualTo(52)
    }

    @Test
    fun `GIVEN big set of integers WHEN applying paretoSet() function to it THEN get pareto's set of the initial set`() {
        val initialSet = setOf(
            1, 5, 6, 7, 8, 10, 11, 13, 14, 15, 18, 19, 20, 21, 25, 26, 28, 29, 30, 32, 33, 34, 36, 37,
            38, 39, 40, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 53, 55, 56, 57, 59, 60, 61, 62, 63, 64, 67, 69, 70
        ) +
                listOf(425,427,428,429,431,432,433,434,435,436,438,440,441,442,443,444,445,446,447,449,452,
                    453,454,455,456,457,458,459,460,461,463,464,466,468,470,471,472,473,474,475,476,477,478,
                    479,480,481,482,483,484,485,486,487,488,490,491,492,493,494,495,496,497,498,500,502,504,
                    505,506,507,509,510,511,512,513,514,515,517,518,519,520,521,522,523,525,526,527,528,529,
                    530,531,532,533,534,535,536,537,538,540,541,542,543,544,545,547,548,549,550,551,552,553,
                    555,556,557,558,559,560,561,562,563,564,565,566,567,568,569,570,571,572,573,574,575) +
                listOf(1008,1010,1013,1018,1021,1024,1039,1041,1045,1052,1057,
                    1062,1067,1068,1071,1074,1092,1095,1099,1104,1108,1112,1113,1117,1121,1124,1128,
                    1131,1153,1155,1158,1159,1168,1169,1173,1174,1176,1184,1185,1189,1197,1203,1208,1213,
                    1214,1223,1229,1230,1233,1236,1238,1246,1251,1261,1263,1270,1275,1278,1284,1290,1291,
                    1297,1305,1313,1314,1316,1320,1321,1335,1345,1357,1359,1364,1365,1367,1375,1385,1386,1388,
                    1395,1397,1408,1420,1421,1422,1424,1428,1434,1449,1458,1471,1476,1480,1484,1485,
                    1487,1490,1491,1496,1497)


        val expectedSingleCriteriaParetoSet = SingleCriteriaParetoSet(
            listOf(425,427,428,429,431,432,433,434,435,436,438,440,441,
            442,443,444,445,446,447,449,452,
            453,454,455,456,457,458,459,460,461,463,464,466,468,470,471,472,473,474,475,476,477,478,
            479,480,481,482,483,484,485,486,487,488,490,491,492,493,494,495,496,497,498,500,502,504,
            505,506,507,509,510,511,512,513,514,515,517,518,519,520,521,522,523,525,526,527,528,529,
            530,531,532,533,534,535,536,537,538,540,541,542,543,544,545,547,548,549,550,551,552,553,
            555,556,557,558,559,560,561,562,563,564,565,566,567,568,569,570,571,572,573,574,575), 2)
        val actualParetoSet = SingleCriteriaParetoSet(initialSet.toList())

        assertThat(actualParetoSet).isEqualTo(expectedSingleCriteriaParetoSet)
        assertThat(actualParetoSet.averageInt()).isEqualTo(503)
    }
}