package correction.ranking

import correction.Dictionary
import correction.editDistance.JaroWinklerDistance

class JWRanking(topN: Int, dictionary: Dictionary) extends Ranking(topN, dictionary, JaroWinklerDistance)