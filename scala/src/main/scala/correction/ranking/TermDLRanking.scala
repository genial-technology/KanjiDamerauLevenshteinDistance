package correction.ranking

import correction.TermDictionary

class TermDLRanking(topN: Int) extends DLRanking(topN, TermDictionary)