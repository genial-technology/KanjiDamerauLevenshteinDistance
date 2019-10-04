package correction

import java.nio.file.{Path, Paths}

sealed abstract class Dictionary {
  protected val basePath: String = "src/main/resources/dic/"
  val path: Path
}

case object FirstNameDictionary extends Dictionary {
  override val path: Path = Paths.get(basePath, "firstNameDic-WordList.json")
}

case object SurNameDictionary extends Dictionary {
  override val path: Path = Paths.get(basePath, "surNameDic-WordList.json")
}

case object CompanyAddressDictionary extends Dictionary {
  override val path: Path = Paths.get(basePath, "addressDic-WordList.json")
}

case object CompanyNameDictionary extends Dictionary {
  override val path: Path = Paths.get(basePath, "addressDic-WordList.json")
}

case object TermDictionary extends Dictionary {
  override val path: Path = Paths.get(basePath, "legalTermDic-WordList.json")
}