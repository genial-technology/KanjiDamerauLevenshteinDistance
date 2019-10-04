package correction.ranking

import correction.TermDictionary

class TermKDLRanking(topN: Int) extends KDLRanking(topN, TermDictionary)
