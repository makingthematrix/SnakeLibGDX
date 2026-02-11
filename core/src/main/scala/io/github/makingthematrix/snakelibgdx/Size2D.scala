package io.github.makingthematrix.snakelibgdx

final case class Size2D(width: Double, height: Double) {
  def +(other: Size2D): Size2D = Size2D(width + other.width, height + other.height)

  def -(other: Size2D): Size2D = Size2D(width - other.width, height - other.height)

  def *(other: Size2D): Size2D = Size2D(width * other.width, height * other.height)

  def /(other: Size2D): Size2D = Size2D(width / other.width, height / other.height)

  def unary_- : Size2D = Size2D(-width, -height)

  def *(scalar: Double): Size2D = Size2D(width * scalar, height * scalar)

  def /(scalar: Double): Size2D = Size2D(width / scalar, height / scalar)
  
  def abs: Size2D = Size2D(math.abs(width), math.abs(height))
  def min(other: Size2D): Size2D = Size2D(math.min(width, other.width), math.min(height, other.height))
  def max(other: Size2D): Size2D = Size2D(math.max(width, other.width), math.max(height, other.height))
  def clamp(min: Size2D, max: Size2D): Size2D = Size2D(math.max(min.width, math.min(max.width, width)), math.max(min.height, math.min(max.height, height)))
  
  def invert: Size2D = Size2D(height, width)
  
  def vectorLength: Double = math.sqrt(width * width + height * height)
}
