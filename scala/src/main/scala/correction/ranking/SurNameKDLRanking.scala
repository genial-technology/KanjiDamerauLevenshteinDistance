package correction.ranking

import correction.SurNameDictionary

class SurNameKDLRanking(topN: Int) extends KDLRanking(topN, SurNameDictionary)
