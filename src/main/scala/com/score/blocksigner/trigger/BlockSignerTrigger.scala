package com.score.blocksigner.trigger

import java.util

import com.score.blocksigner.config.AppConf
import com.score.blocksigner.util.SenzLogger
import org.apache.cassandra.config.Schema
import org.apache.cassandra.db.partitions.Partition
import org.apache.cassandra.db.{Clustering, Mutation, RowUpdateBuilder}
import org.apache.cassandra.triggers.ITrigger
import org.apache.cassandra.utils.{FBUtilities, UUIDGen}

import scala.collection.JavaConverters._
import scala.collection.mutable

class BlockSignerTrigger extends ITrigger with SenzLogger with AppConf {
  override def augment(partition: Partition): util.List[Mutation] = {
    val message = new mutable.HashMap[String, String]()

    // setting the id
    message.put("table_id", partition.metadata().getKeyValidator.getString(partition.partitionKey().getKey))

    // Parsing Non-ID related fields
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
      case e: Exception =>
        logError(e)
    }

    // inset new column
    val audit = new RowUpdateBuilder(Schema.instance.getCFMetaData(keyspace, table), FBUtilities.timestampMicros(), UUIDGen.getTimeUUID())
    audit.add("keyspace_name", partition.metadata().ksName)
    audit.add("table_name", partition.metadata().cfName)
    audit.add("primary_key", partition.metadata().getKeyValidator.getString(partition.partitionKey().getKey))

    List[Mutation]().asJava
  }
}
