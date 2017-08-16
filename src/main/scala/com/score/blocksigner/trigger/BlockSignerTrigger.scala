package com.score.blocksigner.trigger

import java.util

import com.score.blocksigner.util.SenzLogger
import org.apache.cassandra.db.partitions.Partition
import org.apache.cassandra.db.{Clustering, Mutation}
import org.apache.cassandra.triggers.ITrigger

import scala.collection.JavaConverters._
import scala.collection.mutable

class BlockSignerTrigger extends ITrigger with SenzLogger {
  override def augment(partition: Partition): util.List[Mutation] = {
    val message = new mutable.HashMap[String, String]()

    // setting the id
    message.put("table_id", partition.metadata().getKeyValidator.getString(partition.partitionKey().getKey))

    //Parsing Non-ID related fields
    try {
      val it = partition.unfilteredIterator()
      while (it.hasNext) {
        val un = it.next()
        val clt = un.clustering().asInstanceOf[Clustering]
        val cells = partition.getRow(clt).cells().iterator()
        val columns = partition.getRow(clt).columns().iterator()

        while (columns.hasNext) {
          val columnDef = columns.next()
          val cell = cells.next()
          val data = new String(cell.value().array()) // If cell type is text
          message.put(columnDef.toString, data)
          logger.info(s"columnDef: ${columnDef.toString}")
          logger.info(s"data: $data")
        }
      }
    } catch {
      case e =>
        logError(e)
    }

    List[Mutation]().asJava
  }
}
