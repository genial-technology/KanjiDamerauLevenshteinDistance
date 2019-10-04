package correction.ranking

import correction.Dictionary
import correction.editDistance.LevenshteinDistance

class LRanking(topN: Int, dictionary: Dictionary) extends Ranking(topN, dictionary, LevenshteinDistance)