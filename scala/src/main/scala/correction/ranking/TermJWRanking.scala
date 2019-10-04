package correction.ranking

import correction.TermDictionary

class TermJWRanking(topN: Int) extends JWRanking(topN, TermDictionary)