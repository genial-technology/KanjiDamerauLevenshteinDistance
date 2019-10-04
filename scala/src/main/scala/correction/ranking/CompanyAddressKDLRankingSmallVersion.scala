package correction.ranking

import correction.CompanyAddressDictionary

class CompanyAddressKDLRankingSmallVersion(topN: Int) extends KDLRanking(topN, CompanyAddressDictionary)