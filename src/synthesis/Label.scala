package synthesis

abstract class Label

case object Left extends Label {
  override def toString: String = "L"
}

case object Right extends Label {
  override def toString: String = "R"
}

case object Base extends Label {
  override def toString: String = "B"
}