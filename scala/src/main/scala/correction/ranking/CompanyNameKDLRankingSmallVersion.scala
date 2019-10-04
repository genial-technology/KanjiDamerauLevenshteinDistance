package correction.ranking

import correction.CompanyNameDictionary

class CompanyNameKDLRankingSmallVersion(topN: Int) extends KDLRanking(topN, CompanyNameDictionary)
