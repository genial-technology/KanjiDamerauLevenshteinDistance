package correction.ranking

import correction.TermDictionary

class TermLRanking(topN: Int) extends LRanking(topN, TermDictionary)