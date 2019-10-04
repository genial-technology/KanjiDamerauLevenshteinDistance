package correction.ranking

import correction.Dictionary
import correction.editDistance.Distance

import scala.collection.mutable
import scala.io.Source

class Ranking(topN: Int,
              protected val dictionary: Dictionary,
              protected val distance: Distance) {
  private val priorityQueue: mutable.PriorityQueue[(String, Double)] = {
    mutable.PriorityQueue.empty[(String, Double)](Ordering.by[(String, Double), Double](_._2))
  }

  protected def enqueue(element: String, similarity: Double): Unit = {
    priorityQueue.enqueue((element, similarity))
  }

  private def dequeueAll: Seq[(String, Double)] = {
    priorityQueue.dequeueAll
  }

  private def clear(): Unit = {
    priorityQueue.clear
  }

  protected def set(text: String): Unit = {
    val source = Source.fromFile(dictionary.path.toFile)
    source.getLines foreach { line: String =>
      val tokens: Array[String] = line.trim.split(':').map(_.trim)
      if (tokens.length == 2 && tokens.head.startsWith(""""entry"""")) {
        val entry: String = tokens.last.replaceAllLiterally(""""""", "")
        val similarity: Double = distance.calculate(text: String, entry: String)
        //System.err.println(text)
        //System.err.println(entry)
        //System.err.println(similarity)
        enqueue(entry: String, similarity: Double)
      }
    }
    source.close()
  }

  def result(text: String): Seq[(String, Double)] = {
    clear()
    set(text)
    dequeueAll
  }
}
