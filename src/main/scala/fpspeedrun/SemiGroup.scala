package fpspeedrun

trait SemiGroup[T] {
  def combine(x: T, y: T): T
}

object SemiGroup {

  import syntax.semigroup._

  object Laws {
    def associativity[T: SemiGroup](x: T, y: T, z: T): Boolean = {
      ((x |+| y) |+| z) == (x |+| (y |+| z))
    }
  }

  implicit val stringSemiGroup: SemiGroup[String] = (x: String, y: String) => x + y

  def combineList[T: SemiGroup](list: List[T]): Option[T] = list.reduceOption((x, y) => x |+| y)

  def combineListVia[U[_]] = new CombineListVia[U]

  class CombineListVia[U[_]] {
    def apply[T](list: List[T])(implicit iso: Iso[T, U[T]], semiGroup: SemiGroup[U[T]]): Option[T] =
      list.reduceOption((x, y) => iso.unwrap(iso.wrap(x) |+| iso.wrap(y)))
  }

}

final case class Sum[T](value: T) extends AnyVal

object Sum {
  implicit def sumIso[T]: Iso[T, Sum[T]] = new Iso[T, Sum[T]] {
    override def wrap(x: T): Sum[T] = Sum(x)

    override def unwrap(x: Sum[T]): T = x.value
  }

  implicit val intSum: SemiGroup[Sum[Int]] = (x, y) => Sum(x.value + y.value)

}

final case class Prod[T](value: T) extends AnyVal

object Prod {
  implicit def prodIso[T]: Iso[T, Prod[T]] = new Iso[T, Prod[T]] {
    override def wrap(x: T): Prod[T] = Prod(x)

    override def unwrap(x: Prod[T]): T = x.value
  }

  implicit val intProd: SemiGroup[Prod[Int]] = (x, y) => Prod(x.value * y.value)
}

final case class Div[T](value: T) extends AnyVal

object Div {
  implicit def divIso[T]: Iso[T, Div[T]] = new Iso[T, Div[T]] {
    override def wrap(x: T): Div[T] = Div(x)

    override def unwrap(x: Div[T]): T = x.value
  }

  implicit val intDiv: SemiGroup[Div[Int]] = (x, y) => Div(x.value / y.value)
}
