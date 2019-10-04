package correction.ranking

import correction.Dictionary
import correction.editDistance.KanjiDamerauLevenshteinDistance

class KDLRanking(topN: Int, dictionary: Dictionary) extends Ranking(topN, dictionary, KanjiDamerauLevenshteinDistance)
