package evaluation

import java.nio.file.{Path, Paths}

sealed abstract class OCR(ocr: String) {
  protected val basePath: String = "src/main/resources/ocrResults/"
  val companyAddressPath: Path
  val companyNamePath: Path
  val namePath: Path
  val termPath: Path
  val companyAddressErrorPath: Path
  val companyNameErrorPath: Path
  val nameErrorPath: Path
  val termErrorPath: Path
  val companyAddressErrorCorrectionFormat: String
  val companyNameErrorCorrectionFormat: String
  val nameErrorCorrectionFormat: String
  val termErrorCorrectionFormat: String
}

case object Tesseract extends OCR("Tesseract") {
  override val companyAddressPath: Path = Paths.get(basePath, "address_tesseract.tsv")
  override val companyNamePath: Path = Paths.get(basePath, "companyName_tesseract.tsv")
  override val namePath: Path = Paths.get(basePath, "name_tesseract.tsv")
  override val termPath: Path = Paths.get(basePath, "term_tesseract.tsv")
  override val companyAddressErrorPath: Path = Paths.get(basePath, "address_tesseract_errors.tsv")
  override val companyNameErrorPath: Path = Paths.get(basePath, "companyName_tesseract_errors.tsv")
  override val nameErrorPath: Path = Paths.get(basePath, "name_tesseract_errors.tsv")
  override val termErrorPath: Path = Paths.get(basePath, "term_tesseract_errors.tsv")
  override val companyAddressErrorCorrectionFormat: String = basePath.concat("address_tesseract_error_corrections_%s.tsv")
  override val companyNameErrorCorrectionFormat: String = basePath.concat("companyName_tesseract_error_corrections_%s.tsv")
  override val nameErrorCorrectionFormat: String = basePath.concat("name_tesseract_error_corrections_%s.tsv")
  override val termErrorCorrectionFormat: String = basePath.concat("term_tesseract_error_corrections_%s.tsv")
}

case object AdobeAcrobat extends OCR("AdobeAcrobat") {
  override val companyAddressPath: Path = Paths.get(basePath, "address_adobe.tsv")
  override val companyNamePath: Path = Paths.get(basePath, "companyName_adobe.tsv")
  override val namePath: Path = Paths.get(basePath, "name_adobe.tsv")
  override val termPath: Path = Paths.get(basePath, "term_adobe.tsv")
  override val companyAddressErrorPath: Path = Paths.get(basePath, "address_adobe_errors.tsv")
  override val companyNameErrorPath: Path = Paths.get(basePath, "companyName_adobe_errors.tsv")
  override val nameErrorPath: Path = Paths.get(basePath, "name_adobe_errors.tsv")
  override val termErrorPath: Path = Paths.get(basePath, "term_adobe_errors.tsv")
  override val companyAddressErrorCorrectionFormat: String = basePath.concat("address_adobe_error_corrections_%s.tsv")
  override val companyNameErrorCorrectionFormat: String = basePath.concat("companyName_adobe_error_corrections_%s.tsv")
  override val nameErrorCorrectionFormat: String = basePath.concat("name_adobe_error_corrections_%s.tsv")
  override val termErrorCorrectionFormat: String = basePath.concat("term_adobe_error_corrections_%s.tsv")
}

case object FujiXerox extends OCR("FujiXerox") {
  override val companyAddressPath: Path = Paths.get(basePath, "address_fuji.tsv")
  override val companyNamePath: Path = Paths.get(basePath, "companyName_fuji.tsv")
  override val namePath: Path = Paths.get(basePath, "name_fuji.tsv")
  override val termPath: Path = Paths.get(basePath, "term_fuji.tsv")
  override val companyAddressErrorPath: Path = Paths.get(basePath, "address_fuji_errors.tsv")
  override val companyNameErrorPath: Path = Paths.get(basePath, "companyName_fuji_errors.tsv")
  override val nameErrorPath: Path = Paths.get(basePath, "name_fuji_errors.tsv")
  override val termErrorPath: Path = Paths.get(basePath, "term_fuji_errors.tsv")
  override val companyAddressErrorCorrectionFormat: String = basePath.concat("address_fuji_error_corrections_%s.tsv")
  override val companyNameErrorCorrectionFormat: String = basePath.concat("companyName_fuji_error_corrections_%s.tsv")
  override val nameErrorCorrectionFormat: String = basePath.concat("name_fuji_error_corrections_%s.tsv")
  override val termErrorCorrectionFormat: String = basePath.concat("term_fuji_error_corrections_%s.tsv")
}

//case object Fujitsu extends OCR("Fujitsu")
