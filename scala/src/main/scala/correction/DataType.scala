package correction

sealed trait DataType

case object CompanyAddress extends DataType
case object CompanyName extends DataType
case object Name extends DataType
case object Term extends DataType