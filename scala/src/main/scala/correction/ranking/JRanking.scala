package correction.ranking

import correction.Dictionary
import correction.editDistance.JaroDistance

class JRanking(topN: Int, dictionary: Dictionary) extends Ranking(topN, dictionary, JaroDistance)