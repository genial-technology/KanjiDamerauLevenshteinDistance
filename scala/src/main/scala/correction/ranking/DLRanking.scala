package correction.ranking

import correction.Dictionary
import correction.editDistance.DamerauLevenshteinDistance

class DLRanking(topN: Int, dictionary: Dictionary) extends Ranking(topN, dictionary, DamerauLevenshteinDistance)