package normalization

object Normalizer {
  def normalize(text: String): String = {
    val noSpaceText: String = text.replaceAllLiterally(" ", "")
    val noSymbolText: String = noSpaceText.
      replaceAllLiterally("|", "").
      replaceAllLiterally(".", "").
      replaceAllLiterally("'", "").
      replaceAllLiterally("^", "").
      replaceAll("[\\u002d\\u2010\\u2011\\u2013\\u2014\\u2015\\u2212\\uff70]", "-")//\u30fc
    //val specialCharacter: String = noSymbolText.
    //  replaceAllLiterally("ヵ", "カ").
    //  replaceAllLiterally("ヶ", "ケ").
    //  replaceAllLiterally("ッ", "ツ")
    java.text.Normalizer.normalize(noSymbolText, java.text.Normalizer.Form.NFKC)
  }
}
